
package org.generationcp.middleware.reports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class WLabels05Test {

	private WLabels05 wLabels05;
	private List<List<String>> rows;
	private List<String> headers;
	private static final int COL_SPAN = 20;
	private Map<String, String> studyMeta;

	@Before
	public void setUp() {
		this.wLabels05 = new WLabels05();
		this.initStudyMeta();
		this.initRows();
		this.initHeaders();
	}

	private void initStudyMeta() {
		this.studyMeta = new HashMap<String, String>();
		this.studyMeta.put("Study_UID", "1");
		this.studyMeta.put("START_DATE", "20151201");
		this.studyMeta.put("STUDY_BM_CODE", "");
		this.studyMeta.put("STUDY_TYPE", "Nursery");
		this.studyMeta.put("TRIAL_INSTANCE", "1");
		this.studyMeta.put("STUDY_BMETH", "");
		this.studyMeta.put("STUDY_OBJECTIVE", "Nursery 002 Objective");
		this.studyMeta.put("STUDY_UPDATE", "20151201");
		this.studyMeta.put("STUDY_NAME", "Nursery 002");
		this.studyMeta.put("STUDY_TITLE", "Nursery 002");

		this.wLabels05.setStudyMeta(this.studyMeta);
	}

	private void initHeaders() {
		this.headers = Arrays.asList("GID", "DESIGNATION", "ENTRY_NO", "PLOT_NO", "CROSS");
	}

	private void initRows() {
		this.rows = new ArrayList<List<String>>();

		final String[] row = {"1", "TEST1", "1", "1", "-"};
		final String[] row2 = {"2", "TEST2", "2", "2", "-"};
		this.rows.add(Arrays.asList(row));
		this.rows.add(Arrays.asList(row2));
	}

	@Test
	public void testBuildRecord() {
		final String result = this.wLabels05.buildRecord(this.rows, this.headers, COL_SPAN);
		final int noOfRows = this.rows.size();

		Assert.assertEquals(
				"Expecting that the no of STUDY_NAME string is equal to the no of rows of the germplasm list used in the study. ",
				noOfRows, this.noOfMatches(result, this.studyMeta.get("STUDY_NAME")));

		Assert.assertEquals("Expecting that the OCC string appears " + noOfRows + " times.", noOfRows,
				this.noOfMatches(result, "OCC: " + this.studyMeta.get("TRIAL_INSTANCE")));

		Assert.assertEquals(
				"Expecting that the no of \"Local Check\" strings is equal to the no of rows of the germplasm list used in the study. ",
				noOfRows, this.noOfMatches(result, "Local Check"));

		Assert.assertEquals(
				"Expecting that the no of \"CIMMYT\" strings is equal to the no of rows of the germplasm list used in the study. ",
				noOfRows, this.noOfMatches(result, "CIMMYT"));
	}

	private int noOfMatches(final String result, final String patternStr) {
		final Pattern pattern = Pattern.compile(patternStr);
		final Matcher matcher = pattern.matcher(result);
		int noOfMatches = 0;
		while (matcher.find()) {
			noOfMatches++;
		}
		return noOfMatches;
	}
}
