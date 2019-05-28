package org.generationcp.middleware.domain.germplasm;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Date;

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
	private final static String documentationURL = null;

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
		this.instituteName = instituteCode;
	}

	public String getInstituteName() {
		return this.instituteName;
	}

	public void setInstituteName(final String instituteName) {
		this.instituteCode = instituteName;
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
