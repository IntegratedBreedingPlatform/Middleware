
package org.generationcp.middleware.pojos;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class ListDataProjectTest {

	public static final String TEST_PEDIGREE_DUPE_STRING = ListDataProject.PEDIGREE_DUPE + ": 10,11";
	public static final String TEST_PEDIGREE_RECIP_STRING = ListDataProject.PEDIGREE_RECIP + ": 8";
	public static final String TEST_PLOT_DUPE_STRING = ListDataProject.PLOT_DUPE + ": 2,4";
	public static final String TEST_PLOT_RECIP_STRING = ListDataProject.PLOT_RECIP + ": 6,11";

	@Test
	public void testParsePedigreeDupe() {
		ListDataProject ldp = new ListDataProject();
		ldp.setDuplicate(ListDataProjectTest.TEST_PEDIGREE_DUPE_STRING);

		List<Integer> parsed = ldp.parsePedigreeDupeInformation();
		Assert.assertEquals(2, parsed.size());
	}

	@Test
	public void testParsePedigreeRecip() {
		ListDataProject ldp = new ListDataProject();
		ldp.setDuplicate(ListDataProjectTest.TEST_PEDIGREE_RECIP_STRING);

		List<Integer> parsed = ldp.parsePedigreeReciprocalInformation();
		Assert.assertEquals(1, parsed.size());
	}

	@Test
	public void testParsePlotRecip() {
		ListDataProject ldp = new ListDataProject();
		ldp.setDuplicate(ListDataProjectTest.TEST_PLOT_RECIP_STRING);

		List<Integer> parsed = ldp.parsePlotReciprocalInformation();
		Assert.assertEquals(2, parsed.size());
	}

	@Test
	public void testParsePlotDuplicate() {
		ListDataProject ldp = new ListDataProject();
		ldp.setDuplicate(ListDataProjectTest.TEST_PLOT_DUPE_STRING);

		List<Integer> parsed = ldp.parsePlotDupeInformation();
		Assert.assertEquals(2, parsed.size());
	}

}
