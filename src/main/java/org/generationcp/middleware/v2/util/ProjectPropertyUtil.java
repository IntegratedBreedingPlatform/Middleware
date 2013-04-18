package org.generationcp.middleware.v2.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.v2.pojos.CVTermId;
import org.generationcp.middleware.v2.pojos.ProjectProperty;

public class ProjectPropertyUtil {
	
	public static Set<Integer> extractStandardVariableIds(List<ProjectProperty> properties) {
		return extractStandardVariableIds(properties, null);
	}

	public static Set<Integer> extractLocalStandardVariableIds(List<ProjectProperty> properties) {
		return extractStandardVariableIds(properties, true);
	}

	public static Set<Integer> extractCentralStandardVariableIds(List<ProjectProperty> properties) {
		return extractStandardVariableIds(properties, false);
	}

	//if local is null, then get all
	//if local is true, then get only the negative Ids
	//if local is false, then get only the positive Ids
	private static Set<Integer> extractStandardVariableIds(List<ProjectProperty> properties, Boolean local) {
		Set<Integer> ids = new HashSet<Integer>();
		
		if (properties != null && properties.size() > 0) {
			for (ProjectProperty property : properties) {
				if (CVTermId.STANDARD_VARIABLE.getId().equals(property.getTypeId())
				&& (local == null
					|| local == true && property.getProjectPropertyId() <= 0 
					|| local == false && property.getProjectPropertyId() > 0 )			
				&& property.getValue() != null && !"".equals(property.getValue())) {
					ids.add(Integer.valueOf(property.getValue()));
				}
			}
		}
		
		return ids;
	}

}
