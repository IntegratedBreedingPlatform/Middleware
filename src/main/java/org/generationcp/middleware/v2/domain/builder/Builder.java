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

package org.generationcp.middleware.v2.domain.builder;


import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.v2.util.DatabaseBroker;

/**
 * The Class Builder (stolen from DataManager).
 * Mainly used for local-central initially.
 * 
 * @author Donald Barre
 */
public abstract class Builder extends DatabaseBroker {
	
	public Builder(HibernateSessionProvider sessionProviderForLocal, HibernateSessionProvider sessionProviderForCentral) {
        super(sessionProviderForLocal, sessionProviderForCentral);		
	}

    
    protected final StudyBuilder getStudyBuilder() {
    	return new StudyBuilder(sessionProviderForLocal, sessionProviderForCentral);
    }
	
    protected final VariableBuilder getVariableBuilder() {
    	return new VariableBuilder(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    protected final VariableInfoBuilder getVariableInfoBuilder() {
    	return new VariableInfoBuilder();
    }
    
    protected final VariableTypeBuilder getVariableTypeBuilder() {
    	return new VariableTypeBuilder(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    protected final ExperimentBuilder getExperimentBuilder() {
    	return new ExperimentBuilder(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    protected final StockBuilder getStockBuilder() {
    	return new StockBuilder(sessionProviderForLocal, sessionProviderForCentral);
    }

    protected final StandardVariableBuilder getStandardVariableBuilder() {
    	return new StandardVariableBuilder(sessionProviderForLocal, sessionProviderForCentral);
    }
}
