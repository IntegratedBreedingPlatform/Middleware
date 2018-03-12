package org.generationcp.middleware.service.api;

import java.util.List;
import java.util.Set;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmPedigreeTree;

public interface GermplasmGroupingService {

	/**
	 * When a germplasm (line) has reached a certain point in its development
	 * and is now being "maintained" rather than further developed, Breeders
	 * refer to this transition as line becoming "fixed".
	 *
	 * At the database level, this equates to assigning an mgid for the
	 * germplasm and its descendents based on certain rules.
	 *
	 * @param germplasm
	 *            The germplasm to "fix".
	 * @param includeDescendants
	 *            Whether to include descendants in the new group being created.
	 * @param preserveExistingGroup
	 *            flag to indicate whether existing group (mgid) should be
	 *            preserved.
	 *
	 * @return {@link GermplasmGroup} summary of the result of the grouping
	 *         process.
	 */
	GermplasmGroup markFixed(Germplasm germplasm, boolean includeDescendants, boolean preserveExistingGroup);

	/**
	 * Unfix a germplasm (line).
	 * There are some cases where a line is fixed (and therefore grouped) and then discovered not to be
	 * pertaining to the assigned group or not really bieng fixed, either via genotyping or phenotipic observation.
	 * In this cases the users would like to undo this fixed mark and remove the germplasm from whichever group they've been assigned to.
	 * <p>
	 * At the database level, this will reset the mgid of a germplasm to zero.
	 *
	 * @param germplasm
	 * @return
	 */
	void unfixLine(Germplasm germplasm);

	/**
	 * Service to apply group (MGID) inheritance to newly created crosses.
	 *
	 * @param gidsOfCrossesCreated
	 *            - Must not be null.
	 * @param applyGroupingToPreviousCrosses
	 *            - Whether to apply new group to previous crosses as well when
	 *            creating a new group for new cross?
	 * @param hybridMethods
	 *            Set of method identifiers that indicate hybrid breeding
	 *            methods. Group inheritance is only applied to germplasm where
	 *            breeding method of the germplasm is one of the hybris methods
	 *            specified in this set. Must not be null.
	 */
	void processGroupInheritanceForCrosses(List<Integer> gidsOfCrossesCreated, boolean applyNewGroupToPreviousCrosses,
			Set<Integer> hybridMethods);

	/**
	 * Service to copy 'selection history at fixation' name of the parent to the
	 * submitted child germplasm.
	 *
	 * @param germplasm
	 *            - the germplasm to copy name to.
	 *
	 */
	void copyParentalSelectionHistoryAtFixation(Germplasm germplasm);

	/**
	 * Get all group members where the given germplasm is a founder. For the
	 * founder gid = mgid.
	 */
	GermplasmGroup getGroupMembers(Germplasm founder);

	GermplasmPedigreeTree getDescendantTree(Germplasm germplasm);

	void copyCodedNames(Germplasm germplasm, Germplasm sourceGermplasm);
}
