package org.generationcp.middleware.v2.helper;

import java.util.List;

import org.generationcp.middleware.v2.domain.CVTermId;
import org.generationcp.middleware.v2.pojos.ProjectProperty;

public class ProjectPropertiesHelper {

	private ProjectValues projectValues;
	
	public ProjectPropertiesHelper(List<ProjectProperty> properties) {
		projectValues = new ProjectValues(properties);
	}

	public String getString(CVTermId standardVariableId) {
		return projectValues.getValue(standardVariableId.getId());
	}
	
	public Integer getInteger(CVTermId standardVariableId) {
		return projectValues.getIntValue(standardVariableId.getId());
	}
}
