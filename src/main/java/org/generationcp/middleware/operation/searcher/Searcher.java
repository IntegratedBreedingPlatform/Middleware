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
package org.generationcp.middleware.operation.searcher;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.util.DatabaseBroker;

/**
 * Provides searcher classes that can be used to retrieve data from IBDBv2 schema.
 * Creates searcher classes based on the given local/central session parameters.
 * 
 * @author Donald Barre
 */
public abstract class Searcher extends DatabaseBroker{

    /**
     * Instantiates a new data manager given session providers for local and central.
     */
    protected Searcher(HibernateSessionProvider sessionProviderForLocal, HibernateSessionProvider sessionProviderForCentral) {
       super(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    protected final ExperimentSearcher getExperimentSearcher() {
    	return new ExperimentSearcher(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    protected final StudySearcherByNameStartSeasonCountry getProjectSearcher() {
    	return new StudySearcherByNameStartSeasonCountry(sessionProviderForLocal, sessionProviderForCentral);
    }

}
