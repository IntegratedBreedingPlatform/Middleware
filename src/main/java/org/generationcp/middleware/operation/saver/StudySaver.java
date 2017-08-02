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
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.DmsProject;

/**
 * Saves a study (the corresponding Project, ProjectProperty, ProjectRelationship entries) to the database.
 *
 * @author Joyce Avestro
 *
 */
public class StudySaver extends Saver {

	public StudySaver(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	/**
	 * Saves a study. Creates an entry in project, projectprop and project_relationship tables (default) Creates an entry in nd_experiment
	 * and nd_experiment_project tables if saveStudyExperiment is true.
	 */
	public DmsProject saveStudy(int parentId, VariableTypeList variableTypeList, StudyValues studyValues, boolean saveStudyExperiment,
			final String programUUID, final String cropPrefix) throws Exception {
		DmsProject project = this.getProjectSaver().create(studyValues);
		project.setProgramUUID(programUUID);
		try {
			project = this.getProjectSaver().save(project);
//			this.getProjectPropertySaver().saveProjectProperties(project, variableTypeList);
			this.getProjectPropertySaver().saveProjectPropValues(project.getProjectId(), studyValues.getVariableList());
			this.getProjectRelationshipSaver().saveProjectParentRelationship(project, parentId, true);
			if (saveStudyExperiment) {
				this.saveStudyExperiment(project.getProjectId(), studyValues, cropPrefix);
			}
		} catch (Exception e) {
			throw e;
		}
		return project;

	}

	/**
	 * Creates an entry in nd_experiment and nd_experiment_project tables if saveStudyExperiment is true.
	 */
	public void saveStudyExperiment(final int projectId, final StudyValues values, final String cropPrefix) throws Exception {
		try {
			this.getExperimentModelSaver().addExperiment(projectId, ExperimentType.STUDY_INFORMATION, values, cropPrefix);
		} catch (Exception e) {
			throw e;
		}
	}

}
