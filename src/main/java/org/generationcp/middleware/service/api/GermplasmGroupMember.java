package org.generationcp.middleware.service.api;

import org.generationcp.middleware.pojos.Germplasm;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class GermplasmGroupMember {

	private Integer gid;
	private String preferredName;
	private boolean isGenerative;

	public GermplasmGroupMember() {

	}

	public GermplasmGroupMember(final Integer gid, final String preferredName, final boolean isGenerative) {
		this.gid = gid;
		this.preferredName = preferredName;
		this.isGenerative = isGenerative;
	}

	public GermplasmGroupMember(final Germplasm germplasm) {
		this.gid = germplasm.getGid();
		this.preferredName = germplasm.getPreferredName().getNval();
		this.isGenerative = germplasm.getMethod().isGenerative();
	}

	public Integer getGid() {
		return this.gid;
	}

	public void setGid(final Integer gid) {
		this.gid = gid;
	}

	public String getPreferredName() {
		return this.preferredName;
	}

	public void setPreferredName(final String preferredName) {
		this.preferredName = preferredName;
	}

	public boolean isGenerative() {
		return isGenerative;
	}

	public void setGenerative(final boolean generative) {
		this.isGenerative = generative;
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}
}
