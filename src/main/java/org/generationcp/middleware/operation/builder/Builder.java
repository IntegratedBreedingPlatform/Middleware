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
import org.generationcp.middleware.manager.GermplasmDataManagerImpl;
import org.generationcp.middleware.manager.OntologyDataManagerImpl;
import org.generationcp.middleware.manager.StudyDataManagerImpl;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.operation.saver.CvTermRelationshipSaver;
import org.generationcp.middleware.operation.saver.CvTermSaver;
import org.generationcp.middleware.operation.saver.StandardVariableSaver;
import org.generationcp.middleware.operation.transformer.etl.MeasurementVariableTransformer;
import org.generationcp.middleware.util.DatabaseBroker;

/**
 * Provides builder classes (DatasetBuilder, StudyVariableBuilder, etc) based on the given session for local / central.
 * 
 * @author Donald Barre
 */
public abstract class Builder extends DatabaseBroker {
	
	public Builder(HibernateSessionProvider sessionProvider) {
        super(sessionProvider);		
	}
    
    protected final StudyBuilder getStudyBuilder() {
    	return new StudyBuilder(sessionProvider);
    }
    
    protected final DataSetBuilder getDataSetBuilder() {
    	return new DataSetBuilder(sessionProvider);
    }
	
    protected final StudyVariableBuilder getStudyVariableBuilder() {
    	return new StudyVariableBuilder(sessionProvider);
    }
    
    protected final VariableInfoBuilder getVariableInfoBuilder() {
    	return new VariableInfoBuilder();
    }
    
    protected final VariableTypeBuilder getVariableTypeBuilder() {
    	return new VariableTypeBuilder(sessionProvider);
    }
    
    protected final ExperimentBuilder getExperimentBuilder() {
    	return new ExperimentBuilder(sessionProvider);
    }
    
    protected final StockModelBuilder getStockBuilder() {
    	return new StockModelBuilder(sessionProvider);
    }

    protected final StandardVariableBuilder getStandardVariableBuilder() {
    	return new StandardVariableBuilder(sessionProvider);
    }
    
    protected final TermBuilder getTermBuilder() {
    	return new TermBuilder(sessionProvider);
    }
    
    protected final CvTermSaver getTermSaver() {
    	return new CvTermSaver(sessionProvider);
    }

	protected final StandardVariableSaver getStandardVariableSaver() {
		return new StandardVariableSaver(sessionProvider);
	}
	
	protected final NameSynonymBuilder getNameSynonymBuilder() {
	    return new NameSynonymBuilder(sessionProvider);
	}
	
	protected final CvTermRelationshipSaver getCvTermRelationshipSaver() {
	    return new CvTermRelationshipSaver(sessionProvider);
	}
	
	protected final StudyDataManager getStudyDataManager() {
	    return new StudyDataManagerImpl(sessionProvider);
	}
	
	protected final MeasurementVariableTransformer getMeasurementVariableTransformer() {
	    return new MeasurementVariableTransformer(sessionProvider);
	}
	
    protected final GermplasmDataManager getGermplasmDataManager() {
        return new GermplasmDataManagerImpl(sessionProvider);
    }
    
    protected final OntologyDataManager getOntologyDataManager() {
        return new OntologyDataManagerImpl(sessionProvider);
    }

    protected final WorkbookBuilder getWorkbookBuilder() {
    	return new WorkbookBuilder(sessionProvider);
    }
}
