
package org.generationcp.middleware.pojos;

import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ListDataProjectTest {

	public static final String TEST_PEDIGREE_DUPE_STRING = ListDataProject.PEDIGREE_DUPE + ": 10,11";
	public static final String TEST_PEDIGREE_RECIP_STRING = ListDataProject.PEDIGREE_RECIP + ": 8";
	public static final String TEST_PLOT_DUPE_STRING = ListDataProject.PLOT_DUPE + ": 2,4";
	public static final String TEST_PLOT_RECIP_STRING = ListDataProject.PLOT_RECIP + ": 6,11";

	private static ListDataProject ldp;

	@BeforeClass
	public static void beforeClass() {
		ListDataProjectTest.ldp = new ListDataProject();
	}

	@Test
	public void testParsePedigreeDupe() {
		ListDataProjectTest.ldp.setDuplicate(ListDataProjectTest.TEST_PEDIGREE_DUPE_STRING);

		final List<Integer> parsed = ListDataProjectTest.ldp.parsePedigreeDupeInformation();
		Assert.assertEquals("The parsed list's size should be 2", 2, parsed.size());
	}

	@Test
	public void testParsePedigreeRecip() {
		ListDataProjectTest.ldp.setDuplicate(ListDataProjectTest.TEST_PEDIGREE_RECIP_STRING);

		final List<Integer> parsed = ListDataProjectTest.ldp.parsePedigreeReciprocalInformation();
		Assert.assertEquals("The parsed list's size should be 1", 1, parsed.size());
	}

	@Test
	public void testParsePlotRecip() {
		ListDataProjectTest.ldp.setDuplicate(ListDataProjectTest.TEST_PLOT_RECIP_STRING);

		final List<Integer> parsed = ListDataProjectTest.ldp.parsePlotReciprocalInformation();
		Assert.assertEquals("The parsed list's size should be 2", 2, parsed.size());
	}

	@Test
	public void testParsePlotDuplicate() {
		ListDataProjectTest.ldp.setDuplicate(ListDataProjectTest.TEST_PLOT_DUPE_STRING);

		final List<Integer> parsed = ListDataProjectTest.ldp.parsePlotDupeInformation();
		Assert.assertEquals("The parsed list's size should be 2", 2, parsed.size());
	}

	@Test
	public void testParseDuplicateString() {
		final String duplicate = ListDataProjectTest.TEST_PEDIGREE_DUPE_STRING + " | " + ListDataProjectTest.TEST_PEDIGREE_RECIP_STRING
				+ " | " + ListDataProjectTest.TEST_PLOT_DUPE_STRING + " | " + ListDataProjectTest.TEST_PLOT_RECIP_STRING;
		ListDataProjectTest.ldp.setDuplicate(duplicate);

		// Test for pedigree dupes
		List<Integer> parsed = ListDataProjectTest.ldp.parseDuplicateString(ListDataProject.PEDIGREE_DUPE);
		Assert.assertEquals("The parsed list's size should be 2", 2, parsed.size());

		// Test for pedigree recips
		parsed = ListDataProjectTest.ldp.parseDuplicateString(ListDataProject.PEDIGREE_RECIP);
		Assert.assertEquals("The parsed list's size should be 1", 1, parsed.size());

		// Test for plot dupes
		parsed = ListDataProjectTest.ldp.parseDuplicateString(ListDataProject.PLOT_DUPE);
		Assert.assertEquals("The parsed list's size should be 2", 2, parsed.size());

		// Test for plot recips
		parsed = ListDataProjectTest.ldp.parseDuplicateString(ListDataProject.PLOT_RECIP);
		Assert.assertEquals("The parsed list's size should be 2", 2, parsed.size());
	}
}
