package org.generationcp.middleware.api.germplasm.pedigree.cop;

import com.google.common.collect.Table;

import java.util.Set;

public interface CopService {

	/**
	 * used for integration testing
	 */
	double coefficientOfParentage(int gid1, int gid2, BTypeEnum btype);

	/**
	 * used for integration testing
	 */
	double coefficientOfInbreeding(int gid, BTypeEnum btype);

	Table<Integer, Integer, Double> getCopMatrixByGids(Set<Integer> gids);
}
