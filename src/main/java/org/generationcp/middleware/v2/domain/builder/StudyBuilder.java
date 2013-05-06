package org.generationcp.middleware.v2.domain.builder;

import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.v2.domain.Study;
import org.generationcp.middleware.v2.domain.VariableTypeList;
import org.generationcp.middleware.v2.pojos.DmsProject;
import org.generationcp.middleware.v2.pojos.ProjectProperty;

public class StudyBuilder extends Builder {

	public StudyBuilder(HibernateSessionProvider sessionProviderForLocal,
			               HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public Study createStudy(int studyId) throws MiddlewareQueryException {
		Study study = null;
		if (setWorkingDatabase(studyId)) {
			DmsProject project = getDmsProjectDao().getById(studyId);
			if (project != null) {
				study = createStudy(project);
			}
		}
		return study;
	}

	public Study createStudy(DmsProject project) throws MiddlewareQueryException {
		Study study = new Study();
		study.setId(project.getProjectId());
		
		List<ProjectProperty> conditions = project.getConditions();
		VariableTypeList conditionVariableTypes = getVariableTypeBuilder().create(conditions);
		study.setConditions(getVariableBuilder().create(conditions, conditionVariableTypes));
		
		List<ProjectProperty> constants = project.getConstants();
		VariableTypeList constantVariableTypes = getVariableTypeBuilder().create(constants);
		study.setConstants(getVariableBuilder().create(constants, constantVariableTypes));
		
		return study;
	}
}
