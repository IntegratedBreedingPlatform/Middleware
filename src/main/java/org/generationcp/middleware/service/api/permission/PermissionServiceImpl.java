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
	public List<PermissionDto> getPermissions(final Integer userId, final String cropName, final Integer programId) {
		return this.daoFactory.getPermissionDAO().getPermissions(userId, cropName, programId);
	}

	@Override
	public Set<PermissionDto> getSidebarLinks(
		final Integer userId, final String cropName, final Integer programId) {
		final Set<PermissionDto> result = new HashSet<>();
		final List<PermissionDto> permissions = this.daoFactory.getPermissionDAO().getPermissions(userId, cropName, programId);
		for (final PermissionDto permissionDto: permissions) {
			this.getLinks(result, permissionDto);
		}
		return result;
	}

	private void getLinks(final Set<PermissionDto> permissionDtoList, final PermissionDto permissionDto ) {
		final List<PermissionDto> children = this.daoFactory.getPermissionDAO().getChildrenOfPermission(permissionDto);
		if (children.size() != 0) {
			for (final PermissionDto dto : children) {
				if (dto.getWorkbenchCategoryLinkId() != null) {
					permissionDtoList.add(dto);
				}
				this.getLinks(permissionDtoList, dto);
			}
		}
		else {
			permissionDtoList.add(permissionDto);
		}
	}

	@Override
	public void close() {
		if (this.sessionProvider != null) {
			this.sessionProvider.close();
		}
	}
}
