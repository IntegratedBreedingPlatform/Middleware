package org.generationcp.middleware.api.breedingmethod;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.ArrayList;
import java.util.List;

@AutoProperty
public class BreedingMethodSearchRequest {

	private String programUUID;
	private List<String> methodTypes = new ArrayList<>();
	private List<Integer> methodIds = new ArrayList<>();
	private List<String> methodAbbreviations = new ArrayList<>();
	private boolean favoritesOnly;

	public BreedingMethodSearchRequest() {

	}

	public BreedingMethodSearchRequest(final String programUUID, final List<String> methodAbbreviations, final boolean favoritesOnly) {
		this.programUUID = programUUID;
		this.methodAbbreviations = methodAbbreviations;
		this.favoritesOnly = favoritesOnly;
	}

	public String getProgramUUID() {
		return programUUID;
	}

	public void setProgramUUID(final String programUUID) {
		this.programUUID = programUUID;
	}

	public List<String> getMethodTypes() {
		return methodTypes;
	}

	public void setMethodTypes(final List<String> methodTypes) {
		this.methodTypes = methodTypes;
	}

	public List<Integer> getMethodIds() {
		return methodIds;
	}

	public void setMethodIds(final List<Integer> methodIds) {
		this.methodIds = methodIds;
	}

	public List<String> getMethodAbbreviations() {
		return methodAbbreviations;
	}

	public void setMethodAbbreviations(final List<String> methodAbbreviations) {
		this.methodAbbreviations = methodAbbreviations;
	}

	public boolean isFavoritesOnly() {
		return favoritesOnly;
	}

	public void setFavoritesOnly(final boolean favoritesOnly) {
		this.favoritesOnly = favoritesOnly;
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
