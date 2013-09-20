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
package org.generationcp.middleware.domain.dms;

import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.util.Debug;

/** 
 * Contains the details of a study - id, conditions and constants.
 */

public class Study {

    private int id;
	
	private VariableList conditions;

	private VariableList constants;
	
	public Study(){
	}

	public Study(int id, VariableList conditions, VariableList constants) {
		this.id = id;
		this.conditions = conditions;
		this.constants = constants;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return getDisplayValue(TermId.STUDY_NAME);
	}

	public String getTitle() {
		return getDisplayValue(TermId.STUDY_TITLE);
	}

	public String getObjective() {
		return getDisplayValue(TermId.STUDY_OBJECTIVE);
	}
	
	public Integer getPrimaryInvestigator() {
		return getDisplayValueAsInt(TermId.PI_ID);
	}

	public String getType() {
		return getDisplayValue(TermId.STUDY_TYPE);
	}

	public Integer getStartDate() {
		return getDisplayValueAsInt(TermId.START_DATE);
	}

	public Integer getEndDate() {
		return getDisplayValueAsInt(TermId.END_DATE);
	}

	public Integer getUser() {
		return getDisplayValueAsInt(TermId.STUDY_UID);
	}

	public Integer getStatus() {
		return getDisplayValueAsInt(TermId.STUDY_STATUS);
	}

	public Integer getCreationDate() {
		return getDisplayValueAsInt(TermId.CREATION_DATE);
	}
	
	public String getDisplayValue(TermId termId) {
		String value = null;
		Variable variable = conditions.findById(termId);
		if (variable == null) {
			variable = constants.findById(termId);
		}
		if (variable != null) {
			value = variable.getActualValue();
		}
		return value;
	}
	
	public Integer getDisplayValueAsInt(TermId termId) {
		Integer value = null;
		String strValue = getDisplayValue(termId);
		if (strValue != null && !"".equals(strValue)) {
			value = Integer.parseInt(strValue);
		}
		return value;
	}
	
	public VariableList getConditions() {
		return conditions.sort();
	}

	public void setConditions(VariableList conditions) {
		this.conditions = conditions;
	}


	public VariableTypeList getConditionVariableTypes() {
		return conditions.getVariableTypes().sort();
	}

	public VariableList getConstants() {
		return constants.sort();
	}

	public void setConstants(VariableList constants) {
		this.constants = constants;
	}
	
	public VariableTypeList getConstantVariableTypes() {
		return constants.getVariableTypes().sort();
	}
	
	public void print(int indent) {
		Debug.println(indent, "Study: ");
		Debug.println(indent + 3, "Id: " + getId());
		Debug.println(indent + 3, "Name: " + getName());
	    Debug.println(indent + 3, "Title: " + getTitle());
	    
	    Debug.println(indent + 3, "Conditions: ");
	    for (Variable condition : conditions.getVariables()) {
	    	condition.print(indent + 6);
	    }

	    Debug.println(indent + 3, "Constants: ");
	    for (Variable constant : constants.getVariables()) {
	    	constant.print(indent + 6);
	    }
	}
	
	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((conditions == null) ? 0 : conditions.hashCode());
        result = prime * result
                + ((constants == null) ? 0 : constants.hashCode());
        result = prime * result + id;
        return result;
    }

	@Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Study)) return false;
        Study other = (Study) obj;
        return getId() == other.getId();
    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Study [id=");
		builder.append(id);
		builder.append(", conditions=");
		builder.append(conditions);
		builder.append(", constants=");
		builder.append(constants);
		builder.append("]");
		return builder.toString();
	}

	public VariableTypeList getVariableTypesByPhenotypicType(PhenotypicType pheotypicType) {
		VariableTypeList filteredFactors = new VariableTypeList();
		VariableTypeList factors = getConditionVariableTypes();
		if (factors != null && factors.getVariableTypes() != null) {
			for (VariableType factor : factors.getVariableTypes()) {
				if (factor.getStandardVariable().getPhenotypicType() == pheotypicType) {
					filteredFactors.add(factor);
				}
			}
		}
		return filteredFactors.sort();
	}
}
