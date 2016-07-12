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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.domain.dms.DMSVariableType;
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
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.PhenotypeException;
import org.generationcp.middleware.helper.VariableInfo;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.operation.transformer.etl.ExperimentValuesTransformer;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.util.TimerWatch;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// ASsumptions - can be added to validations
// Mandatory fields: workbook.studyDetails.studyName
// template must not contain exact same combo of property-scale-method

public class WorkbookSaver extends Saver {

	private static final Logger LOG = LoggerFactory.getLogger(WorkbookSaver.class);

	public WorkbookSaver(final HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
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
	public Map saveVariables(final Workbook workbook, final String programUUID) throws Exception {
		// make sure to reset all derived variables
		workbook.reset();

		// Create Maps, which we will fill with transformed Workbook Variable Data
		final Map<String, List<String>> headerMap = new HashMap<String, List<String>>();
		final Map<String, VariableTypeList> variableTypeMap = new HashMap<String, VariableTypeList>();
		final Map<String, List<MeasurementVariable>> measurementVariableMap = new HashMap<String, List<MeasurementVariable>>();

		// GCP-6091 start
		final List<MeasurementVariable> trialMV = workbook.getTrialVariables();
		final List<String> trialHeaders = workbook.getTrialHeaders();
		final VariableTypeList trialVariables =
				this.getVariableTypeListTransformer().transform(workbook.getTrialConditions(), programUUID);
		final List<MeasurementVariable> trialFactors = workbook.getTrialFactors();
		VariableTypeList trialVariableTypeList = null;
		if (trialFactors != null && !trialFactors.isEmpty()) {// multi-location
			trialVariableTypeList = this.getVariableTypeListTransformer().transform(trialFactors, trialVariables.size() + 1, programUUID);
			trialVariables.addAll(trialVariableTypeList);
		}
		// GCP-6091 end
		trialVariables.addAll(this.getVariableTypeListTransformer()
				.transform(workbook.getTrialConstants(), trialVariables.size() + 1, programUUID));

		final VariableTypeList effectVariables =
				this.getVariableTypeListTransformer().transform(workbook.getNonTrialFactors(), programUUID);
		effectVariables.addAll(this.getVariableTypeListTransformer().transform(workbook.getVariates(), effectVariables.size() + 1 ,programUUID));

		// -- headers
		headerMap.put("trialHeaders", trialHeaders);
		// -- variableTypeLists
		variableTypeMap.put("trialVariableTypeList", trialVariableTypeList);
		variableTypeMap.put("trialVariables", trialVariables);
		variableTypeMap.put("effectVariables", effectVariables);
		// -- measurementVariables
		measurementVariableMap.put("trialMV", trialMV);

		final List<MeasurementVariable> effectMV = workbook.getMeasurementDatasetVariables();
		measurementVariableMap.put("effectMV", effectMV);

		// load 3 maps into a super Map
		final Map<String, Map<String, ?>> variableMap = new HashMap<>();
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
	public int saveDataset(final Workbook workbook, Map<String, ?> variableMap, final boolean retainValues,
			final boolean isDeleteObservations, final String programUUID) throws Exception {

		// unpack maps first level - Maps of Strings, Maps of VariableTypeList , Maps of Lists of MeasurementVariable
		Map<String, List<String>> headerMap = (Map<String, List<String>>) variableMap.get("headerMap");
		Map<String, VariableTypeList> variableTypeMap = (Map<String, VariableTypeList>) variableMap.get("variableTypeMap");
		Map<String, List<MeasurementVariable>> measurementVariableMap =
				(Map<String, List<MeasurementVariable>>) variableMap.get("measurementVariableMap");

		// unpack maps
		// Strings
		final List<String> trialHeaders = headerMap.get("trialHeaders");
		// VariableTypeLists
		VariableTypeList trialVariableTypeList = variableTypeMap.get("trialVariableTypeList");
		final VariableTypeList trialVariables = variableTypeMap.get("trialVariables");
		final VariableTypeList effectVariables = variableTypeMap.get("effectVariables");
		// Lists of measurementVariables
		final List<MeasurementVariable> trialMV = measurementVariableMap.get("trialMV");
		List<MeasurementVariable> effectMV = measurementVariableMap.get("effectMV");

		// TODO : Review code and see whether variable validation and possible dataset creation abort is a good idea (rebecca)

		// GCP-6091 start
		final int studyLocationId;
		final List<Integer> locationIds = new ArrayList<Integer>();
		final Map<Integer, VariableList> trialVariatesMap = new HashMap<Integer, VariableList>();

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

		if (isDeleteObservations && trialDatasetId != null) {
			isDeleteTrialObservations = true;
			// delete measurement data
			this.getExperimentDestroyer().deleteExperimentsByStudy(datasetId);
			// reset trial observation details such as experimentid, stockid and geolocationid
			this.resetTrialObservations(workbook.getTrialObservations());
		}

		if (trialVariableTypeList != null && !isDeleteObservations) {
			// multi-location for data loader
			studyLocationId =
					this.createLocationsAndSetToObservations(locationIds, workbook, trialVariableTypeList, trialHeaders, trialVariatesMap,
							false, programUUID);
		} else if (workbook.getTrialObservations().size() > 1) {
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

			final ExperimentModel studyExperiment =
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
				totalRows, isDeleteObservations, programUUID);

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
		} else {
			workbook.getStudyDetails().setId(studyId);
			workbook.setTrialDatasetId(trialDatasetId);
			workbook.setMeasurementDatesetId(datasetId);
		}

		this.createMeasurementEffectExperiments(datasetId, effectVariables, workbook.getObservations(), trialHeaders);

		return studyId;
	}

	public void removeDeletedVariablesAndObservations(final Workbook workbook) {
		this.deleteDeletedVariablesInObservations(workbook.getFactors(), workbook.getObservations());
		this.deleteDeletedVariablesInObservations(workbook.getVariates(), workbook.getObservations());
		this.deleteDeletedVariables(workbook.getConditions());
		this.deleteDeletedVariables(workbook.getFactors());
		this.deleteDeletedVariables(workbook.getVariates());
		this.deleteDeletedVariables(workbook.getConstants());
	}

	private void deleteDeletedVariablesInObservations(final List<MeasurementVariable> variableList, final List<MeasurementRow> observations) {
		final List<Integer> deletedList = new ArrayList<Integer>();
		if (variableList != null) {
			for (final MeasurementVariable var : variableList) {
				if (var.getOperation() != null && var.getOperation().equals(Operation.DELETE)) {
					deletedList.add(Integer.valueOf(var.getTermId()));
				}
			}
		}
		if (deletedList != null && observations != null) {
			for (final Integer termId : deletedList) {
				// remove from measurement rows
				int index = 0;
				int varIndex = 0;
				boolean found = false;
				for (final MeasurementRow row : observations) {
					if (index == 0) {
						for (final MeasurementData var : row.getDataList()) {
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

	private void deleteDeletedVariables(final List<MeasurementVariable> variableList) {
		if (variableList != null) {
			final Iterator<MeasurementVariable> variable = variableList.iterator();
			while (variable.hasNext()) {
				final MeasurementVariable var = variable.next();
				if (var.getOperation() != null && var.getOperation().equals(Operation.DELETE)) {
					variable.remove();
				}
			}
		}
	}

	public void resetTrialObservations(final List<MeasurementRow> trialObservations) {
		for (final MeasurementRow row : trialObservations) {
			row.setExperimentId(0);
			row.setLocationId(0);
			row.setStockId(0);
			for (final MeasurementData data : row.getDataList()) {
				data.setPhenotypeId(null);
			}
		}
	}

	public void saveOrUpdateTrialObservations(final int trialDatasetId, final Workbook workbook,
			final VariableTypeList trialVariableTypeList, final List<Integer> locationIds,
			final Map<Integer, VariableList> trialVariatesMap, final int studyLocationId, final int totalRows,
			final boolean isDeleteObservations, final String programUUID) {
		if (totalRows == workbook.getTrialObservations().size() && totalRows > 0
				&& !isDeleteObservations) {
			this.saveTrialObservations(workbook,programUUID);
		} else {
			if (locationIds != null && !locationIds.isEmpty()) {// multi-location
				for (final Integer locationId : locationIds) {
					this.createTrialExperiment(trialDatasetId, locationId, trialVariatesMap.get(locationId));
				}
			} else {
				this.createTrialExperiment(trialDatasetId, studyLocationId, trialVariatesMap.get(studyLocationId));
			}
		}
	}

	public void saveTrialObservations(final Workbook workbook, final String programUUID) {
		if (!workbook.getTrialObservations().isEmpty()) {
			for (final MeasurementRow trialObservation : workbook.getTrialObservations()) {
				this.getGeolocationSaver().updateGeolocationInformation(trialObservation, workbook.isNursery(), programUUID);
			}
		}
	}

	public int createLocationAndSetToObservations(final Workbook workbook, final List<MeasurementVariable> trialMV,
			final VariableTypeList trialVariables, final Map<Integer, VariableList> trialVariatesMap,
			final boolean isDeleteTrialObservations, final String programUUID) {

		final TimerWatch watch = new TimerWatch("transform trial environment");
		if (workbook.getTrialObservations().size() == 1) {
			final MeasurementRow trialObs = workbook.getTrialObservations().get(0);
			for (final MeasurementVariable mv : trialMV) {
				for (final MeasurementData mvrow : trialObs.getDataList()) {
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
			geolocation = this.createDefaultGeolocationVariableList(programUUID);
		}

		watch.restart("save geolocation");
		final Geolocation g =
				this.getGeolocationSaver().saveGeolocationOrRetrieveIfExisting(workbook.getStudyDetails().getStudyName(), geolocation,
						null, workbook.isNursery(), isDeleteTrialObservations, programUUID);
		studyLocationId = g.getLocationId();
		if (g.getVariates() != null && !g.getVariates().isEmpty()) {
			final VariableList trialVariates = new VariableList();
			trialVariates.addAll(g.getVariates());
			trialVariatesMap.put(studyLocationId, trialVariates);
		}

		watch.restart("set to observations(total)");
		if (!workbook.getTrialObservations().isEmpty()) {
			for (final MeasurementRow row : workbook.getTrialObservations()) {
				row.setLocationId(studyLocationId);
			}
		}
		if (workbook.getObservations() != null) {
			for (final MeasurementRow row : workbook.getObservations()) {
				row.setLocationId(studyLocationId);
			}
		}
		watch.stop();

		return studyLocationId;
	}

	public int createLocationsAndSetToObservations(final List<Integer> locationIds, final Workbook workbook,
			final VariableTypeList trialFactors, final List<String> trialHeaders, final Map<Integer, VariableList> trialVariatesMap,
			final boolean isDeleteTrialObservations, final String programUUID) {

		List<MeasurementRow> observations = null;
		Long geolocationId = null;
		boolean hasTrialObservations = false;
		if (!workbook.getTrialObservations().isEmpty()) {
			observations = workbook.getTrialObservations();
			hasTrialObservations = true;
			if (workbook.isNursery()) {
				geolocationId = observations.get(0).getLocationId();
			}
		} else {
			observations = workbook.getObservations();
		}
		final Map<String, Long> locationMap = new HashMap<String, Long>();
		if (observations != null) {
			for (final MeasurementRow row : observations) {
				geolocationId = row.getLocationId();
				if (geolocationId == null || geolocationId == 0) {
					// if geolocationId does not exist, create the geolocation and set to row.locationId
					final TimerWatch watch = new TimerWatch("transformTrialEnvironment in createLocationsAndSetToObservations");
					final VariableList geolocation =
							this.getVariableListTransformer().transformTrialEnvironment(row, trialFactors, trialHeaders);
					if (geolocation != null && !geolocation.isEmpty()) {
						final String trialInstanceNumber = this.getTrialInstanceNumber(geolocation);
						if (WorkbookSaver.LOG.isDebugEnabled()) {
							WorkbookSaver.LOG.debug("trialInstanceNumber = " + trialInstanceNumber);
						}
						if (!locationMap.containsKey(trialInstanceNumber)) {
							// if new location (unique by trial instance number)
							watch.restart("save geolocation");
							final Geolocation g =
									this.getGeolocationSaver().saveGeolocationOrRetrieveIfExisting(
											workbook.getStudyDetails().getStudyName(), geolocation, row, workbook.isNursery(),
											isDeleteTrialObservations, programUUID);
							geolocationId = g.getLocationId().longValue();
							locationIds.add(geolocationId.intValue());
							if (g.getVariates() != null && !g.getVariates().isEmpty()) {
								final VariableList trialVariates = new VariableList();
								trialVariates.addAll(g.getVariates());
								trialVariatesMap.put(geolocationId.intValue(), trialVariates);
							}
							locationMap.put(trialInstanceNumber, geolocationId);
						} else {
							geolocationId = locationMap.get(trialInstanceNumber);
						}
						row.setLocationId(geolocationId);
					}
				}
			}

			if (hasTrialObservations && workbook.getObservations() != null) {
				for (final MeasurementRow row : workbook.getObservations()) {
					if(row.getLocationId() > 0) {
						continue;
					}
					final String trialInstance = this.getTrialInstanceNumber(row);
					if (trialInstance != null) {
						row.setLocationId(locationMap.get(trialInstance));
					} else if (geolocationId != null && geolocationId != 0) {
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

	private String getTrialInstanceNumber(final MeasurementRow row) {
		for (final MeasurementData data : row.getDataList()) {
			if (data.getMeasurementVariable().getTermId() == TermId.TRIAL_INSTANCE_FACTOR.getId()) {
				return data.getValue();
			}
		}
		return null;
	}

	private MeasurementVariable getTrialInstanceFactor(final List<MeasurementVariable> mvars) {
		for (final MeasurementVariable mvar : mvars) {
			if (mvar.getTermId() == TermId.TRIAL_INSTANCE_FACTOR.getId()) {
				return mvar;
			}
		}
		return null;
	}

	private String getStockFactor(final VariableList stockVariables) {
		if(stockVariables!=null && stockVariables.getVariables()!=null) {
			for (final Variable variable : stockVariables.getVariables()) {
				if(TermId.ENTRY_NO.getId() == variable.getVariableType().getStandardVariable().getId()) {
					return variable.getValue();
				}
			}
		}
		return null;
	}

	private String getTrialInstanceNumber(final VariableList trialVariables) {
		if(trialVariables!=null && trialVariables.getVariables()!=null) {
			for (final Variable variable : trialVariables.getVariables()) {
				if(TermId.TRIAL_INSTANCE_FACTOR.getId() == variable.getVariableType().getStandardVariable().getId()) {
					return variable.getValue();
				}
			}
		}
		return null;

	}

	private String generateTrialDatasetName(final String studyName, final StudyType studyType) {
		return studyName + "-ENVIRONMENT";
	}

	private String generateMeasurementEffectDatasetName(final String studyName) {
		return studyName + "-PLOTDATA";
	}

	private String generateMeansDatasetName(final String studyName) {
		return studyName + "-MEANS";
	}

	private ExperimentValues createTrialExperimentValues(final Integer locationId, final VariableList variates) {
		final ExperimentValues value = new ExperimentValues();
		value.setLocationId(locationId);
		value.setVariableList(variates);
		return value;
	}

	private int createStudyIfNecessary(final Workbook workbook, final int studyLocationId, final boolean saveStudyExperiment,
			final String programUUID)
			throws Exception {
		final TimerWatch watch = new TimerWatch("find study");

		Integer studyId = null;
		if (workbook.getStudyDetails() != null) {
			studyId = this.getStudyId(workbook.getStudyDetails().getStudyName(), programUUID);
		}

		if (studyId == null) {
			watch.restart("transform variables for study");
			final List<MeasurementVariable> studyMV = workbook.getStudyVariables();
			final VariableTypeList studyVariables =
					this.getVariableTypeListTransformer().transform(workbook.getStudyConditions(), programUUID);
			studyVariables.addAll(this.getVariableTypeListTransformer().transform(workbook.getStudyConstants(), studyVariables.size() + 1,programUUID));

			if (workbook.isNursery() && this.getTrialInstanceFactor(workbook.getTrialVariables()) == null) {
				studyVariables.add(this.createOccVariableType(studyVariables.size() + 1,programUUID));
			}

			final StudyValues studyValues =
					this.getStudyValuesTransformer().transform(null, studyLocationId, workbook.getStudyDetails(), studyMV, studyVariables);

			watch.restart("save study");
			final DmsProject study =
					this.getStudySaver().saveStudy((int) workbook.getStudyDetails().getParentFolderId(), studyVariables, studyValues,
							saveStudyExperiment, programUUID);
			studyId = study.getProjectId();
		}
		watch.stop();

		return studyId;
	}

	private int createTrialDatasetIfNecessary(final Workbook workbook, final int studyId, final List<MeasurementVariable> trialMV,
			final VariableTypeList trialVariables, final String programUUID) {
		final TimerWatch watch = new TimerWatch("find trial dataset");
		String trialName = workbook.getStudyDetails().getTrialDatasetName();
		Integer trialDatasetId = null;
		if (trialName == null || "".equals(trialName)) {
			final List<DatasetReference> datasetRefList = this.getStudyDataManager().getDatasetReferences(studyId);
			if (datasetRefList != null) {
				for (final DatasetReference datasetRef : datasetRefList) {
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
			final DatasetValues trialValues =
					this.getDatasetValuesTransformer().transform(trialName, trialName, DataSetType.SUMMARY_DATA, trialMV, trialVariables);

			if (workbook.isNursery() && (trialMV == null || trialMV.isEmpty() || this.getTrialInstanceFactor(trialMV) == null)) {
				trialVariables.add(this.createOccVariableType(trialVariables.size() + 1,programUUID));
			}

			watch.restart("save trial dataset");
			final DmsProject trial = this.getDatasetProjectSaver().addDataSet(studyId, trialVariables, trialValues, programUUID);
			trialDatasetId = trial.getProjectId();
		} else {
			if (workbook.isNursery() && (trialMV == null || trialMV.isEmpty() || this.getTrialInstanceFactor(trialMV) == null)) {
				trialVariables.add(this.createOccVariableType(trialVariables.size() + 1,programUUID));
			}
		}

		watch.stop();
		return trialDatasetId;
	}

	private void createTrialExperiment(final int trialProjectId, final int locationId, final VariableList trialVariates) {
		final TimerWatch watch = new TimerWatch("save trial experiments");
		final ExperimentValues trialDatasetValues = this.createTrialExperimentValues(locationId, trialVariates);

		List<Integer> locationIds = new ArrayList<>();
		locationIds.add(locationId);
		if (!this.getExperimentDao().checkIfAnyLocationIDsExistInExperiments(trialProjectId, locationIds)) {
			this.getExperimentModelSaver().addExperiment(trialProjectId, ExperimentType.TRIAL_ENVIRONMENT, trialDatasetValues);
		}

		watch.stop();
	}

	private int createMeasurementEffectDatasetIfNecessary(final Workbook workbook, final int studyId,
			final List<MeasurementVariable> effectMV, final VariableTypeList effectVariables, final VariableTypeList trialVariables,
			final String programUUID) {
		final TimerWatch watch = new TimerWatch("find measurement effect dataset");
		String datasetName = workbook.getStudyDetails().getMeasurementDatasetName();
		Integer datasetId = null;

		if (datasetName == null || "".equals(datasetName)) {
			final List<DatasetReference> datasetRefList = this.getStudyDataManager().getDatasetReferences(studyId);
			if (datasetRefList != null) {
				for (final DatasetReference datasetRef : datasetRefList) {
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
			final DatasetValues datasetValues =
					this.getDatasetValuesTransformer()
					.transform(datasetName, datasetName, DataSetType.PLOT_DATA, effectMV, effectVariables);

			watch.restart("save measurement effect dataset");
			// fix for GCP-6436 start
			final VariableTypeList datasetVariables = this.propagateTrialFactorsIfNecessary(effectVariables, trialVariables);
			// no need to add occ as it is already added in trialVariables
			// fix for GCP-6436 end
			final DmsProject dataset = this.getDatasetProjectSaver().addDataSet(studyId, datasetVariables, datasetValues, programUUID);
			datasetId = dataset.getProjectId();
		}

		watch.stop();
		return datasetId;
	}

	public void createStocksIfNecessary(final int datasetId, final Workbook workbook, final VariableTypeList effectVariables,
			final List<String> trialHeaders) {
		final Map<String, Integer> stockMap = this.getStockModelBuilder().getStockMapForDataset(datasetId);

		List<Integer> variableIndexesList = new ArrayList<Integer>();
		// we get the indexes so that in the next rows we dont need to compare anymore per row
		if (workbook.getObservations() != null && !workbook.getObservations().isEmpty()) {
			final MeasurementRow row = workbook.getObservations().get(0);
			variableIndexesList = this.getVariableListTransformer().transformStockIndexes(row, effectVariables, trialHeaders);
		}

		if (workbook.getObservations() != null) {
			final Session activeSession = this.getActiveSession();
			final FlushMode existingFlushMode = activeSession.getFlushMode();
			activeSession.setFlushMode(FlushMode.MANUAL);
			try {
				for (final MeasurementRow row : workbook.getObservations()) {

				final VariableList stock =
						this.getVariableListTransformer().transformStockOptimize(variableIndexesList, row, effectVariables, trialHeaders);
				final String stockFactor = this.getStockFactor(stock);
				Integer stockId = stockMap.get(stockFactor);

					if (stockId == null) {
						stockId = this.getStockSaver().saveStock(stock);
						stockMap.put(stockFactor, stockId);
					} else {
						this.getStockSaver().saveOrUpdateStock(stock, stockId);
					}
					row.setStockId(stockId);
				}
				activeSession.flush();
			} finally {
				if(existingFlushMode != null) {
					activeSession.setFlushMode(existingFlushMode);
				}
			}
		}

	}

	private void createMeasurementEffectExperiments(final int datasetId, final VariableTypeList effectVariables,
			final List<MeasurementRow> observations, final List<String> trialHeaders) {

		final TimerWatch watch = new TimerWatch("saving stocks and measurement effect data (total)");
		final TimerWatch rowWatch = new TimerWatch("for each row");

		// observation values start at row 2
		int i = 2;

		final ExperimentValuesTransformer experimentValuesTransformer = this.getExperimentValuesTransformer();
		final ExperimentModelSaver experimentModelSaver = this.getExperimentModelSaver();
		Map<Integer, PhenotypeExceptionDto> exceptions = null;
		final Session activeSession = this.getActiveSession();
		final FlushMode existingFlushMode = activeSession.getFlushMode();
		try {
			activeSession.setFlushMode(FlushMode.MANUAL);
			if (observations != null) {
				for (final MeasurementRow row : observations) {
					int experimentId = row.getExperimentId();
					if(experimentId > 0) {
						continue;
					}
					rowWatch.restart("saving row " + i++);
					final ExperimentValues experimentValues = experimentValuesTransformer.transform(row, effectVariables, trialHeaders);
					try {
						experimentModelSaver.addExperiment(datasetId, ExperimentType.PLOT, experimentValues);
					} catch (final PhenotypeException e) {
						WorkbookSaver.LOG.error(e.getMessage(), e);
						if (exceptions == null) {
							exceptions = e.getExceptions();
						} else {
							for (final Integer standardVariableId : e.getExceptions().keySet()) {
								final PhenotypeExceptionDto exception = e.getExceptions().get(standardVariableId);
								if (exceptions.get(standardVariableId) == null) {
									// add exception
									exceptions.put(standardVariableId, exception);
								} else {
									// add invalid values to the existing map of exceptions for each phenotype
									for (final String invalidValue : exception.getInvalidValues()) {
										exceptions.get(standardVariableId).getInvalidValues().add(invalidValue);
									}
								}
							}
						}
					}
				}
			}
			activeSession.flush();
		} finally {
			if (existingFlushMode != null) {
				activeSession.setFlushMode(existingFlushMode);
			}
		}

		rowWatch.stop();
		watch.stop();

		if (exceptions != null) {
			throw new PhenotypeException(exceptions);
		}
	}

	private boolean isTrialFactorInDataset(final VariableTypeList list) {

		for (final DMSVariableType var : list.getVariableTypes()) {
			if (TermId.TRIAL_INSTANCE_FACTOR.getId() == var.getStandardVariable().getId()) {
				return true;
			}
		}
		return false;

	}

	private DMSVariableType createOccVariableType(final int rank, final String programUUID) {
		final VariableInfo info = new VariableInfo();
		info.setLocalName("TRIAL_INSTANCE");
		info.setLocalDescription("TRIAL_INSTANCE");
		info.setStdVariableId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		info.setRank(rank);
		info.setRole(PhenotypicType.TRIAL_ENVIRONMENT);
		return this.getVariableTypeBuilder().create(info,programUUID);
	}

	protected VariableTypeList propagateTrialFactorsIfNecessary(final VariableTypeList effectVariables,
			final VariableTypeList trialVariables) {

		final VariableTypeList newList = new VariableTypeList();

		if (!this.isTrialFactorInDataset(effectVariables) && trialVariables != null) {
			int index = 1;
			for (final DMSVariableType var : trialVariables.getVariableTypes()) {
				if (var.getId() == TermId.TRIAL_INSTANCE_FACTOR.getId()) {
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

	private Integer getStudyId(final String name, final String programUUID) {
		return this.getProjectId(name, programUUID, TermId.IS_STUDY);
	}

	private Integer getDatasetId(final String name, final String generatedName, final String programUUID) {
		Integer id = this.getProjectId(name, programUUID, TermId.BELONGS_TO_STUDY);
		if (id == null && !name.equals(generatedName)) {
			id = this.getProjectId(generatedName, programUUID, TermId.BELONGS_TO_STUDY);
		}
		return id;
	}

	private Integer getProjectId(final String name, final String programUUID, final TermId relationship) {
		return this.getDmsProjectDao().getProjectIdByNameAndProgramUUID(name, programUUID, relationship);
	}

	private Integer getMeansDataset(final Integer studyId) {
		Integer id = null;
		final List<DmsProject> datasets =
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
	public int saveProjectOntology(final Workbook workbook, final String programUUID) throws Exception {

		final Map<String, ?> variableMap = this.saveVariables(workbook,programUUID);
		workbook.setVariableMap(variableMap);

		// unpack maps first level - Maps of Strings, Maps of VariableTypeList , Maps of Lists of MeasurementVariable
		final Map<String, VariableTypeList> variableTypeMap = (Map<String, VariableTypeList>) variableMap.get("variableTypeMap");
		final Map<String, List<MeasurementVariable>> measurementVariableMap =
				(Map<String, List<MeasurementVariable>>) variableMap.get("measurementVariableMap");

		// unpack maps
		final VariableTypeList trialVariables = new VariableTypeList();
		// addAll instead of assigning directly to avoid changing the state of the object
		trialVariables.addAll(variableTypeMap.get("trialVariables"));
		final VariableTypeList effectVariables = new VariableTypeList();
		// addAll instead of assigning directly to avoid changing the state of the object
		effectVariables.addAll(variableTypeMap.get("effectVariables"));
		final List<MeasurementVariable> trialMV = measurementVariableMap.get("trialMV");
		final List<MeasurementVariable> effectMV = measurementVariableMap.get("effectMV");

		// locationId and experiment are not yet needed here
		final int studyId = this.createStudyIfNecessary(workbook, 0, false, programUUID);
		final int trialDatasetId = this.createTrialDatasetIfNecessary(workbook, studyId, trialMV, trialVariables, programUUID);
		int measurementDatasetId = 0;
		int meansDatasetId = 0;
		if (workbook.getImportType() != null && workbook.getImportType().intValue() == DataSetType.MEANS_DATA.getId()) {
			meansDatasetId = this.createMeansDatasetIfNecessary(workbook, studyId, effectMV, effectVariables, trialVariables, programUUID);
		} else {
			measurementDatasetId =
					this.createMeasurementEffectDatasetIfNecessary(workbook, studyId, effectMV, effectVariables, trialVariables,
							programUUID);
		}

		workbook.getStudyDetails().setId(studyId);
		workbook.populateDatasetIds(trialDatasetId, measurementDatasetId, meansDatasetId);

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
	public void saveProjectData(final Workbook workbook, final String programUUID) throws Exception {

		final int studyId = workbook.getStudyDetails().getId();
		final int trialDatasetId = workbook.getTrialDatasetId();
		final int measurementDatasetId = workbook.getMeasurementDatesetId() != null ? workbook.getMeasurementDatesetId() : 0;
		final int meansDatasetId = workbook.getMeansDatasetId() != null ? workbook.getMeansDatasetId() : 0;
		final boolean isMeansDataImport =
				workbook.getImportType() != null && workbook.getImportType().intValue() == DataSetType.MEANS_DATA.getId();

		Map<String, ?> variableMap = workbook.getVariableMap();
		if (variableMap == null || variableMap.isEmpty()) {
			variableMap = this.saveVariables(workbook,programUUID);
		}

		// unpack maps first level - Maps of Strings, Maps of VariableTypeList , Maps of Lists of MeasurementVariable
		final Map<String, List<String>> headerMap = (Map<String, List<String>>) variableMap.get("headerMap");
		final Map<String, VariableTypeList> variableTypeMap = (Map<String, VariableTypeList>) variableMap.get("variableTypeMap");
		final Map<String, List<MeasurementVariable>> measurementVariableMap =
				(Map<String, List<MeasurementVariable>>) variableMap.get("measurementVariableMap");

		// unpack maps
		final List<String> trialHeaders = headerMap.get("trialHeaders");
		final VariableTypeList trialVariableTypeList = variableTypeMap.get("trialVariableTypeList");
		final VariableTypeList trialVariables = variableTypeMap.get("trialVariables");
		final VariableTypeList effectVariables = variableTypeMap.get("effectVariables");
		final List<MeasurementVariable> trialMV = measurementVariableMap.get("trialMV");

		// GCP-8092 Nurseries will always have a unique geolocation, no more concept of shared/common geolocation
		// create locations (entries to nd_geolocation) and associate to observations
		final int studyLocationId/* = DEFAULT_GEOLOCATION_ID */;
		final List<Integer> locationIds = new ArrayList<Integer>();
		final Map<Integer, VariableList> trialVariatesMap = new HashMap<Integer, VariableList>();
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
		final boolean hasExistingStudyExperiment = this.checkIfHasExistingStudyExperiment(studyId);
		final boolean hasExistingTrialExperiments = this.checkIfHasExistingExperiments(locationIds);
		if (!hasExistingStudyExperiment) {
			// 1. study experiment
			final StudyValues values = new StudyValues();
			values.setLocationId(studyLocationId);
			this.getStudySaver().saveStudyExperiment(studyId, values);
		}
		// create trial experiments if not yet existing
		if (!hasExistingTrialExperiments) {
			// 2. trial experiments
			if (trialVariableTypeList != null) {// multi-location
				for (final Integer locationId : locationIds) {
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
			this.createMeasurementEffectExperiments(measurementDatasetId, effectVariables, workbook.getObservations(), trialHeaders);
		}
	}

	private boolean checkIfHasExistingStudyExperiment(final int studyId) {
		final Integer experimentId = this.getExperimentProjectDao().getExperimentIdByProjectId(studyId);
		return experimentId != null;
	}

	private boolean checkIfHasExistingExperiments(final List<Integer> locationIds) {
		final List<Integer> experimentIds = this.getExperimentDao().getExperimentIdsByGeolocationIds(locationIds);
		return experimentIds != null && !experimentIds.isEmpty();
	}

	private VariableList createDefaultGeolocationVariableList(final String programUUID) {
		final VariableList list = new VariableList();

		final DMSVariableType variableType =
				new DMSVariableType(PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().get(0), PhenotypicType.TRIAL_ENVIRONMENT.getLabelList()
						.get(0), this.getStandardVariableBuilder().create(TermId.TRIAL_INSTANCE_FACTOR.getId(),programUUID), 1);
		final Variable variable = new Variable(variableType, "1");
		list.add(variable);

		return list;
	}

	public void saveWorkbookVariables(final Workbook workbook) {
		this.getProjectRelationshipSaver().saveOrUpdateStudyToFolder(workbook.getStudyDetails().getId(),
				Long.valueOf(workbook.getStudyDetails().getParentFolderId()).intValue());
		final DmsProject study = this.getDmsProjectDao().getById(workbook.getStudyDetails().getId());
		Integer trialDatasetId = workbook.getTrialDatasetId(), measurementDatasetId = workbook.getMeasurementDatesetId();
		if (workbook.getTrialDatasetId() == null || workbook.getMeasurementDatesetId() == null) {
			measurementDatasetId = this.getWorkbookBuilder().getMeasurementDataSetId(study.getProjectId(), workbook.getStudyName());
			final List<DmsProject> datasets =
					this.getProjectRelationshipDao().getSubjectsByObjectIdAndTypeId(study.getProjectId(), TermId.BELONGS_TO_STUDY.getId());
			if (datasets != null) {
				for (final DmsProject dataset : datasets) {
					if (!dataset.getProjectId().equals(measurementDatasetId)) {
						trialDatasetId = dataset.getProjectId();
						break;
					}
				}
			}
		}
		final DmsProject trialDataset = this.getDmsProjectDao().getById(trialDatasetId);
		final DmsProject measurementDataset = this.getDmsProjectDao().getById(measurementDatasetId);

		this.getProjectPropertySaver().saveProjectProperties(study, trialDataset, measurementDataset, workbook.getConditions(), false);
		this.getProjectPropertySaver().saveProjectProperties(study, trialDataset, measurementDataset, workbook.getConstants(), true);
		this.getProjectPropertySaver().saveProjectProperties(study, trialDataset, measurementDataset, workbook.getVariates(), false);
		this.getProjectPropertySaver().saveFactors(measurementDataset, workbook.getFactors());
	}

	private int createMeansDatasetIfNecessary(final Workbook workbook, final int studyId, final List<MeasurementVariable> effectMV,
			final VariableTypeList effectVariables, final VariableTypeList trialVariables, final String programUUID) {

		final TimerWatch watch = new TimerWatch("find means dataset");
		Integer datasetId = this.getMeansDataset(studyId);

		if (datasetId == null) {
			watch.restart("transform means dataset");
			final String datasetName = this.generateMeansDatasetName(workbook.getStudyDetails().getStudyName());
			final DatasetValues datasetValues =
					this.getDatasetValuesTransformer().transform(datasetName, datasetName, DataSetType.MEANS_DATA, effectMV,
							effectVariables);

			watch.restart("save means dataset");
			final VariableTypeList datasetVariables = this.getMeansData(effectVariables, trialVariables);
			final DmsProject dataset = this.getDatasetProjectSaver().addDataSet(studyId, datasetVariables, datasetValues, programUUID);
			datasetId = dataset.getProjectId();
		}

		watch.stop();
		return datasetId;
	}

	private VariableTypeList getMeansData(final VariableTypeList effectVariables, final VariableTypeList trialVariables) {

		final VariableTypeList newList = new VariableTypeList();
		int rank = 1;
		for (final DMSVariableType var : trialVariables.getVariableTypes()) {
			var.setRank(rank++);
			newList.add(var);
		}
		for (final DMSVariableType var : effectVariables.getVariableTypes()) {
			var.setRank(rank++);
			newList.add(var);
		}
		return newList;
	}

	private void createMeansExperiments(final int datasetId, final VariableTypeList effectVariables,
			final List<MeasurementRow> observations, final List<String> trialHeaders, final Map<Integer, VariableList> trialVariatesMap) {

		final TimerWatch watch = new TimerWatch("saving means data (total)");
		final TimerWatch rowWatch = new TimerWatch("for each row");

		// observation values start at row 2
		int i = 2;

		final ExperimentValuesTransformer experimentValuesTransformer = this.getExperimentValuesTransformer();
		final ExperimentModelSaver experimentModelSaver = this.getExperimentModelSaver();
		Map<Integer, PhenotypeExceptionDto> exceptions = null;
		if (observations != null) {
			for (final MeasurementRow row : observations) {
				rowWatch.restart("saving row " + i++);
				final ExperimentValues experimentValues = experimentValuesTransformer.transform(row, effectVariables, trialHeaders);
				final VariableList trialVariates = trialVariatesMap.get((int) row.getLocationId());
				if (trialVariates != null) {
					experimentValues.getVariableList().addAll(trialVariates);
				}
				try {
					experimentModelSaver.addExperiment(datasetId, ExperimentType.AVERAGE, experimentValues);
				} catch (final PhenotypeException e) {
					WorkbookSaver.LOG.error(e.getMessage(), e);
					if (exceptions == null) {
						exceptions = e.getExceptions();
					} else {
						for (final Integer standardVariableId : e.getExceptions().keySet()) {
							final PhenotypeExceptionDto exception = e.getExceptions().get(standardVariableId);
							if (exceptions.get(standardVariableId) == null) {
								// add exception
								exceptions.put(standardVariableId, exception);
							} else {
								// add invalid values to the existing map of exceptions for each phenotype
								for (final String invalidValue : exception.getInvalidValues()) {
									exceptions.get(standardVariableId).getInvalidValues().add(invalidValue);
								}
							}
						}
					}
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
