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

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.hibernate.Session;

/**
 * The Class DataManager.
 * Superclass of DataManager implementations.
 * Mainly used for local-central initially.
 * 
 * @author Joyce Avestro
 * @author Glenn Marintes
 */
public abstract class DataManager {

    public final static String NO_LOCAL_INSTANCE_MSG = "There is no connection to a local instance.";
    public final static String NO_CENTRAL_INSTANCE_MSG = "There is no connection to a central instance.";

    /**
     * The {@link HibernateSessionProvider} for local database.
     */
    private HibernateSessionProvider sessionProviderForLocal;
    
    /**
     * The {@link HibernateSessionProvider} for central database.
     */
    private HibernateSessionProvider sessionProviderForCentral;
    
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
     * Instantiates a new data manager.
     */
    public DataManager() {
    }
    
    public DataManager(HibernateSessionProvider sessionProviderForLocal, HibernateSessionProvider sessionProviderForCentral) {
        this.sessionProviderForLocal = sessionProviderForLocal;
        this.sessionProviderForCentral = sessionProviderForCentral;
    }

    /**
     * Instantiates a new data manager.
     * 
     * @param sessionForLocal
     * @param sessionForCentral
     */
    public DataManager(Session sessionForLocal, Session sessionForCentral) {
        this.sessionForLocal = sessionForLocal;
        this.sessionForCentral = sessionForCentral;
    }
    
    public HibernateSessionProvider getSessionProviderForLocal() {
        return sessionProviderForLocal;
    }

    public void setSessionProviderForLocal(HibernateSessionProvider sessionProviderForLocal) {
        this.sessionProviderForLocal = sessionProviderForLocal;
    }

    public HibernateSessionProvider getSessionProviderForCentral() {
        return sessionProviderForCentral;
    }

    public void setSessionProviderForCentral(HibernateSessionProvider sessionProviderForCentral) {
        this.sessionProviderForCentral = sessionProviderForCentral;
    }

    public Session getCurrentSessionForLocal() {
        if (sessionForLocal != null) {
            return sessionForLocal;
        }
        else if (sessionProviderForLocal != null) {
            return sessionProviderForLocal.getSession();
        }
        
        return null;
    }
    
    public Session getCurrentSessionForCentral() {
        if (sessionForCentral != null) {
            return sessionForCentral;
        }
        else if (sessionProviderForCentral != null) {
            return sessionProviderForCentral.getSession();
        }
        
        return null;
    }

    /**
     * Utility method that returns the appropriate {@link Session} based on the
     * given database instance.
     * 
     * @param instance
     * @return
     * @throws MiddlewareQueryException
     *             if a {@link Session} for the specified database instance is
     *             not available
     */
    protected Session getSession(Database instance) throws MiddlewareQueryException {
        if (instance == Database.CENTRAL) {
            Session session = getCurrentSessionForCentral();
            if (session == null) {
                throw new MiddlewareQueryException("Error in getSession(Database.CENTRAL): The central instance was specified "
                    + "but there is no database connection for central provided.");
            }
            
            return session;
        }
        else if (instance == Database.LOCAL) {
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
     * Utility method that returns the appropriate {@link Session} based on the
     * specified <code>id</code>.
     * 
     * @param id
     * @return the {@link Session} for the central database if the specified
     *         <code>id</code> is positive or equal to zero, otherwise, this
     *         method returns <code>null</code>.
     * @throws MiddlewareQueryException
     */
    protected Session getSession(int id) {
        return id >= 0 ? getCurrentSessionForCentral() : getCurrentSessionForLocal();
    }
    
    protected void requireLocalDatabaseInstance() throws MiddlewareQueryException {
        if (getCurrentSessionForLocal() == null) {
            throw new MiddlewareQueryException(NO_LOCAL_INSTANCE_MSG);
        }
    }
    
    protected void requireCentralDatabaseInstance() throws MiddlewareQueryException {
        if (getCurrentSessionForCentral() == null) {
            throw new MiddlewareQueryException(NO_CENTRAL_INSTANCE_MSG);
        }
    }
}
