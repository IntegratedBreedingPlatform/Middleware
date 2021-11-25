package org.generationcp.middleware.api.germplasmlist.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.generationcp.middleware.domain.search_request.SearchRequestDto;
import org.generationcp.middleware.domain.sqlfilter.SqlTextFilter;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Date;
import java.util.List;
import java.util.Map;

@AutoProperty
public class GermplasmListDataSearchRequest extends SearchRequestDto {

	private List<Integer> listDataIds;
	private List<Integer> entryNumbers;
	private List<Integer> gids;
	private String germplasmUUID;
	private String groupId;
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

	private Map<Integer, Object> namesFilters;
	private Map<Integer, Object> descriptorsFilters;
	private Map<Integer, Object> variablesFilters;

	public List<Integer> getEntryNumbers() {
		return this.entryNumbers;
	}

	public void setEntryNumbers(final List<Integer> entryNumbers) {
		this.entryNumbers = entryNumbers;
	}

	public List<Integer> getGids() {
		return this.gids;
	}

	public void setGids(final List<Integer> gids) {
		this.gids = gids;
	}

	public String getGermplasmUUID() {
		return this.germplasmUUID;
	}

	public void setGermplasmUUID(final String germplasmUUID) {
		this.germplasmUUID = germplasmUUID;
	}

	public String getGroupId() {
		return this.groupId;
	}

	public void setGroupId(final String groupId) {
		this.groupId = groupId;
	}

	public SqlTextFilter getDesignationFilter() {
		return this.designationFilter;
	}

	public void setDesignationFilter(final SqlTextFilter designationFilter) {
		this.designationFilter = designationFilter;
	}

	public SqlTextFilter getImmediateSourceName() {
		return this.immediateSourceName;
	}

	public void setImmediateSourceName(final SqlTextFilter immediateSourceName) {
		this.immediateSourceName = immediateSourceName;
	}

	public SqlTextFilter getGroupSourceName() {
		return this.groupSourceName;
	}

	public void setGroupSourceName(final SqlTextFilter groupSourceName) {
		this.groupSourceName = groupSourceName;
	}

	public SqlTextFilter getFemaleParentName() {
		return this.femaleParentName;
	}

	public void setFemaleParentName(final SqlTextFilter femaleParentName) {
		this.femaleParentName = femaleParentName;
	}

	public SqlTextFilter getMaleParentName() {
		return this.maleParentName;
	}

	public void setMaleParentName(final SqlTextFilter maleParentName) {
		this.maleParentName = maleParentName;
	}

	public String getBreedingMethodName() {
		return this.breedingMethodName;
	}

	public void setBreedingMethodName(final String breedingMethodName) {
		this.breedingMethodName = breedingMethodName;
	}

	public String getBreedingMethodAbbreviation() {
		return this.breedingMethodAbbreviation;
	}

	public void setBreedingMethodAbbreviation(final String breedingMethodAbbreviation) {
		this.breedingMethodAbbreviation = breedingMethodAbbreviation;
	}

	public String getBreedingMethodGroup() {
		return this.breedingMethodGroup;
	}

	public void setBreedingMethodGroup(final String breedingMethodGroup) {
		this.breedingMethodGroup = breedingMethodGroup;
	}

	public String getLocationName() {
		return this.locationName;
	}

	public void setLocationName(final String locationName) {
		this.locationName = locationName;
	}

	public String getLocationAbbreviation() {
		return this.locationAbbreviation;
	}

	public void setLocationAbbreviation(final String locationAbbreviation) {
		this.locationAbbreviation = locationAbbreviation;
	}

	public Date getGermplasmDateFrom() {
		return this.germplasmDateFrom;
	}

	public void setGermplasmDateFrom(final Date germplasmDateFrom) {
		this.germplasmDateFrom = germplasmDateFrom;
	}

	public Date getGermplasmDateTo() {
		return this.germplasmDateTo;
	}

	public void setGermplasmDateTo(final Date germplasmDateTo) {
		this.germplasmDateTo = germplasmDateTo;
	}

	public String getReference() {
		return this.reference;
	}

	public void setReference(final String reference) {
		this.reference = reference;
	}

	public Map<Integer, Object> getNamesFilters() {
		return this.namesFilters;
	}

	public void setNamesFilters(final Map<Integer, Object> namesFilters) {
		this.namesFilters = namesFilters;
	}

	public Map<Integer, Object> getDescriptorsFilters() {
		return this.descriptorsFilters;
	}

	public void setDescriptorsFilters(final Map<Integer, Object> descriptorsFilters) {
		this.descriptorsFilters = descriptorsFilters;
	}

	public Map<Integer, Object> getVariablesFilters() {
		return this.variablesFilters;
	}

	public void setVariablesFilters(final Map<Integer, Object> variablesFilters) {
		this.variablesFilters = variablesFilters;
	}

	public List<Integer> getListDataIds() {
		return this.listDataIds;
	}

	public void setListDataIds(final List<Integer> listDataIds) {
		this.listDataIds = listDataIds;
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
