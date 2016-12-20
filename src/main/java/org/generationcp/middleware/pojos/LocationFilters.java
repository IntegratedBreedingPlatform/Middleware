
package org.generationcp.middleware.pojos;

import java.io.Serializable;
import java.util.Comparator;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * POJO for location details query.
 *
 * SELECT l.locid as locationDbId, ud.fname as locationType, l.lname as name, labbr as abbreviation, c.isothree as countryCode, c.isoabbr as
 * countryName, g.lat as latitude, g.lon as longitude, g.alt as altitude FROM location l LEFT JOIN georef g on l.locid = g.locid LEFT JOIN
 * cntry c on l.cntryid = c.cntryid LEFT JOIN udflds ud on ud.fldno = l.ltype WHERE l.ltype = 410 ORDER BY l.locid limit 0, 1000
 *
 * @author Diego Cuenya
 */
@Entity
@Table(name = "location")
// JAXB Element Tags for JSON output
@XmlRootElement(name = "location")
@XmlType(propOrder = {"locationDbId", "name", "countryName", "abbreviation", "locationType"})
@XmlAccessorType(XmlAccessType.NONE)
public class LocationFilters implements Serializable, Comparable<LocationFilters> {

	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@Column(name = "locationDbId")
	private Integer locationDbId;

	@Basic(optional = false)
	@Column(name = "locationType")
	private String locationType;

	@Basic(optional = false)
	@Column(name = "name")
	private String name;

	@Basic(optional = false)
	@Column(name = "abbreviation")
	private String abbreviation;

	@Basic(optional = false)
	@Column(name = "countryCode")
	private String countryCode;

	@Basic(optional = false)
	@Column(name = "countryName")
	private String countryName;

	@Basic(optional = true)
	@Column(name = "latitude")
	private Double latitude;

	@Basic(optional = true)
	@Column(name = "longitude")
	private Double longitude;

	@Basic(optional = true)
	@Column(name = "altitude")
	private Double altitude;

	public LocationFilters() {
	}

	public LocationFilters(Integer locationDbId) {
		this.locationDbId = locationDbId;
	}

	public LocationFilters(final Integer locationDbId, final String locationType, final String name, final String abbreviation,
			final String countryCode, final String countryName, final Double latitude, final Double longitude, final Double altitude) {
		super();
		this.locationDbId = locationDbId;
		this.locationType = locationType;
		this.name = name;
		this.abbreviation = abbreviation;
		this.countryCode = countryCode;
		this.countryName = countryName;
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;

	}

	public Integer getLocationDbId() {
		return locationDbId;
	}

	public void setLocationDbId(Integer locationDbId) {
		this.locationDbId = locationDbId;
	}

	public String getLocationType() {
		return locationType;
	}

	public void setLocationType(String locationType) {
		this.locationType = locationType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
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

	public Double getAltitude() {
		return altitude;
	}

	public void setAltitude(Double altitude) {
		this.altitude = altitude;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof LocationDetails) {
			LocationFilters param = (LocationFilters) obj;
			if (this.getLocationDbId().equals(param.getLocationDbId())) {
				return true;
			}
		}

		return false;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Location [locationDbId=");
		builder.append(this.locationDbId);
		builder.append(", locationType=");
		builder.append(this.locationType);
		builder.append(", name=");
		builder.append(this.name);
		builder.append(", abbreviation=");
		builder.append(this.abbreviation);
		builder.append(", countryCode=");
		builder.append(this.countryCode);
		builder.append(", countryName=");
		builder.append(this.countryName);
		builder.append(this.latitude);
		builder.append(", longitude=");
		builder.append(this.longitude);
		builder.append(", altitude=");
		builder.append(this.altitude);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int compareTo(LocationFilters compareLocation) {
		String compareName = compareLocation.getName();

		// ascending order
		return this.name.compareTo(compareName);
	}

	public static Comparator<LocationFilters> LocationNameComparator = new Comparator<LocationFilters>() {

		@Override
		public int compare(LocationFilters location1, LocationFilters location2) {
			String locationName1 = location1.getName().toUpperCase();
			String locationName2 = location2.getName().toUpperCase();

			// ascending order
			return locationName1.compareTo(locationName2);
		}

	};
}
