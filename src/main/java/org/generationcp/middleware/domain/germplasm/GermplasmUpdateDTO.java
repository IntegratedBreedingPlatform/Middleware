package org.generationcp.middleware.domain.germplasm;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.HashMap;
import java.util.Map;

@AutoProperty
public class GermplasmUpdateDTO {

	private Integer gid;
	private String germplasmUUID;
	private String preferredNameType;
	private String locationAbbreviation;
	private String creationDate;
	private String breedingMethodAbbr;
	private String reference;
	private Map<String, String> names = new HashMap<>();
	private Map<String, String> attributes = new HashMap<>();
	private Map<String, String> progenitors = new HashMap<>();

	public Integer getGid() {
		return this.gid;
	}

	public void setGid(final Integer gid) {
		this.gid = gid;
	}

	public String getGermplasmUUID() {
		return this.germplasmUUID;
	}

	public void setGermplasmUUID(final String germplasmUUID) {
		this.germplasmUUID = germplasmUUID;
	}

	public String getPreferredNameType() {
		return this.preferredNameType;
	}

	public void setPreferredNameType(final String preferredNameType) {
		this.preferredNameType = preferredNameType;
	}

	public String getLocationAbbreviation() {
		return this.locationAbbreviation;
	}

	public void setLocationAbbreviation(final String locationAbbreviation) {
		this.locationAbbreviation = locationAbbreviation;
	}

	public String getCreationDate() {
		return this.creationDate;
	}

	public void setCreationDate(final String creationDate) {
		this.creationDate = creationDate;
	}

	public String getBreedingMethodAbbr() {
		return this.breedingMethodAbbr;
	}

	public void setBreedingMethodAbbr(final String breedingMethodAbbr) {
		this.breedingMethodAbbr = breedingMethodAbbr;
	}

	public String getReference() {
		return this.reference;
	}

	public void setReference(final String reference) {
		this.reference = reference;
	}

	public Map<String, String> getNames() {
		return this.names;
	}

	public void setNames(final Map<String, String> names) {
		this.names = names;
	}

	public Map<String, String> getAttributes() {
		return this.attributes;
	}

	public void setAttributes(final Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public Map<String, String> getProgenitors() {
		return this.progenitors;
	}

	public void setProgenitors(final Map<String, String> progenitors) {
		this.progenitors = progenitors;
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
