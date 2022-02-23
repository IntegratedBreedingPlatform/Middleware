package org.generationcp.middleware.api.germplasm.pedigree.cop;

import java.util.Set;

public interface CopService {

	double coefficientOfParentage(int gid1, int gid2, final BTypeEnum btype);

	double coefficientOfInbreeding(int gid, final BTypeEnum btype);

	/**
	 * retrieve existing cop matrix if available. Does not trigger any calculation
	 */
	CopResponse coefficientOfParentage(Set<Integer> gids);

	/**
	 * retrieve existing cop matrix if available. Does not trigger any calculation
	 */
	CopResponse calculateCoefficientOfParentage(Set<Integer> gids);

	/**
	 * cancel job/s for the specified gids
	 */
	void cancelJobs(Set<Integer> gids);
}
