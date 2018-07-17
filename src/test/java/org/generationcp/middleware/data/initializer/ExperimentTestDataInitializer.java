package org.generationcp.middleware.data.initializer;

import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.VariableList;

public class ExperimentTestDataInitializer {
	public static Experiment createExperiment() {
		final Experiment experiment = new Experiment();
		experiment.setId(1);
		return experiment;
	}
	
	public static Experiment createExperiment(final VariableList factors, final VariableList variates) {
		final Experiment experiment = new Experiment();
		experiment.setId(1);
		experiment.setFactors(factors);
		experiment.setVariates(variates);
		return experiment;
	}
}
