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
	private Set<Integer> locationTypes = new HashSet<>();
	private List<Integer> locationIds = new ArrayList<>();
	private List<String> locationAbbreviations = new ArrayList<>();
	private String locationName;
	private boolean favoritesOnly;

	public LocationSearchRequest() {

	}

	public LocationSearchRequest(final String programUUID, final Set<Integer> locationTypes, final String locationName,
		final boolean favoritesOnly) {
		this.programUUID = programUUID;
		this.locationTypes = locationTypes;
		this.locationName = locationName;
		this.favoritesOnly = favoritesOnly;
	}

	public LocationSearchRequest(final String programUUID, final Set<Integer> locationTypes, final List<Integer> locationIds,
		final List<String> locationAbbreviations, final String locationName, final Boolean favoritesOnly) {
		this.programUUID = programUUID;
		this.locationTypes = locationTypes;
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

	public Set<Integer> getLocationTypes() {
		return this.locationTypes;
	}

	public void setLocationTypes(final Set<Integer> locationTypes) {
		this.locationTypes = locationTypes;
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
