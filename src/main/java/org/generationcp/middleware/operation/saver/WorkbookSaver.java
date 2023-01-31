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

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.dao.LocationDAO;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DatasetValues;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.ExperimentValues;
import org.generationcp.middleware.domain.dms.PhenotypeExceptionDto;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StudyValues;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.PhenotypeException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.operation.builder.WorkbookBuilder;
import org.generationcp.middleware.operation.transformer.etl.ExperimentValuesTransformer;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.PedigreeService;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.generationcp.middleware.util.TimerWatch;
import org.generationcp.middleware.util.Util;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

// Assumptions - can be added to validations
// Mandatory fields: workbook.studyDetails.studyName
// template must not contain exact same combo of property-scale-method


public class WorkbookSaver extends Saver {

	private static final Logger LOG = LoggerFactory.getLogger(WorkbookSaver.class);

	private static final String TRIALHEADERS = "trialHeaders";
	private static final String TRIALVARIABLETYPELIST = "trialVariableTypeList";
	private static final String TRIALVARIABLES = "trialVariables";
	private static final String EFFECTVARIABLE = "effectVariables";
	private static final String TRIALMV = "trialMV";
	private static final String EFFECTMV = "effectMV";
	private static final String HEADERMAP = "headerMap";
	private static final String VARIABLETYPEMAP = "variableTypeMap";
	private static final String MEASUREMENTVARIABLEMAP = "measurementVariableMap";
	public static final String ENVIRONMENT = "-ENVIRONMENT";
	public static final String PLOTDATA = "-PLOTDATA";

	private DaoFactory daoFactory;

	@Resource
	private WorkbookBuilder workbookBuilder;

	@Resource
	private StudyDataManager studyDataManager;

	@Resource
	private PedigreeService pedigreeService;

	@Resource
	private CrossExpansionProperties crossExpansionProperties;

	public WorkbookSaver() {

	}

	public WorkbookSaver(final HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
		this.daoFactory = new DaoFactory(sessionProviderForLocal);
	}

	/**
	 * This method transforms Variable data from a Fieldbook presented as an XLS
	 * style Workbook. - Variables new to the ontology are created and persisted
	 * - Columns and rows are transformed into entities suitables for
	 * persistence
	 * <p>
	 * Note : the result of this process is suitable for Dataset Creation
	 *
	 * @param workbook
	 * @return Map<String>, ?> : a map of 3 sub-maps containing
	 * Strings(headers), VariableTypeLists and Lists of
	 * MeasurementVariables
	 */

	@SuppressWarnings("rawtypes")
	public Map saveVariables(final Workbook workbook, final String programUUID) {
		// make sure to reset all derived variables
		workbook.reset();

		// Create Maps, which we will fill with transformed Workbook Variable
		// Data
		final Map<String, List<String>> headerMap = new HashMap<>();
		final Map<String, VariableTypeList> variableTypeMap = new HashMap<>();
		final Map<String, List<MeasurementVariable>> measurementVariableMap = new HashMap<>();

		// GCP-6091 start
		final List<MeasurementVariable> trialMV = workbook.getTrialVariables();
		final List<String> trialHeaders = workbook.getTrialHeaders();
		final VariableTypeList trialVariables = this.getVariableTypeListTransformer().transform(workbook.getTrialConditions(), programUUID);
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
		effectVariables
			.addAll(this.getVariableTypeListTransformer().transform(workbook.getVariates(), effectVariables.size() + 1, programUUID));
		effectVariables
			.addAll(this.getVariableTypeListTransformer().transform(workbook.getEntryDetails(), effectVariables.size() + 1, programUUID));

		// -- headers
		headerMap.put(WorkbookSaver.TRIALHEADERS, trialHeaders);
		// -- variableTypeLists
		variableTypeMap.put(WorkbookSaver.TRIALVARIABLETYPELIST, trialVariableTypeList);
		variableTypeMap.put(WorkbookSaver.TRIALVARIABLES, trialVariables);
		variableTypeMap.put(WorkbookSaver.EFFECTVARIABLE, effectVariables);
		// -- measurementVariables
		measurementVariableMap.put(WorkbookSaver.TRIALMV, trialMV);

		final List<MeasurementVariable> effectMV = workbook.getMeasurementDatasetVariables();
		measurementVariableMap.put(WorkbookSaver.EFFECTMV, effectMV);

		// load 3 maps into a super Map
		final Map<String, Map<String, ?>> variableMap = new HashMap<>();
		variableMap.put(WorkbookSaver.HEADERMAP, headerMap);
		variableMap.put(WorkbookSaver.VARIABLETYPEMAP, variableTypeMap);
		variableMap.put(WorkbookSaver.MEASUREMENTVARIABLEMAP, measurementVariableMap);
		return variableMap;
	}

