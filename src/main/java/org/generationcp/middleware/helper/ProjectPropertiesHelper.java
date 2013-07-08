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
package org.generationcp.middleware.helper;

import java.util.List;

import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.pojos.dms.ProjectProperty;

public class ProjectPropertiesHelper {

	private ProjectValues projectValues;
	
	public ProjectPropertiesHelper(List<ProjectProperty> properties) {
		projectValues = new ProjectValues(properties);
	}

	public String getString(TermId standardVariableId) {
		return projectValues.getValue(standardVariableId.getId());
	}
	
	public Integer getInteger(TermId standardVariableId) {
		return projectValues.getIntValue(standardVariableId.getId());
	}
}
