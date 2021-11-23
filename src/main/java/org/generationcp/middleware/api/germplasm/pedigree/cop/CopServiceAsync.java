package org.generationcp.middleware.api.germplasm.pedigree.cop;

import com.google.common.collect.Table;

import java.util.Set;
import java.util.concurrent.Future;

public interface CopServiceAsync {

	Future<Boolean> calculateAsync(
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

	/**
	 * Track task so that it can be cancelled in the future
	 */
	void trackFutureTask(Set<Integer> gids, Future<Boolean> future);

	/**
	 * cancel job/s for the specified gids
	 */
	void cancelJobs(Set<Integer> gids);
}
