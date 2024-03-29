package org.generationcp.middleware.api.breedingmethod;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.generationcp.middleware.domain.sqlfilter.SqlTextFilter;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@AutoProperty
public class BreedingMethodSearchRequest {

	private String favoriteProgramUUID;
	private List<String> methodTypes = new ArrayList<>();
	private List<Integer> methodIds = new ArrayList<>();
	private List<String> methodAbbreviations = new ArrayList<>();
	private SqlTextFilter nameFilter;
	private Boolean filterFavoriteProgramUUID;
	private String description;
	private List<String> groups = new ArrayList<>();
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date methodDateFrom;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date methodDateTo;
	private List<Integer> methodClassIds = new ArrayList<>();
	private List<Integer> snameTypeIds = new ArrayList<>();

	public BreedingMethodSearchRequest() {

	}

	public String getFavoriteProgramUUID() {
		return this.favoriteProgramUUID;
	}

	public void setFavoriteProgramUUID(final String favoriteProgramUUID) {
		this.favoriteProgramUUID = favoriteProgramUUID;
	}

	public List<String> getMethodTypes() {
		return this.methodTypes;
	}

	public void setMethodTypes(final List<String> methodTypes) {
		this.methodTypes = methodTypes;
	}

	public List<Integer> getMethodIds() {
		return this.methodIds;
	}

	public void setMethodIds(final List<Integer> methodIds) {
		this.methodIds = methodIds;
	}

	public List<String> getMethodAbbreviations() {
		return this.methodAbbreviations;
	}

	public void setMethodAbbreviations(final List<String> methodAbbreviations) {
		this.methodAbbreviations = methodAbbreviations;
	}

	public SqlTextFilter getNameFilter() {
		return this.nameFilter;
	}

	public void setNameFilter(final SqlTextFilter nameFilter) {
		this.nameFilter = nameFilter;
	}

	public Boolean getFilterFavoriteProgramUUID() {
		return this.filterFavoriteProgramUUID;
	}

	public void setFilterFavoriteProgramUUID(final Boolean filterFavoriteProgramUUID) {
		this.filterFavoriteProgramUUID = filterFavoriteProgramUUID;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public List<String> getGroups() {
		return this.groups;
	}

	public void setGroups(final List<String> groups) {
		this.groups = groups;
	}

	public Date getMethodDateFrom() {
		return this.methodDateFrom;
	}

	public void setMethodDateFrom(final Date methodDateFrom) {
		this.methodDateFrom = methodDateFrom;
	}

	public Date getMethodDateTo() {
		return this.methodDateTo;
	}

	public void setMethodDateTo(final Date methodDateTo) {
		this.methodDateTo = methodDateTo;
	}

	public List<Integer> getMethodClassIds() {
		return this.methodClassIds;
	}

	public void setMethodClassIds(final List<Integer> methodClassIds) {
		this.methodClassIds = methodClassIds;
	}

	public List<Integer> getSnameTypeIds() {
		return this.snameTypeIds;
	}

	public void setSnameTypeIds(final List<Integer> snameTypeIds) {
		this.snameTypeIds = snameTypeIds;
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
