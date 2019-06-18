package org.generationcp.middleware.service.api.user;

import org.generationcp.middleware.domain.workbench.PermissionDto;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class UserServiceImpl implements UserService {

	private HibernateSessionProvider sessionProvider;
	private DaoFactory daoFactory;

	public UserServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
		this.daoFactory = new DaoFactory(this.sessionProvider);
	}

	@Override
	public WorkbenchUser getUserWithAuthorities(final String userName, final String cropName, final String programUuid) {
		final WorkbenchUser user = this.daoFactory.getWorkbenchUserDAO().getUserByUserName(userName);
		final Project project = this.daoFactory.getProjectDAO().getByUuid(programUuid);
		final Integer programId = project != null ? project.getProjectId().intValue() : null;
		final List<PermissionDto> permissions = this.daoFactory.getPermissionDAO().getPermissions(user.getUserid(), cropName, programId);
		user.setPermissions(permissions);
		return user;
	}
}
