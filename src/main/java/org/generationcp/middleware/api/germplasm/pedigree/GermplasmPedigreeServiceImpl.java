package org.generationcp.middleware.api.germplasm.pedigree;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

public class GermplasmPedigreeServiceImpl implements GermplasmPedigreeService {

	private final DaoFactory daoFactory;

	@Resource
	private GermplasmDataManager germplasmDataManager;

	public GermplasmPedigreeServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public GermplasmTreeNode getGermplasmPedigreeTree(final Integer gid, final Integer level, final boolean includeDerivativeLines) {

		final Germplasm root = this.germplasmDataManager.getGermplasmWithPrefName(gid);

		final GermplasmTreeNode rootNode = new GermplasmTreeNode(root);
		if (level > 1) {
			this.addParents(rootNode, level, root, !includeDerivativeLines);
		}
		return rootNode;
	}

	/**
	 * Given a GermplasmPedigreeTreeNode and the level of the desired tree, add parents
	 * to the node recursively until the specified level of the tree is reached.
	 *
	 * @param node
	 * @param level
	 * @return the given GermplasmPedigreeTreeNode with its parents added to it
	 */
	private GermplasmTreeNode addParents(final GermplasmTreeNode node, final int level, final Germplasm germplasmOfNode,
		final boolean excludeDerivativeLines) {
		if (level != 1) {
			final Integer maleGid = germplasmOfNode.getGpid2();
			final Integer femaleGid = germplasmOfNode.getGpid1();
			if (germplasmOfNode.getGnpgs() == -1) {
				if(excludeDerivativeLines) {
					// get and add the source germplasm
					final Germplasm parent = this.germplasmDataManager.getGermplasmWithPrefName(femaleGid);
					if (parent != null) {
						this.addNodeForKnownParents(node, level, parent, excludeDerivativeLines);
					}
				} else {
					// Get and add the source germplasm, if it is unknown
					if (maleGid != 0) {
						this.addMaleParentNode(node, level, maleGid, false);
					// Use female parent to continue traversal if source is unknown
					} else if (femaleGid != 0) {
						node.setMaleParentNode(this.createUnknownParent());
						this.addFemaleParentNode(node, level, femaleGid, excludeDerivativeLines);
					}
				}
			} else if (germplasmOfNode.getGnpgs() >= 2) {
				// Get and add female and male parents
				this.addNodeForParents(node, level, germplasmOfNode, excludeDerivativeLines);

				// IF there are more parents, get and add each of them
				if (germplasmOfNode.getGnpgs() > 2) {
					final List<Germplasm> otherParents =
						this.germplasmDataManager.getProgenitorsByGIDWithPrefName(germplasmOfNode.getGid());
					node.setOtherProgenitors(new ArrayList<>());
					for (final Germplasm otherParent : otherParents) {
						final GermplasmTreeNode maleParentNode = new GermplasmTreeNode(otherParent);
						node.getOtherProgenitors().add(this.addParents(maleParentNode, level-1, otherParent, excludeDerivativeLines));
					}
				}
			}
		}
		return node;
	}

	private void addNodeForParents(final GermplasmTreeNode node, final int level, final Germplasm germplasm, final boolean excludeDerivativeLines) {
		if(germplasm.getGpid1() == 0) {
			node.setFemaleParentNode(this.createUnknownParent());
		} else {
			this.addFemaleParentNode(node, level, germplasm.getGpid1(), excludeDerivativeLines);
		}

		if(germplasm.getGpid2() == 0 ) {
			node.setMaleParentNode(this.createUnknownParent());
		} else {
			this.addMaleParentNode(node, level, germplasm.getGpid2(), excludeDerivativeLines);
		}

	}

	private void addNodeForKnownParents(final GermplasmTreeNode node, final int level, final Germplasm germplasm, final boolean excludeDerivativeLines) {
		addFemaleParentNode(node, level, germplasm.getGpid1(), excludeDerivativeLines);
		addMaleParentNode(node, level, germplasm.getGpid2(), excludeDerivativeLines);
	}

	private void addMaleParentNode(final GermplasmTreeNode node, final int level, final Integer gid, final boolean excludeDerivativeLines) {
		final Germplasm maleParent = this.germplasmDataManager.getGermplasmWithPrefName(gid);
		if(maleParent != null) {
			final GermplasmTreeNode maleParentNode = new GermplasmTreeNode(maleParent);
			node.setMaleParentNode(maleParentNode);
			this.addParents(maleParentNode, level -1, maleParent, excludeDerivativeLines);
		}
	}

	private void addFemaleParentNode(final GermplasmTreeNode node, final int level, final Integer gid, final boolean excludeDerivativeLines) {
		final Germplasm femaleParent = this.germplasmDataManager.getGermplasmWithPrefName(gid);
		if(femaleParent != null) {
			final GermplasmTreeNode femaleParentNode = new GermplasmTreeNode(femaleParent);
			node.setFemaleParentNode(femaleParentNode);
			this.addParents(femaleParentNode, level -1, femaleParent, excludeDerivativeLines);
		}
	}

	private GermplasmTreeNode createUnknownParent() {
		return  new GermplasmTreeNode(this.germplasmDataManager.getUnknownGermplasmWithPreferredName());
	}
}
