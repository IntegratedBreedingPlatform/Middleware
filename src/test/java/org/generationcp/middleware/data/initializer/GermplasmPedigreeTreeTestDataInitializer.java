
package org.generationcp.middleware.data.initializer;

import java.util.LinkedList;
import java.util.List;

import org.generationcp.middleware.pojos.GermplasmPedigreeTree;
import org.generationcp.middleware.pojos.GermplasmPedigreeTreeNode;

public class GermplasmPedigreeTreeTestDataInitializer {

	public GermplasmPedigreeTreeTestDataInitializer() {
		// do nothing
	}

	public GermplasmPedigreeTree createGermplasmPedigreeTree(final int rootGid, final int level) {
		final GermplasmPedigreeTree pedigreeTree = new GermplasmPedigreeTree();

		pedigreeTree.setRoot(this.createGermplasmPedigreeTreeNode(rootGid, level));

		return pedigreeTree;
	}

	private GermplasmPedigreeTreeNode createGermplasmPedigreeTreeNode(final int gid, final int level) {
		final GermplasmPedigreeTreeNode node = new GermplasmPedigreeTreeNode();
		node.setGermplasm(GermplasmTestDataInitializer.createGermplasm(gid));
		if (level > 1) {
			node.setLinkedNodes(this.createLinkedNodes(gid, level));
		}
		return node;
	}

	private List<GermplasmPedigreeTreeNode> createLinkedNodes(final int gid, final int level) {
		final List<GermplasmPedigreeTreeNode> linkedNodes = new LinkedList<GermplasmPedigreeTreeNode>();
		linkedNodes.add(this.createGermplasmPedigreeTreeNode(gid + 1, level - 1)); // female
		linkedNodes.add(this.createGermplasmPedigreeTreeNode(gid + 2, level - 1)); // male
		return linkedNodes;
	}
}
