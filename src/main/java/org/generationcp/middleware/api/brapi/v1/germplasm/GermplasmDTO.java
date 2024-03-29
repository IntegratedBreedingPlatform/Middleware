package org.generationcp.middleware.api.brapi.v1.germplasm;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.api.brapi.v2.germplasm.Synonym;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Date;
import java.util.List;
import java.util.Map;

@AutoProperty
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GermplasmDTO {

	private String germplasmDbId;
	private String defaultDisplayName;
	private String accessionNumber;
	private String germplasmName;
	private String germplasmPUI;
	private String pedigree;
	private String germplasmSeedSource;
	private String commonCropName;
	private String instituteCode;
	private String instituteName;
	private Integer biologicalStatusOfAccessionCode;
	private String countryOfOriginCode;
	private String genus;
	private String species;
	private String speciesAuthority;
	private String subtaxa;
	private String subtaxaAuthority;
	private Date acquisitionDate;
	private String breedingMethodDbId;
	private String germplasmGenus;
	private String germplasmSpecies;
	private String seedSource;
	private String entryNumber;
	private String germplasmOrigin;
	private List<Synonym> synonyms;
	private Map<String, String> additionalInfo;
	private String gid;
	private List<ExternalReferenceDTO> externalReferences;
	private String documentationURL;

	public String getBreedingMethodDbId() {
		return this.breedingMethodDbId;
	}

	public void setBreedingMethodDbId(final String breedingMethodDbId) {
		this.breedingMethodDbId = breedingMethodDbId;
	}

	public String getGermplasmGenus() {
		return this.germplasmGenus;
	}

	public void setGermplasmGenus(final String germplasmGenus) {
		this.germplasmGenus = germplasmGenus;
	}

	public String getGermplasmSpecies() {
		return this.germplasmSpecies;
	}

	public void setGermplasmSpecies(final String germplasmSpecies) {
		this.germplasmSpecies = germplasmSpecies;
	}

	public String getSeedSource() {
		return this.seedSource;
	}

	public void setSeedSource(final String seedSource) {
		this.seedSource = seedSource;
	}

	public String getDocumentationURL() {
		return this.documentationURL;
	}

	public void setDocumentationURL(final String documentationURL) {
		this.documentationURL = documentationURL;
	}

	public String getGermplasmDbId() {
		return this.germplasmDbId;
	}

	public void setGermplasmDbId(final String germplasmDbId) {
		this.germplasmDbId = germplasmDbId;
	}

	public String getDefaultDisplayName() {
		return this.defaultDisplayName;
	}

	public void setDefaultDisplayName(final String defaultDisplayName) {
		this.defaultDisplayName = defaultDisplayName;
		this.germplasmName = defaultDisplayName;
	}

	public String getAccessionNumber() {
		return this.accessionNumber;
	}

	public void setAccessionNumber(final String accessionNumber) {
		this.accessionNumber = accessionNumber;
	}

	public String getGermplasmName() {
		return this.germplasmName;
	}

	public void setGermplasmName(final String germplasmName) {
		this.germplasmName = germplasmName;
		this.defaultDisplayName = germplasmName;
	}

	public String getGermplasmPUI() {
		return this.germplasmPUI;
	}

	public void setGermplasmPUI(final String germplasmPUI) {
		this.germplasmPUI = germplasmPUI;
	}

	public String getPedigree() {
		return this.pedigree;
	}

	public void setPedigree(final String pedigree) {
		this.pedigree = pedigree;
	}

	public String getGermplasmSeedSource() {
		return this.germplasmSeedSource;
	}

	public void setGermplasmSeedSource(final String germplasmSeedSource) {
		this.germplasmSeedSource = germplasmSeedSource;
		this.seedSource = germplasmSeedSource;
	}

	public String getCommonCropName() {
		return this.commonCropName;
	}

	public void setCommonCropName(final String commonCropName) {
		this.commonCropName = commonCropName;
	}

	public String getInstituteCode() {
		return this.instituteCode;
	}

	public void setInstituteCode(final String instituteCode) {
		this.instituteCode = instituteCode;
	}

	public String getInstituteName() {
		return this.instituteName;
	}

	public void setInstituteName(final String instituteName) {
		this.instituteName = instituteName;
	}

	public Integer getBiologicalStatusOfAccessionCode() {
		return this.biologicalStatusOfAccessionCode;
	}

	public void setBiologicalStatusOfAccessionCode(final Integer biologicalStatusOfAccessionCode) {
		this.biologicalStatusOfAccessionCode = biologicalStatusOfAccessionCode;
	}

	public String getCountryOfOriginCode() {
		return this.countryOfOriginCode;
	}

	public void setCountryOfOriginCode(final String countryOfOriginCode) {
		this.countryOfOriginCode = countryOfOriginCode;
	}

	public String getGenus() {
		return this.genus;
	}

	public void setGenus(final String genus) {
		this.genus = genus;
		this.germplasmGenus = genus;
	}

	public String getSpecies() {
		return this.species;
	}

	public void setSpecies(final String species) {
		this.species = species;
		this.germplasmSpecies = species;
	}

	public String getSpeciesAuthority() {
		return this.speciesAuthority;
	}

	public void setSpeciesAuthority(final String speciesAuthority) {
		this.speciesAuthority = speciesAuthority;
	}

	public String getSubtaxa() {
		return this.subtaxa;
	}

	public void setSubtaxa(final String subtaxa) {
		this.subtaxa = subtaxa;
	}

	public String getSubtaxaAuthority() {
		return this.subtaxaAuthority;
	}

	public void setSubtaxaAuthority(final String subtaxaAuthority) {
		this.subtaxaAuthority = subtaxaAuthority;
	}

	public Date getAcquisitionDate() {
		return this.acquisitionDate;
	}

	public void setAcquisitionDate(final Date acquisitionDate) {
		this.acquisitionDate = acquisitionDate;
	}

	public String getEntryNumber() {
		return this.entryNumber;
	}

	public void setEntryNumber(final String entryNumber) {
		this.entryNumber = entryNumber;
	}

	public String getGermplasmOrigin() {
		return this.germplasmOrigin;
	}

	public void setGermplasmOrigin(final String germplasmOrigin) {
		this.germplasmOrigin = germplasmOrigin;
	}

	public List<Synonym> getSynonyms() {
		return this.synonyms;
	}

	public void setSynonyms(final List<Synonym> synonyms) {
		this.synonyms = synonyms;
	}

	public Map<String, String> getAdditionalInfo() {
		return this.additionalInfo;
	}

	public void setAdditionalInfo(final Map<String, String> additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public String getGid() {
		return this.gid;
	}

	public void setGid(final String gid) {
		this.gid = gid;
	}

	public List<ExternalReferenceDTO> getExternalReferences() {
		return this.externalReferences;
	}

	public void setExternalReferences(final List<ExternalReferenceDTO> externalReferences) {
		this.externalReferences = externalReferences;
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
