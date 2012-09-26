/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.middleware.manager;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.net.URL;

import org.generationcp.commons.util.ResourceFinder;
import org.generationcp.middleware.exceptions.ConfigException;
import org.generationcp.middleware.hibernate.HibernateSessionPerThreadProvider;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.GenotypicDataManager;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.TraitDataManager;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link FourConnectionManagerFactory} gives access to the different Manager implementation
 * classes. This class takes care of opening and closing the connection to the
 * databases.
 * 
 */
public class FourConnectionManagerFactory implements Serializable{

    private static final long serialVersionUID = 6357074640567637349L;

    private final static Logger LOG = LoggerFactory.getLogger(FourConnectionManagerFactory.class);

    private static final String MIDDLEWARE_INTERNAL_HIBERNATE_CFG = "ibpmidware_hib.cfg.xml";

    private SessionFactory sessionFactoryForDMSLocal;
    private SessionFactory sessionFactoryForDMSCentral;
    private SessionFactory sessionFactoryForGMSLocal;
    private SessionFactory sessionFactoryForGMSCentral;
    private HibernateSessionProvider sessionProviderForDMSLocal;
    private HibernateSessionProvider sessionProviderForDMSCentral;
    private HibernateSessionProvider sessionProviderForGMSLocal;
    private HibernateSessionProvider sessionProviderForGMSCentral;

    public FourConnectionManagerFactory() {
    }

    public FourConnectionManagerFactory(DatabaseConnectionParameters databaseConnectionsForGMSLocal,
            DatabaseConnectionParameters databaseConnectionsForGMSCentral, DatabaseConnectionParameters databaseConnectionsForDMSLocal,
            DatabaseConnectionParameters databaseConnectionsForDMSCentral) throws ConfigException {
        LOG.trace("Created ManagerFactory instance");

        try {
            openSessionFactory(databaseConnectionsForGMSLocal, databaseConnectionsForGMSCentral, databaseConnectionsForDMSLocal,
                    databaseConnectionsForDMSCentral);
        } catch (FileNotFoundException e) {
            throw new ConfigException(e.getMessage(), e);
        }
    }

