package org.generationcp.middleware.service.api.permission;

import org.generationcp.middleware.domain.workbench.PermissionDto;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PermissionServiceImpl implements PermissionService {

	private HibernateSessionProvider sessionProvider;
	private DaoFactory daoFactory;

	public PermissionServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
		this.daoFactory = new DaoFactory(this.sessionProvider);
	}

	public PermissionServiceImpl() {
	}

	@Override
	public List<PermissionDto> getPermissionLinks(
		final Integer userId, final String cropName, final Integer programId) {
		Set<PermissionDto> links = new HashSet();
		final List<PermissionDto> permissions = this.daoFactory.getPermissionDAO().getPermissions(userId, cropName, programId);
		for (PermissionDto permissionDto: permissions) {
			links.addAll(this.getLinks(permissionDto));
		}
		return permissions;
	}

	private List<PermissionDto> getLinks(final PermissionDto permissionDto) {
		return this.daoFactory.getPermissionDAO().getChildrenOfPermission(permissionDto);
	}

	@Override
	public void close() {
		if (this.sessionProvider != null) {
			this.sessionProvider.close();
		}
	}
}
