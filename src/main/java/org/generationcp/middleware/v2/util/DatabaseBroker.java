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
package org.generationcp.middleware.v2.util;

import org.generationcp.middleware.dao.CountryDAO;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.dao.UserDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.v2.dao.CVDao;
import org.generationcp.middleware.v2.dao.CVTermDao;
import org.generationcp.middleware.v2.dao.CVTermRelationshipDao;
import org.generationcp.middleware.v2.dao.DmsProjectDao;
import org.generationcp.middleware.v2.dao.ExperimentDao;
import org.generationcp.middleware.v2.dao.ExperimentProjectDao;
import org.generationcp.middleware.v2.dao.ExperimentPropertyDao;
import org.generationcp.middleware.v2.dao.ExperimentStockDao;
import org.generationcp.middleware.v2.dao.GeolocationDao;
import org.generationcp.middleware.v2.dao.GeolocationPropertyDao;
import org.generationcp.middleware.v2.dao.PhenotypeDao;
import org.generationcp.middleware.v2.dao.ProjectPropertyDao;
import org.generationcp.middleware.v2.dao.ProjectRelationshipDao;
import org.generationcp.middleware.v2.dao.StockDao;
import org.generationcp.middleware.v2.dao.StockPropertyDao;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * The Class DatabaseBroker. 
 * Mainly used to handle DAO instances and sessions connecting to the database
 * Superclass of DataManager, Builder, Searcher and Saver classes.
 * Maintains session for local and central connection.
 * 
 * @author Joyce Avestro
 */

public class DatabaseBroker {

    public final static String NO_LOCAL_INSTANCE_MSG = "There is no connection to a local instance.";
    public final static String NO_CENTRAL_INSTANCE_MSG = "There is no connection to a central instance.";

    /**
     * The {@link HibernateSessionProvider} for local database.
     */
    protected HibernateSessionProvider sessionProviderForLocal;

    /**
     * The {@link HibernateSessionProvider} for central database.
     */
    protected HibernateSessionProvider sessionProviderForCentral;

    /**
     * Hibernate {@link Session} for local.
     */
    private Session sessionForLocal;

    /**
     * Hibernate {@link Session} for central.
     */
    private Session sessionForCentral;

    /** The Constant JDBC_BATCH_SIZE. */
    protected static final int JDBC_BATCH_SIZE = 50;

    /**
     * Contains the current active session - either local or central.
     */
    private Session activeSession;
    
    private Database activeDatabase;

    protected DatabaseBroker(){
    	
    }
    /**
     * Instantiates a new data manager given session providers for local and central.
     */
    protected DatabaseBroker(HibernateSessionProvider sessionProviderForLocal, HibernateSessionProvider sessionProviderForCentral) {
        this.sessionProviderForLocal = sessionProviderForLocal;
        this.sessionProviderForCentral = sessionProviderForCentral;
    }

    public DatabaseBroker(Session sessionForLocal, Session sessionForCentral) {
        this.sessionForLocal = sessionForLocal;
        this.sessionForCentral = sessionForCentral;
    }

    public HibernateSessionProvider getSessionProviderForLocal() {
        return sessionProviderForLocal;
    }
    
    public void setSessionProviderForLocal(HibernateSessionProvider sessionProviderForLocal){
    	this.sessionProviderForLocal = sessionProviderForLocal;
    }
    
    public HibernateSessionProvider getSessionProviderForCentral() {
        return sessionProviderForCentral;
    }

    public void setSessionProviderForCentral(HibernateSessionProvider sessionProviderForCentral){
    	this.sessionProviderForCentral = sessionProviderForCentral;
    }

    /**
     * Returns the current session for local if not null, otherwise returns null
     * 
     */
    public Session getCurrentSessionForLocal() {
        if (sessionForLocal != null) {
            return sessionForLocal;
        } else if (sessionProviderForLocal != null) {
            return sessionProviderForLocal.getSession();
        }

        return null;
    }

    /**
     * Returns the current session for central if not null, otherwise returns null
     * 
     */
    public Session getCurrentSessionForCentral() {
        if (sessionForCentral != null) {
            return sessionForCentral;
        } else if (sessionProviderForCentral != null) {
            return sessionProviderForCentral.getSession();
        }

        return null;
    }

    /**
     * Utility method that returns the appropriate {@link Session} based on the given database instance.
     * 
     * @param instance
     * @return
     * @throws MiddlewareQueryException
     *             if a {@link Session} for the specified database instance is not available
     */
    protected Session getSession(Database instance) throws MiddlewareQueryException {
        if (instance == Database.CENTRAL) {
        	this.activeDatabase = instance;
            Session session = getCurrentSessionForCentral();
            if (session == null) {
                throw new MiddlewareQueryException("Error in getSession(Database.CENTRAL): The central instance was specified "
                        + "but there is no database connection for central provided.");
            }

            return session;
        } else if (instance == Database.LOCAL) {
        	this.activeDatabase = instance;
            Session session = getCurrentSessionForLocal();
            if (session == null) {
                throw new MiddlewareQueryException("Error in getSession(Database.LOCAL): The local instance was specified "
                        + "but there is no database connection for local provided.");
            }

            return session;
        }

        return null;
    }

