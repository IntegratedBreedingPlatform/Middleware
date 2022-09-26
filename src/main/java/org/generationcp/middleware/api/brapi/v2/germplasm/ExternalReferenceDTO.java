package org.generationcp.middleware.api.brapi.v2.germplasm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"referenceID", "referenceSource",})
public class ExternalReferenceDTO {

	@JsonIgnore
	private String entityId;

	private String referenceID;

	private String referenceSource;

	public ExternalReferenceDTO() {
	}

	public ExternalReferenceDTO(final String entityId, final String referenceID, final String referenceSource) {
		this.entityId = entityId;
		this.referenceID = referenceID;
		this.referenceSource = referenceSource;
	}

	public String getReferenceID() {
		return this.referenceID;
	}

	public void setReferenceID(final String referenceID) {
		this.referenceID = referenceID;
	}

	public String getReferenceSource() {
		return this.referenceSource;
	}

	public void setReferenceSource(final String referenceSource) {
		this.referenceSource = referenceSource;
	}

	public String getEntityId() {
		return this.entityId;
	}

	public void setEntityId(final String entityId) {
		this.entityId = entityId;
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
