package org.generationcp.middleware.domain.dms;

import org.junit.Assert;
import org.junit.Test;

public class ValueReferenceTest {

	public static final String TEST_NAME = "test_name";
	public static final String TEST_DESCRIPTION = "test_description";
	public static final String SEPARATOR = "= ";
	public static final String TEST_NAME_WITH_DESCRIPTION = "test_name = description";

	@Test
	public void testGetDisplayDescription() throws Exception {
		ValueReference testValueRef = new ValueReference("test_key", TEST_NAME, TEST_DESCRIPTION);
		Assert.assertEquals("should return name=description format",TEST_NAME + SEPARATOR + TEST_DESCRIPTION,testValueRef.getDisplayDescription());
	}

	@Test
	public void testGetDisplayDescriptionAlreadyInAEqXFormat() throws Exception {
		ValueReference testValueRef = new ValueReference("test_key", TEST_NAME, TEST_NAME_WITH_DESCRIPTION);
		Assert.assertEquals("should just return the description",TEST_NAME_WITH_DESCRIPTION,testValueRef.getDisplayDescription());
	}
}