    /**
     * Utility method that returns the appropriate {@link Session} based on the specified <code>id</code>.
     * 
     * @param id
     * @return the {@link Session} for the central database if the specified
     *         <code>id</code> is positive or equal to zero, otherwise, this
     *         method returns the {@link Session} for the local database.
     * @throws MiddlewareQueryException
     */
    protected Session getSession(int id) {
        return id >= 0 ? getCurrentSessionForCentral() : getCurrentSessionForLocal();
    }
    
    protected Database getActiveDatabase() {
    	return activeDatabase;
    }

    /**
     * Checks for the existence of a local database session. Throws an exception if not found.
     * 
     */
    protected void requireLocalDatabaseInstance() throws MiddlewareQueryException {
        if (!setWorkingDatabase(Database.LOCAL)) {
            throw new MiddlewareQueryException(NO_LOCAL_INSTANCE_MSG);
        }
    }

    /**
     * Checks for the existence of a central database session. Throws an exception if not found.
     * 
     */
    protected void requireCentralDatabaseInstance() throws MiddlewareQueryException {
        if (!setWorkingDatabase(Database.CENTRAL)) {
            throw new MiddlewareQueryException(NO_CENTRAL_INSTANCE_MSG);
        }
    }

    /**
     * Sets the session of a given DAO. Returns true if successful. 
     * 
     */
    @SuppressWarnings("rawtypes")
	protected boolean setDaoSession(GenericDAO dao, Session session) {
        if (session != null) {
            dao.setSession(session);
            return true;
        }
        return false;
    }
    
	@SuppressWarnings("rawtypes")
	protected boolean setDaoSession(GenericDAO dao, Integer id) {
		if (setWorkingDatabase(id)) {
			dao.setSession(activeSession);
			return true;
		}
		return false;
	}

    /** 
     * Retrieves the current active session - either local or central database connection.
     * 
     * @return
     */
    protected Session getActiveSession() {
        return activeSession;
    }

    /**
     * Sets the current active session - either local or central.
     * @param session
     */
    protected void setActiveSession(Session session) {
        this.activeSession = session;
    }

    /**
     * Sets the active session based on the given instance. <br/>
     * Returns true if the active session is not null.      <br/>
     * @param instance 
     *          The database instance - either Database.LOCAL or Database.CENTRAL
     */
    protected boolean setWorkingDatabase(Database instance) {
        if (instance == Database.LOCAL) {
            activeSession = getCurrentSessionForLocal();
            activeDatabase = instance;
        } else if (instance == Database.CENTRAL) {
            activeSession = getCurrentSessionForCentral();
            activeDatabase = instance;
        }
        if (activeSession != null) {
            return true;
        }
        return false;
    }

    /**
     * Sets the active session based on the given ID.   <br/>
     * If the id is positive, the central connection is assigned as the active session. <br/> 
     * If the id is negative, the local connection is assigned as the active session.  <br/>
     * Returns true if the active session is not null. <br/>
     * 
     * @param id
     */
    protected boolean setWorkingDatabase(Integer id) {
    	if (id != null) {
	        activeSession = id >= 0 ? getCurrentSessionForCentral() : getCurrentSessionForLocal();
	        activeDatabase = id >= 0 ? Database.CENTRAL : Database.LOCAL;
	        if (activeSession != null) {
	            return true;
	        }
    	}
        return false;
    }

    /**
     * Sets the active session based on the session.     <br/>
     * Returns true if the active session is not null.   <br/>
     * 
     * @param session The session to assign
     */
    protected boolean setWorkingDatabase(Session session) {
        activeSession = session;
        activeDatabase = session == getCurrentSessionForCentral() ? Database.CENTRAL : Database.LOCAL;
        if (activeSession != null) {
            return true;
        }
        return false;
    }

    /**
     * Sets the active session based on the given instance.     <br/>
     * Returns true if the active session is not null.          <br/>
     * @param instance  The database instance - either Database.LOCAL or Database.CENTRAL
     * @param dao   The DAO to set the active session into
     */
    @SuppressWarnings("rawtypes")
    protected boolean setWorkingDatabase(Database instance, GenericDAO dao) {
        if (instance == Database.LOCAL) {
            activeSession = getCurrentSessionForLocal();
            activeDatabase = instance;
        } else if (instance == Database.CENTRAL) {
            activeSession = getCurrentSessionForCentral();
            activeDatabase = instance;
        }
        if (activeSession != null) {
            return setDaoSession(dao, activeSession);
        }
        return false;
    }

