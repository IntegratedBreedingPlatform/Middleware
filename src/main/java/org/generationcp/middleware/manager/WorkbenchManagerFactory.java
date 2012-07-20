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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.Properties;

import org.generationcp.middleware.exceptions.ConfigException;
import org.generationcp.middleware.manager.api.GenotypicDataManager;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.util.HibernateUtil;
import org.generationcp.middleware.util.ResourceFinder;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The WorkbenchManagerFactory gives access to the workbenchmanager implementation
 * class. This class takes care of opening and closing the connection to the
 * databases.
 * 
 */
public class WorkbenchManagerFactory implements Serializable{

    private final static Logger LOG = LoggerFactory.getLogger(WorkbenchManagerFactory.class);
	
    private static final long serialVersionUID = -2846462010022009403L;

    private HibernateUtil hibernateUtilForWorkBench;
    
    private String host;
    private String port;
    private String dbName;
    private String userName;
    private String password;
    
    public WorkbenchManagerFactory() {
    
	    Properties prop = new Properties();
	
	    try {
	        InputStream in = null;
	
	        try {
	            in = new FileInputStream(new File(ResourceFinder.locateFile("Workbench.properties").toURI()));
	        } catch (IllegalArgumentException ex) {
	            in = Thread.currentThread().getContextClassLoader().getResourceAsStream("Workbench.properties");
	        }
	        prop.load(in);
	
	        host = prop.getProperty("host");
	        dbName = prop.getProperty("dbname");
	        port = prop.getProperty("port");
	        userName = prop.getProperty("username");
	        password = prop.getProperty("password");
	
	        in.close();
	
	        DatabaseConnectionParameters paramsForWorkbench = new DatabaseConnectionParameters(host, port, dbName, userName, password);
	
	        validateDatabaseParameters(paramsForWorkbench);
	        
	    } catch (URISyntaxException e) {
                LOG.error(e.toString() + "\n" + e.getStackTrace());
	    } catch (HibernateException e) {
	        LOG.error(e.toString() + "\n" + e.getStackTrace());
	    } catch (ConfigException e) {
	        LOG.error(e.toString() + "\n" + e.getStackTrace());
	    } catch (IOException e) {
	        LOG.error(e.toString() + "\n" + e.getStackTrace());
	    }

    }
    
    /**
     * 
     * @param paramsForLocal
     * @throws ConfigException
     */
    public WorkbenchManagerFactory(DatabaseConnectionParameters paramsForLocal)
            throws ConfigException {
    	
    	 validateDatabaseParameters(paramsForLocal);
    	 
    }
    
    private void validateDatabaseParameters(DatabaseConnectionParameters paramsForWorkbench) {
    	
		// instantiate HibernateUtil with given db connection parameters
		// one for local
		if (paramsForWorkbench != null) {
		    hibernateUtilForWorkBench = new HibernateUtil(
		    		paramsForWorkbench.getHost(), paramsForWorkbench.getPort(),
		    		paramsForWorkbench.getDbName(), paramsForWorkbench.getUsername(),
		    		paramsForWorkbench.getPassword());
		} else {
		    hibernateUtilForWorkBench = null;
		}
	
		if ((hibernateUtilForWorkBench == null)) {
		    throw new ConfigException(
			    "No connection was established because database connection parameters were null.");
		}
    	
    }


    public WorkbenchDataManager getWorkBenchDataManager() throws ConfigException {
        if (this.hibernateUtilForWorkBench == null) {
            throw new ConfigException("The WorkBenchDataManager needs a connection to a local IBDB instance which is not provided.");
        } else {
            return new WorkbenchDataManagerImpl(this.hibernateUtilForWorkBench);
        }
    }

    /**
     * Closes the db connection by shutting down the HibernateUtil object
     */
    public void close() {
        if (this.hibernateUtilForWorkBench != null) {
            this.hibernateUtilForWorkBench.shutdown();
        }
    }
}
