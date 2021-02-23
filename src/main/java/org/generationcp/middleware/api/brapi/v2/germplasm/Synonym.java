package org.generationcp.middleware.api.brapi.v2.germplasm;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@AutoProperty
public class Synonym {

	private String synonym;
	private String type;

	public Synonym(final String synonym, final String type) {
		this.synonym = synonym;
		this.type = type;
	}

	public String getSynonym() {
		return synonym;
	}

	public void setSynonym(final String synonym) {
		this.synonym = synonym;
	}

	public String getType() {
		return type;
	}

	public void setType(final String type) {
		this.type = type;
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
