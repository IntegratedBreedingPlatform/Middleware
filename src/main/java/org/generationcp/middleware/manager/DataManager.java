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

import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.util.HibernateUtil;

// TODO Local-Central: Code Review: Superclass of data manager implementations.
// Mainly used for local-central for now
/**
 * The Class DataManager.
 */
public abstract class DataManager{

    /** The hibernate util for local. */
    protected HibernateUtil hibernateUtilForLocal;

    /** The hibernate util for central. */
    protected HibernateUtil hibernateUtilForCentral;

    /** The Constant JDBC_BATCH_SIZE. */
    protected static final int JDBC_BATCH_SIZE = 50;

    /**
     * Instantiates a new data manager.
     */
    public DataManager() {
        this.hibernateUtilForLocal = null;
        this.hibernateUtilForCentral = null;

    }

    /**
     * Instantiates a new data manager.
     * 
     * @param hibernateUtilForLocal
     *            the hibernate util for local
     * @param hibernateUtilForCentral
     *            the hibernate util for central
     */
    public DataManager(HibernateUtil hibernateUtilForLocal, HibernateUtil hibernateUtilForCentral) {
        this.hibernateUtilForLocal = hibernateUtilForLocal;
        this.hibernateUtilForCentral = hibernateUtilForCentral;
    }

    /**
     * Utility function that returns the appropriate HibernateUtil based on the
     * given instance. If the instance is Database.CENTRAL, it returns
     * hibernateUtilForCentral If the instance is Database.LOCAL, it returns
     * hibernateUtilForLocal
     * 
     * @param instance
     *            the instance
     * @return the hibernate util
     * @throws QueryException
     *             the query exception
     */
    protected HibernateUtil getHibernateUtil(Database instance) throws QueryException {
        if (instance == Database.CENTRAL) {
            if (this.hibernateUtilForCentral != null) {
                return hibernateUtilForCentral;
            } else {
                throw new QueryException("The central instance was specified "
                        + "but there is no database connection for central provided.");
            }
        } else if (instance == Database.LOCAL) {
            if (this.hibernateUtilForLocal != null) {
                return hibernateUtilForLocal;
            } else {
                throw new QueryException("The local instance was specified " + "but there is no database connection for local provided.");
            }
        }
        return null;
    }

    /**
     * Utility function that returns the appropriate HibernateUtil based on the
     * given id. If the id is negative, hibernateUtilForLocal is returned If the
     * id is positive, hibernateUtilForCentral is returned
     * 
     * @param id
     *            the id
     * @return the hibernate util
     */
    protected HibernateUtil getHibernateUtil(Integer id) {
        if (id > 0 && this.hibernateUtilForCentral != null) {
            return hibernateUtilForCentral;
        } else if (id < 0 && this.hibernateUtilForLocal != null) {
            return hibernateUtilForLocal;
        }
        return null;
    }

}
