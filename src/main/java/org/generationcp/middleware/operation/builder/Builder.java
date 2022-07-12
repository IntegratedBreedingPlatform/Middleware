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

import org.generationcp.middleware.api.germplasm.GermplasmService;
import org.generationcp.middleware.api.germplasm.GermplasmServiceImpl;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.GermplasmDataManagerImpl;
import org.generationcp.middleware.manager.InventoryDataManagerImpl;
import org.generationcp.middleware.manager.OntologyDataManagerImpl;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.manager.api.OntologyDataManager;
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
import org.generationcp.middleware.operation.transformer.etl.MeasurementVariableTransformer;
import org.generationcp.middleware.operation.transformer.etl.StandardVariableTransformer;
import org.generationcp.middleware.service.api.derived_variables.FormulaService;
import org.generationcp.middleware.service.impl.derived_variables.FormulaServiceImpl;

/**
 * Provides builder classes (DatasetBuilder, StudyVariableBuilder, etc) based on the given session for local / central.
 *
 * @author Donald Barre
 */
public abstract class Builder {

	protected HibernateSessionProvider sessionProvider;

	public Builder() {

	}

	public Builder(final HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
	}

	protected final StudyBuilder getStudyBuilder() {
		return new StudyBuilder(this.sessionProvider);
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

	protected final StudyTypeBuilder getStudyTypeBuilder() {
		return new StudyTypeBuilder();
	}

	protected final ExperimentBuilder getExperimentBuilder() {
		return new ExperimentBuilder(this.sessionProvider);
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

	protected final NameSynonymBuilder getNameSynonymBuilder() {
		return new NameSynonymBuilder(this.sessionProvider);
	}

	protected final CvTermRelationshipSaver getCvTermRelationshipSaver() {
		return new CvTermRelationshipSaver(this.sessionProvider);
	}

	protected final MeasurementVariableTransformer getMeasurementVariableTransformer() {
		return new MeasurementVariableTransformer();
	}

	protected final GermplasmDataManager getGermplasmDataManager() {
		return new GermplasmDataManagerImpl(this.sessionProvider);
	}

	protected final OntologyDataManager getOntologyDataManager() {
		return new OntologyDataManagerImpl(this.sessionProvider);
	}

	
	protected final OntologyVariableDataManager getOntologyVariableDataManager() {
		return new OntologyVariableDataManagerImpl(this.getOntologyMethodDataManager(), this.getOntologyPropertyDataManager(),
				this.getOntologyScaleDataManager(), this.getFormulaService(), this.sessionProvider);
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
		return new StandardVariableTransformer();
	}

	protected final FormulaService getFormulaService() {
		return new FormulaServiceImpl(this.sessionProvider);
	}

	protected final InventoryDataManager getInventoryDataManager() {
		return new InventoryDataManagerImpl(this.sessionProvider);
	}

	protected final GermplasmService getGermplasmService() {
		return new GermplasmServiceImpl(this.sessionProvider);
	}

	public HibernateSessionProvider getSessionProvider() {
		return this.sessionProvider;
	}

	public void setSessionProvider(final HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
	}
}
