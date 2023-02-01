/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.middleware.api.role;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.pojos.workbench.Permission;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.RoleType;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.user.RoleDto;
import org.generationcp.middleware.service.api.user.RoleGeneratorInput;
import org.generationcp.middleware.service.api.user.RoleSearchDto;
import org.generationcp.middleware.service.api.user.UserRoleDto;
import org.generationcp.middleware.service.api.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of the RoleServiceImpl interface. To instantiate this class, a Hibernate Session must be passed to its constructor.
 */
@Transactional
public class RoleServiceImpl implements RoleService {

	private WorkbenchDaoFactory workbenchDaoFactory;

	@Autowired
	private UserService userService;

	public RoleServiceImpl() {
		super();
	}

	public RoleServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.workbenchDaoFactory = new WorkbenchDaoFactory(sessionProvider);
	}

	@Override
	public long countRolesUsers(final RoleSearchDto roleSearchDto) {
		return this.workbenchDaoFactory.getRoleDao().countRolesUsers(roleSearchDto);
	}

	@Override
	public List<RoleDto> searchRoles(final RoleSearchDto roleSearchDto, Pageable pageable) {
		final List<Role> list = this.workbenchDaoFactory.getRoleDao().searchRoles(roleSearchDto, pageable);
		final List<RoleDto> roles = list.stream()
			.map(role -> new RoleDto(role))
			.collect(Collectors.toList());
		return roles;
	}

	@Override
	public RoleDto saveRole(final RoleGeneratorInput dto) {
		final WorkbenchUser user = this.userService.getUserByUsername(dto.getUsername());
		final RoleType roleType = this.workbenchDaoFactory.getRoleTypeDAO().getById(dto.getRoleType());
		final Role role = new Role();
		role.setName(dto.getName());
		role.setEditable(dto.isEditable());
		role.setAssignable(dto.isAssignable());
		role.setDescription(dto.getDescription());
		role.setCreatedDate(new Date());
		role.setCreatedBy(user);
		role.setActive(true);
		role.setPermissions(this.getPermission(dto.getPermissions()));
		role.setRoleType(roleType);
		role.setUpdatedBy(user);
		role.setUpdatedDate(new Date());
		this.workbenchDaoFactory.getRoleDAO().save(role);
		return new RoleDto(role);
	}

	@Override
	public RoleDto updateRole(final RoleGeneratorInput roleGeneratorInput) {
		final Role role = this.workbenchDaoFactory.getRoleDAO().getById(roleGeneratorInput.getId());
		role.setName(roleGeneratorInput.getName());
		role.setDescription(roleGeneratorInput.getDescription());
		role.setPermissions(this.getPermission(roleGeneratorInput.getPermissions()));
		final RoleType roleType = this.workbenchDaoFactory.getRoleTypeDAO().getById(roleGeneratorInput.getRoleType());
		role.setRoleType(roleType);
		this.workbenchDaoFactory.getRoleDAO().save(role);
		return new RoleDto(role);
	}

	@Override
	public Optional<RoleDto> getRoleByName(final String name) {
		final RoleSearchDto roleSearchDto = new RoleSearchDto();
		roleSearchDto.setName(name);
		final List<Role> roles = this.workbenchDaoFactory.getRoleDao().searchRoles(roleSearchDto, null);
		return roles.isEmpty() ? Optional.empty() : Optional.of(new RoleDto(roles.get(0)));
	}

	@Override
	public Optional<RoleDto> getRoleById(final Integer id) {
		final Role role = this.workbenchDaoFactory.getRoleDao().getRoleById(id);
		if (role!=null) {
			return Optional.of(new RoleDto(role));
		}
		return Optional.empty();
	}

	public Optional<RoleDto> getRoleWithUsers(final Integer id) {
		final Role role = this.workbenchDaoFactory.getRoleDao().getRoleById(id);
		if (role==null) {
			return Optional.empty();
		}
		final RoleDto roleDto = new RoleDto(role);
		role.getUserRoles().forEach(userRole -> roleDto.getUserRoles().add(new UserRoleDto(userRole)));
		return Optional.of(roleDto);
	}

	@Override
	public boolean isRoleInUse(final Integer roleId) {
		return this.workbenchDaoFactory.getUserRoleDao().getUsersByRoleId(roleId).isEmpty() ? false : true;
	}


	private List<Permission> getPermission(final List<Integer> permissions) {
		final List<Permission> permissionList = this.workbenchDaoFactory.getPermissionDAO().getPermissions(new HashSet<>(permissions));
		for (final Iterator<Permission> it = permissionList.iterator(); it.hasNext(); ) {
			final Permission permission = it.next();
			if (permission.getParent() != null && permissions.contains(permission.getParent().getPermissionId())) {
				it.remove();
			}
		}
		return permissionList;
	}
}
