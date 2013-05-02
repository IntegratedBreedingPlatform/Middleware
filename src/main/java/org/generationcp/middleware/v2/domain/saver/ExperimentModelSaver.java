package org.generationcp.middleware.v2.domain.saver;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.v2.domain.Experiment;
import org.generationcp.middleware.v2.pojos.DmsProject;
import org.generationcp.middleware.v2.pojos.ExperimentModel;

public class ExperimentModelSaver extends Saver {

	public ExperimentModelSaver(
			HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	public void save(DmsProject project, Experiment experiment) throws MiddlewareQueryException {
		ExperimentModel experimentModel = create(project, experiment);
		getExperimentDao().save(experimentModel);
	}

	public ExperimentModel create(DmsProject project, Experiment experiment) throws MiddlewareQueryException {
		ExperimentModel experimentModel = new ExperimentModel();
		
		experimentModel.setNdExperimentId(getExperimentDao().getNegativeId("ndExperimentId"));
		experimentModel.setProject(project);
		//experimentModel.setTypeId(typeId);
		//experimentModel.setGeoLocation(getGeolocationBuilder().create(experiment));
		//experimentModel.setProperties(getExperimentPropertyBuilder().create(experiment));
		//experimentModel.setExperimentStocks(getExperimentStockBuilder().create(experiment));
		//experimentModel.setPhenotypes(getPhenotypeBuilder().create(experiment));
		
		//getExperimentDao().save(experimentModel);
		
		return experimentModel;
	}
}
