package org.generationcp.middleware.service.impl;

import org.generationcp.middleware.dao.ConfigurationDAO;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.Configuration;
import org.generationcp.middleware.service.api.ConfigurationService;


public class ConfigurationServiceImpl implements ConfigurationService {

	private ConfigurationDAO configurationDAO;

	public ConfigurationServiceImpl() {
	}

	public ConfigurationServiceImpl(HibernateSessionProvider sessionProvider) {
		this.configurationDAO = new ConfigurationDAO();
		this.configurationDAO.setSession(sessionProvider.getSession());
	}

	public ConfigurationServiceImpl(ConfigurationDAO configurationDAO) {
		this.configurationDAO = configurationDAO;
	}

	@Override
	public Configuration getConfiguration(String key) {
		return this.configurationDAO.getById(key);
	}

}
