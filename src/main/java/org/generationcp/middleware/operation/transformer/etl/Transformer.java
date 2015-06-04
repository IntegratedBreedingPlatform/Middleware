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

package org.generationcp.middleware.operation.transformer.etl;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.operation.builder.StandardVariableBuilder;
import org.generationcp.middleware.util.DatabaseBroker;

public abstract class Transformer extends DatabaseBroker {

	protected Transformer(HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	protected final VariableTypeListTransformer getVariableTypeListTransformer() {
		return new VariableTypeListTransformer(this.sessionProvider);
	}

	protected final MeasurementVariableTransformer getMeasurementVariableTransformer() {
		return new MeasurementVariableTransformer(this.sessionProvider);
	}

	protected final StudyValuesTransformer getStudyValuesTransformer() {
		return new StudyValuesTransformer(this.sessionProvider);
	}

	protected final DatasetValuesTransformer getDatasetValuesTransformer() {
		return new DatasetValuesTransformer(this.sessionProvider);
	}

	protected final VariableListTransformer getVariableListTransformer() {
		return new VariableListTransformer(this.sessionProvider);
	}

	protected final ExperimentValuesTransformer getExperimentValuesTransformer() {
		return new ExperimentValuesTransformer(this.sessionProvider);
	}

	protected final StandardVariableBuilder getStandardVariableBuilder() {
		return new StandardVariableBuilder(this.sessionProvider);
	}
}
