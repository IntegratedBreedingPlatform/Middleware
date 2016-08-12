
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
		
		final List<Integer> parsedIds = ListDataProjectTest.ldp.parsePedigreeDupeInformation();
		Assert.assertEquals("The parsed pedigree duplicates ids list's size should be 2", 2, parsedIds.size());
		Assert.assertTrue("The parsed pedigree duplicates ids list should contain 10 and 11", parsedIds.contains(10) && parsedIds.contains(11));
	}

	@Test
	public void testParsePedigreeRecip() {
		ListDataProjectTest.ldp.setDuplicate(ListDataProjectTest.TEST_PEDIGREE_RECIP_STRING);

		final List<Integer> parsedIds = ListDataProjectTest.ldp.parsePedigreeReciprocalInformation();
		Assert.assertEquals("The parsed pedigree reciprocals ids list's size should be 1", 1, parsedIds.size());
		Assert.assertTrue("The parsed pedigree reciprocals ids list should contain 8", parsedIds.contains(8));
	}

	@Test
	public void testParsePlotRecip() {
		ListDataProjectTest.ldp.setDuplicate(ListDataProjectTest.TEST_PLOT_RECIP_STRING);

		final List<Integer> parsedIds = ListDataProjectTest.ldp.parsePlotReciprocalInformation();
		Assert.assertEquals("The parsed plot reciprocals ids list's size should be 2", 2, parsedIds.size());
		Assert.assertTrue("The parsed plot reciprocals ids list should contain 6 and 11", parsedIds.contains(6) && parsedIds.contains(11));
	}

	@Test
	public void testParsePlotDuplicate() {
		ListDataProjectTest.ldp.setDuplicate(ListDataProjectTest.TEST_PLOT_DUPE_STRING);

		final List<Integer> parsedIds = ListDataProjectTest.ldp.parsePlotDupeInformation();
		Assert.assertEquals("The parsed plot duplicates ids list's size should be 2", 2, parsedIds.size());
		Assert.assertTrue("The parsed plot duplicates ids list should contain 2 and 4", parsedIds.contains(2) && parsedIds.contains(4));
	}

	@Test
	public void testParseDuplicateString() {
		final String duplicate = ListDataProjectTest.TEST_PEDIGREE_DUPE_STRING + " | " + ListDataProjectTest.TEST_PEDIGREE_RECIP_STRING
				+ " | " + ListDataProjectTest.TEST_PLOT_DUPE_STRING + " | " + ListDataProjectTest.TEST_PLOT_RECIP_STRING;
		
		//Duplicate string : "Pedigree Dupe: 10,11 | Pedigree Recip: 8 | Plot Dupe: 2,4 | Plot Recip: 6,11"
		ListDataProjectTest.ldp.setDuplicate(duplicate);
		
		// Test for pedigree dupes
		List<Integer> parsedIds = ListDataProjectTest.ldp.parseDuplicateString(ListDataProject.PEDIGREE_DUPE);
		Assert.assertEquals("The parsed pedigree duplicates ids list's size should be 2", 2, parsedIds.size());
		Assert.assertTrue("The parsed pedigree duplicates ids list should contain 10 and 11", parsedIds.contains(10) && parsedIds.contains(11));
		
		// Test for pedigree recips
		parsedIds = ListDataProjectTest.ldp.parseDuplicateString(ListDataProject.PEDIGREE_RECIP);
		
		Assert.assertEquals("The parsed pedigree reciprocals ids list's size should be 1", 1, parsedIds.size());
		Assert.assertTrue("The parsed pedigree reciprocals ids list should contain 8", parsedIds.contains(8));
		
		// Test for plot dupes
		parsedIds = ListDataProjectTest.ldp.parseDuplicateString(ListDataProject.PLOT_DUPE);
		Assert.assertEquals("The parsed plot duplicates ids list's size should be 2", 2, parsedIds.size());
		Assert.assertTrue("The parsed plot duplicates ids list should contain 2 and 4", parsedIds.contains(2) && parsedIds.contains(4));
		
		// Test for plot recips
		parsedIds = ListDataProjectTest.ldp.parseDuplicateString(ListDataProject.PLOT_RECIP);
		Assert.assertEquals("The parsed plot reciprocals ids list's size should be 2", 2, parsedIds.size());
		Assert.assertTrue("The parsed plot reciprocals ids list should contain 6 and 11", parsedIds.contains(6) && parsedIds.contains(11));
	}
}
