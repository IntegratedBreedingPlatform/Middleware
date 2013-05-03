package org.generationcp.middleware.v2.domain.builder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.v2.domain.CVTermId;
import org.generationcp.middleware.v2.domain.VariableInfo;
import org.generationcp.middleware.v2.pojos.ProjectProperty;

public class VariableInfoBuilder {

	public Set<VariableInfo> create(List<ProjectProperty> properties) {
		Set<VariableInfo> variableDefs = new HashSet<VariableInfo>();
		for (ProjectProperty property : properties) {
			if (isStandardVariableType(property)) {
				variableDefs.add(createVariableDef(property, filterByRank(properties, property.getRank())));
			}
		}
		return variableDefs;
	}
	
	private VariableInfo createVariableDef(ProjectProperty stdVariableProperty, Set<ProjectProperty> properties) {
		
		ProjectProperty localNameProperty = findLocalNameProperty(stdVariableProperty.getValue(), properties);
		ProjectProperty localDescriptionProperty = findLocalDescriptionProperty(properties);
		
		VariableInfo variableDef = new VariableInfo();
		variableDef.setLocalName(localNameProperty == null ? null : localNameProperty.getValue());
	    variableDef.setLocalDescription(localDescriptionProperty == null ? null : localDescriptionProperty.getValue());
	    variableDef.setStdVariableId(Integer.parseInt(stdVariableProperty.getValue()));
	    if (properties.iterator().hasNext()) {
	    	variableDef.setRank(properties.iterator().next().getRank());
	    }
	    
		return variableDef;
	}

	private ProjectProperty findLocalDescriptionProperty(Set<ProjectProperty> properties) {
		for (ProjectProperty property : properties) {
			 if (isLocalDescriptionType(property)) {
				 return property;
			 }
		 }
		 return null;
	}

	private ProjectProperty findLocalNameProperty(String stdVariableIdStr, Set<ProjectProperty> properties) {
		Integer stdVariableId = Integer.parseInt(stdVariableIdStr);
		for (ProjectProperty property : properties) {
			 if (isStudyInformationType(property)) {
				 return property;
			 }
			 
			 if (!isLocalDescriptionType(property) && !isStandardVariableType(property)) {
				 if (!stdVariableId.equals(property.getTypeId())) {
					 return property;
				 }
			 }
		 }
		 return null;
	}
	
	private boolean isStudyInformationType(ProjectProperty property) {
		return CVTermId.STUDY_INFORMATION.getId().equals(property.getTypeId());
	}
	
	private boolean isLocalDescriptionType(ProjectProperty property) {
		return CVTermId.VARIABLE_DESCRIPTION.getId().equals(property.getTypeId());
	}

	private boolean isStandardVariableType(ProjectProperty property) {
		return CVTermId.STANDARD_VARIABLE.getId().equals(property.getTypeId());
	}

	private Set<ProjectProperty> filterByRank(List<ProjectProperty> properties, int rank) {
		Set<ProjectProperty> filteredProperties = new HashSet<ProjectProperty>();
		for (ProjectProperty property : properties) {
			if (property.getRank() == rank) {
				filteredProperties.add(property);
			}
		}
		return filteredProperties;
	}
}
