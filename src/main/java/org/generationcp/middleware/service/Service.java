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

package org.generationcp.middleware.service;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.exceptions.PhenotypeException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.GermplasmDataManagerImpl;
import org.generationcp.middleware.manager.GermplasmListManagerImpl;
import org.generationcp.middleware.manager.InventoryDataManagerImpl;
import org.generationcp.middleware.manager.LocationDataManagerImpl;
import org.generationcp.middleware.manager.OntologyDataManagerImpl;
import org.generationcp.middleware.manager.StudyDataManagerImpl;
import org.generationcp.middleware.manager.UserDataManagerImpl;
import org.generationcp.middleware.manager.WorkbenchDataManagerImpl;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.manager.ontology.OntologyMethodDataManagerImpl;
import org.generationcp.middleware.manager.ontology.OntologyPropertyDataManagerImpl;
import org.generationcp.middleware.manager.ontology.OntologyScaleDataManagerImpl;
import org.generationcp.middleware.manager.ontology.OntologyVariableDataManagerImpl;
import org.generationcp.middleware.manager.ontology.TermDataManagerImpl;
import org.generationcp.middleware.manager.ontology.api.OntologyMethodDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyPropertyDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyScaleDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.manager.ontology.api.TermDataManager;
import org.generationcp.middleware.operation.builder.DataSetBuilder;
import org.generationcp.middleware.operation.builder.ExperimentBuilder;
import org.generationcp.middleware.operation.builder.LotBuilder;
import org.generationcp.middleware.operation.builder.NameBuilder;
import org.generationcp.middleware.operation.builder.StandardVariableBuilder;
import org.generationcp.middleware.operation.builder.StockBuilder;
import org.generationcp.middleware.operation.builder.TermBuilder;
import org.generationcp.middleware.operation.builder.TransactionBuilder;
import org.generationcp.middleware.operation.builder.ValueReferenceBuilder;
import org.generationcp.middleware.operation.builder.WorkbookBuilder;
import org.generationcp.middleware.operation.destroyer.ExperimentDestroyer;
import org.generationcp.middleware.operation.destroyer.StudyDestroyer;
import org.generationcp.middleware.operation.saver.ExperimentPropertySaver;
import org.generationcp.middleware.operation.saver.GeolocationSaver;
import org.generationcp.middleware.operation.saver.ListDataProjectSaver;
import org.generationcp.middleware.operation.saver.PhenotypeOutlierSaver;
import org.generationcp.middleware.operation.saver.PhenotypeSaver;
import org.generationcp.middleware.operation.saver.WorkbookSaver;
import org.generationcp.middleware.operation.transformer.etl.MeasurementVariableTransformer;
import org.generationcp.middleware.util.DatabaseBroker;
import org.slf4j.Logger;

public abstract class Service extends DatabaseBroker {

	public Service() {
	}

	public Service(HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	public Service(HibernateSessionProvider sessionProvider, String databaseName) {
		super(sessionProvider, databaseName);
	}

	private LocationDataManager locationDataManager = new LocationDataManagerImpl(this.sessionProvider);

	protected void logAndThrowException(String message, Throwable e, Logger log) throws MiddlewareQueryException {
		log.error(e.getMessage(), e);
		if (e instanceof PhenotypeException) {
			throw (PhenotypeException) e;
		}
		throw new MiddlewareQueryException(message + e.getMessage(), e);
	}

	protected final PhenotypeSaver getPhenotypeSaver() {
		return new PhenotypeSaver(this.sessionProvider);
	}

	protected final PhenotypeOutlierSaver getPhenotypeOutlierSaver() {
		return new PhenotypeOutlierSaver(this.sessionProvider);
	}

	protected final WorkbookSaver getWorkbookSaver() {
		return new WorkbookSaver(this.sessionProvider);
	}

	protected final ExperimentPropertySaver getExperimentPropertySaver() {
		return new ExperimentPropertySaver(this.sessionProvider);
	}

	protected StudyDataManager getStudyDataManager() {
		return new StudyDataManagerImpl(this.sessionProvider, this.databaseName);
	}

	protected final OntologyDataManager getOntologyDataManager() {
		return new OntologyDataManagerImpl(this.sessionProvider);
	}

	protected final TermDataManager getTermDataManager() {
		return new TermDataManagerImpl(this.sessionProvider);
	}

	protected final WorkbenchDataManager getWorkbenchDataManager() {
		return new WorkbenchDataManagerImpl(this.sessionProvider);
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

	protected final OntologyVariableDataManager getOntologyVariableDataManager() {
		return new OntologyVariableDataManagerImpl(this.getOntologyMethodDataManager(), this.getOntologyPropertyDataManager(),
				this.getOntologyScaleDataManager(), this.sessionProvider);
	}

	protected GermplasmDataManager getGermplasmDataManager() {
		return new GermplasmDataManagerImpl(this.sessionProvider, this.databaseName);
	}

	protected final GermplasmListManager getGermplasmListManager() {
		return new GermplasmListManagerImpl(this.sessionProvider, this.databaseName);
	}

	protected final InventoryDataManager getInventoryDataManager() {
		return new InventoryDataManagerImpl(this.sessionProvider, this.databaseName);
	}

	protected final UserDataManager getUserDataManager() {
		return new UserDataManagerImpl(this.sessionProvider);
	}

	protected WorkbookBuilder getWorkbookBuilder() {
		return new WorkbookBuilder(this.sessionProvider);
	}

	protected final ValueReferenceBuilder getValueReferenceBuilder() {
		return new ValueReferenceBuilder(this.sessionProvider);
	}

	protected final GeolocationSaver getGeolocationSaver() {
		return new GeolocationSaver(this.sessionProvider);
	}

	protected final StandardVariableBuilder getStandardVariableBuilder() {
		return new StandardVariableBuilder(this.sessionProvider);
	}

	protected final LotBuilder getLotBuilder() {
		return new LotBuilder(this.sessionProvider);
	}

	protected final ExperimentBuilder getExperimentBuilder() {
		return new ExperimentBuilder(this.sessionProvider);
	}

	protected final StockBuilder getStockBuilder() {
		return new StockBuilder(this.sessionProvider);
	}

	protected final ExperimentDestroyer getExperimentDestroyer() {
		return new ExperimentDestroyer(this.sessionProvider);
	}

	protected final TransactionBuilder getTransactionBuilder() {
		return new TransactionBuilder(this.sessionProvider);
	}

	protected final MeasurementVariableTransformer getMeasurementVariableTransformer() {
		return new MeasurementVariableTransformer(this.sessionProvider);
	}

	protected DataSetBuilder getDataSetBuilder() {
		return new DataSetBuilder(this.sessionProvider);
	}

	protected final TermBuilder getTermBuilder() {
		return new TermBuilder(this.sessionProvider);
	}

	protected final StudyDestroyer getStudyDestroyer() {
		return new StudyDestroyer(this.sessionProvider);
	}

	protected final ListDataProjectSaver getListDataProjectSaver() {
		return new ListDataProjectSaver(this.sessionProvider);
	}

	protected final NameBuilder getNameBuilder() {
		return new NameBuilder(this.sessionProvider);
	}

	protected void setLocationDataManager(LocationDataManager locationDataManager) {
		this.locationDataManager = locationDataManager;
	}

	protected LocationDataManager getLocationDataManager() {
		return locationDataManager;
	}
}
