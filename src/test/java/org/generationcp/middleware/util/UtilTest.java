
package org.generationcp.middleware.util;

import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;

public class UtilTest {

	@Test
	public void testNullIfEmpty() {
		Assert.assertNull("It should be null", Util.nullIfEmpty(""));
		String value = "testValue";
		Assert.assertEquals("It should return the original value: " + value, value, Util.nullIfEmpty(value));
		Assert.assertNull("It should be null", null);
	}

	@Test
	public void testZeroIfNull() {
		Assert.assertEquals("It should be zero", 0, Util.zeroIfNull(null).compareTo(0.0));
		Double value = 2.5;
		Assert.assertEquals("It should return the original value: " + value, 0, Util.zeroIfNull(value).compareTo(value));
	}

	@Test
	public void testPrependToCSV() {
		String valueToPrepend = "SID1-1";
		String csv = "SID1-2, SID1-3";
		String expectedValue = "SID1-1, SID1-2, SID1-3";
		Assert.assertEquals("It should return " + expectedValue, expectedValue, Util.prependToCSV(valueToPrepend, csv));
	}

	@Test
	public void testConvertCollectionToCSV() {
		Set<String> valueSet = new TreeSet<String>();
		valueSet.add("SID1-1");
		valueSet.add("SID1-2");
		valueSet.add("SID1-4");
		String expectedValue = "SID1-1, SID1-2, SID1-4";
		Assert.assertEquals("It should return " + expectedValue, expectedValue, Util.convertCollectionToCSV(valueSet));
	}

	@Test
	public void testPrependToCSVAndArrange() {
		String valueToPrepend = "SID1-2";
		String csv = "SID1-1, SID1-4";
		String expectedValue = "SID1-1, SID1-2, SID1-4";
		Assert.assertEquals("It should return " + expectedValue, expectedValue, Util.prependToCSVAndArrange(valueToPrepend, csv));
	}

	@Test
	public void testTryParse() {
		Assert.assertThat(Util.tryParseDate(null), is(nullValue()));
		Assert.assertThat(Util.tryParseDate(null, Util.FRONTEND_DATE_FORMAT_2), is(nullValue()));
		Assert.assertThat(Util.tryParseDate("2015-08-18", Util.FRONTEND_DATE_FORMAT), is(notNullValue()));
		Assert.assertThat(Util.tryParseDate("20150818"), is(notNullValue()));
	}

	@Test
	public void testIsValidDate() {
		Assert.assertTrue(Util.isValidDate("2015-08-18"));
		Assert.assertTrue(Util.isValidDate("20150818"));
		Assert.assertFalse(Util.isValidDate("99999999"));
		Assert.assertFalse(Util.isValidDate("123456789"));
		Assert.assertFalse(Util.isValidDate(StringUtils.EMPTY));
		Assert.assertFalse(Util.isValidDate(null));
	}

	@Test
	public void testConvertDate() {
		Assert.assertThat(Util.tryConvertDate(null, Util.FRONTEND_DATE_FORMAT, Util.DATE_AS_NUMBER_FORMAT), is(nullValue()));
		Assert.assertThat(Util.tryConvertDate("2015-08-18", Util.FRONTEND_DATE_FORMAT, Util.DATE_AS_NUMBER_FORMAT), is("20150818"));
		Assert.assertThat(Util.tryConvertDate("20150818", Util.DATE_AS_NUMBER_FORMAT, Util.FRONTEND_DATE_FORMAT), is("2015-08-18"));
		Assert.assertThat(Util.tryConvertDate("20150818", Util.FRONTEND_DATE_FORMAT, Util.DATE_AS_NUMBER_FORMAT), is(nullValue()));
		Assert.assertThat(Util.tryConvertDate("2015-08-18", Util.DATE_AS_NUMBER_FORMAT, Util.FRONTEND_DATE_FORMAT), is(nullValue()));
	}
}
