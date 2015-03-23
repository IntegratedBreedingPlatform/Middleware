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
import org.generationcp.middleware.manager.StudyDataManagerImpl;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.operation.builder.*;
import org.generationcp.middleware.operation.destroyer.ExperimentDestroyer;
import org.generationcp.middleware.operation.transformer.etl.*;
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
    protected Saver(HibernateSessionProvider sessionProvider) {
       super(sessionProvider);
    }
    
    protected final StudySaver getStudySaver() {
    	return new StudySaver(sessionProvider);
    }

    protected final ProjectSaver getProjectSaver() {
    	return new ProjectSaver(sessionProvider);
    }

    protected final ProjectPropertySaver getProjectPropertySaver() {
    	return new ProjectPropertySaver(sessionProvider);
    }

    protected final ProjectRelationshipSaver getProjectRelationshipSaver() {
    	return new ProjectRelationshipSaver(sessionProvider);
    }

    protected final GeolocationSaver getGeolocationSaver() {
    	return new GeolocationSaver(sessionProvider);
    }
    
    protected final StockSaver getStockSaver() {
    	return new StockSaver(sessionProvider);
    }
    
    protected final PhenotypeSaver getPhenotypeSaver() {
    	return new PhenotypeSaver(sessionProvider);
    }
    
    protected final ExperimentModelSaver getExperimentModelSaver() {
    	return new ExperimentModelSaver(sessionProvider);
    }
    
    protected final StandardVariableBuilder getStandardVariableBuilder() {
    	return new StandardVariableBuilder(sessionProvider);
    }

	protected final VariableTypeListTransformer getVariableTypeListTransformer() {
		return new VariableTypeListTransformer(sessionProvider);
	}
	
	protected final VariableListTransformer getVariableListTransformer() {
		return new VariableListTransformer(sessionProvider);
	}

	protected final StudyValuesTransformer getStudyValuesTransformer() {
		return new StudyValuesTransformer(sessionProvider);
	}
	
	protected final DatasetValuesTransformer getDatasetValuesTransformer() {
		return new DatasetValuesTransformer(sessionProvider);
	}
	
	protected final ExperimentValuesTransformer getExperimentValuesTransformer() {
		return new ExperimentValuesTransformer(sessionProvider);
	}
	
	protected final DatasetProjectSaver getDatasetProjectSaver() {
		return new DatasetProjectSaver(sessionProvider);
	}
	
	protected final VariableTypeBuilder getVariableTypeBuilder() {
		return new VariableTypeBuilder(sessionProvider);
	}
	
	protected final StockModelBuilder getStockModelBuilder() {
		return new StockModelBuilder(sessionProvider);
	}
	
	protected final TermBuilder getTermBuilder() {
	    return new TermBuilder(sessionProvider);
	}
	
    protected final ExperimentBuilder getExperimentBuilder() {
        return new ExperimentBuilder(sessionProvider);
    }

    protected final ExperimentPropertySaver getExperimentPropertySaver() {
        return new ExperimentPropertySaver(sessionProvider);
    }
    
    protected final ListDataPropertySaver getListDataPropertySaver() {
        return new ListDataPropertySaver(sessionProvider);
    }

    protected final WorkbookBuilder getWorkbookBuilder() {
        return new WorkbookBuilder(sessionProvider);
    }

    protected final GeolocationPropertySaver getGeolocationPropertySaver() {
    	return new GeolocationPropertySaver(sessionProvider);
    }
 
    protected final StudyDataManager getStudyDataManager() {
        return new StudyDataManagerImpl(sessionProvider);
    }
    
    protected final ExperimentDestroyer getExperimentDestroyer() {
        return new ExperimentDestroyer(sessionProvider);
    }
}
