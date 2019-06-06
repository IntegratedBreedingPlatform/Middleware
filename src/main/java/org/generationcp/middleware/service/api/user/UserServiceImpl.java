package org.generationcp.middleware.service.api.user;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class UserServiceImpl implements UserService {

	private HibernateSessionProvider sessionProvider;
	private DaoFactory daoFactory;

	public UserServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
		this.daoFactory = new DaoFactory(this.sessionProvider);
	}

	@Override
	public WorkbenchUser getUserWithAuthorities(final String userName, final String crop, final String program) {
	 	return this.daoFactory.getWorkbenchUserDAO().getUserWithAuthorities(userName, crop, program);
	}
}
