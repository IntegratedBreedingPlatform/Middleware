package org.generationcp.middleware.api.role;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.pojos.workbench.RoleType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class RoleTypeServiceImpl implements RoleTypeService {

	private WorkbenchDaoFactory workbenchDaoFactory;

	public RoleTypeServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.workbenchDaoFactory = new WorkbenchDaoFactory(sessionProvider);
	}

	@Override
	public List<RoleType> getRoleTypes() {
		return this.workbenchDaoFactory.getRoleTypeDAO().getRoleTypes();
	}

	@Override
	public RoleType getRoleType(final Integer id) {
		return this.workbenchDaoFactory.getRoleTypeDAO().getById(id);
	}

}
