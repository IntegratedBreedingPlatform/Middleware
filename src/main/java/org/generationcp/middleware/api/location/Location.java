
package org.generationcp.middleware.api.location;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonView;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.generationcp.middleware.service.api.BrapiView;
import org.generationcp.middleware.api.location.Coordinate;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"locationDbId", "locationType", "name", "abbreviation", "countryCode", "countryName", "latitude", "longitude",
	"altitude", "coordinateDescription", "coordinateUncertainty", "coordinates", "documentationURL", "environmentType", "exposure", "instituteAddress",
	"instituteName", "siteStatus", "slope", "topography", "attributes", "additionalInfo"})
public class Location implements Serializable {

	private String locationDbId;

	private String locationType;

	@JsonView(BrapiView.BrapiV1_3.class)
	private String name;

	private String locationName;

	private String abbreviation;

	private String countryCode;

	private String countryName;

	@JsonView(BrapiView.BrapiV1_3.class)
	private Double latitude;

	@JsonView(BrapiView.BrapiV1_3.class)
	private Double longitude;

	@JsonView(BrapiView.BrapiV1_3.class)
	private Double altitude;

	@JsonView(BrapiView.BrapiV2.class)
	private String coordinateDescription;

	@JsonView(BrapiView.BrapiV2.class)
	private Double coordinateUncertainty;

	@JsonView(BrapiView.BrapiV2.class)
	private Coordinate coordinates;

	@JsonView(BrapiView.BrapiV2.class)
	private List<String> externalReferences;

	@JsonView(BrapiView.BrapiV2.class)
	private String documentationURL;

	@JsonView(BrapiView.BrapiV2.class)
	private String environmentType;

	@JsonView(BrapiView.BrapiV2.class)
	private String exposure;

	@JsonView(BrapiView.BrapiV2.class)
	private String instituteAddress;

	@JsonView(BrapiView.BrapiV2.class)
	private String instituteName;

	@JsonView(BrapiView.BrapiV2.class)
	private String siteStatus;

	@JsonView(BrapiView.BrapiV2.class)
	private Double slope;

	@JsonView(BrapiView.BrapiV2.class)
	private String topography;

	@JsonInclude(Include.NON_EMPTY)
	private List<Object> attributes = new ArrayList<>();

	private Map<String, String> additionalInfo = new HashMap<>();

	/**
	 * No args constructor required by serialization libraries.
	 */
	public Location() {
	}

	public Location(final String locationDbId, final String locationType, final String name, final String abbreviation,
		final String countryCode, final String countryName, final Double latitude, final Double longitude, final Double altitude,
		final List<Object> attributes, final Map<String, String> additionalInfo) {
		this.locationDbId = locationDbId;
		this.locationType = locationType;
		this.name = name;
		this.abbreviation = abbreviation;
		this.countryCode = countryCode;
		this.countryName = countryName;
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
		this.attributes = attributes;
		this.additionalInfo = additionalInfo;
	}

	/**
	 *
	 * @return The locationDbId
	 */
	public String getLocationDbId() {
		return this.locationDbId;
	}

	/**
	 *
	 * @param locationDbId The locationDbId
	 */
	public void setLocationDbId(final String locationDbId) {
		this.locationDbId = locationDbId;
	}

	public Location withLocationDbId(final String locationDbId) {
		this.locationDbId = locationDbId;
		return this;
	}

	/**
	 *
	 * @return The name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 *
	 * @param name The name
	 */
	public void setName(final String name) {
		this.name = name;
	}

	public Location withName(final String name) {
		this.name = name;
		return this;
	}

	/**
	 *
	 * @return The countryCode
	 */
	public String getCountryCode() {
		return this.countryCode;
	}

	/**
	 *
	 * @param countryCode The countryCode
	 */
	public void setCountryCode(final String countryCode) {
		this.countryCode = countryCode;
	}

	public Location withCountryCode(final String countryCode) {
		this.countryCode = countryCode;
		return this;
	}

	/**
	 *
	 * @return The countryName
	 */
	public String getCountryName() {
		return this.countryName;
	}

	/**
	 *
	 * @param countryName The countryName
	 */
	public void setCountryName(final String countryName) {
		this.countryName = countryName;
	}

	public Location withCountryName(final String countryName) {
		this.countryName = countryName;
		return this;
	}

	/**
	 *
	 * @return The latitude
	 */
	public Double getLatitude() {
		return this.latitude;
	}

	/**
	 *
	 * @param latitude The latitude
	 */
	public void setLatitude(final Double latitude) {
		this.latitude = latitude;
	}

	public Location withLatitude(final Double latitude) {
		this.latitude = latitude;
		return this;
	}

