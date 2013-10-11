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
import org.generationcp.middleware.util.ResourceFinder;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * The {@link ServiceFactory} is a convenience class intended to provide methods
 * to get instances of the Service implementations provided by the Middleware.
 * </p>
 * 
 * @author Kevin Manansala
 * @author Glenn Marintes
 */
public class ServiceFactory implements Serializable {
    private static final long serialVersionUID = -2846462010022009403L;
    
    private final static Logger LOG = LoggerFactory.getLogger(ServiceFactory.class);

    private static final String MIDDLEWARE_INTERNAL_HIBERNATE_CFG = "ibpmidware_hib.cfg.xml";

    private String hibernateConfigurationFilename = MIDDLEWARE_INTERNAL_HIBERNATE_CFG;
    
    private SessionFactory sessionFactoryForLocal;
    private SessionFactory sessionFactoryForCentral;
    private HibernateSessionProvider sessionProviderForLocal;
    private HibernateSessionProvider sessionProviderForCentral;
    
    public ServiceFactory() {
    }
    
    public ServiceFactory(String propertyFile) {
        LOG.trace("Created ManagerFactory instance");
        
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
    
            DatabaseConnectionParameters paramsForLocal = new DatabaseConnectionParameters(localHost, localPort, localDbname, localUsername,
                    localPassword);
            DatabaseConnectionParameters paramsForCentral = new DatabaseConnectionParameters(centralHost, centralPort, centralDbname,
                    centralUsername, centralPassword);
    
            openSessionFactory(paramsForLocal, paramsForCentral);
            
            // FIXME: Do we really want to hide these exceptions?
        } catch (URISyntaxException e) {
            LOG.error(e.getMessage(), e);
        } catch (HibernateException e) {
            LOG.error(e.getMessage(), e);
        } catch (ConfigException e) {
            LOG.error(e.getMessage(), e);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }

    }
    
    public ServiceFactory(DatabaseConnectionParameters paramsForLocal, DatabaseConnectionParameters paramsForCentral)
        throws ConfigException {
        this(MIDDLEWARE_INTERNAL_HIBERNATE_CFG, paramsForLocal, paramsForCentral);
    }
    
    /**
     * This constructor accepts two DatabaseConnectionParameters objects as
     * parameters. The first is used to connect to a local instance of IBDB and
     * the second is used to connect to a central instance of IBDB. The user can
     * provide both or can provide one of the two.<br>
     * <br>
     * For example:<br>
     * <br>
     * 1. creating a ManagerFactory which uses connections to both local and
     * central instances<br>
     * <br>
     * DatabaseConnectionParameters local = new
     * DatabaseConnectionParameters(...);<br>
     * DatabaseConnectionParameters central = new
     * DatabaseConnectionParameters(...);<br>
     * ManagerFactory factory = new ManagerFactory(local, central);<br>
     * <br>
     * 2. creating a ManagerFactory which uses a connection to local only<br>
     * <br>
     * DatabaseConnectionParameters local = new
     * DatabaseConnectionParameters(...);<br>
     * ManagerFactory factory = new ManagerFactory(local, null);<br>
     * <br>
     * 3. creating a ManagerFactory which uses a connection to central only<br>
     * <br>
     * DatabaseConnectionParameters central = new
     * DatabaseConnectionParameters(...);<br>
     * ManagerFactory factory = new ManagerFactory(null, central);<br>
     * <br>
     * 
     * @param paramsForLocal
     * @param paramsForCentral
     * @throws ConfigException
     */
    public ServiceFactory(String hibernateConfigurationFilename, DatabaseConnectionParameters paramsForLocal, DatabaseConnectionParameters paramsForCentral)
            throws ConfigException {
        LOG.trace("Created ManagerFactory instance");
        
        if (hibernateConfigurationFilename != null) {
            this.hibernateConfigurationFilename = hibernateConfigurationFilename;
        }
        
         try {
            openSessionFactory(paramsForLocal, paramsForCentral);
        }
        catch (FileNotFoundException e) {
            throw new ConfigException(e.getMessage(), e);
        }
    }
    
    public String getHibernateConfigurationFilename() {
        return hibernateConfigurationFilename;
    }

    public void setHibernateConfigurationFilename(String hibernateConfigurationFile) {
        this.hibernateConfigurationFilename = hibernateConfigurationFile;
    }

    public SessionFactory getSessionFactoryForLocal() {
        return sessionFactoryForLocal;
    }
    
    public void setSessionFactoryForLocal(SessionFactory sessionFactoryForLocal) {
        this.sessionFactoryForLocal = sessionFactoryForLocal;
    }
    
    public SessionFactory getSessionFactoryForCentral() {
        return sessionFactoryForCentral;
    }
    
    public void setSessionFactoryForCentral(SessionFactory sessionFactoryForCentral) {
        this.sessionFactoryForCentral = sessionFactoryForCentral;
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

    private void openSessionFactory(DatabaseConnectionParameters paramsForLocal, DatabaseConnectionParameters paramsForCentral) throws FileNotFoundException {
        // if local database parameters were specified,
        // create a SessionFactory for local database
        if (paramsForLocal != null) {
            String connectionUrl = String.format("jdbc:mysql://%s:%s/%s", paramsForLocal.getHost()
                                                                        , paramsForLocal.getPort()
                                                                        , paramsForLocal.getDbName()
                                                                        );
            
            URL urlOfCfgFile = ResourceFinder.locateFile(hibernateConfigurationFilename);

            AnnotationConfiguration cfg = new AnnotationConfiguration().configure(urlOfCfgFile);
            cfg.setProperty("hibernate.connection.url", connectionUrl);
            cfg.setProperty("hibernate.connection.username", paramsForLocal.getUsername());
            cfg.setProperty("hibernate.connection.password", paramsForLocal.getPassword());
            
            LOG.info("Opening SessionFactory for local database...");
            sessionFactoryForLocal = cfg.buildSessionFactory();
        }
    
        // if central database parameters were specified,
        // create a SessionFactory for central database
        if (paramsForCentral != null) {
            String connectionUrl = String.format("jdbc:mysql://%s:%s/%s", paramsForCentral.getHost()
                                                 , paramsForCentral.getPort()
                                                 , paramsForCentral.getDbName()
                                                 );

            URL urlOfCfgFile = ResourceFinder.locateFile(hibernateConfigurationFilename);

            AnnotationConfiguration cfg = new AnnotationConfiguration().configure(urlOfCfgFile);
            cfg.setProperty("hibernate.connection.url", connectionUrl);
            cfg.setProperty("hibernate.connection.username", paramsForCentral.getUsername());
            cfg.setProperty("hibernate.connection.password", paramsForCentral.getPassword());

            LOG.info("Opening SessionFactory for central database...");
            sessionFactoryForCentral = cfg.buildSessionFactory();
        }
        
        // if no local and central database parameters were set,
        // throw a ConfigException
        if ((this.sessionFactoryForCentral == null)
            && (this.sessionFactoryForLocal == null)) {
            throw new ConfigException(
                "No connection was established because database connection parameters were null.");
        }
        
        sessionProviderForLocal = new HibernateSessionPerThreadProvider(sessionFactoryForLocal);
        sessionProviderForCentral = new HibernateSessionPerThreadProvider(sessionFactoryForCentral);
    }
    
    public DataImportService getDataImportService(){
    	return new DataImportServiceImpl(sessionProviderForLocal, sessionProviderForCentral);
    }


    /**
     * Closes the db connection by shutting down the HibernateUtil object
     */
    public void close() {
        LOG.trace("Closing ManagerFactory...");
        
        if(sessionProviderForLocal != null) {
            sessionProviderForLocal.close();
        }
        
        if(sessionProviderForCentral != null) {
            sessionProviderForCentral.close();
        }
        
        if (sessionFactoryForLocal != null && !sessionFactoryForLocal.isClosed()) {
            sessionFactoryForLocal.close();
        }
        
        if (sessionFactoryForCentral != null && !sessionFactoryForCentral.isClosed()) {
            sessionFactoryForCentral.close();
        }
        
        LOG.trace("Closing ManagerFactory... DONE");
    }
}
