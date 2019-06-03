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

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.helper.VariableInfo;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class DataSetBuilder extends Builder {

	// ready for Sring autowiring :-)
	private final DmsProjectDao dmsProjectDao;
	private final StudyDataManager studyDataManager;

	private static final List<Integer> HIDDEN_DATASET_COLUMNS = Arrays.asList(TermId.DATASET_NAME.getId(), TermId.DATASET_TITLE.getId());

	private static final Logger LOG = LoggerFactory.getLogger(DataSetBuilder.class);

	public DataSetBuilder(final HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
		this.dmsProjectDao = this.getDmsProjectDao();
		this.studyDataManager = this.getStudyDataManager();
	}

	public DataSetBuilder(final HibernateSessionProvider sessionProviderForLocal, final DmsProjectDao dmsProjectDao, final StudyDataManager studyDataManager) {
		super(sessionProviderForLocal);
		this.dmsProjectDao = dmsProjectDao;
		this.studyDataManager = studyDataManager;
	}

	public DataSet build(final int dataSetId)  {
		final Monitor monitor = MonitorFactory.start("Build DataSet. dataSetId: " + dataSetId);
		try {
			DataSet dataSet = null;
			final DmsProject project = this.dmsProjectDao.getById(dataSetId);
			if (project != null) {
				dataSet = this.createDataSet(project);
			}
			return dataSet;
		} finally {
			LOG.debug("" + monitor.stop());
		}

	}

	public VariableTypeList getVariableTypes(final int dataSetId)  {
		final VariableTypeList variableTypeList = new VariableTypeList();
		final DmsProject project = this.dmsProjectDao.getById(dataSetId);
		if (project != null) {
			final Set<VariableInfo> variableInfoList = this.getVariableInfoBuilder().create(project.getProperties());
			for (final VariableInfo variableInfo : variableInfoList) {
				variableTypeList.add(this.getVariableTypeBuilder().create(variableInfo, project.getProgramUUID()));
			}
		}
		return variableTypeList.sort();
	}

	public VariableTypeList getTreatmentFactorVariableTypes(final int dataSetId)  {
		final VariableTypeList variableTypeList = new VariableTypeList();
		final DmsProject project = this.dmsProjectDao.getById(dataSetId);
		if (project != null) {
			final Set<VariableInfo> variableInfoList = this.getVariableInfoBuilder().create(project.getProperties());
			for (final VariableInfo variableInfo : variableInfoList) {
				if(!StringUtil.isEmpty(variableInfo.getTreatmentLabel())) {
					variableTypeList.add(this.getVariableTypeBuilder().create(variableInfo, project.getProgramUUID()));
				}
			}
		}
		return variableTypeList.sort();
	}

	private DataSet createDataSet(final DmsProject project)  {
		final DataSet dataSet = new DataSet();
		dataSet.setId(project.getProjectId());
		dataSet.setName(project.getName());
		dataSet.setDescription(project.getDescription());
		dataSet.setStudyId(project.getStudy().getProjectId());
		dataSet.setDatasetType(project.getDatasetType());
		dataSet.setVariableTypes(this.getVariableTypes(project));
		dataSet.setLocationIds(this.getLocationIds(project.getProjectId()));
		dataSet.setProgramUUID(project.getProgramUUID());
		return dataSet;
	}

	private Set<Integer> getLocationIds(final Integer projectId) {
		return this.getGeolocationDao().getLocationIds(projectId);
	}

	private VariableTypeList getVariableTypes(final DmsProject project)  {
		final VariableTypeList variableTypes = new VariableTypeList();

		final Set<VariableInfo> variableInfoList = this.getVariableInfoBuilder().create(project.getProperties());
		for (final VariableInfo variableInfo : variableInfoList) {
			variableTypes.add(this.getVariableTypeBuilder().create(variableInfo, project.getProgramUUID()));
		}
		return variableTypes.sort();
	}

	public DmsProject getTrialDataset(final int studyId) {
		// Get dataset reference with Summary Data type
		final DatasetReference trialDatasetReference = this.studyDataManager.findOneDataSetReferenceByType(studyId, DatasetTypeEnum.SUMMARY_DATA.getId());
		if (trialDatasetReference != null) {
			return this.getDmsProjectById(trialDatasetReference.getId());
		}
		throw new MiddlewareQueryException("no.trial.dataset.found", "Study exists but no environment dataset for " + studyId);
	}

	public Workbook buildCompleteDataset(final int datasetId)  {
		final DataSet dataset = this.build(datasetId);
		final List<Integer> siblingVariables = this.getVariablesOfSiblingDatasets(datasetId);
		final boolean isMeasurementDataset = (dataset.getDatasetType() != null) ? dataset.getDatasetType().isObservationType() : Boolean.FALSE;
		VariableTypeList variables;
		if (isMeasurementDataset) {
			variables = this.filterVariables(dataset.getVariableTypes(), siblingVariables);
		} else {
			variables = dataset.getVariableTypes();
		}

		// We need to set the role of the variables based on the experiments before filtering them based on role
		final long expCount = this.getStudyDataManager().countExperiments(datasetId);
		final List<Experiment> experiments = this.getStudyDataManager().getExperiments(datasetId, 0, (int) expCount, variables);

		variables = this.filterDatasetVariables(variables, isMeasurementDataset);

		final List<MeasurementVariable> factorList = this.getMeasurementVariableTransformer().transform(variables.getFactors(), true);
		final List<MeasurementVariable> variateList = this.getMeasurementVariableTransformer().transform(variables.getVariates(), false);
		final Workbook workbook = new Workbook();
		workbook.setObservations(this.getWorkbookBuilder().buildDatasetObservations(experiments, variables, factorList, variateList));
		workbook.setFactors(factorList);
		workbook.setVariates(variateList);
		final List<MeasurementVariable> measurementDatasetVariables = new ArrayList<>();
		measurementDatasetVariables.addAll(factorList);
		measurementDatasetVariables.addAll(variateList);
		workbook.setMeasurementDatasetVariables(measurementDatasetVariables);

		return workbook;
	}

	protected VariableTypeList filterDatasetVariables(final VariableTypeList variables, final boolean isMeasurementDataset) {
		final VariableTypeList newVariables = new VariableTypeList();
		if (variables != null) {
			for (final DMSVariableType variable : variables.getVariableTypes()) {
				final boolean partOfHiddenDatasetColumns = DataSetBuilder.HIDDEN_DATASET_COLUMNS.contains(variable.getId());
				final boolean isOccAndNurseryAndMeasurementDataset =
						variable.getId() == TermId.TRIAL_INSTANCE_FACTOR.getId() && isMeasurementDataset;
				final boolean isMeasurementDatasetAndIsTrialFactors =
						isMeasurementDataset && PhenotypicType.TRIAL_ENVIRONMENT == variable.getRole();
				final boolean isTrialAndOcc = variable.getId() == TermId.TRIAL_INSTANCE_FACTOR.getId();
				final boolean isTreatmentFactorDuplicate = variable.getVariableType() == null && !StringUtils.isEmpty(variable.getTreatmentLabel());
				if (!partOfHiddenDatasetColumns && !isOccAndNurseryAndMeasurementDataset && !isMeasurementDatasetAndIsTrialFactors && !isTreatmentFactorDuplicate
						|| isTrialAndOcc) {
					newVariables.add(variable);
				}
			}
		}
		return newVariables;
	}

	private List<Integer> getVariablesOfSiblingDatasets(final int datasetId) {
		return this.getProjectPropertyDao().getVariablesOfSiblingDatasets(datasetId);
	}

	private VariableTypeList filterVariables(final VariableTypeList variables, final List<Integer> filters) {
		final VariableTypeList newList = new VariableTypeList();
		if (variables != null && !variables.getVariableTypes().isEmpty()) {
			for (final DMSVariableType variable : variables.getVariableTypes()) {
				if (!filters.contains(variable.getId()) || variable.getId() == TermId.TRIAL_INSTANCE_FACTOR.getId()) {
					newList.add(variable);
				}
			}
		}
		return newList;
	}

	protected DmsProject getDmsProjectById(final int studyId) {
		return this.dmsProjectDao.getById(studyId);
	}

}
