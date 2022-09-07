package org.generationcp.middleware.service.impl;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class GermplasmGroupingServiceImpl implements GermplasmGroupingService {

	private static final Logger LOG = LoggerFactory.getLogger(GermplasmGroupingServiceImpl.class);

	static final String SELECTION_HISTORY_NAME_CODE = "DRVNM";
	static final String SELECTION_HISTORY_NAME_CODE_FOR_CROSS = "CRSNM";
	static final String SELECTION_HISTORY_AT_FIXATION_NAME_CODE = "SELHISFIX";
	static final String CODED_NAME_1 = "CODE1";
	static final String CODED_NAME_2 = "CODE2";
	static final String CODED_NAME_3 = "CODE3";

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	@Value("${germplasm.grouping.max.recursion}")
	public int maxRecursiveQueries;

	private UserDefinedField selHistNameType;

	private UserDefinedField selHistAtFixationNameType;

	private UserDefinedField selHistNameTypeForCode;

	private final List<UserDefinedField> codingNameTypes = new ArrayList<>();

	private final DaoFactory daoFactory;

	private final Map<Integer, String> germplasmSelHistNameMap = new HashMap<>();

	public GermplasmGroupingServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<GermplasmGroup> markFixed(final List<Integer> gids, final boolean includeDescendants, final boolean preserveExistingGroup) {
		final List<Germplasm> germplasmList = this.daoFactory.getGermplasmDao().getByGIDList(gids);
		final Map<Integer, Germplasm> germplasmMap = germplasmList.stream().collect(Collectors.toMap(
			Germplasm::getGid, Function.identity()));
		this.selHistNameType = this.getSelectionHistoryNameType(SELECTION_HISTORY_NAME_CODE);
		this.selHistAtFixationNameType = this.getSelectionHistoryNameType(SELECTION_HISTORY_AT_FIXATION_NAME_CODE);

		for (final Integer gid : gids) {
			GermplasmGroupingServiceImpl.LOG.info("Marking germplasm with gid {} as fixed.", gid);
			final Germplasm germplasmToFix = germplasmMap.get(gid);
			if (includeDescendants) {
				final GermplasmPedigreeTree tree = this.getDescendantTree(germplasmToFix);
				this.traverseAssignGroup(tree.getRoot(), gid, preserveExistingGroup);
			} else {
				this.assignGroup(germplasmToFix, gid, preserveExistingGroup);
			}
		}
		return this.getGroupMembersForGermplasm(germplasmList);
	}

	@Override
	public List<Integer> unfixLines(final List<Integer> gids) {
		final List<Integer> gidsToUnFix = new ArrayList<>(gids);
		final GermplasmDAO germplasmDao = this.daoFactory.getGermplasmDao();
		final List<Integer> unfixedLines =
			germplasmDao.getGermplasmWithoutGroup(gids).stream().map(Germplasm::getGid).collect(Collectors.toList());
		gidsToUnFix.removeAll(unfixedLines);
		germplasmDao.resetGermplasmGroup(gidsToUnFix);
		return gidsToUnFix;
	}

	@Override
	public List<GermplasmGroup> getGroupMembers(final List<Integer> gids) {
		final List<Germplasm> germplasmList = this.daoFactory.getGermplasmDao().getByGIDList(gids);
		return this.getGroupMembersForGermplasm(germplasmList);
	}

	private List<GermplasmGroup> getGroupMembersForGermplasm(final List<Germplasm> germplasmList) {
		final List<GermplasmGroup> germplasmGroupList = new ArrayList<>();
		for (final Germplasm founder : germplasmList) {
			final GermplasmGroup germplasmGroup = new GermplasmGroup();
			germplasmGroup.setFounderGid(founder.getGid());
			germplasmGroup.setGenerative(founder.getMethod().isGenerative());
			germplasmGroup.setGroupId(founder.getMgid());
			this.daoFactory.getGermplasmDao().getManagementGroupMembers(founder.getMgid()).forEach(germplasmGroup::addGroupMember);
			germplasmGroupList.add(germplasmGroup);
		}

		return germplasmGroupList;
	}

	@Override
	public List<Integer> getDescendantGroupMembersGids(final Integer gid, final Integer mgid) {
		final List<Germplasm> descendantGroupMembers = new ArrayList<>();
		this.populateDescendantGroupMembers(descendantGroupMembers, gid, mgid);
		return descendantGroupMembers.stream().map(Germplasm::getGid).collect(Collectors.toList());
	}

	private void populateDescendantGroupMembers(final List<Germplasm> descendantGroupMembers, final Integer gid, final Integer mgid) {
		// Grouping only applies to advanced germplasm so we only need to retrieve non-generative children
		final List<Germplasm> nonGenerativeChildren = this.daoFactory.getGermplasmDao().getDescendants(gid, 'D');

		//Filter children in the same maintenance group
		final List<Germplasm> descendantGroupChildren = nonGenerativeChildren.stream()
			.filter(germplasm -> mgid.equals(germplasm.getMgid())).collect(Collectors.toList());
		descendantGroupMembers.addAll(descendantGroupChildren);

		for (final Germplasm germplasm : descendantGroupChildren) {
			this.populateDescendantGroupMembers(descendantGroupMembers, germplasm.getGid(), mgid);
		}
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
		GermplasmGroupingServiceImpl.LOG.info("Building descendant tree for gid {}.", germplasm.getGid());
		final GermplasmPedigreeTree tree = new GermplasmPedigreeTree();
		tree.setRoot(this.buildDescendantsTree(germplasm, 1));
		return tree;
	}

	private GermplasmPedigreeTreeNode buildDescendantsTree(final Germplasm germplasm, final int level) {
		if (level > this.maxRecursiveQueries) {
			throw new MiddlewareRequestException("", "germplasm.grouping.max.recursive.queries.reached", "");
		}
		final GermplasmPedigreeTreeNode node = new GermplasmPedigreeTreeNode();
		node.setGermplasm(germplasm);

		final List<Germplasm> allChildren = this.daoFactory.getGermplasmDao().getAllChildren(germplasm.getGid());

		final String indent = Strings.padStart(">", level + 1, '-');
		final Set<Integer> childrenIds = new TreeSet<>();

		for (final Germplasm child : allChildren) {
			childrenIds.add(child.getGid());
		}
		GermplasmGroupingServiceImpl.LOG.info("{} Level {} (gid: {}) Children: {}  ", indent, level, germplasm.getGid(), childrenIds);

		for (final Germplasm child : allChildren) {
			node.getLinkedNodes().add(this.buildDescendantsTree(child, level + 1));
		}
		return node;
	}

	/**
	 * Return value is a boolean flag that indicates whether further processing
	 * (descendants) should continue or not. Currenlty we stop processing based
	 * on one rul: when we encounter generative germplasm.
	 */
	private boolean assignGroup(final Germplasm germplasm, final Integer groupId, final boolean preserveExistingGroup) {

		final boolean hasMGID = germplasm.getMgid() != null && germplasm.getMgid() != 0;

		if (!preserveExistingGroup && hasMGID && !germplasm.getMgid().equals(groupId)) {
			GermplasmGroupingServiceImpl.LOG
				.info("Gerplasm with gid [{}] already has mgid [{}]. Service has been asked to ignore it, and assign new mgid [{}].",
					germplasm.getGid(), germplasm.getMgid(), groupId);
		}

		final Method method = germplasm.getMethod();
		if (method != null && method.isGenerative()) {
			GermplasmGroupingServiceImpl.LOG
				.info("Method {} ({}), of the germplasm (gid {}) is generative. MGID assignment for generative germplasm is not supported.",
					germplasm.getMethod().getMid(), method.getMname(), germplasm.getGid());
			return false;
		}

		if (preserveExistingGroup && hasMGID) {
			GermplasmGroupingServiceImpl.LOG
				.info("Retaining the existing mgid = [{}] for germplasm with gid = [{}] as it is.", germplasm.getMgid(),
					germplasm.getGid());
		} else {
			GermplasmGroupingServiceImpl.LOG.info("Assigning mgid = [{}] for germplasm with gid = [{}]", groupId, germplasm.getGid());
			germplasm.setMgid(groupId);
			this.retrieveSelectionHistory(germplasm);
			this.daoFactory.getGermplasmDao().save(germplasm);
		}

		return true;
	}

	Name getSelectionHistory(final Germplasm germplasm, final String nameType) {
		final UserDefinedField selectionHistoryNameType = this.getSelectionHistoryNameType(nameType);
		return this.findNameByType(germplasm, selectionHistoryNameType);
	}

	Name findNameByType(final Germplasm germplasm, final UserDefinedField nameType) {
		if (nameType != null) {
			final java.util.Optional<Name> nameOptional =
				germplasm.getNames().stream().filter(name -> nameType.getFldno().equals(name.getTypeId())).findFirst();
			if (nameOptional.isPresent()) {
				return nameOptional.get();
			}
		}
		return null;
	}

	/**
	 * Background: one of the requirements of grouping/coding is that the
	 * selection history should not continue to grow after it has been decided
	 * that the line is finished or fixed. The solution adopted for meeting this
	 * requirement while remaining consistent with the design of the BMS is to
	 * create a new name type to represent the "selection history at fixation".
	 * We will call this the ftype=‘SELHISFIX’. When setting a line as fixed
	 * we create the MGID and copy the selection history name to the
	 * ‘SELHISFIX’ name. If new germplasm records are made from this record
	 * the ‘SELHISFIX’ name will be copied (along with the MGID). In the
	 * background, the ‘selection history’ will continue to grow as it
	 * usually does and will be accessible through the germplasm details screen
	 * but will not be the name displayed in lists.
	 */
	private void addOrUpdateSelectionHistoryAtFixationName(final Germplasm germplasm, final Name nameToCopyFrom,
		final UserDefinedField nameType) {

		// 1. Check if there is an existing "selection history at fixation" name
		final Name existingSelHisFixName =
			this.findNameByType(germplasm, nameType);

		// 2. Add a new name as "selection history at fixation" with supplied
		// name value.
		if (existingSelHisFixName == null) {
			final UserDefinedField selHisFixNameType = nameType;
			final Name newSelectionHistoryAtFixation = new Name();
			newSelectionHistoryAtFixation.setGermplasm(germplasm);
			newSelectionHistoryAtFixation.setTypeId(selHisFixNameType.getFldno());
			newSelectionHistoryAtFixation.setNval(nameToCopyFrom.getNval());
			newSelectionHistoryAtFixation.setNstat(0);
			newSelectionHistoryAtFixation.setLocationId(nameToCopyFrom.getLocationId());
			newSelectionHistoryAtFixation.setNdate(Util.getCurrentDateAsIntegerValue());
			newSelectionHistoryAtFixation.setReferenceId(0);
			germplasm.getNames().add(newSelectionHistoryAtFixation);
		} else {
			// 3. Update the extisting "selection history at fixation" with
			// supplied name
			existingSelHisFixName.setNval(nameToCopyFrom.getNval());
			existingSelHisFixName.setNstat(0);
			existingSelHisFixName.setNdate(Util.getCurrentDateAsIntegerValue());
		}
	}

	UserDefinedField getSelectionHistoryNameType(final String nameType) {
		final UserDefinedField selectionHistoryNameType =
			this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode("NAMES", "NAME", nameType);
		if (selectionHistoryNameType == null) {
			throw new IllegalStateException(
				"Missing required reference data: Please ensure User defined field (UDFLD) record for name type '" + nameType
					+ "' has been setup.");
		}
		return selectionHistoryNameType;
	}

	@Override
	public void copyCodedNames(final Germplasm germplasm, final Germplasm sourceGermplasm) {
		for (final UserDefinedField codingNameType : this.getCodingNameTypes()) {
			this.copyCodedName(germplasm, this.findNameByType(sourceGermplasm, codingNameType));
		}
	}

	private List<UserDefinedField> getCodingNameTypes() {
		if (this.codingNameTypes.isEmpty()) {
			this.codingNameTypes.add(this.getSelectionHistoryNameType(CODED_NAME_1));
			this.codingNameTypes.add(this.getSelectionHistoryNameType(CODED_NAME_2));
			this.codingNameTypes.add(this.getSelectionHistoryNameType(CODED_NAME_3));
		}

		return this.codingNameTypes;
	}

	private void copyCodedName(final Germplasm germplasm, final Name codedName) {
		if (codedName != null) {
			this.addCodedName(germplasm, codedName);
			GermplasmGroupingServiceImpl.LOG
				.info("Coded name for gid {} saved as germplasm name {} .", germplasm.getGid(), codedName.getNval());
		} else {
			GermplasmGroupingServiceImpl.LOG.info("No coded name was found for germplasm {}. Nothing to copy.", germplasm.getGid());
		}
	}

	private void addCodedName(final Germplasm germplasm, final Name nameToCopyFrom) {

		// 1. Make current preferred name a non-preferred name by setting nstat
		// = 0
		// because we are about to add a different name as preferred.
		final Name currentPreferredName = germplasm.findPreferredName();
		if (currentPreferredName != null) {
			currentPreferredName.setNstat(0);
		}

		final Name codedName = new Name();
		codedName.setGermplasm(germplasm);
		codedName.setTypeId(nameToCopyFrom.getTypeId());
		codedName.setNval(nameToCopyFrom.getNval());
		// nstat = 1 means it is preferred name.
		codedName.setNstat(1);
		codedName.setLocationId(nameToCopyFrom.getLocationId());
		codedName.setNdate(Util.getCurrentDateAsIntegerValue());
		codedName.setReferenceId(0);
		germplasm.getNames().add(codedName);
	}

	private void retrieveSelectionHistory(final Germplasm germplasm) {
		final Integer gid = germplasm.getGid();

		if (this.germplasmSelHistNameMap.containsKey(gid)) {
			this.logSelectionHistoryTypeNameStatus(gid, this.germplasmSelHistNameMap.get(gid));
		} else {
			this.copySelectionHistory(germplasm);
		}
	}

	private void copySelectionHistory(final Germplasm germplasm) {
		final Integer gid = germplasm.getGid();
		String nameVal = StringUtils.EMPTY;

		final Name mySelectionHistory = this.findNameByType(germplasm, this.selHistNameType);
		if (mySelectionHistory != null) {
			this.addOrUpdateSelectionHistoryAtFixationName(germplasm, mySelectionHistory, this.selHistAtFixationNameType);
			nameVal = mySelectionHistory.getNval();
		}

		this.germplasmSelHistNameMap.put(gid, nameVal);
		this.logSelectionHistoryTypeNameStatus(gid, nameVal);
	}

	private void logSelectionHistoryTypeNameStatus(final Integer gid, final String nameVal) {
		if (StringUtils.isEmpty(nameVal)) {
			GermplasmGroupingServiceImpl.LOG
				.info("No selection history type name was found for germplasm {}. Nothing to copy.", gid);
		} else {
			GermplasmGroupingServiceImpl.LOG
				.info("Selection history at fixation for gid {} saved as germplasm name {} .", gid, nameVal);
		}
	}

	@Override
	public void copyParentalSelectionHistoryAtFixation(final Germplasm germplasm) {
		if (this.selHistAtFixationNameType == null) {
			this.selHistAtFixationNameType =
				this.getSelectionHistoryNameType(GermplasmGroupingServiceImpl.SELECTION_HISTORY_AT_FIXATION_NAME_CODE);
		}

		final Germplasm parent = this.daoFactory.getGermplasmDao().getById(germplasm.getGpid2());
		final Name parentSelectionHistoryAtFixation =
			this.findNameByType(parent, this.selHistAtFixationNameType);

		if (parentSelectionHistoryAtFixation != null) {
			this.addOrUpdateSelectionHistoryAtFixationName(germplasm, parentSelectionHistoryAtFixation, this.selHistAtFixationNameType);
			GermplasmGroupingServiceImpl.LOG
				.info("Selection history at fixation {} was copied from parent with gid {} to the child germplasm with gid {}.",
					parentSelectionHistoryAtFixation.getNval(), germplasm.getGid(), parent.getGid());
		} else {
			GermplasmGroupingServiceImpl.LOG
				.info("No 'selection history at fixation' type name was found for parent germplasm with gid {}. Nothing to copy.",
					parent.getGid());
		}
	}

	private void copySelectionHistoryForCross(final Germplasm cross, final Germplasm previousCross) {

		final Name previousCrossSelectionHistory = this.findNameByType(previousCross, this.selHistNameTypeForCode);

		if (previousCrossSelectionHistory != null) {
			this.addOrUpdateSelectionHistoryAtFixationName(cross, previousCrossSelectionHistory, this.selHistAtFixationNameType);
			GermplasmGroupingServiceImpl.LOG.info("Selection history {} for cross with gid {} was copied from previous cross with gid {}.",
				previousCrossSelectionHistory.getNval(), cross.getGid(), previousCross.getGid());
		} else {
			GermplasmGroupingServiceImpl.LOG
				.info("No selection history type name was found for previous cross with gid {}. Nothing to copy.",
					previousCross.getGid());
		}
	}

	// Rigorous INFO logging in this method is intentional. Currently we dont
	// have good visualization tools in BMS to verify results of such
	// complex operations. INFO LOGGing helps.
	@Override
	public void processGroupInheritanceForCrosses(final String cropName, final Map<Integer, Integer> germplasmIdMethodIdMap,
		final boolean applyNewGroupToPreviousCrosses,
		final Set<Integer> hybridMethods) {
		final GermplasmCache germplasmCache = new GermplasmCache(this.germplasmDataManager, 2);
		final Set<Integer> gidsOfCrosses = germplasmIdMethodIdMap.keySet();
		this.selHistNameTypeForCode = this.getSelectionHistoryNameType(GermplasmGroupingServiceImpl.SELECTION_HISTORY_NAME_CODE_FOR_CROSS);
		this.selHistAtFixationNameType = this.getSelectionHistoryNameType(SELECTION_HISTORY_AT_FIXATION_NAME_CODE);

		germplasmCache.initialiseCache(cropName, gidsOfCrosses, 2);
		// We passed method is as map to optimize performance instead of retrieving cross to get the germplasm method
		for (final Map.Entry<Integer, Integer> germplasmData : germplasmIdMethodIdMap.entrySet()) {
			final Integer methodId = germplasmData.getValue();
			if (hybridMethods.contains(methodId)) {
				final Germplasm cross = this.getGermplasmFromOptionalValue(cropName, germplasmCache, germplasmData.getKey());
				if (cross != null) {
					GermplasmGroupingServiceImpl.LOG
						.info("Processing group inheritance for cross: gid {}, gpid1: {}, gpid2: {}, mgid: {}, methodId: {}.",
							cross.getGid(), cross.getGpid1(), cross.getGpid2(), cross.getMgid(), cross.getMethod().getMid());
				}

				GermplasmGroupingServiceImpl.LOG.info("Breeding method {} of the cross is hybrid.", cross.getMethod().getMid());
				final Germplasm parent1 = this.getGermplasmFromOptionalValue(cropName, germplasmCache, cross.getGpid1());
				final Germplasm parent2 = this.getGermplasmFromOptionalValue(cropName, germplasmCache, cross.getGpid2());
				if (parent1 != null) {
					GermplasmGroupingServiceImpl.LOG
						.info("Parent 1: gid {}, gpid1: {}, gpid2: {}, mgid: {}, methodId: {}.", parent1.getGid(), parent1.getGpid1(),
							parent1.getGpid2(), parent1.getMgid(), parent1.getMethod().getMid());
				}

				if (parent2 != null) {
					GermplasmGroupingServiceImpl.LOG
						.info("Parent 2: gid {}, gpid1: {}, gpid2: {}, mgid: {}, methodId: {}.", parent2.getGid(), parent2.getGpid1(),
							parent2.getGpid2(), parent2.getMgid(), parent2.getMethod().getMid());
				}
				final boolean parent1HasMGID = parent1 != null && parent1.getMgid() != null && parent1.getMgid() != 0;
				final boolean parent2HasMGID = parent2 != null && parent2.getMgid() != null && parent2.getMgid() != 0;
				final boolean bothParentsHaveMGID = parent1HasMGID && parent2HasMGID;

				if (bothParentsHaveMGID) {
					GermplasmGroupingServiceImpl.LOG
						.info("Both parents have MGIDs. Parent1 mgid {}. Parent2 mgid {}.", parent1.getMgid(), parent2.getMgid());
					final List<Germplasm> previousCrosses = this.daoFactory.getGermplasmDao().getPreviousCrossesBetweenParentGroups(cross);

					// Remove members of the current processing batch from the
					// list of "previous crosses" retrieved.
					final Iterator<Germplasm> previousCrossesIterator = previousCrosses.iterator();
					while (previousCrossesIterator.hasNext()) {
						final Germplasm previousCross = previousCrossesIterator.next();
						if (gidsOfCrosses.contains(previousCross.getGid())) {
							previousCrossesIterator.remove();
						}
					}

					final boolean crossingFirstTime = previousCrosses.isEmpty();
					if (crossingFirstTime) {
						GermplasmGroupingServiceImpl.LOG
							.info("This is a first cross of the two parents. Starting a new group. Setting gid {} to mgid.",
								cross.getGid());
						cross.setMgid(cross.getGid());
					} else {
						// Not the first time cross. Assign MGID of previous
						// cross to new cross.
						// When there are multiple previous crosses, we choose
						// the oldest created cross with MGID as preference.
						GermplasmGroupingServiceImpl.LOG.info("Previous crosses exist between the female and male parent groups:");
						for (final Germplasm previousCross : previousCrosses) {
							GermplasmGroupingServiceImpl.LOG
								.info("\t[gid {}, gpid1: {}, gpid2: {}, mgid: {}, methodId: {}]", previousCross.getGid(),
									previousCross.getGpid1(), previousCross.getGpid2(), previousCross.getMgid(),
									previousCross.getMethod().getMid());
						}

						Germplasm oldestPreviousCrossWithMGID = null;
						for (final Germplasm previousCross : previousCrosses) {
							if (previousCross.getMgid() != null && previousCross.getMgid() != 0) {
								oldestPreviousCrossWithMGID = previousCross;
								break;
							}
						}

						if (oldestPreviousCrossWithMGID != null) {
							GermplasmGroupingServiceImpl.LOG.info("Assigning mgid {} from the oldest previous cross (gid {}).",
								oldestPreviousCrossWithMGID.getMgid(), oldestPreviousCrossWithMGID.getGid());
							cross.setMgid(oldestPreviousCrossWithMGID.getMgid());
							this.copySelectionHistoryForCross(cross, oldestPreviousCrossWithMGID);
							this.copyCodedNames(cross, oldestPreviousCrossWithMGID);
						} else {
							GermplasmGroupingServiceImpl.LOG
								.info(
									"Previous crosses exist but there is none with MGID. Starting a new group with mgid = gid of current cross.");
							cross.setMgid(cross.getGid());
							// TODO Flowchart says warn user for this case -
							// this will require returning flag back to the
							// caller from
							// service.

						}

						if (applyNewGroupToPreviousCrosses) {
							GermplasmGroupingServiceImpl.LOG
								.info("Applying the new mgid {} to the previous crosses as well.", cross.getMgid());
							for (final Germplasm previousCross : previousCrosses) {
								previousCross.setMgid(cross.getMgid());
								this.daoFactory.getGermplasmDao().save(previousCross);
							}
						}
					}
					this.daoFactory.getGermplasmDao().save(cross);
				} else {
					GermplasmGroupingServiceImpl.LOG.info("Both parents do not have MGID. No MGID for cross to inherit.");
				}
			} else {
				GermplasmGroupingServiceImpl.LOG.info("Breeding method is not hybrid. Cross does not inherit MGID.");
			}

		}
	}

	private Germplasm getGermplasmFromOptionalValue(final String cropName, final GermplasmCache germplasmCache, final Integer crossGID) {
		final Optional<Germplasm> germplasm = germplasmCache.getGermplasm(new CropGermplasmKey(cropName, crossGID));
		if (germplasm.isPresent()) {
			return germplasm.get();
		}
		return null;
	}

}
