
package org.generationcp.middleware.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.generationcp.middleware.pojos.Germplasm;

public class GermplasmGroup {

	private Integer founderGid;
	private Integer groupMgid;
	private List<Germplasm> groupMembers = new ArrayList<>();

	public Integer getFounderGid() {
		return this.founderGid;
	}

	public void setFounderGid(final Integer founderGid) {
		this.founderGid = founderGid;
	}

	public Integer getGroupMgid() {
		return this.groupMgid;
	}

	public void setGroupMgid(final Integer groupMgid) {
		this.groupMgid = groupMgid;
	}

	public List<Germplasm> getGroupMembers() {
		return this.groupMembers;
	}

	public void setGroupMembers(List<Germplasm> groupMembers) {
		this.groupMembers = groupMembers;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append(this.founderGid).append(this.groupMgid).append(this.groupMembers).toString();
	}
}
