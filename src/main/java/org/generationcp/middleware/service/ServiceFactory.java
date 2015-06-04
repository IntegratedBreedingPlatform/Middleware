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

package org.generationcp.middleware.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import org.generationcp.middleware.exceptions.ConfigException;
import org.generationcp.middleware.hibernate.HibernateSessionPerThreadProvider;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.service.api.DataImportService;
import org.generationcp.middleware.service.api.FieldbookService;
import org.generationcp.middleware.service.api.InventoryService;
import org.generationcp.middleware.service.api.OntologyService;
import org.generationcp.middleware.util.ResourceFinder;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * The {@link ServiceFactory} is a convenience class intended to provide methods to get instances of the Service implementations provided by
 * the Middleware.
 * </p>
 *
 * @author Kevin Manansala
 * @author Glenn Marintes
 */
public class ServiceFactory implements Serializable {

	private static final long serialVersionUID = -2846462010022009403L;

	private final static Logger LOG = LoggerFactory.getLogger(ServiceFactory.class);

	private static final String MIDDLEWARE_INTERNAL_HIBERNATE_CFG = "ibpmidware_hib.cfg.xml";

	private String hibernateConfigurationFilename = ServiceFactory.MIDDLEWARE_INTERNAL_HIBERNATE_CFG;

	private SessionFactory sessionFactoryForLocal;
	private SessionFactory sessionFactoryForCentral;
	private HibernateSessionProvider sessionProviderForLocal;
	private HibernateSessionProvider sessionProviderForCentral;

	private String localDatabaseName;
	private String centralDatabaseName;

	public ServiceFactory() {
	}

	public ServiceFactory(String propertyFile) {
		ServiceFactory.LOG.trace("Created ServiceFactory instance");

		Properties prop = new Properties();

		try {
			InputStream in = null;

			try {
				in = new FileInputStream(new File(ResourceFinder.locateFile(propertyFile).toURI()));
			} catch (IllegalArgumentException ex) {
				in = Thread.currentThread().getContextClassLoader().getResourceAsStream(propertyFile);
			}
			prop.load(in);

			String localHost = prop.getProperty("local.host");
			String localDbname = prop.getProperty("local.dbname");
			String localPort = prop.getProperty("local.port");
			String localUsername = prop.getProperty("local.username");
			String localPassword = prop.getProperty("local.password");

			String centralHost = prop.getProperty("central.host");
			String centralDbname = prop.getProperty("central.dbname");
			String centralPort = prop.getProperty("central.port");
			String centralUsername = prop.getProperty("central.username");
			String centralPassword = prop.getProperty("central.password");

			in.close();

			DatabaseConnectionParameters paramsForLocal =
					new DatabaseConnectionParameters(localHost, localPort, localDbname, localUsername, localPassword);
			DatabaseConnectionParameters paramsForCentral =
					new DatabaseConnectionParameters(centralHost, centralPort, centralDbname, centralUsername, centralPassword);

			this.openSessionFactory(paramsForLocal, paramsForCentral);

			// FIXME: Do we really want to hide these exceptions?
		} catch (URISyntaxException e) {
			ServiceFactory.LOG.error(e.getMessage(), e);
		} catch (HibernateException e) {
			ServiceFactory.LOG.error(e.getMessage(), e);
		} catch (ConfigException e) {
			ServiceFactory.LOG.error(e.getMessage(), e);
		} catch (IOException e) {
			ServiceFactory.LOG.error(e.getMessage(), e);
		}

	}

	public ServiceFactory(DatabaseConnectionParameters paramsForLocal, DatabaseConnectionParameters paramsForCentral)
			throws ConfigException {
		this(ServiceFactory.MIDDLEWARE_INTERNAL_HIBERNATE_CFG, paramsForLocal, paramsForCentral);
	}

	/**
	 * This constructor accepts two DatabaseConnectionParameters objects as parameters. The first is used to connect to a local instance of
	 * IBDB and the second is used to connect to a central instance of IBDB. The user can provide both or can provide one of the two.<br>
	 * <br>
	 * For example:<br>
	 * <br>
	 * 1. creating a ManagerFactory which uses connections to both local and central instances<br>
	 * <br>
	 * DatabaseConnectionParameters local = new DatabaseConnectionParameters(...);<br>
	 * DatabaseConnectionParameters central = new DatabaseConnectionParameters(...);<br>
	 * ManagerFactory factory = new ManagerFactory(local, central);<br>
	 * <br>
	 * 2. creating a ManagerFactory which uses a connection to local only<br>
	 * <br>
	 * DatabaseConnectionParameters local = new DatabaseConnectionParameters(...);<br>
	 * ManagerFactory factory = new ManagerFactory(local, null);<br>
	 * <br>
	 * 3. creating a ManagerFactory which uses a connection to central only<br>
	 * <br>
	 * DatabaseConnectionParameters central = new DatabaseConnectionParameters(...);<br>
	 * ManagerFactory factory = new ManagerFactory(null, central);<br>
	 * <br>
	 * 
	 * @param paramsForLocal
	 * @param paramsForCentral
	 * @throws ConfigException
	 */
	public ServiceFactory(String hibernateConfigurationFilename, DatabaseConnectionParameters paramsForLocal,
			DatabaseConnectionParameters paramsForCentral) throws ConfigException {
		ServiceFactory.LOG.trace("Created ServiceFactory instance");

		if (hibernateConfigurationFilename != null) {
			this.hibernateConfigurationFilename = hibernateConfigurationFilename;
		}

		try {
			this.openSessionFactory(paramsForLocal, paramsForCentral);
		} catch (FileNotFoundException e) {
			throw new ConfigException(e.getMessage(), e);
		}
	}

