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
package org.generationcp.middleware.operation.transformer.etl;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.operation.builder.StandardVariableBuilder;
import org.generationcp.middleware.util.DatabaseBroker;

public abstract class Transformer extends DatabaseBroker {
	
	private StandardVariableBuilder standardVariableBuilder;
	
	protected Transformer(HibernateSessionProvider sessionProviderForLocal, HibernateSessionProvider sessionProviderForCentral) {
       super(sessionProviderForLocal, sessionProviderForCentral);
    }
	
	protected final VariableTypeListTransformer getVariableTypeListTransformer(){
		return new VariableTypeListTransformer(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	protected final MeasurementVariableTransformer getMeasurementVariableTransformer(){
	        return new MeasurementVariableTransformer(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	protected final StudyValuesTransformer getStudyValuesTransformer(){
		return new StudyValuesTransformer(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	protected final DatasetValuesTransformer getDatasetValuesTransformer(){
		return new DatasetValuesTransformer(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	protected final VariableListTransformer getVariableListTransformer(){
		return new VariableListTransformer(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	protected final ExperimentValuesTransformer getExperimentValuesTransformer(){
		return new ExperimentValuesTransformer(sessionProviderForLocal, sessionProviderForCentral);
	}
	
    protected final StandardVariableBuilder getStandardVariableBuilder() {
    	if (standardVariableBuilder == null) {
    		standardVariableBuilder = new StandardVariableBuilder(sessionProviderForLocal, sessionProviderForCentral);
    	} 
    	return standardVariableBuilder;
    }
}
