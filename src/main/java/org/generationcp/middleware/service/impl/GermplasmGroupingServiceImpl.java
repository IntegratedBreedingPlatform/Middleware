
package org.generationcp.middleware.service.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.MethodDAO;
import org.generationcp.middleware.dao.UserDefinedFieldDAO;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.GermplasmDataManagerImpl;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmPedigreeTree;
import org.generationcp.middleware.pojos.GermplasmPedigreeTreeNode;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.service.api.GermplasmGroup;
import org.generationcp.middleware.service.api.GermplasmGroupingService;
import org.generationcp.middleware.service.pedigree.GermplasmCache;
import org.generationcp.middleware.service.pedigree.cache.keys.CropGermplasmKey;
import org.generationcp.middleware.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Optional;
import com.google.common.base.Strings;

public class GermplasmGroupingServiceImpl implements GermplasmGroupingService {

	private static final Logger LOG = LoggerFactory.getLogger(GermplasmGroupingServiceImpl.class);

	static final String SELECTION_HISTORY_NAME_CODE = "DRVNM";
	static final String SELECTION_HISTORY_NAME_CODE_FOR_CROSS = "CRSNM";
	static final String SELECTION_HISTORY_AT_FIXATION_NAME_CODE = "SELHISFIX";
	static final String CODED_NAME_1 = "CODE1";
	static final String CODED_NAME_2 = "CODE2";
	static final String CODED_NAME_3 = "CODE3";

	private GermplasmDAO germplasmDAO;

	private MethodDAO methodDAO;

	private UserDefinedFieldDAO userDefinedFieldDAO;
	
	private GermplasmDataManager germplasmDataManager;

	private String cropName;

	public GermplasmGroupingServiceImpl() {

	}

	public GermplasmGroupingServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.germplasmDAO = new GermplasmDAO();
		this.germplasmDAO.setSession(sessionProvider.getSession());

		this.methodDAO = new MethodDAO();
		this.methodDAO.setSession(sessionProvider.getSession());

		this.userDefinedFieldDAO = new UserDefinedFieldDAO();
		this.userDefinedFieldDAO.setSession(sessionProvider.getSession());
		
		this.germplasmDataManager = new GermplasmDataManagerImpl(sessionProvider);
		
