
package org.generationcp.middleware.data.initializer;

import org.generationcp.middleware.pojos.GermplasmPedigreeTree;
import org.generationcp.middleware.pojos.GermplasmPedigreeTreeNode;

import java.util.LinkedList;
import java.util.List;

public class GermplasmPedigreeTreeTestDataInitializer {

	public GermplasmPedigreeTreeTestDataInitializer() {
		// do nothing
	}

	public GermplasmPedigreeTree createGermplasmPedigreeTree(final int rootGid, final int level) {
		final GermplasmPedigreeTree pedigreeTree = new GermplasmPedigreeTree();

		pedigreeTree.setRoot(this.createGermplasmPedigreeTreeNode(rootGid, level));

		return pedigreeTree;
	}

	/**
	 * Mocks scenario Derivative Unknown Male Parent
	 * @param rootGid - GID to create
	 * @param level - Level of Pedigree
	 * @return GermplasmPedigree with Unknown Node
	 */
	public GermplasmPedigreeTree createGermplasmPedigreeTreeDerivativeUnkownMale(final int rootGid, final int level) {
		final GermplasmPedigreeTree pedigreeTree = new GermplasmPedigreeTree();
		final GermplasmPedigreeTreeNode root = new GermplasmPedigreeTreeNode();
		root.setGermplasm(GermplasmTestDataInitializer.createGermplasm(rootGid));

		final GermplasmPedigreeTreeNode maleParent = this.createUnknownNode(root);
		maleParent.getLinkedNodes().addAll(this.createLinkedNodes(2, level));

		pedigreeTree.setRoot(root);
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

	private GermplasmPedigreeTreeNode createUnknownNode(GermplasmPedigreeTreeNode parent) {
		GermplasmPedigreeTreeNode node = new GermplasmPedigreeTreeNode();
		node.setGermplasm(GermplasmTestDataInitializer.createGermplasm(0));
		parent.getLinkedNodes().add(node);
		return node;
	}
}