	/**
	 *
	 * @return The longitude
	 */
	public Double getLongitude() {
		return this.longitude;
	}

	/**
	 *
	 * @param longitude The longitude
	 */
	public void setLongitude(final Double longitude) {
		this.longitude = longitude;
	}

	public Location withLongitude(final Double longitude) {
		this.longitude = longitude;
		return this;
	}

	/**
	 *
	 * @return The altitude
	 */
	public Double getAltitude() {
		return this.altitude;
	}

	/**
	 *
	 * @param altitude The altitude
	 */
	public void setAltitude(final Double altitude) {
		this.altitude = altitude;
	}

	public Location withAltitude(final Double altitude) {
		this.altitude = altitude;
		return this;
	}

	/**
	 *
	 * @return The attributes
	 */
	public List<Object> getAttributes() {
		return this.attributes;
	}

	/**
	 *
	 * @param attributes The attributes
	 */
	public void setAttributes(final List<Object> attributes) {
		this.attributes = attributes;
	}

	public Location withAttributes(final List<Object> attributes) {
		this.attributes = attributes;
		return this;
	}

	public String getLocationType() {
		return this.locationType;
	}

	public void setLocationType(final String locationType) {
		this.locationType = locationType;
	}

	public Location withLocationType(final String locationType) {
		this.locationType = locationType;
		return this;
	}

	public String getAbbreviation() {
		return this.abbreviation;
	}

	public void setAbbreviation(final String abbreviation) {
		this.abbreviation = abbreviation;
	}

	public Location withAbbreviation(final String abbreviation) {
		this.abbreviation = abbreviation;
		return this;
	}

	public String getCoordinateDescription() {
		return this.coordinateDescription;
	}

	public Location setCoordinateDescription(final String coordinateDescription) {
		this.coordinateDescription = coordinateDescription;
		return this;
	}

	public Double getCoordinateUncertainty() {
		return this.coordinateUncertainty;
	}

	public Location setCoordinateUncertainty(final Double coordinateUncertainty) {
		this.coordinateUncertainty = coordinateUncertainty;
		return this;
	}

	public Coordinate getCoordinates() {
		return this.coordinates;
	}

	public void setCoordinates(final Coordinate coordinates) {
		this.coordinates = coordinates;
	}

	public Location withCoordinates(final Coordinate coordinates) {
		this.coordinates = coordinates;
		return this;
	}

	public List<String> getExternalReferences() {
		return this.externalReferences;
	}

	public Location setExternalReferences(final List<String> externalReferences) {
		this.externalReferences = externalReferences;
		return this;
	}

	public String getDocumentationURL() {
		return this.documentationURL;
	}

	public Location setDocumentationURL(final String documentationURL) {
		this.documentationURL = documentationURL;
		return this;
	}

	public String getEnvironmentType() {
		return this.environmentType;
	}

	public Location setEnvironmentType(final String environmentType) {
		this.environmentType = environmentType;
		return this;
	}

	public String getExposure() {
		return this.exposure;
	}

	public Location setExposure(final String exposure) {
		this.exposure = exposure;
		return this;
	}

	public String getInstituteAddress() {
		return this.instituteAddress;
	}

	public Location setInstituteAddress(final String instituteAddress) {
		this.instituteAddress = instituteAddress;
		return this;
	}

	public String getInstituteName() {
		return this.instituteName;
	}

	public Location setInstituteName(final String instituteName) {
		this.instituteName = instituteName;
		return this;
	}

	public String getSiteStatus() {
		return this.siteStatus;
	}

	public Location setSiteStatus(final String siteStatus) {
		this.siteStatus = siteStatus;
		return this;
	}

	public Double getSlope() {
		return this.slope;
	}

	public Location setSlope(final Double slope) {
		this.slope = slope;
		return this;
	}

	public String getTopography() {
		return this.topography;
	}

	public Location setTopography(final String topography) {
		this.topography = topography;
		return this;
	}

	public String getLocationName() {
		return this.locationName;
	}

	public void setLocationName(final String locationName) {
		this.locationName = locationName;
	}

	public Location withLocationName(final String locationName) {
		this.locationName = locationName;
		return this;
	}

	public Map<String, String> getAdditionalInfo() {
		return this.additionalInfo;
	}

	public void setAdditionalInfo(final Map<String, String> additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public Location withAdditionalInfo(final Map<String, String> additionalInfo) {
		this.additionalInfo = additionalInfo;
		return this;
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof Location)) {
			return false;
		}
		final Location castOther = (Location) other;
		return new EqualsBuilder().append(this.locationDbId, castOther.locationDbId).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.locationDbId).hashCode();
	}

	@Override
	public String toString() {
		return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
	}
}
