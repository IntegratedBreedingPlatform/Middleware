package org.generationcp.middleware.service.api.permission;

import com.google.common.collect.Lists;
import org.generationcp.middleware.domain.workbench.PermissionDto;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.pojos.workbench.Permission;
import org.generationcp.middleware.pojos.workbench.RoleTypePermission;
import org.generationcp.middleware.service.api.user.RoleTypeDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Transactional
public class PermissionServiceImpl implements PermissionService {

	private HibernateSessionProvider sessionProvider;
	private WorkbenchDaoFactory workbenchDaoFactory;

	public PermissionServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
		this.workbenchDaoFactory = new WorkbenchDaoFactory(this.sessionProvider);
	}

	public PermissionServiceImpl() {
	}

	@Override
	public List<PermissionDto> getPermissions(final Integer userId, final String cropName, final Integer programId) {
		return this.workbenchDaoFactory.getPermissionDAO().getPermissions(userId, cropName, programId);
	}

	@Override
	public List<PermissionDto> getPermissionLinks(
		final Integer userId, final String cropName, final Integer programId) {
		final Set<PermissionDto> result = new HashSet<>();
		final List<PermissionDto> permissions = this.workbenchDaoFactory.getPermissionDAO().getPermissions(userId, cropName, programId);
		for (final PermissionDto permissionDto: permissions) {
			this.getLinks(result, permissionDto);
		}
		return Lists.newArrayList(result);
	}

	private void getLinks(final Set<PermissionDto> permissionDtoList, final PermissionDto permissionDto ) {
		final List<PermissionDto> children = this.workbenchDaoFactory.getPermissionDAO().getChildrenOfPermission(permissionDto);
		permissionDtoList.add(permissionDto);
		if (children.size() != 0) {
			for (final PermissionDto dto : children) {
				if (dto.getWorkbenchCategoryLinkId() != null) {
					permissionDtoList.add(dto);
				}
				this.getLinks(permissionDtoList, dto);
			}
		}
	}

	@Override
	public List<PermissionDto> getPermissionsDtoByIds(final Set<Integer> permissionIds) {
		final List<PermissionDto> permissionDtoList = new ArrayList<>();
		this.workbenchDaoFactory.getPermissionDAO().getPermissions(permissionIds).forEach( p -> {
			final PermissionDto permissionDto = new PermissionDto(p);
			//load role types
			final List<RoleTypeDto> roleTypeDtos = new ArrayList<>();
			p.getRoleTypePermissions().forEach(rtp -> {
				roleTypeDtos.add(new RoleTypeDto(rtp.getRoleType()));
			});
			permissionDto.setRoleTypes(roleTypeDtos);
			permissionDtoList.add(permissionDto);
		}
		);
		return permissionDtoList;
	}
	@Override
	public List<Permission> getPermissionsByIds(final Set<Integer> permissionIds) {
		return this.workbenchDaoFactory.getPermissionDAO().getPermissions(permissionIds);
	}

	@Override
	public PermissionDto getPermissionTree(final Integer roleTypeId) {
		final List<RoleTypePermission> children =
			this.workbenchDaoFactory.getRoleTypePermissionDAO().getPermissionsByRoleTypeAndParent(roleTypeId, null);
		PermissionDto permissionDto = new PermissionDto(children.get(0).getPermission());
		permissionDto.setSelectable(children.get(0).getSelectable());

		this.getPermissionTree(permissionDto, roleTypeId);
		return permissionDto;
	}

	private void getPermissionTree(PermissionDto permissionDto, Integer roleTypeId) {
		final List<RoleTypePermission> children =
			this.workbenchDaoFactory.getRoleTypePermissionDAO().getPermissionsByRoleTypeAndParent(roleTypeId, permissionDto.getId());
		if (children.isEmpty()) {
			return;
		} else {
			for (final RoleTypePermission roleTypePermission : children) {
				PermissionDto child = new PermissionDto(roleTypePermission.getPermission());
				child.setSelectable(roleTypePermission.getSelectable());
				permissionDto.addChild(child);
				this.getPermissionTree(child, roleTypeId);
			}

		}
	}

	@Override
	public void close() {
		if (this.sessionProvider != null) {
			this.sessionProvider.close();
		}
	}
}
