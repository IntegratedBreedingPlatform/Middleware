
package org.generationcp.middleware.service.pedigree;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.service.pedigree.cache.keys.CropGermplasmKey;
import org.generationcp.middleware.util.cache.FunctionBasedGuavaCacheLoader;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * A central class will be used to retrieve germplasm and all its parents in an efficient manner. This class should be used to prime a cache
 * so that germplasm and parents can be pre-fetched. Note this class is designed to be created on a per thread basis. It will hold the
 * results for the duration of a request.
 */
public class GermplasmCache {

	/**
	 * The germplasm cache. Do not make this static. The code has been designed in such a way that the cache is only needed for the duration
	 * of the request. This is done purposefully.
	 */
	private Cache<CropGermplasmKey, Germplasm> germplasmCache;

	private FunctionBasedGuavaCacheLoader<CropGermplasmKey, Germplasm> germplasmCropBasedCache;

	private GermplasmDataManager germplasmDataManager;

	/**
	 * Initializes the germplasm cache.
	 */
	public GermplasmCache(final GermplasmDataManager germplasmDataManager, final int numberOfLevels) {
		this.germplasmDataManager = germplasmDataManager;

		// Cache will expire in 10 minutes so that we do not have old germplasm in case of name changes.
		// The timeout is a bit meaning less because no request will last 10 minutes.
		germplasmCache = CacheBuilder.newBuilder().maximumSize(50000).expireAfterWrite(100, TimeUnit.MINUTES).build();

		germplasmCropBasedCache =
				new FunctionBasedGuavaCacheLoader<CropGermplasmKey, Germplasm>(germplasmCache, new Function<CropGermplasmKey, Germplasm>() {

					@Override
					public Germplasm apply(CropGermplasmKey key) {
						// Get all the ancestor germplasm in the cache
						final Map<Integer, Germplasm> initialisesCache = initialisesCache(key.getCropName(), Collections.singleton(key.getGid()), numberOfLevels);

						// The gid should already be in the cache
						final Germplasm returnResult = initialisesCache.get(key.getGid());

						// If the germplasm has been replaced get the correct germplasm.
						if (returnResult != null && returnResult.getGrplce() != 0) {
							return germplasmCropBasedCache.get(new CropGermplasmKey(key.getCropName(), returnResult.getGrplce())).get();
						}
						return returnResult;
					}
				});
	}

	/**
	 * Method to help with testing.
	 * 
	 * @param germplasmCropBasedCache dummy function for testing
	 */
	public GermplasmCache(final FunctionBasedGuavaCacheLoader<CropGermplasmKey, Germplasm> germplasmCropBasedCache) {
		// Cache will expire in 10 minutes so that we do not have old germplasm in case of name changes.
		germplasmCache = CacheBuilder.newBuilder().maximumSize(10000).expireAfterWrite(10, TimeUnit.MINUTES).build();

		this.germplasmCropBasedCache = germplasmCropBasedCache;
	}

	/**
	 * @param cropName the crop name for which we wish to cache germplasm
	 * @param germplasms the list of germplasm for with we want to cache germplasm and their ancestry
	 * @param numberOfLevels the number of levels to cache
	 * @return 
	 */
	public Map<Integer, Germplasm> initialisesCache(final String cropName, final Set<Integer> germplasms, final int numberOfLevels) {
		final List<Germplasm> germplasmWithPrefNameAndAncestry =
				germplasmDataManager.getGermplasmWithAllNamesAndAncestry(germplasms, numberOfLevels);
		final Map<Integer, Germplasm> resultsetMap = new HashMap<>();
		for (Germplasm germplasm : germplasmWithPrefNameAndAncestry) {
			germplasmCache.put(new CropGermplasmKey(cropName, germplasm.getGid()), germplasm);
			resultsetMap.put(germplasm.getGid(), germplasm);
		}
		return resultsetMap;
	}

	/**
	 * @param germplasmKey key used to retrieve a germplasm
	 * @return the appropriate germplasm
	 */
	public Optional<Germplasm> getGermplasm(final CropGermplasmKey germplasmKey) {
		return germplasmCropBasedCache.get(germplasmKey);
	}

	
	/**
	 * @return the cache housing the germplasm. Only exposing for testing purposes.
	 */
	Cache<CropGermplasmKey, Germplasm> getGermplasmCache() {
		return germplasmCache;
	}
	
	

}
