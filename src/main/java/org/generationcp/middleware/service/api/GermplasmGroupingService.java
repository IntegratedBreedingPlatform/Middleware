package org.generationcp.middleware.service.api;

import org.generationcp.middleware.pojos.Germplasm;


public interface GermplasmGroupingService {

	/**
	 * When a germplasm (line) has reached a certain point in its development and is now being "maintained" rather than further developed,
	 * Breeders refer to this transition as line becoming "fixed".
	 * 
	 * At the database level, this equates to assigning an mgid for the germplasm and its descendents based on certain rules.
	 * 
	 * @param germplasm The germplasm to "fix".
	 * @param includeDescendants Whether to include descendants in the new group being created.
	 * @param preserveExistingGroup flag to indicate whether existing group (mgid) should be preserved.
	 */
	void markFixed(Germplasm germplasm, boolean includeDescendants, boolean preserveExistingGroup);
}
