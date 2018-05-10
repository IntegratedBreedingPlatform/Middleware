package org.generationcp.middleware.data.initializer;

import org.generationcp.middleware.pojos.dms.ProjectProperty;

public class ProjectPropertyTestDataInitializer {
	public static ProjectProperty createProjectProperty(String alias, Integer variableId) {
		ProjectProperty projectProperty = new ProjectProperty();
		projectProperty.setAlias(alias);
		projectProperty.setVariableId(variableId);
		return projectProperty;
	}
	
}
