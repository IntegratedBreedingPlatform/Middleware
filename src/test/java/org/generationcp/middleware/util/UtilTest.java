package org.generationcp.middleware.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class UtilTest {

	@Test
	public void testNullIfEmpty() {
		assertNull("It should be null",Util.nullIfEmpty(""));
		String value = "testValue";
		assertEquals("It should return the original value: "+value,
				value, Util.nullIfEmpty(value));
		assertNull("It should be null",null);
	}
	
	@Test
	public void testZeroIfNull() {
		assertEquals("It should be zero",
				0,Util.zeroIfNull(null).compareTo(0.0));
		Double value = 2.5;
		assertEquals("It should return the original value: "+value,
				0, Util.zeroIfNull(value).compareTo(value));
	}
}
