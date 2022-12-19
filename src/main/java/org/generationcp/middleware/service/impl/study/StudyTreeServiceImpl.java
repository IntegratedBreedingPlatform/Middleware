package org.generationcp.middleware.service.impl.study;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.service.api.study.StudyTreeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StudyTreeServiceImpl implements StudyTreeService {

	private final DaoFactory daoFactory;

	public StudyTreeServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public Integer createStudyTreeFolder(final int parentId, final String name, final String programUUID) {
		final DmsProject project = new DmsProject();
		project.setProgramUUID(programUUID);
		project.setParent(this.daoFactory.getDmsProjectDAO().getById(parentId));
		project.setName(name);
		project.setDescription(name);
		project.setObjective(name);

		return this.daoFactory.getDmsProjectDAO().save(project).getProjectId();
	}

}
