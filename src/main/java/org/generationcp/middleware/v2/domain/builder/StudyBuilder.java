package org.generationcp.middleware.v2.domain.builder;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.v2.domain.Study;
import org.generationcp.middleware.v2.pojos.DmsProject;

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
		study.setVariableTypes(getVariableTypeBuilder().create(project.getProperties()));
		
		//TODO get conditions only from project then assign to study
		study.setConditions(getVariableBuilder().create(project.getProperties(), study.getVariableTypes()));
		
		study.setConstants(getVariableBuilder().create(project.getConstants(), study.getVariableTypes()));
		
		return study;
	}

}
