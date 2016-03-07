
package org.generationcp.middleware.domain.cache;

import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.pojos.Method;
import org.junit.Assert;
import org.junit.Test;

public class BreedingMethodCacheTest {

	@Test
	public void testBreedingMethodCacheOperations() {

		ContextHolder.setCurrentCrop("maize");
		Assert.assertEquals(0, BreedingMethodCache.getCacheSize());

		Integer method1Id = 1;
		Method method1 = new Method();
		method1.setMid(method1Id);

		BreedingMethodCache.addToCache(method1Id, method1);

		Assert.assertEquals(1, BreedingMethodCache.getCacheSize());
		Assert.assertEquals(method1, BreedingMethodCache.getFromCache(method1Id));

		// Another variable with same ID but different crop
		ContextHolder.setCurrentCrop("wheat");
		Integer method2Id = method1Id;
		Method method2 = new Method();
		method2.setMid(method2Id);

		BreedingMethodCache.addToCache(method2Id, method2);
		Assert.assertEquals(2, BreedingMethodCache.getCacheSize());
		Assert.assertEquals(method2, BreedingMethodCache.getFromCache(method2Id));

		// Test remove operations
		ContextHolder.setCurrentCrop("maize");

		BreedingMethodCache.removeFromCache(method1Id);

		Assert.assertEquals(1, BreedingMethodCache.getCacheSize());
		Assert.assertNull(BreedingMethodCache.getFromCache(method1Id));

		ContextHolder.setCurrentCrop("wheat");

		BreedingMethodCache.removeFromCache(method2Id);
		Assert.assertEquals(0, BreedingMethodCache.getCacheSize());
		Assert.assertNull(BreedingMethodCache.getFromCache(method2Id));

	}

	@Test(expected = IllegalStateException.class)
	public void testBreedingMethodCacheGetWithNoCurrentCrop() {

		ContextHolder.setCurrentCrop(null);
		BreedingMethodCache.getFromCache(1);
		Assert.fail("Expected IllegalStateException when current crop is unknown and invoking get from BreedingMethodCache");
	}

	@Test(expected = IllegalStateException.class)
	public void testBreedingMethodCacheAddWithNoCurrentCrop() {

		ContextHolder.setCurrentCrop(null);
		BreedingMethodCache.addToCache(1, new Method());
		Assert.fail("Expected IllegalStateException when current crop is unknown and invoking add to BreedingMethodCache");
	}

	@Test(expected = IllegalStateException.class)
	public void testBreedingMethodCacheRemoveWithNoCurrentCrop() {

		ContextHolder.setCurrentCrop(null);
		BreedingMethodCache.removeFromCache(1);
		Assert.fail("Expected IllegalStateException when current crop is unknown and invoking remove from BreedingMethodCache");
	}
}
