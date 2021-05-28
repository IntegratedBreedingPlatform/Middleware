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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * http://gmod.org/wiki/Chado_Tables#Table:_cvtermsynonym
 *
 * @author tippsgo
 *
 */
@Entity
@Table(name = "cvtermprop", uniqueConstraints = {@UniqueConstraint(columnNames = {"cvterm_id", "type_id", "value", "rank"})})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE, region="cvtermprop")
public class CVTermProperty implements Serializable {

	private static final long serialVersionUID = -6496723408899540369L;
	public static final String ID_NAME = "cvTermPropertyId";

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
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

	public CVTermProperty(final Integer typeId, final String value, final Integer rank, final Integer cvTermId) {
		this.typeId = typeId;
		this.value = value;
		this.rank = rank;
		this.cvTermId = cvTermId;
	}

	public Integer getCvTermPropertyId() {
		return this.cvTermPropertyId;
	}

	public CVTermProperty() {
	}

	public CVTermProperty(final Integer cvTermPropertyId, final Integer cvTermId, final Integer typeId, final String value, final Integer rank) {
		super();
		this.cvTermPropertyId = cvTermPropertyId;
		this.cvTermId = cvTermId;
		this.typeId = typeId;
		this.value = value;
		this.rank = rank;
	}

	public void setCvTermPropertyId(final Integer cvTermPropertyId) {
		this.cvTermPropertyId = cvTermPropertyId;
	}

	public Integer getTypeId() {
		return this.typeId;
	}

	public void setTypeId(final Integer typeId) {
		this.typeId = typeId;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public Integer getRank() {
		return this.rank;
	}

	public void setRank(final Integer rank) {
		this.rank = rank;
	}

	public Integer getCvTermId() {
		return this.cvTermId;
	}

	public void setCvTermId(final Integer cvTermId) {
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
		final CVTermProperty other = (CVTermProperty) obj;
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
