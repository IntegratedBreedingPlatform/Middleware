package org.generationcp.middleware.api.germplasm.pedigree;

import org.generationcp.middleware.pojos.Germplasm;
import org.pojomatic.annotations.AutoProperty;

import java.util.ArrayList;
import java.util.List;

@AutoProperty
public class GermplasmNeighborhoodNode {

	private Integer gid;

	private String preferredName;

	private List<GermplasmNeighborhoodNode> linkedNodes = new ArrayList<>();

	public GermplasmNeighborhoodNode(final Integer gid, final String preferredName) {
		this.gid = gid;
		this.preferredName = preferredName;
	}

	public GermplasmNeighborhoodNode(final Germplasm germplasm) {
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

	public List<GermplasmNeighborhoodNode> getLinkedNodes() {
		return this.linkedNodes;
	}

	public void setLinkedNodes(final List<GermplasmNeighborhoodNode> linkedNodes) {
		this.linkedNodes = linkedNodes;
	}
}
