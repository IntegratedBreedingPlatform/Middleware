
package org.generationcp.middleware.service.impl;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmPedigreeTree;
import org.generationcp.middleware.pojos.GermplasmPedigreeTreeNode;
import org.generationcp.middleware.service.api.GermplasmGroupingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

public class GermplasmGroupingServiceImpl implements GermplasmGroupingService {

	private static final Logger LOG = LoggerFactory.getLogger(GermplasmGroupingServiceImpl.class);

	private GermplasmDAO germplasmDAO;

	public GermplasmGroupingServiceImpl(GermplasmDAO germplasmDAO) {
		this.germplasmDAO = germplasmDAO;
	}

	@Override
	public void markFixed(Germplasm germplasmToFix, boolean includeDescendants, boolean preserveExistingGroup) {

		if (includeDescendants) {
			GermplasmPedigreeTree tree = new GermplasmPedigreeTree();
			LOG.debug("Building descendant tree for assignment of MGID.");
			tree.setRoot(buildDescendantsTree(germplasmToFix, 1));
			traverseAssignMGID(tree.getRoot(), germplasmToFix.getGid(), preserveExistingGroup);
		} else {
			assignMGID(germplasmToFix, germplasmToFix.getGid(), preserveExistingGroup);
		}
	}

	private void traverseAssignMGID(GermplasmPedigreeTreeNode node, Integer mgidToAssign, boolean preserveExistingGroup) {
		assignMGID(node.getGermplasm(), mgidToAssign, preserveExistingGroup);
		for (GermplasmPedigreeTreeNode child : node.getLinkedNodes()) {
			traverseAssignMGID(child, mgidToAssign, preserveExistingGroup);
		}
	}

	private GermplasmPedigreeTreeNode buildDescendantsTree(Germplasm germplasm, int level) {
		GermplasmPedigreeTreeNode node = new GermplasmPedigreeTreeNode();
		node.setGermplasm(germplasm);

		List<Germplasm> allChildren = this.germplasmDAO.getAllChildren(germplasm.getGid());

		String indent = Strings.padStart(">", level + 1, '-');
		Set<Integer> childrenIds = new TreeSet<>();

		for (Germplasm child : allChildren) {
			childrenIds.add(child.getGid());
		}
		LOG.debug("{} Level {} (gid: {}) Children: {}  ", indent, level, germplasm.getGid(), childrenIds);

		for (Germplasm child : allChildren) {
			node.getLinkedNodes().add(buildDescendantsTree(child, level + 1));
		}
		return node;
	}

	private void assignMGID(Germplasm germplasm, Integer mgidToAssign, boolean preserveExistingGroup) {

		if (!preserveExistingGroup && germplasm.getMgid() != null && germplasm.getMgid() != 0) {
			LOG.warn("Gerplasm with gid [{}] already has mgid [{}]. Service has been asked to ignore it, and assign new mgid [{}].",
					germplasm.getGid(), germplasm.getMgid(), mgidToAssign);
		}

		if (!preserveExistingGroup) {
			germplasm.setMgid(mgidToAssign);
		}
		
		// TODO save germplasm records where mgid was updated or names were created.
	}
}
