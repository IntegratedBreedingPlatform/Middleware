package org.generationcp.middleware.v2.domain.saver;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.v2.domain.DataSet;
import org.generationcp.middleware.v2.domain.DatasetValues;
import org.generationcp.middleware.v2.domain.TermId;
import org.generationcp.middleware.v2.domain.VariableTypeList;
import org.generationcp.middleware.v2.pojos.DmsProject;
import org.generationcp.middleware.v2.pojos.ProjectRelationship;

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
	
	public DmsProject addDataSet(int studyId, VariableTypeList variableTypeList, DatasetValues datasetValues) throws MiddlewareQueryException {
		setWorkingDatabase(Database.LOCAL);
		
		DmsProject datasetProject = new DmsProject();
		
		datasetProject.setProjectId(getDmsProjectDao().getNegativeId("projectId"));
		datasetProject.setName(getStringValue(datasetValues, TermId.DATASET_NAME.getId()));
		datasetProject.setDescription(getStringValue(datasetValues, TermId.DATASET_TITLE.getId()));
		
		datasetProject.setProperties(getProjectPropertySaver().create(datasetProject, variableTypeList));
		
		datasetProject.setRelatedTos(createProjectRelationship(studyId, datasetProject));
		
		getDmsProjectDao().save(datasetProject);
		
		return datasetProject;
	}
	
	private String getStringValue(DatasetValues datasetValues, int termId) {
		return datasetValues.getVariableList().findById(termId).getValue();
	}
	
	private List<ProjectRelationship> createProjectRelationship(int studyId, DmsProject datasetProject) throws MiddlewareQueryException {
		ProjectRelationship relationship = new ProjectRelationship();
		
		relationship.setProjectRelationshipId(getProjectRelationshipDao().getNegativeId("projectRelationshipId"));
		relationship.setSubjectProject(datasetProject);
		relationship.setObjectProject(getDmsProjectDao().getById(studyId));
		relationship.setTypeId(TermId.BELONGS_TO_STUDY.getId());
		
		List<ProjectRelationship> relationships = new ArrayList<ProjectRelationship>();
		relationships.add(relationship);
		
		return relationships;
	}
	
}
