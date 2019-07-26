package org.generationcp.middleware.service.api.permission;

import com.google.common.collect.Lists;
import org.generationcp.middleware.domain.workbench.PermissionDto;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.workbench.Permission;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Transactional
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
	public List<PermissionDto> getPermissionLinks(
		final Integer userId, final String cropName, final Integer programId) {
		final Set<PermissionDto> result = new HashSet<>();
		final List<PermissionDto> permissions = this.daoFactory.getPermissionDAO().getPermissions(userId, cropName, programId);
		for (final PermissionDto permissionDto: permissions) {
			this.getLinks(result, permissionDto);
		}
		return Lists.newArrayList(result);
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
	public Permission getPermissionById(final Integer permissionId) {
		return this.daoFactory.getPermissionDAO().getById(permissionId);
	}

	@Override
	public List<Permission> getAllPermissions() {
		return this.daoFactory.getPermissionDAO().getAll();
	}

	@Override
	public List<Permission> getPermissionsByIds(final Set<Integer> permissionIds) {
		return this.daoFactory.getPermissionDAO().getPermissions(permissionIds);
	}

	@Override
	public PermissionDto getPermissionTree(final Integer roleTypeId) {
		return null;
	}


	@Override
	public void close() {
		if (this.sessionProvider != null) {
			this.sessionProvider.close();
		}
	}
}
