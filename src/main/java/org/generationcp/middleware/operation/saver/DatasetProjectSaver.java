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

import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DatasetValues;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.operation.builder.StandardVariableBuilder;
import org.generationcp.middleware.pojos.dms.DatasetType;
import org.generationcp.middleware.pojos.dms.DmsProject;

public class DatasetProjectSaver {

	private DaoFactory daoFactory;

	private ProjectPropertySaver projectPropertySaver;

	private StandardVariableBuilder standardVariableBuilder;

	public DatasetProjectSaver() {
		// for unit testing purpose
	}

	public DatasetProjectSaver(final HibernateSessionProvider sessionProvider) {
		this.projectPropertySaver = new ProjectPropertySaver(sessionProvider);
		this.standardVariableBuilder = new StandardVariableBuilder(sessionProvider);
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	public DmsProject addDataSet(
		final int studyId, final VariableTypeList variableTypeList, final DatasetValues datasetValues, final String programUUID,
		final int datasetTypeId) {
		final DmsProject datasetProject = new DmsProject();
		datasetProject.setName(this.getName(datasetValues));
		datasetProject.setDescription(this.getDescription(datasetValues));
		datasetProject.setDatasetType(new DatasetType(datasetTypeId));
		datasetProject.setProgramUUID(programUUID);

		this.addNameVariableTypeIfNecessary(variableTypeList, programUUID);
		this.addDescriptionVariableTypeIfNecessary(variableTypeList, programUUID);

		datasetProject.setProperties(this.projectPropertySaver.create(datasetProject, variableTypeList, datasetValues.getVariables()));
		final DmsProject study = this.daoFactory.getDmsProjectDAO().getById(studyId);
		datasetProject.setParent(study);
		datasetProject.setStudy(study);
		this.daoFactory.getDmsProjectDAO().save(datasetProject);

		return datasetProject;
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

	private void addNameVariableTypeIfNecessary(final VariableTypeList variableTypeList, final String programUUID) {
		if (variableTypeList.findById(TermId.DATASET_NAME) == null) {
			variableTypeList.makeRoom(1);
			final DMSVariableType dataSetName =
				new DMSVariableType("DATASET_NAME", "Dataset name", this.getStandardVariable(TermId.DATASET_NAME, programUUID), 1);
			dataSetName.setRole(PhenotypicType.DATASET);
			variableTypeList.add(dataSetName);
		}
	}

	private void addDescriptionVariableTypeIfNecessary(final VariableTypeList variableTypeList, final String programUUID) {
		if (variableTypeList.findById(TermId.DATASET_TITLE) == null) {
			variableTypeList.makeRoom(2);
			final DMSVariableType dataSetTitle =
				new DMSVariableType("DATASET_TITLE", "Dataset title", this.getStandardVariable(TermId.DATASET_TITLE, programUUID), 2);
			dataSetTitle.setRole(PhenotypicType.DATASET);
			variableTypeList.add(dataSetTitle);
		}
	}

	private StandardVariable getStandardVariable(final TermId stdVarId, final String programUUID) {
		return this.standardVariableBuilder.create(stdVarId.getId(), programUUID);
	}

	public void addDatasetVariableType(final int datasetId, final DMSVariableType variableType) {
		final DmsProject project = this.daoFactory.getDmsProjectDAO().getById(datasetId);
		if (project != null) {
			this.projectPropertySaver.saveVariableType(project, variableType, null);
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

}
