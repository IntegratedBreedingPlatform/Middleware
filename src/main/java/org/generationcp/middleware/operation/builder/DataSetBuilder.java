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

package org.generationcp.middleware.operation.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.helper.VariableInfo;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.util.DatasetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

public class DataSetBuilder extends Builder {

	// ready for Sring autowiring :-)
	private DmsProjectDao dmsProjectDao;
	private StudyDataManager studyDataManager;

	private static final List<Integer> HIDDEN_DATASET_COLUMNS = Arrays.asList(TermId.DATASET_NAME.getId(), TermId.DATASET_TITLE.getId(),
			TermId.DATASET_TYPE.getId());
	
	private static final Logger LOG = LoggerFactory.getLogger(DataSetBuilder.class);

	public DataSetBuilder(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
		this.dmsProjectDao = this.getDmsProjectDao();
		this.studyDataManager = this.getStudyDataManager();
	}

	public DataSetBuilder(HibernateSessionProvider sessionProviderForLocal, DmsProjectDao dmsProjectDao, StudyDataManager studyDataManager) {
		super(sessionProviderForLocal);
		this.dmsProjectDao = dmsProjectDao;
		this.studyDataManager = studyDataManager;
	}

	public DataSet build(int dataSetId) throws MiddlewareException {
		Monitor monitor = MonitorFactory.start("OpenTrial.bms.middleware.DatasetBuilder.build");
		try {
			DataSet dataSet = null;
			DmsProject project = this.dmsProjectDao.getById(dataSetId);
			if (project != null) {
				dataSet = this.createDataSet(project);
			}
			return dataSet;
		} finally {
			monitor.stop();
		}

	}

	public VariableTypeList getVariableTypes(int dataSetId) throws MiddlewareException {
		VariableTypeList variableTypeList = new VariableTypeList();
		DmsProject project = this.dmsProjectDao.getById(dataSetId);
		if (project != null) {
			Set<VariableInfo> variableInfoList = this.getVariableInfoBuilder().create(project.getProperties());
			for (VariableInfo variableInfo : variableInfoList) {
				variableTypeList.add(this.getVariableTypeBuilder().create(variableInfo, project.getProgramUUID()));
			}
		}
		return variableTypeList.sort();
	}

	private DataSet createDataSet(DmsProject project) throws MiddlewareException {
		DataSet dataSet = new DataSet();
		dataSet.setId(project.getProjectId());
		dataSet.setName(project.getName());
		dataSet.setDescription(project.getDescription());
		dataSet.setStudyId(this.getStudyId(project));
		dataSet.setDataSetType(this.getDataSetType(project));
		dataSet.setVariableTypes(this.getVariableTypes(project));
		dataSet.setLocationIds(this.getLocationIds(project.getProjectId()));
		return dataSet;
	}

	private Set<Integer> getLocationIds(Integer projectId) throws MiddlewareQueryException {
		return this.getGeolocationDao().getLocationIds(projectId);
	}

	private VariableTypeList getVariableTypes(DmsProject project) throws MiddlewareException {
		VariableTypeList variableTypes = new VariableTypeList();

		Set<VariableInfo> variableInfoList = this.getVariableInfoBuilder().create(project.getProperties());
		for (VariableInfo variableInfo : variableInfoList) {
			variableTypes.add(this.getVariableTypeBuilder().create(variableInfo, project.getProgramUUID()));
		}
		return variableTypes.sort();
	}

	private int getStudyId(DmsProject project) {
		DmsProject study = project.getRelatedTos().get(0).getObjectProject();
		return study.getProjectId();
	}

	private DataSetType getDataSetType(DmsProject project) {
		for (ProjectProperty property : project.getProperties()) {
			if (TermId.DATASET_TYPE.getId() == property.getTypeId()) {
				return DataSetType.findById(Integer.valueOf(property.getValue()));
			}
		}
		return null;
	}

	public DmsProject getTrialDataset(int studyId) {
		Monitor monitor = MonitorFactory.start("OpenTrial.bms.middleware.DataSetBuilder.getTrialDataset");
		try {
			List<DatasetReference> datasetReferences = this.studyDataManager.getDatasetReferences(studyId);
			if (datasetReferences == null || datasetReferences.isEmpty()) {
				throw new MiddlewareQueryException("no.dataset.found", "No datasets found for study " + studyId);
			}
			for (DatasetReference datasetReference : datasetReferences) {
				if (datasetReference.getName().endsWith(DatasetUtil.NEW_ENVIRONMENT_DATASET_NAME_SUFFIX)) {
					return this.getDmsProjectById(datasetReference.getId());
				}
			}
			// if not found in the list using the name, get dataset reference with Summary Data type
			final DatasetReference trialDatasetReference =
					this.studyDataManager.findOneDataSetReferenceByType(studyId, DataSetType.SUMMARY_DATA);
			if (trialDatasetReference != null) {
				return this.getDmsProjectById(trialDatasetReference.getId());
			}
			throw new MiddlewareQueryException("no.trial.dataset.found", "Study exists but no environment dataset for " + studyId);
		} finally {
			monitor.stop();
		}
	}

