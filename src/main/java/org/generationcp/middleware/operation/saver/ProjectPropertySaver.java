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
package org.generationcp.middleware.operation.saver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.hibernate.Session;

public class ProjectPropertySaver extends Saver {

	protected static final String PROJECT_PROPERTY_ID = "projectPropertyId";
	
	public ProjectPropertySaver(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}
	
	public List<ProjectProperty> create(DmsProject project, VariableTypeList variableTypeList) throws MiddlewareQueryException {
		List<ProjectProperty> properties = new ArrayList<ProjectProperty>();
		List<VariableType> variableTypes = variableTypeList != null ? variableTypeList.getVariableTypes() : null;
		
		if (variableTypes != null && !variableTypes.isEmpty()) {
			int index = getProjectPropertyDao().getNextId(PROJECT_PROPERTY_ID);
			for (VariableType variableType : variableTypes) {
				List<ProjectProperty> list = createVariableProperties(index, project, variableType);
				properties.addAll(list);
				index = index + list.size();
			}
		}
		return properties;
	}
	
	public void saveProjectProperties(DmsProject project, VariableTypeList variableTypeList) throws MiddlewareQueryException {
		List<ProjectProperty> properties = create(project, variableTypeList);
		
		Integer generatedId;
		ProjectPropertyDao projectPropertyDao = getProjectPropertyDao();
        for (ProjectProperty property : properties){
            generatedId = projectPropertyDao.getNextId(PROJECT_PROPERTY_ID);
            //kim
            //property.setProjectPropertyId(generatedId);
            property.setProject(project);
            projectPropertyDao.save(property);
        }
        
		project.setProperties(properties);
	}

	private List<ProjectProperty> createVariableProperties(int startIndex, DmsProject project, VariableType variableType) throws MiddlewareQueryException {
		List<ProjectProperty> properties = new ArrayList<ProjectProperty>();
		int index = startIndex;
		properties.add(new ProjectProperty(index++, project, variableType.getStandardVariable().getStoredIn().getId(), variableType.getLocalName(), variableType.getRank()));
		properties.add(new ProjectProperty(index++, project, TermId.VARIABLE_DESCRIPTION.getId(), variableType.getLocalDescription(), variableType.getRank()));
		properties.add(new ProjectProperty(index++, project, TermId.STANDARD_VARIABLE.getId(), String.valueOf(variableType.getId()), variableType.getRank()));
		
		if (variableType.getTreatmentLabel() != null && !"".equals(variableType.getTreatmentLabel())) {
			properties.add(new ProjectProperty(index++, project, TermId.MULTIFACTORIAL_INFO.getId(), variableType.getTreatmentLabel(), variableType.getRank()));
		}
		
		return properties;
	}
	
	public void saveProjectPropValues(int projectId, VariableList variableList) throws MiddlewareQueryException {
		if (variableList != null && variableList.getVariables() != null && !variableList.getVariables().isEmpty()) {
			for (Variable variable : variableList.getVariables()) {
				int storedInId = variable.getVariableType().getStandardVariable().getStoredIn().getId();
				if (TermId.STUDY_INFO_STORAGE.getId() == storedInId
				|| TermId.DATASET_INFO_STORAGE.getId() == storedInId) {
					ProjectProperty property = new ProjectProperty();
					//kim
					//property.setProjectPropertyId(getProjectPropertyDao().getNextId(PROJECT_PROPERTY_ID));
					property.setTypeId(variable.getVariableType().getStandardVariable().getId());
					property.setValue(variable.getValue());
					property.setRank(variable.getVariableType().getRank());
					property.setProject(getDmsProjectDao().getById(projectId));
					getProjectPropertyDao().save(property);
				}
			}
		}
	}
	
	public void saveVariableType(DmsProject project, VariableType variableType) throws MiddlewareQueryException {
		saveProjectProperty(project, variableType.getStandardVariable().getStoredIn().getId(), variableType.getLocalName(), variableType.getRank());
		saveProjectProperty(project, TermId.VARIABLE_DESCRIPTION.getId(), variableType.getLocalDescription(), variableType.getRank());
		saveProjectProperty(project, TermId.STANDARD_VARIABLE.getId(), Integer.toString(variableType.getStandardVariable().getId()), variableType.getRank());
		if (variableType.getTreatmentLabel() != null && !variableType.getTreatmentLabel().isEmpty()) {
			saveProjectProperty(project, TermId.MULTIFACTORIAL_INFO.getId(), variableType.getTreatmentLabel(), variableType.getRank());
		}
	}
	
