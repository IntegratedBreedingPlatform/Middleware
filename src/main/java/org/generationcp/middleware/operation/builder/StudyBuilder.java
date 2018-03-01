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
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StudyBuilder extends Builder {

	private static final Logger LOG = LoggerFactory.getLogger(StudyBuilder.class);
	
	public StudyBuilder(final HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public Study createStudy(final int studyId) throws MiddlewareException {
		Study study = null;
		final DmsProject project = this.getDmsProjectDao().getById(studyId);
		if (project != null) {
			study = this.createStudy(project);
		}
		return study;
	}

	public Study createStudy(final int studyId, final boolean hasVariabletype) throws MiddlewareException {
		final Monitor monitor = MonitorFactory.start("Build Study");
		try {
			Study study = null;
			final DmsProject project = this.getDmsProjectDao().getById(studyId);
			if (project != null) {
				study = this.createStudy(project, hasVariabletype);
			}
			return study;
		} finally {
			LOG.debug("" + monitor.stop());
		}
	}

	public Study createStudy(final DmsProject project) throws MiddlewareException {
		final Study study = new Study();
		study.setId(project.getProjectId());
		study.setProgramUUID(project.getProgramUUID());
		study.setStudyType(project.getStudyType());
		study.setDescription(project.getDescription());
		study.setStartDate(project.getStartDate());
		study.setEndDate(project.getEndDate());
		study.setStudyUpdate(project.getStudyUpdate());
		study.setObjective(project.getObjective());
		study.setName(project.getName());
		study.setCreatedBy(project.getCreatedBy());

		final VariableTypeList variableTypes = this.getVariableTypeBuilder().create(project.getProperties(),
				project.getProgramUUID());
		final VariableTypeList conditionVariableTypes = variableTypes.getFactors();
		final VariableTypeList constantVariableTypes = variableTypes.getVariates();

		final Experiment experiment = this.getExperimentBuilder().buildOne(project.getProjectId(), TermId.STUDY_EXPERIMENT, variableTypes);

		study.setConditions(this.getStudyVariableBuilder().create(project, experiment, conditionVariableTypes));
		study.setConstants(this.getStudyVariableBuilder().create(project, experiment, constantVariableTypes));

		return study;
	}

	public Study createStudy(final DmsProject project, final boolean hasVariableType) throws MiddlewareException {
		final Study study = new Study();
		study.setId(project.getProjectId());
		study.setProgramUUID(project.getProgramUUID());
		study.setStudyType(project.getStudyType());
		study.setDescription(project.getDescription());
		study.setStartDate(project.getStartDate());
		study.setEndDate(project.getEndDate());
		study.setStudyUpdate(project.getStudyUpdate());
		study.setCreatedBy(project.getCreatedBy());
		study.setName(project.getName());

		final VariableTypeList variableTypes = this.getVariableTypeBuilder().create(
				project.getProperties(),project.getProgramUUID());
		final VariableTypeList conditionVariableTypes = variableTypes.getFactors();
		final VariableTypeList constantVariableTypes = variableTypes.getVariates();

		final Experiment experiment =
				this.getExperimentBuilder().buildOne(project.getProjectId(), TermId.STUDY_EXPERIMENT, variableTypes, hasVariableType);

		study.setConditions(this.getStudyVariableBuilder().create(project, experiment, conditionVariableTypes, hasVariableType));
		study.setConstants(this.getStudyVariableBuilder().create(project, experiment, constantVariableTypes, hasVariableType));

		return study;
	}
}
