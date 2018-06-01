
package org.generationcp.middleware.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.reports.Reporter;
import org.generationcp.middleware.service.api.ReportService;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.beans.factory.annotation.Autowired;

@Ignore
@RunWith(JUnit4.class)
public class ReportServiceIntegrationTest extends IntegrationTestBase {

	@Autowired
	private ReportService reportService;

	private static final int PROJECT_ID = -2; // local nursery;
	private static final String KEY_MAIZE_FIELDBOOK_NURSERY = "MFbNur";
	private static final String KEY_MAIZE_FIELDBOOK = "MFbTrial";
	private static final String KEY_MAIZE_FIELDBOOK_SHIPM = "MFbShipList";

	private static final int PROJECT_WHEAT_ID = -2; // local study;
	private static final int PROJECT_WHEAT_CROSSES_ID = -8; // local study;
	private static final String KEY_WHEAT_FIELDBOOK_23 = "WFb23";
	private static final String KEY_WHEAT_FIELDBOOK_24 = "WFb24";
	private static final String KEY_WHEAT_FIELDBOOK_25 = "WFb25";
	private static final String KEY_WHEAT_FIELDBOOK_26 = "WFb26";
	private static final String KEY_WHEAT_FIELDBOOK_28 = "WFb28";
	private static final String KEY_WHEAT_FIELDBOOK_29 = "WFb29";
	private static final String KEY_WHEAT_FIELDBOOK_41 = "WFb41";
	private static final String KEY_WHEAT_FIELDBOOK_42 = "WFb42";
	private static final String KEY_WHEAT_FIELDBOOK_43 = "WFb43";
	private static final String KEY_WHEAT_FIELDBOOK_47 = "WFb47";
	private static final String KEY_WHEAT_FIELDBOOK_60 = "WFb60";
	private static final String KEY_WHEAT_FIELDBOOK_61 = "WFb61";
	private static final String KEY_WHEAT_TAGS_04 = "WTAG04";
	private static final String KEY_WHEAT_TAGS_22 = "WTAG22";
	private static final String KEY_WHEAT_LABELS_05 = "WLBL05";
	private static final String KEY_WHEAT_LABELS_21 = "WLBL21";

	// TODO create separate class for testing keys
	// TODO create separate class for testing total number of parameters by report type

	@Test
	public void testGetReportKeys() {
		Assert.assertTrue(this.reportService.getReportKeys().size() > 0);
	}

	@Test
	public void testGetStreamReport_MaizeNursery() {
		this.assertReportGenerated(ReportServiceIntegrationTest.PROJECT_ID, ReportServiceIntegrationTest.KEY_MAIZE_FIELDBOOK_NURSERY);
	}

	@Test
	public void testGetStreamReport_Maize() {
		this.assertReportGenerated(ReportServiceIntegrationTest.PROJECT_ID, ReportServiceIntegrationTest.KEY_MAIZE_FIELDBOOK);

	}

	@Test
	public void testGetStreamReport_ShipList() {
		this.assertReportGenerated(ReportServiceIntegrationTest.PROJECT_ID, ReportServiceIntegrationTest.KEY_MAIZE_FIELDBOOK_SHIPM);

	}

	@Test
	public void testGetStreamReport_WheatFb23() {
		this.assertReportGenerated(ReportServiceIntegrationTest.PROJECT_WHEAT_CROSSES_ID,
				ReportServiceIntegrationTest.KEY_WHEAT_FIELDBOOK_23);
	}

	@Test
	public void testGetStreamReport_WheatFb24() {
		this.assertReportGenerated(ReportServiceIntegrationTest.PROJECT_WHEAT_ID, ReportServiceIntegrationTest.KEY_WHEAT_FIELDBOOK_24);
	}

	@Test
	public void testGetStreamReport_WheatFb25() {
		this.assertReportGenerated(ReportServiceIntegrationTest.PROJECT_WHEAT_ID, ReportServiceIntegrationTest.KEY_WHEAT_FIELDBOOK_25);
	}

	@Test
	public void testGetStreamReport_WheatFb26() {
		this.assertReportGenerated(ReportServiceIntegrationTest.PROJECT_WHEAT_ID, ReportServiceIntegrationTest.KEY_WHEAT_FIELDBOOK_26);
	}

