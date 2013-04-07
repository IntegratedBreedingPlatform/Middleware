package org.generationcp.middleware.helper.dms;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.pojos.dms.CVTermId;
import org.generationcp.middleware.pojos.dms.ProjectProperty;

public class ProjectPropertiesHelper {

	private Map<Long, String> propertyMap;

	private static final Comparator<ProjectProperty> propertiesComparator = new Comparator<ProjectProperty>() {

		@Override
		public int compare(ProjectProperty o1, ProjectProperty o2) {
	
			//1st sort order: by rank
			if (o1.getRank().equals(o2.getRank())) {
			
				//2nd sort order: put cvtermId of "standard variable" on top
				if (CVTermId.STANDARD_VARIABLE.getId().equals(o1.getTypeId())) {
					return -1;
					
				} else if (CVTermId.STANDARD_VARIABLE.getId().equals(o2.getTypeId())) {
					return 1;
					
				} else {
					return 0;
				}
				
			} else {
				return o1.getRank().compareTo(o2.getRank());
			}
			
		}
		
	};
	
	public ProjectPropertiesHelper(List<ProjectProperty> properties) {
				
		//sort the properties by rank with the "Standard Variable" type on top for each rank
		Collections.sort(properties, propertiesComparator);
		
		this.propertyMap = translateProperties(properties);
	}
	
	private Map<Long, String> translateProperties(List<ProjectProperty> properties) {
		long rankValue;
		long valueTerm;
		long typeTerm;
		ProjectProperty property;
		Map<Long, String> map = new HashMap<Long, String>();
 
		for (int i = 0; i < properties.size(); i++) {
			property = properties.get(i);
			
			if (CVTermId.STANDARD_VARIABLE.getId().equals(property.getTypeId())) {
				rankValue = property.getRank();
				typeTerm = Long.valueOf(property.getValue());
				//valueTerm is the CVTerm Id of the property that holds the actual value
				//in the future, this might change to a "Value" cvterm, 
				valueTerm = Long.valueOf(property.getValue());
				
				//find the value using the valueTermId within the same rank
				while (rankValue == property.getRank() && i < properties.size()-1) {
					i++;
					property = properties.get(i);
					if (valueTerm == property.getTypeId()) {
						map.put(typeTerm, property.getValue());
						i--;
						break;
					}
				}
				
			}
		}
		return map;
	}
	
	public String getString(CVTermId type) {
		return propertyMap.get(type.getId());
	}
	
	public Integer getInteger(CVTermId type) {
		String value = getString(type);
		return value != null ? Integer.valueOf(value) : null;
	}
}
