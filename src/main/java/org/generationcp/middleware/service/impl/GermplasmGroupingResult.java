
package org.generationcp.middleware.service.impl;

import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class GermplasmGroupingResult {

	private Integer founderGid;
	private Integer groupMgid;
	private final Set<Integer> groupMemberGids = new TreeSet<>();

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

	public Set<Integer> getGroupMemberGids() {
		return this.groupMemberGids;
	}

	public void addGroupMemberGid(final Integer memberGid) {
		if (memberGid != null) {
			this.groupMemberGids.add(memberGid);
		}
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append(this.founderGid).append(this.groupMgid).append(this.groupMemberGids).toString();
	}
}
