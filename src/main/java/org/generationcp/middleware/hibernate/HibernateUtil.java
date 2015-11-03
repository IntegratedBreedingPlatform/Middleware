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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.URL;

import org.generationcp.middleware.exceptions.ConfigException;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.util.ResourceFinder;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This utility class instantiates a SessionFactory from which Sessions for a thread can be opened.
 *
 * @author klmanansala
 *
 */
public class HibernateUtil implements Serializable {

	private static final long serialVersionUID = -6399030839728425831L;

	private static final Logger LOG = LoggerFactory.getLogger(HibernateUtil.class);

	private static final String MIDDLEWARE_INTERNAL_HIBERNATE_CFG = "ibpmidware_hib.cfg.xml";

	private SessionFactory sessionFactory;
	private ThreadLocal<Session> threadSession;

	/**
	 * Given a file name of a hibernate cfg xml file, this constructor creates a SessionFactory based on it. It is assumed that connection
	 * properties are defined in the config file.
	 * 
	 * @param hibernateCfgFileName
	 * @throws ConfigException
	 * @throws HibernateException
	 */
	public HibernateUtil(String hibernateCfgFileName) throws ConfigException, HibernateException {
		try {
			HibernateUtil.LOG.info("Reading Hibernate config file: " + hibernateCfgFileName);
			URL urlOfCfgFile = ResourceFinder.locateFile(hibernateCfgFileName);

			AnnotationConfiguration cfg = new AnnotationConfiguration().configure(urlOfCfgFile);
			HibernateUtil.LOG.info("Opening SessionFactory...");
			this.sessionFactory = cfg.buildSessionFactory();

			this.threadSession = new ThreadLocal<Session>();
		} catch (FileNotFoundException e) {
			throw new ConfigException(e.getMessage(), e);
		}
	}

	/**
	 * Creates a SessionFactory which connects to the database identified by the host, port, and dbname parameters. The username and
	 * password parameters are used for authentication with the database system. The parameters are used in conjuction with the
	 * ibpmidware_hib.cfg.xml file in src/main/config.
	 * 
	 * @param host
	 * @param port
	 * @param dbName
	 * @param username
	 * @param password
	 * @throws ConfigException
	 * @throws HibernateException
	 */
	public HibernateUtil(String host, String port, String dbName, String username, String password) throws ConfigException,
			HibernateException {
		this(HibernateUtil.MIDDLEWARE_INTERNAL_HIBERNATE_CFG, "jdbc:mysql://" + host + ":" + port + "/" + dbName, username, password);
	}

	/**
	 * Creates a SessionFactory which connects to the database identified by the host, port, and dbname parameters. The username and
	 * password parameters are used for authentication with the database system. The parameters are used in conjuction with the
	 * ibpmidware_hib.cfg.xml file in src/main/config.
	 * 
	 * @param hibernateCfgFilename
	 * @param host
	 * @param port
	 * @param dbName
	 * @param username
	 * @param password
	 * @throws ConfigException
	 * @throws HibernateException
	 */
	public HibernateUtil(String hibernateCfgFilename, String host, String port, String dbName, String username, String password)
			throws ConfigException, HibernateException {
		this(hibernateCfgFilename, "jdbc:mysql://" + host + ":" + port + "/" + dbName, username, password);
	}

	public HibernateUtil(String hibernateCfgFilename, String connectionUrl, String username, String password) {
		try {
			HibernateUtil.LOG.info("Reading Hibernate config file: " + hibernateCfgFilename);
			URL urlOfCfgFile = ResourceFinder.locateFile(hibernateCfgFilename);

			AnnotationConfiguration cfg = new AnnotationConfiguration().configure(urlOfCfgFile);
			cfg.setProperty("hibernate.connection.url", connectionUrl);
			cfg.setProperty("hibernate.connection.username", username);
			cfg.setProperty("hibernate.connection.password", password);
			HibernateUtil.LOG.info("Opening SessionFactory...");
			this.sessionFactory = cfg.buildSessionFactory();

			this.threadSession = new ThreadLocal<Session>();
		} catch (FileNotFoundException e) {
			throw new ConfigException(e.getMessage(), e);
		}
	}

	/**
	 * Creates a SessionFactory which connects to the database given the DatabaseConnectionParameters object. The host, port, databasename,
	 * username and password fields will be used as connection parameters.
	 * 
	 * 
	 * 
	 * @param connectionParams
	 * @throws ConfigException
	 * @throws HibernateException
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws FileNotFoundException
	 */
	public HibernateUtil(DatabaseConnectionParameters connectionParams) throws ConfigException, HibernateException, FileNotFoundException,
			URISyntaxException, IOException {
		this(HibernateUtil.MIDDLEWARE_INTERNAL_HIBERNATE_CFG, connectionParams);
	}

	/**
	 * Creates a SessionFactory which connects to the database given the DatabaseConnectionParameters object. The host, port, databasename,
	 * username and password fields will be used as connection parameters.
	 * 
	 * @param hibernateCfgFilename
	 * @param connectionParams
	 * @throws ConfigException
	 * @throws HibernateException
	 */
	public HibernateUtil(String hibernateCfgFilename, DatabaseConnectionParameters connectionParams) throws ConfigException,
			HibernateException {
		this(hibernateCfgFilename, "jdbc:mysql://" + connectionParams.getHost() + ":" + connectionParams.getPort() + "/"
				+ connectionParams.getDbName(), connectionParams.getUsername(), connectionParams.getPassword());
	}

	/**
	 * Returns the SessionFactory object.
	 * 
	 * @return the SessionFactory
	 */
	public SessionFactory getSessionFactory() {
		return this.sessionFactory;
	}

	/**
	 * Closes the SessionFactory object to release its resources.
	 */
	public void shutdown() {
		this.closeCurrentSession();
		this.getSessionFactory().close();
	}

	/**
	 * Returns the Session for the thread which made the call to this method.
	 * 
	 * @return the Session
	 */
	public Session getCurrentSession() {
		Session session = this.threadSession.get();

		if (session == null || !session.isOpen()) {
			session = this.getSessionFactory().openSession();
			this.threadSession.set(session);
		}

		return session;
	}

	/**
	 * Closes the Session associated with the thread which called this method.
	 */
	public void closeCurrentSession() {
		Session session = this.threadSession.get();

		if (session != null) {
			session.close();
			session = null;
		}

		this.threadSession.set(null);
	}
	/**
	 * Parse hibernate query result value to boolean with null check
	 *
	 * @param val value
	 * @return boolean
	 */
	public static boolean typeSafeObjectToBoolean(Object val) {
		if (val == null) {
			return false;
		}
		if (val instanceof Integer) {
			return (Integer) val != 0;
		}
		if (val instanceof Boolean) {
			return (Boolean) val;
		}
		return false;
	}

	/**
	 * Parse hibernate query result value to Integer with null check
	 *
	 * @param val value
	 * @return boolean
	 */
	public static Integer typeSafeObjectToInteger(Object val) {
		if (val == null) {
			return null;
		}
		if (val instanceof Integer) {
			return (Integer) val;
		}
		if (val instanceof String) {
			return Integer.valueOf((String) val);
		}
		throw new NumberFormatException("Can not cast " + val.getClass() + " to Integer for value: " + val);
	}
}
