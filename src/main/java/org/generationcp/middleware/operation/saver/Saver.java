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

package org.generationcp.middleware.operation.saver;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.operation.builder.StandardVariableBuilder;
import org.generationcp.middleware.operation.builder.TermBuilder;
import org.generationcp.middleware.operation.destroyer.ExperimentDestroyer;
import org.generationcp.middleware.operation.transformer.etl.DatasetValuesTransformer;
import org.generationcp.middleware.operation.transformer.etl.ExperimentValuesTransformer;
import org.generationcp.middleware.operation.transformer.etl.StudyValuesTransformer;
import org.generationcp.middleware.operation.transformer.etl.VariableListTransformer;
import org.generationcp.middleware.operation.transformer.etl.VariableTypeListTransformer;
import org.hibernate.Session;

/**
 * Provides saver classes that can be used to save logical/physical data in IBDBv2. Creates saver classes based on the given local/central
 * session parameters. The super class of all the Saver classes.
 *
 * @author Joyce Avestro
 */
public class Saver {

	protected HibernateSessionProvider sessionProvider;

	public Saver() {

	}

	/**
	 * Instantiates a new data manager given session providers for local and central.
	 */
	protected Saver(HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
	}

	protected final StudySaver getStudySaver() {
		return new StudySaver(this.sessionProvider);
	}

	protected final ProjectSaver getProjectSaver() {
		return new ProjectSaver(this.sessionProvider);
	}

	protected final ProjectPropertySaver getProjectPropertySaver() {
		return new ProjectPropertySaver(this.sessionProvider);
	}

	protected final GeolocationSaver getGeolocationSaver() {
		return new GeolocationSaver(this.sessionProvider);
	}

	protected final StockSaver getStockSaver() {
		return new StockSaver(this.sessionProvider);
	}

	protected final PhenotypeSaver getPhenotypeSaver() {
		return new PhenotypeSaver(this.sessionProvider);
	}

	protected final ExperimentModelSaver getExperimentModelSaver() {
		return new ExperimentModelSaver(this.sessionProvider);
	}

	protected final StandardVariableBuilder getStandardVariableBuilder() {
		return new StandardVariableBuilder(this.sessionProvider);
	}

	protected VariableTypeListTransformer getVariableTypeListTransformer() {
		return new VariableTypeListTransformer(this.sessionProvider);
	}

	protected final VariableListTransformer getVariableListTransformer() {
		return new VariableListTransformer();
	}

	protected final StudyValuesTransformer getStudyValuesTransformer() {
		return new StudyValuesTransformer();
	}

	protected final DatasetValuesTransformer getDatasetValuesTransformer() {
		return new DatasetValuesTransformer();
	}

	protected final ExperimentValuesTransformer getExperimentValuesTransformer() {
		return new ExperimentValuesTransformer();
	}

	protected final DatasetProjectSaver getDatasetProjectSaver() {
		return new DatasetProjectSaver(this.sessionProvider);
	}

	protected final TermBuilder getTermBuilder() {
		return new TermBuilder(this.sessionProvider);
	}

	protected final GeolocationPropertySaver getGeolocationPropertySaver() {
		return new GeolocationPropertySaver(this.sessionProvider);
	}

	protected final ExperimentDestroyer getExperimentDestroyer() {
		return new ExperimentDestroyer(this.sessionProvider);
	}

	public Session getActiveSession() {
		if (this.sessionProvider != null) {
			return this.sessionProvider.getSession();
		}
		return null;
	}

	public HibernateSessionProvider getSessionProvider() {
		return this.sessionProvider;
	}

	public void setSessionProvider(final HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
	}

}
