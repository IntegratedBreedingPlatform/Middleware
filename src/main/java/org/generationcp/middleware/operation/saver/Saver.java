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
    protected Saver(HibernateSessionProvider sessionProviderForLocal) {
       super(sessionProviderForLocal);
    }
    
    protected final StudySaver getStudySaver() {
    	return new StudySaver(sessionProviderForLocal);
    }

    protected final ProjectSaver getProjectSaver() {
    	return new ProjectSaver(sessionProviderForLocal);
    }

    protected final ProjectPropertySaver getProjectPropertySaver() {
    	return new ProjectPropertySaver(sessionProviderForLocal);
    }

    protected final ProjectRelationshipSaver getProjectRelationshipSaver() {
    	return new ProjectRelationshipSaver(sessionProviderForLocal);
    }

    protected final GeolocationSaver getGeolocationSaver() {
    	return new GeolocationSaver(sessionProviderForLocal);
    }
    
    protected final StockSaver getStockSaver() {
    	return new StockSaver(sessionProviderForLocal);
    }
    
    protected final PhenotypeSaver getPhenotypeSaver() {
    	return new PhenotypeSaver(sessionProviderForLocal);
    }
    
    protected final ExperimentModelSaver getExperimentModelSaver() {
    	return new ExperimentModelSaver(sessionProviderForLocal);
    }
    
    protected final StandardVariableBuilder getStandardVariableBuilder() {
    	return new StandardVariableBuilder(sessionProviderForLocal);
    }

	protected final VariableTypeListTransformer getVariableTypeListTransformer() {
		return new VariableTypeListTransformer(sessionProviderForLocal);
	}
	
	protected final VariableListTransformer getVariableListTransformer() {
		return new VariableListTransformer(sessionProviderForLocal);
	}

	protected final StudyValuesTransformer getStudyValuesTransformer() {
		return new StudyValuesTransformer(sessionProviderForLocal);
	}
	
	protected final DatasetValuesTransformer getDatasetValuesTransformer() {
		return new DatasetValuesTransformer(sessionProviderForLocal);
	}
	
	protected final ExperimentValuesTransformer getExperimentValuesTransformer() {
		return new ExperimentValuesTransformer(sessionProviderForLocal);
	}
	
	protected final DatasetProjectSaver getDatasetProjectSaver() {
		return new DatasetProjectSaver(sessionProviderForLocal);
	}
	
	protected final VariableTypeBuilder getVariableTypeBuilder() {
		return new VariableTypeBuilder(sessionProviderForLocal);
	}
	
	protected final StockModelBuilder getStockModelBuilder() {
		return new StockModelBuilder(sessionProviderForLocal);
	}
	
	protected final TermBuilder getTermBuilder() {
	    return new TermBuilder(sessionProviderForLocal);
	}
	
    protected final ExperimentBuilder getExperimentBuilder() {
        return new ExperimentBuilder(sessionProviderForLocal);
    }

    protected final ExperimentPropertySaver getExperimentPropertySaver() {
        return new ExperimentPropertySaver(sessionProviderForLocal);
    }
    
    protected final ListDataPropertySaver getListDataPropertySaver() {
        return new ListDataPropertySaver(sessionProviderForLocal);
    }

    protected final WorkbookBuilder getWorkbookBuilder() {
        return new WorkbookBuilder(sessionProviderForLocal);
    }

    protected final GeolocationPropertySaver getGeolocationPropertySaver() {
    	return new GeolocationPropertySaver(sessionProviderForLocal);
    }
 
    protected final StudyDataManager getStudyDataManager() {
        return new StudyDataManagerImpl(sessionProviderForLocal);
    }
    
    protected final ExperimentDestroyer getExperimentDestroyer() {
        return new ExperimentDestroyer(sessionProviderForLocal);
    }
}
