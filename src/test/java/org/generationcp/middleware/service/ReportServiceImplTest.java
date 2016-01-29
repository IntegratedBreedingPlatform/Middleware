
package org.generationcp.middleware.service;

import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.junit.Assert;
import org.junit.Test;

public class ReportServiceImplTest {

	private final ReportServiceImpl reportService = new ReportServiceImpl();

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
}
