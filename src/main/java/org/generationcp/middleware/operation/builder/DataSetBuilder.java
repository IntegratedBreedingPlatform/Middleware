/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.operation.builder;

import org.generationcp.middleware.domain.dms.*;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.helper.VariableInfo;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.dms.ProjectRelationship;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class DataSetBuilder extends Builder {
	
	private static final List<Integer> HIDDEN_DATASET_COLUMNS = Arrays.asList(TermId.DATASET_NAME.getId(), TermId.DATASET_TITLE.getId(),
			TermId.DATASET_TYPE.getId());

	public DataSetBuilder(HibernateSessionProvider sessionProviderForLocal,
			                 HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public DataSet build(int dataSetId) throws MiddlewareQueryException {
		DataSet dataSet = null;
		if (setWorkingDatabase(dataSetId)) {
			DmsProject project = getDmsProjectDao().getById(dataSetId);
			if (project != null) {
				dataSet = createDataSet(project);
			}
		}
		return dataSet;
	}
	
	public VariableTypeList getVariableTypes(int dataSetId) throws MiddlewareQueryException {
		VariableTypeList variableTypeList = new VariableTypeList();
		if (setWorkingDatabase(dataSetId)) {
			DmsProject project = getDmsProjectDao().getById(dataSetId);
			if (project != null) {
				Set<VariableInfo> variableInfoList = getVariableInfoBuilder().create(project.getProperties());
				for (VariableInfo variableInfo : variableInfoList) {
					variableTypeList.add(getVariableTypeBuilder().create(variableInfo));
				}
			}
		}
		return variableTypeList.sort();
	}

	private DataSet createDataSet(DmsProject project) throws MiddlewareQueryException {
		DataSet dataSet = new DataSet();
		dataSet.setId(project.getProjectId());
		dataSet.setName(project.getName());
		dataSet.setDescription(project.getDescription());
		dataSet.setStudyId(getStudyId(project));
		dataSet.setDataSetType(getDataSetType(project));
		dataSet.setVariableTypes(getVariableTypes(project));
		dataSet.setLocationIds(getLocationIds(project.getProjectId()));
		return dataSet;
	}

	private Set<Integer> getLocationIds(Integer projectId) throws MiddlewareQueryException {
		return this.getGeolocationDao().getLocationIds(projectId);
	}

	private VariableTypeList getVariableTypes(DmsProject project) throws MiddlewareQueryException {
		VariableTypeList variableTypes = new VariableTypeList();
		
		Set<VariableInfo> variableInfoList = getVariableInfoBuilder().create(project.getProperties());
		for (VariableInfo variableInfo : variableInfoList) {
			variableTypes.add(getVariableTypeBuilder().create(variableInfo));
		}
		return variableTypes.sort();
	}

	private int getStudyId(DmsProject project) {
		DmsProject study = project.getRelatedTos().get(0).getObjectProject();
		return study.getProjectId();
	}
	
	private DataSetType getDataSetType(DmsProject project) throws MiddlewareQueryException {
		for (ProjectProperty property : project.getProperties()) {
			if (TermId.DATASET_TYPE.getId() == property.getTypeId()) {
				return DataSetType.findById(Integer.valueOf(property.getValue()));
			}
		}
		return null;
	}

	public DmsProject getTrialDataset(int studyId, int measurementDatasetId) throws MiddlewareQueryException {
	    setWorkingDatabase(studyId);
	    DmsProject trialDataset = null;
	    DmsProject study = getDmsProjectById(studyId);
	    List<ProjectRelationship> datasets = study.getRelatedBys();
	    if (datasets != null) {
	    	trialDataset = datasets.get(0).getSubjectProject();
	    	int lowest = Math.abs(trialDataset.getProjectId());
	        for (ProjectRelationship dataset : datasets) {
	        	Integer projectId = dataset.getSubjectProject().getProjectId();
	        	int id = Math.abs(projectId);
	        	if(id < lowest) {
    				lowest = id;
                    trialDataset = dataset.getSubjectProject();
                }
	        }
	    }
	    return trialDataset;
	}

	public Workbook buildCompleteDataset(int datasetId, boolean isTrial) throws MiddlewareQueryException {
		DataSet dataset = build(datasetId);
		List<Integer> siblingVariables = getVariablesOfSiblingDatasets(datasetId);
		boolean isMeasurementDataset = isMeasurementDataset(dataset);
		VariableTypeList variables = null;
		if (isMeasurementDataset) {
			variables = filterVariables(dataset.getVariableTypes(), siblingVariables);
		}
		else {
			variables = dataset.getVariableTypes();
		}
		variables = filterDatasetVariables(variables, !isTrial, isMeasurementDataset);
		long expCount = getStudyDataManager().countExperiments(datasetId);
		List<Experiment> experiments = getStudyDataManager().getExperiments(datasetId, 0, (int)expCount, variables);
		List<MeasurementVariable> factorList = getMeasurementVariableTransformer().transform(variables.getFactors(), true);
		List<MeasurementVariable> variateList = getMeasurementVariableTransformer().transform(variables.getVariates(), false, true);
		Workbook workbook = new Workbook();
		workbook.setObservations(getWorkbookBuilder().buildDatasetObservations(experiments, variables, factorList, variateList));
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
			for (VariableType variable : variables.getVariableTypes()) {
				boolean partOfHiddenDatasetColumns = HIDDEN_DATASET_COLUMNS.contains(variable.getId());
				boolean isOccAndNurseryAndMeasurementDataset = variable.getId() == TermId.TRIAL_INSTANCE_FACTOR.getId() && isNursery && isMeasurementDataset;
				boolean isMeasurementDatasetAndIsTrialFactors = isMeasurementDataset 
						&& PhenotypicType.TRIAL_ENVIRONMENT.getTypeStorages().contains(variable.getStandardVariable().getStoredIn().getId());
				boolean isTrialAndOcc = !isNursery && variable.getId() == TermId.TRIAL_INSTANCE_FACTOR.getId();
				if (!partOfHiddenDatasetColumns && !isOccAndNurseryAndMeasurementDataset && !isMeasurementDatasetAndIsTrialFactors || isTrialAndOcc) {
					newVariables.add(variable);
				}
			}
		}
		return newVariables;
	}
	
	private boolean isMeasurementDataset(DataSet dataset) {
		String datasetName = dataset.getName();
		DataSetType datasetType = dataset.getDataSetType();

		return datasetName.toUpperCase().startsWith("MEASUREMENT EFEC_") 
				|| datasetName.toUpperCase().startsWith("MEASUREMENT EFECT_")
				|| !datasetName.toUpperCase().startsWith("TRIAL_") && datasetType == DataSetType.PLOT_DATA;
	}
	
	private List<Integer> getVariablesOfSiblingDatasets(int datasetId) throws MiddlewareQueryException {
		setWorkingDatabase(datasetId);
		return getProjectPropertyDao().getVariablesOfSiblingDatasets(datasetId);
	}
	
	private VariableTypeList filterVariables(VariableTypeList variables, List<Integer> filters) {
		VariableTypeList newList = new VariableTypeList();
		if (variables != null && !variables.getVariableTypes().isEmpty()) {
			for (VariableType variable : variables.getVariableTypes()) {
				if (!filters.contains(variable.getId()) || variable.getId() == TermId.TRIAL_INSTANCE_FACTOR.getId()) {
					newList.add(variable);
				}
			}
		}
		return newList;
	}
	
	protected DmsProject getDmsProjectById(int studyId) throws MiddlewareQueryException {
		return getDmsProjectDao().getById(studyId);
	}
}
