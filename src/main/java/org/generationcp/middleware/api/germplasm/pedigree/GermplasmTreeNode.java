package org.generationcp.middleware.api.germplasm.pedigree;

import org.generationcp.middleware.pojos.Germplasm;
import org.pojomatic.annotations.AutoProperty;

import java.util.ArrayList;
import java.util.List;

@AutoProperty
public class GermplasmTreeNode {

	private Integer gid;

	private String preferredName;

	private GermplasmTreeNode femaleParentNode;

	private GermplasmTreeNode maleParentNode;

	private Integer numberOfGenerations;

	private List<GermplasmTreeNode> otherProgenitors = new ArrayList<>();

	public GermplasmTreeNode(final Integer gid, final String preferredName) {
		this.gid = gid;
		this.preferredName = preferredName;
	}

	public GermplasmTreeNode(final Germplasm germplasm) {
		this.gid = germplasm.getGid();
		this.preferredName = germplasm.getPreferredName().getNval();
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

	public GermplasmTreeNode getFemaleParentNode() {
		return this.femaleParentNode;
	}

	public void setFemaleParentNode(final GermplasmTreeNode femaleParentNode) {
		this.femaleParentNode = femaleParentNode;
	}

	public GermplasmTreeNode getMaleParentNode() {
		return this.maleParentNode;
	}

	public void setMaleParentNode(final GermplasmTreeNode maleParentNode) {
		this.maleParentNode = maleParentNode;
	}

	public List<GermplasmTreeNode> getOtherProgenitors() {
		return this.otherProgenitors;
	}

	public void setOtherProgenitors(final List<GermplasmTreeNode> otherProgenitors) {
		this.otherProgenitors = otherProgenitors;
	}

	public Integer getNumberOfGenerations() {
		return this.numberOfGenerations;
	}

	public void setNumberOfGenerations(final Integer numberOfGenerations) {
		this.numberOfGenerations = numberOfGenerations;
	}

}
