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

import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.StudyValues;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.DmsProject;

/**
 * Saves a study (the corresponding Project, ProjectProperty, ProjectRelationship entries) to the database.
 *
 * @author Joyce Avestro
 *
 */
public class StudySaver extends Saver {

	public StudySaver(final HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	/**
	 * Saves a study. Creates an entry in project, projectprop and project_relationship tables (default) Creates an entry in nd_experiment
	 * and nd_experiment_project tables if saveStudyExperiment is true.
	 */
	public DmsProject saveStudy(final int parentId, final VariableTypeList variableTypeList, final StudyValues studyValues, final boolean saveStudyExperiment,
		final String programUUID, final String cropPrefix, final StudyType studyType, final String description, final String startDate,
		final String endDate, final String objective, final String name, final String createdBy) throws Exception {

		DmsProject project = this.getProjectSaver().create(studyValues, studyType, description, startDate, endDate, objective, name,
			createdBy);

		project.setProgramUUID(programUUID);

		project = this.getProjectSaver().save(project);
		this.getProjectPropertySaver().saveProjectProperties(project, variableTypeList, studyValues.getVariableList());
		this.getProjectRelationshipSaver().saveProjectParentRelationship(project, parentId, true);
		if (saveStudyExperiment) {
			this.saveStudyExperiment(project.getProjectId(), studyValues, cropPrefix);
		}
		return project;

	}

	/**
	 * Creates an entry in nd_experiment table if saveStudyExperiment is true.
	 */
	public void saveStudyExperiment(final int projectId, final StudyValues values, final String cropPrefix) throws Exception {
		try {
			this.getExperimentModelSaver().addExperiment(projectId, ExperimentType.STUDY_INFORMATION, values, cropPrefix);
		} catch (final Exception e) {
			throw e;
		}
	}

}
