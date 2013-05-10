package org.generationcp.middleware.v2.domain.saver;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.v2.dao.ProjectPropertyDao;
import org.generationcp.middleware.v2.domain.TermId;
import org.generationcp.middleware.v2.domain.Variable;
import org.generationcp.middleware.v2.domain.VariableList;
import org.generationcp.middleware.v2.domain.VariableType;
import org.generationcp.middleware.v2.domain.VariableTypeList;
import org.generationcp.middleware.v2.pojos.DmsProject;
import org.generationcp.middleware.v2.pojos.ProjectProperty;

public class ProjectPropertySaver extends Saver {

	public ProjectPropertySaver(
			HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public List<ProjectProperty> create(DmsProject project, VariableTypeList variableTypeList) throws MiddlewareQueryException {
		setWorkingDatabase(Database.LOCAL);
		
		List<ProjectProperty> properties = new ArrayList<ProjectProperty>();
		List<VariableType> variableTypes = variableTypeList != null ? variableTypeList.getVariableTypes() : null;
		
		if (variableTypes != null && variableTypes.size() > 0) {
			int index = getProjectPropertyDao().getNegativeId("projectPropertyId");
			for (VariableType variableType : variableTypes) {
				properties.addAll(createVariableProperties(index, project, variableType));
				index = index - 3;
			}
		}
		
		return properties;
	}
	
	public void saveProjectProperties(DmsProject project, VariableTypeList variableTypeList) throws MiddlewareQueryException {
		requireLocalDatabaseInstance();
		List<ProjectProperty> properties = create(project, variableTypeList);
		
		Integer generatedId;
		ProjectPropertyDao projectPropertyDao = getProjectPropertyDao();
        for (ProjectProperty property : properties){
            generatedId = projectPropertyDao.getNegativeId("projectPropertyId");
            property.setProjectPropertyId(generatedId);
             property.setProject(project);
             projectPropertyDao.save(property);
        }
        
		project.setProperties(properties);
	}

	private List<ProjectProperty> createVariableProperties(int index, DmsProject project, VariableType variableType) throws MiddlewareQueryException {
		List<ProjectProperty> properties = new ArrayList<ProjectProperty>();
		
		properties.add(new ProjectProperty(index--, project, variableType.getStandardVariable().getStoredIn().getId(), variableType.getLocalName(), variableType.getRank()));
		properties.add(new ProjectProperty(index--, project, TermId.VARIABLE_DESCRIPTION.getId(), variableType.getLocalDescription(), variableType.getRank()));
		properties.add(new ProjectProperty(index--, project, TermId.STANDARD_VARIABLE.getId(), String.valueOf(variableType.getId()), variableType.getRank()));
		
		return properties;
	}
	
	public void saveProjectPropValues(VariableList variableList) throws MiddlewareQueryException {
		setWorkingDatabase(Database.LOCAL);
		
		if (variableList != null && variableList.getVariables() != null && variableList.getVariables().size() > 0) {
			for (Variable variable : variableList.getVariables()) {
				int storedInId = variable.getVariableType().getStandardVariable().getStoredIn().getId();
				if (TermId.STUDY_INFO_STORAGE.getId() == storedInId
				|| TermId.DATASET_INFO_STORAGE.getId() == storedInId) {
					ProjectProperty property = new ProjectProperty();
					property.setProjectPropertyId(getProjectPropertyDao().getNegativeId("projectPropertyId"));
					property.setTypeId(variable.getVariableType().getStandardVariable().getId());
					property.setValue(variable.getValue());
					property.setRank(variable.getVariableType().getRank());
					getProjectPropertyDao().save(property);
				}
			}
		}
		
	}
	
}
