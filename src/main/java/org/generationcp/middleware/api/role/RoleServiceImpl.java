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

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.service.api.user.RoleSearchDto;
import org.hibernate.Session;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of the RoleServiceImpl interface. To instantiate this class, a Hibernate Session must be passed to its constructor.
 */
@Transactional
public class RoleServiceImpl implements RoleService {

	private HibernateSessionProvider sessionProvider;

	private WorkbenchDaoFactory workbenchDaoFactory;

	public RoleServiceImpl() {
		super();
	}

	public RoleServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
		this.workbenchDaoFactory = new WorkbenchDaoFactory(sessionProvider);
	}

	public Session getCurrentSession() {
		return this.sessionProvider.getSession();
	}

	@Override
	public void close() {
		if (this.sessionProvider != null) {
			this.sessionProvider.close();
		}
	}

	@Override
	public List<Role> getRoles(final RoleSearchDto roleSearchDto) {
		return this.workbenchDaoFactory.getRoleDao().getRoles(roleSearchDto);
	}

	@Override
	public Role saveRole(final Role role) {

		try {
			this.workbenchDaoFactory.getRoleDao().saveOrUpdate(role);
		} catch (final Exception e) {
			throw new MiddlewareQueryException(
				"Cannot save Role: RoleService.saveRole(role=" + role + "): " + e.getMessage(), e);
		}

		return role;
	}

	@Override
	public Role getRoleByName(final String name) {
		final RoleSearchDto roleSearchDto = new RoleSearchDto();
		roleSearchDto.setName(name);
		final List<Role> roles = this.workbenchDaoFactory.getRoleDao().getRoles(roleSearchDto);
		return roles.isEmpty() ? null : roles.get(0);
	}

	@Override
	public Role getRoleById(final Integer id) {
		return this.workbenchDaoFactory.getRoleDao().getRoleById(id);
	}
}