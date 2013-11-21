/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.operation.saver;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ProjectProperty;

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
	
	public void saveProjectPropValues(int projectId, VariableList variableList) throws MiddlewareQueryException {
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
					property.setProject(getDmsProjectDao().getById(projectId));
					getProjectPropertyDao().save(property);
				}
			}
		}
	}
	
	public void saveVariableType(DmsProject project, VariableType variableType) throws MiddlewareQueryException {
		setWorkingDatabase(Database.LOCAL);
		saveProjectProperty(project, variableType.getStandardVariable().getStoredIn().getId(), variableType.getLocalName(), variableType.getRank());
		saveProjectProperty(project, TermId.VARIABLE_DESCRIPTION.getId(), variableType.getLocalDescription(), variableType.getRank());
		saveProjectProperty(project, TermId.STANDARD_VARIABLE.getId(), Integer.toString(variableType.getStandardVariable().getId()), variableType.getRank());
	}
	
	private void saveProjectProperty(DmsProject project, int typeId, String value, int rank) throws MiddlewareQueryException {
		ProjectProperty property = new ProjectProperty();
		property.setProjectPropertyId(getProjectPropertyDao().getNegativeId("projectPropertyId"));
		property.setTypeId(typeId);
		property.setValue(value);
		property.setRank(rank);
		property.setProject(project);
		getProjectPropertyDao().save(property);
		project.addProperty(property);
	}
	
	public void createProjectPropertyIfNecessary(DmsProject project, TermId termId, int storedIn) throws MiddlewareQueryException {
	    setWorkingDatabase(project.getProjectId());
        ProjectProperty property = getProjectPropertyDao().getByStandardVariableId(project, termId.getId());
        if (property == null) {
            setWorkingDatabase(project.getProjectId());
            int rank = getProjectPropertyDao().getNextRank(project.getProjectId());
            StandardVariable stdvar = new StandardVariable();
            stdvar.setId(termId.getId());
            stdvar.setStoredIn(new Term(storedIn, null, null));
            VariableType variableType = new VariableType(termId.toString(), termId.toString(), stdvar, rank);
            saveVariableType(project, variableType);
        }
	}
	
}
