
package org.generationcp.middleware.service.impl;

import java.util.List;

import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmPedigreeTreeNode;
import org.generationcp.middleware.service.api.GermplasmGroupingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GermplasmGroupingServiceImpl implements GermplasmGroupingService {

	private static final Logger LOG = LoggerFactory.getLogger(GermplasmGroupingServiceImpl.class);

	private GermplasmDAO germplasmDAO;

	public GermplasmGroupingServiceImpl(GermplasmDAO germplasmDAO) {
		this.germplasmDAO = germplasmDAO;
	}

	@Override
	public void markFixed(Germplasm germplasmToFix, boolean includeDescendants, boolean preserveExistingGroup) {
		assignMGID(germplasmToFix, germplasmToFix.getGid(), preserveExistingGroup);

		if (includeDescendants) {
			GermplasmPedigreeTreeNode descendentsTree = buildDescendentsTree(germplasmToFix, DEFAULT_DESCENDENT_TREE_LEVELS);
			traverseAssignMGID(descendentsTree, germplasmToFix.getGid(), preserveExistingGroup);
			// TODO save germplasm nodes in tree where mgid was updated.
		}
	}

	private void traverseAssignMGID(GermplasmPedigreeTreeNode node, Integer mgidToAssign, boolean preserveExistingGroup) {
		assignMGID(node.getGermplasm(), mgidToAssign, preserveExistingGroup);
		for (GermplasmPedigreeTreeNode child : node.getLinkedNodes()) {
			traverseAssignMGID(child, mgidToAssign, preserveExistingGroup);
		}
	}

	private GermplasmPedigreeTreeNode buildDescendentsTree(Germplasm germplasm, int levels) {
		GermplasmPedigreeTreeNode node = new GermplasmPedigreeTreeNode();
		node.setGermplasm(germplasm);

		if (levels == 0) {
			LOG.debug("Reached maximum levels specified for this branch. Stopping here and returning.");
			return node;
		}

		List<Germplasm> allChildren = this.germplasmDAO.getAllChildren(germplasm.getGid());
		for (Germplasm child : allChildren) {
			node.getLinkedNodes().add(buildDescendentsTree(child, levels - 1));
		}
		return node;
	}

	private void assignMGID(Germplasm germplasm, Integer mgidToAssign, boolean preserveExistingGroup) {
		if ((germplasm.getMgid() == null || new Integer(0).equals(germplasm.getMgid())) && !preserveExistingGroup) {
			germplasm.setMgid(mgidToAssign);
		}
	}
}
