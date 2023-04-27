package org.generationcp.middleware.api.location.search;

import org.generationcp.middleware.api.location.Coordinate;
import org.generationcp.middleware.domain.search_request.SearchRequestDto;
import org.generationcp.middleware.domain.sqlfilter.SqlTextFilter;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AutoProperty
public class LocationSearchRequest extends SearchRequestDto {

	private String favoriteProgramUUID;

	private Set<Integer> locationTypeIds = new HashSet<>();

	private List<Integer> locationDbIds = new ArrayList<>();
	private List<String> locationNames = new ArrayList<>();
	private List<String> locationTypes = new ArrayList<>();
	private List<String> abbreviations = new ArrayList<>();

	private SqlTextFilter locationNameFilter;
	private Set<Integer> countryIds = new HashSet<>();
	private Set<Integer> provinceIds = new HashSet<>();
	private String countryName;
	private String provinceName;
	private Double latitudeFrom;
	private Double latitudeTo;
	private Double longitudeFrom;
	private Double longitudeTo;

	private Double altitudeMin;
	private Double altitudeMax;

	private Boolean filterFavoriteProgramUUID;

	private Coordinate coordinates;
	private List<String> countryCodes = new ArrayList<>();
	private List<String> countryNames = new ArrayList<>();

	public LocationSearchRequest() {

	}

	public LocationSearchRequest(final Set<Integer> locationTypeIds, final List<Integer> locationDbIds,
		final List<String> locationAbbreviations, final SqlTextFilter locationNameFilter) {
		this.locationTypeIds = locationTypeIds;
		this.locationDbIds = locationDbIds;
		this.abbreviations = locationAbbreviations;
		this.locationNameFilter = locationNameFilter;
	}

	public String getFavoriteProgramUUID() {
		return this.favoriteProgramUUID;
	}

	public void setFavoriteProgramUUID(final String favoriteProgramUUID) {
		this.favoriteProgramUUID = favoriteProgramUUID;
	}

	public Set<Integer> getLocationTypeIds() {
		return this.locationTypeIds;
	}

	public void setLocationTypeIds(final Set<Integer> locationTypeIds) {
		this.locationTypeIds = locationTypeIds;
	}

	public List<Integer> getLocationDbIds() {
		return this.locationDbIds;
	}

	public void setLocationDbIds(final List<Integer> locationDbIds) {
		this.locationDbIds = locationDbIds;
	}

	public List<String> getAbbreviations() {
		return this.abbreviations;
	}

	public void setAbbreviations(final List<String> abbreviations) {
		this.abbreviations = abbreviations;
	}

	public SqlTextFilter getLocationNameFilter() {
		return this.locationNameFilter;
	}

	public void setLocationNameFilter(final SqlTextFilter locationNameFilter) {
		this.locationNameFilter = locationNameFilter;
	}

	public Set<Integer> getCountryIds() {
		return this.countryIds;
	}

	public void setCountryIds(final Set<Integer> countryIds) {
		this.countryIds = countryIds;
	}

	public Set<Integer> getProvinceIds() {
		return this.provinceIds;
	}

	public void setProvinceIds(final Set<Integer> provinceIds) {
		this.provinceIds = provinceIds;
	}

	public String getCountryName() {
		return this.countryName;
	}

	public void setCountryName(final String countryName) {
		this.countryName = countryName;
	}

	public String getProvinceName() {
		return this.provinceName;
	}

	public void setProvinceName(final String provinceName) {
		this.provinceName = provinceName;
	}

	public Double getLatitudeFrom() {
		return this.latitudeFrom;
	}

	public void setLatitudeFrom(final Double latitudeFrom) {
		this.latitudeFrom = latitudeFrom;
	}

	public Double getLatitudeTo() {
		return this.latitudeTo;
	}

	public void setLatitudeTo(final Double latitudeTo) {
		this.latitudeTo = latitudeTo;
	}

	public Double getLongitudeFrom() {
		return this.longitudeFrom;
	}

	public void setLongitudeFrom(final Double longitudeFrom) {
		this.longitudeFrom = longitudeFrom;
	}

	public Double getLongitudeTo() {
		return this.longitudeTo;
	}

	public void setLongitudeTo(final Double longitudeTo) {
		this.longitudeTo = longitudeTo;
	}

	public Double getAltitudeMin() {
		return this.altitudeMin;
	}

	public void setAltitudeMin(final Double altitudeMin) {
		this.altitudeMin = altitudeMin;
	}

	public Double getAltitudeMax() {
		return this.altitudeMax;
	}

	public void setAltitudeMax(final Double altitudeMax) {
		this.altitudeMax = altitudeMax;
	}

	public Boolean getFilterFavoriteProgramUUID() {
		return this.filterFavoriteProgramUUID;
	}

	public void setFilterFavoriteProgramUUID(final Boolean filterFavoriteProgramUUID) {
		this.filterFavoriteProgramUUID = filterFavoriteProgramUUID;
	}

	public List<String> getLocationNames() {
		return this.locationNames;
	}

	public void setLocationNames(final List<String> locationNames) {
		this.locationNames = locationNames;
	}

	public List<String> getLocationTypes() {
		return this.locationTypes;
	}

	public void setLocationTypes(final List<String> locationTypes) {
		this.locationTypes = locationTypes;
	}

	public Coordinate getCoordinates() {
		return this.coordinates;
	}

	public void setCoordinates(final Coordinate coordinates) {
		this.coordinates = coordinates;
	}

	public List<String> getCountryCodes() {
		return this.countryCodes;
	}

	public void setCountryCodes(final List<String> countryCodes) {
		this.countryCodes = countryCodes;
	}

	public List<String> getCountryNames() {
		return this.countryNames;
	}

	public void setCountryNames(final List<String> countryNames) {
		this.countryNames = countryNames;
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}

}
