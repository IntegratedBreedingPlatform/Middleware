package org.generationcp.middleware.api.brapi.v2.trial;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class Publication {

	private String publicationPUI;
	private String publicationReference;

	public String getPublicationPUI() {
		return this.publicationPUI;
	}

	public void setPublicationPUI(final String publicationPUI) {
		this.publicationPUI = publicationPUI;
	}

	public String getPublicationReference() {
		return this.publicationReference;
	}

	public void setPublicationReference(final String publicationReference) {
		this.publicationReference = publicationReference;
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}
}
