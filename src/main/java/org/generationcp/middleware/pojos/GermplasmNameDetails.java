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

package org.generationcp.middleware.pojos;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Placeholder POJO for Gid-Nid Pairs Element
 *
 * @author Joyce Avestro
 *
 */
public class GermplasmNameDetails implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer germplasmId;

	private Integer nameId;

	private String nVal;

	public GermplasmNameDetails(Integer germplasmId, Integer nameId) {
		this.germplasmId = germplasmId;
		this.nameId = nameId;
	}

	public GermplasmNameDetails(Integer germplasmId, Integer nameId, String nVal) {
		this.germplasmId = germplasmId;
		this.nameId = nameId;
		this.nVal = nVal;
	}

	public Integer getGermplasmId() {
		return this.germplasmId;
	}

	public void setGermplasmId(Integer germplasmId) {
		this.germplasmId = germplasmId;
	}

	public Integer getNameId() {
		return this.nameId;
	}

	public void setNameId(Integer nameId) {
		this.nameId = nameId;
	}

	public String getNVal() {
		return this.nVal;
	}

	public void setNVal(String nVal) {
		this.nVal = nVal;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GermplasmNameDetails [germplasmId=");
		builder.append(this.germplasmId);
		builder.append(", nameId=");
		builder.append(this.nameId);
		builder.append(", nVal=");
		builder.append(this.nVal);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof GermplasmNameDetails)) {
			return false;
		}

		GermplasmNameDetails rhs = (GermplasmNameDetails) obj;
		return new EqualsBuilder().append(this.germplasmId, rhs.germplasmId).append(this.nameId, rhs.nameId)
				.append(this.nVal, rhs.nVal).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(77, 7).append(this.germplasmId).append(this.nameId).append(this.nVal).toHashCode();
	}

}
