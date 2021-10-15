package org.generationcp.middleware.api.germplasm.pedigree.cop;

import com.google.common.collect.Table;

import java.util.List;

public interface CopService {

	double coefficientOfParentage(int gid1, int gid2);

	double coefficientOfInbreeding(int gid);

	Table<Integer, Integer, Double> coefficientOfParentage(List<Integer> gids);
}
