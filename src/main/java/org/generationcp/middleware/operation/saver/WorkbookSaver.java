/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.operation.saver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.DatasetValues;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.ExperimentValues;
import org.generationcp.middleware.domain.dms.PhenotypeExceptionDto;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StudyValues;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.exceptions.PhenotypeException;
import org.generationcp.middleware.helper.VariableInfo;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.operation.transformer.etl.ExperimentValuesTransformer;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.service.api.PedigreeService;
import org.generationcp.middleware.util.TimerWatch;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// ASsumptions - can be added to validations
// Mandatory fields: workbook.studyDetails.studyName
// template must not contain exact same combo of property-scale-method

// TODO : CONTROL THE SESSION - we need to flush in new Standard Variables as soon as we can - before Datasets and constructed
public class WorkbookSaver extends Saver {

	private static final Logger LOG = LoggerFactory.getLogger(WorkbookSaver.class);
	private final PedigreeService pedigreeService;

	public WorkbookSaver(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
		this.pedigreeService = ManagerFactory.getCurrentManagerFactoryThreadLocal().get().getPedigreeService();
	}

	/**
	 * This method transforms Variable data from a Fieldbook presented as an XLS style Workbook. - Variables new to the ontology are created
	 * and persisted - Columns and rows are transformed into entities suitables for persistence
	 * 
	 * Note : the result of this process is suitable for Dataset Creation
	 * 
	 * @param workbook
	 * @return Map<String>, ?> : a map of 3 sub-maps containing Strings(headers), VariableTypeLists and Lists of MeasurementVariables
	 * @throws Exception
	 */

	@SuppressWarnings("rawtypes")
	public Map saveVariables(Workbook workbook) throws Exception {
		// make sure to reset all derived variables
		workbook.reset();

		// Create Maps, which we will fill with transformed Workbook Variable Data
		Map<String, Map<String, ?>> variableMap = new HashMap<String, Map<String, ?>>();
		Map<String, List<String>> headerMap = new HashMap<String, List<String>>();
		Map<String, VariableTypeList> variableTypeMap = new HashMap<String, VariableTypeList>();
		Map<String, List<MeasurementVariable>> measurementVariableMap = new HashMap<String, List<MeasurementVariable>>();

		// GCP-6091 start
		List<MeasurementVariable> trialMV = workbook.getTrialVariables();
		List<String> trialHeaders = workbook.getTrialHeaders();
		VariableTypeList trialVariables = this.getVariableTypeListTransformer().transform(workbook.getTrialConditions(), false);
		List<MeasurementVariable> trialFactors = workbook.getTrialFactors();
		VariableTypeList trialVariableTypeList = null;
		if (trialFactors != null && !trialFactors.isEmpty()) {// multi-location
			trialVariableTypeList = this.getVariableTypeListTransformer().transform(trialFactors, false, trialVariables.size() + 1);
			trialVariables.addAll(trialVariableTypeList);
		}
		// GCP-6091 end
		trialVariables.addAll(this.getVariableTypeListTransformer()
				.transform(workbook.getTrialConstants(), true, trialVariables.size() + 1));

		List<MeasurementVariable> effectMV = workbook.getMeasurementDatasetVariables();
		VariableTypeList effectVariables = this.getVariableTypeListTransformer().transform(workbook.getNonTrialFactors(), false);
		effectVariables.addAll(this.getVariableTypeListTransformer().transform(workbook.getVariates(), true, effectVariables.size() + 1));

		// Load Lists into Maps in order to return to the front end (and force Session Flush)
		// -- headers
		headerMap.put("trialHeaders", trialHeaders);
		// -- variableTypeLists
		variableTypeMap.put("trialVariableTypeList", trialVariableTypeList);
		variableTypeMap.put("trialVariables", trialVariables);
		variableTypeMap.put("effectVariables", effectVariables);
		// -- measurementVariables
		measurementVariableMap.put("trialMV", trialMV);
		measurementVariableMap.put("effectMV", effectMV);
		// load 3 maps into a super Map
		variableMap.put("headerMap", headerMap);
		variableMap.put("variableTypeMap", variableTypeMap);
		variableMap.put("measurementVariableMap", measurementVariableMap);
		return variableMap;

	}

