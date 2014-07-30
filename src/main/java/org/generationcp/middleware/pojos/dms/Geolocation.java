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
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.generationcp.middleware.domain.dms.VariableList;

/**
 * 
 * The Geolocation table maps to the Location module of the logical data model. 
 * Information in this table corresponds to actual physical locations where Field Trials are conducted. 
 *  
 * @author Darla Ani
 *
 */
@Entity
@Table(name = "nd_geolocation")
public class Geolocation implements Serializable {

	private static final long serialVersionUID = 1L;
	
    @Id
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
	private List<GeolocationProperty> properties;
	
    
    public Geolocation(){
    	
    }
    
    public Geolocation(Integer id){
    	super();
    	this.locationId = id;
    }

	public Integer getLocationId() {
		return locationId;
	}

	public void setLocationId(Integer id) {
		this.locationId = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getGeodeticDatum() {
		return geodeticDatum;
	}

	public void setGeodeticDatum(String geodeticDatum) {
		this.geodeticDatum = geodeticDatum;
	}

	public Double getAltitude() {
		return altitude;
	}

	public void setAltitude(Double altitude) {
		this.altitude = altitude;
	}
    

	public List<GeolocationProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<GeolocationProperty> properties) {
		this.properties = properties;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((locationId == null) ? 0 : locationId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Geolocation))
			return false;
		
		Geolocation other = (Geolocation) obj;
		if (locationId == null) {
			if (other.locationId != null)
				return false;
		} else if (!locationId.equals(other.locationId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Geolocation [locationId=");
		builder.append(locationId);
		builder.append(", description=");
		builder.append(description);
		builder.append(", latitude=");
		builder.append(latitude);
		builder.append(", longitude=");
		builder.append(longitude);
		builder.append(", geodeticDatum=");
		builder.append(geodeticDatum);
		builder.append(", altitude=");
		builder.append(altitude);
		builder.append("]");
		return builder.toString();
	}

	@Transient 
	public VariableList getVariates() {
		return variates;
	}

	@Transient 
	public void setVariates(VariableList variates) {
		this.variates = variates;
	}

	

}
