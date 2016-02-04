package org.generationcp.middleware.service.api;

import org.generationcp.middleware.pojos.Germplasm;


public interface GermplasmGroupingService {

	int DEFAULT_DESCENDENT_TREE_LEVELS = 5;

	/**
	 * When a germplasm (line) has reached a certina point in its development and is now being "maintained" rather than further developed,
	 * Breeders refer to this transition as line becoming "fixed".
	 * 
	 * At the database level, this equates to assigning an mgid for the germplasm and its descendents based on certain rules.
	 * 
	 * @param germplasm The germplasm to "fix".
	 * @param preserveExistingGroup flag to indicate whether existing group (mgid) should be preserved.
	 */
	void markFixed(Germplasm germplasm, boolean preserveExistingGroup);
}
