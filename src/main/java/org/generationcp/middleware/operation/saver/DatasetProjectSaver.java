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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.domain.dms.DatasetValues;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.dms.ProjectRelationship;
import org.hibernate.Hibernate;

public class DatasetProjectSaver extends Saver {

	public DatasetProjectSaver(HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public DmsProject addDataSet(int studyId, VariableTypeList variableTypeList, DatasetValues datasetValues) throws MiddlewareQueryException {
		setWorkingDatabase(Database.LOCAL);
		
		DmsProject datasetProject = new DmsProject();
		
		datasetProject.setProjectId(getDmsProjectDao().getNegativeId("projectId"));
		datasetProject.setName(getName(datasetValues));
		datasetProject.setDescription(getDescription(datasetValues));
		
		addNameVariableTypeIfNecessary(variableTypeList);
		addDescriptionVariableTypeIfNecessary(variableTypeList);
		if (datasetValues.getType() != null) {
			VariableType variableType = addDataTypeVariableTypeIfNecessary(variableTypeList);
			addDataTypeVariableIfNecessary(datasetValues, variableType);
		}
	
		datasetProject.setProperties(getProjectPropertySaver().create(datasetProject, variableTypeList));
	
		datasetProject.setRelatedTos(createProjectRelationship(studyId, datasetProject));

		getDmsProjectDao().save(datasetProject);
		
		getProjectPropertySaver().saveProjectPropValues(datasetProject.getProjectId(), datasetValues.getVariables());
	
		return datasetProject;
	}
	
	private String getName(DatasetValues datasetValues) {
		if (datasetValues.getName() != null) return datasetValues.getName();
		return getStringValue(datasetValues, TermId.DATASET_NAME.getId());
	}

	private String getDescription(DatasetValues datasetValues) {
		if (datasetValues.getDescription() != null) return datasetValues.getDescription();
		return getStringValue(datasetValues, TermId.DATASET_TITLE.getId());
	}

	private void addDataTypeVariableIfNecessary(DatasetValues datasetValues, VariableType variableType) {
		VariableList variables = datasetValues.getVariables();
		if (variables == null || variables.findById(TermId.DATASET_TYPE) == null) {
		    Variable variable = new Variable(variableType, datasetValues.getType().getId());
		    datasetValues.addVariable(variable);
		}
	}

	private void addNameVariableTypeIfNecessary(VariableTypeList variableTypeList) throws MiddlewareQueryException {
		if (variableTypeList.findById(TermId.DATASET_NAME) == null) {
			variableTypeList.makeRoom(1);
			variableTypeList.add(new VariableType("DATASET_NAME", "Dataset name", getStandardVariable(TermId.DATASET_NAME), 1));
		}
	}
	
	private void addDescriptionVariableTypeIfNecessary(VariableTypeList variableTypeList) throws MiddlewareQueryException {
		if (variableTypeList.findById(TermId.DATASET_TITLE) == null) {
			variableTypeList.makeRoom(2);
			variableTypeList.add(new VariableType("DATASET_TITLE", "Dataset title", getStandardVariable(TermId.DATASET_TITLE), 2));
		}
	}
	
	private VariableType addDataTypeVariableTypeIfNecessary(VariableTypeList variableTypeList) throws MiddlewareQueryException {
		VariableType variableType = variableTypeList.findById(TermId.DATASET_TYPE);
		if (variableType == null) {
			variableType = new VariableType("DATASET_TYPE", "Dataset type", getStandardVariable(TermId.DATASET_TYPE), 3);
			variableTypeList.makeRoom(3);
			variableTypeList.add(variableType);
		}
		return variableType;
	}

	private StandardVariable getStandardVariable(TermId stdVarId) throws MiddlewareQueryException {
		return this.getStandardVariableBuilder().create(stdVarId.getId());
	}

	public void addDatasetVariableType(int datasetId, VariableType variableType) throws MiddlewareQueryException {
		if (this.setWorkingDatabase(datasetId)) {
			DmsProject project = getDmsProjectDao().getById(datasetId);
			if (project != null) {
				getProjectPropertySaver().saveVariableType(project, variableType);
			}
		}
	}
	
	private String getStringValue(DatasetValues datasetValues, int termId) {
		if (datasetValues.getVariables() != null) {
			Variable variable = datasetValues.getVariables().findById(termId);
			if (variable != null) {
				return variable.getValue();
			}
		}
		return null;
	}
	
	private List<ProjectRelationship> createProjectRelationship(int studyId, DmsProject datasetProject) throws MiddlewareQueryException {
		ProjectRelationship relationship = new ProjectRelationship();
		
		relationship.setProjectRelationshipId(getProjectRelationshipDao().getNegativeId("projectRelationshipId"));
		relationship.setSubjectProject(datasetProject);
		relationship.setObjectProject(getDmsProjectDao().getById(studyId));
		relationship.setTypeId(TermId.BELONGS_TO_STUDY.getId());
		
		List<ProjectRelationship> relationships = new ArrayList<ProjectRelationship>();
		relationships.add(relationship);
		
		return relationships;
	}
	
	public void addPropertiesIfNotExisting(int datasetId, VariableTypeList variableTypeList) throws MiddlewareQueryException {
		setWorkingDatabase(Database.LOCAL);
		
		DmsProject datasetProject = getDmsProjectDao().getById(datasetId);
		Hibernate.initialize(datasetProject.getProperties());
		Map<Integer, ProjectProperty> existingPropertiesMap = new HashMap<Integer, ProjectProperty>();
		for (ProjectProperty property : datasetProject.getProperties()) {
			existingPropertiesMap.put(property.getProjectPropertyId(), property);
		}		
		VariableTypeList additionalProperties = new VariableTypeList();
		for(VariableType variableType: variableTypeList.getVariableTypes()){
			if(!existingPropertiesMap.containsKey(variableType.getId())) {
				additionalProperties.add(variableType);
			}
		}
		getProjectPropertySaver().saveProjectProperties(datasetProject, additionalProperties);
	}
}
