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
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.OntologyDataManagerImpl;
import org.generationcp.middleware.manager.StudyDataManagerImpl;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.operation.saver.WorkbookSaver;
import org.generationcp.middleware.util.DatabaseBroker;
import org.slf4j.Logger;

public abstract class Service extends DatabaseBroker {

	public Service(){		
	}
	
	public Service(HibernateSessionProvider sessionProviderForLocal, HibernateSessionProvider sessionProviderForCentral) {
        super(sessionProviderForLocal, sessionProviderForCentral);		
	}
	
    protected void logAndThrowException(String message, Throwable e, Logger log) throws MiddlewareQueryException {
        log.error(e.getMessage(), e);
        throw new MiddlewareQueryException(message + e.getMessage(), e);
    }

    protected final WorkbookSaver getWorkbookSaver() {
        return new WorkbookSaver(sessionProviderForLocal, sessionProviderForCentral);
    }

    protected final StudyDataManager getStudyDataManager() {
        return new StudyDataManagerImpl(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    protected final OntologyDataManager getOntologyDataManager() {
        return new OntologyDataManagerImpl(sessionProviderForLocal, sessionProviderForCentral);
    }

}
