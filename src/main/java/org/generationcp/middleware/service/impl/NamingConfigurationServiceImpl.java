package org.generationcp.middleware.service.impl;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.naming.NamingConfiguration;
import org.generationcp.middleware.service.api.NamingConfigurationService;

public class NamingConfigurationServiceImpl implements NamingConfigurationService {

	private final DaoFactory daoFactory;

	public NamingConfigurationServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public NamingConfiguration getNamingConfigurationByName(final String name) {
		return this.daoFactory.getNamingConfigurationDAO().getByName(name);
	}
}
