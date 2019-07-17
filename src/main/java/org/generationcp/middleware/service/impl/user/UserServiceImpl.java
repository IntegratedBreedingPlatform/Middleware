package org.generationcp.middleware.service.impl.user;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.service.api.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	private WorkbenchDaoFactory workbenchDaoFactory;

	public UserServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.workbenchDaoFactory = new WorkbenchDaoFactory(sessionProvider);
	}

	@Override
	public Map<Integer, String> getUserIDFullNameMap(final List<Integer> userIds) {
		return this.workbenchDaoFactory.getWorkbenchUserDAO().getUserIDFullNameMap(userIds);
	}
}
