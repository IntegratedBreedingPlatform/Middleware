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
import java.util.List;

import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DatasetValues;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ProjectRelationship;

public class DatasetProjectSaver extends Saver {

	public DatasetProjectSaver(final HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public DmsProject addDataSet(final int studyId, final VariableTypeList variableTypeList, final DatasetValues datasetValues, final String programUUID)
			throws MiddlewareException {
		final DmsProject datasetProject = new DmsProject();
		datasetProject.setName(this.getName(datasetValues));
		datasetProject.setDescription(this.getDescription(datasetValues));

		datasetProject.setProgramUUID(programUUID);

		this.addNameVariableTypeIfNecessary(variableTypeList, programUUID);
		this.addDescriptionVariableTypeIfNecessary(variableTypeList, programUUID);
		if (datasetValues.getType() != null) {
			final DMSVariableType variableType = this.addDataTypeVariableTypeIfNecessary(variableTypeList, programUUID);
			this.addDataTypeVariableIfNecessary(datasetValues, variableType);
		}

		datasetProject.setProperties(this.getProjectPropertySaver().create(datasetProject, variableTypeList, datasetValues.getVariables()));
		datasetProject.setRelatedTos(this.createProjectRelationship(studyId, datasetProject));
		this.getDmsProjectDao().save(datasetProject);

		return datasetProject;
	}


	public void updateDataSetName(final int projectId, final String name)
		throws MiddlewareException {
		final DmsProject datasetProject = this.getDmsProjectDao().getById(projectId);
		datasetProject.setName(name);
		datasetProject.setProjectId(projectId);
		datasetProject.setDescription(name);

		this.getDmsProjectDao().update(datasetProject);
	}

	private String getName(final DatasetValues datasetValues) {
		if (datasetValues.getName() != null) {
			return datasetValues.getName();
		}
		return this.getStringValue(datasetValues, TermId.DATASET_NAME.getId());
	}

	private String getDescription(final DatasetValues datasetValues) {
		if (datasetValues.getDescription() != null) {
			return datasetValues.getDescription();
		}
		return this.getStringValue(datasetValues, TermId.DATASET_TITLE.getId());
	}

	private void addDataTypeVariableIfNecessary(final DatasetValues datasetValues, final DMSVariableType variableType) {
		final VariableList variables = datasetValues.getVariables();
		if (variables == null || variables.findById(TermId.DATASET_TYPE) == null) {
			final Variable variable = new Variable(variableType, datasetValues.getType().getId());
			datasetValues.addVariable(variable);
		}
	}

	private void addNameVariableTypeIfNecessary(final VariableTypeList variableTypeList, final String programUUID) throws MiddlewareException {
		if (variableTypeList.findById(TermId.DATASET_NAME) == null) {
			variableTypeList.makeRoom(1);
			final DMSVariableType dataSetName =
					new DMSVariableType("DATASET_NAME", "Dataset name", this.getStandardVariable(TermId.DATASET_NAME, programUUID), 1);
			dataSetName.setRole(PhenotypicType.DATASET);
			variableTypeList.add(dataSetName);
		}
	}

	private void addDescriptionVariableTypeIfNecessary(final VariableTypeList variableTypeList, final String programUUID) throws MiddlewareException {
		if (variableTypeList.findById(TermId.DATASET_TITLE) == null) {
			variableTypeList.makeRoom(2);
			final DMSVariableType dataSetTitle =
					new DMSVariableType("DATASET_TITLE", "Dataset title", this.getStandardVariable(TermId.DATASET_TITLE, programUUID), 2);
			dataSetTitle.setRole(PhenotypicType.DATASET);
			variableTypeList.add(dataSetTitle);
		}
	}

	private DMSVariableType addDataTypeVariableTypeIfNecessary(final VariableTypeList variableTypeList, final String programUUID)
			throws MiddlewareException {
		DMSVariableType variableType = variableTypeList.findById(TermId.DATASET_TYPE);
		if (variableType == null) {
			variableType =
					new DMSVariableType("DATASET_TYPE", "Dataset type", this.getStandardVariable(TermId.DATASET_TYPE, programUUID), 3);
			variableType.setRole(PhenotypicType.DATASET);
			variableTypeList.makeRoom(3);
			variableTypeList.add(variableType);
		}
		return variableType;
	}

	private StandardVariable getStandardVariable(final TermId stdVarId, final String programUUID) throws MiddlewareException {
		return this.getStandardVariableBuilder().create(stdVarId.getId(),programUUID);
	}

	public void addDatasetVariableType(final int datasetId, final DMSVariableType variableType) throws MiddlewareQueryException {
		final DmsProject project = this.getDmsProjectDao().getById(datasetId);
		if (project != null) {
			this.getProjectPropertySaver().saveVariableType(project, variableType, null);
		}
	}

	private String getStringValue(final DatasetValues datasetValues, final int termId) {
		if (datasetValues.getVariables() != null) {
			final Variable variable = datasetValues.getVariables().findById(termId);
			if (variable != null) {
				return variable.getValue();
			}
		}
		return null;
	}

	private List<ProjectRelationship> createProjectRelationship(final int studyId, final DmsProject datasetProject) throws MiddlewareQueryException {
		final ProjectRelationship relationship = new ProjectRelationship();
		relationship.setSubjectProject(datasetProject);
		relationship.setObjectProject(this.getDmsProjectDao().getById(studyId));
		relationship.setTypeId(TermId.BELONGS_TO_STUDY.getId());

		final List<ProjectRelationship> relationships = new ArrayList<ProjectRelationship>();
		relationships.add(relationship);

		return relationships;
	}

}