	@Test
	public void testGetStreamReport_WheatFb28() {
		this.assertReportGenerated(ReportServiceIntegrationTest.PROJECT_WHEAT_ID, ReportServiceIntegrationTest.KEY_WHEAT_FIELDBOOK_28);
	}

	@Test
	public void testGetStreamReport_WheatFb29() {
		this.assertReportGenerated(ReportServiceIntegrationTest.PROJECT_WHEAT_ID, ReportServiceIntegrationTest.KEY_WHEAT_FIELDBOOK_29);
	}

	@Test
	public void testGetStreamReport_WheatFb41() {
		this.assertReportGenerated(ReportServiceIntegrationTest.PROJECT_WHEAT_ID, ReportServiceIntegrationTest.KEY_WHEAT_FIELDBOOK_41);
	}

	@Test
	public void testGetStreamReport_WheatFb42() {
		this.assertReportGenerated(ReportServiceIntegrationTest.PROJECT_WHEAT_ID, ReportServiceIntegrationTest.KEY_WHEAT_FIELDBOOK_42);
	}

	@Test
	public void testGetStreamReport_WheatFb43() {
		this.assertReportGenerated(ReportServiceIntegrationTest.PROJECT_WHEAT_ID, ReportServiceIntegrationTest.KEY_WHEAT_FIELDBOOK_43);
	}

	@Test
	public void testGetStreamReport_WheatFb47() {
		this.assertReportGenerated(ReportServiceIntegrationTest.PROJECT_WHEAT_CROSSES_ID,
				ReportServiceIntegrationTest.KEY_WHEAT_FIELDBOOK_47);
	}

	@Test
	public void testGetStreamReport_WheatFb60() {
		this.assertReportGenerated(ReportServiceIntegrationTest.PROJECT_WHEAT_ID, ReportServiceIntegrationTest.KEY_WHEAT_FIELDBOOK_60);
	}

	@Test
	public void testGetStreamReport_WheatFb61() {
		this.assertReportGenerated(ReportServiceIntegrationTest.PROJECT_WHEAT_ID, ReportServiceIntegrationTest.KEY_WHEAT_FIELDBOOK_61);
	}

	@Test
	public void testGetStreamReport_WheatTag04() {
		this.assertReportGenerated(ReportServiceIntegrationTest.PROJECT_WHEAT_CROSSES_ID, ReportServiceIntegrationTest.KEY_WHEAT_TAGS_04);
	}

	@Test
	public void testGetStreamReport_WheatTag22() {
		this.assertReportGenerated(ReportServiceIntegrationTest.PROJECT_WHEAT_CROSSES_ID, ReportServiceIntegrationTest.KEY_WHEAT_TAGS_22);
	}

	@Test
	public void testGetStreamReport_WheatLabel05() {
		this.assertReportGenerated(ReportServiceIntegrationTest.PROJECT_WHEAT_CROSSES_ID, ReportServiceIntegrationTest.KEY_WHEAT_LABELS_05);
	}

	@Test
	public void testGetStreamReport_WheatLabel21() {
		this.assertReportGenerated(ReportServiceIntegrationTest.PROJECT_WHEAT_CROSSES_ID, ReportServiceIntegrationTest.KEY_WHEAT_LABELS_21);
	}

	/**
	 * Tests that a particular report is indeed created, given a studyId.
	 *
	 * @param studyId id of the test study
	 * @param reportCode specific report code to generate.
	 */
	private void assertReportGenerated(final Integer studyId, final String reportCode) {

		final boolean hasReportKey = this.reportService.getReportKeys().contains(reportCode);

		if (hasReportKey) {
			try {
				final ByteArrayOutputStream baos = new ByteArrayOutputStream();

				final Reporter rep = this.reportService.getStreamReport(reportCode, studyId, "testReportName", baos);

				Assert.assertTrue("Failed test - empty report for code [" + reportCode + "].", baos.size() > 0);

				// additionally creates the file in 'target' folder, for human validation ;)
				final File xlsx = new File("target", rep.getFileName());
				baos.writeTo(new FileOutputStream(xlsx));

			} catch (final Exception e) {
				e.printStackTrace();
				Assert.fail("Failed test - generate report with code [" + reportCode + "].");
			}
		}
	}

}
