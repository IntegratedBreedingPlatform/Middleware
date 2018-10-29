package org.generationcp.middleware.domain.dms;

import org.junit.Assert;
import org.junit.Test;

public class DataSetTypeTest {
	
	@Test
	public void testIsSubObservationDatasetType() {
		Assert.assertFalse(DataSetType.isSubObservationDatasetType(DataSetType.STUDY_CONDITIONS));
		Assert.assertFalse(DataSetType.isSubObservationDatasetType(DataSetType.MEANS_DATA));
		Assert.assertFalse(DataSetType.isSubObservationDatasetType(DataSetType.SUMMARY_DATA));
		Assert.assertFalse(DataSetType.isSubObservationDatasetType(DataSetType.PLOT_DATA));
		Assert.assertTrue(DataSetType.isSubObservationDatasetType(DataSetType.PLANT_SUBOBSERVATIONS));
		Assert.assertTrue(DataSetType.isSubObservationDatasetType(DataSetType.QUADRAT_SUBOBSERVATIONS));
		Assert.assertTrue(DataSetType.isSubObservationDatasetType(DataSetType.TIME_SERIES_SUBOBSERVATIONS));
		Assert.assertTrue(DataSetType.isSubObservationDatasetType(DataSetType.CUSTOM_SUBOBSERVATIONS));
	}

}