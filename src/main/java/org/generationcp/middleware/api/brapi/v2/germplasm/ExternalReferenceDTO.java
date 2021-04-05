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
	private String gid;

	private String referenceID;

	private String referenceSource;

	public ExternalReferenceDTO() {
	}

	public String getReferenceID() {
		return referenceID;
	}

	public void setReferenceID(final String referenceID) {
		this.referenceID = referenceID;
	}

	public String getReferenceSource() {
		return referenceSource;
	}

	public void setReferenceSource(final String referenceSource) {
		this.referenceSource = referenceSource;
	}

	public String getGid() {
		return gid;
	}

	public void setGid(final String gid) {
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
