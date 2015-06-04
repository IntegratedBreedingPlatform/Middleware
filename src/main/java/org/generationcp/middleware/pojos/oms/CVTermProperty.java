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

package org.generationcp.middleware.pojos.oms;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * http://gmod.org/wiki/Chado_Tables#Table:_cvtermsynonym
 *
 * @author tippsgo
 *
 */
@Entity
@Table(name = "cvtermprop", uniqueConstraints = {@UniqueConstraint(columnNames = {"cvterm_id", "type_id", "value", "rank"})})
public class CVTermProperty implements Serializable {

	private static final long serialVersionUID = -6496723408899540369L;
	public static final String ID_NAME = "cvTermPropertyId";

	@Id
	@Basic(optional = false)
	@Column(name = "cvtermprop_id")
	private Integer cvTermPropertyId;

	/**
	 * Type of property.
	 */
	@Column(name = "type_id")
	private Integer typeId;

	/**
	 * Value of the property.
	 */
	@Column(name = "value")
	private String value;

	/**
	 * Rank of the property.
	 */
	@Column(name = "rank")
	private Integer rank;

	@Column(name = "cvterm_id")
	private Integer cvTermId;

	public Integer getCvTermPropertyId() {
		return this.cvTermPropertyId;
	}

	public CVTermProperty() {
	}

	public CVTermProperty(Integer cvTermPropertyId, Integer cvTermId, Integer typeId, String value, Integer rank) {
		super();
		this.cvTermPropertyId = cvTermPropertyId;
		this.cvTermId = cvTermId;
		this.typeId = typeId;
		this.value = value;
		this.rank = rank;
	}

	public void setCvTermPropertyId(Integer cvTermPropertyId) {
		this.cvTermPropertyId = cvTermPropertyId;
	}

	public Integer getTypeId() {
		return this.typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getRank() {
		return this.rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public Integer getCvTermId() {
		return this.cvTermId;
	}

	public void setCvTermId(Integer cvTermId) {
		this.cvTermId = cvTermId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.cvTermPropertyId == null ? 0 : this.cvTermPropertyId.hashCode());
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
		CVTermProperty other = (CVTermProperty) obj;
		if (this.cvTermPropertyId == null) {
			if (other.cvTermPropertyId != null) {
				return false;
			}
		} else if (!this.cvTermPropertyId.equals(other.cvTermPropertyId)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "CVTermProperty [cvTermPropertyId=" + this.cvTermPropertyId + ", typeId=" + this.typeId + ", value=" + this.value
				+ ", rank=" + this.rank + "]";
	}

}
