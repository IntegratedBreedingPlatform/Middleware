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

import org.generationcp.commons.util.ResourceFinder;
import org.generationcp.middleware.exceptions.ConfigException;
import org.generationcp.middleware.manager.api.GenotypicDataManager;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.TraitDataManager;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The ManagerFactory gives access to the different Manager implementation
 * classes. This class takes care of opening and closing the connection to the
 * databases.
 * 
 */
public class ManagerFactory implements Serializable{

    private final static Logger LOG = LoggerFactory.getLogger(ManagerFactory.class);
    
    private static final long serialVersionUID = -2846462010022009403L;

    private HibernateUtil hibernateUtilForLocal;
    private HibernateUtil hibernateUtilForCentral;
    
    private String localHost;
    private String localPort;
    private String localDbname;
    private String localUsername;
    private String localPassword;

    private String centralHost;
    private String centralPort;
    private String centralDbname;
    private String centralUsername;
    private String centralPassword;

    
    public ManagerFactory() {
        LOG.trace("Created ManagerFactory instance");
        
        Properties prop = new Properties();
    
        try {
            InputStream in = null;
    
            try {
                in = new FileInputStream(new File(ResourceFinder.locateFile("IBPDatasource.properties").toURI()));
            } catch (IllegalArgumentException ex) {
                in = Thread.currentThread().getContextClassLoader().getResourceAsStream("IBPDatasource.properties");
            }
            prop.load(in);
    
            localHost = prop.getProperty("local.host");
            localDbname = prop.getProperty("local.dbname");
            localPort = prop.getProperty("local.port");
            localUsername = prop.getProperty("local.username");
            localPassword = prop.getProperty("local.password");
    
            centralHost = prop.getProperty("central.host");
            centralDbname = prop.getProperty("central.dbname");
            centralPort = prop.getProperty("central.port");
            centralUsername = prop.getProperty("central.username");
            centralPassword = prop.getProperty("central.password");
    
            in.close();
    
            DatabaseConnectionParameters paramsForLocal = new DatabaseConnectionParameters(localHost, localPort, localDbname, localUsername,
                    localPassword);
            DatabaseConnectionParameters paramsForCentral = new DatabaseConnectionParameters(centralHost, centralPort, centralDbname,
                    centralUsername, centralPassword);
    
            validateDatabaseParameters(paramsForLocal, paramsForCentral);
            
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
     * This constructor accepts two DatabaseConnectionParameters objects as
     * parameters. The first is used to connect to a local instance of IBDB and
     * the second is used to connect to a central instance of IBDB. The user can
     * provide both or can provide one of the two.</br> </br> For example:</br>
     * </br> 1. creating a ManagerFactory which uses connections to both local
     * and central instances</br> </br> DatabaseConnectionParameters local = new
     * DatabaseConnectionParameters(...);</br> DatabaseConnectionParameters
     * central = new DatabaseConnectionParameters(...);</br> ManagerFactory
     * factory = new ManagerFactory(local, central);</br> </br> 2. creating a
     * ManagerFactory which uses a connection to local only</br> </br>
     * DatabaseConnectionParameters local = new
     * DatabaseConnectionParameters(...);</br> ManagerFactory factory = new
     * ManagerFactory(local, null);</br> </br> 3. creating a ManagerFactory
     * which uses a connection to central only</br> </br>
     * DatabaseConnectionParameters central = new
     * DatabaseConnectionParameters(...);</br> ManagerFactory factory = new
     * ManagerFactory(null, central);</br> </br>
     * 
     * @param paramsForLocal
     * @param paramsForCentral
     * @throws ConfigException
     */
    public ManagerFactory(DatabaseConnectionParameters paramsForLocal, DatabaseConnectionParameters paramsForCentral)
            throws ConfigException {
        LOG.trace("Created ManagerFactory instance");
        
         validateDatabaseParameters(paramsForLocal, paramsForCentral);
         
    }
    
    private void validateDatabaseParameters(DatabaseConnectionParameters paramsForLocal, DatabaseConnectionParameters paramsForCentral) {
        
        // instantiate HibernateUtil with given db connection parameters
        // one for local
        if (paramsForLocal != null) {
            this.hibernateUtilForLocal = new HibernateUtil(
                paramsForLocal.getHost(), paramsForLocal.getPort(),
                paramsForLocal.getDbName(), paramsForLocal.getUsername(),
                paramsForLocal.getPassword());
        } else {
            this.hibernateUtilForLocal = null;
        }
    
        // one for central
        if (paramsForCentral != null) {
            this.hibernateUtilForCentral = new HibernateUtil(
                paramsForCentral.getHost(), paramsForCentral.getPort(),
                paramsForCentral.getDbName(),
                paramsForCentral.getUsername(),
                paramsForCentral.getPassword());
        } else {
            this.hibernateUtilForCentral = null;
        }
    
        if ((this.hibernateUtilForCentral == null)
            && (this.hibernateUtilForLocal == null)) {
            throw new ConfigException(
                "No connection was established because database connection parameters were null.");
        }
        
    }

    public GermplasmDataManager getGermplasmDataManager() {
        return new GermplasmDataManagerImpl(this.hibernateUtilForLocal, this.hibernateUtilForCentral);
    }

    public GermplasmListManager getGermplasmListManager() {
        return new GermplasmListManagerImpl(this.hibernateUtilForLocal, this.hibernateUtilForCentral);
    }

    public TraitDataManager getTraitDataManager() {
        return new TraitDataManagerImpl(this.hibernateUtilForLocal, this.hibernateUtilForCentral);
    }

    public StudyDataManager getStudyDataManager() throws ConfigException {
        return new StudyDataManagerImpl(this.hibernateUtilForLocal, this.hibernateUtilForCentral);
    }

    public InventoryDataManager getInventoryDataManager() throws ConfigException {
        if (this.hibernateUtilForLocal == null) {
            throw new ConfigException("The InventoryDataManager needs a connection to a local IBDB instance which is not provided.");
        } else {
            return new InventoryDataManagerImpl(this.hibernateUtilForLocal, this.hibernateUtilForCentral);
        }
    }
    
    public GenotypicDataManager getGenotypicDataManager() throws ConfigException {
        return new GenotypicDataManagerImpl(this.hibernateUtilForLocal, this.hibernateUtilForCentral);
    }
    
    public UserDataManager getUserDataManager() {
        
        return new UserDataManagerImpl(hibernateUtilForLocal, hibernateUtilForCentral);
    }

    /**
     * Closes the db connection by shutting down the HibernateUtil object
     */
    public void close() {
        LOG.trace("Closing ManagerFactory...");
        
        if (this.hibernateUtilForLocal != null) {
            this.hibernateUtilForLocal.shutdown();
        }

        if (this.hibernateUtilForCentral != null) {
            this.hibernateUtilForCentral.shutdown();
        }
        
        LOG.trace("Closing ManagerFactory... DONE");
    }
}
