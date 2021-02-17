package org.generationcp.middleware.api.location.search;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AutoProperty
public class LocationSearchRequest {

	private String programUUID;
	private Set<Integer> locationTypeIds = new HashSet<>();
	private List<Integer> locationIds = new ArrayList<>();
	private List<String> locationAbbreviations = new ArrayList<>();
	private String locationId;
	private String locationType;
	private String locationName;
	private boolean favoritesOnly;

	public LocationSearchRequest() {

	}

	public LocationSearchRequest(final String programUUID, final Set<Integer> locationTypeIds, final String locationName,
		final boolean favoritesOnly) {
		this.programUUID = programUUID;
		this.locationTypeIds = locationTypeIds;
		this.locationName = locationName;
		this.favoritesOnly = favoritesOnly;
	}

	public LocationSearchRequest(final String programUUID, final Set<Integer> locationTypeIds, final List<Integer> locationIds,
		final List<String> locationAbbreviations, final String locationName, final Boolean favoritesOnly) {
		this.programUUID = programUUID;
		this.locationTypeIds = locationTypeIds;
		this.locationIds = locationIds;
		this.locationAbbreviations = locationAbbreviations;
		this.locationName = locationName;
		this.favoritesOnly = favoritesOnly;
	}

	public boolean getFavoritesOnly() {
		return this.favoritesOnly;
	}

	public void setFavoritesOnly(final boolean favoritesOnly) {
		this.favoritesOnly = favoritesOnly;
	}

	public String getProgramUUID() {
		return this.programUUID;
	}

	public void setProgramUUID(final String programUUID) {
		this.programUUID = programUUID;
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

	public String getLocationName() {
		return this.locationName;
	}

	public void setLocationName(final String locationName) {
		this.locationName = locationName;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(final String locationId) {
		this.locationId = locationId;
	}

	public String getLocationType() {
		return locationType;
	}

	public void setLocationType(final String locationType) {
		this.locationType = locationType;
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
