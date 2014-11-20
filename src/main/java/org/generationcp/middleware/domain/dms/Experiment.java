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

import java.util.HashMap;
import java.util.Map;

/** 
 * Contains the details of an experiment - id, factors, variates.
 */
public class Experiment {

	private int id;
	
	private VariableList factors;
	
	private VariableList variates;
	
	private Integer locationId;
	
	private Map<String, Variable> variatesMap;

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

	/**
	 * @return the locationId
	 */
	public Integer getLocationId() {
		return locationId;
	}

	/**
	 * @param locationId the locationId to set
	 */
	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}

	public void setVariates(VariableList variates) {
		this.variates = variates;
		if(variatesMap == null){
			variatesMap = new HashMap<String, Variable>();
			if(variates != null){
				
				for(Variable var : variates.getVariables()){
					if(var != null && var.getVariableType() != null) {
                        variatesMap.put(Integer.toString(var.getVariableType().getId()), var);
                    }
				}
			}
		}		
	}
	
	

	public Map<String, Variable> getVariatesMap() {
		return variatesMap;
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
