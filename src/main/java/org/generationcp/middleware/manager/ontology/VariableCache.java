
package org.generationcp.middleware.manager.ontology;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.domain.ontology.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Global cache of ontology variables. NOTE: As Middleware is embedded in each application war file + the fact that Tomcat class loading is
 * at the application level, each BMS application war file will have its own separate variable cache.
 *
 */
public class VariableCache {

	private static final ConcurrentMap<VariableCacheKey, Variable> VARIABLE_CACHE = new ConcurrentHashMap<>();

	private static final Logger LOG = LoggerFactory.getLogger(VariableCache.class);

	public static Variable getFromCache(final Integer variableId) {
		final String currentCrop = VariableCache.getCurrentCrop();
		final String currentProgram = VariableCache.getCurrentProgram();
		final VariableCacheKey key = new VariableCacheKey(variableId, currentCrop, currentProgram);
		final Variable variable = VariableCache.VARIABLE_CACHE.get(key);
		if (variable != null) {
			VariableCache.LOG.debug("Variable identified by [{}] in [{}] database is in cache.", variableId, currentCrop);
		}
		return variable;
	}

	public static void addToCache(final Integer variableId, final Variable variable) {
		VariableCache.VARIABLE_CACHE
				.put(new VariableCacheKey(variableId, VariableCache.getCurrentCrop(), VariableCache.getCurrentProgram()), variable);
	}

	public static void removeFromCache(final Integer variableId) {
		VariableCache.VARIABLE_CACHE
				.remove(new VariableCacheKey(variableId, VariableCache.getCurrentCrop(), VariableCache.getCurrentProgram()));
	}

	// For tests only
	public static int getCacheSize() {
		return VariableCache.VARIABLE_CACHE.size();
	}

	// For tests only
	static void clearCache() {
		VariableCache.VARIABLE_CACHE.clear();
	}

	private static String getCurrentCrop() {
		return ContextHolder.getCurrentCrop();
	}

	private static String getCurrentProgram() {
		return ContextHolder.getCurrentProgram();
	}
}
