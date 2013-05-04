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

package org.generationcp.middleware.v2.domain.searcher;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.v2.util.DatabaseBroker;

/**
 * Mainly used for local-central initially.
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
    
    protected final ProjectSearcher getProjectSearcher() {
    	return new ProjectSearcher(sessionProviderForLocal, sessionProviderForCentral);
    }

}