    /**
     * Sets the active session based on the given instance.         <br/>
     * Returns true if the active session is not null.              <br/>
     * @param id
     *          If the given id is positive, the session is set to Central.
     *          If the given id is negative, the session is set to Local.
     * @param dao 
     *          The DAO to set the active session into
     */
    @SuppressWarnings("rawtypes")
    protected boolean setWorkingDatabase(Integer id, GenericDAO dao) {
        if (id < 0) {
            activeSession = getCurrentSessionForLocal();
            activeDatabase = Database.LOCAL;
        } else if (id >= 0) {
            activeSession = getCurrentSessionForCentral();
            activeDatabase = Database.CENTRAL;
        }
        if (activeSession != null) {
            return setDaoSession(dao, activeSession);
        }
        return false;
    }
    
    /**
     * Rolls back a given transaction
     * 
     * @param trans
     */
    public void rollbackTransaction(Transaction trans) {
        if (trans != null) {
            trans.rollback();
        }
    }


    //================================  Get DAO Methods =============================

    protected final DmsProjectDao getDmsProjectDao() {
	    DmsProjectDao dmsProjectDao = new DmsProjectDao();
		dmsProjectDao.setSession(getActiveSession());
		return dmsProjectDao;
	}
    
    protected final CVTermDao getCvTermDao() {
    	CVTermDao cvTermDao = new CVTermDao();
    	cvTermDao.setSession(getActiveSession());
		return cvTermDao;
	}
    
    protected final CVTermRelationshipDao getCvTermRelationshipDao() {
    	CVTermRelationshipDao cvTermRelationshipDao = new CVTermRelationshipDao();
    	cvTermRelationshipDao.setSession(getActiveSession());
		return cvTermRelationshipDao;
	}
    
    protected final CountryDAO getCountryDao() {
    	CountryDAO countryDao = new CountryDAO();
    	countryDao.setSession(getActiveSession());
    	return countryDao;
    }
    
    protected final UserDAO getUserDao() {
    	UserDAO userDao = new UserDAO();
    	userDao.setSession(getActiveSession());
    	return userDao;
    }
    
    protected final CVDao getCvDao() {
    	CVDao cvDao = new CVDao();
    	cvDao.setSession(getActiveSession());
    	return cvDao;
    }
    
    protected final StockDao getStockDao() {
    	StockDao stockDao = new StockDao();
    	stockDao.setSession(getActiveSession());
    	return stockDao;
    }
    
    protected final GeolocationPropertyDao getGeolocationPropertyDao() {
    	GeolocationPropertyDao geolocationPropertyDao = new GeolocationPropertyDao();
    	geolocationPropertyDao.setSession(getActiveSession());
    	return geolocationPropertyDao;
    }
    
    protected final ExperimentDao getExperimentDao() {
    	ExperimentDao experimentDao = new ExperimentDao();
    	experimentDao.setSession(getActiveSession());
    	return experimentDao;
    }
    
    protected final ExperimentPropertyDao getExperimentPropertyDao() {
    	ExperimentPropertyDao experimentPropertyDao = new ExperimentPropertyDao();
    	experimentPropertyDao.setSession(getActiveSession());
    	return experimentPropertyDao;
    }
    
    protected final StockPropertyDao getStockPropertyDao() {
    	StockPropertyDao stockPropertyDao = new StockPropertyDao();
    	stockPropertyDao.setSession(getActiveSession());
    	return stockPropertyDao;
    }

    protected final ExperimentStockDao getExperimentStockDao() {
    	ExperimentStockDao experimentStockDao = new ExperimentStockDao();
    	experimentStockDao.setSession(getActiveSession());
    	return experimentStockDao;
    }
    
    protected final ExperimentProjectDao getExperimentProjectDao() {
    	ExperimentProjectDao experimentProjectDao = new ExperimentProjectDao();
    	experimentProjectDao.setSession(getActiveSession());
    	return experimentProjectDao;
    }
    
    protected final ProjectPropertyDao getProjectPropertyDao() {
    	ProjectPropertyDao projectPropertyDao = new ProjectPropertyDao();
		projectPropertyDao.setSession(getActiveSession());
		return projectPropertyDao;
	}
    
    protected final ProjectRelationshipDao getProjectRelationshipDao() {
    	ProjectRelationshipDao projectRelationshipDao = new ProjectRelationshipDao();
		projectRelationshipDao.setSession(getActiveSession());
		return projectRelationshipDao;
	}
    
    protected final GeolocationDao getGeolocationDao() {
    	GeolocationDao geolocationDao = new GeolocationDao();
    	geolocationDao.setSession(getActiveSession());
    	return geolocationDao;
    }

    protected final PhenotypeDao getPhenotypeDao() {
    	PhenotypeDao phenotypeDao = new PhenotypeDao();
    	phenotypeDao.setSession(getActiveSession());
    	return phenotypeDao;
    }

}
