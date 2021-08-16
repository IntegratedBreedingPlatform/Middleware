package org.generationcp.middleware.domain.search_request.brapi.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.domain.search_request.SearchRequestDto;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;

@AutoProperty
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GermplasmSearchRequestDto extends SearchRequestDto {

	private int page;
	private int pageSize;

	@JsonIgnore
	private String preferredName;

	private List<String> commonCropNames;
	private List<String> germplasmDbIds;
	private List<String> germplasmNames;
	private List<String> studyDbIds;
	private List<String> studyNames;
	private List<String> externalReferenceIDs;
	private List<String> externalReferenceSources;
	private List<String> accessionNumbers;
	private List<String> collections;
	private List<String> genus;
	private List<String> germplasmPUIs;
	private List<String> parentDbIds;
	private List<String> progenyDbIds;
	private List<String> species;
	private List<String> synonyms;

	public GermplasmSearchRequestDto() {
		this.commonCropNames = Lists.newArrayList();
		this.germplasmDbIds = Lists.newArrayList();
		this.germplasmNames = Lists.newArrayList();
		this.studyDbIds = Lists.newArrayList();
		this.studyNames = Lists.newArrayList();
		this.externalReferenceIDs = Lists.newArrayList();
		this.externalReferenceSources = Lists.newArrayList();
		this.accessionNumbers = Lists.newArrayList();
		this.collections = Lists.newArrayList();
		this.genus = Lists.newArrayList();
		this.germplasmPUIs = Lists.newArrayList();
		this.parentDbIds = Lists.newArrayList();
		this.progenyDbIds = Lists.newArrayList();
		this.species = Lists.newArrayList();
		this.synonyms = Lists.newArrayList();
	}

	public List<String> getStudyNames() {
		return this.studyNames;
	}

	public void setStudyNames(final List<String> studyNames) {
		this.studyNames = studyNames;
	}

	public int getPage() {
		return this.page;
	}

	public void setPage(final int page) {
		this.page = page;
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public void setPageSize(final int pageSize) {
		this.pageSize = pageSize;
	}

	public String getPreferredName() {
		return this.preferredName;
	}

	public void setPreferredName(final String preferredName) {
		this.preferredName = preferredName;
	}

	public List<String> getAccessionNumbers() {
		return this.accessionNumbers;
	}

	public void setAccessionNumbers(final List<String> accessionNumbers) {
		this.accessionNumbers = accessionNumbers;
	}

	public List<String> getCommonCropNames() {
		return this.commonCropNames;
	}

	public void setCommonCropNames(final List<String> commonCropNames) {
		this.commonCropNames = commonCropNames;
	}

	public List<String> getGermplasmDbIds() {
		return this.germplasmDbIds;
	}

	public void setGermplasmDbIds(final List<String> germplasmDbIds) {
		this.germplasmDbIds = germplasmDbIds;
	}

	public List<String> getGenus() {
		return this.genus;
	}

	public void setGenus(final List<String> genus) {
		this.genus = genus;
	}

	public List<String> getGermplasmNames() {
		return this.germplasmNames;
	}

	public void setGermplasmNames(final List<String> germplasmNames) {
		this.germplasmNames = germplasmNames;
	}

	public List<String> getGermplasmPUIs() {
		return this.germplasmPUIs;
	}

	public void setGermplasmPUIs(final List<String> germplasmPUIs) {
		this.germplasmPUIs = germplasmPUIs;
	}

	public List<String> getStudyDbIds() {
		return this.studyDbIds;
	}

	public void setStudyDbIds(final List<String> studyDbIds) {
		this.studyDbIds = studyDbIds;
	}

	public List<String> getParentDbIds() {
		return this.parentDbIds;
	}

	public void setParentDbIds(final List<String> parentDbIds) {
		this.parentDbIds = parentDbIds;
	}

	public List<String> getProgenyDbIds() {
		return this.progenyDbIds;
	}

	public void setProgenyDbIds(final List<String> progenyDbIds) {
		this.progenyDbIds = progenyDbIds;
	}

	public List<String> getExternalReferenceIDs() {
		return this.externalReferenceIDs;
	}

	public void setExternalReferenceIDs(final List<String> externalReferenceIDs) {
		this.externalReferenceIDs = externalReferenceIDs;
	}

	public List<String> getExternalReferenceSources() {
		return this.externalReferenceSources;
	}

	public void setExternalReferenceSources(final List<String> externalReferenceSources) {
		this.externalReferenceSources = externalReferenceSources;
	}

	public List<String> getSynonyms() {
		return this.synonyms;
	}

	public void setSynonyms(final List<String> synonyms) {
		this.synonyms = synonyms;
	}

	public List<String> getSpecies() {
		return this.species;
	}

	public void setSpecies(final List<String> species) {
		this.species = species;
	}

	public List<String> getCollections() {
		return this.collections;
	}

	public void setCollections(final List<String> collections) {
		this.collections = collections;
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

	public boolean noFiltersSpecified() {
		return StringUtils.isEmpty(this.preferredName) &&
			CollectionUtils.isEmpty(this.commonCropNames) &&
			CollectionUtils.isEmpty(this.germplasmDbIds) &&
			CollectionUtils.isEmpty(this.germplasmNames) &&
			CollectionUtils.isEmpty(this.studyDbIds) &&
			CollectionUtils.isEmpty(this.studyNames) &&
			CollectionUtils.isEmpty(this.externalReferenceIDs) &&
			CollectionUtils.isEmpty(this.externalReferenceSources) &&
			CollectionUtils.isEmpty(this.accessionNumbers) &&
			CollectionUtils.isEmpty(this.collections) &&
			CollectionUtils.isEmpty(this.genus) &&
			CollectionUtils.isEmpty(this.germplasmPUIs) &&
			CollectionUtils.isEmpty(this.parentDbIds) &&
			CollectionUtils.isEmpty(this.progenyDbIds) &&
			CollectionUtils.isEmpty(this.species) &&
			CollectionUtils.isEmpty(this.synonyms);
	}
}
