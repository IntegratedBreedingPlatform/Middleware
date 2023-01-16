package org.generationcp.middleware.service.api;

import org.generationcp.middleware.domain.germplasm.BasicNameDTO;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmPedigreeTree;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface GermplasmGroupingService {

	/**
	 * When a germplasm (line) has reached a certain point in its development
	 * and is now being "maintained" rather than further developed, Breeders
	 * refer to this transition as line becoming "fixed".
	 * <p>
	 * At the database level, this equates to assigning an mgid for the
	 * germplasm and their descendents based on certain rules.
	 *
	 * @param gids            GIDs to "fix".
	 * @param includeDescendants    Whether to include descendants in the new group being created.
	 * @param preserveExistingGroup flag to indicate whether existing group (mgid) should be
	 *                              preserved.
	 * @return {@link GermplasmGroup} summary of the result of the grouping
	 * process.
	 */
	List<GermplasmGroup> markFixed(List<Integer> gids, boolean includeDescendants, boolean preserveExistingGroup);

	/**
	 * Unfix a list of germplasm (line).
	 * There are some cases where a line is fixed (and therefore grouped) and then discovered not to be
	 * pertaining to the assigned group or not really being fixed, either via genotyping or phenotypic observation.
	 * In these cases the users would like to undo this fixed mark and remove the germplasm from whichever group they've been assigned to.
	 * <p>
	 * At the database level, this will reset the mgid of a germplasm to zero.
	 *
	 * @param gids - List of gids
	 * @return list of gids which removed from whichever group they've been assigned to
	 */
	List<Integer> unfixLines(List<Integer> gids);

	/**
	 * Service to apply group (MGID) inheritance to newly created crosses.
	 *
	 * @param germplasmIdMethodIdMap           - Must not be null.
	 * @param applyNewGroupToPreviousCrosses - Whether to apply new group to previous crosses as well when
	 *                                       creating a new group for new cross?
	 * @param hybridMethods                  Set of method identifiers that indicate hybrid breeding
	 *                                       methods. Group inheritance is only applied to germplasm where
	 *                                       breeding method of the germplasm is one of the hybris methods
	 *                                       specified in this set. Must not be null.
	 */
	void processGroupInheritanceForCrosses(String cropName, Map<Integer, Integer> germplasmIdMethodIdMap, boolean applyNewGroupToPreviousCrosses,
			Set<Integer> hybridMethods);

	/**
	 * Service to copy 'selection history at fixation' name of the parent to the
	 * submitted child germplasm.
	 *  @param germplasm - the germplasm to copy name to.
	 * @param parentGid
	 * @param parentNames
	 */
	void copyParentalSelectionHistoryAtFixation(Germplasm germplasm, Integer parentGid, List<BasicNameDTO> parentNames);

	/**
	 * Get all group members where the given germplasm is a founder. For the
	 * founder gid = mgid.
	 */
	List<GermplasmGroup> getGroupMembers(List<Integer> gids);

	GermplasmPedigreeTree getDescendantTree(Germplasm germplasm);

	List<Integer> getDescendantGroupMembersGids(Integer gid, Integer mgid);

	void copyCodedNames(Germplasm germplasm, List<BasicNameDTO> sourceNames);
}
