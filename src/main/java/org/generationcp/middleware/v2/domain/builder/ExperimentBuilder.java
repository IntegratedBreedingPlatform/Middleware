package org.generationcp.middleware.v2.domain.builder;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.v2.domain.Experiment;
import org.generationcp.middleware.v2.pojos.ExperimentModel;

public class ExperimentBuilder extends Builder {

	public ExperimentBuilder(HibernateSessionProvider sessionProviderForLocal,
			                 HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public List<Experiment> create(List<ExperimentModel> experimentModels) {
		List<Experiment> experiments = new ArrayList<Experiment>();
		for (ExperimentModel experimentModel : experimentModels) {
			experiments.add(create(experimentModel));
		}
		return experiments;
	}

	public Experiment create(ExperimentModel experimentModel) {
		Experiment experiment = new Experiment();
		experiment.setId(experimentModel.getNdExperimentId());
		return experiment;
	}
}