	/**
	 * Dataset creation and persistence for Fieldbook upload
	 * <p>
	 * NOTE IMPORTANT : This step will fail if the Fieldbook has not had new
	 * Variables processed and new ontology terms created.
	 *
	 * @param workbook
	 * @param variableMap : a map of 3 sub-maps containing Strings(headers),
	 *                    VariableTypeLists and Lists of MeasurementVariables
	 * @return int (success/fail)
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public int saveDataset(
		final Workbook workbook, final Map<String, ?> variableMap, final boolean retainValues,
		final boolean isDeleteObservations, final String programUUID, final CropType crop) throws Exception {

		// unpack maps first level - Maps of Strings, Maps of VariableTypeList ,
		// Maps of Lists of MeasurementVariable
		final Map<String, List<String>> headerMap = (Map<String, List<String>>) variableMap.get(WorkbookSaver.HEADERMAP);
		final Map<String, VariableTypeList> variableTypeMap =
			(Map<String, VariableTypeList>) variableMap.get(WorkbookSaver.VARIABLETYPEMAP);
		final Map<String, List<MeasurementVariable>> measurementVariableMap =
			(Map<String, List<MeasurementVariable>>) variableMap.get(WorkbookSaver.MEASUREMENTVARIABLEMAP);

		// unpack maps
		// Strings
		final List<String> trialHeaders = headerMap.get(WorkbookSaver.TRIALHEADERS);
		// VariableTypeLists
		final VariableTypeList trialVariableTypeList = variableTypeMap.get(WorkbookSaver.TRIALVARIABLETYPELIST);
		final VariableTypeList trialVariables = variableTypeMap.get(WorkbookSaver.TRIALVARIABLES);
		final VariableTypeList effectVariables = variableTypeMap.get(WorkbookSaver.EFFECTVARIABLE);
		// Lists of measurementVariables
		final List<MeasurementVariable> trialMV = measurementVariableMap.get(WorkbookSaver.TRIALMV);
		final List<MeasurementVariable> effectMV = measurementVariableMap.get(WorkbookSaver.EFFECTMV);

		// TODO : Review code and see whether variable validation and possible
		// dataset creation abort is a good idea (rebecca)

		// GCP-6091 start
		final int studyLocationId;
		final List<Integer> locationIds = new ArrayList<>();
		final Map<Integer, VariableList> trialVariatesMap = new HashMap<>();

		// get the trial and measurement dataset id to use in deletion of
		// experiments
		Integer environmentDatasetId = workbook.getTrialDatasetId();
		Integer plotDatasetId = workbook.getMeasurementDatesetId();
		int savedEnvironmentsCount = 0;
		boolean isDeleteTrialObservations = false;
		if (environmentDatasetId == null && workbook.getStudyDetails().getId() != null) {
			environmentDatasetId = this.workbookBuilder.getTrialDataSetId(workbook.getStudyDetails().getId());
		}
		if (plotDatasetId == null && workbook.getStudyDetails().getId() != null) {
			plotDatasetId = this.workbookBuilder.getMeasurementDataSetId(workbook.getStudyDetails().getId());
		}

		if (environmentDatasetId != null) {
			savedEnvironmentsCount = (int) this.studyDataManager.countExperiments(environmentDatasetId);
		}

		if ((savedEnvironmentsCount != workbook.getTrialObservations().size() && savedEnvironmentsCount > 0 || isDeleteObservations)
			&& environmentDatasetId != null) {
			isDeleteTrialObservations = true;
			// delete measurement data
			this.daoFactory.getExperimentDao().deleteExperimentsForDataset(plotDatasetId);
			// reset trial observation details such as experimentid, stockid and
			// geolocationid
			this.resetTrialObservations(workbook.getTrialObservations());
		}

		studyLocationId =
			this.createLocationIfNecessary(trialVariableTypeList, isDeleteObservations, locationIds, workbook, trialVariables, trialMV,
				trialHeaders, trialVariatesMap, isDeleteTrialObservations, programUUID);

		// GCP-6091 end
		if (isDeleteTrialObservations) {

			final ExperimentModel studyExperiment =
				this.daoFactory.getExperimentDao().getExperimentsByProjectIds(Arrays.asList(workbook.getStudyDetails().getId())).get(0);
			studyExperiment.setGeoLocation(this.daoFactory.getGeolocationDao().getById(studyLocationId));
			this.daoFactory.getExperimentDao().saveOrUpdate(studyExperiment);

			// delete trial observations
			this.daoFactory.getExperimentDao().deleteTrialExperimentsOfStudy(environmentDatasetId);
		}

		final int studyId;
		if (!(workbook.getStudyDetails() != null && workbook.getStudyDetails().getId() != null)) {
			studyId = this.createStudyIfNecessary(workbook, studyLocationId, true, programUUID, crop);
		} else {
			studyId = workbook.getStudyDetails().getId();
		}
		environmentDatasetId = this.createTrialDatasetIfNecessary(workbook, studyId, trialMV, trialVariables, programUUID);

		this.saveOrUpdateTrialObservations(crop, environmentDatasetId, workbook, locationIds, trialVariatesMap, studyLocationId,
			savedEnvironmentsCount,
			isDeleteObservations, programUUID, workbook.getUserId());

		plotDatasetId =
			this.createPlotDatasetIfNecessary(workbook, studyId, effectMV, effectVariables, trialVariables, programUUID);
		this.createOrResolveStudyGermplasm(workbook, studyId, effectVariables, trialHeaders);

		if (!retainValues) {
			// clean up some variable references to save memory space before
			// saving the measurement effects
			workbook.reset();
			workbook.setConditions(null);
			workbook.setConstants(null);
			workbook.setFactors(null);
			workbook.setStudyDetails(null);
			workbook.setVariates(null);
			workbook.setEntryDetails(null);
		} else {
			workbook.getStudyDetails().setId(studyId);
			workbook.setTrialDatasetId(environmentDatasetId);
			workbook.setMeasurementDatesetId(plotDatasetId);
		}

		this.createMeasurementEffectExperiments(crop, plotDatasetId, effectVariables, workbook.getObservations(), trialHeaders,
			workbook.getUserId());

		return studyId;
	}

	public void savePlotDataset(final Workbook workbook, final Map<String, ?> variableMap, final String programUUID, final CropType crop) {

		// unpack maps first level - Maps of Strings, Maps of VariableTypeList ,
		// Maps of Lists of MeasurementVariable
		final Map<String, List<String>> headerMap = (Map<String, List<String>>) variableMap.get(WorkbookSaver.HEADERMAP);
		final Map<String, VariableTypeList> variableTypeMap =
			(Map<String, VariableTypeList>) variableMap.get(WorkbookSaver.VARIABLETYPEMAP);
		final Map<String, List<MeasurementVariable>> measurementVariableMap =
			(Map<String, List<MeasurementVariable>>) variableMap.get(WorkbookSaver.MEASUREMENTVARIABLEMAP);

		final List<MeasurementVariable> trialMV = measurementVariableMap.get(WorkbookSaver.TRIALMV);

		// VariableTypeLists
		final VariableTypeList trialVariableTypeList = variableTypeMap.get(WorkbookSaver.TRIALVARIABLETYPELIST);
		final VariableTypeList trialVariables = variableTypeMap.get(WorkbookSaver.TRIALVARIABLES);
		final List<String> trialHeaders = headerMap.get(WorkbookSaver.TRIALHEADERS);
		final VariableTypeList effectVariables = variableTypeMap.get(WorkbookSaver.EFFECTVARIABLE);

		final int studyLocationId;
		final List<Integer> locationIds = new ArrayList<>();
		final Map<Integer, VariableList> trialVariatesMap = new HashMap<>();

		final int environmentDatasetId = this.workbookBuilder.getTrialDataSetId(workbook.getStudyDetails().getId());
		final int plotDatasetId = this.workbookBuilder.getMeasurementDataSetId(workbook.getStudyDetails().getId());
		final int studyId = workbook.getStudyDetails().getId();

		final int savedEnvironmentsCount = (int) this.studyDataManager.countExperiments(environmentDatasetId);
		this.daoFactory.getExperimentDao().deleteExperimentsForDataset(plotDatasetId);

		this.resetTrialObservations(workbook.getTrialObservations());

		studyLocationId = this.createLocationIfNecessary(trialVariableTypeList, true, locationIds, workbook, trialVariables, trialMV,
			trialHeaders, trialVariatesMap, true, programUUID);

		final ExperimentModel studyExperiment =
			this.daoFactory.getExperimentDao().getExperimentsByProjectIds(Arrays.asList(studyId)).get(0);
		studyExperiment.setGeoLocation(this.daoFactory.getGeolocationDao().getById(studyLocationId));
		this.daoFactory.getExperimentDao().saveOrUpdate(studyExperiment);

		// delete trial observations
		this.daoFactory.getExperimentDao().deleteTrialExperimentsOfStudy(environmentDatasetId);

		this.saveOrUpdateTrialObservations(crop, environmentDatasetId, workbook, locationIds, trialVariatesMap, studyLocationId,
			savedEnvironmentsCount, true, programUUID, workbook.getUserId());

		// set study germplasm to observation rows
		this.setDatasetStocks(workbook, this.daoFactory.getStockDao().getStocksForStudy(studyId));
		this.createMeasurementEffectExperiments(crop, plotDatasetId, effectVariables, workbook.getObservations(), trialHeaders,
			workbook.getUserId());

	}

	private int createLocationIfNecessary(final VariableTypeList trialVariableTypeList, final boolean isDeleteObservations,
		final List<Integer> locationIds, final Workbook workbook, final VariableTypeList trialVariables,
		final List<MeasurementVariable> trialMV, final List<String> trialHeaders, final Map<Integer, VariableList> trialVariatesMap,
		final boolean isDeleteTrialObservations, final String programUUID) {

		final int studyLocationId;

		if (trialVariableTypeList != null && !isDeleteObservations) {
			// multi-location for data loader
			studyLocationId =
				this.createLocationsAndSetToObservations(locationIds, workbook, trialVariables, trialHeaders, trialVariatesMap, false,
					programUUID);
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

		return studyLocationId;
	}

	public void removeDeletedVariablesAndObservations(final Workbook workbook) {
		for (final MeasurementRow measurementRow : workbook.getTrialObservations()) {
			this.removeDeletedVariablesInObservations(workbook.getConstants(), workbook.getTrialObservations());
			this.removeDeletedVariablesInObservations(measurementRow.getMeasurementVariables(), workbook.getTrialObservations());
		}
		this.removeDeletedVariablesInObservations(workbook.getFactors(), workbook.getObservations());
		this.removeDeletedVariablesInObservations(workbook.getVariates(), workbook.getObservations());
		this.removeDeletedVariables(workbook.getConditions());
		this.removeDeletedVariables(workbook.getFactors());
		this.removeDeletedVariables(workbook.getVariates());
		this.removeDeletedVariables(workbook.getConstants());

	}

	private void removeDeletedVariablesInObservations(
		final List<MeasurementVariable> variableList,
		final List<MeasurementRow> observations) {
		final List<Integer> deletedList = new ArrayList<>();
		if (variableList != null) {
			for (final MeasurementVariable var : variableList) {
				if (var.getOperation() != null && var.getOperation().equals(Operation.DELETE)) {
					deletedList.add(Integer.valueOf(var.getTermId()));
				}
			}
		}
		if (observations != null) {
			for (final Integer deletedTermId : deletedList) {
				// remove from measurement rows
				int index = 0;
				int varIndex = 0;
				boolean found = false;
				for (final MeasurementRow row : observations) {
					if (index == 0) {
						for (final MeasurementData mData : row.getDataList()) {
							if (mData.getMeasurementVariable().getTermId() == deletedTermId.intValue()) {
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

	private void removeDeletedVariables(final List<MeasurementVariable> variableList) {
		if (variableList != null) {
			final Iterator<MeasurementVariable> itrMVariable = variableList.iterator();
			while (itrMVariable.hasNext()) {
				final MeasurementVariable mVariable = itrMVariable.next();
				if (mVariable.getOperation() != null && mVariable.getOperation().equals(Operation.DELETE)) {
					itrMVariable.remove();
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
				data.setMeasurementDataId(null);
			}
		}
	}

	public void saveOrUpdateTrialObservations(
		final CropType crop, final int trialDatasetId, final Workbook workbook, final List<Integer> locationIds,
		final Map<Integer, VariableList> trialVariatesMap, final int studyLocationId, final int totalRows,
		final boolean isDeleteObservations, final String programUUID, final Integer userId) {
		if (totalRows == workbook.getTrialObservations().size() && totalRows > 0 && !isDeleteObservations) {
			this.saveTrialObservations(workbook, programUUID);
		} else {
			if (locationIds != null && !locationIds.isEmpty()) {// multi-location
				for (final Integer locationId : locationIds) {
					this.setVariableListValues(trialVariatesMap.get(locationId), workbook.getConstants());
					this.createTrialExperiment(crop, trialDatasetId, locationId, trialVariatesMap.get(locationId), userId);
				}
			} else {
				this.createTrialExperiment(crop, trialDatasetId, studyLocationId, trialVariatesMap.get(studyLocationId), userId);
			}
		}
	}

	public void saveTrialObservations(final Workbook workbook, final String programUUID) {
		if (!workbook.getTrialObservations().isEmpty()) {
			for (final MeasurementRow trialObservation : workbook.getTrialObservations()) {
				this.getGeolocationSaver().updateGeolocationInformation(trialObservation, programUUID, workbook.getUserId());
			}
		}
	}

	public int createLocationAndSetToObservations(
		final Workbook workbook, final List<MeasurementVariable> trialMV,
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
		this.setVariableListValues(geolocation, trialMV);
		final Integer studyLocationId;

		// GCP-8092 Nurseries will always have a unique geolocation, no more
		// concept of shared/common geolocation
		if (geolocation == null || geolocation.isEmpty()) {
			geolocation = this.createDefaultGeolocationVariableList(programUUID);
		}

		watch.restart("save geolocation");

		this.assignLocationVariableWithUnspecifiedLocationIfEmptyOrInvalid(geolocation, this.daoFactory.getLocationDAO());
		this.assignExptDesignAsExternallyGeneratedDesignIfEmpty(geolocation);

		final Geolocation g = this.getGeolocationSaver()
			.saveGeolocationOrRetrieveIfExisting(workbook.getStudyDetails().getStudyName(), geolocation, null,
				isDeleteTrialObservations, programUUID);
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

	private int createLocationsAndSetToObservations(
		final List<Integer> locationIds, final Workbook workbook,
		final VariableTypeList trialFactors, final List<String> trialHeaders, final Map<Integer, VariableList> trialVariatesMap,
		final boolean isDeleteTrialObservations, final String programUUID) {

		final List<MeasurementRow> observations;
		Long geolocationId = null;
		boolean hasTrialObservations = false;
		if (!workbook.getTrialObservations().isEmpty()) {
			observations = workbook.getTrialObservations();
			hasTrialObservations = true;
		} else {
			observations = workbook.getObservations();
		}
		final Map<String, Long> locationMap = new HashMap<>();
		if (observations != null) {
			for (final MeasurementRow row : observations) {
				geolocationId = row.getLocationId();
				if (geolocationId == 0) {
					// if geolocationId does not exist, create the geolocation
					// and set to row.locationId
					final TimerWatch watch = new TimerWatch("transformTrialEnvironment in createLocationsAndSetToObservations");
					final VariableList geolocation =
						this.getVariableListTransformer().transformTrialEnvironment(row, trialFactors, trialHeaders);

					this.setVariableListValues(geolocation, workbook.getConditions());
					if (geolocation != null && !geolocation.isEmpty()) {

						final String trialInstanceNumber = this.getTrialInstanceNumber(geolocation);
						if (WorkbookSaver.LOG.isDebugEnabled()) {
							WorkbookSaver.LOG.debug("trialInstanceNumber = " + trialInstanceNumber);
						}
						if (!locationMap.containsKey(trialInstanceNumber)) {

							// if new location (unique by trial instance number)
							watch.restart("save geolocation");

							this.assignLocationVariableWithUnspecifiedLocationIfEmptyOrInvalid(
								geolocation, this.daoFactory.getLocationDAO());
							this.assignExptDesignAsExternallyGeneratedDesignIfEmpty(geolocation);

							final Geolocation g = this.getGeolocationSaver()
								.saveGeolocationOrRetrieveIfExisting(workbook.getStudyDetails().getStudyName(), geolocation, row,
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

	protected void assignExptDesignAsExternallyGeneratedDesignIfEmpty(final VariableList variableList) {
		final Variable exptDesignVariable = variableList.findById(TermId.EXPERIMENT_DESIGN_FACTOR);

		if (exptDesignVariable != null) {
			if (StringUtils.isEmpty(exptDesignVariable.getValue())) {
				exptDesignVariable.setValue(String.valueOf(TermId.EXTERNALLY_GENERATED.getId()));
			}
		}
	}

	protected void assignLocationVariableWithUnspecifiedLocationIfEmptyOrInvalid(
		final VariableList variableList, final LocationDAO locationDAO) {
		final Variable locationIdVariable = variableList.findById(TermId.LOCATION_ID);

		if (locationIdVariable != null) {
			final List<Integer> locationId = new ArrayList<>();
			boolean locationIdExists = false;

			if (!StringUtils.isEmpty(locationIdVariable.getValue())) {
				locationId.add(Integer.valueOf(locationIdVariable.getValue()));
				locationIdExists = locationDAO.getByIds(locationId).size() > 0;
			}
			if (StringUtils.isEmpty(locationIdVariable.getValue()) || !locationIdExists) {
				String unspecifiedLocationLocId = "";
				final List<Location> locations = locationDAO.getByName(Location.UNSPECIFIED_LOCATION, Operation.EQUAL);
				if (!locations.isEmpty()) {
					unspecifiedLocationLocId = String.valueOf(locations.get(0).getLocid());
				}
				locationIdVariable.setValue(unspecifiedLocationLocId);
			}
		}

	}

	void setVariableListValues(final VariableList variableList, final List<MeasurementVariable> measurementVariables) {
		if (measurementVariables != null) {
			for (final MeasurementVariable mvar : measurementVariables) {
				final Variable variable = variableList.findById(mvar.getTermId());
				if (variable != null && variable.getValue() == null) {
					variable.setValue(mvar.getValue());
				}
				this.setCategoricalVariableValues(mvar, variable);
			}
		}
	}

	// Sets the value of categorical variables to the key of the possible value
	// instead of its name
	void setCategoricalVariableValues(final MeasurementVariable mvar, final Variable variable) {
		if (variable != null && mvar.getPossibleValues() != null && !mvar.getPossibleValues().isEmpty()) {
			for (final ValueReference possibleValue : mvar.getPossibleValues()) {
				if (possibleValue.getName().equalsIgnoreCase(mvar.getValue())) {
					variable.setValue(possibleValue.getKey());
					break;
				}
			}
		}
	}

	private String getTrialInstanceNumber(final MeasurementRow row) {
		for (final MeasurementData data : row.getDataList()) {
			if (data.getMeasurementVariable().getTermId() == TermId.TRIAL_INSTANCE_FACTOR.getId()) {
				return data.getValue();
			}
		}
		return null;
	}

	private String getEntryNumber(final VariableList stockVariables) {
		if (stockVariables != null && stockVariables.getVariables() != null) {
			for (final Variable variable : stockVariables.getVariables()) {
				if (TermId.ENTRY_NO.getId() == variable.getVariableType().getStandardVariable().getId()) {
					return variable.getValue();
				}
			}
		}
		return null;
	}

	private String getTrialInstanceNumber(final VariableList trialVariables) {
		if (trialVariables != null && trialVariables.getVariables() != null) {
			for (final Variable variable : trialVariables.getVariables()) {
				if (TermId.TRIAL_INSTANCE_FACTOR.getId() == variable.getVariableType().getStandardVariable().getId()) {
					return variable.getValue();
				}
			}
		}
		return null;

	}

	private String generateTrialDatasetName(final String studyName) {
		return studyName + ENVIRONMENT;
	}

	private String generatePlotDatasetName(final String studyName) {
		return studyName + PLOTDATA;
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

	private int createStudyIfNecessary(
		final Workbook workbook, final int studyLocationId, final boolean saveStudyExperiment,
		final String programUUID, final CropType crop) throws Exception {
		final TimerWatch watch = new TimerWatch("find study");

		Integer studyId = null;
		if (workbook.getStudyDetails() != null) {
			studyId =
				this.daoFactory.getDmsProjectDAO().getProjectIdByNameAndProgramUUID(workbook.getStudyDetails().getStudyName(), programUUID);
		}

		if (studyId == null) {
			watch.restart("transform variables for study");
			final List<MeasurementVariable> studyMV = workbook.getStudyVariables();
			final VariableTypeList studyVariables =
				this.getVariableTypeListTransformer().transform(workbook.getStudyConditions(), programUUID);
			studyVariables.addAll(this.getVariableTypeListTransformer()
				.transform(workbook.getStudyConstants(), studyVariables.size() + 1, programUUID));

			final StudyValues studyValues = this.getStudyValuesTransformer().transform(null, studyLocationId, studyMV, studyVariables);

			watch.restart("save study");

			//Recover the studyTypeDto if the id is null. Is necessary to save it in the project table.
			if (null == workbook.getStudyDetails().getStudyType().getId()) {
				final StudyTypeDto studyTypeDto =
					this.studyDataManager.getStudyTypeByName(workbook.getStudyDetails().getStudyType().getName());
				workbook.getStudyDetails().setStudyType(studyTypeDto);
			}

			final DmsProject study = this.getStudySaver()
				.saveStudy(crop, (int) workbook.getStudyDetails().getParentFolderId(), studyVariables, studyValues, saveStudyExperiment,
					programUUID, workbook.getStudyDetails().getStudyType(), workbook.getStudyDetails().getDescription(),
					workbook.getStudyDetails().getStartDate(), workbook.getStudyDetails().getEndDate(),
					workbook.getStudyDetails().getObjective(), workbook.getStudyDetails().getStudyName(),
					workbook.getStudyDetails().getCreatedBy());

			studyId = study.getProjectId();
		}
		watch.stop();

		return studyId;
	}

	private int createTrialDatasetIfNecessary(
		final Workbook workbook, final int studyId, final List<MeasurementVariable> trialMV,
		final VariableTypeList trialVariables, final String programUUID) {
		final TimerWatch watch = new TimerWatch("find trial dataset");
		String trialName = workbook.getStudyDetails().getTrialDatasetName();
		Integer datasetId = null;
		if (trialName == null || "".equals(trialName)) {

			final List<DataSet> dataSetsByType = this.studyDataManager.getDataSetsByType(studyId, DatasetTypeEnum.SUMMARY_DATA.getId());
			if (dataSetsByType != null && !CollectionUtils.isEmpty(dataSetsByType)) {
				datasetId = dataSetsByType.get(0).getId();
			}

			if (datasetId == null) {
				final String studyName = workbook.getStudyDetails().getStudyName();
				trialName = this.generateTrialDatasetName(studyName);
			}
		}

		if (datasetId == null) {
			watch.restart("transform trial dataset values");
			final String trialDescription = !workbook.getStudyDetails().getDescription().isEmpty() ?
				this.generateTrialDatasetName(workbook.getStudyDetails().getDescription()) :
				trialName;
			final DatasetValues trialValues = this.getDatasetValuesTransformer()
				.transform(trialName, trialDescription, trialMV, trialVariables);

			watch.restart("save trial dataset");
			final DmsProject trial =
				this.getDatasetProjectSaver()
					.addDataSet(studyId, trialVariables, trialValues, programUUID, DatasetTypeEnum.SUMMARY_DATA.getId());
			datasetId = trial.getProjectId();
		}

		watch.stop();
		return datasetId;
	}

	private void createTrialExperiment(
		final CropType crop, final int trialProjectId, final int locationId, final VariableList trialVariates,
		final Integer loggedInUserId) {
		final TimerWatch watch = new TimerWatch("save trial experiments");
		final ExperimentValues trialDatasetValues = this.createTrialExperimentValues(locationId, trialVariates);
		this.getExperimentModelSaver()
			.addExperiment(crop, trialProjectId, ExperimentType.TRIAL_ENVIRONMENT, trialDatasetValues, loggedInUserId);
		watch.stop();
	}

	private int createPlotDatasetIfNecessary(
		final Workbook workbook, final int studyId,
		final List<MeasurementVariable> effectMV, final VariableTypeList effectVariables, final VariableTypeList trialVariables,
		final String programUUID) {
		final TimerWatch watch = new TimerWatch("find plotdata dataset");

		String datasetName = workbook.getStudyDetails().getMeasurementDatasetName();
		Integer datasetId = null;

		if (datasetName == null || "".equals(datasetName)) {
			final List<DataSet> dataSetsByType = this.studyDataManager.getDataSetsByType(studyId, DatasetTypeEnum.PLOT_DATA.getId());
			if (dataSetsByType != null && !CollectionUtils.isEmpty(dataSetsByType)) {
				datasetId = dataSetsByType.get(0).getId();
			}

			if (datasetId == null) {
				final String studyName = workbook.getStudyDetails().getStudyName();
				datasetName = this.generatePlotDatasetName(studyName);
			}
		}

		if (datasetId == null) {
			watch.restart("transform measurement effect dataset");
			final String datasetDescription = !workbook.getStudyDetails().getDescription().isEmpty() ?
				this.generatePlotDatasetName(workbook.getStudyDetails().getDescription()) :
				datasetName;
			final DatasetValues datasetValues = this.getDatasetValuesTransformer()
				.transform(datasetName, datasetDescription, effectMV, effectVariables);

			watch.restart("save measurement effect dataset");
			// fix for GCP-6436 start
			final VariableTypeList datasetVariables = this.propagateTrialFactorsIfNecessary(effectVariables, trialVariables);
			// no need to add occ as it is already added in trialVariables
			// fix for GCP-6436 end
			final DmsProject dataset =
				this.getDatasetProjectSaver()
					.addDataSet(studyId, datasetVariables, datasetValues, programUUID, DatasetTypeEnum.PLOT_DATA.getId());
			datasetId = dataset.getProjectId();
		}

		watch.stop();
		return datasetId;
	}

	public void createStocksIfNecessary(final int studyId, final Workbook workbook, final VariableTypeList effectVariables,
		final List<String> trialHeaders) {
		final List<StockModel> studyGermplasm = this.daoFactory.getStockDao().getStocksForStudy(studyId);
		final Map<String, Integer> entryNoStockIdMap =
			studyGermplasm.stream().collect(Collectors.toMap(StockModel::getUniqueName, StockModel::getStockId));

		if (workbook.getObservations() != null) {
			final Session activeSession = this.getActiveSession();
			final FlushMode existingFlushMode = activeSession.getFlushMode();
			activeSession.setFlushMode(FlushMode.MANUAL);

			final Set<Integer> newStocksGids = workbook.getObservations()
				.stream()
				.filter(row -> !entryNoStockIdMap.keySet().contains(row.getMeasurementData(TermId.ENTRY_NO.getId())))
				.map(row -> row.getMeasurementData(TermId.GID.getId()).getValue())
				.filter(NumberUtils::isNumber)
				.map(Integer::valueOf)
				.collect(Collectors.toSet());

			final Map<Integer, String> pedigreeByGids = new HashMap<>();
			if (!newStocksGids.isEmpty()) {
				pedigreeByGids.putAll(this.pedigreeService.getCrossExpansionsBulk(newStocksGids, 1, this.crossExpansionProperties));
			}

			try {
				for (final MeasurementRow row : workbook.getObservations()) {

					final VariableList stock = this.getVariableListTransformer()
						.transformStockOptimize(row, effectVariables, trialHeaders);
					final String entryNumber = this.getEntryNumber(stock);
					Integer stockId = entryNoStockIdMap.get(entryNumber);
					if (stockId == null) {
						stockId = this.getStockSaver().saveStock(studyId, stock, pedigreeByGids);
						entryNoStockIdMap.put(String.valueOf(entryNumber), stockId);
					} else {
						this.getStockSaver().saveOrUpdateStock(stock, stockId);
					}
					row.setStockId(stockId);
				}
				activeSession.flush();
			} finally {
				if (existingFlushMode != null) {
					activeSession.setFlushMode(existingFlushMode);
				}
			}
		}

	}

	private void createMeasurementEffectExperiments(
		final CropType crop, final int datasetId, final VariableTypeList effectVariables,
		final List<MeasurementRow> observations, final List<String> trialHeaders, final Integer loggedInUserId) {

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
					rowWatch.restart("saving row " + i++);
					final ExperimentValues experimentValues = experimentValuesTransformer.transform(row, effectVariables, trialHeaders);
					try {
						experimentModelSaver.addExperiment(crop, datasetId, ExperimentType.PLOT, experimentValues, loggedInUserId);
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
									// add invalid values to the existing map of
									// exceptions for each phenotype
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

	protected VariableTypeList propagateTrialFactorsIfNecessary(
		final VariableTypeList effectVariables,
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

	private Integer getMeansDataset(final Integer studyId) {
		Integer id = null;
		final List<DmsProject> datasets = this.daoFactory.getDmsProjectDAO()
			.getDatasetsByTypeForStudy(studyId, DatasetTypeEnum.MEANS_DATA.getId());
		if (datasets != null && !datasets.isEmpty()) {
			id = datasets.get(0).getProjectId();
		}
		return id;
	}

	/**
	 * Saves project ontology creating entries in the following tables: project and projectprop tables
	 *
	 * @param workbook
	 * @return study id
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public int saveProjectOntology(final Workbook workbook, final String programUUID, final CropType crop) throws Exception {

		final Map<String, ?> variableMap = this.saveVariables(workbook, programUUID);
		workbook.setVariableMap(variableMap);

		// unpack maps first level - Maps of Strings, Maps of VariableTypeList ,
		// Maps of Lists of MeasurementVariable
		final Map<String, VariableTypeList> variableTypeMap =
			(Map<String, VariableTypeList>) variableMap.get(WorkbookSaver.VARIABLETYPEMAP);
		final Map<String, List<MeasurementVariable>> measurementVariableMap =
			(Map<String, List<MeasurementVariable>>) variableMap.get(WorkbookSaver.MEASUREMENTVARIABLEMAP);

		// unpack maps
		final VariableTypeList trialVariables = new VariableTypeList();
		// addAll instead of assigning directly to avoid changing the state of
		// the object
		trialVariables.addAll(variableTypeMap.get(WorkbookSaver.TRIALVARIABLES));
		final VariableTypeList effectVariables = new VariableTypeList();
		// addAll instead of assigning directly to avoid changing the state of
		// the object
		effectVariables.addAll(variableTypeMap.get(WorkbookSaver.EFFECTVARIABLE));
		final List<MeasurementVariable> trialMV = measurementVariableMap.get(WorkbookSaver.TRIALMV);
		final List<MeasurementVariable> effectMV = measurementVariableMap.get(WorkbookSaver.EFFECTMV);

		// locationId and experiment are not yet needed here
		final int studyId = this.createStudyIfNecessary(workbook, 0, false, programUUID, crop);
		final int trialDatasetId = this.createTrialDatasetIfNecessary(workbook, studyId, trialMV, trialVariables, programUUID);
		int measurementDatasetId = 0;
		int meansDatasetId = 0;

		if (workbook.getImportType() != null && workbook.getImportType().intValue() == DatasetTypeEnum.MEANS_DATA.getId()) {
			meansDatasetId = this.createMeansDatasetIfNecessary(workbook, studyId, effectMV, effectVariables, trialVariables, programUUID);
		} else {
			measurementDatasetId =
				this.createPlotDatasetIfNecessary(workbook, studyId, effectMV, effectVariables, trialVariables,
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
	 * Saves experiments creating entries in the following tables:
	 * nd_geolocation, nd_geolocationprop, nd_experiment,
	 * nd_experimentprop, stock, stockprop,
	 * and phenotype
	 *
	 * @param workbook
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void saveProjectData(final Workbook workbook, final String programUUID, final CropType crop) throws Exception {

		final int studyId = workbook.getStudyDetails().getId();
		final int trialDatasetId = workbook.getTrialDatasetId();
		final int measurementDatasetId = workbook.getMeasurementDatesetId() != null ? workbook.getMeasurementDatesetId() : 0;
		final int meansDatasetId = workbook.getMeansDatasetId() != null ? workbook.getMeansDatasetId() : 0;

		final boolean isMeansDataImport =
			workbook.getImportType() != null && workbook.getImportType().intValue() == DatasetTypeEnum.MEANS_DATA.getId();

		Map<String, ?> variableMap = workbook.getVariableMap();
		if (variableMap == null || variableMap.isEmpty()) {
			variableMap = this.saveVariables(workbook, programUUID);
		}

		// unpack maps first level - Maps of Strings, Maps of VariableTypeList ,
		// Maps of Lists of MeasurementVariable
		final Map<String, List<String>> headerMap = (Map<String, List<String>>) variableMap.get(WorkbookSaver.HEADERMAP);
		final Map<String, VariableTypeList> variableTypeMap =
			(Map<String, VariableTypeList>) variableMap.get(WorkbookSaver.VARIABLETYPEMAP);
		final Map<String, List<MeasurementVariable>> measurementVariableMap =
			(Map<String, List<MeasurementVariable>>) variableMap.get(WorkbookSaver.MEASUREMENTVARIABLEMAP);

		// unpack maps
		final List<String> trialHeaders = headerMap.get(WorkbookSaver.TRIALHEADERS);
		final VariableTypeList trialVariableTypeList = variableTypeMap.get(WorkbookSaver.TRIALVARIABLETYPELIST);
		final VariableTypeList trialVariables = variableTypeMap.get(WorkbookSaver.TRIALVARIABLES);
		final VariableTypeList effectVariables = variableTypeMap.get(WorkbookSaver.EFFECTVARIABLE);
		final List<MeasurementVariable> trialMV = measurementVariableMap.get(WorkbookSaver.TRIALMV);
		this.removeConstantsVariables(effectVariables, workbook.getConstants());
		// GCP-8092 Nurseries will always have a unique geolocation, no more
		// concept of shared/common geolocation
		// create locations (entries to nd_geolocation) and associate to
		// observations
		final int studyLocationId/* = DEFAULT_GEOLOCATION_ID */;
		final List<Integer> locationIds = new ArrayList<>();
		final Map<Integer, VariableList> trialVariatesMap = new HashMap<>();
		if (trialVariableTypeList != null) {// multi-location
			studyLocationId =
				this.createLocationsAndSetToObservations(locationIds, workbook, trialVariables, trialHeaders, trialVariatesMap, false,
					programUUID);
		} else {
			studyLocationId =
				this.createLocationAndSetToObservations(workbook, trialMV, trialVariables, trialVariatesMap, false, programUUID);
		}

