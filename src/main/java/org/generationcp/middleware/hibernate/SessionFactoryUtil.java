/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.hibernate;

import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.util.ResourceFinder;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.net.URL;

/**
 * <p>
 * A utility class providing methods for creating {@link SessionFactory} instances.
 * </p>
 *
 * @author Glenn Marintes
 */
public abstract class SessionFactoryUtil {

	private static final Logger LOG = LoggerFactory.getLogger(SessionFactoryUtil.class);

	/**
	 * The default Hibernate configuration filename.
	 */
	private static final String MIDDLEWARE_INTERNAL_HIBERNATE_CFG = "ibpmidware_hib.cfg.xml";

	/**
	 * Open a {@link SessionFactory} using the default Middleware Hibernate configuration file.
	 *
	 * @param params
	 * @return the SessionFactory
	 * @throws FileNotFoundException
	 */
	public static SessionFactory openSessionFactory(DatabaseConnectionParameters params) throws FileNotFoundException {
		return SessionFactoryUtil.openSessionFactory(SessionFactoryUtil.MIDDLEWARE_INTERNAL_HIBERNATE_CFG, params);
	}

	/**
	 * Open a {@link SessionFactory} using the specified Hibernate configuration file.
	 *
	 * @param hibernateConfigurationFile the hibernate configuration filename. If null, this method will use the default Middleware
	 *        Hibernate configuration filename
	 * @param params
	 * @return the SessionFactory
	 * @throws FileNotFoundException
	 */
	public static SessionFactory openSessionFactory(String hibernateConfigurationFile, DatabaseConnectionParameters params,
			String... additionalResourceFiles) throws FileNotFoundException {
		if (hibernateConfigurationFile == null) {
			hibernateConfigurationFile = SessionFactoryUtil.MIDDLEWARE_INTERNAL_HIBERNATE_CFG;
		}

		String connectionUrl = String.format("jdbc:mysql://%s:%s/%s", params.getHost(), params.getPort(), params.getDbName());

		URL urlOfCfgFile = ResourceFinder.locateFile(hibernateConfigurationFile);

		Configuration cfg = new Configuration().configure(urlOfCfgFile);

		cfg.setProperty("hibernate.connection.url", connectionUrl);
		cfg.setProperty("hibernate.connection.username", params.getUsername());
		cfg.setProperty("hibernate.connection.password", params.getPassword());

		ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(
			cfg.getProperties()). buildServiceRegistry();

		if (additionalResourceFiles != null) {
			for (String resourceFile : additionalResourceFiles) {
				cfg.addResource(resourceFile);
			}
		}

		return cfg.buildSessionFactory(serviceRegistry);
	}
}
