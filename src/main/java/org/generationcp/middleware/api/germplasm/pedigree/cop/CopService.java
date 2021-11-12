package org.generationcp.middleware.api.germplasm.pedigree.cop;

import com.google.common.collect.Table;

import java.util.Set;

public interface CopService {

	double coefficientOfParentage(int gid1, int gid2);

	double coefficientOfInbreeding(int gid);

	Table<Integer, Integer, Double> coefficientOfParentage(Set<Integer> gids);
}
