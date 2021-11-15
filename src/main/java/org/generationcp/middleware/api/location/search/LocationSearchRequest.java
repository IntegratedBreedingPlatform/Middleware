package org.generationcp.middleware.api.location.search;

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
	private String locationName;

	public LocationSearchRequest() {

	}

	public LocationSearchRequest(final String favoriteProgramUUID, final Set<Integer> locationTypeIds, final String locationName) {
		this.favoriteProgramUUID = favoriteProgramUUID;
		this.locationTypeIds = locationTypeIds;
		this.locationName = locationName;
	}

	public LocationSearchRequest(final String favoriteProgramUUID, final Set<Integer> locationTypeIds, final List<Integer> locationIds,
		final List<String> locationAbbreviations, final String locationName) {
		this.favoriteProgramUUID = favoriteProgramUUID;
		this.locationTypeIds = locationTypeIds;
		this.locationIds = locationIds;
		this.locationAbbreviations = locationAbbreviations;
		this.locationName = locationName;
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

	public String getLocationName() {
		return this.locationName;
	}

	public void setLocationName(final String locationName) {
		this.locationName = locationName;
	}

	public String getLocationTypeName() {
		return this.locationTypeName;
	}

	public void setLocationTypeName(final String locationType) {
		this.locationTypeName = locationType;
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
