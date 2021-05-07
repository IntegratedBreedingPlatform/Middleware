package org.generationcp.middleware.api.germplasm.pedigree;

import org.generationcp.middleware.pojos.Germplasm;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;

@AutoProperty
public class GermplasmTreeNode {

	private Integer gid;

	private String preferredName;

	private GermplasmTreeNode femaleParentNode;

	private GermplasmTreeNode maleParentNode;

	private List<GermplasmTreeNode> otherProgenitors;

	public GermplasmTreeNode(final Integer gid, final String preferredName) {
		this.gid = gid;
		this.preferredName = preferredName;
	}

	public GermplasmTreeNode(final Germplasm germplasm) {
		this.gid = germplasm.getGid();
		this.preferredName = germplasm.getPreferredName().getNval();
	}

	public Integer getGid() {
		return gid;
	}

	public void setGid(final Integer gid) {
		this.gid = gid;
	}

	public String getPreferredName() {
		return preferredName;
	}

	public void setPreferredName(final String preferredName) {
		this.preferredName = preferredName;
	}

	public GermplasmTreeNode getFemaleParentNode() {
		return femaleParentNode;
	}

	public void setFemaleParentNode(final GermplasmTreeNode femaleParentNode) {
		this.femaleParentNode = femaleParentNode;
	}

	public GermplasmTreeNode getMaleParentNode() {
		return maleParentNode;
	}

	public void setMaleParentNode(final GermplasmTreeNode maleParentNode) {
		this.maleParentNode = maleParentNode;
	}

	public List<GermplasmTreeNode> getOtherProgenitors() {
		return otherProgenitors;
	}

	public void setOtherProgenitors(final List<GermplasmTreeNode> otherProgenitors) {
		this.otherProgenitors = otherProgenitors;
	}
}
