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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Placeholder POJO for Parent Element.
 *
 */
@Embeddable
public class ParentElement implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "parent_a_nid")
	private Integer parentANId;

	@Column(name = "parent_b_nid")
	private Integer parentBNId;

	@Column(name = "mapping_type")
	private String mappingPopType;

	public ParentElement() {
	}

	public ParentElement(Integer parentANId, Integer parentBNId, String mappingType) {
		this.parentANId = parentANId;
		this.parentBNId = parentBNId;
		this.mappingPopType = mappingType;
	}

	public Integer getParentANId() {
		return this.parentANId;
	}

	public void setParentANId(Integer parentAGId) {
		this.parentANId = parentAGId;
	}

	public Integer getParentBGId() {
		return this.parentBNId;
	}

	public void setParentBGId(Integer parentBGId) {
		this.parentBNId = parentBGId;
	}

	public String getMappingType() {
		return this.mappingPopType;
	}

	public void setMappingType(String mappingType) {
		this.mappingPopType = mappingType;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 139).append(this.parentANId).append(this.parentBNId).append(this.mappingPopType).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof ParentElement)) {
			return false;
		}

		ParentElement rhs = (ParentElement) obj;
		return new EqualsBuilder().appendSuper(super.equals(obj)).append(this.parentANId, rhs.parentANId)
				.append(this.parentBNId, rhs.parentBNId).append(this.mappingPopType, rhs.mappingPopType).isEquals();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ParentElement [parentAGId=");
		builder.append(this.parentANId);
		builder.append(", parentBGId=");
		builder.append(this.parentBNId);
		builder.append(", mappingPopType=");
		builder.append(this.mappingPopType);
		builder.append("]");
		return builder.toString();
	}

}
