
package org.generationcp.middleware.util.cache;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.cache.Cache;

public class FunctionBasedGuavaCacheLoader<K, V> {

	private Cache<K, V> cache;

	private Function<K, V> loader;

	public FunctionBasedGuavaCacheLoader(final Cache<K, V> cache, final Function<K, V> loader) {
		this.cache = cache;
		this.loader = loader;
	}


	public Optional<V> get(final K key) {

		final Optional<V> cacheValue = Optional.fromNullable(cache.getIfPresent(key));
		if (cacheValue.isPresent()) {
			return cacheValue;
		}

		final Optional<V> loadedValue = Optional.fromNullable(loader.apply(key));
		if (loadedValue.isPresent()) {
			cache.put(key, loadedValue.get());
		}
		return loadedValue;
	}
}
