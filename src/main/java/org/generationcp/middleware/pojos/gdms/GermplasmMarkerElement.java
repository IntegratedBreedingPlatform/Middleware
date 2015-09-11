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

package org.generationcp.middleware.pojos.gdms;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * The Class GermplasmMarkerName. Contains the germplasm name and marker names associated with it. Used in getting marker names by
 * germplasmName.
 *
 * @author Joyce Avestro
 *
 */
public class GermplasmMarkerElement {

	private String germplasmName;

	private List<String> markerNames;

	GermplasmMarkerElement() {
	}

	public GermplasmMarkerElement(String germplasmName, List<String> markerNames) {
		this.germplasmName = germplasmName;
		this.markerNames = markerNames;
	}

	public List<String> getMarkerNames() {
		return this.markerNames;
	}

	public void setMarkerName(List<String> markerNames) {
		this.markerNames = markerNames;
	}

	public String getGermplasmName() {
		return this.germplasmName;
	}

	public void setGermplasmName(String germplasmName) {
		this.germplasmName = germplasmName;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(139, 11).append(this.germplasmName).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof GermplasmMarkerElement)) {
			return false;
		}

		GermplasmMarkerElement rhs = (GermplasmMarkerElement) obj;
		return new EqualsBuilder().append(this.germplasmName, rhs.germplasmName)
				.append(this.markerNames, rhs.markerNames).isEquals();
	}

	@Override
	public String toString() {
		StringBuilder markerNamesBuf = new StringBuilder("[");
		for (String mName : this.markerNames) {
			if (this.markerNames.indexOf(mName) != this.markerNames.size() - 1) { // if not the last element on the list
				markerNamesBuf.append(mName).append(", ");
			} else {
				markerNamesBuf.append(mName).append("]");
			}
		}
		if (this.markerNames.isEmpty()) { // if empty markerNames
			markerNamesBuf.append("]");
		}

		StringBuilder builder = new StringBuilder();
		builder.append("GermplasmMarkerElement [germplasmName=");
		builder.append(this.germplasmName);
		builder.append(", markerNames=");
		builder.append(markerNamesBuf);
		builder.append("]");
		return builder.toString();
	}

}
