/***************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the 
 * GNU General Public License (http://bit.ly/8Ztv8M) and the 
 * provisions of Part F of the Generation Challenge Programme 
 * Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 **************************************************************/
package org.generationcp.middleware.util;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.generationcp.middleware.exceptions.ConfigException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

/**
 * This utility class instantiates a SessionFactory from which Sessions for a
 * thread can be opened.
 * 
 * @author klmanansala
 * 
 */
public class HibernateUtil implements Serializable {
    private static final long serialVersionUID = -6399030839728425831L;

    private final static Logger log = LoggerFactory
	    .getLogger(HibernateUtil.class);

    private static final String MIDDLEWARE_INTERNAL_HIBERNATE_CFG = "ibpmidware_hib.cfg.xml";

    private SessionFactory sessionFactory;
    private ThreadLocal<Session> threadSession;

    /**
     * Given a file name of a hibernate cfg xml file, this constructor creates a
     * SessionFactory based on it. It is assumed that connection properties are
     * defined in the config file.
     * 
     * @param hibernateCfgFileName
     * @throws ConfigException
     * @throws HibernateException
     */
    public HibernateUtil(String hibernateCfgFileName) throws ConfigException,
	    HibernateException {
	try {
	    log.info("Reading Hibernate config file: " + hibernateCfgFileName);
	    URL urlOfCfgFile = ResourceFinder.locateFile(hibernateCfgFileName);

	    AnnotationConfiguration cfg = new AnnotationConfiguration()
		    .configure(urlOfCfgFile);
	    log.info("Opening SessionFactory...");
	    sessionFactory = cfg.buildSessionFactory();

	    threadSession = new ThreadLocal<Session>();
	} catch (FileNotFoundException ex) {
	    throw new ConfigException(ex.getMessage());
	}
    }

    /**
     * Creates a SessionFactory which connects to the database identified by the
     * host, port, and dbname parameters. The username and password parameters
     * are used for authentication with the database system. The parameters are
     * used in conjuction with the ibpmidware_hib.cfg.xml file in
     * src/main/config.
     * 
     * @param host
     * @param port
     * @param dbName
     * @param username
     * @param password
     * @throws ConfigException
     * @throws HibernateException
     */
    public HibernateUtil(String host, String port, String dbName,
	    String username, String password) throws ConfigException,
	    HibernateException {
	try {
	    log.info("Reading Hibernate config file: "
		    + MIDDLEWARE_INTERNAL_HIBERNATE_CFG);
	    URL urlOfCfgFile = ResourceFinder
		    .locateFile(MIDDLEWARE_INTERNAL_HIBERNATE_CFG);

	    AnnotationConfiguration cfg = new AnnotationConfiguration()
		    .configure(urlOfCfgFile);
	    String connectionURL = "jdbc:mysql://" + host + ":" + port + "/"
		    + dbName;
	    cfg.setProperty("hibernate.connection.url", connectionURL);
	    cfg.setProperty("hibernate.connection.username", username);
	    cfg.setProperty("hibernate.connection.password", password);
	    log.info("Opening SessionFactory...");
	    sessionFactory = cfg.buildSessionFactory();

	    threadSession = new ThreadLocal<Session>();
	} catch (FileNotFoundException ex) {
	    throw new ConfigException(ex.getMessage());
	}
    }

    /**
     * Returns the SessionFactory object.
     * 
     * @return
     */
    public SessionFactory getSessionFactory() {
	return this.sessionFactory;
    }

    /**
     * Closes the SessionFactory object to release its resources.
     */
    public void shutdown() {
	getSessionFactory().close();
    }

    /**
     * Returns the Session for the thread which made the call to this method.
     * 
     * @return
     */
    public Session getCurrentSession() {
	Session session = threadSession.get();

	if (session == null || !session.isOpen()) {
	    session = getSessionFactory().openSession();
	    threadSession.set(session);
	}

	return session;
    }

    /**
     * Closes the Session associated with the thread which called this method.
     */
    public void closeCurrentSession() {
	Session session = threadSession.get();

	if (session != null) {
	    session.close();
	    session = null;
	}

	threadSession.set(null);
    }
}