	private void saveProjectProperty(DmsProject project, int typeId, String value, int rank) throws MiddlewareQueryException {
		ProjectProperty property = new ProjectProperty();
		//kim
		//property.setProjectPropertyId(getProjectPropertyDao().getNextId(PROJECT_PROPERTY_ID));
		property.setTypeId(typeId);
		property.setValue(value);
		property.setRank(rank);
		property.setProject(project);
		getProjectPropertyDao().save(property);
		project.addProperty(property);
	}
	
	public void createProjectPropertyIfNecessary(DmsProject project, TermId termId, int storedIn) throws MiddlewareQueryException {
        ProjectProperty property = getProjectPropertyDao().getByStandardVariableId(project, termId.getId());
        if (property == null) {
            int rank = getProjectPropertyDao().getNextRank(project.getProjectId());
            StandardVariable stdvar = new StandardVariable();
            stdvar.setId(termId.getId());
            stdvar.setStoredIn(new Term(storedIn, null, null));
            VariableType variableType = new VariableType(termId.toString(), termId.toString(), stdvar, rank);
            saveVariableType(project, variableType);
        }
	}
	
	public void saveProjectProperties(DmsProject study, DmsProject trialDataset, DmsProject measurementDataset, 
			List<MeasurementVariable> variables, boolean isConstant) throws MiddlewareQueryException {
		
		if (variables != null) {
			int rank = getNextRank(study);
			Set<Integer> geoIds = getGeolocationDao().getLocationIds(study.getProjectId());
			Geolocation geolocation = getGeolocationDao().getById(geoIds.iterator().next()); 
			for (MeasurementVariable variable : variables) {
				if (variable.getOperation() == Operation.ADD) {
					insertVariable(study, trialDataset, measurementDataset, variable, rank, isConstant, geolocation);
					rank++;
				} else if (variable.getOperation() == Operation.UPDATE) {
					if (variable.getTermId() != TermId.TRIAL_INSTANCE_FACTOR.getId()) {
						updateVariable(study, trialDataset, measurementDataset, variable, isConstant, geolocation);
					}
				} else if (variable.getOperation() == Operation.DELETE) {
					deleteVariable(study, trialDataset, measurementDataset, variable.getStoredIn(), variable.getTermId(), geolocation);
				}
			}
		}
	}
	
	private int getNextRank(DmsProject project) {
		int nextRank = 1;
		if (project.getProperties() != null) {
			for (ProjectProperty property : project.getProperties()) {
				if (property.getRank() >= nextRank) {
					nextRank = property.getRank() + 1;
				}
			}
		}
		return nextRank;
	}
	private void insertVariable(DmsProject project, DmsProject trialDataset, DmsProject measurementDataset, 
			MeasurementVariable variable, int rank, boolean isConstant, Geolocation geolocation) throws MiddlewareQueryException {
		
		if (PhenotypicType.TRIAL_ENVIRONMENT.getTypeStorages().contains(variable.getStoredIn())) {
			int datasetRank = getNextRank(trialDataset);
			int measurementRank = getNextRank(measurementDataset);
			
			insertVariable(trialDataset, variable, datasetRank);
			
			//GCP-9959
			if (variable.getTermId() == TermId.TRIAL_INSTANCE_FACTOR.getId()) {
				insertVariable(measurementDataset, variable, measurementRank);
			}
			
			if (variable.getStoredIn() == TermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId()) {
				getGeolocationPropertySaver().saveOrUpdate(geolocation, variable.getTermId(), variable.getValue());
			} else {    
				getGeolocationSaver().setGeolocation(geolocation, variable.getTermId(), variable.getStoredIn(), variable.getValue());
				getGeolocationDao().saveOrUpdate(geolocation);
			}
		} else if (variable.getStoredIn() == TermId.OBSERVATION_VARIATE.getId()
				|| variable.getStoredIn() == TermId.CATEGORICAL_VARIATE.getId()) {
			
			if (isConstant) {
				if (PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().contains(variable.getLabel())) { 
					//a trial constant
					int datasetRank = getNextRank(trialDataset);
					insertVariable(trialDataset, variable, datasetRank);
					getPhenotypeSaver().saveOrUpdatePhenotypeValue(trialDataset.getProjectId(), variable.getTermId(), variable.getStoredIn(), variable.getValue());
				} else { 
					// a study constant
					insertVariable(project, variable, rank);
					getPhenotypeSaver().saveOrUpdatePhenotypeValue(project.getProjectId(), variable.getTermId(), variable.getStoredIn(), variable.getValue());
				}
			} else {
				int measurementRank = getNextRank(measurementDataset);
				insertVariable(measurementDataset, variable, measurementRank);
			}
		} else { 
			//study
			insertVariable(project, variable, rank);
			VariableList variableList = new VariableList();
			variableList.add(new Variable(createVariableType(variable, rank), variable.getValue()));
			saveProjectPropValues(project.getProjectId(), variableList);
		}
	}
	private void insertVariable(DmsProject project, MeasurementVariable variable, int rank) throws MiddlewareQueryException {
		if (project.getProperties() == null) {
			project.setProperties(new ArrayList<ProjectProperty>());
		}
		saveVariableType(project, createVariableType(variable, rank));
	}
	