		// create stock and stockprops and associate to observations
		this.createOrResolveStudyGermplasm(workbook, studyId, effectVariables, trialHeaders);

		// create trial experiments if not yet existing
		final boolean hasExistingStudyExperiment = this.checkIfHasExistingStudyExperiment(studyId);
		final boolean hasExistingTrialExperiments = this.checkIfHasExistingExperiments(locationIds);
		if (!hasExistingStudyExperiment) {
			// 1. study experiment
			final StudyValues values = new StudyValues();
			values.setLocationId(studyLocationId);
			this.getStudySaver().saveStudyExperiment(crop, studyId, values);
		}
		// create trial experiments if not yet existing
		final Integer loggedInUserId = workbook.getUserId();
		if (!hasExistingTrialExperiments) {
			// 2. trial experiments
			if (trialVariableTypeList != null) {// multi-location
				for (final Integer locationId : locationIds) {
					this.setVariableListValues(trialVariatesMap.get(locationId), workbook.getConstants());
					this.createTrialExperiment(crop, trialDatasetId, locationId, trialVariatesMap.get(locationId), loggedInUserId);
				}
			} else {
				this.createTrialExperiment(crop, trialDatasetId, studyLocationId, trialVariatesMap.get(studyLocationId), loggedInUserId);
			}
		}
		if (isMeansDataImport) {
			// 3. means experiments
			this.createMeansExperiments(crop, meansDatasetId, effectVariables, workbook.getObservations(), trialHeaders, trialVariatesMap,
				loggedInUserId);
		} else {
			// 3. measurement experiments
			this.createMeasurementEffectExperiments(crop, measurementDatasetId, effectVariables, workbook.getObservations(), trialHeaders,
				loggedInUserId);
		}
	}

	void createOrResolveStudyGermplasm(final Workbook workbook, final Integer studyId, final VariableTypeList effectVariables,
		final List<String> trialHeaders) {
		final List<StockModel> studyGermplasm = this.daoFactory.getStockDao().getStocksForStudy(studyId);
		if (CollectionUtils.isEmpty(studyGermplasm)) {
			this.createStocksIfNecessary(studyId, workbook, effectVariables, trialHeaders);
		} else {
			this.setDatasetStocks(workbook, studyGermplasm);
		}
	}

	void setDatasetStocks(final Workbook workbook, final List<StockModel> studyGermplasm) {
		final Map<String, Integer> entryNoStockIdMap =
			studyGermplasm.stream().collect(Collectors.toMap(StockModel::getUniqueName, StockModel::getStockId));
		for (final MeasurementRow row : workbook.getObservations()) {
			final String entryNo = row.getMeasurementData(TermId.ENTRY_NO.getId()).getValue();
			row.setStockId(entryNoStockIdMap.get(entryNo));
		}
	}

	// The constants are not needed in the creation of stocks, means
	// experiments, and measurement effects experiments so we need to remove it
	void removeConstantsVariables(final VariableTypeList effectVariables, final List<MeasurementVariable> constants) {

		final List<DMSVariableType> variableTypes = new ArrayList<>();
		for (final DMSVariableType varType : effectVariables.getVariableTypes()) {
			boolean isConstant = false;
			for (final MeasurementVariable mvar : constants) {
				if (varType.getId() == mvar.getTermId()) {
					isConstant = true;
					break;
				}
			}
			if (!isConstant) {
				variableTypes.add(varType);
			}
		}
		effectVariables.setVariableTypes(variableTypes);

	}

	private boolean checkIfHasExistingStudyExperiment(final int studyId) {
		final Integer experimentId = this.daoFactory.getExperimentDao().getExperimentIdByProjectId(studyId);
		return experimentId != null;
	}

	private boolean checkIfHasExistingExperiments(final List<Integer> locationIds) {
		final List<Integer> experimentIds = this.daoFactory.getExperimentDao().getExperimentIdsByGeolocationIds(locationIds);
		return experimentIds != null && !experimentIds.isEmpty();
	}

	private VariableList createDefaultGeolocationVariableList(final String programUUID) {
		final VariableList list = new VariableList();

		final DMSVariableType variableType = new DMSVariableType(PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().get(0),
			PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().get(0),
			this.getStandardVariableBuilder().create(TermId.TRIAL_INSTANCE_FACTOR.getId(), programUUID), 1);
		final Variable variable = new Variable(variableType, "1");
		list.add(variable);

		return list;
	}

	public void saveWorkbookVariables(final Workbook workbook) throws ParseException {

		final int parentFolderId = (int) workbook.getStudyDetails().getParentFolderId();

		final DmsProject study = this.daoFactory.getDmsProjectDAO().getById(workbook.getStudyDetails().getId());
		study.setParent(this.daoFactory.getDmsProjectDAO().getById(parentFolderId));
		Integer trialDatasetId = workbook.getTrialDatasetId();
		Integer measurementDatasetId = workbook.getMeasurementDatesetId();
		if (workbook.getTrialDatasetId() == null || workbook.getMeasurementDatesetId() == null) {
			final Integer studyId = study.getProjectId();
			measurementDatasetId = this.workbookBuilder.getMeasurementDataSetId(studyId);
			trialDatasetId = this.workbookBuilder.getTrialDataSetId(studyId);
		}
		final DmsProject trialDataset = this.daoFactory.getDmsProjectDAO().getById(trialDatasetId);
		final DmsProject measurementDataset = this.daoFactory.getDmsProjectDAO().getById(measurementDatasetId);

		this.saveProjectProperties(workbook);

		final String description = workbook.getStudyDetails().getDescription();
		final String startDate = workbook.getStudyDetails().getStartDate();
		final String endDate = workbook.getStudyDetails().getEndDate();
		final String objective = workbook.getStudyDetails().getObjective();
		final String createdBy = workbook.getStudyDetails().getCreatedBy();

		this.updateStudyDetails(description + WorkbookSaver.ENVIRONMENT, trialDataset, objective);
		this.updateStudyDetails(description, startDate, endDate, study, objective, createdBy);
		this.updateStudyDetails(description + WorkbookSaver.PLOTDATA, measurementDataset, objective);
	}

	public void saveProjectProperties(final Workbook workbook) {
		final Integer studyId = workbook.getStudyDetails().getId();
		final Integer trialDatasetId = workbook.getTrialDatasetId();
		final Integer measurementDatasetId = workbook.getMeasurementDatesetId();

		final DmsProject study = this.daoFactory.getDmsProjectDAO().getById(studyId);
		final DmsProject trialDataset = this.daoFactory.getDmsProjectDAO().getById(trialDatasetId);
		final DmsProject measurementDataset = this.daoFactory.getDmsProjectDAO().getById(measurementDatasetId);

		this.getProjectPropertySaver().saveProjectProperties(study, trialDataset, measurementDataset, workbook.getConditions(), false);
		this.getProjectPropertySaver().saveProjectProperties(study, trialDataset, measurementDataset, workbook.getConstants(), true);
		this.getProjectPropertySaver().saveProjectProperties(study, trialDataset, measurementDataset, workbook.getVariates(), false);
		this.getProjectPropertySaver().saveFactors(measurementDataset, workbook.getFactors());
	}

	private void updateStudyDetails(
		final String description, final String startDate, final String endDate, final DmsProject study,
		final String objective, final String createdBy) throws ParseException {

		if (study.getCreatedBy() == null) {
			study.setCreatedBy(createdBy);
		}

		if (startDate != null && startDate.contains("-")) {
			study.setStartDate(Util.convertDate(startDate, Util.FRONTEND_DATE_FORMAT, Util.DATE_AS_NUMBER_FORMAT));
		} else {
			study.setStartDate(startDate);
		}
		study.setStudyUpdate(Util.getCurrentDateAsStringValue(Util.DATE_AS_NUMBER_FORMAT));

		if (endDate != null && endDate.contains("-")) {
			study.setEndDate(Util.convertDate(endDate, Util.FRONTEND_DATE_FORMAT, Util.DATE_AS_NUMBER_FORMAT));
		} else {
			study.setEndDate(endDate);
		}

		this.updateStudyDetails(description, study, objective);
	}

	private void updateStudyDetails(final String description, final DmsProject study, final String objective) {
		study.setDescription(description);
		study.setObjective(objective);
		this.daoFactory.getDmsProjectDAO().merge(study);
	}

	private int createMeansDatasetIfNecessary(
		final Workbook workbook, final int studyId, final List<MeasurementVariable> effectMV,
		final VariableTypeList effectVariables, final VariableTypeList trialVariables, final String programUUID) {

		final TimerWatch watch = new TimerWatch("find means dataset");
		Integer datasetId = this.getMeansDataset(studyId);

		if (datasetId == null) {
			watch.restart("transform means dataset");
			final String datasetName = this.generateMeansDatasetName(workbook.getStudyDetails().getStudyName());
			final String datasetDescription = this.generateMeansDatasetName(workbook.getStudyDetails().getDescription());
			final DatasetValues datasetValues = this.getDatasetValuesTransformer()
				.transform(datasetName, datasetDescription, effectMV, effectVariables);

			watch.restart("save means dataset");
			final VariableTypeList datasetVariables = this.getMeansData(effectVariables, trialVariables);
			final DmsProject dataset =
				this.getDatasetProjectSaver()
					.addDataSet(studyId, datasetVariables, datasetValues, programUUID, DatasetTypeEnum.MEANS_DATA.getId());
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

	private void createMeansExperiments(
		final CropType crop, final int datasetId, final VariableTypeList effectVariables,
		final List<MeasurementRow> observations, final List<String> trialHeaders, final Map<Integer, VariableList> trialVariatesMap,
		final Integer loggedinUserId) {

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
					experimentModelSaver.addExperiment(crop, datasetId, ExperimentType.AVERAGE, experimentValues,
						loggedinUserId);
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
								// add invalid values to the existing map of
								// exceptions for each phenotype
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
