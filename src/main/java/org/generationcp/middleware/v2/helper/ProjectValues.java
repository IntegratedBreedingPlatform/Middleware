package org.generationcp.middleware.v2.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.v2.pojos.ProjectProperty;

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
