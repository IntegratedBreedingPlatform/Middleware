package org.generationcp.middleware.domain.germplasm;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Date;
import java.util.List;
import java.util.Map;

@AutoProperty
public class GermplasmImportRequestDto {

	@AutoProperty
	@JsonPropertyOrder({
		"clientId", "guid", "locationAbbr", "breedingMethodAbbr", "reference", "preferredName", "names", "attributes", "creationDate"})
	public static class GermplasmDto {

		private Integer clientId;

		private String guid;

		private String locationAbbr;

		private String breedingMethodAbbr;

		private String reference;

		private String preferredName;

		private Map<String, String> names;

		private Map<String, String> attributes;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
		private Date creationDate;

		public GermplasmDto() {
		}

		public GermplasmDto(final Integer clientId, final String guid, final String locationAbbr, final String breedingMethodAbbr,
			final String reference,
			final String preferredName, final Map<String, String> names, final Map<String, String> attributes, final Date creationDate) {
			this.clientId = clientId;
			this.guid = guid;
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

		public String getGuid() {
			return guid;
		}

		public void setGuid(final String guid) {
			this.guid = guid;
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

		public Date getCreationDate() {
			return creationDate;
		}

		public void setCreationDate(final Date creationDate) {
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


	private List<GermplasmDto> germplasmSet;

	public List<GermplasmDto> getGermplasmSet() {
		return germplasmSet;
	}

	public void setGermplasmSet(final List<GermplasmDto> germplasmSet) {
		this.germplasmSet = germplasmSet;
	}

	public GermplasmImportRequestDto() {
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