		final ManagerFactory managerFactory = ManagerFactory.getCurrentManagerFactoryThreadLocal().get();
		this.cropName = managerFactory.getCropName();

		
	}

	public GermplasmGroupingServiceImpl(final GermplasmDAO germplasmDAO, final MethodDAO methodDAO,
			final UserDefinedFieldDAO userDefinedFieldDAO, final GermplasmDataManager germplasmDataManager,
			final String cropName) {
		this.germplasmDAO = germplasmDAO;
		this.methodDAO = methodDAO;
		this.userDefinedFieldDAO = userDefinedFieldDAO;
		this.germplasmDataManager = germplasmDataManager;
		this.cropName = cropName;
	}

	@Override
	@Transactional
	public GermplasmGroup markFixed(final Germplasm germplasmToFix, final boolean includeDescendants, final boolean preserveExistingGroup) {
		LOG.info("Marking germplasm with gid {} as fixed.", germplasmToFix.getGid());

		if (includeDescendants) {
			final GermplasmPedigreeTree tree = this.getDescendantTree(germplasmToFix);
			this.traverseAssignGroup(tree.getRoot(), germplasmToFix.getGid(), preserveExistingGroup);
		} else {
			this.assignGroup(germplasmToFix, germplasmToFix.getGid(), preserveExistingGroup);
		}

		return this.getGroupMembers(germplasmToFix);
	}

	@Override
	public GermplasmGroup getGroupMembers(final Germplasm founder) {
		final GermplasmGroup germplasmGroup = new GermplasmGroup();

		final Method method = this.methodDAO.getById(founder.getMethodId());
		founder.setMethod(method);

		germplasmGroup.setFounder(founder);
		germplasmGroup.setGroupId(founder.getMgid());
		germplasmGroup.setGroupMembers(this.germplasmDAO.getManagementGroupMembers(founder.getMgid()));
		return germplasmGroup;
	}

	private void traverseAssignGroup(final GermplasmPedigreeTreeNode node, final Integer groupId, final boolean preserveExistingGroup) {

		final boolean continueProcessing = this.assignGroup(node.getGermplasm(), groupId, preserveExistingGroup);

		if (continueProcessing) {
			for (final GermplasmPedigreeTreeNode child : node.getLinkedNodes()) {
				this.traverseAssignGroup(child, groupId, preserveExistingGroup);
			}
		}
	}

	@Override
	public GermplasmPedigreeTree getDescendantTree(final Germplasm germplasm) {
		LOG.info("Building descendant tree for gid {}.", germplasm.getGid());
		final GermplasmPedigreeTree tree = new GermplasmPedigreeTree();
		tree.setRoot(this.buildDescendantsTree(germplasm, 1));
		return tree;
	}

	private GermplasmPedigreeTreeNode buildDescendantsTree(final Germplasm germplasm, final int level) {
		final GermplasmPedigreeTreeNode node = new GermplasmPedigreeTreeNode();
		node.setGermplasm(germplasm);

		final List<Germplasm> allChildren = this.germplasmDAO.getAllChildren(germplasm.getGid());

		final String indent = Strings.padStart(">", level + 1, '-');
		final Set<Integer> childrenIds = new TreeSet<>();

		for (final Germplasm child : allChildren) {
			childrenIds.add(child.getGid());
		}
		LOG.info("{} Level {} (gid: {}) Children: {}  ", indent, level, germplasm.getGid(), childrenIds);

		for (final Germplasm child : allChildren) {
			node.getLinkedNodes().add(this.buildDescendantsTree(child, level + 1));
		}
		return node;
	}

	/**
	 * Return value is a boolean flag that indicates whether further processing (descendants) should continue or not. Currenlty we stop
	 * processing based on one rul: when we encounter generative germplasm.
	 */
	private boolean assignGroup(final Germplasm germplasm, final Integer groupId, final boolean preserveExistingGroup) {

		final boolean hasMGID = germplasm.getMgid() != null && germplasm.getMgid() != 0;

		if (!preserveExistingGroup && hasMGID && !germplasm.getMgid().equals(groupId)) {
			LOG.info("Gerplasm with gid [{}] already has mgid [{}]. Service has been asked to ignore it, and assign new mgid [{}].",
					germplasm.getGid(), germplasm.getMgid(), groupId);
		}

		final Method method = this.methodDAO.getById(germplasm.getMethodId());
		if (method != null && method.isGenerative()) {
			LOG.info("Method {} ({}), of the germplasm (gid {}) is generative. MGID assignment for generative germplasm is not supported.",
					germplasm.getMethodId(), method.getMname(), germplasm.getGid());
			return false;
		}

		if (preserveExistingGroup && hasMGID) {
			LOG.info("Retaining the existing mgid = [{}] for germplasm with gid = [{}] as it is.", germplasm.getMgid(), germplasm.getGid());
		} else {
			LOG.info("Assigning mgid = [{}] for germplasm with gid = [{}]", groupId, germplasm.getGid());
			germplasm.setMgid(groupId);
			this.copySelectionHistory(germplasm);
			this.germplasmDAO.save(germplasm);
		}

		return true;
	}

	Name getSelectionHistory(final Germplasm germplasm, final String nameType) {
		final UserDefinedField selectionHistoryNameType = this.getSelectionHistoryNameType(nameType);
		return this.findNameByType(germplasm, selectionHistoryNameType);
	}
	
	

	Name findNameByType(final Germplasm germplasm, final UserDefinedField nameType) {
		final List<Name> names = germplasm.getNames();
		Name matchingName = null;
		if (!names.isEmpty() && nameType != null) {
			for (final Name name : names) {
				if (nameType.getFldno().equals(name.getTypeId())) {
					matchingName = name;
					break;
				}
			}
		}
		return matchingName;
	}

	/**
	 * Background: one of the requirements of grouping/coding is that the selection history should not continue to grow after it has been
	 * decided that the line is finished or fixed. The solution adopted for meeting this requirement while remaining consistent with the
	 * design of the BMS is to create a new name type to represent the "selection history at fixation". We will call this the
	 * ftype=‘SELHISFIX’. When setting a line as fixed we create the MGID and copy the selection history name to the ‘SELHISFIX’
	 * name. We will set ‘SELHISFIX’ as the preferred name (designation, nstat=1) so that this will be the name that the user sees in
	 * lists within the BMS. If new germplasm records are made from this record the ‘SELHISFIX’ name will be copied (along with the
	 * MGID) and continue to be the preferred name. In the background, the ‘selection history’ will continue to grow as it usually does
	 * and will be accessible through the germplasm details screen but will not be the name displayed in lists.
	 * 
	 */
	private void addOrUpdateSelectionHistoryAtFixationName(final Germplasm germplasm, final Name nameToCopyFrom) {

		// 1. Make current preferred name a non-preferred name by setting nstat = 0
		// because we are about to add a different name as preferred.
		final Name currentPreferredName = germplasm.findPreferredName();

		if (currentPreferredName != null) {
			currentPreferredName.setNstat(0);
		}

		// 2. Check if there is an existing "selection history at fixation" name
		final Name existingSelHisFixName = this.getSelectionHistory(germplasm, GermplasmGroupingServiceImpl.SELECTION_HISTORY_AT_FIXATION_NAME_CODE);

		// 3. Add a new name as "selection history at fixation" with supplied name value and make it a preferred name.
		if (existingSelHisFixName == null) {
			final UserDefinedField selHisFixNameType = this.getSelectionHistoryNameType(GermplasmGroupingServiceImpl.SELECTION_HISTORY_AT_FIXATION_NAME_CODE);
			final Name newSelectionHistoryAtFixation = new Name();
			newSelectionHistoryAtFixation.setGermplasmId(germplasm.getGid());
			newSelectionHistoryAtFixation.setTypeId(selHisFixNameType.getFldno());
			newSelectionHistoryAtFixation.setNval(nameToCopyFrom.getNval());
			newSelectionHistoryAtFixation.setNstat(1); // nstat = 1 means it is preferred name.
			newSelectionHistoryAtFixation.setUserId(nameToCopyFrom.getUserId());
			newSelectionHistoryAtFixation.setLocationId(nameToCopyFrom.getLocationId());
			newSelectionHistoryAtFixation.setNdate(Util.getCurrentDateAsIntegerValue());
			newSelectionHistoryAtFixation.setReferenceId(0);
			germplasm.getNames().add(newSelectionHistoryAtFixation);
		} else {
			// 4. Update the extisting "selection history at fixation" with supplied name and make it a preferred name.
			existingSelHisFixName.setNval(nameToCopyFrom.getNval());
			existingSelHisFixName.setNstat(1); // nstat = 1 means it is preferred name.
			existingSelHisFixName.setNdate(Util.getCurrentDateAsIntegerValue());
		}
	}

	UserDefinedField getSelectionHistoryNameType(final String nameType) {
		final UserDefinedField selectionHistoryNameType =
				this.userDefinedFieldDAO.getByTableTypeAndCode("NAMES", "NAME", nameType);
		if (selectionHistoryNameType == null) {
			throw new IllegalStateException(
					"Missing required reference data: Please ensure User defined field (UDFLD) record for name type '"
							+ nameType + "' has been setup.");
		}
		return selectionHistoryNameType;
	}
	
	@Override
	public void copyCodedNames(final Germplasm germplasm) {
		final Germplasm parent = this.germplasmDAO.getById(germplasm.getGpid2());
		this.copyCodedName(germplasm, this.getSelectionHistory(parent, GermplasmGroupingServiceImpl.CODED_NAME_1));
		this.copyCodedName(germplasm, this.getSelectionHistory(parent, GermplasmGroupingServiceImpl.CODED_NAME_2));
		this.copyCodedName(germplasm, this.getSelectionHistory(parent, GermplasmGroupingServiceImpl.CODED_NAME_3));
	}
	
	
	private void copyCodedName(final Germplasm germplasm, final Name codedName) {
		if (codedName != null) {
			this.addCodedName(germplasm, codedName);
			LOG.info("Coded name for gid {} saved as germplasm name {} .", germplasm.getGid(),
					codedName.getNval());
		} else {
			LOG.info("No coded name was found for germplasm {}. Nothing to copy.", germplasm.getGid());
		}
	}
	
	private void addCodedName(final Germplasm germplasm, final Name nameToCopyFrom) {

		// 1. Make current preferred name a non-preferred name by setting nstat = 0
		// because we are about to add a different name as preferred.
		final Name currentPreferredName = germplasm.findPreferredName();
		if (currentPreferredName != null) {
			currentPreferredName.setNstat(0);
		}

		final Name codedName = new Name();
		codedName.setGermplasmId(germplasm.getGid());
		codedName.setTypeId(nameToCopyFrom.getTypeId());
		codedName.setNval(nameToCopyFrom.getNval());
		codedName.setNstat(1); // nstat = 1 means it is preferred name.
		codedName.setUserId(nameToCopyFrom.getUserId());
		codedName.setLocationId(nameToCopyFrom.getLocationId());
		codedName.setNdate(Util.getCurrentDateAsIntegerValue());
		codedName.setReferenceId(0);
		germplasm.getNames().add(codedName);
	}
	

	private void copySelectionHistory(final Germplasm germplasm) {

		final Name mySelectionHistory = this.getSelectionHistory(germplasm, GermplasmGroupingServiceImpl.SELECTION_HISTORY_NAME_CODE);

		if (mySelectionHistory != null) {
			this.addOrUpdateSelectionHistoryAtFixationName(germplasm, mySelectionHistory);
			LOG.info("Selection history at fixation for gid {} saved as germplasm name {} .", germplasm.getGid(),
					mySelectionHistory.getNval());
		} else {
			LOG.info("No selection history type name was found for germplasm {}. Nothing to copy.", germplasm.getGid());
		}
	}

	@Override
	public void copyParentalSelectionHistoryAtFixation(final Germplasm germplasm) {
		final Germplasm parent = this.germplasmDAO.getById(germplasm.getGpid2());
		final Name parentSelectionHistoryAtFixation = this.getSelectionHistory(parent, GermplasmGroupingServiceImpl.SELECTION_HISTORY_AT_FIXATION_NAME_CODE);

		if (parentSelectionHistoryAtFixation != null) {
			this.addOrUpdateSelectionHistoryAtFixationName(germplasm, parentSelectionHistoryAtFixation);
			LOG.info("Selection history at fixation {} was copied from parent with gid {} to the child germplasm with gid {}.",
					parentSelectionHistoryAtFixation.getNval(), germplasm.getGid(), parent.getGid());
		} else {
			LOG.info("No 'selection history at fixation' type name was found for parent germplasm with gid {}. Nothing to copy.",
					parent.getGid());
		}
	}

	private void copySelectionHistoryForCross(final Germplasm cross, final Germplasm previousCross) {

		final Name previousCrossSelectionHistory = this.getSelectionHistory(previousCross, GermplasmGroupingServiceImpl.SELECTION_HISTORY_NAME_CODE_FOR_CROSS);

		if (previousCrossSelectionHistory != null) {
			this.addOrUpdateSelectionHistoryAtFixationName(cross, previousCrossSelectionHistory);
			LOG.info("Selection history {} for cross with gid {} was copied from previous cross with gid {}.",
					previousCrossSelectionHistory.getNval(), cross.getGid(), previousCross.getGid());
		} else {
			LOG.info("No selection history type name was found for previous cross with gid {}. Nothing to copy.", previousCross.getGid());
		}
	}

	// Rigorous INFO logging in this method is intentional. Currently we dont have good visualization tools in BMS to verify results of such
	// complex operations. INFO LOGGing helps.
	@Override
	public void processGroupInheritanceForCrosses(final List<Integer> gidsOfCrosses, final boolean applyNewGroupToPreviousCrosses,
			final Set<Integer> hybridMethods) {
		final GermplasmCache germplasmCache = new GermplasmCache(germplasmDataManager, 2);
		germplasmCache.initialiseCache(cropName, new HashSet<>(gidsOfCrosses), 2);
		for (final Integer crossGID : gidsOfCrosses) {
			final Germplasm cross = getGermplasmFromOptionalValue(germplasmCache, crossGID);
			final Germplasm parent1 = getGermplasmFromOptionalValue(germplasmCache, cross.getGpid1());
			final Germplasm parent2 = getGermplasmFromOptionalValue(germplasmCache, cross.getGpid2());

			if (cross != null) {
				LOG.info("Processing group inheritance for cross: gid {}, gpid1: {}, gpid2: {}, mgid: {}, methodId: {}.", cross.getGid(),
						cross.getGpid1(), cross.getGpid2(), cross.getMgid(), cross.getMethodId());
			}

			if (parent1 != null) {
				LOG.info("Parent 1: gid {}, gpid1: {}, gpid2: {}, mgid: {}, methodId: {}.", parent1.getGid(), parent1.getGpid1(),
						parent1.getGpid2(), parent1.getMgid(), parent1.getMethodId());
			}

			if (parent2 != null) {
				LOG.info("Parent 2: gid {}, gpid1: {}, gpid2: {}, mgid: {}, methodId: {}.", parent2.getGid(), parent2.getGpid1(),
						parent2.getGpid2(), parent2.getMgid(), parent2.getMethodId());
			}

			if (hybridMethods.contains(cross.getMethodId())) {
				LOG.info("Breeding method {} of the cross is hybrid.", cross.getMethodId());
				final boolean parent1HasMGID = parent1.getMgid() != null && parent1.getMgid() != 0;
				final boolean parent2HasMGID = parent2.getMgid() != null && parent2.getMgid() != 0;
				final boolean bothParentsHaveMGID = parent1HasMGID && parent2HasMGID;

				if (bothParentsHaveMGID) {
					LOG.info("Both parents have MGIDs. Parent1 mgid {}. Parent2 mgid {}.", parent1.getMgid(), parent2.getMgid());
					final List<Germplasm> previousCrosses = this.germplasmDAO.getPreviousCrossesBetweenParentGroups(cross);

					// Remove members of the current processing batch from the list of "previous crosses" retrieved.
					final Iterator<Germplasm> previousCrossesIterator = previousCrosses.iterator();
					while (previousCrossesIterator.hasNext()) {
						final Germplasm previousCross = previousCrossesIterator.next();
						if (gidsOfCrosses.contains(previousCross.getGid())) {
							previousCrossesIterator.remove();
						}
					}

					final boolean crossingFirstTime = previousCrosses.isEmpty();
					if (crossingFirstTime) {
						LOG.info("This is a first cross of the two parents. Starting a new group. Setting gid {} to mgid.", cross.getGid());
						cross.setMgid(cross.getGid());
					} else {
						// Not the first time cross. Assign MGID of previous cross to new cross.
						// When there are multiple previous crosses, we choose the oldest created cross with MGID as preference.
						LOG.info("Previous crosses exist between the female and male parent groups:");
						for (final Germplasm previousCross : previousCrosses) {
							LOG.info("\t[gid {}, gpid1: {}, gpid2: {}, mgid: {}, methodId: {}]", previousCross.getGid(),
									previousCross.getGpid1(), previousCross.getGpid2(), previousCross.getMgid(),
									previousCross.getMethodId());
						}

						Germplasm oldestPreviousCrossWithMGID = null;
						for (final Germplasm previousCross : previousCrosses) {
							if (previousCross.getMgid() != null && previousCross.getMgid() != 0) {
								oldestPreviousCrossWithMGID = previousCross;
								break;
							}
						}

						if (oldestPreviousCrossWithMGID != null) {
							LOG.info("Assigning mgid {} from the oldest previous cross (gid {}).", oldestPreviousCrossWithMGID.getMgid(),
									oldestPreviousCrossWithMGID.getGid());
							cross.setMgid(oldestPreviousCrossWithMGID.getMgid());
							// TODO extend to include coded names as well.
							this.copySelectionHistoryForCross(cross, oldestPreviousCrossWithMGID);
						} else {
							LOG.info("Previous crosses exist but there is none with MGID. Starting a new group with mgid = gid of current cross.");
							cross.setMgid(cross.getGid());
							// TODO Flowchart says warn user for this case - this will require returning flag back to the caller from
							// service.

							if (applyNewGroupToPreviousCrosses) {
								LOG.info("Applying the new mgid {} to the previous crosses as well.", cross.getMgid());
								for (final Germplasm previousCross : previousCrosses) {
									previousCross.setMgid(cross.getMgid());
									this.germplasmDAO.save(previousCross);
								}
							}

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

	private Germplasm getGermplasmFromOptionalValue(final GermplasmCache germplasmCache, final Integer crossGID) {
		final Optional<Germplasm> germplasm = germplasmCache.getGermplasm(new CropGermplasmKey(cropName, crossGID));
		if(germplasm.isPresent()) {
			return germplasm.get();
		}
		return null;
	}
}
