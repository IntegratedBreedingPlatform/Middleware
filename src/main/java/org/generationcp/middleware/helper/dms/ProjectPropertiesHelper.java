package org.generationcp.middleware.helper.dms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.pojos.Factor;
import org.generationcp.middleware.pojos.dms.CVTermId;
import org.generationcp.middleware.pojos.dms.ProjectProperty;

public class ProjectPropertiesHelper {

	private static enum VariableType {STUDY, FACTOR, VARIATE}
	
	private static final List<Long> VARIATE_TYPES = Arrays.asList(
			CVTermId.OBSERVATION_VARIATE.getId(), CVTermId.CATEGORICAL_VARIATE.getId()
	);
	
	private List<Factor> factors = new ArrayList<Factor>();
	private Map<Long, String> propertyMap = new HashMap<Long, String>();
	

	public ProjectPropertiesHelper(List<ProjectProperty> properties) {
		List<Variable> variableList = createVariables(properties);
		processVariables(variableList);
	}
	
	private List<Variable> createVariables(List<ProjectProperty> properties) {
		Long rankValue, prevRankValue = null;
		ProjectProperty property;
		Variable variable = null;
		List<Variable> variableList = null;
		
		//a list of properties with the same rank constitutes a Variable object
		for (int i = 0; i < properties.size(); i++) {
			property = properties.get(i);
			rankValue = property.getRank();

			if (rankValue.equals(prevRankValue)) {
				setPropertyToVariable(property, variable);
								
			} else {
				prevRankValue = rankValue;
				if (variableList != null) {
					variableList.add(variable);
				} else {
					variableList = new ArrayList<Variable>();
				}
				variable = new Variable();
				setPropertyToVariable(property, variable);
			}
		}
		
		//last element
		if (variable != null && variableList != null) {
			variableList.add(variable);
		}

		return variableList;
	}
	
	private void setPropertyToVariable(ProjectProperty property, Variable variable) {
		Long typeId = property.getTypeId();
		
		if (CVTermId.STUDY_INFORMATION.getId().equals(typeId)) {
			variable.setType(VariableType.STUDY);
			variable.setName(property.getValue());
			
		} else if (CVTermId.STANDARD_VARIABLE.getId().equals(typeId)) {
			variable.setVarId(Long.valueOf(property.getValue()));
			
		} else if (CVTermId.VARIABLE_DESCRIPTION.getId().equals(typeId)) {
			variable.setDescription(property.getValue());
			
		} else if (VARIATE_TYPES.contains(typeId)){
			variable.setType(VariableType.VARIATE);
			variable.setName(property.getValue());
			
		} else {
			variable.setValue(property.getValue());
			if (variable.getType() == null){
				variable.setType(VariableType.FACTOR);
				variable.setName(property.getValue());
			}
		}
	}
	
	private void processVariables(List<Variable> variableList) {
		if (variableList != null && variableList.size() > 0) {
			for (Variable variable : variableList) {
				if (variable.getType() == VariableType.STUDY) {
					processStudy(variable);
					processFactor(variable);
					
				} else if (variable.getType() == VariableType.FACTOR) {
					processFactor(variable);
					
				} else if (variable.getType() == VariableType.VARIATE) {
					
				}
			}
		}
	}
	
	private void processStudy(Variable variable) {
		propertyMap.put(variable.getVarId(), variable.getValue());
	}
	
	private void processFactor(Variable variable) {
		Factor factor = new Factor();
		factor.setId(variable.getVarId().intValue());
		factor.setFactorId(variable.getVarId().intValue());
		factor.setName(variable.getName());
		factors.add(factor);
	}
	
	public String getString(CVTermId type) {
		return propertyMap.get(type.getId());
	}
	
	public Integer getInteger(CVTermId type) {
		String value = getString(type);
		return value != null ? Integer.valueOf(value) : null;
	}
	
	public List<Factor> getFactors() {
		return this.factors;
	}
	
	class Variable {
		private VariableType type;
		private Long varId;
		private String name;
		private String description;
		private String value;
				
			
		VariableType getType() {
			return type;
		}

		void setType(VariableType type) {
			this.type = type;
		}

		Long getVarId() {
			return varId;
		}

		void setVarId(Long varId) {
			this.varId = varId;
		}

		String getName() {
			return name;
		}

		void setName(String name) {
			this.name = name;
		}

		String getDescription() {
			return description;
		}

		void setDescription(String description) {
			this.description = description;
		}

		String getValue() {
			return value;
		}

		void setValue(String value) {
			this.value = value;
		}

	}
}
