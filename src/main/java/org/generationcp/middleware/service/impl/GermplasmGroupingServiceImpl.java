
package org.generationcp.middleware.service.impl;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.NameDAO;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
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
import com.google.common.collect.Sets;

public class GermplasmGroupingServiceImpl implements GermplasmGroupingService {

	private static final Logger LOG = LoggerFactory.getLogger(GermplasmGroupingServiceImpl.class);

	private GermplasmDAO germplasmDAO;

	private NameDAO nameDAO;

	public GermplasmGroupingServiceImpl() {

	}

	public GermplasmGroupingServiceImpl(HibernateSessionProvider sessionProvider) {
		this.germplasmDAO = new GermplasmDAO();
		this.germplasmDAO.setSession(sessionProvider.getSession());

		this.nameDAO = new NameDAO();
		this.nameDAO.setSession(sessionProvider.getSession());
	}

	public GermplasmGroupingServiceImpl(GermplasmDAO germplasmDAO, NameDAO nameDAO) {
		this.germplasmDAO = germplasmDAO;
		this.nameDAO = nameDAO;
	}

	@Override
	@Transactional
	public void markFixed(Germplasm germplasmToFix, boolean includeDescendants, boolean preserveExistingGroup) {
		LOG.info("Marking germplasm with gid {} as fixed.", germplasmToFix.getGid());

		if (includeDescendants) {
			GermplasmPedigreeTree tree = new GermplasmPedigreeTree();
			LOG.info("Building descendant tree for gid {} for assigning group (mgid).", germplasmToFix.getGid());
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
		LOG.info("{} Level {} (gid: {}) Children: {}  ", indent, level, germplasm.getGid(), childrenIds);

		for (Germplasm child : allChildren) {
			node.getLinkedNodes().add(buildDescendantsTree(child, level + 1));
		}
		return node;
	}

	private void assignGroup(Germplasm germplasm, Integer groupId, boolean preserveExistingGroup) {

		if (!preserveExistingGroup && germplasm.getMgid() != null && germplasm.getMgid() != 0 && !germplasm.getMgid().equals(groupId)) {
			LOG.info("Gerplasm with gid [{}] already has mgid [{}]. Service has been asked to ignore it, and assign new mgid [{}].",
					germplasm.getGid(), germplasm.getMgid(), groupId);
		}

		if (!preserveExistingGroup) {
			germplasm.setMgid(groupId);
			this.germplasmDAO.save(germplasm);
			LOG.info("Saved mgid = [{}] for germplasm with gid = [{}]", germplasm.getMgid(), germplasm.getGid());
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
			LOG.info("Selection history at fixation for gid {} saved as germplasm name {} .", germplasm.getGid(),
					selectionHistoryNameValue);
		} else {
			LOG.info("No selection history type name was found for germplasm {}.", germplasm.getGid());
		}
	}

	private void copySelectionHistoryForCross(Germplasm cross, Germplasm previousCross) {
		List<Name> names = this.nameDAO.getByGIDWithFilters(previousCross.getGid(), null, GermplasmNameType.SELECTION_HISTORY);

		if (!names.isEmpty()) {
			String selectionHistoryNameValue = names.get(0).getNval();

			Name selectionHistoryOfPreviousCross = new Name();
			selectionHistoryOfPreviousCross.setGermplasmId(cross.getGid());
			selectionHistoryOfPreviousCross.setTypeId(GermplasmNameType.SELECTION_HISTORY_AT_FIXATION.getUserDefinedFieldID());
			selectionHistoryOfPreviousCross.setNval(selectionHistoryNameValue);
			selectionHistoryOfPreviousCross.setNstat(1);
			selectionHistoryOfPreviousCross.setUserId(1); // TODO get current user passed to the service and use here.
			selectionHistoryOfPreviousCross.setLocationId(0); // TODO get location passed to the service and use here.
			selectionHistoryOfPreviousCross.setNdate(Util.getCurrentDateAsIntegerValue());
			selectionHistoryOfPreviousCross.setReferenceId(0);
			this.nameDAO.save(selectionHistoryOfPreviousCross);
			LOG.info("Selection history {} for cross with gid {} was copied from previous cross with gid {}.", selectionHistoryNameValue,
					cross.getGid(), previousCross.getGid());
		} else {
			LOG.info("No selection history type name was found for previous cross with gid {}.", previousCross.getGid());
		}
	}

	@Override
	public void processGroupInheritance(List<Integer> gidsOfCrossesCreated) {

		Set<Integer> hybridMethods = Sets.newHashSet(416, 417, 418, 419, 426, 321);

		for (Integer crossGID : gidsOfCrossesCreated) {

			Germplasm cross = this.germplasmDAO.getById(crossGID);
			Germplasm parent1 = this.germplasmDAO.getById(cross.getGpid1());
			Germplasm parent2 = this.germplasmDAO.getById(cross.getGpid2());

			// Is the crossing method hybrid?
			if (hybridMethods.contains(cross.getMethodId())) {
				boolean parent1HasMGID = parent1.getMgid() != null && parent1.getMgid() != 0;
				boolean parent2HasMGID = parent2.getMgid() != null && parent2.getMgid() != 0;
				boolean bothParentsHaveMGID = parent1HasMGID && parent2HasMGID;

				// Do both parents have MGIDs?
				if (bothParentsHaveMGID) {

					List<Germplasm> previousCrosses = this.germplasmDAO.getPreviousCrosses(parent1.getGid(), parent2.getGid());
					boolean crossingFirstTime = previousCrosses.isEmpty();
					if (crossingFirstTime) {
						// Crossing for the first time. Cross starts new group. Copy GID to MGID.
						cross.setMgid(cross.getGid());
					} else {
						// Not the first time cross. Assign MGID of previous cross to new cross.
						// When there are multiple previous crosses, we choose the oldest created cross with MGID as preference.
						Germplasm previousCrossSelected = null;
						for (Germplasm previousCross : previousCrosses) {
							if (previousCross.getMgid() != null && previousCross.getMgid() != 0) {
								previousCrossSelected = previousCross;
								break;
							}
						}

						if (previousCrossSelected != null) {
							cross.setMgid(previousCrossSelected.getMgid());
							copySelectionHistoryForCross(cross, previousCrossSelected);
						}
					}

				} else {
					// Both parents don't have MGIDs. Cross does not inherit MGID.
				}
			} else {
				// Breeding method not hybrid. Cross does not inherit MGID.
			}

			this.germplasmDAO.save(cross);
		}
	}

}
