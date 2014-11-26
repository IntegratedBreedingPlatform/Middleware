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
	
	public Builder(HibernateSessionProvider sessionProviderForLocal) {
        super(sessionProviderForLocal);		
	}

    
    protected final StudyBuilder getStudyBuilder() {
    	return new StudyBuilder(sessionProviderForLocal);
    }
    
    protected final DataSetBuilder getDataSetBuilder() {
    	return new DataSetBuilder(sessionProviderForLocal);
    }
	
    protected final StudyVariableBuilder getStudyVariableBuilder() {
    	return new StudyVariableBuilder(sessionProviderForLocal);
    }
    
    protected final VariableInfoBuilder getVariableInfoBuilder() {
    	return new VariableInfoBuilder();
    }
    
    protected final VariableTypeBuilder getVariableTypeBuilder() {
    	return new VariableTypeBuilder(sessionProviderForLocal);
    }
    
    protected final ExperimentBuilder getExperimentBuilder() {
    	return new ExperimentBuilder(sessionProviderForLocal);
    }
    
    protected final StockModelBuilder getStockBuilder() {
    	return new StockModelBuilder(sessionProviderForLocal);
    }

    protected final StandardVariableBuilder getStandardVariableBuilder() {
    	return new StandardVariableBuilder(sessionProviderForLocal);
    }
    
    protected final TermBuilder getTermBuilder() {
    	return new TermBuilder(sessionProviderForLocal);
    }
    
    protected final CvTermSaver getTermSaver() {
    	return new CvTermSaver(sessionProviderForLocal);
    }

	protected final StandardVariableSaver getStandardVariableSaver() {
		return new StandardVariableSaver(sessionProviderForLocal);
	}
	
	protected final NameSynonymBuilder getNameSynonymBuilder() {
	    return new NameSynonymBuilder(sessionProviderForLocal);
	}
	
	protected final CvTermRelationshipSaver getCvTermRelationshipSaver() {
	    return new CvTermRelationshipSaver(sessionProviderForLocal);
	}
	
	protected final StudyDataManager getStudyDataManager() {
	    return new StudyDataManagerImpl(sessionProviderForLocal);
	}
	
	protected final MeasurementVariableTransformer getMeasurementVariableTransformer() {
	    return new MeasurementVariableTransformer(sessionProviderForLocal);
	}
	
    protected final GermplasmDataManager getGermplasmDataManager() {
        return new GermplasmDataManagerImpl(sessionProviderForLocal);
    }
    
    protected final OntologyDataManager getOntologyDataManager() {
        return new OntologyDataManagerImpl(sessionProviderForLocal);
    }

    protected final WorkbookBuilder getWorkbookBuilder() {
    	return new WorkbookBuilder(sessionProviderForLocal);
    }
}
