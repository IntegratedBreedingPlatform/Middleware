package org.generationcp.middleware.api.germplasmlist.search;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.generationcp.middleware.domain.search_request.SearchRequestDto;
import org.generationcp.middleware.domain.sqlfilter.SqlTextFilter;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Date;
import java.util.List;

@AutoProperty
public class GermplasmListDataSearchRequest extends SearchRequestDto {

	private List<Integer> entryNumbers;
	private List<Integer> gids;
	private String germplasmUUID;
	private SqlTextFilter designationFilter;
	private SqlTextFilter immediateSourceName;
	private SqlTextFilter groupSourceName;
	private SqlTextFilter femaleParentName;
	private SqlTextFilter maleParentName;
	private String breedingMethodName;
	private String breedingMethodAbbreviation;
	private String breedingMethodGroup;
	private String locationName;
	private String locationAbbreviation;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date germplasmDateFrom;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date germplasmDateTo;
	private String reference;

	public List<Integer> getEntryNumbers() {
		return entryNumbers;
	}

	public void setEntryNumbers(final List<Integer> entryNumbers) {
		this.entryNumbers = entryNumbers;
	}

	public List<Integer> getGids() {
		return gids;
	}

	public void setGids(final List<Integer> gids) {
		this.gids = gids;
	}

	public String getGermplasmUUID() {
		return germplasmUUID;
	}

	public void setGermplasmUUID(final String germplasmUUID) {
		this.germplasmUUID = germplasmUUID;
	}

	public SqlTextFilter getDesignationFilter() {
		return designationFilter;
	}

	public void setDesignationFilter(final SqlTextFilter designationFilter) {
		this.designationFilter = designationFilter;
	}

	public SqlTextFilter getImmediateSourceName() {
		return immediateSourceName;
	}

	public void setImmediateSourceName(final SqlTextFilter immediateSourceName) {
		this.immediateSourceName = immediateSourceName;
	}

	public SqlTextFilter getGroupSourceName() {
		return groupSourceName;
	}

	public void setGroupSourceName(final SqlTextFilter groupSourceName) {
		this.groupSourceName = groupSourceName;
	}

	public SqlTextFilter getFemaleParentName() {
		return femaleParentName;
	}

	public void setFemaleParentName(final SqlTextFilter femaleParentName) {
		this.femaleParentName = femaleParentName;
	}

	public SqlTextFilter getMaleParentName() {
		return maleParentName;
	}

	public void setMaleParentName(final SqlTextFilter maleParentName) {
		this.maleParentName = maleParentName;
	}

	public String getBreedingMethodName() {
		return breedingMethodName;
	}

	public void setBreedingMethodName(final String breedingMethodName) {
		this.breedingMethodName = breedingMethodName;
	}

	public String getBreedingMethodAbbreviation() {
		return breedingMethodAbbreviation;
	}

	public void setBreedingMethodAbbreviation(final String breedingMethodAbbreviation) {
		this.breedingMethodAbbreviation = breedingMethodAbbreviation;
	}

	public String getBreedingMethodGroup() {
		return breedingMethodGroup;
	}

	public void setBreedingMethodGroup(final String breedingMethodGroup) {
		this.breedingMethodGroup = breedingMethodGroup;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(final String locationName) {
		this.locationName = locationName;
	}

	public String getLocationAbbreviation() {
		return locationAbbreviation;
	}

	public void setLocationAbbreviation(final String locationAbbreviation) {
		this.locationAbbreviation = locationAbbreviation;
	}

	public Date getGermplasmDateFrom() {
		return germplasmDateFrom;
	}

	public void setGermplasmDateFrom(final Date germplasmDateFrom) {
		this.germplasmDateFrom = germplasmDateFrom;
	}

	public Date getGermplasmDateTo() {
		return germplasmDateTo;
	}

	public void setGermplasmDateTo(final Date germplasmDateTo) {
		this.germplasmDateTo = germplasmDateTo;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(final String reference) {
		this.reference = reference;
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
