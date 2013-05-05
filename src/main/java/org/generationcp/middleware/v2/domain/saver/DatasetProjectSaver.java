package org.generationcp.middleware.v2.domain.saver;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.v2.domain.DataSet;
import org.generationcp.middleware.v2.pojos.DmsProject;

public class DatasetProjectSaver extends Saver {

	public DatasetProjectSaver(HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public DmsProject saveDataSet(DataSet dataset) throws MiddlewareQueryException {
		setWorkingDatabase(Database.LOCAL);
		
  		DmsProject datasetProject = createDataSet(dataset);
		datasetProject.setProperties(getProjectPropertySaver().create(datasetProject, dataset.getVariableTypes()));
		
		getDmsProjectDao().save(datasetProject);
		return datasetProject;
	}
	
	public DmsProject createDataSet(DataSet dataset) throws MiddlewareQueryException {
		DmsProject datasetProject = new DmsProject();
		
		datasetProject.setProjectId(getDmsProjectDao().getNegativeId("projectId"));
		datasetProject.setName(dataset.getName());
		datasetProject.setDescription(dataset.getDescription());
		
		return datasetProject;
	}
	
	

}
