
package org.generationcp.middleware.domain.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.pojos.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Global cache of methods
 *
 */
public class BreedingMethodCache {

	private static final ConcurrentMap<BreedingMethodCacheKey, Method> BREEDING_METHOD_CACHE = new ConcurrentHashMap<>();

	private static final Logger LOG = LoggerFactory.getLogger(BreedingMethodCache.class);

	public static Method getFromCache(final Integer methodId) {
		final String currentCrop = BreedingMethodCache.getCurrentCrop();
		final BreedingMethodCacheKey key = new BreedingMethodCacheKey(methodId, currentCrop);
		final Method method = BreedingMethodCache.BREEDING_METHOD_CACHE.get(key);
		if (method != null) {
			BreedingMethodCache.LOG.debug("Breeding method identified by [{}] in [{}] database is in cache.", methodId, currentCrop);
		}
		return method;
	}

	public static void addToCache(final Integer methodId, final Method method) {
		BreedingMethodCache.BREEDING_METHOD_CACHE.put(new BreedingMethodCacheKey(methodId, BreedingMethodCache.getCurrentCrop()), method);
	}

	public static void removeFromCache(final Integer methodId) {
		BreedingMethodCache.BREEDING_METHOD_CACHE.remove(new BreedingMethodCacheKey(methodId, BreedingMethodCache.getCurrentCrop()));
	}

	// For tests only
	static int getCacheSize() {
		return BreedingMethodCache.BREEDING_METHOD_CACHE.size();
	}

	private static String getCurrentCrop() {
		return ContextHolder.getCurrentCrop();
	}
}