    private void openSessionFactory(DatabaseConnectionParameters databaseConnectionsForGMSLocal,
            DatabaseConnectionParameters databaseConnectionsForGMSCentral, DatabaseConnectionParameters databaseConnectionsForDMSLocal,
            DatabaseConnectionParameters databaseConnectionsForDMSCentral) throws FileNotFoundException {

        // if local database parameters were specified,
        // create a SessionFactory for GMS local database
        if (databaseConnectionsForGMSLocal != null) {
            String connectionUrl = databaseConnectionsForGMSLocal.getUrl();

            URL urlOfCfgFile = ResourceFinder.locateFile(MIDDLEWARE_INTERNAL_HIBERNATE_CFG);

            AnnotationConfiguration cfg = new AnnotationConfiguration().configure(urlOfCfgFile);
            cfg.setProperty("hibernate.connection.url", connectionUrl);
            cfg.setProperty("hibernate.connection.username", databaseConnectionsForGMSLocal.getUsername());
            cfg.setProperty("hibernate.connection.password", databaseConnectionsForGMSLocal.getPassword());

            LOG.info("Opening SessionFactory for local database...");
            sessionFactoryForGMSLocal = cfg.buildSessionFactory();
        }

        // if central database parameters were specified,
        // create a SessionFactory for GMS central database
        if (databaseConnectionsForGMSCentral != null) {
            String connectionUrl = databaseConnectionsForGMSCentral.getUrl();

            URL urlOfCfgFile = ResourceFinder.locateFile(MIDDLEWARE_INTERNAL_HIBERNATE_CFG);

            AnnotationConfiguration cfg = new AnnotationConfiguration().configure(urlOfCfgFile);
            cfg.setProperty("hibernate.connection.url", connectionUrl);
            cfg.setProperty("hibernate.connection.username", databaseConnectionsForGMSCentral.getUsername());
            cfg.setProperty("hibernate.connection.password", databaseConnectionsForGMSCentral.getPassword());

            LOG.info("Opening SessionFactory for central database...");
            sessionFactoryForGMSCentral = cfg.buildSessionFactory();
        }

        // if local database parameters were specified,
        // create a SessionFactory for DMS local database
        if (databaseConnectionsForDMSLocal != null) {
            String connectionUrl = databaseConnectionsForDMSLocal.getUrl();

            URL urlOfCfgFile = ResourceFinder.locateFile(MIDDLEWARE_INTERNAL_HIBERNATE_CFG);

            AnnotationConfiguration cfg = new AnnotationConfiguration().configure(urlOfCfgFile);
            cfg.setProperty("hibernate.connection.url", connectionUrl);
            cfg.setProperty("hibernate.connection.username", databaseConnectionsForDMSLocal.getUsername());
            cfg.setProperty("hibernate.connection.password", databaseConnectionsForDMSLocal.getPassword());

            LOG.info("Opening SessionFactory for local database...");
            sessionFactoryForDMSLocal = cfg.buildSessionFactory();
        }

        // if central database parameters were specified,
        // create a SessionFactory for DMS central database
        if (databaseConnectionsForDMSCentral != null) {
            String connectionUrl = databaseConnectionsForDMSCentral.getUrl();

            URL urlOfCfgFile = ResourceFinder.locateFile(MIDDLEWARE_INTERNAL_HIBERNATE_CFG);

            AnnotationConfiguration cfg = new AnnotationConfiguration().configure(urlOfCfgFile);
            cfg.setProperty("hibernate.connection.url", connectionUrl);
            cfg.setProperty("hibernate.connection.username", databaseConnectionsForDMSCentral.getUsername());
            cfg.setProperty("hibernate.connection.password", databaseConnectionsForDMSCentral.getPassword());

            LOG.info("Opening SessionFactory for central database...");
            sessionFactoryForDMSCentral = cfg.buildSessionFactory();
        }

        // if no local and central database parameters were set,
        // throw a ConfigException
        if ((this.sessionFactoryForGMSLocal == null) && (this.sessionFactoryForGMSCentral == null)
                && (this.sessionFactoryForDMSLocal == null) && (this.sessionFactoryForDMSCentral == null)) {
            throw new ConfigException("No connection was established because database connection parameters were null.");
        }

        sessionProviderForGMSLocal = new HibernateSessionPerThreadProvider(sessionFactoryForGMSLocal);
        sessionProviderForGMSCentral = new HibernateSessionPerThreadProvider(sessionFactoryForGMSCentral);
        sessionProviderForDMSLocal = new HibernateSessionPerThreadProvider(sessionFactoryForDMSLocal);
        sessionProviderForDMSCentral = new HibernateSessionPerThreadProvider(sessionFactoryForDMSCentral);
    }

    public SessionFactory getSessionFactoryForDMSLocal() {
        return sessionFactoryForDMSLocal;
    }

    public void setSessionFactoryForDMSLocal(SessionFactory sessionFactoryForDMSLocal) {
        this.sessionFactoryForDMSLocal = sessionFactoryForDMSLocal;
    }

    public SessionFactory getSessionFactoryForDMSCentral() {
        return sessionFactoryForDMSCentral;
    }

    public void setSessionFactoryForDMSCentral(SessionFactory sessionFactoryForDMSCentral) {
        this.sessionFactoryForDMSCentral = sessionFactoryForDMSCentral;
    }

    public SessionFactory getSessionFactoryForGMSLocal() {
        return sessionFactoryForGMSLocal;
    }

    public void setSessionFactoryForGMSLocal(SessionFactory sessionFactoryForGMSLocal) {
        this.sessionFactoryForGMSLocal = sessionFactoryForGMSLocal;
    }

    public SessionFactory getSessionFactoryForGMSCentral() {
        return sessionFactoryForGMSCentral;
    }

    public void setSessionFactoryForGMSCentral(SessionFactory sessionFactoryForGMSCentral) {
        this.sessionFactoryForGMSCentral = sessionFactoryForGMSCentral;
    }

