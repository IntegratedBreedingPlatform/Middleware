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
import org.generationcp.middleware.api.nametype.GermplasmNameTypeDTO;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.helper.VariableInfo;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.generationcp.middleware.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DataSetBuilder extends Builder {

	private DaoFactory daoFactory;

	@Resource
	private StudyDataManager studyDataManager;

	@Resource
	private WorkbookBuilder workbookBuilder;

	private static final List<Integer> HIDDEN_DATASET_COLUMNS = Arrays.asList(TermId.DATASET_NAME.getId(), TermId.DATASET_TITLE.getId());

	private static final Logger LOG = LoggerFactory.getLogger(DataSetBuilder.class);

	@Resource
	private DatasetService datasetService;

	public DataSetBuilder() {

	}

	public DataSetBuilder(final HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
		this.daoFactory = new DaoFactory(sessionProviderForLocal);
	}

	public DataSet build(final int dataSetId) {
		final Monitor monitor = MonitorFactory.start("Build DataSet. dataSetId: " + dataSetId);
		try {
			DataSet dataSet = null;
			final DmsProject project = this.daoFactory.getDmsProjectDAO().getById(dataSetId);
			if (project != null) {
				dataSet = this.createDataSet(project);
			}
			return dataSet;
		} finally {
			LOG.debug("" + monitor.stop());
		}

	}

	public VariableTypeList getVariableTypes(final int dataSetId) {
		final VariableTypeList variableTypeList = new VariableTypeList();
		final DmsProject project = this.daoFactory.getDmsProjectDAO().getById(dataSetId);
		if (project != null) {
			final Set<VariableInfo> variableInfoList = this.getVariableInfoBuilder().create(project.getProperties());
			for (final VariableInfo variableInfo : variableInfoList) {
				variableTypeList.add(this.getVariableTypeBuilder().create(variableInfo, project.getProgramUUID()));
			}
		}
		return variableTypeList.sort();
	}

	public VariableTypeList getTreatmentFactorVariableTypes(final int dataSetId) {
		final VariableTypeList variableTypeList = new VariableTypeList();
		final DmsProject project = this.daoFactory.getDmsProjectDAO().getById(dataSetId);
		if (project != null) {
			final Set<VariableInfo> variableInfoList = this.getVariableInfoBuilder().create(project.getProperties());
			for (final VariableInfo variableInfo : variableInfoList) {
				if (!StringUtil.isEmpty(variableInfo.getTreatmentLabel())) {
					variableTypeList.add(this.getVariableTypeBuilder().create(variableInfo, project.getProgramUUID()));
				}
			}
		}
		return variableTypeList.sort();
	}

	private DataSet createDataSet(final DmsProject project) {
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
		return this.daoFactory.getGeolocationDao().getLocationIds(projectId);
	}

	private VariableTypeList getVariableTypes(final DmsProject project) {
		final VariableTypeList variableTypes = new VariableTypeList();

		final Set<VariableInfo> variableInfoList = this.getVariableInfoBuilder().create(project.getProperties());
		for (final VariableInfo variableInfo : variableInfoList) {
			variableTypes.add(this.getVariableTypeBuilder().create(variableInfo, project.getProgramUUID()));
		}
		return variableTypes.sort();
	}

	public DmsProject getTrialDataset(final int studyId) {
		// Get dataset reference with Summary Data type
		final DatasetReference trialDatasetReference =
			this.studyDataManager.findOneDataSetReferenceByType(studyId, DatasetTypeEnum.SUMMARY_DATA.getId());
		if (trialDatasetReference != null) {
			return this.getDmsProjectById(trialDatasetReference.getId());
		}
		throw new MiddlewareQueryException("no.trial.dataset.found", "Study exists but no environment dataset for " + studyId);
	}

	public Workbook buildCompleteDataset(final int datasetId) {
		final DataSet dataset = this.build(datasetId);

		final List<GermplasmNameTypeDTO> germplasmNameTypeDTOs = this.datasetService.getDatasetNameTypes(dataset.getId());
		germplasmNameTypeDTOs.sort(Comparator.comparing(GermplasmNameTypeDTO::getCode));
		final List<MeasurementVariable> NameList = germplasmNameTypeDTOs.stream().map(
				germplasmNameTypeDTO -> //
					new MeasurementVariable(germplasmNameTypeDTO.getCode(), //
						germplasmNameTypeDTO.getDescription(), //
						germplasmNameTypeDTO.getId(), DataType.CHARACTER_VARIABLE.getId(), //
						germplasmNameTypeDTO.getCode(), true)) //
			.collect(Collectors.toList());

		final boolean isMeasurementDataset =
			(dataset.getDatasetType() != null) ? dataset.getDatasetType().isObservationType() : Boolean.FALSE;
		VariableTypeList variables;
		if (isMeasurementDataset) {
			variables = this.filterVariables(dataset.getVariableTypes());
		} else {
			variables = dataset.getVariableTypes();
		}

		// We need to set the role of the variables based on the experiments before filtering them based on role
		final long expCount = this.studyDataManager.countExperiments(datasetId);
		final List<Experiment> experiments = this.studyDataManager.getExperiments(datasetId, 0, (int) expCount, variables);

		variables = this.filterDatasetVariables(variables, isMeasurementDataset);

		final List<MeasurementVariable> factorList = this.getMeasurementVariableTransformer().transform(variables.getFactors(), true);
		final List<MeasurementVariable> variateList = this.getMeasurementVariableTransformer().transform(variables.getVariates(), false);
		final Workbook workbook = new Workbook();
		workbook.setObservations(this.workbookBuilder.buildDatasetObservations(experiments, variables, factorList, variateList, NameList));
		workbook.setFactors(factorList);
		workbook.setVariates(variateList);
		final List<MeasurementVariable> measurementDatasetVariables = new ArrayList<>();
		measurementDatasetVariables.addAll(factorList);
		measurementDatasetVariables.addAll(variateList);
		measurementDatasetVariables.addAll(NameList);
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
				final boolean isTreatmentFactorDuplicate =
					variable.getVariableType() == null && !StringUtils.isEmpty(variable.getTreatmentLabel());
				if (!partOfHiddenDatasetColumns && !isOccAndNurseryAndMeasurementDataset && !isMeasurementDatasetAndIsTrialFactors
					&& !isTreatmentFactorDuplicate
					|| isTrialAndOcc) {
					newVariables.add(variable);
				}
			}
		}
		return newVariables;
	}

	public VariableTypeList filterVariables(final VariableTypeList variables) {
		final VariableTypeList newList = new VariableTypeList();
		if (variables != null && !variables.getVariableTypes().isEmpty()) {
			for (final DMSVariableType variable : variables.getVariableTypes()) {
				if (!PhenotypicType.DATASET.equals(variable.getStandardVariable().getPhenotypicType()) &&
					!PhenotypicType.STUDY.equals(variable.getStandardVariable().getPhenotypicType())) {
					newList.add(variable);
				}
			}
		}
		return newList;
	}

	protected DmsProject getDmsProjectById(final int studyId) {
		return this.daoFactory.getDmsProjectDAO().getById(studyId);
	}

}
