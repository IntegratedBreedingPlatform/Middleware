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
import java.util.HashMap;
import java.util.Map;

import org.generationcp.middleware.util.Debug;

/**
 * Contains the details of an experiment - id, factors, variates.
 */
public class Experiment implements Serializable{

	private static final long serialVersionUID = 1L;

	private int id;

	private VariableList factors;

	private VariableList variates;

	private Integer locationId;

	private Map<String, Variable> variatesMap;

	private String obsUnitId;

	private Map<Integer, String> nameValueMap;

	public int getId() {
		return this.id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public VariableList getFactors() {
		return this.factors.sort();
	}

	public void setFactors(final VariableList factors) {
		this.factors = factors;
	}

	public VariableList getVariates() {
		return this.variates.sort();
	}

	/**
	 * @return the locationId
	 */
	public Integer getLocationId() {
		return this.locationId;
	}

	/**
	 * @param locationId the locationId to set
	 */
	public void setLocationId(final Integer locationId) {
		this.locationId = locationId;
	}

	public void setVariates(final VariableList variates) {
		this.variates = variates;
		if (this.variatesMap == null) {
			this.variatesMap = new HashMap<>();
			if (variates != null) {

				for (final Variable var : variates.getVariables()) {
					if (var != null && var.getVariableType() != null) {
						this.variatesMap.put(Integer.toString(var.getVariableType().getId()), var);
					}
				}
			}
		}
	}

	public Map<String, Variable> getVariatesMap() {
		return this.variatesMap;
	}

	public String getObsUnitId() {
		return this.obsUnitId;
	}

	public void setObsUnitId(final String obsUnitId) {
		this.obsUnitId = obsUnitId;
	}

	public void print(final int indent) {
		Debug.println(indent, "Experiment: " + this.id);
		Debug.println(indent + 3, "Factors:");
		if (this.factors != null) {
			for (final Variable variable : this.factors.getVariables()) {
				variable.print(indent + 6);
			}
		}
		Debug.println(indent + 3, "Variates:");
		if (this.factors != null) {
			for (final Variable variate : this.variates.getVariables()) {
				variate.print(indent + 6);
			}
		}
	}

	public Map<Integer, String> getNameValueMap() {
		return this.nameValueMap;
	}

	public void setNameValueMap(final Map<Integer, String> nameValueMap) {
		this.nameValueMap = nameValueMap;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Experiment [id=");
		builder.append(this.id);
		builder.append(", factors=");
		builder.append(this.factors);
		builder.append(", variates=");
		builder.append(this.variates);
		builder.append("]");
		return builder.toString();
	}

}
