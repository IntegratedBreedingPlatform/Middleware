package org.generationcp.middleware.v2.helper;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.v2.pojos.CVTermId;
import org.generationcp.middleware.v2.pojos.ProjectProperty;

public class Variables {
	
	private List<Variable> variables = new ArrayList<Variable>();
	
	public Variables(List<ProjectProperty> properties) {
		for (ProjectProperty property : properties) {
			if (isNameField(property)) {
				addVariable(property, properties);
			}
		}
	}
	
	private boolean isNameField(ProjectProperty property) {
		return (property.getTypeId() == CVTermId.STUDY_INFORMATION.getId());
	}
	
	private boolean isDescriptionField(ProjectProperty property) {
		return (property.getTypeId() == CVTermId.VARIABLE_DESCRIPTION.getId());
	}
	
	private boolean isStandardVariableIdField(ProjectProperty property) {
		return (property.getTypeId() == CVTermId.STANDARD_VARIABLE.getId());
	}
	
	private void addVariable(ProjectProperty property, List<ProjectProperty> properties) {
		 variables.add(createVariable(property, properties));
	}
	
	private Variable createVariable(ProjectProperty property, List<ProjectProperty> properties) {
		Variable variable = new Variable();
		variable.setName(property.getValue());
		variable.setDescription(getDescription(property.getRank(), properties));
		variable.setStandardVariableId(getStandardVariableId(property.getRank(), properties));
		return variable;
	}
	
    private String getDescription(Integer rank, List<ProjectProperty> properties) {
		ProjectProperty property = findDescriptionField(rank, properties);
		if (property != null) {
			return property.getValue();
		}
		return null;
	}

	private ProjectProperty findDescriptionField(int rank, List<ProjectProperty> properties) {
		for (ProjectProperty property : properties) {
			if (isDescriptionField(property) && property.getRank() == rank) {
				return property;
			}
		}
		return null;
	}
	
	private Integer getStandardVariableId(Integer rank, List<ProjectProperty> properties) {
		ProjectProperty property = findStandardVariableIdField(rank, properties);
		if (property != null) {
			return Integer.parseInt(property.getValue());
		}
		return null;
	}

	private ProjectProperty findStandardVariableIdField(int rank, List<ProjectProperty> properties) {
		for (ProjectProperty property : properties) {
			if (isStandardVariableIdField(property) && property.getRank() == rank) {
				return property;
			}
		}
		return null;
	}
}
