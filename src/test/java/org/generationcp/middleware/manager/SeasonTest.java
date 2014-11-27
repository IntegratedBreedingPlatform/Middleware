package org.generationcp.middleware.manager;

import org.generationcp.middleware.domain.oms.TermId;
import org.junit.Test;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte
 * Date: 11/18/2014
 * Time: 2:14 PM
 */
@RunWith(JUnit4ClassRunner.class)
public class SeasonTest {

	@Test
	public void testNullSeasonRetrieval() {
		Season season = Season.getSeason(null);

		assertEquals("Season retrieval unable to properly handle null string", Season.GENERAL, season);
	}

	@Test
	public void testBlankSeasonRetrieval() {
		Season season = Season.getSeason("");

		assertEquals("Season retrieval unable to properly handle blank string", Season.GENERAL,
				season);
	}

	@Test
	public void testUnknownSeasonRetrieval() {
		Season season = Season.getSeason("12345");

		assertEquals("Season retrieval unable to properly handle undefined id", Season.GENERAL,
				season);
	}

	@Test
	public void testDrySeasonRetrieval() {
		Season season = Season.getSeason(Integer.toString(TermId.SEASON_DRY.getId()));

		assertEquals("Season retrieval unable to properly dry season ID", Season.DRY,
				season);
	}

	@Test
	public void testWetSeasonRetrieval() {
		Season season = Season.getSeason(Integer.toString(TermId.SEASON_WET.getId()));

		assertEquals("Season retrieval unable to properly dry season ID", Season.WET,
				season);
	}
}
