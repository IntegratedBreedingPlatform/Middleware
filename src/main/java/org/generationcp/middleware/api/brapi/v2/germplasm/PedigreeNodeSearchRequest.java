package org.generationcp.middleware.api.brapi.v2.germplasm;

import java.util.List;

public class PedigreeNodeSearchRequest {

	private List<String> accessionNumbers;
	private List<String> binomialNames;
	private List<String> collections;
	private List<String> commonCropNames;
	private List<String> externalReferenceIds;
	private List<String> externalReferenceSources;
	private List<String> familyCodes;
	private List<String> genus;
	private List<String> germplasmDbIds;
	private List<String> germplasmNames;
	private List<String> germplasmPUIs;
	private boolean includeFullTree;
	private boolean includeParents;
	private boolean includeProgeny;
	private boolean includeSiblings;
	private List<String> instituteCodes;
	private int page;
	private int pageSize;
	private int pedigreeDepth;
	private int progenyDepth;
	private List<String> programDbIds;
	private List<String> programNames;
	private List<String> species;
	private List<String> studyDbIds;
	private List<String> studyNames;
	private List<String> synonyms;
	private List<String> trialDbIds;
	private List<String> trialNames;

	public List<String> getAccessionNumbers() {
		return this.accessionNumbers;
	}

	public void setAccessionNumbers(final List<String> accessionNumbers) {
		this.accessionNumbers = accessionNumbers;
	}

	public List<String> getBinomialNames() {
		return this.binomialNames;
	}

	public void setBinomialNames(final List<String> binomialNames) {
		this.binomialNames = binomialNames;
	}

	public List<String> getCollections() {
		return this.collections;
	}

	public void setCollections(final List<String> collections) {
		this.collections = collections;
	}

	public List<String> getCommonCropNames() {
		return this.commonCropNames;
	}

	public void setCommonCropNames(final List<String> commonCropNames) {
		this.commonCropNames = commonCropNames;
	}

	public List<String> getExternalReferenceIds() {
		return this.externalReferenceIds;
	}

	public void setExternalReferenceIds(final List<String> externalReferenceIds) {
		this.externalReferenceIds = externalReferenceIds;
	}

	public List<String> getExternalReferenceSources() {
		return this.externalReferenceSources;
	}

	public void setExternalReferenceSources(final List<String> externalReferenceSources) {
		this.externalReferenceSources = externalReferenceSources;
	}

	public List<String> getFamilyCodes() {
		return this.familyCodes;
	}

	public void setFamilyCodes(final List<String> familyCodes) {
		this.familyCodes = familyCodes;
	}

	public List<String> getGenus() {
		return this.genus;
	}

	public void setGenus(final List<String> genus) {
		this.genus = genus;
	}

	public List<String> getGermplasmDbIds() {
		return this.germplasmDbIds;
	}

	public void setGermplasmDbIds(final List<String> germplasmDbIds) {
		this.germplasmDbIds = germplasmDbIds;
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

	public boolean isIncludeFullTree() {
		return this.includeFullTree;
	}

	public void setIncludeFullTree(final boolean includeFullTree) {
		this.includeFullTree = includeFullTree;
	}

	public boolean isIncludeParents() {
		return this.includeParents;
	}

	public void setIncludeParents(final boolean includeParents) {
		this.includeParents = includeParents;
	}

	public boolean isIncludeProgeny() {
		return this.includeProgeny;
	}

	public void setIncludeProgeny(final boolean includeProgeny) {
		this.includeProgeny = includeProgeny;
	}

	public boolean isIncludeSiblings() {
		return this.includeSiblings;
	}

	public void setIncludeSiblings(final boolean includeSiblings) {
		this.includeSiblings = includeSiblings;
	}

	public List<String> getInstituteCodes() {
		return this.instituteCodes;
	}

	public void setInstituteCodes(final List<String> instituteCodes) {
		this.instituteCodes = instituteCodes;
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

	public int getPedigreeDepth() {
		return this.pedigreeDepth;
	}

	public void setPedigreeDepth(final int pedigreeDepth) {
		this.pedigreeDepth = pedigreeDepth;
	}

	public int getProgenyDepth() {
		return this.progenyDepth;
	}

	public void setProgenyDepth(final int progenyDepth) {
		this.progenyDepth = progenyDepth;
	}

	public List<String> getProgramDbIds() {
		return this.programDbIds;
	}

	public void setProgramDbIds(final List<String> programDbIds) {
		this.programDbIds = programDbIds;
	}

	public List<String> getProgramNames() {
		return this.programNames;
	}

	public void setProgramNames(final List<String> programNames) {
		this.programNames = programNames;
	}

	public List<String> getSpecies() {
		return this.species;
	}

	public void setSpecies(final List<String> species) {
		this.species = species;
	}

	public List<String> getStudyDbIds() {
		return this.studyDbIds;
	}

	public void setStudyDbIds(final List<String> studyDbIds) {
		this.studyDbIds = studyDbIds;
	}

	public List<String> getStudyNames() {
		return this.studyNames;
	}

	public void setStudyNames(final List<String> studyNames) {
		this.studyNames = studyNames;
	}

	public List<String> getSynonyms() {
		return this.synonyms;
	}

	public void setSynonyms(final List<String> synonyms) {
		this.synonyms = synonyms;
	}

	public List<String> getTrialDbIds() {
		return this.trialDbIds;
	}

	public void setTrialDbIds(final List<String> trialDbIds) {
		this.trialDbIds = trialDbIds;
	}

	public List<String> getTrialNames() {
		return this.trialNames;
	}

	public void setTrialNames(final List<String> trialNames) {
		this.trialNames = trialNames;
	}

}
