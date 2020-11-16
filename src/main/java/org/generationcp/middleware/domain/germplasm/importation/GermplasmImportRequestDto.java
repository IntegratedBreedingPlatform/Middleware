package org.generationcp.middleware.domain.germplasm.importation;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Map;

@AutoProperty
@JsonPropertyOrder({
	"clientId", "germplasmUUID", "locationAbbr", "breedingMethodAbbr", "reference", "preferredName", "names", "attributes", "creationDate"})
public class GermplasmImportRequestDto {

	private Integer clientId;

	private String germplasmUUID;

	private String locationAbbr;

	private String breedingMethodAbbr;

	private String reference;

	private String preferredName;

	private Map<String, String> names;

	private Map<String, String> attributes;

	private String creationDate;

	public GermplasmImportRequestDto() {
	}

	public GermplasmImportRequestDto(final Integer clientId, final String germplasmUUID, final String locationAbbr,
		final String breedingMethodAbbr,
		final String reference,
		final String preferredName, final Map<String, String> names, final Map<String, String> attributes, final String creationDate) {
		this.clientId = clientId;
		this.germplasmUUID = germplasmUUID;
		this.locationAbbr = locationAbbr;
		this.breedingMethodAbbr = breedingMethodAbbr;
		this.reference = reference;
		this.preferredName = preferredName;
		this.names = names;
		this.attributes = attributes;
		this.creationDate = creationDate;
	}

	public Integer getClientId() {
		return clientId;
	}

	public void setClientId(final Integer clientId) {
		this.clientId = clientId;
	}

	public String getLocationAbbr() {
		return locationAbbr;
	}

	public void setLocationAbbr(final String locationAbbr) {
		this.locationAbbr = locationAbbr;
	}

	public String getBreedingMethodAbbr() {
		return breedingMethodAbbr;
	}

	public void setBreedingMethodAbbr(final String breedingMethodAbbr) {
		this.breedingMethodAbbr = breedingMethodAbbr;
	}

	public String getGermplasmUUID() {
		return germplasmUUID;
	}

	public void setGermplasmUUID(final String germplasmUUID) {
		this.germplasmUUID = germplasmUUID;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(final String reference) {
		this.reference = reference;
	}

	public String getPreferredName() {
		return preferredName;
	}

	public void setPreferredName(final String preferredName) {
		this.preferredName = preferredName;
	}

	public Map<String, String> getNames() {
		return names;
	}

	public void setNames(final Map<String, String> names) {
		this.names = names;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(final Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(final String creationDate) {
		this.creationDate = creationDate;
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