	public String getHibernateConfigurationFilename() {
		return this.hibernateConfigurationFilename;
	}

	public void setHibernateConfigurationFilename(String hibernateConfigurationFile) {
		this.hibernateConfigurationFilename = hibernateConfigurationFile;
	}

	public SessionFactory getSessionFactoryForLocal() {
		return this.sessionFactoryForLocal;
	}

	public void setSessionFactoryForLocal(SessionFactory sessionFactoryForLocal) {
		this.sessionFactoryForLocal = sessionFactoryForLocal;
	}

	public SessionFactory getSessionFactoryForCentral() {
		return this.sessionFactoryForCentral;
	}

	public void setSessionFactoryForCentral(SessionFactory sessionFactoryForCentral) {
		this.sessionFactoryForCentral = sessionFactoryForCentral;
	}

	public HibernateSessionProvider getSessionProviderForLocal() {
		return this.sessionProviderForLocal;
	}

	public void setSessionProviderForLocal(HibernateSessionProvider sessionProviderForLocal) {
		this.sessionProviderForLocal = sessionProviderForLocal;
	}

	public HibernateSessionProvider getSessionProviderForCentral() {
		return this.sessionProviderForCentral;
	}

	public void setSessionProviderForCentral(HibernateSessionProvider sessionProviderForCentral) {
		this.sessionProviderForCentral = sessionProviderForCentral;
	}

	private void openSessionFactory(DatabaseConnectionParameters paramsForLocal, DatabaseConnectionParameters paramsForCentral)
			throws FileNotFoundException {
		// if local database parameters were specified,
		// create a SessionFactory for local database
		if (paramsForLocal != null) {
			String connectionUrl =
					String.format("jdbc:mysql://%s:%s/%s", paramsForLocal.getHost(), paramsForLocal.getPort(), paramsForLocal.getDbName());

			URL urlOfCfgFile = ResourceFinder.locateFile(this.hibernateConfigurationFilename);

			AnnotationConfiguration cfg = new AnnotationConfiguration().configure(urlOfCfgFile);
			cfg.setProperty("hibernate.connection.url", connectionUrl);
			cfg.setProperty("hibernate.connection.username", paramsForLocal.getUsername());
			cfg.setProperty("hibernate.connection.password", paramsForLocal.getPassword());

			ServiceFactory.LOG.info("Opening SessionFactory for local database...");
			this.sessionFactoryForLocal = cfg.buildSessionFactory();
			this.localDatabaseName = paramsForLocal.getDbName();
		}

		// if central database parameters were specified,
		// create a SessionFactory for central database
		if (paramsForCentral != null) {
			String connectionUrl =
					String.format("jdbc:mysql://%s:%s/%s", paramsForCentral.getHost(), paramsForCentral.getPort(),
							paramsForCentral.getDbName());

			URL urlOfCfgFile = ResourceFinder.locateFile(this.hibernateConfigurationFilename);

			AnnotationConfiguration cfg = new AnnotationConfiguration().configure(urlOfCfgFile);
			cfg.setProperty("hibernate.connection.url", connectionUrl);
			cfg.setProperty("hibernate.connection.username", paramsForCentral.getUsername());
			cfg.setProperty("hibernate.connection.password", paramsForCentral.getPassword());

			ServiceFactory.LOG.info("Opening SessionFactory for central database...");
			this.sessionFactoryForCentral = cfg.buildSessionFactory();
			this.centralDatabaseName = paramsForCentral.getDbName();
		}

		// if no local and central database parameters were set,
		// throw a ConfigException
		if (this.sessionFactoryForCentral == null && this.sessionFactoryForLocal == null) {
			throw new ConfigException("No connection was established because database connection parameters were null.");
		}

		this.sessionProviderForLocal = new HibernateSessionPerThreadProvider(this.sessionFactoryForLocal);
		this.sessionProviderForCentral = new HibernateSessionPerThreadProvider(this.sessionFactoryForCentral);
	}

	public DataImportService getDataImportService() {
		return new DataImportServiceImpl(this.sessionProviderForLocal);
	}

	public FieldbookService getFieldbookService() {
		return new FieldbookServiceImpl(this.sessionProviderForLocal, this.localDatabaseName);
	}

	public OntologyService getOntologyService() {
		return new OntologyServiceImpl(this.sessionProviderForLocal);
	}

	public InventoryService getInventoryService() {
		return new InventoryServiceImpl(this.sessionProviderForLocal, this.localDatabaseName);
	}

	/**
	 * Closes the db connection by shutting down the HibernateUtil object
	 */
	public void close() {
		ServiceFactory.LOG.trace("Closing ManagerFactory...");

		if (this.sessionProviderForLocal != null) {
			this.sessionProviderForLocal.close();
		}

		if (this.sessionProviderForCentral != null) {
			this.sessionProviderForCentral.close();
		}

		if (this.sessionFactoryForLocal != null && !this.sessionFactoryForLocal.isClosed()) {
			this.sessionFactoryForLocal.close();
		}

		if (this.sessionFactoryForCentral != null && !this.sessionFactoryForCentral.isClosed()) {
			this.sessionFactoryForCentral.close();
		}

		ServiceFactory.LOG.trace("Closing ManagerFactory... DONE");
	}

	public String getLocalDatabaseName() {
		return this.localDatabaseName;
	}

	public void setLocalDatabaseName(String localDatabaseName) {
		this.localDatabaseName = localDatabaseName;
	}

	public String getCentralDatabaseName() {
		return this.centralDatabaseName;
	}

	public void setCentralDatabaseName(String centralDatabaseName) {
		this.centralDatabaseName = centralDatabaseName;
	}

}
