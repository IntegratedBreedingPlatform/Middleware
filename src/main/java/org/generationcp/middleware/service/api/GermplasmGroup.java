
package org.generationcp.middleware.service.api;

import org.generationcp.middleware.pojos.Germplasm;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.ArrayList;
import java.util.List;

@AutoProperty
public class GermplasmGroup {

	private Integer founderGid;
	private Integer groupId;
	private boolean isGenerative;
	private List<GermplasmGroupMember> groupMembers = new ArrayList<>();

	public Integer getFounderGid() {
		return founderGid;
	}

	public void setFounderGid(final Integer founderGid) {
		this.founderGid = founderGid;
	}

	public Integer getGroupId() {
		return this.groupId;
	}

	public void setGroupId(final Integer groupId) {
		this.groupId = groupId;
	}

	public List<GermplasmGroupMember> getGroupMembers() {
		return this.groupMembers;
	}

	public void setGroupMembers(final List<GermplasmGroupMember> groupMembers) {
		this.groupMembers = groupMembers;
	}

	public void addGroupMember(final Germplasm germplasm) {
		this.groupMembers.add(new GermplasmGroupMember(germplasm));
	}

	public boolean isGenerative() {
		return isGenerative;
	}

	public void setGenerative(final boolean generative) {
		isGenerative = generative;
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
