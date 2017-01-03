
package org.generationcp.middleware.service.api.location;

import java.io.Serializable;
import java.util.Comparator;

public class LocationDetailsDto implements Serializable, Comparable<LocationDetailsDto> {


	/**
	 * 
	 */
	private static final long serialVersionUID = 2241318661332095315L;

	private Integer locationDbId;

	private String locationType;

	private String name;

	private String abbreviation;

	private String countryCode;

	private String countryName;

	private Double latitude;

	private Double longitude;

	private Double altitude;

	public LocationDetailsDto() {
	}

	public LocationDetailsDto(Integer locationDbId) {
		this.locationDbId = locationDbId;
	}

	public LocationDetailsDto(final Integer locationDbId, final String locationType, final String name, final String abbreviation,
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

		if (obj instanceof LocationDetailsDto) {
			LocationDetailsDto param = (LocationDetailsDto) obj;
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
	public int compareTo(LocationDetailsDto compareLocation) {
		String compareName = compareLocation.getName();

		// ascending order
		return this.name.compareTo(compareName);
	}

	public static Comparator<LocationDetailsDto> LocationNameComparator = new Comparator<LocationDetailsDto>() {

		@Override
		public int compare(LocationDetailsDto location1, LocationDetailsDto location2) {
			String locationName1 = location1.getName().toUpperCase();
			String locationName2 = location2.getName().toUpperCase();

			// ascending order
			return locationName1.compareTo(locationName2);
		}

	};
}
