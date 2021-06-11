package org.generationcp.middleware.service.api;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class GermplasmGroupMember {

	private Integer gid;
	private String preferredName;

	public GermplasmGroupMember() {

	}

	public GermplasmGroupMember(final Integer gid, final String preferredName) {
		this.gid = gid;
		this.preferredName = preferredName;
	}

	public GermplasmGroupMember(final Germplasm germplasm) {
		this.gid = germplasm.getGid();
		final Name preferredName = germplasm.getPreferredName();
		if (preferredName != null) {
			this.preferredName = preferredName.getNval();
		}
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
