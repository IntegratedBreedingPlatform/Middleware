package org.generationcp.middleware.service.impl;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.service.api.CropConfigurationService;

public class CropConfigurationServiceImpl implements CropConfigurationService {

	private final DaoFactory daoFactory;

	public CropConfigurationServiceImpl(final HibernateSessionProvider hibernateSessionProvider) {
		this.daoFactory = new DaoFactory(hibernateSessionProvider);
	}

	@Override
	public String getConfiguration(final String key) {
		return this.daoFactory.getCropConfigurationDAO().getConfiguration(key);
	}
}