	private VariableType createVariableType(MeasurementVariable variable, int rank) {
		VariableType varType = new VariableType();
		StandardVariable stdvar = new StandardVariable();
		varType.setStandardVariable(stdvar);

		stdvar.setId(variable.getTermId());
		stdvar.setStoredIn(new Term(variable.getStoredIn(), null, null));
		
		varType.setLocalName(variable.getName());
		varType.setLocalDescription(variable.getDescription());
		varType.setRank(rank);
		
		if (variable.getTreatmentLabel() != null && !variable.getTreatmentLabel().isEmpty()) {
			varType.setTreatmentLabel(variable.getTreatmentLabel());
		}
		return varType;
	}
	
	private void updateVariable(DmsProject project, DmsProject trialDataset, DmsProject measurementDataset, 
			MeasurementVariable variable, boolean isConstant, Geolocation geolocation) throws MiddlewareQueryException {
		
		if (PhenotypicType.TRIAL_ENVIRONMENT.getTypeStorages().contains(variable.getStoredIn())) {
			updateVariable(project, variable);
			updateVariable(trialDataset, variable);
			updateVariable(measurementDataset, variable);
			if (variable.getStoredIn() == TermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId()) {
				getGeolocationPropertySaver().saveOrUpdate(geolocation, variable.getTermId(), variable.getValue());
			} else {
				getGeolocationSaver().setGeolocation(geolocation, variable.getTermId(), variable.getStoredIn(), variable.getValue());
				getGeolocationDao().saveOrUpdate(geolocation);
			}
		} else if (variable.getStoredIn() == TermId.OBSERVATION_VARIATE.getId()
				|| variable.getStoredIn() == TermId.CATEGORICAL_VARIATE.getId()) {
			
			if (isConstant) {
				if (PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().contains(variable.getLabel())) { 
					//a trial constant
					updateVariable(trialDataset, variable);
					updateVariable(measurementDataset, variable);
					getPhenotypeSaver().saveOrUpdatePhenotypeValue(trialDataset.getProjectId(), variable.getTermId(), variable.getStoredIn(), variable.getValue());
				} else { 
					//a study constant
					updateVariable(project, variable);
					getPhenotypeSaver().saveOrUpdatePhenotypeValue(project.getProjectId(), variable.getTermId(), variable.getStoredIn(), variable.getValue());
				}
			} else {
				updateVariable(measurementDataset, variable);
			}
		} else { 
			//study
			updateVariable(project, variable);
			if (variable.getStoredIn() == TermId.STUDY_NAME_STORAGE.getId()) {
				project.setName(variable.getValue());
				getDmsProjectDao().merge(project);

			} else if (variable.getStoredIn() == TermId.STUDY_TITLE_STORAGE.getId()) {
				project.setDescription(variable.getValue());
				getDmsProjectDao().merge(project);

			}
		}
	}
	private void updateVariable(DmsProject project, MeasurementVariable variable) throws MiddlewareQueryException {
		if (project.getProperties() != null) {
			List<Integer> allTypeStorages = PhenotypicType.getAllTypeStorages();
			int rank = getRank(project, variable.getTermId());
			for (ProjectProperty property : project.getProperties()) {
				if (rank == property.getRank()) {
					if (property.getTypeId().intValue() == TermId.VARIABLE_DESCRIPTION.getId()) {
						property.setValue(variable.getDescription());
					} else if (property.getTypeId().intValue() == variable.getTermId()) {
						property.setValue(variable.getValue());
					} else if (allTypeStorages.contains(property.getTypeId().intValue())) {
						property.setValue(variable.getName());
					}
					getProjectPropertyDao().update(property);
				}
			}
		}
	}
	
	private int getRank(DmsProject project, int termId) {
		int rank = -1;
		if (project.getProperties() != null) {
			for (ProjectProperty property : project.getProperties()) {
				if (property.getTypeId().intValue() == TermId.STANDARD_VARIABLE.getId()
						&& property.getValue().equals(String.valueOf(termId))) {
					rank = property.getRank();
					break;
				}
			}
		}
		return rank;
	}
	
