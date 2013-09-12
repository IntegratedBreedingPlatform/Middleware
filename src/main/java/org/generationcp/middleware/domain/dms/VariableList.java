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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.generationcp.middleware.domain.oms.TermId;

/** 
 * List of variables and types.
 */
public class VariableList {

	private List<Variable> variables = new ArrayList<Variable>();
	private VariableTypeList variableTypes = null;
	
	public void add(Variable variable) {
		variables.add(variable);
	}

	public Variable findById(TermId termId) {
		return findById(termId.getId());
	}
	
	public Variable findById(int id) {
		if (variables != null) {
			for (Variable variable : variables) {
				if (variable.getVariableType().getId() == id) {
					return variable;
				}
			}
		}
		return null;
	}

	public List<Variable> getVariables() {
		return variables;
	}

	public void setVariables(List<Variable> variables) {
		this.variables = variables;
	}
	
	public VariableTypeList getVariableTypes() {
		if (variableTypes == null) {
			variableTypes = new VariableTypeList();
			for (Variable variable : variables) {
				variableTypes.add(variable.getVariableType());
			}
		}
		return variableTypes.sort();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VariableList [variables=");
		builder.append(variables);
		builder.append("]");
		return builder.toString();
	}	
	
	public VariableList sort(){
		Collections.sort(variables);
		if (variableTypes != null){
			variableTypes.sort();
		}
		return this;
	}

	public boolean containsValueByLocalName(String localName, String value) {
		boolean found = false;
		Variable variable = findByLocalName(localName);
		if (variable != null) {
			found = strEquals(variable.getValue(), value);
		}
		return found;
	}
	
	private boolean strEquals(String s1, String s2) {
		if (s1 == null && s2 == null) return true;
		if (s1 == s2) return true;
		if (s1 != null && s1.equals(s2)) return true;
		return false;
	}

	public Variable findByLocalName(String localName) {
		if (variables != null && localName != null) {
			for (Variable variable : variables) {
				if (localName.equals(variable.getVariableType().getLocalName())) {
					return variable;
				}
			}
		}
		return null;
	}
	
	public int size() {
		return (variables != null ? variables.size() : 0);
	}

	public void print(int indent) {
		if (variables != null) {
			for (Variable variable : variables) {
				variable.print(indent);
			}
		}
	}
}
