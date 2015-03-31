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
	
	protected Transformer(HibernateSessionProvider sessionProvider) {
       super(sessionProvider);
    }
	
	protected final VariableTypeListTransformer getVariableTypeListTransformer(){
		return new VariableTypeListTransformer(sessionProvider);
	}
	
	protected final MeasurementVariableTransformer getMeasurementVariableTransformer(){
		return new MeasurementVariableTransformer(sessionProvider);
	}
	
	protected final StudyValuesTransformer getStudyValuesTransformer(){
		return new StudyValuesTransformer(sessionProvider);
	}
	
	protected final DatasetValuesTransformer getDatasetValuesTransformer(){
		return new DatasetValuesTransformer(sessionProvider);
	}
	
	protected final VariableListTransformer getVariableListTransformer(){
		return new VariableListTransformer(sessionProvider);
	}
	
	protected final ExperimentValuesTransformer getExperimentValuesTransformer(){
		return new ExperimentValuesTransformer(sessionProvider);
	}
	
    protected final StandardVariableBuilder getStandardVariableBuilder() {
    	return new StandardVariableBuilder(sessionProvider);
    }
}