	private void deleteVariable(DmsProject project, DmsProject trialDataset, DmsProject measurementDataset, 
			int storedInId, int termId, Geolocation geolocation) throws MiddlewareQueryException {
		
		Session session = getCurrentSession();
		deleteVariable(project, termId);
		if (PhenotypicType.TRIAL_ENVIRONMENT.getTypeStorages().contains(storedInId)) {
			deleteVariable(trialDataset, termId);
			deleteVariable(measurementDataset, termId);
			session.flush();
            session.clear();
			if (storedInId == TermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId()) {
				getGeolocationPropertyDao().deleteGeolocationPropertyValueInProject(project.getProjectId(), termId);
			} else {
				getGeolocationSaver().setGeolocation(geolocation, termId, storedInId, null);
				getGeolocationDao().saveOrUpdate(geolocation);
			}
		} else if (storedInId == TermId.OBSERVATION_VARIATE.getId()
				|| storedInId == TermId.CATEGORICAL_VARIATE.getId()) {
			//for constants
			deleteVariable(project, termId); 
			deleteVariable(trialDataset, termId);
			
			//for variates
			deleteVariable(measurementDataset, termId); 
			//remove phoenotype value
			List<Integer> ids = Arrays.asList(project.getProjectId(), trialDataset.getProjectId(), measurementDataset.getProjectId());
			getPhenotypeDao().deletePhenotypesInProjectByTerm(ids, termId);
		}
	}
	
	private void deleteVariable(DmsProject project, int termId) throws MiddlewareQueryException {
		int rank = getRank(project, termId);
		if (project.getProperties() != null && !project.getProperties().isEmpty()) {
			for (Iterator<ProjectProperty> iterator = project.getProperties().iterator(); iterator.hasNext();) {
				ProjectProperty property = iterator.next(); 
				if (rank == property.getRank()) {
					getProjectPropertyDao().makeTransient(property);
					iterator.remove();
				}
			}
		}
	}
	
	private void deleteVariableForFactors(DmsProject project, MeasurementVariable variable) throws MiddlewareQueryException {
		deleteVariable(project, variable.getTermId());
		
		if (variable.getStoredIn() == TermId.TRIAL_DESIGN_INFO_STORAGE.getId()) {
			getExperimentPropertyDao().deleteExperimentPropInProjectByTermId(project.getProjectId(), variable.getTermId());
		} else if (variable.getStoredIn() == TermId.GERMPLASM_ENTRY_STORAGE.getId()) {
			getStockPropertyDao().deleteStockPropInProjectByTermId(project.getProjectId(), variable.getTermId());
		}
	}
	
	public void saveFactors(DmsProject measurementDataset, List<MeasurementVariable> variables) throws MiddlewareQueryException {
		if (variables != null && !variables.isEmpty()) {
			for (MeasurementVariable variable : variables) {
				if (variable.getOperation() == Operation.ADD) {
					int measurementRank = getNextRank(measurementDataset);
					insertVariable(measurementDataset, variable, measurementRank);
				} else if (variable.getOperation() == Operation.DELETE) {
					deleteVariableForFactors(measurementDataset, variable);
				} 
				// update operation is not allowed with factors
			}
		}
	}
	
	public void updateVariablesRanking(int datasetId, List<Integer> variableIds) throws MiddlewareQueryException{
		int rank = getProjectPropertyDao().getNextRank(datasetId);
		Map<Integer, List<Integer>> projectPropIDMap = getProjectPropertyDao().getProjectPropertyIDsPerVariableId(datasetId);
		rank = updateVariableRank(variableIds, rank, projectPropIDMap);
		
		// if any factors were added but not included in list of variables, update their ranks also so they come last
		List<Integer> storedInIds = new ArrayList<Integer>();
		storedInIds.addAll(PhenotypicType.GERMPLASM.getTypeStorages());
		storedInIds.addAll(PhenotypicType.TRIAL_DESIGN.getTypeStorages());
		storedInIds.addAll(PhenotypicType.VARIATE.getTypeStorages());

		List<Integer> germplasmPlotVariateIds = getProjectPropertyDao().getDatasetVariableIdsForGivenStoredInIds(datasetId, storedInIds, variableIds);
		updateVariableRank(germplasmPlotVariateIds, rank, projectPropIDMap);
	}

	//Iterate and update rank, exclude deleted variables
	private int updateVariableRank(List<Integer> variableIds, int startRank,
			Map<Integer, List<Integer>> projectPropIDMap) {
		int rank = startRank;
		for (Integer variableId : variableIds){
			List<Integer> projectPropIds = projectPropIDMap.get(variableId);
			if (projectPropIds != null) {
				getProjectPropertyDao().updateRank(projectPropIds, rank);
				rank++;
			}
		}
		return rank;
	}
	
}
