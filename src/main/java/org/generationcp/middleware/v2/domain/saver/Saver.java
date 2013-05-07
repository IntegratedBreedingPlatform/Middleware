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

package org.generationcp.middleware.v2.domain.saver;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.v2.util.DatabaseBroker;

/**
 * The super class for all the Saver classes. 
 * 
 * @author Joyce Avestro
 */
public abstract class Saver extends DatabaseBroker{

    /**
     * Instantiates a new data manager given session providers for local and central.
     */
    protected Saver(HibernateSessionProvider sessionProviderForLocal, HibernateSessionProvider sessionProviderForCentral) {
       super(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    protected final StudySaver getStudySaver() {
    	return new StudySaver(sessionProviderForLocal, sessionProviderForCentral);
    }

    protected final ProjectSaver getProjectSaver() {
    	return new ProjectSaver(sessionProviderForLocal, sessionProviderForCentral);
    }

    protected final ProjectPropertySaver getProjectPropertySaver() {
    	return new ProjectPropertySaver(sessionProviderForLocal, sessionProviderForCentral);
    }

    protected final ProjectRelationshipSaver getProjectRelationshipSaver() {
    	return new ProjectRelationshipSaver(sessionProviderForLocal, sessionProviderForCentral);
    }

    protected final GeolocationSaver getGeolocationSaver() {
    	return new GeolocationSaver(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    protected final StockSaver getStockSaver() {
    	return new StockSaver(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    protected final PhenotypeSaver getPhenotypeSaver() {
    	return new PhenotypeSaver(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    protected final ExperimentModelSaver getExperimentModelSaver() {
    	return new ExperimentModelSaver(sessionProviderForLocal, sessionProviderForCentral);
    }
}
