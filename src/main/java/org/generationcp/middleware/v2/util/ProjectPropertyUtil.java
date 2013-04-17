package org.generationcp.middleware.v2.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.v2.pojos.CVTermId;
import org.generationcp.middleware.v2.pojos.ProjectProperty;

public class ProjectPropertyUtil {
	
	public static Set<Integer> extractStandardVariableIds(List<ProjectProperty> properties) {
		Set<Integer> ids = new HashSet<Integer>();
		for (ProjectProperty property : properties) {
			if (property.getValue().equals("8030")) 
				System.out.println("hi " + properties.size());
			if (CVTermId.STANDARD_VARIABLE.getId().equals(property.getTypeId()) 
			&& property.getValue() != null && !"".equals(property.getValue())) {
				ids.add(Integer.valueOf(property.getValue()));
			}
		}
		return ids;
	}
}
