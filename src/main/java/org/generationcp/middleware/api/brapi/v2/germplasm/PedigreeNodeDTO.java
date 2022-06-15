package org.generationcp.middleware.api.brapi.v2.germplasm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;
import java.util.Map;

@AutoProperty
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PedigreeNodeDTO {

	private Map<String, String> additionalInfo;
	private String breedingMethodDbId;
	private String breedingMethodName;
	private String crossingProjectDbId;
	private Integer crossingYear;
	private String defaultDisplayName;
	private List<ExternalReferenceDTO> externalReferences;
	private String familyCode;
	private String germplasmDbId;
	private String germplasmName;
	private String germplasmPUI;
	private String pedigreeString;
	private List<PedigreeNodeReferenceDTO> parents;
	private List<PedigreeNodeReferenceDTO> progeny;
	private List<PedigreeNodeReferenceDTO> siblings;

	@JsonIgnore
	private Integer gid;

	public Map<String, String> getAdditionalInfo() {
		return this.additionalInfo;
	}

	public void setAdditionalInfo(final Map<String, String> additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public String getBreedingMethodDbId() {
		return this.breedingMethodDbId;
	}

	public void setBreedingMethodDbId(final String breedingMethodDbId) {
		this.breedingMethodDbId = breedingMethodDbId;
	}

	public String getBreedingMethodName() {
		return this.breedingMethodName;
	}

	public void setBreedingMethodName(final String breedingMethodName) {
		this.breedingMethodName = breedingMethodName;
	}

	public String getCrossingProjectDbId() {
		return this.crossingProjectDbId;
	}

	public void setCrossingProjectDbId(final String crossingProjectDbId) {
		this.crossingProjectDbId = crossingProjectDbId;
	}

	public Integer getCrossingYear() {
		return this.crossingYear;
	}

	public void setCrossingYear(final Integer crossingYear) {
		this.crossingYear = crossingYear;
	}

	public String getDefaultDisplayName() {
		return this.defaultDisplayName;
	}

	public void setDefaultDisplayName(final String defaultDisplayName) {
		this.defaultDisplayName = defaultDisplayName;
	}

	public List<ExternalReferenceDTO> getExternalReferences() {
		return this.externalReferences;
	}

	public void setExternalReferences(final List<ExternalReferenceDTO> externalReferences) {
		this.externalReferences = externalReferences;
	}

	public String getFamilyCode() {
		return this.familyCode;
	}

	public void setFamilyCode(final String familyCode) {
		this.familyCode = familyCode;
	}

	public String getGermplasmDbId() {
		return this.germplasmDbId;
	}

	public void setGermplasmDbId(final String germplasmDbId) {
		this.germplasmDbId = germplasmDbId;
	}

	public String getGermplasmName() {
		return this.germplasmName;
	}

	public void setGermplasmName(final String germplasmName) {
		this.germplasmName = germplasmName;
	}

	public String getGermplasmPUI() {
		return this.germplasmPUI;
	}

	public void setGermplasmPUI(final String germplasmPUI) {
		this.germplasmPUI = germplasmPUI;
	}

	public String getPedigreeString() {
		return this.pedigreeString;
	}

	public void setPedigreeString(final String pedigreeString) {
		this.pedigreeString = pedigreeString;
	}

	public List<PedigreeNodeReferenceDTO> getParents() {
		return this.parents;
	}

	public void setParents(final List<PedigreeNodeReferenceDTO> parents) {
		this.parents = parents;
	}

	public List<PedigreeNodeReferenceDTO> getProgeny() {
		return this.progeny;
	}

	public void setProgeny(final List<PedigreeNodeReferenceDTO> progeny) {
		this.progeny = progeny;
	}

	public List<PedigreeNodeReferenceDTO> getSiblings() {
		return this.siblings;
	}

	public void setSiblings(final List<PedigreeNodeReferenceDTO> siblings) {
		this.siblings = siblings;
	}

	public Integer getGid() {
		return this.gid;
	}

	public void setGid(final Integer gid) {
		this.gid = gid;
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
