
package org.generationcp.middleware.service.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.generationcp.middleware.pojos.Germplasm;

public class GermplasmGroup {

	private Integer founderGid;
	private Integer groupId;
	private List<Germplasm> groupMembers = new ArrayList<>();

	public Integer getFounderGid() {
		return this.founderGid;
	}

	public void setFounderGid(final Integer founderGid) {
		this.founderGid = founderGid;
	}

	public Integer getGroupId() {
		return this.groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public List<Germplasm> getGroupMembers() {
		return this.groupMembers;
	}

	public void setGroupMembers(List<Germplasm> groupMembers) {
		this.groupMembers = groupMembers;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append(this.founderGid).append(this.groupId).append(this.groupMembers).toString();
	}
}
