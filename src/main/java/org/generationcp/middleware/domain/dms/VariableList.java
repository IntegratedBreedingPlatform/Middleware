/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.domain.dms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.domain.oms.TermId;

/**
 * List of variables and types.
 */
public class VariableList implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<Variable> variables = new ArrayList<Variable>();
	private VariableTypeList variableTypes = null;

	public void add(Variable variable) {
		this.variables.add(variable);
	}

	public void addAll(VariableList variableList) {
		this.variables.addAll(variableList.getVariables());
	}

	public Variable findById(TermId termId) {
		return this.findById(termId.getId());
	}

	public Variable findById(int id) {
		if (this.variables != null) {
			for (Variable variable : this.variables) {
				if (variable.getVariableType().getId() == id) {
					return variable;
				}
			}
		}
		return null;
	}

	public List<Variable> getVariables() {
		return this.variables;
	}

	public void setVariables(List<Variable> variables) {
		this.variables = variables;
	}

	public VariableTypeList getVariableTypes() {
		if (this.variableTypes == null) {
			this.variableTypes = new VariableTypeList();
			for (Variable variable : this.variables) {
				this.variableTypes.add(variable.getVariableType());
			}
		}
		return this.variableTypes.sort();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VariableList [variables=");
		builder.append(this.variables);
		builder.append("]");
		return builder.toString();
	}

	public VariableList sort() {
		Collections.sort(this.variables);
		if (this.variableTypes != null) {
			this.variableTypes.sort();
		}
		return this;
	}

	public boolean containsValueByLocalName(String localName, String value) {
		boolean found = false;
		Variable variable = this.findByLocalName(localName);
		if (variable != null) {
			found = this.strEquals(variable.getValue(), value);
		}
		return found;
	}

	private boolean strEquals(String s1, String s2) {
		if (s1 == null && s2 == null) {
			return true;
		}
		if (s1 == s2) {
			return true;
		}
		if (s1 != null && s1.equals(s2)) {
			return true;
		}
		return false;
	}

	public Variable findByLocalName(String localName) {
		if (this.variables != null && localName != null) {
			for (Variable variable : this.variables) {
				if (localName.equalsIgnoreCase(variable.getVariableType().getLocalName())) {
					return variable;
				}
			}
		}
		return null;
	}

	public int size() {
		return this.variables != null ? this.variables.size() : 0;
	}

	public boolean isEmpty() {
		return this.variables == null || this.variables.isEmpty();
	}

	public void print(int indent) {
		if (this.variables != null) {
			for (Variable variable : this.variables) {
				variable.print(indent);
			}
		}
	}
}
