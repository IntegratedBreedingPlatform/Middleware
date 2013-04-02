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

package org.generationcp.middleware.pojos.dms;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * 
 * http://gmod.org/wiki/Chado_Natural_Diversity_Module#Table:_nd_geolocationprop
 * 
 * Property/value associations for geolocations. 
 * This table can store the properties such as location and environment
 *  
 * @author Joyce Avestro
 *
 */
@Entity
@Table(name = "nd_geolocationprop", 
		uniqueConstraints = {
			@UniqueConstraint(columnNames = { "nd_geolocation_id", "type_id", "rank" })
		 })
public class GeolocationProperty implements Serializable {

	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue
    @Basic(optional = false)
    @Column(name = "nd_geolocationprop_id")
	private Long geolocationPropertyId;

    @OneToOne
    @JoinColumn(name = "nd_geolocation_id")
	private Geolocation geolocation;

    @Column(name = "value")
	private String value;
	
    @Basic(optional = false)
    @Column(name = "rank")
	private Long rank;
	
    @OneToOne
    @JoinColumn(name="type_id", referencedColumnName="cvterm_id")
    private CVTerm type;
    
	public GeolocationProperty() {
	}

	public GeolocationProperty(Long geolocationPropertyId,
			Geolocation geolocation, String value, Long rank, CVTerm type) {
		super();
		this.geolocationPropertyId = geolocationPropertyId;
		this.geolocation = geolocation;
		this.value = value;
		this.rank = rank;
		this.type = type;
	}

	public Long getGeolocationPropertyId() {
		return geolocationPropertyId;
	}

	public void setGeolocationPropertyId(Long geolocationPropertyId) {
		this.geolocationPropertyId = geolocationPropertyId;
	}

	public Geolocation getGeolocation() {
		return geolocation;
	}

	public void setGeolocation(Geolocation geolocation) {
		this.geolocation = geolocation;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Long getRank() {
		return rank;
	}

	public void setRank(Long rank) {
		this.rank = rank;
	}

	public CVTerm getType() {
		return type;
	}

	public void setType(CVTerm type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((geolocation == null) ? 0 : geolocation.hashCode());
		result = prime
				* result
				+ ((geolocationPropertyId == null) ? 0 : geolocationPropertyId
						.hashCode());
		result = prime * result + ((rank == null) ? 0 : rank.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		GeolocationProperty other = (GeolocationProperty) obj;
		if (geolocation == null) {
			if (other.geolocation != null)
				return false;
		} else if (!geolocation.equals(other.geolocation))
			return false;
		if (geolocationPropertyId == null) {
			if (other.geolocationPropertyId != null)
				return false;
		} else if (!geolocationPropertyId.equals(other.geolocationPropertyId))
			return false;
		if (rank == null) {
			if (other.rank != null)
				return false;
		} else if (!rank.equals(other.rank))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GeolocationProperty [geolocationPropertyId=");
		builder.append(geolocationPropertyId);
		builder.append(", geolocation=");
		builder.append(geolocation);
		builder.append(", value=");
		builder.append(value);
		builder.append(", rank=");
		builder.append(rank);
		builder.append(", type=");
		builder.append(type);
		builder.append("]");
		return builder.toString();
	}
    
    

}
