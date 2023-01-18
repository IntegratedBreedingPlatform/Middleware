package org.generationcp.middleware.api.role;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.service.api.user.RoleTypeDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class RoleTypeServiceImpl implements RoleTypeService {

	private WorkbenchDaoFactory workbenchDaoFactory;

	public RoleTypeServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.workbenchDaoFactory = new WorkbenchDaoFactory(sessionProvider);
	}

	@Override
	public List<RoleTypeDto> getRoleTypes() {
		return
			this.workbenchDaoFactory.getRoleTypeDAO().getRoleTypes().stream().map(r -> new RoleTypeDto(r)).collect(
				Collectors.toList());
	}

	@Override
	public RoleTypeDto getRoleType(final Integer id) {
		return new RoleTypeDto(this.workbenchDaoFactory.getRoleTypeDAO().getById(id));
	}

}
