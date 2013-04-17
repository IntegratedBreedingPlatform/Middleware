package org.generationcp.middleware.v2.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.v2.pojos.CVTermId;
import org.generationcp.middleware.v2.pojos.CVTermRelationship;
import org.generationcp.middleware.v2.pojos.ProjectProperty;
import org.generationcp.middleware.v2.util.ProjectPropertyUtil;

public class Variables {
	
	private List<Variable> variables = new ArrayList<Variable>();
	
	private Set<Integer> standardVariableIds;
	
	public Set<Integer> getStandardVariableIds() {
		return standardVariableIds;
	}
	
	public Variables(List<ProjectProperty> properties, List<CVTermRelationship> relationships) {
		standardVariableIds = ProjectPropertyUtil.extractStandardVariableIds(properties);
		CVTermRelationshipHelper relationshipHelper = new CVTermRelationshipHelper(relationships);
		
		for (ProjectProperty property : properties) {
			if (isNameField(property)) {
				addVariable(property, properties, relationshipHelper);
			}
		}
	}
	
	public List<Variable> getVariables() {
		return variables;
	}
	
	private boolean isNameField(ProjectProperty property) {
		return (CVTermId.STUDY_INFORMATION.getId().equals(property.getTypeId())
				|| !CVTermId.VARIABLE_DESCRIPTION.getId().equals(property.getTypeId())
					&& !CVTermId.STANDARD_VARIABLE.getId().equals(property.getTypeId())
					&& !getStandardVariableIds().contains(property.getTypeId()) );
	}
	
	private boolean isDescriptionField(ProjectProperty property) {
		return (property.getTypeId().equals(CVTermId.VARIABLE_DESCRIPTION.getId()));
	}
	
	private boolean isStandardVariableIdField(ProjectProperty property) {
		return (property.getTypeId().equals(CVTermId.STANDARD_VARIABLE.getId()));
	}
	
	private void addVariable(ProjectProperty property, List<ProjectProperty> properties, CVTermRelationshipHelper relationshipHelper) {
		variables.add(createVariable(property, properties, relationshipHelper));
	}
	
	private Variable createVariable(ProjectProperty property, List<ProjectProperty> properties, CVTermRelationshipHelper relationshipHelper) {
		Variable variable = new Variable();
		variable.setName(property.getValue());
		variable.setDescription(getDescription(property.getRank(), properties));
		Integer varId = getStandardVariableId(property.getRank(), properties);
		variable.setStandardVariableId(varId);
		variable.setProperty(relationshipHelper.getProperty(varId));
		variable.setMethod(relationshipHelper.getMethod(varId));
		variable.setScale(relationshipHelper.getScale(varId));
		variable.setDataType(relationshipHelper.getDataType(varId));
		variable.setDeterminantId(relationshipHelper.getStoredIn(varId));
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
