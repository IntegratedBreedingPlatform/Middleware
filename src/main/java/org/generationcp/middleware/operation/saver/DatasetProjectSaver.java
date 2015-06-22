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
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.dms.ProjectRelationship;
import org.hibernate.Hibernate;

public class DatasetProjectSaver extends Saver {

	public DatasetProjectSaver(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public DmsProject addDataSet(int studyId, VariableTypeList variableTypeList, DatasetValues datasetValues, String programUUID)
			throws MiddlewareException {
		DmsProject datasetProject = new DmsProject();
		datasetProject.setProjectId(this.getDmsProjectDao().getNextId("projectId"));
		datasetProject.setName(this.getName(datasetValues));
		datasetProject.setDescription(this.getDescription(datasetValues));
		datasetProject.setProgramUUID(programUUID);

		this.addNameVariableTypeIfNecessary(variableTypeList,programUUID);
		this.addDescriptionVariableTypeIfNecessary(variableTypeList,programUUID);
		if (datasetValues.getType() != null) {
			VariableType variableType = this.addDataTypeVariableTypeIfNecessary(variableTypeList,programUUID);
			this.addDataTypeVariableIfNecessary(datasetValues, variableType);
		}

		datasetProject.setProperties(this.getProjectPropertySaver().create(datasetProject, variableTypeList));
		datasetProject.setRelatedTos(this.createProjectRelationship(studyId, datasetProject));
		this.getDmsProjectDao().save(datasetProject);
		this.getProjectPropertySaver().saveProjectPropValues(datasetProject.getProjectId(), datasetValues.getVariables());

		return datasetProject;
	}

	private String getName(DatasetValues datasetValues) {
		if (datasetValues.getName() != null) {
			return datasetValues.getName();
		}
		return this.getStringValue(datasetValues, TermId.DATASET_NAME.getId());
	}

	private String getDescription(DatasetValues datasetValues) {
		if (datasetValues.getDescription() != null) {
			return datasetValues.getDescription();
		}
		return this.getStringValue(datasetValues, TermId.DATASET_TITLE.getId());
	}

	private void addDataTypeVariableIfNecessary(DatasetValues datasetValues, VariableType variableType) {
		VariableList variables = datasetValues.getVariables();
		if (variables == null || variables.findById(TermId.DATASET_TYPE) == null) {
			Variable variable = new Variable(variableType, datasetValues.getType().getId());
			datasetValues.addVariable(variable);
		}
	}

	private void addNameVariableTypeIfNecessary(VariableTypeList variableTypeList,String programUUID) throws MiddlewareException {
		if (variableTypeList.findById(TermId.DATASET_NAME) == null) {
			variableTypeList.makeRoom(1);
			variableTypeList.add(new VariableType("DATASET_NAME", "Dataset name", this.getStandardVariable(TermId.DATASET_NAME,programUUID), 1));
		}
	}

	private void addDescriptionVariableTypeIfNecessary(VariableTypeList variableTypeList,String programUUID) throws MiddlewareException {
		if (variableTypeList.findById(TermId.DATASET_TITLE) == null) {
			variableTypeList.makeRoom(2);
			variableTypeList.add(new VariableType("DATASET_TITLE", "Dataset title", this.getStandardVariable(TermId.DATASET_TITLE,programUUID), 2));
		}
	}

	private VariableType addDataTypeVariableTypeIfNecessary(VariableTypeList variableTypeList,String programUUID) throws MiddlewareException {
		VariableType variableType = variableTypeList.findById(TermId.DATASET_TYPE);
		if (variableType == null) {
			variableType = new VariableType("DATASET_TYPE", "Dataset type", this.getStandardVariable(TermId.DATASET_TYPE,programUUID), 3);
			variableTypeList.makeRoom(3);
			variableTypeList.add(variableType);
		}
		return variableType;
	}

	private StandardVariable getStandardVariable(TermId stdVarId, String programUUID) throws MiddlewareException {
		return this.getStandardVariableBuilder().create(stdVarId.getId(),programUUID);
	}

	public void addDatasetVariableType(int datasetId, VariableType variableType) throws MiddlewareQueryException {
		DmsProject project = this.getDmsProjectDao().getById(datasetId);
		if (project != null) {
			this.getProjectPropertySaver().saveVariableType(project, variableType);
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

		relationship.setProjectRelationshipId(this.getProjectRelationshipDao().getNextId("projectRelationshipId"));
		relationship.setSubjectProject(datasetProject);
		relationship.setObjectProject(this.getDmsProjectDao().getById(studyId));
		relationship.setTypeId(TermId.BELONGS_TO_STUDY.getId());

		List<ProjectRelationship> relationships = new ArrayList<ProjectRelationship>();
		relationships.add(relationship);

		return relationships;
	}

	public void addPropertiesIfNotExisting(int datasetId, VariableTypeList variableTypeList) throws MiddlewareQueryException {
		DmsProject datasetProject = this.getDmsProjectDao().getById(datasetId);
		Hibernate.initialize(datasetProject.getProperties());
		Map<Integer, ProjectProperty> existingPropertiesMap = new HashMap<Integer, ProjectProperty>();
		for (ProjectProperty property : datasetProject.getProperties()) {
			existingPropertiesMap.put(property.getProjectPropertyId(), property);
		}
		VariableTypeList additionalProperties = new VariableTypeList();
		for (VariableType variableType : variableTypeList.getVariableTypes()) {
			if (!existingPropertiesMap.containsKey(variableType.getId())) {
				additionalProperties.add(variableType);
			}
		}
		this.getProjectPropertySaver().saveProjectProperties(datasetProject, additionalProperties);
	}
}
