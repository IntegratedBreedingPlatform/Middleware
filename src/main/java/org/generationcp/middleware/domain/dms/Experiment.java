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

import org.generationcp.middleware.util.Debug;

/** 
 * Contains the details of an experiment - id, factors, variates.
 */
public class Experiment {

	private int id;
	
	private VariableList factors;
	
	private VariableList variates;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public VariableList getFactors() {
		return factors.sort();
	}

	public void setFactors(VariableList factors) {
		this.factors = factors;
	}

	public VariableList getVariates() {
		return variates.sort();
	}

	public void setVariates(VariableList variates) {
		this.variates = variates;
	}

	public void print(int indent) {
		Debug.println(indent, "Experiment: " + id);
		Debug.println(indent + 3, "Factors:");
		if (factors != null) {
			for (Variable variable : factors.getVariables()) {
				variable.print(indent + 6);
			}
		}
		Debug.println(indent + 3, "Variates:");
		if (factors != null) {
			for (Variable variate : variates.getVariables()) {
				variate.print(indent + 6);
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Experiment [id=");
		builder.append(id);
		builder.append(", factors=");
		builder.append(factors);
		builder.append(", variates=");
		builder.append(variates);
		builder.append("]");
		return builder.toString();
	}
	
	
}
