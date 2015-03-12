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

import java.io.Serializable;

import org.generationcp.middleware.exceptions.ConfigException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.CrossStudyDataManager;
import org.generationcp.middleware.manager.api.GenotypicDataManager;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.manager.api.MBDTDataManager;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.api.PedigreeDataManager;
import org.generationcp.middleware.manager.api.PresetDataManager;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.manager.ontology.OntologyBasicDataManagerImpl;
import org.generationcp.middleware.manager.ontology.OntologyMethodDataManagerImpl;
import org.generationcp.middleware.manager.ontology.OntologyPropertyDataManagerImpl;
import org.generationcp.middleware.manager.ontology.api.OntologyBasicDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyMethodDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyPropertyDataManager;
import org.generationcp.middleware.service.DataImportServiceImpl;
import org.generationcp.middleware.service.FieldbookServiceImpl;
import org.generationcp.middleware.service.InventoryServiceImpl;
import org.generationcp.middleware.service.OntologyServiceImpl;
import org.generationcp.middleware.service.api.DataImportService;
import org.generationcp.middleware.service.api.FieldbookService;
import org.generationcp.middleware.service.api.InventoryService;
import org.generationcp.middleware.service.api.OntologyService;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * The {@link ManagerFactory} is a convenience class intended to provide methods
 * to get instances of the Manager/Service implementations provided by the Middleware.
 * </p>
 * 
 * @author Kevin Manansala
 * @author Glenn Marintes
 */
public class ManagerFactory implements Serializable {
    private static final long serialVersionUID = -2846462010022009403L;
    
    private final static Logger LOG = LoggerFactory.getLogger(ManagerFactory.class);

    private SessionFactory sessionFactory;
    private HibernateSessionProvider sessionProvider;
    
    private String databaseName;
    
    public ManagerFactory() {
    }
    
    public SessionFactory getsessionFactory() {
        return sessionFactory;
    }
    
    public void setsessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    public HibernateSessionProvider getSessionProvider() {
        return sessionProvider;
    }

    public void setSessionProvider(HibernateSessionProvider sessionProvider) {
        this.sessionProvider = sessionProvider;
    }

    public GermplasmDataManager getGermplasmDataManager() {
        return new GermplasmDataManagerImpl(sessionProvider, databaseName);
    }

    public PedigreeDataManager getPedigreeDataManager() {
        return new PedigreeDataManagerImpl(sessionProvider, databaseName);
    }

    public CrossStudyDataManager getCrossStudyDataManager() {
        return new CrossStudyDataManagerImpl(sessionProvider);
    }

    public GermplasmListManager getGermplasmListManager() {
        return new GermplasmListManagerImpl(sessionProvider, databaseName);
    }

    public LocationDataManager getLocationDataManager() {
        return new LocationDataManagerImpl(sessionProvider);
    }

    public OntologyDataManager getOntologyDataManager() {
        return new OntologyDataManagerImpl(sessionProvider);
    }

    public OntologyBasicDataManager getOntologyBasicDataManager() {
        return new OntologyBasicDataManagerImpl(sessionProvider);
    }

    public OntologyMethodDataManager getOntologyMethodDataManager() {
        return new OntologyMethodDataManagerImpl(sessionProvider);
    }

    public OntologyPropertyDataManager getOntologyPropertyDataManager() {
        return new OntologyPropertyDataManagerImpl(sessionProvider);
    }

    public PresetDataManager getPresetDataManager() {
        return new PresetDataManagerImpl(sessionProvider);
    }

    public StudyDataManager getStudyDataManager() throws ConfigException {
        return new StudyDataManagerImpl(sessionProvider);
    }
    
    public StudyDataManager getNewStudyDataManager() throws ConfigException {
    	return new StudyDataManagerImpl(sessionProvider, databaseName);
    }

    public OntologyDataManager getNewOntologyDataManager() throws ConfigException {
    	return new OntologyDataManagerImpl(sessionProvider);
    }

    public InventoryDataManager getInventoryDataManager() throws ConfigException {
        return new InventoryDataManagerImpl(sessionProvider, databaseName);
    }
    
    public GenotypicDataManager getGenotypicDataManager() throws ConfigException {
        return new GenotypicDataManagerImpl(sessionProvider);
    }
    
    public UserDataManager getUserDataManager() {
        return new UserDataManagerImpl(sessionProvider);
    }
    
    public FieldbookService getFieldbookMiddlewareService() throws ConfigException {
        return new FieldbookServiceImpl(sessionProvider, databaseName);
    }
    
    public InventoryService getInventoryMiddlewareService() throws ConfigException {
        return new InventoryServiceImpl(sessionProvider, databaseName);
    }
    
    public DataImportService getDataImportService() throws ConfigException {
        return new DataImportServiceImpl(sessionProvider);
    }
    
    public OntologyService getOntologyService() throws ConfigException {
        return new OntologyServiceImpl(sessionProvider);
    }

    public MBDTDataManager getMbdtDataManager() {
        return new MBDTDataManagerImpl(sessionProvider);
    }
    
    /**
     * Closes the db connection by shutting down the HibernateUtil object
     */
    public void close() {
        LOG.trace("Closing ManagerFactory...");
        
        if (sessionProvider != null) {
            sessionProvider.close();
        }
        
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
        
        LOG.trace("Closing ManagerFactory...Done.");
    }

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String localDatabaseName) {
		this.databaseName = localDatabaseName;
	}
}
