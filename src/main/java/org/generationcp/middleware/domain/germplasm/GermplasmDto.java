package org.generationcp.middleware.domain.germplasm;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;

@AutoProperty
public class GermplasmDto {

	private Integer gid;

	private String germplasmUUID;

	private String designation;

	private List<GermplasmNameDto> names;

	private String creationDate;

	private String reference;

	private List<GermplasmAttributeDto> attributes;

	private String breedingLocation;

	private String breedingMethod;

	private boolean isGroupedLine;

	private Integer groupId;

	public Integer getGid() {
		return gid;
	}

	public void setGid(final Integer gid) {
		this.gid = gid;
	}

	public String getGermplasmUUID() {
		return germplasmUUID;
	}

	public void setGermplasmUUID(final String germplasmUUID) {
		this.germplasmUUID = germplasmUUID;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(final String designation) {
		this.designation = designation;
	}

	public List<GermplasmNameDto> getNames() {
		return names;
	}

	public void setNames(final List<GermplasmNameDto> names) {
		this.names = names;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(final String creationDate) {
		this.creationDate = creationDate;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(final String reference) {
		this.reference = reference;
	}

	public List<GermplasmAttributeDto> getAttributes() {
		return attributes;
	}

	public void setAttributes(final List<GermplasmAttributeDto> attributes) {
		this.attributes = attributes;
	}

	public String getBreedingLocation() {
		return breedingLocation;
	}

	public void setBreedingLocation(final String breedingLocation) {
		this.breedingLocation = breedingLocation;
	}

	public String getBreedingMethod() {
		return breedingMethod;
	}

	public void setBreedingMethod(final String breedingMethod) {
		this.breedingMethod = breedingMethod;
	}

	public boolean isGroupedLine() {
		return isGroupedLine;
	}

	public void setGroupedLine(final boolean groupedLine) {
		isGroupedLine = groupedLine;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(final Integer groupId) {
		this.groupId = groupId;
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
