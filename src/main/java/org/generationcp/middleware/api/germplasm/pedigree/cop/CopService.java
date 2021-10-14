package org.generationcp.middleware.api.germplasm.pedigree.cop;

public interface CopService {

	double coefficientOfParentage(int gid1, int gid2);

	double coefficientOfInbreeding(int gid);
}
