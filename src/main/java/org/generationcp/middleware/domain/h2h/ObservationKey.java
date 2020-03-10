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

package org.generationcp.middleware.domain.h2h;

import org.generationcp.middleware.util.Debug;

/**
 * The primary identifier of Observation.
 *
 */
public class ObservationKey {

	private int traitId;

	private int germplasmId;

	private int instanceId;

	public ObservationKey(int traitId, int instanceId) {
		this.traitId = traitId;
		this.instanceId = instanceId;
	}

	public ObservationKey(int traitId, int germplasmId, int instanceId) {
		this.traitId = traitId;
		this.germplasmId = germplasmId;
		this.instanceId = instanceId;
	}

	public int getTraitId() {
		return this.traitId;
	}

	public void setTraitId(int traitId) {
		this.traitId = traitId;
	}

	public int getGermplasmId() {
		return this.germplasmId;
	}

	public void setGermplasmId(int germplasmId) {
		this.germplasmId = germplasmId;
	}

	public int getInstanceId() {
		return this.instanceId;
	}

	public void setInstanceId(int instanceId) {
		this.instanceId = instanceId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.instanceId;
		result = prime * result + this.germplasmId;
		result = prime * result + this.traitId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		ObservationKey other = (ObservationKey) obj;
		if (this.instanceId != other.instanceId) {
			return false;
		}
		if (this.germplasmId != other.germplasmId) {
			return false;
		}
		if (this.traitId != other.traitId) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ObservationKey [traitId=");
		builder.append(this.traitId);
		builder.append(", germplasmId=");
		builder.append(this.germplasmId);
		builder.append(", instanceId=");
		builder.append(this.instanceId);
		builder.append("]");
		return builder.toString();
	}

	public void print(int indent) {
		Debug.println(indent, "Observation Key: ");
		Debug.println(indent + 3, "Trait Id: " + this.getTraitId());
		Debug.println(indent + 3, "Germplasm Id: " + this.getGermplasmId());
		Debug.println(indent + 3, "Environment Id: " + this.getInstanceId());
	}

}
