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

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.exceptions.PhenotypeException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.*;
import org.generationcp.middleware.manager.api.*;
import org.generationcp.middleware.manager.ontology.*;
import org.generationcp.middleware.manager.ontology.api.*;
import org.generationcp.middleware.operation.builder.*;
import org.generationcp.middleware.operation.destroyer.ExperimentDestroyer;
import org.generationcp.middleware.operation.destroyer.StudyDestroyer;
import org.generationcp.middleware.operation.saver.*;
import org.generationcp.middleware.operation.transformer.etl.MeasurementVariableTransformer;
import org.generationcp.middleware.util.DatabaseBroker;
import org.slf4j.Logger;

public abstract class Service extends DatabaseBroker {

	public Service(){		
	}
	
	public Service(HibernateSessionProvider sessionProvider) {
        super(sessionProvider);		
	}
	
	public Service(HibernateSessionProvider sessionProvider, String databaseName) {
        super(sessionProvider, databaseName);
    }
	
    protected void logAndThrowException(String message, Throwable e, Logger log) throws MiddlewareQueryException {
        log.error(e.getMessage(), e);
        if(e instanceof PhenotypeException) {
        	throw (PhenotypeException)e;
        }
        throw new MiddlewareQueryException(message + e.getMessage(), e);
    }

    protected final PhenotypeSaver getPhenotypeSaver() {
        return new PhenotypeSaver(sessionProvider);
    }

    protected final WorkbookSaver getWorkbookSaver() {
        return new WorkbookSaver(sessionProvider);
    }
    
    protected final ExperimentPropertySaver getExperimentPropertySaver() {
        return new ExperimentPropertySaver(sessionProvider);
    }

    protected final StudyDataManager getStudyDataManager() {
        return new StudyDataManagerImpl(sessionProvider, databaseName);
    }
    
    protected final OntologyDataManager getOntologyDataManager() {
        return new OntologyDataManagerImpl(sessionProvider);
    }

    protected final OntologyBasicDataManager getOntologyBasicDataManager(){
        return new OntologyBasicDataManagerImpl(sessionProvider);
    }

    protected final OntologyMethodDataManager getOntologyMethodDataManager(){
        return new OntologyMethodDataManagerImpl(sessionProvider);
    }

    protected final OntologyPropertyDataManager getOntologyPropertyDataManager(){
        return new OntologyPropertyDataManagerImpl(getOntologyBasicDataManager(), sessionProvider);
    }

    protected final OntologyScaleDataManager getOntologyScaleDataManager(){
        return new OntologyScaleDataManagerImpl(sessionProvider);
    }

    protected final OntologyVariableDataManager getOntologyVariableDataManager(){
        return new OntologyVariableDataManagerImpl(getOntologyBasicDataManager(), getOntologyMethodDataManager(), getOntologyPropertyDataManager(), getOntologyScaleDataManager(), sessionProvider);
    }

    protected final GermplasmDataManager getGermplasmDataManager() {
        return new GermplasmDataManagerImpl(sessionProvider, databaseName);
    }
    
    protected final GermplasmListManager getGermplasmListManager() {
        return new GermplasmListManagerImpl(sessionProvider, databaseName);
    }
    
    protected final InventoryDataManager getInventoryDataManager() {
        return new InventoryDataManagerImpl(sessionProvider, databaseName);
    }
    
    protected final LocationDataManager getLocationDataManager() {
        return new LocationDataManagerImpl(sessionProvider);
    }
    
    protected final UserDataManager getUserDataManager() {
        return new UserDataManagerImpl(sessionProvider);
    }

    protected WorkbookBuilder getWorkbookBuilder() {
        return new WorkbookBuilder(sessionProvider);
    }
    
    protected final ValueReferenceBuilder getValueReferenceBuilder() {
    	return new ValueReferenceBuilder(sessionProvider);
    }
    
    protected final GeolocationSaver getGeolocationSaver() {
        return new GeolocationSaver(sessionProvider);
    }
        
    protected final StandardVariableBuilder getStandardVariableBuilder() {
    	return new StandardVariableBuilder(sessionProvider);
    }

    protected final LotBuilder getLotBuilder() {
    	return new LotBuilder(sessionProvider);
    }

    protected final ExperimentBuilder getExperimentBuilder() {
    	return new ExperimentBuilder(sessionProvider);
    }

    protected final StockBuilder getStockBuilder() {
    	return new StockBuilder(sessionProvider);
    }
    
    protected final ExperimentDestroyer getExperimentDestroyer() {
    	return new ExperimentDestroyer(sessionProvider);
    }

    protected final TransactionBuilder getTransactionBuilder() {
    	return new TransactionBuilder(sessionProvider);
    }
    
	protected final MeasurementVariableTransformer getMeasurementVariableTransformer() {
	    return new MeasurementVariableTransformer(sessionProvider);
	}
	
    protected DataSetBuilder getDataSetBuilder() {
    	return new DataSetBuilder(sessionProvider);
    }
    
    protected final TermBuilder getTermBuilder() {
    	return new TermBuilder(sessionProvider);
    }
    
    protected final StudyDestroyer getStudyDestroyer() {
    	return new StudyDestroyer(sessionProvider);
    }

    protected final ListDataProjectSaver getListDataProjectSaver() {
        return new ListDataProjectSaver(sessionProvider);
    }

    protected final NameBuilder getNameBuilder() {
    	return new NameBuilder(sessionProvider);
    }
    
}