	/**
	 * Dataset creation and persistence for Fieldbook upload
	 * 
	 * NOTE IMPORTANT : This step will fail if the Fieldbook has not had new Variables processed and new ontology terms created.
	 * 
	 * 
	 * @param workbook
	 * @param variableMap : a map of 3 sub-maps containing Strings(headers), VariableTypeLists and Lists of MeasurementVariables
	 * @return int (success/fail)
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public int saveDataset(Workbook workbook, Map<String, ?> variableMap, boolean retainValues, boolean isDeleteObservations,
			String programUUID) throws Exception {
		Session session = this.getCurrentSession();
		// unpack maps first level - Maps of Strings, Maps of VariableTypeList , Maps of Lists of MeasurementVariable
		Map<String, List<String>> headerMap = (Map<String, List<String>>) variableMap.get("headerMap");
		Map<String, VariableTypeList> variableTypeMap = (Map<String, VariableTypeList>) variableMap.get("variableTypeMap");
		Map<String, List<MeasurementVariable>> measurementVariableMap =
				(Map<String, List<MeasurementVariable>>) variableMap.get("measurementVariableMap");

		// unpack maps
		// Strings
		List<String> trialHeaders = headerMap.get("trialHeaders");
		// VariableTypeLists
		VariableTypeList trialVariableTypeList = variableTypeMap.get("trialVariableTypeList");
		VariableTypeList trialVariables = variableTypeMap.get("trialVariables");
		VariableTypeList effectVariables = variableTypeMap.get("effectVariables");
		// Lists of measurementVariables
		List<MeasurementVariable> trialMV = measurementVariableMap.get("trialMV");
		List<MeasurementVariable> effectMV = measurementVariableMap.get("effectMV");

		// TODO : Review code and see whether variable validation and possible dataset creation abort is a good idea (rebecca)

		// GCP-6091 start
		int studyLocationId;
		List<Integer> locationIds = new ArrayList<Integer>();
		Map<Integer, VariableList> trialVariatesMap = new HashMap<Integer, VariableList>();

		// get the trial and measurement dataset id to use in deletion of experiments
		Integer trialDatasetId = workbook.getTrialDatasetId();
		Integer datasetId = workbook.getMeasurementDatesetId();
		int totalRows = 0;
		boolean isDeleteTrialObservations = false;
		if (trialDatasetId == null && workbook.getStudyDetails().getId() != null) {
			trialDatasetId = this.getWorkbookBuilder().getTrialDataSetId(workbook.getStudyDetails().getId(), workbook.getStudyName());
		}
		if (datasetId == null && workbook.getStudyDetails().getId() != null) {
			datasetId = this.getWorkbookBuilder().getMeasurementDataSetId(workbook.getStudyDetails().getId(), workbook.getStudyName());
		}

		if (trialDatasetId != null) {
			totalRows = (int) this.getStudyDataManager().countExperiments(trialDatasetId);
		}

		if ((workbook.getTrialObservations() != null && totalRows != workbook.getTrialObservations().size() && totalRows > 0 || isDeleteObservations)
				&& trialDatasetId != null) {
			isDeleteTrialObservations = true;
			// delete measurement data
			this.getExperimentDestroyer().deleteExperimentsByStudy(datasetId);
			session.flush();
			session.clear();

			// reset trial observation details such as experimentid, stockid and geolocationid
			this.resetTrialObservations(workbook.getTrialObservations());
		}

		if (trialVariableTypeList != null && !isDeleteObservations) {
			// multi-location for data loader
			studyLocationId =
					this.createLocationsAndSetToObservations(locationIds, workbook, trialVariableTypeList, trialHeaders, trialVariatesMap,
							false, programUUID);
		} else if (workbook.getTrialObservations() != null && workbook.getTrialObservations().size() > 1) {
			// also a multi-location
			studyLocationId =
					this.createLocationsAndSetToObservations(locationIds, workbook, trialVariables, trialHeaders, trialVariatesMap,
							isDeleteTrialObservations, programUUID);
		} else {
			studyLocationId =
					this.createLocationAndSetToObservations(workbook, trialMV, trialVariables, trialVariatesMap, isDeleteTrialObservations,
							programUUID);
		}

		// GCP-6091 end
		if (isDeleteTrialObservations) {
			session.flush();
			session.clear();
			ExperimentModel studyExperiment =
					this.getExperimentDao().getExperimentsByProjectIds(Arrays.asList(workbook.getStudyDetails().getId())).get(0);
			studyExperiment.setGeoLocation(this.getGeolocationDao().getById(studyLocationId));
			this.getExperimentDao().saveOrUpdate(studyExperiment);

			// delete trial observations
			this.getExperimentDestroyer().deleteTrialExperimentsOfStudy(trialDatasetId);
		}

		int studyId = 0;
		if (!(workbook.getStudyDetails() != null && workbook.getStudyDetails().getId() != null)) {
			studyId = this.createStudyIfNecessary(workbook, studyLocationId, true, programUUID);
		} else {
			studyId = workbook.getStudyDetails().getId();
		}
		trialDatasetId = this.createTrialDatasetIfNecessary(workbook, studyId, trialMV, trialVariables, programUUID);

		this.saveOrUpdateTrialObservations(trialDatasetId, workbook, trialVariableTypeList, locationIds, trialVariatesMap, studyLocationId,
				totalRows, isDeleteObservations);

		datasetId =
				this.createMeasurementEffectDatasetIfNecessary(workbook, studyId, effectMV, effectVariables, trialVariables, programUUID);
		this.createStocksIfNecessary(datasetId, workbook, effectVariables, trialHeaders);

		if (!retainValues) {
			// clean up some variable references to save memory space before saving the measurement effects
			variableMap = null;
			headerMap = null;
			variableTypeMap = null;
			measurementVariableMap = null;
			trialVariableTypeList = null;
			effectMV = null;
			workbook.reset();
			workbook.setConditions(null);
			workbook.setConstants(null);
			workbook.setFactors(null);
			workbook.setStudyDetails(null);
			workbook.setVariates(null);
		}

		this.createMeasurementEffectExperiments(datasetId, effectVariables, workbook.getObservations(), trialHeaders, trialVariatesMap);

		return studyId;
	}

	public void removeDeletedVariablesAndObservations(Workbook workbook) {
		this.deleteDeletedVariablesInObservations(workbook.getFactors(), workbook.getObservations());
		this.deleteDeletedVariablesInObservations(workbook.getVariates(), workbook.getObservations());
		this.deleteDeletedVariables(workbook.getConditions());
		this.deleteDeletedVariables(workbook.getFactors());
		this.deleteDeletedVariables(workbook.getVariates());
		this.deleteDeletedVariables(workbook.getConstants());
	}

	private void deleteDeletedVariablesInObservations(List<MeasurementVariable> variableList, List<MeasurementRow> observations) {
		List<Integer> deletedList = new ArrayList<Integer>();
		if (variableList != null) {
			for (MeasurementVariable var : variableList) {
				if (var.getOperation() != null && var.getOperation().equals(Operation.DELETE)) {
					deletedList.add(Integer.valueOf(var.getTermId()));
				}
			}
		}
		if (deletedList != null && observations != null) {
			for (Integer termId : deletedList) {
				// remove from measurement rows
				int index = 0;
				int varIndex = 0;
				boolean found = false;
				for (MeasurementRow row : observations) {
					if (index == 0) {
						for (MeasurementData var : row.getDataList()) {
							if (var.getMeasurementVariable().getTermId() == termId.intValue()) {
								found = true;
								break;
							}
							varIndex++;
						}
					}
					if (found) {
						row.getDataList().remove(varIndex);
					} else {
						break;
					}
					index++;
				}
			}
		}
	}

	private void deleteDeletedVariables(List<MeasurementVariable> variableList) {
		if (variableList != null) {
			Iterator<MeasurementVariable> variable = variableList.iterator();
			while (variable.hasNext()) {
				MeasurementVariable var = variable.next();
				if (var.getOperation() != null && var.getOperation().equals(Operation.DELETE)) {
					variable.remove();
				}
			}
		}
	}

	public void resetTrialObservations(List<MeasurementRow> trialObservations) {
		for (MeasurementRow row : trialObservations) {
			row.setExperimentId(0);
			row.setLocationId(0);
			row.setStockId(0);
			for (MeasurementData data : row.getDataList()) {
				data.setPhenotypeId(null);
			}
		}
	}

	public void saveOrUpdateTrialObservations(int trialDatasetId, Workbook workbook, VariableTypeList trialVariableTypeList,
			List<Integer> locationIds, Map<Integer, VariableList> trialVariatesMap, int studyLocationId, int totalRows,
			boolean isDeleteObservations) throws MiddlewareQueryException, MiddlewareException {
		if (workbook.getTrialObservations() != null && totalRows == workbook.getTrialObservations().size() && totalRows > 0
				&& !isDeleteObservations) {
			this.saveTrialObservations(workbook);
		} else {
			if (locationIds != null && !locationIds.isEmpty()) {// multi-location
				for (Integer locationId : locationIds) {
					this.createTrialExperiment(trialDatasetId, locationId, trialVariatesMap.get(locationId));
				}
			} else {
				this.createTrialExperiment(trialDatasetId, studyLocationId, trialVariatesMap.get(studyLocationId));
			}
		}
	}

	public void saveTrialObservations(Workbook workbook) throws MiddlewareQueryException, MiddlewareException {
		if (workbook.getTrialObservations() != null && !workbook.getTrialObservations().isEmpty()) {
			for (MeasurementRow trialObservation : workbook.getTrialObservations()) {
				this.getGeolocationSaver().updateGeolocationInformation(trialObservation, workbook.isNursery());
			}
		}
	}

	public int createLocationAndSetToObservations(Workbook workbook, List<MeasurementVariable> trialMV, VariableTypeList trialVariables,
			Map<Integer, VariableList> trialVariatesMap, boolean isDeleteTrialObservations, String programUUID)
			throws MiddlewareQueryException {

		TimerWatch watch = new TimerWatch("transform trial environment");
		if (workbook.getTrialObservations() != null && workbook.getTrialObservations().size() == 1) {
			MeasurementRow trialObs = workbook.getTrialObservations().get(0);
			for (MeasurementVariable mv : trialMV) {
				for (MeasurementData mvrow : trialObs.getDataList()) {
					if (mvrow.getMeasurementVariable().getTermId() == mv.getTermId()) {
						mv.setValue(mvrow.getValue());
						break;
					}
				}
			}
		}
		VariableList geolocation = this.getVariableListTransformer().transformTrialEnvironment(trialMV, trialVariables);
		Integer studyLocationId = null;

		// GCP-8092 Nurseries will always have a unique geolocation, no more concept of shared/common geolocation
		if (geolocation == null || geolocation.isEmpty()) {
			geolocation = this.createDefaultGeolocationVariableList();
		}

		watch.restart("save geolocation");
		Geolocation g =
				this.getGeolocationSaver().saveGeolocationOrRetrieveIfExisting(workbook.getStudyDetails().getStudyName(), geolocation,
						null, workbook.isNursery(), isDeleteTrialObservations, programUUID);
		studyLocationId = g.getLocationId();
		if (g.getVariates() != null && !g.getVariates().isEmpty()) {
			VariableList trialVariates = new VariableList();
			trialVariates.addAll(g.getVariates());
			trialVariatesMap.put(studyLocationId, trialVariates);
		}

		watch.restart("set to observations(total)");
		if (workbook.getTrialObservations() != null && !workbook.getTrialObservations().isEmpty()) {
			for (MeasurementRow row : workbook.getTrialObservations()) {
				row.setLocationId(studyLocationId);
			}
		}
		if (workbook.getObservations() != null) {
			for (MeasurementRow row : workbook.getObservations()) {
				row.setLocationId(studyLocationId);
			}
		}
		watch.stop();

		return studyLocationId;
	}

	public int createLocationsAndSetToObservations(List<Integer> locationIds, Workbook workbook, VariableTypeList trialFactors,
			List<String> trialHeaders, Map<Integer, VariableList> trialVariatesMap, boolean isDeleteTrialObservations, String programUUID)
			throws MiddlewareQueryException {

		Set<String> trialInstanceNumbers = new HashSet<String>();
		Integer locationId = null;
		List<MeasurementRow> observations = null;
		Long geolocationId = null;
		boolean hasTrialObservations = false;
		if (workbook.getTrialObservations() != null && !workbook.getTrialObservations().isEmpty()) {
			observations = workbook.getTrialObservations();
			hasTrialObservations = true;
			if (workbook.isNursery()) {
				geolocationId = observations.get(0).getLocationId();
			}
		} else {
			observations = workbook.getObservations();
		}
		Map<String, Integer> locationMap = new HashMap<String, Integer>();
		if (observations != null) {
			for (MeasurementRow row : observations) {
				geolocationId = row.getLocationId();
				if (geolocationId != null && geolocationId != 0) {
					// if geolocationId already exists, no need to create the geolocation
					row.setLocationId(geolocationId);
				} else {
					TimerWatch watch =
							new TimerWatch("transformTrialEnvironment in createLocationsAndSetToObservations");
					VariableList geolocation = this.getVariableListTransformer().transformTrialEnvironment(row, trialFactors, trialHeaders);
					if (geolocation != null && !geolocation.isEmpty()) {
						String trialInstanceNumber = this.getTrialInstanceNumber(geolocation);
						if (WorkbookSaver.LOG.isDebugEnabled()) {
							WorkbookSaver.LOG.debug("trialInstanceNumber = " + trialInstanceNumber);
						}
						if (trialInstanceNumbers.add(trialInstanceNumber)) {
							// if new location (unique by trial instance number)
							watch.restart("save geolocation");
							Geolocation g =
									this.getGeolocationSaver().saveGeolocationOrRetrieveIfExisting(
											workbook.getStudyDetails().getStudyName(), geolocation, row, workbook.isNursery(),
											isDeleteTrialObservations, programUUID);
							locationId = g.getLocationId();
							locationIds.add(locationId);
							if (g.getVariates() != null && !g.getVariates().isEmpty()) {
								VariableList trialVariates = new VariableList();
								trialVariates.addAll(g.getVariates());
								trialVariatesMap.put(locationId, trialVariates);
							}
						}
						row.setLocationId(locationId);
						locationMap.put(trialInstanceNumber, locationId);
					}
				}
			}

			if (hasTrialObservations && workbook.getObservations() != null) {
				for (MeasurementRow row : workbook.getObservations()) {
					String trialInstance = this.getTrialInstanceNumber(row);
					if (trialInstance != null) {
						Integer locId = locationMap.get(trialInstance);
						row.setLocationId(locId);
					} else if (geolocationId != null) {
						row.setLocationId(geolocationId);
					}
				}
			}
			// return studyLocationId
			if (workbook.getObservations() != null && !workbook.getObservations().isEmpty()) {
				return Long.valueOf(workbook.getObservations().get(0).getLocationId()).intValue();
			} else {
				return Long.valueOf(workbook.getTrialObservations().get(0).getLocationId()).intValue();
			}
		}

		return 0;
	}

	private String getTrialInstanceNumber(MeasurementRow row) {
		for (MeasurementData data : row.getDataList()) {
			if (data.getMeasurementVariable().getTermId() == TermId.TRIAL_INSTANCE_FACTOR.getId()) {
				return data.getValue();
			}
		}
		return null;
	}

	private MeasurementVariable getMainFactor(List<MeasurementVariable> mvars) {
		for (MeasurementVariable mvar : mvars) {
			if (mvar.getName().equals(mvar.getLabel())) {
				return mvar;
			}
		}
		return null;
	}

	private String getStockFactor(VariableList stockVariables) {
		Map<String, Variable> variableMap = stockVariables.getVariableMap();
		if (variableMap != null) {
			Variable var = variableMap.get(Integer.toString(TermId.ENTRY_NUMBER_STORAGE.getId()));
			if (var != null) {
				return var.getValue();
			}
		}
		return null;
	}

	private String getTrialInstanceNumber(VariableList trialVariables) {
		Map<String, Variable> variableMap = trialVariables.getVariableMap();
		if (variableMap != null) {
			Variable var = variableMap.get(Integer.toString(TermId.TRIAL_INSTANCE_STORAGE.getId()));
			if (var != null) {
				return var.getValue();
			}
		}
		return null;
	}

	private String generateTrialDatasetName(String studyName, StudyType studyType) {
		return studyName + "-ENVIRONMENT";
	}

	private String generateMeasurementEffectDatasetName(String studyName) {
		return studyName + "-PLOTDATA";
	}

	private String generateMeansDatasetName(String studyName) {
		return studyName + "-MEANS";
	}

	private ExperimentValues createTrialExperimentValues(Integer locationId, VariableList variates) {
		ExperimentValues value = new ExperimentValues();
		value.setLocationId(locationId);
		value.setVariableList(variates);
		return value;
	}

	private int createStudyIfNecessary(Workbook workbook, int studyLocationId, boolean saveStudyExperiment, String programUUID)
			throws Exception {
		TimerWatch watch = new TimerWatch("find study");

		Integer studyId = null;
		if (workbook.getStudyDetails() != null) {
			studyId = this.getStudyId(workbook.getStudyDetails().getStudyName(), programUUID);
		}

		if (studyId == null) {
			watch.restart("transform variables for study");
			List<MeasurementVariable> studyMV = workbook.getStudyVariables();
			VariableTypeList studyVariables = this.getVariableTypeListTransformer().transform(workbook.getStudyConditions(), false);
			studyVariables.addAll(this.getVariableTypeListTransformer().transform(workbook.getStudyConstants(), true,
					studyVariables.size() + 1));

			if (workbook.isNursery() && this.getMainFactor(workbook.getTrialVariables()) == null) {
				studyVariables.add(this.createOccVariableType(studyVariables.size() + 1));
			}

			StudyValues studyValues =
					this.getStudyValuesTransformer().transform(null, studyLocationId, workbook.getStudyDetails(), studyMV, studyVariables);

			watch.restart("save study");
			DmsProject study =
					this.getStudySaver().saveStudy((int) workbook.getStudyDetails().getParentFolderId(), studyVariables, studyValues,
							saveStudyExperiment, programUUID);
			studyId = study.getProjectId();
		}
		watch.stop();

		return studyId;
	}

	private int createTrialDatasetIfNecessary(Workbook workbook, int studyId, List<MeasurementVariable> trialMV,
			VariableTypeList trialVariables, String programUUID) throws MiddlewareQueryException {
		TimerWatch watch = new TimerWatch("find trial dataset");
		String trialName = workbook.getStudyDetails().getTrialDatasetName();
		Integer trialDatasetId = null;
		if (trialName == null || "".equals(trialName)) {
			List<DatasetReference> datasetRefList = this.getStudyDataManager().getDatasetReferences(studyId);
			if (datasetRefList != null) {
				for (DatasetReference datasetRef : datasetRefList) {
					if (datasetRef.getName().equals("TRIAL_" + workbook.getStudyDetails().getStudyName())) {
						trialDatasetId = datasetRef.getId();
					}
				}
				if (trialDatasetId == null) {
					trialName =
							this.generateTrialDatasetName(workbook.getStudyDetails().getStudyName(), workbook.getStudyDetails()
									.getStudyType());
					trialDatasetId =
							this.getDatasetId(trialName, this.generateTrialDatasetName(workbook.getStudyDetails().getStudyName(), workbook
									.getStudyDetails().getStudyType()), programUUID);
				}
			} else {
				trialName =
						this.generateTrialDatasetName(workbook.getStudyDetails().getStudyName(), workbook.getStudyDetails().getStudyType());
				trialDatasetId =
						this.getDatasetId(trialName, this.generateTrialDatasetName(workbook.getStudyDetails().getStudyName(), workbook
								.getStudyDetails().getStudyType()), programUUID);
			}
		}
		if (trialDatasetId == null) {
			watch.restart("transform trial dataset values");
			DatasetValues trialValues =
					this.getDatasetValuesTransformer().transform(trialName, trialName, DataSetType.SUMMARY_DATA, trialMV, trialVariables);

			if (workbook.isNursery() && (trialMV == null || trialMV.isEmpty() || this.getMainFactor(trialMV) == null)) {
				trialVariables.add(this.createOccVariableType(trialVariables.size() + 1));
			}

			watch.restart("save trial dataset");
			DmsProject trial = this.getDatasetProjectSaver().addDataSet(studyId, trialVariables, trialValues, programUUID);
			trialDatasetId = trial.getProjectId();
		} else {
			if (workbook.isNursery() && (trialMV == null || trialMV.isEmpty() || this.getMainFactor(trialMV) == null)) {
				trialVariables.add(this.createOccVariableType(trialVariables.size() + 1));
			}
		}

		watch.stop();
		return trialDatasetId;
	}

	private void createTrialExperiment(int trialProjectId, int locationId, VariableList trialVariates) throws MiddlewareQueryException {
		TimerWatch watch = new TimerWatch("save trial experiments");
		ExperimentValues trialDatasetValues = this.createTrialExperimentValues(locationId, trialVariates);
		this.getExperimentModelSaver().addExperiment(trialProjectId, ExperimentType.TRIAL_ENVIRONMENT, trialDatasetValues);
		watch.stop();
	}

	private int createMeasurementEffectDatasetIfNecessary(Workbook workbook, int studyId, List<MeasurementVariable> effectMV,
			VariableTypeList effectVariables, VariableTypeList trialVariables, String programUUID) throws MiddlewareQueryException,
			MiddlewareException {
		TimerWatch watch = new TimerWatch("find measurement effect dataset");
		String datasetName = workbook.getStudyDetails().getMeasurementDatasetName();
		Integer datasetId = null;

		if (datasetName == null || "".equals(datasetName)) {
			List<DatasetReference> datasetRefList = this.getStudyDataManager().getDatasetReferences(studyId);
			if (datasetRefList != null) {
				for (DatasetReference datasetRef : datasetRefList) {
					if (datasetRef.getName().equals("MEASUREMENT EFEC_" + workbook.getStudyDetails().getStudyName())
							|| datasetRef.getName().equals("MEASUREMENT EFECT_" + workbook.getStudyDetails().getStudyName())) {
						datasetId = datasetRef.getId();
					}
				}
				if (datasetId == null) {
					datasetName = this.generateMeasurementEffectDatasetName(workbook.getStudyDetails().getStudyName());
					datasetId =
							this.getDatasetId(datasetName,
									this.generateMeasurementEffectDatasetName(workbook.getStudyDetails().getStudyName()), programUUID);
				}
			} else {
				datasetName = this.generateMeasurementEffectDatasetName(workbook.getStudyDetails().getStudyName());
				datasetId =
						this.getDatasetId(datasetName,
								this.generateMeasurementEffectDatasetName(workbook.getStudyDetails().getStudyName()), programUUID);
			}
		}

		if (datasetId == null) {
			watch.restart("transform measurement effect dataset");
			DatasetValues datasetValues =
					this.getDatasetValuesTransformer()
							.transform(datasetName, datasetName, DataSetType.PLOT_DATA, effectMV, effectVariables);

			watch.restart("save measurement effect dataset");
			// fix for GCP-6436 start
			VariableTypeList datasetVariables = this.propagateTrialFactorsIfNecessary(effectVariables, trialVariables);
			// no need to add occ as it is already added in trialVariables
			// fix for GCP-6436 end
			DmsProject dataset = this.getDatasetProjectSaver().addDataSet(studyId, datasetVariables, datasetValues, programUUID);
			datasetId = dataset.getProjectId();
		}

		watch.stop();
		return datasetId;
	}

	public void createStocksIfNecessary(int datasetId, Workbook workbook, VariableTypeList effectVariables, List<String> trialHeaders)
			throws MiddlewareQueryException {
		Map<String, Integer> stockMap = this.getStockModelBuilder().getStockMapForDataset(datasetId);

		Session session = this.getCurrentSession();
		int i = 0;
		List<Integer> variableIndexesList = new ArrayList<Integer>();
		// we get the indexes so that in the next rows we dont need to compare anymore per row
		if (workbook.getObservations() != null && !workbook.getObservations().isEmpty()) {
			MeasurementRow row = workbook.getObservations().get(0);
			variableIndexesList = this.getVariableListTransformer().transformStockIndexes(row, effectVariables, trialHeaders);
		}
		if (workbook.getObservations() != null) {
			for (MeasurementRow row : workbook.getObservations()) {

				VariableList stock =
						this.getVariableListTransformer().transformStockOptimize(variableIndexesList, row, effectVariables, trialHeaders);
				String stockFactor = this.getStockFactor(stock);
				Integer stockId = stockMap.get(stockFactor);

				if (stockId == null) {
					stockId = this.getStockSaver().saveStock(stock);
					stockMap.put(stockFactor, stockId);
				} else {
					this.getStockSaver().saveOrUpdateStock(stock, stockId);
				}
				row.setStockId(stockId);
				if (i % 50 == 0) {
					// to save memory space - http://docs.jboss.org/hibernate/core/3.3/reference/en/html/batch.html#batch-inserts
					session.flush();
					session.clear();
				}
				i++;
			}
		}

	}

	private void createMeasurementEffectExperiments(int datasetId, VariableTypeList effectVariables, List<MeasurementRow> observations,
			List<String> trialHeaders, Map<Integer, VariableList> trialVariatesMap) throws MiddlewareQueryException {

		TimerWatch watch = new TimerWatch("saving stocks and measurement effect data (total)");
		TimerWatch rowWatch = new TimerWatch("for each row");

		// observation values start at row 2
		int i = 2;
		Session session = this.getCurrentSession();
		ExperimentValuesTransformer experimentValuesTransformer = this.getExperimentValuesTransformer();
		ExperimentModelSaver experimentModelSaver = this.getExperimentModelSaver();
		Map<Integer, PhenotypeExceptionDto> exceptions = null;
		if (observations != null) {
			for (MeasurementRow row : observations) {
				rowWatch.restart("saving row " + i++);
				ExperimentValues experimentValues = experimentValuesTransformer.transform(row, effectVariables, trialHeaders);
				VariableList trialVariates = trialVariatesMap.get((int) row.getLocationId());
				if (trialVariates != null) {
					experimentValues.getVariableList().addAll(trialVariates);
				}
				try {
					experimentModelSaver.addExperiment(datasetId, ExperimentType.PLOT, experimentValues);
				} catch (PhenotypeException e) {
					WorkbookSaver.LOG.error(e.getMessage(), e);
					if (exceptions == null) {
						exceptions = e.getExceptions();
					} else {
						for (Integer standardVariableId : e.getExceptions().keySet()) {
							PhenotypeExceptionDto exception = e.getExceptions().get(standardVariableId);
							if (exceptions.get(standardVariableId) == null) {
								// add exception
								exceptions.put(standardVariableId, exception);
							} else {
								// add invalid values to the existing map of exceptions for each phenotype
								for (String invalidValue : exception.getInvalidValues()) {
									exceptions.get(standardVariableId).getInvalidValues().add(invalidValue);
								}
							}
						}
					}
				}
				if (i % 50 == 0) {
					// to save memory space - http://docs.jboss.org/hibernate/core/3.3/reference/en/html/batch.html#batch-inserts
					session.flush();
					session.clear();
				}
			}
		}
		rowWatch.stop();
		watch.stop();

		if (exceptions != null) {
			throw new PhenotypeException(exceptions);
		}
	}

	private boolean isTrialFactorInDataset(VariableTypeList list) {

		for (VariableType var : list.getVariableTypes()) {
			if (TermId.TRIAL_INSTANCE_STORAGE.getId() == var.getStandardVariable().getStoredIn().getId()) {
				return true;
			}
		}
		return false;

	}

	private VariableType createOccVariableType(int rank) throws MiddlewareQueryException {
		VariableInfo info = new VariableInfo();
		info.setLocalName("TRIAL_INSTANCE");
		info.setLocalDescription("TRIAL_INSTANCE");
		info.setStdVariableId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		info.setRank(rank);
		return this.getVariableTypeBuilder().create(info);
	}

	protected VariableTypeList propagateTrialFactorsIfNecessary(VariableTypeList effectVariables, VariableTypeList trialVariables) {

		VariableTypeList newList = new VariableTypeList();

		if (!this.isTrialFactorInDataset(effectVariables) && trialVariables != null) {
			int index = 1;
			for (VariableType var : trialVariables.getVariableTypes()) {
				if (var.getId() == TermId.TRIAL_INSTANCE_FACTOR.getId()
						|| !PhenotypicType.TRIAL_ENVIRONMENT.getTypeStorages().contains(
								Integer.valueOf(var.getStandardVariable().getStoredIn().getId()))
						&& !PhenotypicType.VARIATE.getTypeStorages().contains(
								Integer.valueOf(var.getStandardVariable().getStoredIn().getId()))) {
					var.setRank(index);
					newList.add(var);
					index++;
				}
			}

			effectVariables.allocateRoom(newList.size());
		}
		newList.addAll(effectVariables);

		return newList;
	}

	private Integer getStudyId(String name, String programUUID) throws MiddlewareQueryException {
		return this.getProjectId(name, programUUID, TermId.IS_STUDY);
	}

	private Integer getDatasetId(String name, String generatedName, String programUUID) throws MiddlewareQueryException {
		Integer id = this.getProjectId(name, programUUID, TermId.BELONGS_TO_STUDY);
		if (id == null && !name.equals(generatedName)) {
			id = this.getProjectId(generatedName, programUUID, TermId.BELONGS_TO_STUDY);
		}
		return id;
	}

	private Integer getProjectId(String name, String programUUID, TermId relationship) throws MiddlewareQueryException {
		return this.getDmsProjectDao().getProjectIdByNameAndProgramUUID(name, programUUID, relationship);
	}

	private Integer getMeansDataset(Integer studyId) throws MiddlewareQueryException {
		Integer id = null;
		List<DmsProject> datasets =
				this.getDmsProjectDao().getDataSetsByStudyAndProjectProperty(studyId, TermId.DATASET_TYPE.getId(),
						String.valueOf(DataSetType.MEANS_DATA.getId()));
		if (datasets != null && !datasets.isEmpty()) {
			id = datasets.get(0).getProjectId();
		}
		return id;
	}

	/**
	 * Saves project ontology creating entries in the following tables: project, projectprop and project_relationship
	 * 
	 * @param workbook
	 * @return study id
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public int saveProjectOntology(Workbook workbook, String programUUID) throws Exception {

		final Map<String, ?> variableMap = this.saveVariables(workbook);
		workbook.setVariableMap(variableMap);

		// unpack maps first level - Maps of Strings, Maps of VariableTypeList , Maps of Lists of MeasurementVariable
		Map<String, VariableTypeList> variableTypeMap = (Map<String, VariableTypeList>) variableMap.get("variableTypeMap");
		Map<String, List<MeasurementVariable>> measurementVariableMap =
				(Map<String, List<MeasurementVariable>>) variableMap.get("measurementVariableMap");

		// unpack maps
		VariableTypeList trialVariables = new VariableTypeList();
		// addAll instead of assigning directly to avoid changing the state of the object
		trialVariables.addAll(variableTypeMap.get("trialVariables"));
		VariableTypeList effectVariables = new VariableTypeList();
		// addAll instead of assigning directly to avoid changing the state of the object
		effectVariables.addAll(variableTypeMap.get("effectVariables"));
		List<MeasurementVariable> trialMV = measurementVariableMap.get("trialMV");
		List<MeasurementVariable> effectMV = measurementVariableMap.get("effectMV");

		// locationId and experiment are not yet needed here
		int studyId = this.createStudyIfNecessary(workbook, 0, false, programUUID);
		int trialDatasetId = this.createTrialDatasetIfNecessary(workbook, studyId, trialMV, trialVariables, programUUID);
		int measurementDatasetId = 0;
		int meansDatasetId = 0;
		if (workbook.getImportType() != null && workbook.getImportType().intValue() == DataSetType.MEANS_DATA.getId()) {
			meansDatasetId = this.createMeansDatasetIfNecessary(workbook, studyId, effectMV, effectVariables, trialVariables, programUUID);
		} else {
			measurementDatasetId =
					this.createMeasurementEffectDatasetIfNecessary(workbook, studyId, effectMV, effectVariables, trialVariables,
							programUUID);
		}

		workbook.populateStudyAndDatasetIds(studyId, trialDatasetId, measurementDatasetId, meansDatasetId);

		if (WorkbookSaver.LOG.isDebugEnabled()) {
			WorkbookSaver.LOG.debug("studyId = " + studyId);
			WorkbookSaver.LOG.debug("trialDatasetId = " + trialDatasetId);
			WorkbookSaver.LOG.debug("measurementDatasetId = " + measurementDatasetId);
			WorkbookSaver.LOG.debug("meansDatasetId = " + meansDatasetId);
		}

		return studyId;
	}

	/**
	 * Saves experiments creating entries in the following tables: nd_geolocation, nd_geolocationprop, nd_experiment, nd_experiment_project,
	 * nd_experimentprop nd_experiment_stock, stock, stockprop, nd_experiment_phenotype and phenotype
	 * 
	 * @param workbook
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void saveProjectData(Workbook workbook, String programUUID) throws Exception {

		int studyId = workbook.getStudyId();
		int trialDatasetId = workbook.getTrialDatasetId();
		int measurementDatasetId = workbook.getMeasurementDatesetId() != null ? workbook.getMeasurementDatesetId() : 0;
		int meansDatasetId = workbook.getMeansDatasetId() != null ? workbook.getMeansDatasetId() : 0;
		boolean isMeansDataImport =
				workbook.getImportType() != null && workbook.getImportType().intValue() == DataSetType.MEANS_DATA.getId();

		Map<String, ?> variableMap = workbook.getVariableMap();
		if (variableMap == null || variableMap.isEmpty()) {
			variableMap = this.saveVariables(workbook);
		}

		// unpack maps first level - Maps of Strings, Maps of VariableTypeList , Maps of Lists of MeasurementVariable
		Map<String, List<String>> headerMap = (Map<String, List<String>>) variableMap.get("headerMap");
		Map<String, VariableTypeList> variableTypeMap = (Map<String, VariableTypeList>) variableMap.get("variableTypeMap");
		Map<String, List<MeasurementVariable>> measurementVariableMap =
				(Map<String, List<MeasurementVariable>>) variableMap.get("measurementVariableMap");

		// unpack maps
		List<String> trialHeaders = headerMap.get("trialHeaders");
		VariableTypeList trialVariableTypeList = variableTypeMap.get("trialVariableTypeList");
		VariableTypeList trialVariables = variableTypeMap.get("trialVariables");
		VariableTypeList effectVariables = variableTypeMap.get("effectVariables");
		List<MeasurementVariable> trialMV = measurementVariableMap.get("trialMV");

		// GCP-8092 Nurseries will always have a unique geolocation, no more concept of shared/common geolocation
		// create locations (entries to nd_geolocation) and associate to observations
		int studyLocationId/* = DEFAULT_GEOLOCATION_ID */;
		List<Integer> locationIds = new ArrayList<Integer>();
		Map<Integer, VariableList> trialVariatesMap = new HashMap<Integer, VariableList>();
		if (trialVariableTypeList != null) {// multi-location
			studyLocationId =
					this.createLocationsAndSetToObservations(locationIds, workbook, trialVariableTypeList, trialHeaders, trialVariatesMap,
							false, programUUID);
		} else {
			studyLocationId =
					this.createLocationAndSetToObservations(workbook, trialMV, trialVariables, trialVariatesMap, false, programUUID);
		}

