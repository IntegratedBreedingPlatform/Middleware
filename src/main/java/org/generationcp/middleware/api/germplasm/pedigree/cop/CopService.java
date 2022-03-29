package org.generationcp.middleware.api.germplasm.pedigree.cop;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Set;

public interface CopService {

	double coefficientOfParentage(int gid1, int gid2, BTypeEnum btype);

	double coefficientOfInbreeding(int gid, BTypeEnum btype);

	/**
	 * retrieve existing cop matrix if available. Does not trigger any calculation
	 */
	CopResponse coefficientOfParentage(
		Set<Integer> gids, Integer listId, HttpServletRequest request, HttpServletResponse response) throws IOException;

	/**
	 * retrieve existing cop matrix if available
	 */
	CopResponse calculateCoefficientOfParentage(Set<Integer> gids, Integer listId);

	/**
	 * retrieve existing cop matrix if available
	 */
	CopResponse calculateCoefficientOfParentage(Integer listId);

	/**
	 * cancel job/s for the specified gids
	 */
	void cancelJobs(Set<Integer> gids, Integer listId);

	File downloadCoefficientOfParentage(Integer listId);
}
