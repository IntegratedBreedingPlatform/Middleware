
package org.generationcp.middleware.service.impl;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.MethodDAO;
import org.generationcp.middleware.dao.NameDAO;
import org.generationcp.middleware.dao.UserDefinedFieldDAO;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmPedigreeTree;
import org.generationcp.middleware.pojos.GermplasmPedigreeTreeNode;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.UserDefinedField;
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

	private MethodDAO methodDAO;

	private UserDefinedFieldDAO userDefinedFieldDAO;

	public GermplasmGroupingServiceImpl() {

	}

	public GermplasmGroupingServiceImpl(HibernateSessionProvider sessionProvider) {
		this.germplasmDAO = new GermplasmDAO();
		this.germplasmDAO.setSession(sessionProvider.getSession());

		this.nameDAO = new NameDAO();
		this.nameDAO.setSession(sessionProvider.getSession());

		this.methodDAO = new MethodDAO();
		this.methodDAO.setSession(sessionProvider.getSession());

		this.userDefinedFieldDAO = new UserDefinedFieldDAO();
		this.userDefinedFieldDAO.setSession(sessionProvider.getSession());
	}

	public GermplasmGroupingServiceImpl(GermplasmDAO germplasmDAO, NameDAO nameDAO, MethodDAO methodDAO,
			UserDefinedFieldDAO userDefinedFieldDAO) {
		this.germplasmDAO = germplasmDAO;
		this.nameDAO = nameDAO;
		this.methodDAO = methodDAO;
		this.userDefinedFieldDAO = userDefinedFieldDAO;
	}

	@Override
	@Transactional
	public GermplasmGroupingResult markFixed(Germplasm germplasmToFix, boolean includeDescendants, boolean preserveExistingGroup) {
		LOG.info("Marking germplasm with gid {} as fixed.", germplasmToFix.getGid());

		if (includeDescendants) {
			GermplasmPedigreeTree tree = new GermplasmPedigreeTree();
			LOG.info("Building descendant tree for gid {} for assigning group (mgid).", germplasmToFix.getGid());
			tree.setRoot(buildDescendantsTree(germplasmToFix, 1));
			traverseAssignGroup(tree.getRoot(), germplasmToFix.getGid(), preserveExistingGroup);
		} else {
			assignGroup(germplasmToFix, germplasmToFix.getGid(), preserveExistingGroup);
		}

		GermplasmGroupingResult groupingResult = new GermplasmGroupingResult();
		groupingResult.setFounderGid(germplasmToFix.getGid());
		groupingResult.setGroupMgid(germplasmToFix.getMgid());
		groupingResult.setGroupMembers(this.germplasmDAO.getManagementNeighbors(germplasmToFix.getGid(), 0, Integer.MAX_VALUE));
		return groupingResult;
	}

	private void traverseAssignGroup(GermplasmPedigreeTreeNode node, Integer groupId, boolean preserveExistingGroup) {

		boolean continueProcessing = assignGroup(node.getGermplasm(), groupId, preserveExistingGroup);

		if (continueProcessing) {
			for (GermplasmPedigreeTreeNode child : node.getLinkedNodes()) {
				traverseAssignGroup(child, groupId, preserveExistingGroup);
			}
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

	/**
	 * Return value is a boolean flag that indicates whether further processing (descendants) should continue or not. Currenlty we stop
	 * processing based on one rul: when we encounter generative germplasm.
	 */
	private boolean assignGroup(Germplasm germplasm, Integer groupId, boolean preserveExistingGroup) {

		if (!preserveExistingGroup && germplasm.getMgid() != null && germplasm.getMgid() != 0 && !germplasm.getMgid().equals(groupId)) {
			LOG.info("Gerplasm with gid [{}] already has mgid [{}]. Service has been asked to ignore it, and assign new mgid [{}].",
					germplasm.getGid(), germplasm.getMgid(), groupId);
		}

		Method method = this.methodDAO.getById(germplasm.getMethodId());
		if (method != null && method.isGenerative()) {
			LOG.info("Method {} ({}), of the germplasm (gid {}) is generative. MGID assignment for generative germplasm is not supported.",
					germplasm.getMethodId(),
					method.getMname(), germplasm.getGid());
			return false;
		}

		if (!preserveExistingGroup) {
			LOG.info("Assigning mgid = [{}] for germplasm with gid = [{}]", groupId, germplasm.getGid());
			germplasm.setMgid(groupId);
			this.germplasmDAO.save(germplasm);
			copySelectionHistory(germplasm);
		} else {
			LOG.info("Retaining the existing mgid = [{}] for germplasm with gid = [{}] as it is.", germplasm.getMgid(), germplasm.getGid());
		}

		return true;
	}

	private String getSelectionHistory(Integer gid) {
		List<Name> names = this.nameDAO.getByGIDWithFilters(gid, null, null);
		UserDefinedField selectionHistoryNameType = this.userDefinedFieldDAO.getByTableTypeAndCode("NAMES", "NAME", "SELHIS");

		String matchingName = null;
		if (!names.isEmpty() && selectionHistoryNameType != null) {
			for (Name name : names) {
				if (name.getTypeId().equals(selectionHistoryNameType.getFldno())) {
					matchingName = name.getNval();
					break;
				}
			}
		}
		return matchingName;
	}

	public void copySelectionHistory(Germplasm germplasm) {

		String selectionHistoryNameValue = getSelectionHistory(germplasm.getGid());

		if (selectionHistoryNameValue != null) {
			Name selectionHistoryAtFixation = new Name();
			selectionHistoryAtFixation.setGermplasmId(germplasm.getGid());
			UserDefinedField selHisFix = this.userDefinedFieldDAO.getByTableTypeAndCode("NAMES", "NAME", "SELHISFIX");
			selectionHistoryAtFixation.setTypeId(selHisFix.getFldno());
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

		String selectionHistoryNameValue = getSelectionHistory(previousCross.getGid());

		if (selectionHistoryNameValue != null) {
			Name selectionHistoryOfPreviousCross = new Name();
			selectionHistoryOfPreviousCross.setGermplasmId(cross.getGid());
			UserDefinedField selHisFix = this.userDefinedFieldDAO.getByTableTypeAndCode("NAMES", "NAME", "SELHISFIX");
			selectionHistoryOfPreviousCross.setTypeId(selHisFix.getFldno());
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

	// Rigorous INFO logging in this method is intentional. Currently we dont have good visualization tools in BMS to verify results of such
	// complex operations. INFO LOGGing helps.
	@Override
	public void processGroupInheritanceForCrosses(List<Integer> gidsOfCrosses) {

		for (Integer crossGID : gidsOfCrosses) {
			Germplasm cross = this.germplasmDAO.getById(crossGID);
			Germplasm parent1 = this.germplasmDAO.getById(cross.getGpid1());
			Germplasm parent2 = this.germplasmDAO.getById(cross.getGpid2());

			if (cross != null) {
				LOG.info("Processing group inheritance for cross: gid {}, gpid1: {}, gpid2: {}, mgid: {}, methodId: {}.",
					cross.getGid(), cross.getGpid1(), cross.getGpid2(), cross.getMgid(), cross.getMethodId());
			}

			if (parent1 != null) {
				LOG.info("Parent 1: gid {}, gpid1: {}, gpid2: {}, mgid: {}, methodId: {}.", parent1.getGid(), parent1.getGpid1(),
					parent1.getGpid2(), parent1.getMgid(), parent1.getMethodId());
			}

			if (parent2 != null) {
				LOG.info("Parent 2: gid {}, gpid1: {}, gpid2: {}, mgid: {}, methodId: {}.", parent2.getGid(), parent2.getGpid1(),
					parent2.getGpid2(), parent2.getMgid(), parent2.getMethodId());
			}

			if (Method.isHybrid(cross.getMethodId())) {
				LOG.info("Breeding method of the cross is {} which is hybrid.", cross.getMethod());
				boolean parent1HasMGID = parent1.getMgid() != null && parent1.getMgid() != 0;
				boolean parent2HasMGID = parent2.getMgid() != null && parent2.getMgid() != 0;
				boolean bothParentsHaveMGID = parent1HasMGID && parent2HasMGID;

				if (bothParentsHaveMGID) {
					LOG.info("Both parents have MGIDs. Parent1 mgid {}. Parent2 mgid {}.", parent1.getMgid(), parent2.getMgid());
					List<Germplasm> previousCrosses = this.germplasmDAO.getPreviousCrosses(parent1.getGid(), parent2.getGid());
					boolean crossingFirstTime = previousCrosses.isEmpty();
					if (crossingFirstTime) {
						LOG.info("This is a first cross of the two parents. Starting a new group. Setting gid {} to mgid.",
								cross.getGid());
						cross.setMgid(cross.getGid());
					} else {
						// Not the first time cross. Assign MGID of previous cross to new cross.
						// When there are multiple previous crosses, we choose the oldest created cross with MGID as preference.
						LOG.info("Previous cross(es) of the same two parents exist: {}.", previousCrosses);
						Germplasm previousCrossSelected = null;
						for (Germplasm previousCross : previousCrosses) {
							if (previousCross.getMgid() != null && previousCross.getMgid() != 0) {
								previousCrossSelected = previousCross;
								break;
							}
						}

						if (previousCrossSelected != null) {
							LOG.info("Assigning mgid {} from previous cross gid {}.", previousCrossSelected.getMgid(),
									previousCrossSelected.getGid());
							cross.setMgid(previousCrossSelected.getMgid());
							copySelectionHistoryForCross(cross, previousCrossSelected);
						} else {
							LOG.info("Previous crosses exist but there is none with MGID.");
							// TODO check if doing nothing is fine in this case.
						}
					}
					this.germplasmDAO.save(cross);
				} else {
					LOG.info("Both parents do not have MGID. No MGID for cross to inherit.");
				}
			} else {
				LOG.info("Breeding method is not hybrid. Cross does not inherit MGID.");
			}
		}
	}

}