		// create stock and stockprops and associate to observations
		int datasetId = measurementDatasetId;
		if (isMeansDataImport) {
			datasetId = meansDatasetId;
		}
		this.createStocksIfNecessary(datasetId, workbook, effectVariables, trialHeaders);

		// create trial experiments if not yet existing
		boolean hasExistingStudyExperiment = this.checkIfHasExistingStudyExperiment(studyId);
		boolean hasExistingTrialExperiments = this.checkIfHasExistingExperiments(locationIds);
		if (!hasExistingStudyExperiment) {
			// 1. study experiment
			StudyValues values = new StudyValues();
			values.setLocationId(studyLocationId);
			this.getStudySaver().saveStudyExperiment(studyId, values);
		}
		// create trial experiments if not yet existing
		if (!hasExistingTrialExperiments) {
			// 2. trial experiments
			if (trialVariableTypeList != null) {// multi-location
				for (Integer locationId : locationIds) {
					this.createTrialExperiment(trialDatasetId, locationId, trialVariatesMap.get(locationId));
				}
			} else {
				this.createTrialExperiment(trialDatasetId, studyLocationId, trialVariatesMap.get(studyLocationId));
			}
		}
		if (isMeansDataImport) {
			// 3. means experiments
			this.createMeansExperiments(meansDatasetId, effectVariables, workbook.getObservations(), trialHeaders, trialVariatesMap);
		} else {
			// 3. measurement experiments
			this.createMeasurementEffectExperiments(measurementDatasetId, effectVariables, workbook.getObservations(), trialHeaders,
					trialVariatesMap);
		}
	}

	private boolean checkIfHasExistingStudyExperiment(int studyId) throws MiddlewareQueryException {
		Integer experimentId = this.getExperimentProjectDao().getExperimentIdByProjectId(studyId);
		if (experimentId != null) {
			return true;
		}
		return false;
	}

	private boolean checkIfHasExistingExperiments(List<Integer> locationIds) throws MiddlewareQueryException {
		List<Integer> experimentIds = this.getExperimentDao().getExperimentIdsByGeolocationIds(locationIds);
		if (experimentIds != null && !experimentIds.isEmpty()) {
			return true;
		}
		return false;
	}

	private VariableList createDefaultGeolocationVariableList() throws MiddlewareQueryException {
		VariableList list = new VariableList();

		VariableType variableType =
				new VariableType(PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().get(0), PhenotypicType.TRIAL_ENVIRONMENT.getLabelList()
						.get(0), this.getStandardVariableBuilder().create(TermId.TRIAL_INSTANCE_FACTOR.getId()), 1);
		Variable variable = new Variable(variableType, "1");
		list.add(variable);

		return list;
	}

	public void saveWorkbookVariables(Workbook workbook) throws MiddlewareQueryException {
		this.getProjectRelationshipSaver().saveOrUpdateStudyToFolder(workbook.getStudyDetails().getId(),
				Long.valueOf(workbook.getStudyDetails().getParentFolderId()).intValue());
		DmsProject study = this.getDmsProjectDao().getById(workbook.getStudyDetails().getId());
		Integer trialDatasetId = workbook.getTrialDatasetId(), measurementDatasetId = workbook.getMeasurementDatesetId();
		if (workbook.getTrialDatasetId() == null || workbook.getMeasurementDatesetId() == null) {
			measurementDatasetId = this.getWorkbookBuilder().getMeasurementDataSetId(study.getProjectId(), workbook.getStudyName());
			List<DmsProject> datasets =
					this.getProjectRelationshipDao().getSubjectsByObjectIdAndTypeId(study.getProjectId(), TermId.BELONGS_TO_STUDY.getId());
			if (datasets != null) {
				for (DmsProject dataset : datasets) {
					if (!dataset.getProjectId().equals(measurementDatasetId)) {
						trialDatasetId = dataset.getProjectId();
						break;
					}
				}
			}
		}
		DmsProject trialDataset = this.getDmsProjectDao().getById(trialDatasetId);
		DmsProject measurementDataset = this.getDmsProjectDao().getById(measurementDatasetId);

		this.getProjectPropertySaver().saveProjectProperties(study, trialDataset, measurementDataset, workbook.getConditions(), false);
		this.getProjectPropertySaver().saveProjectProperties(study, trialDataset, measurementDataset, workbook.getConstants(), true);
		this.getProjectPropertySaver().saveProjectProperties(study, trialDataset, measurementDataset, workbook.getVariates(), false);
		this.getProjectPropertySaver().saveFactors(measurementDataset, workbook.getFactors());
	}

	private int createMeansDatasetIfNecessary(Workbook workbook, int studyId, List<MeasurementVariable> effectMV,
			VariableTypeList effectVariables, VariableTypeList trialVariables, String programUUID) throws MiddlewareQueryException,
			MiddlewareException {

		TimerWatch watch = new TimerWatch("find means dataset");
		Integer datasetId = this.getMeansDataset(studyId);

		if (datasetId == null) {
			watch.restart("transform means dataset");
			String datasetName = this.generateMeansDatasetName(workbook.getStudyDetails().getStudyName());
			DatasetValues datasetValues =
					this.getDatasetValuesTransformer().transform(datasetName, datasetName, DataSetType.MEANS_DATA, effectMV,
							effectVariables);

			watch.restart("save means dataset");
			VariableTypeList datasetVariables = this.getMeansData(effectVariables, trialVariables);
			DmsProject dataset = this.getDatasetProjectSaver().addDataSet(studyId, datasetVariables, datasetValues, programUUID);
			datasetId = dataset.getProjectId();
		}

		watch.stop();
		return datasetId;
	}

	private VariableTypeList getMeansData(VariableTypeList effectVariables, VariableTypeList trialVariables)
			throws MiddlewareQueryException, MiddlewareException {

		VariableTypeList newList = new VariableTypeList();
		int rank = 1;
		for (VariableType var : trialVariables.getVariableTypes()) {
			var.setRank(rank++);
			newList.add(var);
		}
		for (VariableType var : effectVariables.getVariableTypes()) {
			var.setRank(rank++);
			newList.add(var);
		}
		return newList;
	}

	private void createMeansExperiments(int datasetId, VariableTypeList effectVariables, List<MeasurementRow> observations,
			List<String> trialHeaders, Map<Integer, VariableList> trialVariatesMap) throws MiddlewareQueryException {

		TimerWatch watch = new TimerWatch("saving means data (total)");
		TimerWatch rowWatch = new TimerWatch("for each row");

		// observation values start at row 2
		int i = 2;
		Session session = this.getCurrentSession();
		ExperimentValuesTransformer experimentValuesTransformer = this.getExperimentValuesTransformer();
		ExperimentModelSaver experimentModelSaver = this.getExperimentModelSaver();
		Map<Integer, PhenotypeExceptionDto> exceptions = null;
		if (observations != null) {
			for (MeasurementRow row : observations) {
				rowWatch.restart("saving row " + i++);
				ExperimentValues experimentValues = experimentValuesTransformer.transform(row, effectVariables, trialHeaders);
				VariableList trialVariates = trialVariatesMap.get((int) row.getLocationId());
				if (trialVariates != null) {
					experimentValues.getVariableList().addAll(trialVariates);
				}
				try {
					experimentModelSaver.addExperiment(datasetId, ExperimentType.AVERAGE, experimentValues);
				} catch (PhenotypeException e) {
					WorkbookSaver.LOG.error(e.getMessage(), e);
					if (exceptions == null) {
						exceptions = e.getExceptions();
					} else {
						for (Integer standardVariableId : e.getExceptions().keySet()) {
							PhenotypeExceptionDto exception = e.getExceptions().get(standardVariableId);
							if (exceptions.get(standardVariableId) == null) {
								// add exception
								exceptions.put(standardVariableId, exception);
							} else {
								// add invalid values to the existing map of exceptions for each phenotype
								for (String invalidValue : exception.getInvalidValues()) {
									exceptions.get(standardVariableId).getInvalidValues().add(invalidValue);
								}
							}
						}
					}
				}
				if (i % 50 == 0) {
					// to save memory space - http://docs.jboss.org/hibernate/core/3.3/reference/en/html/batch.html#batch-inserts
					session.flush();
					session.clear();
				}
			}
		}
		rowWatch.stop();
		watch.stop();

		if (exceptions != null) {
			throw new PhenotypeException(exceptions);
		}
	}
}
