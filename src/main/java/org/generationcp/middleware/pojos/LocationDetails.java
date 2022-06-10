/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.middleware.pojos;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.Comparator;

/**
 * POJO for location details query.
 * <p>
 * select locid, lname as location_name, c.isofull as country_full_name, l.labbr as location_abbreviation, ud.fname as location_type, ud.fdesc
 * as location_description from location l inner join cntry c on l.cntryid = c.cntryid inner join udflds ud on ud.fldno = l.ltype where
 * locid = 1
 *
 * @author Aldrich Abrogena
 */
// TODO: remove this class and instead use Location
@Entity
@Table(name = "location")
// JAXB Element Tags for JSON output
@XmlRootElement(name = "location")
@XmlType(propOrder = {"locid", "location_name", "country_full_name", "location_abbreviation", "location_type", "location_description"})
@XmlAccessorType(XmlAccessType.NONE)
public class LocationDetails implements Serializable, Comparable<LocationDetails> {

	private static final long serialVersionUID = 1L;

	public static final String GET_ALL = "getAllLocation";
	public static final String COUNT_ALL = "countAllLocation";

	public static final Comparator<LocationDetails> LocationNameComparator = new Comparator<LocationDetails>() {

		@Override
		public int compare(final LocationDetails location1, final LocationDetails location2) {
			final String locationName1 = location1.getLocationName().toUpperCase();
			final String locationName2 = location2.getLocationName().toUpperCase();

			// ascending order
			return locationName1.compareTo(locationName2);
		}

	};

	@Id
	@Basic(optional = false)
	@Column(name = "locid")
	private Integer locid;

	@Basic(optional = false)
	@Column(name = "location_name")
	private String locationName;

	@Basic(optional = false)
	@Column(name = "country_full_name")
	private String countryFullName;

	@Basic(optional = false)
	@Column(name = "cntryid")
	private Integer cntryid;

	@Basic(optional = false)
	@Column(name = "location_abbreviation")
	private String locationAbbreviation;

	@Basic(optional = false)
	@Column(name = "location_type")
	private String locationType;

	@Basic(optional = false)
	@Column(name = "ltype")
	private Integer ltype;

	@Basic(optional = false)
	@Column(name = "location_description")
	private String locationDescription;

	@Basic(optional = true)
	@Column(name = "latitude")
	private Double latitude;

	@Basic(optional = true)
	@Column(name = "longitude")
	private Double longitude;

	@Basic(optional = true)
	@Column(name = "altitude")
	private Double altitude;

	@Basic(optional = true)
	@Column(name = "cntry_name")
	private String countryName;

	@Basic(optional = true)
	@Column(name = "province_name")
	private String provinceName;

	@Basic(optional = true)
	@Column(name = "province_id")
	private Integer provinceId;

	public LocationDetails() {
	}

	public LocationDetails(final Integer locid) {
		this.locid = locid;
	}

	public LocationDetails(
		final Integer locid, final String locationName, final String countryFullName, final String locationAbbreviation, final String locationType,
			final String locationDescription) {
		super();
		this.locid = locid;
		this.locationName = locationName;
		this.countryFullName = countryFullName;
		this.locationAbbreviation = locationAbbreviation;
		this.locationType = locationType;
		this.locationDescription = locationDescription;

	}

	@Override
	public int hashCode() {
		return this.getLocid();
	}

	public Integer getLocid() {
		return this.locid;
	}

	public void setLocid(final Integer locid) {
		this.locid = locid;
	}

	public String getLocationName() {
		return this.locationName;
	}

	public void setLocationName(final String locationName) {
		this.locationName = locationName;
	}

	public String getCountryFullName() {
		return this.countryFullName;
	}

	public void setCountryFullName(final String countryFullName) {
		this.countryFullName = countryFullName;
	}

	public String getLocationAbbreviation() {
		return this.locationAbbreviation;
	}

	public void setLocationAbbreviation(final String locationAbbreviation) {
		this.locationAbbreviation = locationAbbreviation;
	}

	public String getLocationType() {
		return this.locationType;
	}

	public void setLocationType(final String locationType) {
		this.locationType = locationType;
	}

	public String getLocationDescription() {
		return this.locationDescription;
	}

	public void setLocationDescription(final String locationDescription) {
		this.locationDescription = locationDescription;
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

	public Double getAltitude() {
		return this.altitude;
	}

	public void setAltitude(final Double altitude) {
		this.altitude = altitude;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof LocationDetails) {
			final LocationDetails param = (LocationDetails) obj;
			if (this.getLocid().equals(param.getLocid())) {
				return true;
			}
		}

		return false;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Location [locid=");
		builder.append(this.locid);
		builder.append(", location_description=");
		builder.append(this.locationDescription);
		builder.append(", country_full_name=");
		builder.append(this.countryFullName);
		builder.append(", location_type=");
		builder.append(this.locationType);
		builder.append(", location_abbreviation=");
		builder.append(this.locationAbbreviation);
		builder.append(", location_name=");
		builder.append(this.locationName);
		builder.append(this.latitude);
		builder.append(", longitude=");
		builder.append(this.longitude);
		builder.append(", altitude=");
		builder.append(this.altitude);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int compareTo(final LocationDetails compareLocation) {
		final String compareName = compareLocation.getLocationName();

		// ascending order
		return this.locationName.compareTo(compareName);
	}

	public Integer getCntryid() {
		return this.cntryid;
	}

	public void setCntryid(final Integer cntryid) {
		this.cntryid = cntryid;
	}

	public Integer getLtype() {
		return this.ltype;
	}

	public void setLtype(final Integer ltype) {
		this.ltype = ltype;
	}

	public String getProvinceName() {
		return this.provinceName;
	}

	public void setProvinceName(final String provinceName) {
		this.provinceName = provinceName;
	}

	public String getCountryName() {
		return this.countryName;
	}

	public void setCountryName(final String countryName) {
		this.countryName = countryName;
	}

	public Integer getProvinceId() {
		return this.provinceId;
	}

	public void setProvinceId(final Integer provinceId) {
		this.provinceId = provinceId;
	}

}
