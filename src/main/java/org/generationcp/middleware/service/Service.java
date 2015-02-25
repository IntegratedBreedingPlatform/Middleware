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
	
	public Service(HibernateSessionProvider sessionProviderForLocal, HibernateSessionProvider sessionProviderForCentral) {
        super(sessionProviderForLocal, sessionProviderForCentral);		
	}
	
	public Service(HibernateSessionProvider sessionProviderForLocal, HibernateSessionProvider sessionProviderForCentral,
			String localDatabaseName, String centralDatabaseName) {
        super(sessionProviderForLocal, sessionProviderForCentral, localDatabaseName, centralDatabaseName);
    }
	
    protected void logAndThrowException(String message, Throwable e, Logger log) throws MiddlewareQueryException {
        log.error(e.getMessage(), e);
        if(e instanceof PhenotypeException) {
        	throw (PhenotypeException)e;
        }
        throw new MiddlewareQueryException(message + e.getMessage(), e);
    }

    protected final PhenotypeSaver getPhenotypeSaver() {
        return new PhenotypeSaver(sessionProviderForLocal, sessionProviderForCentral);
    }

    protected final WorkbookSaver getWorkbookSaver() {
        return new WorkbookSaver(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    protected final ExperimentPropertySaver getExperimentPropertySaver() {
        return new ExperimentPropertySaver(sessionProviderForLocal, sessionProviderForCentral);
    }

    protected final StudyDataManager getStudyDataManager() {
        return new StudyDataManagerImpl(sessionProviderForLocal, sessionProviderForCentral, localDatabaseName, centralDatabaseName);
    }
    
    protected final OntologyDataManager getOntologyDataManager() {
        return new OntologyDataManagerImpl(sessionProviderForLocal, sessionProviderForCentral);
    }

    protected final GermplasmDataManager getGermplasmDataManager() {
        return new GermplasmDataManagerImpl(sessionProviderForLocal, sessionProviderForCentral, localDatabaseName, centralDatabaseName);
    }
    
    protected final GermplasmListManager getGermplasmListManager() {
        return new GermplasmListManagerImpl(sessionProviderForLocal, sessionProviderForCentral, localDatabaseName, centralDatabaseName);
    }
    
    protected final InventoryDataManager getInventoryDataManager() {
        return new InventoryDataManagerImpl(sessionProviderForLocal, sessionProviderForCentral, localDatabaseName, centralDatabaseName);
    }
    
    protected final LocationDataManager getLocationDataManager() {
        return new LocationDataManagerImpl(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    protected final UserDataManager getUserDataManager() {
        return new UserDataManagerImpl(sessionProviderForLocal, sessionProviderForCentral);
    }

    protected WorkbookBuilder getWorkbookBuilder() {
        return new WorkbookBuilder(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    protected final ValueReferenceBuilder getValueReferenceBuilder() {
    	return new ValueReferenceBuilder(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    protected final GeolocationSaver getGeolocationSaver() {
        return new GeolocationSaver(sessionProviderForLocal, sessionProviderForCentral);
    }
        
    protected final StandardVariableBuilder getStandardVariableBuilder() {
    	return new StandardVariableBuilder(sessionProviderForLocal, sessionProviderForCentral);
    }

    protected final LotBuilder getLotBuilder() {
    	return new LotBuilder(sessionProviderForLocal, sessionProviderForCentral);
    }

    protected final ExperimentBuilder getExperimentBuilder() {
    	return new ExperimentBuilder(sessionProviderForLocal, sessionProviderForCentral);
    }

    protected final StockBuilder getStockBuilder() {
    	return new StockBuilder(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    protected final ExperimentDestroyer getExperimentDestroyer() {
    	return new ExperimentDestroyer(sessionProviderForLocal, sessionProviderForCentral);
    }

    protected final TransactionBuilder getTransactionBuilder() {
    	return new TransactionBuilder(sessionProviderForLocal, sessionProviderForCentral);
    }
    
	protected final MeasurementVariableTransformer getMeasurementVariableTransformer() {
	    return new MeasurementVariableTransformer(sessionProviderForLocal, sessionProviderForCentral);
	}
	
    protected DataSetBuilder getDataSetBuilder() {
    	return new DataSetBuilder(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    protected final TermBuilder getTermBuilder() {
    	return new TermBuilder(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    protected final StudyDestroyer getStudyDestroyer() {
    	return new StudyDestroyer(sessionProviderForLocal, sessionProviderForCentral);
    }

    protected final ListDataProjectSaver getListDataProjectSaver() {
        return new ListDataProjectSaver(sessionProviderForLocal, sessionProviderForCentral);
    }

    protected final NameBuilder getNameBuilder() {
    	return new NameBuilder(sessionProviderForLocal, sessionProviderForCentral);
    }
    
}
