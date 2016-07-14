
package org.generationcp.middleware.manager.ontology;

import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.domain.ontology.Variable;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class VariableCacheTest {

	@Test
	public void testVariableCacheOperations() {

		ContextHolder.setCurrentCrop("maize");
		Assert.assertEquals(0, VariableCache.getCacheSize());

		Integer variable1Id = 1;
		Variable variable1 = new Variable();
		variable1.setId(variable1Id);

		VariableCache.addToCache(variable1Id, variable1);

		Assert.assertEquals(1, VariableCache.getCacheSize());
		Assert.assertEquals(variable1, VariableCache.getFromCache(variable1Id));

		// Another variable with same ID but different crop
		ContextHolder.setCurrentCrop("wheat");
		Integer variable2Id = variable1Id;
		Variable variable2 = new Variable();
		variable2.setId(variable2Id);

		VariableCache.addToCache(variable2Id, variable2);
		Assert.assertEquals(2, VariableCache.getCacheSize());
		Assert.assertEquals(variable2, VariableCache.getFromCache(variable2Id));

		// Test remove operations
		ContextHolder.setCurrentCrop("maize");

		VariableCache.removeFromCache(variable1Id);

		Assert.assertEquals(1, VariableCache.getCacheSize());
		Assert.assertNull(VariableCache.getFromCache(variable1Id));

		ContextHolder.setCurrentCrop("wheat");

		VariableCache.removeFromCache(variable2Id);
		Assert.assertEquals(0, VariableCache.getCacheSize());
		Assert.assertNull(VariableCache.getFromCache(variable2Id));

	}

	@Test(expected = IllegalStateException.class)
	public void testVariableCacheGetWithNoCurrentCrop() {

		ContextHolder.setCurrentCrop(null);
		VariableCache.getFromCache(1);
		Assert.fail("Expected IllegalStateException when current crop is unknown and invoking get from VariableCache");
	}

	@Test(expected = IllegalStateException.class)
	public void testVariableCacheAddWithNoCurrentCrop() {

		ContextHolder.setCurrentCrop(null);
		VariableCache.addToCache(1, new Variable());
		Assert.fail("Expected IllegalStateException when current crop is unknown and invoking add to VariableCache");
	}

	@Test(expected = IllegalStateException.class)
	public void testVariableCacheRemoveWithNoCurrentCrop() {

		ContextHolder.setCurrentCrop(null);
		VariableCache.removeFromCache(1);
		Assert.fail("Expected IllegalStateException when current crop is unknown and invoking remove from VariableCache");
	}

	@BeforeClass
	public static void init() {
		VariableCache.clearCache();
	}

	@AfterClass
	public static void cleanUp() {
		VariableCache.clearCache();
	}
}