    public HibernateSessionProvider getSessionProviderForDMSLocal() {
        return sessionProviderForDMSLocal;
    }

    public void setSessionProviderForDMSLocal(HibernateSessionProvider sessionProviderForDMSLocal) {
        this.sessionProviderForDMSLocal = sessionProviderForDMSLocal;
    }

    public HibernateSessionProvider getSessionProviderForDMSCentral() {
        return sessionProviderForDMSCentral;
    }

    public void setSessionProviderForDMSCentral(HibernateSessionProvider sessionProviderForDMSCentral) {
        this.sessionProviderForDMSCentral = sessionProviderForDMSCentral;
    }

    public HibernateSessionProvider getSessionProviderForGMSLocal() {
        return sessionProviderForGMSLocal;
    }

    public void setSessionProviderForGMSLocal(HibernateSessionProvider sessionProviderForGMSLocal) {
        this.sessionProviderForGMSLocal = sessionProviderForGMSLocal;
    }

    public HibernateSessionProvider getSessionProviderForGMSCentral() {
        return sessionProviderForGMSCentral;
    }

    public void setSessionProviderForGMSCentral(HibernateSessionProvider sessionProviderForGMSCentral) {
        this.sessionProviderForGMSCentral = sessionProviderForGMSCentral;
    }

    public GermplasmDataManager getGermplasmDataManager() {
        return new GermplasmDataManagerImpl(sessionProviderForGMSLocal, sessionProviderForGMSCentral);
    }

    public GermplasmListManager getGermplasmListManager() {
        return new GermplasmListManagerImpl(sessionProviderForGMSLocal, sessionProviderForGMSCentral);
    }

    public TraitDataManager getTraitDataManager() {
        return new TraitDataManagerImpl(sessionProviderForDMSLocal, sessionProviderForDMSCentral);
    }

    public StudyDataManager getStudyDataManager() throws ConfigException {
        return new StudyDataManagerImpl(sessionProviderForDMSLocal, sessionProviderForDMSCentral);
    }

    public InventoryDataManager getInventoryDataManager() throws ConfigException {
        if (sessionProviderForDMSLocal == null) {
            throw new ConfigException("The InventoryDataManager needs a connection to a local IBDB instance which is not provided.");
        } else {
            return new InventoryDataManagerImpl(sessionProviderForDMSLocal, sessionProviderForDMSCentral);
        }
    }

    public GenotypicDataManager getGenotypicDataManager() throws ConfigException {
        return new GenotypicDataManagerImpl(sessionProviderForDMSLocal, sessionProviderForDMSCentral);
    }

    public UserDataManager getUserDataManager() {
        return new UserDataManagerImpl(sessionProviderForDMSLocal, sessionProviderForDMSCentral);
    }

    /**
     * Closes the db connection by shutting down the HibernateUtil object
     */
    public void close() {
        LOG.trace("Closing ManagerFactory...");

        if (sessionProviderForGMSLocal != null) {
            sessionProviderForGMSLocal.close();
        }

        if (sessionProviderForGMSCentral != null) {
            sessionProviderForGMSCentral.close();
        }

        if (sessionProviderForDMSLocal != null) {
            sessionProviderForDMSLocal.close();
        }

        if (sessionProviderForDMSCentral != null) {
            sessionProviderForDMSCentral.close();
        }

        if (sessionFactoryForGMSLocal != null && !sessionFactoryForGMSLocal.isClosed()) {
            sessionFactoryForGMSLocal.close();
        }

        if (sessionFactoryForGMSCentral != null && !sessionFactoryForGMSCentral.isClosed()) {
            sessionFactoryForGMSCentral.close();
        }

        if (sessionFactoryForDMSLocal != null && !sessionFactoryForDMSLocal.isClosed()) {
            sessionFactoryForDMSLocal.close();
        }

        if (sessionFactoryForDMSCentral != null && !sessionFactoryForDMSCentral.isClosed()) {
            sessionFactoryForDMSCentral.close();
        }

        LOG.trace("Closing ManagerFactory... DONE");
    }
}
