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
package org.generationcp.middleware.operation.saver;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.operation.builder.ExperimentBuilder;
import org.generationcp.middleware.operation.builder.StandardVariableBuilder;
import org.generationcp.middleware.operation.builder.StockModelBuilder;
import org.generationcp.middleware.operation.builder.TermBuilder;
import org.generationcp.middleware.operation.builder.TermPropertyBuilder;
import org.generationcp.middleware.operation.builder.VariableTypeBuilder;
import org.generationcp.middleware.operation.transformer.etl.DatasetValuesTransformer;
import org.generationcp.middleware.operation.transformer.etl.ExperimentValuesTransformer;
import org.generationcp.middleware.operation.transformer.etl.StudyValuesTransformer;
import org.generationcp.middleware.operation.transformer.etl.VariableListTransformer;
import org.generationcp.middleware.operation.transformer.etl.VariableTypeListTransformer;
import org.generationcp.middleware.util.DatabaseBroker;

/**
 * Provides saver classes that can be used to save logical/physical data in IBDBv2.
 * Creates saver classes based on the given local/central session parameters.
 * The super class of all the Saver classes. 
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
    
    protected final StandardVariableBuilder getStandardVariableBuilder() {
    	return new StandardVariableBuilder(sessionProviderForLocal, sessionProviderForCentral);
    }

	protected final VariableTypeListTransformer getVariableTypeListTransformer() {
		return new VariableTypeListTransformer(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	protected final VariableListTransformer getVariableListTransformer() {
		return new VariableListTransformer(sessionProviderForLocal, sessionProviderForCentral);
	}

	protected final StudyValuesTransformer getStudyValuesTransformer() {
		return new StudyValuesTransformer(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	protected final DatasetValuesTransformer getDatasetValuesTransformer() {
		return new DatasetValuesTransformer(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	protected final ExperimentValuesTransformer getExperimentValuesTransformer() {
		return new ExperimentValuesTransformer(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	protected final DatasetProjectSaver getDatasetProjectSaver() {
		return new DatasetProjectSaver(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	protected final VariableTypeBuilder getVariableTypeBuilder() {
		return new VariableTypeBuilder(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	protected final StockModelBuilder getStockModelBuilder() {
		return new StockModelBuilder(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	protected final TermBuilder getTermBuilder() {
	    return new TermBuilder(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	protected final TermPropertyBuilder getTermPropertyBuilder() {
	    return new TermPropertyBuilder(sessionProviderForLocal, sessionProviderForCentral);
	}

    protected final ExperimentBuilder getExperimentBuilder() {
        return new ExperimentBuilder(sessionProviderForLocal, sessionProviderForCentral);
    }

    protected final ExperimentPropertySaver getExperimentPropertySaver() {
        return new ExperimentPropertySaver(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    protected final ListDataPropertySaver getListDataPropertySaver() {
        return new ListDataPropertySaver(sessionProviderForLocal, sessionProviderForCentral);
    }
}
