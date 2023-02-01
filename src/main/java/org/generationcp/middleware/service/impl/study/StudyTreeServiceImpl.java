package org.generationcp.middleware.service.impl.study;

import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.service.api.study.StudyTreeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StudyTreeServiceImpl implements StudyTreeService {

	private final HibernateSessionProvider sessionProvider;
	private final DaoFactory daoFactory;

	public StudyTreeServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public Integer createStudyTreeFolder(final int parentId, final String folderName, final String programUUID) {
		final DmsProject project = new DmsProject();
		project.setProgramUUID(programUUID);
		project.setParent(this.daoFactory.getDmsProjectDAO().getById(parentId));
		project.setName(folderName);
		project.setDescription(folderName);
		project.setObjective(folderName);
		project.setCreatedBy(ContextHolder.getLoggedInUserId().toString());
		return this.daoFactory.getDmsProjectDAO().save(project).getProjectId();
	}

	@Override
	public Integer updateStudyTreeFolder(final int parentId, final String newFolderName) {
		final DmsProject project = this.daoFactory.getDmsProjectDAO().getById(parentId);
		project.setName(newFolderName);
		project.setDescription(newFolderName);
		project.setObjective(newFolderName);
		return this.daoFactory.getDmsProjectDAO().update(project).getProjectId();
	}

	@Override
	public void deleteStudyFolder(final Integer folderId) {
		final DmsProject project = this.daoFactory.getDmsProjectDAO().getById(folderId);
		this.daoFactory.getDmsProjectDAO().makeTransient(project);
	}

	@Override
	public Integer moveStudyNode(final int itemId, final int newParentFolderId) {
		final DmsProject folderToMove = this.daoFactory.getDmsProjectDAO().getById(itemId);
		final DmsProject newParentFolder = this.daoFactory.getDmsProjectDAO().getById(newParentFolderId);
		folderToMove.setParent(newParentFolder);

		this.daoFactory.getDmsProjectDAO().saveOrUpdate(folderToMove);
		this.sessionProvider.getSession().flush();

		return folderToMove.getProjectId();
	}

}
