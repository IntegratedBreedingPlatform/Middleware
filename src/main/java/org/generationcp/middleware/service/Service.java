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
	
	public Service(HibernateSessionProvider sessionProviderForLocal) {
        super(sessionProviderForLocal);		
	}
	
	public Service(HibernateSessionProvider sessionProviderForLocal, String localDatabaseName) {
        super(sessionProviderForLocal, localDatabaseName);
    }
	
    protected void logAndThrowException(String message, Throwable e, Logger log) throws MiddlewareQueryException {
        log.error(e.getMessage(), e);
        if(e instanceof PhenotypeException) {
        	throw (PhenotypeException)e;
        }
        throw new MiddlewareQueryException(message + e.getMessage(), e);
    }

    protected final PhenotypeSaver getPhenotypeSaver() {
        return new PhenotypeSaver(sessionProviderForLocal);
    }

    protected final WorkbookSaver getWorkbookSaver() {
        return new WorkbookSaver(sessionProviderForLocal);
    }
    
    protected final ExperimentPropertySaver getExperimentPropertySaver() {
        return new ExperimentPropertySaver(sessionProviderForLocal);
    }

    protected final StudyDataManager getStudyDataManager() {
        return new StudyDataManagerImpl(sessionProviderForLocal, localDatabaseName);
    }
    
    protected final OntologyDataManager getOntologyDataManager() {
        return new OntologyDataManagerImpl(sessionProviderForLocal);
    }

    protected final GermplasmDataManager getGermplasmDataManager() {
        return new GermplasmDataManagerImpl(sessionProviderForLocal, localDatabaseName);
    }
    
    protected final GermplasmListManager getGermplasmListManager() {
        return new GermplasmListManagerImpl(sessionProviderForLocal, localDatabaseName);
    }
    
    protected final InventoryDataManager getInventoryDataManager() {
        return new InventoryDataManagerImpl(sessionProviderForLocal, localDatabaseName);
    }
    
    protected final LocationDataManager getLocationDataManager() {
        return new LocationDataManagerImpl(sessionProviderForLocal);
    }
    
    protected final UserDataManager getUserDataManager() {
        return new UserDataManagerImpl(sessionProviderForLocal);
    }

    protected final WorkbookBuilder getWorkbookBuilder() {
        return new WorkbookBuilder(sessionProviderForLocal);
    }
    
    protected final ValueReferenceBuilder getValueReferenceBuilder() {
    	return new ValueReferenceBuilder(sessionProviderForLocal);
    }
    
    protected final GeolocationSaver getGeolocationSaver() {
        return new GeolocationSaver(sessionProviderForLocal);
    }
        
    protected final StandardVariableBuilder getStandardVariableBuilder() {
    	return new StandardVariableBuilder(sessionProviderForLocal);
    }

    protected final LotBuilder getLotBuilder() {
    	return new LotBuilder(sessionProviderForLocal);
    }

    protected final ExperimentBuilder getExperimentBuilder() {
    	return new ExperimentBuilder(sessionProviderForLocal);
    }

    protected final StockBuilder getStockBuilder() {
    	return new StockBuilder(sessionProviderForLocal);
    }
    
    protected final ExperimentDestroyer getExperimentDestroyer() {
    	return new ExperimentDestroyer(sessionProviderForLocal);
    }

    protected final TransactionBuilder getTransactionBuilder() {
    	return new TransactionBuilder(sessionProviderForLocal);
    }
    
	protected final MeasurementVariableTransformer getMeasurementVariableTransformer() {
	    return new MeasurementVariableTransformer(sessionProviderForLocal);
	}
	
    protected final DataSetBuilder getDataSetBuilder() {
    	return new DataSetBuilder(sessionProviderForLocal);
    }
    
    protected final TermBuilder getTermBuilder() {
    	return new TermBuilder(sessionProviderForLocal);
    }
    
    protected final StudyDestroyer getStudyDestroyer() {
    	return new StudyDestroyer(sessionProviderForLocal);
    }

    protected final ListDataProjectSaver getListDataProjectSaver() {
        return new ListDataProjectSaver(sessionProviderForLocal);
    }

    protected final NameBuilder getNameBuilder() {
    	return new NameBuilder(sessionProviderForLocal);
    }
    
}
