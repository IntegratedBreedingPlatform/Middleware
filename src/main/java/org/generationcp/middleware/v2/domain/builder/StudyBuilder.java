package org.generationcp.middleware.v2.domain.builder;

import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.v2.domain.Study;
import org.generationcp.middleware.v2.pojos.DmsProject;
import org.generationcp.middleware.v2.pojos.ProjectProperty;

public class StudyBuilder extends Builder {

	public StudyBuilder(HibernateSessionProvider sessionProviderForLocal,
			               HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public Study createStudyWithoutDataSets(int studyId) throws MiddlewareQueryException {
		Study study = null;
		if (setWorkingDatabase(studyId)) {
			DmsProject project = getDmsProjectDao().getById(studyId);
			if (project != null) {
				study = createStudyWithoutDataSets(project);
			}
		}
		return study;
	}

	private Study createStudyWithoutDataSets(DmsProject project) throws MiddlewareQueryException {
		Study study = new Study();
		study.setId(project.getProjectId());
		study.setName(project.getName());
		study.setDescription(project.getDescription());
		
		List<ProjectProperty> conditions = project.getConditions();
		study.setConditionVariableTypes(getVariableTypeBuilder().create(conditions));
		study.setConditions(getVariableBuilder().create(conditions, study.getConditionVariableTypes()));
		
		List<ProjectProperty> constants = project.getConstants();
		study.setConstantVariableTypes(getVariableTypeBuilder().create(constants));
		study.setConstants(getVariableBuilder().create(constants, study.getConstantVariableTypes()));		
		return study;
	}

}
