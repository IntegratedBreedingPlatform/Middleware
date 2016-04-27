
package org.generationcp.middleware.service;

import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.reports.AbstractReporter;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class ReportServiceImplTest {

	private final ReportServiceImpl reportService = new ReportServiceImpl();

	public static final int TEST_GERMPLASM_LIST_ID = 2;

	@Test
	public void testRetrieveLocationIdFromConditionReturnsNullForEmptyString() {
		final MeasurementVariable condition = new MeasurementVariable();
		condition.setTermId(TermId.LOCATION_ID.getId());
		condition.setValue("");
		Assert.assertNull("Expecting a null for location id when the value of LOCATION ID variable is empty string.",
				this.reportService.retrieveLocationIdFromCondition(condition, TermId.LOCATION_ID));
	}

	@Test
	public void testRetrieveLocationIdFromConditionReturnsAnIntegerForValidNumber() {
		final MeasurementVariable condition = new MeasurementVariable();
		condition.setTermId(TermId.LOCATION_ID.getId());
		final int locationId = 1;
		condition.setValue(String.valueOf(locationId));
		Assert.assertEquals("Expecting an integer for location id when the value of LOCATION ID variable is a valid number > 0.",
				locationId, this.reportService.retrieveLocationIdFromCondition(condition, TermId.LOCATION_ID).intValue());
	}

	@Test
	public void testExtractGermplasmListData() {
		Map<String, Object> data = reportService.extractGermplasmListData(TEST_GERMPLASM_LIST_ID);
		Assert.assertNotNull("Data extracter does not provide non null value for required report generation parameter",
				data.get(AbstractReporter.STUDY_CONDITIONS_KEY));
        Assert.assertNotNull("Data extracter does not provide non null value for required report generation parameter",
                data.get(AbstractReporter.DATA_SOURCE_KEY));
	}
}
