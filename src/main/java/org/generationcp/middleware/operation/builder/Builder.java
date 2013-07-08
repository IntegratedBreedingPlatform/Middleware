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
package org.generationcp.middleware.operation.builder;


import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.util.DatabaseBroker;

/**
 * Provides builder classes (DatasetBuilder, StudyVariableBuilder, etc) based on the given session for local / central.
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
    
    protected final DataSetBuilder getDataSetBuilder() {
    	return new DataSetBuilder(sessionProviderForLocal, sessionProviderForCentral);
    }
	
    protected final StudyVariableBuilder getStudyVariableBuilder() {
    	return new StudyVariableBuilder(sessionProviderForLocal, sessionProviderForCentral);
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
    
    protected final StockModelBuilder getStockBuilder() {
    	return new StockModelBuilder(sessionProviderForLocal, sessionProviderForCentral);
    }

    protected final StandardVariableBuilder getStandardVariableBuilder() {
    	return new StandardVariableBuilder(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    protected final TermBuilder getTermBuilder() {
    	return new TermBuilder(sessionProviderForLocal, sessionProviderForCentral);
    }
}
