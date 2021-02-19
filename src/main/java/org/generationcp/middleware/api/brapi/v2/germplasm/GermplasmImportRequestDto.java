package org.generationcp.middleware.api.brapi.v2.germplasm;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Date;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@AutoProperty
public class GermplasmImportRequestDto {

	private String accessionNumber;
	private Date acquisitionDate;
	private Map<String, String> additionalInfo;
	private Integer biologicalStatusOfAccessionCode;
	private Integer biologicalStatusOfAccessionDescription;
	private String breedingMethodDbId;
	private String germplasmDbId;
	private String collection;
	private String commonCropName;
	private String countryOfOriginCode;
	private String defaultDisplayName;
	private String documentationURL;
	// TODO Donors
	// TODO external references
	private String genus;
	private String germplasmName;
	private String germplasmOrigin;
	private String germplasmPUI;
	private String germplasmPreprocessing;
	private String instituteCode;
	private String instituteName;
	private String pedigree;
	private String seedSource;
	private String seedSourceDescription;
	private String species;
	private String speciesAuthority;
	private String subtaxa;
	private String subtaxaAuthority;
	private List<Synonym> synonyms;

	public String getAccessionNumber() {
		return accessionNumber;
	}

	public void setAccessionNumber(final String accessionNumber) {
		this.accessionNumber = accessionNumber;
	}

	public Date getAcquisitionDate() {
		return acquisitionDate;
	}

	public void setAcquisitionDate(final Date acquisitionDate) {
		this.acquisitionDate = acquisitionDate;
	}

	public Map<String, String> getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(final Map<String, String> additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public Integer getBiologicalStatusOfAccessionCode() {
		return biologicalStatusOfAccessionCode;
	}

	public void setBiologicalStatusOfAccessionCode(final Integer biologicalStatusOfAccessionCode) {
		this.biologicalStatusOfAccessionCode = biologicalStatusOfAccessionCode;
	}

	public Integer getBiologicalStatusOfAccessionDescription() {
		return biologicalStatusOfAccessionDescription;
	}

	public void setBiologicalStatusOfAccessionDescription(final Integer biologicalStatusOfAccessionDescription) {
		this.biologicalStatusOfAccessionDescription = biologicalStatusOfAccessionDescription;
	}

	public String getBreedingMethodDbId() {
		return breedingMethodDbId;
	}

	public void setBreedingMethodDbId(final String breedingMethodDbId) {
		this.breedingMethodDbId = breedingMethodDbId;
	}

	public String getGermplasmDbId() {
		return germplasmDbId;
	}

	public void setGermplasmDbId(final String germplasmDbId) {
		this.germplasmDbId = germplasmDbId;
	}

	public String getCollection() {
		return collection;
	}

	public void setCollection(final String collection) {
		this.collection = collection;
	}

	public String getCommonCropName() {
		return commonCropName;
	}

	public void setCommonCropName(final String commonCropName) {
		this.commonCropName = commonCropName;
	}

	public String getCountryOfOriginCode() {
		return countryOfOriginCode;
	}

	public void setCountryOfOriginCode(final String countryOfOriginCode) {
		this.countryOfOriginCode = countryOfOriginCode;
	}

	public String getDefaultDisplayName() {
		return defaultDisplayName;
	}

	public void setDefaultDisplayName(final String defaultDisplayName) {
		this.defaultDisplayName = defaultDisplayName;
	}

	public String getDocumentationURL() {
		return documentationURL;
	}

	public void setDocumentationURL(final String documentationURL) {
		this.documentationURL = documentationURL;
	}

	public String getGenus() {
		return genus;
	}

	public void setGenus(final String genus) {
		this.genus = genus;
	}

	public String getGermplasmName() {
		return germplasmName;
	}

	public void setGermplasmName(final String germplasmName) {
		this.germplasmName = germplasmName;
	}

	public String getGermplasmOrigin() {
		return germplasmOrigin;
	}

	public void setGermplasmOrigin(final String germplasmOrigin) {
		this.germplasmOrigin = germplasmOrigin;
	}

	public String getGermplasmPUI() {
		return germplasmPUI;
	}

	public void setGermplasmPUI(final String germplasmPUI) {
		this.germplasmPUI = germplasmPUI;
	}

	public String getGermplasmPreprocessing() {
		return germplasmPreprocessing;
	}

	public void setGermplasmPreprocessing(final String germplasmPreprocessing) {
		this.germplasmPreprocessing = germplasmPreprocessing;
	}

	public String getInstituteCode() {
		return instituteCode;
	}

	public void setInstituteCode(final String instituteCode) {
		this.instituteCode = instituteCode;
	}

	public String getInstituteName() {
		return instituteName;
	}

	public void setInstituteName(final String instituteName) {
		this.instituteName = instituteName;
	}

	public String getPedigree() {
		return pedigree;
	}

	public void setPedigree(final String pedigree) {
		this.pedigree = pedigree;
	}

	public String getSeedSource() {
		return seedSource;
	}

	public void setSeedSource(final String seedSource) {
		this.seedSource = seedSource;
	}

	public String getSeedSourceDescription() {
		return seedSourceDescription;
	}

	public void setSeedSourceDescription(final String seedSourceDescription) {
		this.seedSourceDescription = seedSourceDescription;
	}

	public String getSpecies() {
		return species;
	}

	public void setSpecies(final String species) {
		this.species = species;
	}

	public String getSpeciesAuthority() {
		return speciesAuthority;
	}

	public void setSpeciesAuthority(final String speciesAuthority) {
		this.speciesAuthority = speciesAuthority;
	}

	public String getSubtaxa() {
		return subtaxa;
	}

	public void setSubtaxa(final String subtaxa) {
		this.subtaxa = subtaxa;
	}

	public String getSubtaxaAuthority() {
		return subtaxaAuthority;
	}

	public void setSubtaxaAuthority(final String subtaxaAuthority) {
		this.subtaxaAuthority = subtaxaAuthority;
	}

	public List<Synonym> getSynonyms() {
		return synonyms;
	}

	public void setSynonyms(final List<Synonym> synonyms) {
		this.synonyms = synonyms;
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
	public boolean equals(Object o) {
		return Pojomatic.equals(this, o);
	}

}
