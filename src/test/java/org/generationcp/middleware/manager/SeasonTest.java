
package org.generationcp.middleware.manager;

import org.generationcp.middleware.domain.oms.TermId;
import org.junit.Assert;
import org.junit.Test;

public class SeasonTest {

	@Test
	public void testNullSeasonRetrieval() {
		Season season = Season.getSeason(null);

		Assert.assertEquals("Season retrieval unable to properly handle null string", Season.GENERAL, season);
	}

	@Test
	public void testBlankSeasonRetrieval() {
		Season season = Season.getSeason("");

		Assert.assertEquals("Season retrieval unable to properly handle blank string", Season.GENERAL, season);
	}

	@Test
	public void testUnknownSeasonRetrieval() {
		Season season = Season.getSeason("12345");

		Assert.assertEquals("Season retrieval unable to properly handle undefined id", Season.GENERAL, season);
	}

	@Test
	public void testDrySeasonRetrieval() {
		Season season = Season.getSeason(Integer.toString(TermId.SEASON_DRY.getId()));

		Assert.assertEquals("Season retrieval unable to properly dry season ID", Season.DRY, season);
	}

	@Test
	public void testWetSeasonRetrieval() {
		Season season = Season.getSeason(Integer.toString(TermId.SEASON_WET.getId()));

		Assert.assertEquals("Season retrieval unable to properly dry season ID", Season.WET, season);
	}
}
