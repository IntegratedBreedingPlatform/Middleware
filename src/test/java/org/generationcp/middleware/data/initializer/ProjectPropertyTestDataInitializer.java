package org.generationcp.middleware.data.initializer;

import org.generationcp.middleware.pojos.dms.ProjectProperty;

public class ProjectPropertyTestDataInitializer {
	public static ProjectProperty createProjectProperty(final String alias, final Integer variableId) {
		final ProjectProperty projectProperty = new ProjectProperty();
		projectProperty.setAlias(alias);
		projectProperty.setVariableId(variableId);
		return projectProperty;
	}

}