	public Workbook buildCompleteDataset(int datasetId, boolean isTrial) throws MiddlewareException {
		DataSet dataset = this.build(datasetId);
		List<Integer> siblingVariables = this.getVariablesOfSiblingDatasets(datasetId);
		boolean isMeasurementDataset = this.isMeasurementDataset(dataset);
		VariableTypeList variables = null;
		if (isMeasurementDataset) {
			variables = this.filterVariables(dataset.getVariableTypes(), siblingVariables);
		} else {
			variables = dataset.getVariableTypes();
		}

		// We need to set the role of the variables based on the experiments before filtering them based on role
		long expCount = this.getStudyDataManager().countExperiments(datasetId);
		List<Experiment> experiments = this.getStudyDataManager().getExperiments(datasetId, 0, (int) expCount, variables);

		variables = this.filterDatasetVariables(variables, !isTrial, isMeasurementDataset);

		List<MeasurementVariable> factorList = this.getMeasurementVariableTransformer().transform(variables.getFactors(), true);
		List<MeasurementVariable> variateList = this.getMeasurementVariableTransformer().transform(variables.getVariates(), false, true);
		Workbook workbook = new Workbook();
		workbook.setObservations(this.getWorkbookBuilder().buildDatasetObservations(experiments, variables, factorList, variateList));
		workbook.setFactors(factorList);
		workbook.setVariates(variateList);
		List<MeasurementVariable> measurementDatasetVariables = new ArrayList<MeasurementVariable>();
		measurementDatasetVariables.addAll(factorList);
		measurementDatasetVariables.addAll(variateList);
		workbook.setMeasurementDatasetVariables(measurementDatasetVariables);

		return workbook;
	}

	private VariableTypeList filterDatasetVariables(VariableTypeList variables, boolean isNursery, boolean isMeasurementDataset) {
		VariableTypeList newVariables = new VariableTypeList();
		if (variables != null) {
			for (DMSVariableType variable : variables.getVariableTypes()) {
				boolean partOfHiddenDatasetColumns = DataSetBuilder.HIDDEN_DATASET_COLUMNS.contains(variable.getId());
				boolean isOccAndNurseryAndMeasurementDataset =
						variable.getId() == TermId.TRIAL_INSTANCE_FACTOR.getId() && isNursery && isMeasurementDataset;
				boolean isMeasurementDatasetAndIsTrialFactors =
						isMeasurementDataset && PhenotypicType.TRIAL_ENVIRONMENT == variable.getRole();
				boolean isTrialAndOcc = !isNursery && variable.getId() == TermId.TRIAL_INSTANCE_FACTOR.getId();
				if (!partOfHiddenDatasetColumns && !isOccAndNurseryAndMeasurementDataset && !isMeasurementDatasetAndIsTrialFactors
						|| isTrialAndOcc) {
					newVariables.add(variable);
				}
			}
		}
		return newVariables;
	}

	private boolean isMeasurementDataset(DataSet dataset) {
		String datasetName = dataset.getName();
		DataSetType datasetType = dataset.getDataSetType();

		return datasetName.toUpperCase().startsWith("MEASUREMENT EFEC_") || datasetName.toUpperCase().startsWith("MEASUREMENT EFECT_")
				|| !datasetName.toUpperCase().startsWith("TRIAL_") && datasetType == DataSetType.PLOT_DATA;
	}

	private List<Integer> getVariablesOfSiblingDatasets(int datasetId) throws MiddlewareQueryException {
		return this.getProjectPropertyDao().getVariablesOfSiblingDatasets(datasetId);
	}

	private VariableTypeList filterVariables(VariableTypeList variables, List<Integer> filters) {
		VariableTypeList newList = new VariableTypeList();
		if (variables != null && !variables.getVariableTypes().isEmpty()) {
			for (DMSVariableType variable : variables.getVariableTypes()) {
				if (!filters.contains(variable.getId()) || variable.getId() == TermId.TRIAL_INSTANCE_FACTOR.getId()) {
					newList.add(variable);
				}
			}
		}
		return newList;
	}

	protected DmsProject getDmsProjectById(int studyId) throws MiddlewareQueryException {
		return this.dmsProjectDao.getById(studyId);
	}

}
