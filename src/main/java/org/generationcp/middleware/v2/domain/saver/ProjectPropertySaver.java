package org.generationcp.middleware.v2.domain.saver;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.v2.domain.CVTermId;
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

	public List<ProjectProperty> create(DmsProject project, VariableTypeList variableTypeList) {
		List<ProjectProperty> properties = new ArrayList<ProjectProperty>();
		List<VariableType> variableTypes = variableTypeList != null ? variableTypeList.getVariableTypes() : null;
		
		if (variableTypes != null && variableTypes.size() > 0) {
			for (VariableType variableType : variableTypes) {
				properties.addAll(createVariableProperties(project, variableType));
			}
		}
		
		return properties;
	}

	private List<ProjectProperty> createVariableProperties(DmsProject project, VariableType variableType) {
		List<ProjectProperty> properties = new ArrayList<ProjectProperty>();
		
		properties.add(new ProjectProperty(0, project, CVTermId.STUDY_INFORMATION.getId(), variableType.getLocalName(), variableType.getRank()));
		properties.add(new ProjectProperty(0, project, CVTermId.VARIABLE_DESCRIPTION.getId(), variableType.getLocalDescription(), variableType.getRank()));
		properties.add(new ProjectProperty(0, project, CVTermId.STANDARD_VARIABLE.getId(), String.valueOf(variableType.getId()), variableType.getRank()));
		
		return properties;
	}
	
}
