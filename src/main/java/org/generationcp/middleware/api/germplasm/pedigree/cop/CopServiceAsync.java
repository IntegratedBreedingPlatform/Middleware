package org.generationcp.middleware.api.germplasm.pedigree.cop;

import com.google.common.collect.Table;

import java.util.Set;

public interface CopServiceAsync {

	void calculateAsync(
		Set<Integer> gids,
		Table<Integer, Integer, Double> matrix
	);

	/**
	 * Checks thread limit, put gids into queue
	 */
	void prepareExecution(Set<Integer> gids);

	/**
	 * if any gid is being processed, show progress
	 * @return
	 */
	boolean threadExists(Set<Integer> gids);

	/**
	 * @return the percentage of progress done
	 */
	double getProgress(Set<Integer> gids);
}
