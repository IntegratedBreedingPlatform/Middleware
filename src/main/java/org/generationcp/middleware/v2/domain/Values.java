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
package org.generationcp.middleware.v2.domain;

/**
 * This class is used to store variable list, germplasmId and locationId to Experiment. 
 * 
 */
public abstract class Values {

	private VariableList variableList;
	private Integer germplasmId;
	private Integer locationId;

	public Values() {

	}

	public Values(VariableList variableList, Integer germplasmId,
			Integer locationId) {
		super();
		this.variableList = variableList;
		this.germplasmId = germplasmId;
		this.locationId = locationId;
	}

	public VariableList getVariableList() {
		return variableList.sort();
	}

	public void setVariableList(VariableList variableList) {
		this.variableList = variableList;
	}

	public Integer getGermplasmId() {
		return germplasmId;
	}

	public void setGermplasmId(Integer germplasmId) {
		this.germplasmId = germplasmId;
	}

	public Integer getLocationId() {
		return locationId;
	}

	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((germplasmId == null) ? 0 : germplasmId.hashCode());
		result = prime * result
				+ ((locationId == null) ? 0 : locationId.hashCode());
		result = prime * result
				+ ((variableList == null) ? 0 : variableList.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Values other = (Values) obj;
		if (germplasmId == null) {
			if (other.germplasmId != null)
				return false;
		} else if (!germplasmId.equals(other.germplasmId))
			return false;
		if (locationId == null) {
			if (other.locationId != null)
				return false;
		} else if (!locationId.equals(other.locationId))
			return false;
		if (variableList == null) {
			if (other.variableList != null)
				return false;
		} else if (!variableList.equals(other.variableList))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getEntityName() + " [variableList=");
		builder.append(variableList);
		builder.append(", germplasmId=");
		builder.append(germplasmId);
		builder.append(", locationId=");
		builder.append(locationId);
		builder.append("]");
		return builder.toString();
	}

	public String getEntityName(){
		return "Values";
	}
	
	
}
