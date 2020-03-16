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

/**
 * This class is used to store variable list, germplasmId, locationId, and observationUnitNo to Experiment.
 *
 */
public abstract class Values {

	private VariableList variableList;
	private Integer germplasmId;
	private Integer locationId;
	private Integer observationUnitNo;

	public Values() {

	}

	public Values(final VariableList variableList, final Integer germplasmId, final Integer locationId) {
		super();
		this.variableList = variableList;
		this.germplasmId = germplasmId;
		this.locationId = locationId;
	}

	public VariableList getVariableList() {
		return this.variableList != null ? this.variableList.sort() : null;
	}

	public void setVariableList(final VariableList variableList) {
		this.variableList = variableList;
	}

	public Integer getGermplasmId() {
		return this.germplasmId;
	}

	public void setGermplasmId(final Integer germplasmId) {
		this.germplasmId = germplasmId;
	}

	public Integer getLocationId() {
		return this.locationId;
	}

	public void setLocationId(final Integer locationId) {
		this.locationId = locationId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.germplasmId == null ? 0 : this.germplasmId.hashCode());
		result = prime * result + (this.locationId == null ? 0 : this.locationId.hashCode());
		result = prime * result + (this.variableList == null ? 0 : this.variableList.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final Values other = (Values) obj;
		if (this.germplasmId == null) {
			if (other.germplasmId != null) {
				return false;
			}
		} else if (!this.germplasmId.equals(other.germplasmId)) {
			return false;
		}
		if (this.locationId == null) {
			if (other.locationId != null) {
				return false;
			}
		} else if (!this.locationId.equals(other.locationId)) {
			return false;
		}
		if (this.variableList == null) {
			return other.variableList == null;
		} else
			return this.variableList.equals(other.variableList);
	}

	public Integer getObservationUnitNo() {
		return this.observationUnitNo;
	}

	public void setObservationUnitNo(final Integer observationUnitNo) {
		this.observationUnitNo = observationUnitNo;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append(this.getEntityName() + " [variableList=");
		builder.append(this.variableList);
		builder.append(", germplasmId=");
		builder.append(this.germplasmId);
		builder.append(", locationId=");
		builder.append(this.locationId);
		builder.append("]");
		return builder.toString();
	}

	public String getEntityName() {
		return "Values";
	}

}
