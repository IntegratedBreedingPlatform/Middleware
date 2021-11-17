package org.generationcp.middleware.api.germplasm.pedigree.cop;

import com.google.common.collect.TreeBasedTable;

import java.util.Set;

public interface CopServiceAsync {

	void calculateAsync(
		Set<Integer> gids,
		TreeBasedTable<Integer, Integer, Double> matrix,
		TreeBasedTable<Integer, Integer, Double> matrixNew
	);

	/**
	 * Checks thread limit, put gids into queue
	 * @param gids
	 */
	void prepareExecution(Set<Integer> gids);

	/**
	 * if any gid is being processed, show progress
	 * @param gids
	 */
	void checkIfThreadExists(Set<Integer> gids);
}
