package org.generationcp.middleware.pojos;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Daniel Villafuerte on 5/14/2015.
 */

@RunWith(JUnit4.class)
public class ListDataProjectTest {
    public static final String TEST_PEDIGREE_DUPE_STRING = "Pedigree Dupe: 10,11";
    public static final String TEST_PEDIGREE_RECIP_STRING = "Pedigree Recip: 8";
    public static final String TEST_PLOT_DUPE_STRING = "Plot Dupe: 2,4";
    public static final String TEST_PLOT_RECIP_STRING = "Plot Recip: 6,11";

    @Test
    public void testParsePedigreeDupe() {
        ListDataProject ldp = new ListDataProject();
        ldp.setDuplicate(TEST_PEDIGREE_DUPE_STRING);

        List<Integer> parsed = ldp.parsePedigreeDupeInformation();
        assertTrue(parsed.size() == 2);
    }

    @Test
    public void testParsePedigreeRecip() {
        ListDataProject ldp = new ListDataProject();
        ldp.setDuplicate(TEST_PEDIGREE_RECIP_STRING);

        List<Integer> parsed = ldp.parsePedigreeDupeInformation();
        assertTrue(parsed.size() == 1);
    }

    @Test
    public void testParsePlotRecip() {
        ListDataProject ldp = new ListDataProject();
        ldp.setDuplicate(TEST_PLOT_RECIP_STRING);

        List<Integer> parsed = ldp.parsePedigreeDupeInformation();
        assertTrue(parsed.size() == 2);
    }

    @Test
    public void testParsePlotDuplicate() {
        ListDataProject ldp = new ListDataProject();
        ldp.setDuplicate(TEST_PLOT_DUPE_STRING);

        List<Integer> parsed = ldp.parsePedigreeDupeInformation();
        assertTrue(parsed.size() == 2);
    }

}
