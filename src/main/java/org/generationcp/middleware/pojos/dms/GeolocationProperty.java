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

package org.generationcp.middleware.pojos.dms;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 *
 * http://gmod.org/wiki/Chado_Natural_Diversity_Module#Table:_nd_geolocationprop
 *
 * Property/value associations for geolocations. This table can store the properties such as location and environment
 * 
 * @author Joyce Avestro
 *
 */
@Entity
@Table(name = "nd_geolocationprop", uniqueConstraints = {@UniqueConstraint(columnNames = {"nd_geolocation_id", "type_id", "rank"})})
public class GeolocationProperty implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "nd_geolocationprop_id")
	private Integer geolocationPropertyId;

	@ManyToOne(targetEntity = Geolocation.class)
	@JoinColumn(name = "nd_geolocation_id", nullable = false)
	private Geolocation geolocation;

	@Column(name = "value")
	private String value;

	@Basic(optional = false)
	@Column(name = "rank")
	private Integer rank;

	// References cvterm
	@Column(name = "type_id")
	private Integer typeId;

	public GeolocationProperty() {
	}

	public GeolocationProperty(Integer geolocationPropertyId, Geolocation geolocation, String value, Integer rank, Integer typeId) {
		super();
		this.geolocationPropertyId = geolocationPropertyId;
		this.geolocation = geolocation;
		this.value = value;
		this.rank = rank;
		this.typeId = typeId;
	}

	public Integer getGeolocationPropertyId() {
		return this.geolocationPropertyId;
	}

	public void setGeolocationPropertyId(Integer geolocationPropertyId) {
		this.geolocationPropertyId = geolocationPropertyId;
	}

	public Geolocation getGeolocation() {
		return this.geolocation;
	}

	public void setGeolocation(Geolocation geolocation) {
		this.geolocation = geolocation;
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

	public Integer getTypeId() {
		return this.typeId;
	}

	public void setType(Integer typeId) {
		this.typeId = typeId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.geolocation == null ? 0 : this.geolocation.hashCode());
		result = prime * result + (this.geolocationPropertyId == null ? 0 : this.geolocationPropertyId.hashCode());
		result = prime * result + (this.rank == null ? 0 : this.rank.hashCode());
		result = prime * result + (this.typeId == null ? 0 : this.typeId.hashCode());
		result = prime * result + (this.value == null ? 0 : this.value.hashCode());
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
		GeolocationProperty other = (GeolocationProperty) obj;
		if (this.geolocation == null) {
			if (other.geolocation != null) {
				return false;
			}
		} else if (!this.geolocation.equals(other.geolocation)) {
			return false;
		}
		if (this.geolocationPropertyId == null) {
			if (other.geolocationPropertyId != null) {
				return false;
			}
		} else if (!this.geolocationPropertyId.equals(other.geolocationPropertyId)) {
			return false;
		}
		if (this.rank == null) {
			if (other.rank != null) {
				return false;
			}
		} else if (!this.rank.equals(other.rank)) {
			return false;
		}
		if (this.typeId == null) {
			if (other.typeId != null) {
				return false;
			}
		} else if (!this.typeId.equals(other.typeId)) {
			return false;
		}
		if (this.value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!this.value.equals(other.value)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GeolocationProperty [geolocationPropertyId=");
		builder.append(this.geolocationPropertyId);
		builder.append(", geolocation=");
		builder.append(this.geolocation);
		builder.append(", value=");
		builder.append(this.value);
		builder.append(", rank=");
		builder.append(this.rank);
		builder.append(", type=");
		builder.append(this.typeId);
		builder.append("]");
		return builder.toString();
	}

}
