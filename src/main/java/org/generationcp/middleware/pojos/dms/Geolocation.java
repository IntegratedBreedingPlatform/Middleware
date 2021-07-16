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

import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.pojos.InstanceExternalReference;
import org.hibernate.annotations.BatchSize;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The Geolocation table maps to the Location module of the logical data model. Information in this table corresponds to actual physical
 * locations where Field Trials are conducted.
 *
 * @author Darla Ani
 */
@Entity
@Table(name = "nd_geolocation")
public class Geolocation implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "nd_geolocation_id")
	private Integer locationId;

	@Column(name = "description")
	private String description;

	@Column(name = "latitude")
	private Double latitude;

	@Column(name = "longitude")
	private Double longitude;

	@Column(name = "geodetic_datum")
	private String geodeticDatum;

	@Column(name = "altitude")
	private Double altitude;

	@Transient
	private VariableList variates;

	/**
	 * List of Geolocation Properties
	 */
	@OneToMany(mappedBy = "geolocation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@BatchSize(size = 5000)
	private List<GeolocationProperty> properties;

	@OneToMany(mappedBy = "instance", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<InstanceExternalReference> externalReferences = new ArrayList<>();

	public Geolocation() {

	}

	public Geolocation(final Integer id) {
		super();
		this.locationId = id;
	}

	public Integer getLocationId() {
		return this.locationId;
	}

	public void setLocationId(final Integer id) {
		this.locationId = id;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public Double getLatitude() {
		return this.latitude;
	}

	public void setLatitude(final Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return this.longitude;
	}

	public void setLongitude(final Double longitude) {
		this.longitude = longitude;
	}

	public String getGeodeticDatum() {
		return this.geodeticDatum;
	}

	public void setGeodeticDatum(final String geodeticDatum) {
		this.geodeticDatum = geodeticDatum;
	}

	public Double getAltitude() {
		return this.altitude;
	}

	public void setAltitude(final Double altitude) {
		this.altitude = altitude;
	}

	public List<GeolocationProperty> getProperties() {
		return this.properties;
	}

	public void setProperties(final List<GeolocationProperty> properties) {
		this.properties = properties;
	}

	public List<InstanceExternalReference> getExternalReferences() {
		return this.externalReferences;
	}

	public void setExternalReferences(final List<InstanceExternalReference> externalReferences) {
		this.externalReferences = externalReferences;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.locationId == null ? 0 : this.locationId.hashCode());
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
		if (!(obj instanceof Geolocation)) {
			return false;
		}

		final Geolocation other = (Geolocation) obj;
		if (this.locationId == null) {
			if (other.locationId != null) {
				return false;
			}
		} else if (!this.locationId.equals(other.locationId)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Geolocation [locationId=");
		builder.append(this.locationId);
		builder.append(", description=");
		builder.append(this.description);
		builder.append(", latitude=");
		builder.append(this.latitude);
		builder.append(", longitude=");
		builder.append(this.longitude);
		builder.append(", geodeticDatum=");
		builder.append(this.geodeticDatum);
		builder.append(", altitude=");
		builder.append(this.altitude);
		builder.append("]");
		return builder.toString();
	}

	@Transient
	public VariableList getVariates() {
		return this.variates;
	}

	@Transient
	public void setVariates(final VariableList variates) {
		this.variates = variates;
	}

}
