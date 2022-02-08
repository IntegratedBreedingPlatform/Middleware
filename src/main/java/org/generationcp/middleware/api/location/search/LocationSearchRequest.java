package org.generationcp.middleware.api.location.search;

import org.generationcp.middleware.domain.sqlfilter.SqlTextFilter;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AutoProperty
public class LocationSearchRequest {

	private String favoriteProgramUUID;
	private Set<Integer> locationTypeIds = new HashSet<>();
	private List<Integer> locationIds = new ArrayList<>();
	private List<String> locationAbbreviations = new ArrayList<>();
	private String locationTypeName;
	private SqlTextFilter locationNameFilter;
	private Set<Integer> countryIds = new HashSet<>();
	private Set<Integer> provinceIds = new HashSet<>();
	private String countryName;
	private String provinceName;
	private Double latitudeFrom;
	private Double latitudeTo;
	private Double longitudeFrom;
	private Double longitudeTo;
	private Double altitudeFrom;
	private Double altitudeTo;
	private Boolean filterFavoriteProgramUUID;

	public LocationSearchRequest() {

	}

	public LocationSearchRequest(final String favoriteProgramUUID, final Set<Integer> locationTypeIds, final SqlTextFilter locationNameFilter) {
		this.favoriteProgramUUID = favoriteProgramUUID;
		this.locationTypeIds = locationTypeIds;
		this.locationNameFilter = locationNameFilter;
	}

	public LocationSearchRequest(final String favoriteProgramUUID, final Set<Integer> locationTypeIds, final List<Integer> locationIds,
		final List<String> locationAbbreviations, final SqlTextFilter locationNameFilter) {
		this.favoriteProgramUUID = favoriteProgramUUID;
		this.locationTypeIds = locationTypeIds;
		this.locationIds = locationIds;
		this.locationAbbreviations = locationAbbreviations;
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

	public List<Integer> getLocationIds() {
		return this.locationIds;
	}

	public void setLocationIds(final List<Integer> locationIds) {
		this.locationIds = locationIds;
	}

	public List<String> getLocationAbbreviations() {
		return this.locationAbbreviations;
	}

	public void setLocationAbbreviations(final List<String> locationAbbreviations) {
		this.locationAbbreviations = locationAbbreviations;
	}

	public SqlTextFilter getLocationNameFilter() {
		return this.locationNameFilter;
	}

	public void setLocationNameFilter(final SqlTextFilter locationNameFilter) {
		this.locationNameFilter = locationNameFilter;
	}

	public String getLocationTypeName() {
		return this.locationTypeName;
	}

	public void setLocationTypeName(final String locationType) {
		this.locationTypeName = locationType;
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
		return countryName;
	}

	public void setCountryName(final String countryName) {
		this.countryName = countryName;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(final String provinceName) {
		this.provinceName = provinceName;
	}

	public Double getLatitudeFrom() {
		return latitudeFrom;
	}

	public void setLatitudeFrom(final Double latitudeFrom) {
		this.latitudeFrom = latitudeFrom;
	}

	public Double getLatitudeTo() {
		return latitudeTo;
	}

	public void setLatitudeTo(final Double latitudeTo) {
		this.latitudeTo = latitudeTo;
	}

	public Double getLongitudeFrom() {
		return longitudeFrom;
	}

	public void setLongitudeFrom(final Double longitudeFrom) {
		this.longitudeFrom = longitudeFrom;
	}

	public Double getLongitudeTo() {
		return longitudeTo;
	}

	public void setLongitudeTo(final Double longitudeTo) {
		this.longitudeTo = longitudeTo;
	}

	public Double getAltitudeFrom() {
		return altitudeFrom;
	}

	public void setAltitudeFrom(final Double altitudeFrom) {
		this.altitudeFrom = altitudeFrom;
	}

	public Double getAltitudeTo() {
		return altitudeTo;
	}

	public void setAltitudeTo(final Double altitudeTo) {
		this.altitudeTo = altitudeTo;
	}

	public Boolean getFilterFavoriteProgramUUID() {
		return filterFavoriteProgramUUID;
	}

	public void setFilterFavoriteProgramUUID(final Boolean filterFavoriteProgramUUID) {
		this.filterFavoriteProgramUUID = filterFavoriteProgramUUID;
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
