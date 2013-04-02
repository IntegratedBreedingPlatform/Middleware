package org.generationcp.middleware.pojos.dms;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "nd_geolocation")
public class Geolocation implements Serializable {

	private static final long serialVersionUID = 1L;
	
    @Id
    @Basic(optional = false)
    @Column(name = "nd_geolocation_id")
	private Integer id;
	
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
    
    public Geolocation(){
    	
    }
    
    public Geolocation(Integer id){
    	super();
    	this.id = id;
    }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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
    
        
    
    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("Geolocation [id=" + id);
    	sb.append(", description=" + description);
    	sb.append(", latitude=" + latitude);
    	sb.append(", longitude=" + longitude);
    	sb.append(", geodeticDatum=" + geodeticDatum);
    	sb.append(", altitude=" + altitude);
    	sb.append("]");
    	
    	return sb.toString();
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	   
	
	

}
