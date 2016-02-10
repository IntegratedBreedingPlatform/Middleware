
package org.generationcp.middleware.service.impl;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.NameDAO;
import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmPedigreeTree;
import org.generationcp.middleware.pojos.GermplasmPedigreeTreeNode;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.service.api.GermplasmGroupingService;
import org.generationcp.middleware.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;

public class GermplasmGroupingServiceImpl implements GermplasmGroupingService {

	private static final Logger LOG = LoggerFactory.getLogger(GermplasmGroupingServiceImpl.class);

	private GermplasmDAO germplasmDAO;

	private NameDAO nameDAO;

	public GermplasmGroupingServiceImpl(GermplasmDAO germplasmDAO, NameDAO nameDAO) {
		this.germplasmDAO = germplasmDAO;
		this.nameDAO = nameDAO;
	}

	@Override
	@Transactional
	public void markFixed(Germplasm germplasmToFix, boolean includeDescendants, boolean preserveExistingGroup) {

		if (includeDescendants) {
			GermplasmPedigreeTree tree = new GermplasmPedigreeTree();
			LOG.debug("Building descendant tree for gid {} for assigning group (mgid).", germplasmToFix.getGid());
			tree.setRoot(buildDescendantsTree(germplasmToFix, 1));
			traverseAssignGroup(tree.getRoot(), germplasmToFix.getGid(), preserveExistingGroup);
		} else {
			assignGroup(germplasmToFix, germplasmToFix.getGid(), preserveExistingGroup);
		}
	}

	private void traverseAssignGroup(GermplasmPedigreeTreeNode node, Integer groupId, boolean preserveExistingGroup) {
		assignGroup(node.getGermplasm(), groupId, preserveExistingGroup);
		for (GermplasmPedigreeTreeNode child : node.getLinkedNodes()) {
			traverseAssignGroup(child, groupId, preserveExistingGroup);
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

	private void assignGroup(Germplasm germplasm, Integer groupId, boolean preserveExistingGroup) {

		if (!preserveExistingGroup && germplasm.getMgid() != null && germplasm.getMgid() != 0) {
			LOG.warn("Gerplasm with gid [{}] already has mgid [{}]. Service has been asked to ignore it, and assign new mgid [{}].",
					germplasm.getGid(), germplasm.getMgid(), groupId);
		}

		if (!preserveExistingGroup) {
			germplasm.setMgid(groupId);
			this.germplasmDAO.save(germplasm);
			copySelectionHistory(germplasm);
		}
	}

	private void copySelectionHistory(Germplasm germplasm) {
		List<Name> names = this.nameDAO.getByGIDWithFilters(germplasm.getGid(), null, GermplasmNameType.SELECTION_HISTORY);

		if (!names.isEmpty()) {
			String selectionHistoryNameValue = names.get(0).getNval();

			Name selectionHistoryAtFixation = new Name();
			selectionHistoryAtFixation.setGermplasmId(germplasm.getGid());
			selectionHistoryAtFixation.setTypeId(GermplasmNameType.SELECTION_HISTORY_AT_FIXATION.getUserDefinedFieldID());
			selectionHistoryAtFixation.setNval(selectionHistoryNameValue);
			selectionHistoryAtFixation.setNstat(1);
			selectionHistoryAtFixation.setUserId(1); // TODO get current user passed to the service and use here.
			selectionHistoryAtFixation.setLocationId(0); // TODO get location passed to the service and use here.
			selectionHistoryAtFixation.setNdate(Util.getCurrentDateAsIntegerValue());
			selectionHistoryAtFixation.setReferenceId(0);
			this.nameDAO.save(selectionHistoryAtFixation);
		} else {
			LOG.warn("No selection history type name was found for germplasm {}.", germplasm.getGid());
		}
	}
}
