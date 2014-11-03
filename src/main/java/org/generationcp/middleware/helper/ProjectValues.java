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

import org.generationcp.middleware.pojos.dms.ProjectProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectValues {

	private Map<Integer, String> values = new HashMap<Integer, String>();
	
	public ProjectValues(List<ProjectProperty> properties) {
		for (ProjectProperty property : properties) {
			values.put(property.getTypeId(), property.getValue());
		}
	}
	
	public String getValue(Integer standardVariableId) {
		return values.get(standardVariableId);
	}
	
	public Integer getIntValue(Integer standardVariableId) {
		String value = values.get(standardVariableId);
		if (value != null) {
		    return Integer.parseInt(getValue(standardVariableId));
		}
		return null;
	}
}
