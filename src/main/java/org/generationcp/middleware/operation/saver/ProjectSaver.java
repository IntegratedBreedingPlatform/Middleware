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

import org.generationcp.middleware.dao.StudyTypeDAO;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.domain.dms.StudyValues;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.util.Util;

import java.text.ParseException;

public class ProjectSaver extends Saver {

	public ProjectSaver(final HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public DmsProject save(final DmsProject project) {
		final DmsProjectDao projectDao = this.getDmsProjectDao();
		return projectDao.save(project);
	}

	public DmsProject create(final StudyValues studyValues, final StudyTypeDto studyType, final String description, final String startDate,
		final String endDate, final String objective, final String name, final String createdBy, final int parentId) throws ParseException {
		DmsProject project = null;

		if (studyValues != null) {
			final StudyTypeDAO studyTypeDAO = this.getStudyTypeDao();
			final DmsProject parentProject = this.getDmsProjectDao().getById(parentId);

			project = new DmsProject();
			project.setName(name);
			project.setStudyType(studyTypeDAO.getById(studyType.getId()));
			project.setCreatedBy(createdBy);
			project.setParent(parentProject);

			if (parentProject.getProjectId().intValue() != DmsProject.SYSTEM_FOLDER_ID) {
				project.setStudy(parentProject);
			}

			if (startDate != null && startDate.contains("-")) {
				project.setStartDate(Util.convertDate(startDate, Util.FRONTEND_DATE_FORMAT, Util.DATE_AS_NUMBER_FORMAT));
			} else {
				project.setStartDate(startDate);
			}
			project.setStudyUpdate(Util.getCurrentDateAsStringValue(Util.DATE_AS_NUMBER_FORMAT));

			if (endDate != null && endDate.contains("-")) {
				project.setEndDate(Util.convertDate(endDate, Util.FRONTEND_DATE_FORMAT, Util.DATE_AS_NUMBER_FORMAT));
			} else {
				project.setEndDate(endDate);
			}
			this.mapStudytoProject(name, description, project, objective);
		}

		return project;
	}

	private void mapStudytoProject(final String name, final String description, final DmsProject project, final String objective) {
		final StringBuffer errorMessage = new StringBuffer("");

		if (name != null && !name.equals("")) {
			project.setName(name);
		} else {
			errorMessage.append("\nname is null");
		}

		if (description != null && !description.equals("")) {
			project.setDescription(description);
		} else {
			errorMessage.append("\ndescription is null");
		}

		if (objective != null && !objective.equals("")) {
			project.setObjective(objective);
		}

		if (errorMessage.length() > 0) {
			throw new MiddlewareException(errorMessage.toString());
		}

	}

	/**
	 * Saves a folder. Creates an entry in project table
	 */
	public DmsProject saveFolder(final int parentId, final String name, final String description, final String programUUID, final String objective) throws Exception {
		DmsProject project = new DmsProject();
		project.setProgramUUID(programUUID);
		project.setParent(this.getDmsProjectDao().getById(parentId));
		this.mapStudytoProject(name, description, project, objective);

		try {
			project = this.save(project);
			this.getProjectRelationshipSaver().saveProjectParentRelationship(project, parentId, false);
		} catch (final Exception e) {
			throw e;
		}
		return project;

	}

}
