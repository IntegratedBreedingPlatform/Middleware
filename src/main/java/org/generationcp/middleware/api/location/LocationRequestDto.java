package org.generationcp.middleware.api.location;

import org.pojomatic.Pojomatic;

public class LocationRequestDto {

	private String name;
	private Integer type;
	private String abbreviation;
	private Double latitude;
	private Double longitude;
	private Double altitude;
	private Integer countryId;
	private Integer provinceId;

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Integer getType() {
		return this.type;
	}

	public void setType(final Integer type) {
		this.type = type;
	}

	public String getAbbreviation() {
		return this.abbreviation;
	}

	public void setAbbreviation(final String abbreviation) {
		this.abbreviation = abbreviation;
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

	public Integer getCountryId() {
		return this.countryId;
	}

	public void setCountryId(final Integer countryId) {
		this.countryId = countryId;
	}

	public Integer getProvinceId() {
		return this.provinceId;
	}

	public void setProvinceId(final Integer provinceId) {
		this.provinceId = provinceId;
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

	public boolean allAttributesNull() {
		return this.getName() == null && this.getAbbreviation() == null &&
			this.getProvinceId() == null && this.getCountryId() == null &&
			this.getAltitude() == null && this.getLatitude() == null &&
			this.getLongitude() == null && this.getType() == null;
	}
}
