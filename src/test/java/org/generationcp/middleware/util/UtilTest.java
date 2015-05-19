package org.generationcp.middleware.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Set;
import java.util.TreeSet;

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
	
	@Test
	public void testPrependToCSV() {
		String valueToPrepend = "SID1-1";
		String csv = "SID1-2, SID1-3";
		String expectedValue = "SID1-1, SID1-2, SID1-3";
		assertEquals("It should return "+expectedValue,expectedValue,
				Util.prependToCSV(valueToPrepend, csv));
	}
	
	@Test
	public void testConvertCollectionToCSV() {
		Set<String> valueSet = new TreeSet<String>();
		valueSet.add("SID1-1");
		valueSet.add("SID1-2");
		valueSet.add("SID1-4");
		String expectedValue = "SID1-1, SID1-2, SID1-4";
		assertEquals("It should return "+expectedValue,expectedValue,
				Util.convertCollectionToCSV(valueSet));
	}
	
	@Test
	public void testPrependToCSVAndArrange() {
		String valueToPrepend = "SID1-2";
		String csv = "SID1-1, SID1-4";
		String expectedValue = "SID1-1, SID1-2, SID1-4";
		assertEquals("It should return "+expectedValue,expectedValue,
				Util.prependToCSVAndArrange(valueToPrepend, csv));
	}
}
