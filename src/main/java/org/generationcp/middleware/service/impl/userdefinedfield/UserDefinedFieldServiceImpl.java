package org.generationcp.middleware.service.impl.userdefinedfield;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.service.api.userdefinedfield.UserDefinedFieldService;

import java.util.List;
import java.util.Map;

public class UserDefinedFieldServiceImpl implements UserDefinedFieldService {

	private DaoFactory daoFactory;

	public UserDefinedFieldServiceImpl() {
	}

	public UserDefinedFieldServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public Map<String, Integer> getByTableAndCodesInMap(final String table, final List<String> codes) {
		return this.daoFactory.getUserDefinedFieldDAO().getByTableAndCodesInMap(table, codes);
	}

}
