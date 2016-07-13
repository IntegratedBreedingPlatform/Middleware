/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
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
import org.generationcp.middleware.manager.ontology.OntologyMethodDataManagerImpl;
import org.generationcp.middleware.manager.ontology.OntologyPropertyDataManagerImpl;
import org.generationcp.middleware.manager.ontology.OntologyScaleDataManagerImpl;
import org.generationcp.middleware.manager.ontology.OntologyVariableDataManagerImpl;
import org.generationcp.middleware.manager.ontology.api.OntologyMethodDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyPropertyDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyScaleDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.operation.saver.CvTermRelationshipSaver;
import org.generationcp.middleware.operation.saver.CvTermSaver;
import org.generationcp.middleware.operation.saver.StandardVariableSaver;
import org.generationcp.middleware.operation.transformer.etl.MeasurementVariableTransformer;
import org.generationcp.middleware.operation.transformer.etl.StandardVariableTransformer;
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
		return new StudyBuilder(this.sessionProvider);
	}

	protected final DataSetBuilder getDataSetBuilder() {
		return new DataSetBuilder(this.sessionProvider);
	}

	protected final StudyVariableBuilder getStudyVariableBuilder() {
		return new StudyVariableBuilder(this.sessionProvider);
	}

	protected final VariableInfoBuilder getVariableInfoBuilder() {
		return new VariableInfoBuilder();
	}

	protected final VariableTypeBuilder getVariableTypeBuilder() {
		return new VariableTypeBuilder(this.sessionProvider);
	}

	protected final ExperimentBuilder getExperimentBuilder() {
		return new ExperimentBuilder(this.sessionProvider);
	}

	protected final StockModelBuilder getStockBuilder() {
		return new StockModelBuilder(this.sessionProvider);
	}

	protected final StandardVariableBuilder getStandardVariableBuilder() {
		return new StandardVariableBuilder(this.sessionProvider);
	}

	protected final TermBuilder getTermBuilder() {
		return new TermBuilder(this.sessionProvider);
	}

	protected final CvTermSaver getTermSaver() {
		return new CvTermSaver(this.sessionProvider);
	}

	protected final StandardVariableSaver getStandardVariableSaver() {
		return new StandardVariableSaver(this.sessionProvider);
	}

	protected final NameSynonymBuilder getNameSynonymBuilder() {
		return new NameSynonymBuilder(this.sessionProvider);
	}

	protected final CvTermRelationshipSaver getCvTermRelationshipSaver() {
		return new CvTermRelationshipSaver(this.sessionProvider);
	}

	public StudyDataManager getStudyDataManager() {
		return new StudyDataManagerImpl(this.sessionProvider);
	}

	protected final MeasurementVariableTransformer getMeasurementVariableTransformer() {
		return new MeasurementVariableTransformer(this.sessionProvider);
	}

	protected final GermplasmDataManager getGermplasmDataManager() {
		return new GermplasmDataManagerImpl(this.sessionProvider);
	}

	protected final OntologyDataManager getOntologyDataManager() {
		return new OntologyDataManagerImpl(this.sessionProvider);
	}

	protected final WorkbookBuilder getWorkbookBuilder() {
		return new WorkbookBuilder(this.sessionProvider);
	}
	
	protected final OntologyVariableDataManager getOntologyVariableDataManager() {
		return new OntologyVariableDataManagerImpl(this.sessionProvider);
	}
	
	protected final OntologyMethodDataManager getOntologyMethodDataManager() {
		return new OntologyMethodDataManagerImpl(this.sessionProvider);
	}
	
	protected final OntologyPropertyDataManager getOntologyPropertyDataManager() {
		return new OntologyPropertyDataManagerImpl(this.sessionProvider);
	}

	protected final OntologyScaleDataManager getOntologyScaleDataManager() {
		return new OntologyScaleDataManagerImpl(this.sessionProvider);
	}
	
	protected final StandardVariableTransformer getStandardVariableTransformer() {
		return new StandardVariableTransformer(this.sessionProvider);
	}
}
