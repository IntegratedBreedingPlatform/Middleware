package org.generationcp.middleware.service.impl.dataset;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.dms.DatasetTypeDTO;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class DatasetTypeServiceImplTest extends IntegrationTestBase {

	private DatasetTypeServiceImpl datasetTypeService;

	@Before
	public void init() {
		this.datasetTypeService = new DatasetTypeServiceImpl(this.sessionProvder);
	}

	@Test
	public void testGetDatasetTypeById() {
		final DatasetTypeDTO datasetType = this.datasetTypeService.getDatasetTypeById(DatasetTypeEnum.SUMMARY_DATA.getId());
		Assert.assertEquals(DatasetTypeEnum.SUMMARY_DATA.getId(), datasetType.getDatasetTypeId().intValue());
	}

	@Test
	public void testGetAllDatasetTypes() {
		final Map<Integer, DatasetTypeDTO> datasetTypeMap = this.datasetTypeService.getAllDatasetTypesMap();
		Assert.assertEquals(12, datasetTypeMap.size());
		Assert.assertTrue(datasetTypeMap.containsKey(DatasetTypeEnum.STUDY_CONDITIONS.getId()));
		Assert.assertTrue(datasetTypeMap.containsKey(DatasetTypeEnum.MEANS_DATA.getId()));
		Assert.assertTrue(datasetTypeMap.containsKey(DatasetTypeEnum.SUMMARY_DATA.getId()));
		Assert.assertTrue(datasetTypeMap.containsKey(DatasetTypeEnum.PLOT_DATA.getId()));
		Assert.assertTrue(datasetTypeMap.containsKey(DatasetTypeEnum.PLANT_SUBOBSERVATIONS.getId()));
		Assert.assertTrue(datasetTypeMap.containsKey(DatasetTypeEnum.QUADRAT_SUBOBSERVATIONS.getId()));
		Assert.assertTrue(datasetTypeMap.containsKey(DatasetTypeEnum.TIME_SERIES_SUBOBSERVATIONS.getId()));
		Assert.assertTrue(datasetTypeMap.containsKey(DatasetTypeEnum.CUSTOM_SUBOBSERVATIONS.getId()));
		Assert.assertTrue(datasetTypeMap.containsKey(DatasetTypeEnum.SUB_SAMPLE_DATA.getId()));
		Assert.assertTrue(datasetTypeMap.containsKey(DatasetTypeEnum.WEATHER_DATA.getId()));
		Assert.assertTrue(datasetTypeMap.containsKey(DatasetTypeEnum.MEANS_OVER_TRIAL_INSTANCES.getId()));
		Assert.assertTrue(datasetTypeMap.containsKey(DatasetTypeEnum.SUMMARY_STATISTICS_DATA.getId()));
	}

	@Test
	public void testGetObservationDatasetTypeIds() {
		final List<Integer> observationDatasetTypeIds = this.datasetTypeService.getObservationDatasetTypeIds();
		Assert.assertEquals(observationDatasetTypeIds.size(), 6);
		Assert.assertTrue(observationDatasetTypeIds.contains(DatasetTypeEnum.MEANS_DATA.getId()));
		Assert.assertTrue(observationDatasetTypeIds.contains(DatasetTypeEnum.PLOT_DATA.getId()));
		Assert.assertTrue(observationDatasetTypeIds.contains(DatasetTypeEnum.PLANT_SUBOBSERVATIONS.getId()));
		Assert.assertTrue(observationDatasetTypeIds.contains(DatasetTypeEnum.QUADRAT_SUBOBSERVATIONS.getId()));
		Assert.assertTrue(observationDatasetTypeIds.contains(DatasetTypeEnum.TIME_SERIES_SUBOBSERVATIONS.getId()));
		Assert.assertTrue(observationDatasetTypeIds.contains(DatasetTypeEnum.CUSTOM_SUBOBSERVATIONS.getId()));
	}

	@Test
	public void testGetSubObservationDatasetTypeIds() {
		final List<Integer> subObservationDatasetTypeIds = this.datasetTypeService.getSubObservationDatasetTypeIds();
		Assert.assertEquals(subObservationDatasetTypeIds.size(), 4);
		Assert.assertTrue(subObservationDatasetTypeIds.contains(DatasetTypeEnum.PLANT_SUBOBSERVATIONS.getId()));
		Assert.assertTrue(subObservationDatasetTypeIds.contains(DatasetTypeEnum.QUADRAT_SUBOBSERVATIONS.getId()));
		Assert.assertTrue(subObservationDatasetTypeIds.contains(DatasetTypeEnum.TIME_SERIES_SUBOBSERVATIONS.getId()));
		Assert.assertTrue(subObservationDatasetTypeIds.contains(DatasetTypeEnum.CUSTOM_SUBOBSERVATIONS.getId()));

	}

	@Test
	public void testGetObservationLevels() {
		final List<String> observationLevels = this.datasetTypeService.getObservationLevels(1000, 0);
		final List<String> obsLevels = this.getObservationLevels(false);
		Assert.assertEquals(obsLevels.size(), observationLevels.size());
		Assert.assertTrue(obsLevels.contains(observationLevels.get(0)));
		Assert.assertTrue(obsLevels.contains(observationLevels.get(1)));
		Assert.assertTrue(obsLevels.contains(observationLevels.get(2)));
		Assert.assertTrue(obsLevels.contains(observationLevels.get(3)));
		Assert.assertTrue(obsLevels.contains(observationLevels.get(4)));
		Assert.assertTrue(obsLevels.contains(observationLevels.get(5)));
	}

	@Test
	public void testCountObservationLevels() {	
		final Long count = this.datasetTypeService.countObservationLevels();
		Assert.assertEquals(Math.round(count),  6);
	}

	public List<String> getObservationLevels(final boolean isSubObs) {
		List<String> levels = new ArrayList<>();
		levels.add("PLANT");
		levels.add("SUB-PLOT");
		levels.add("TIMESERIES");
		levels.add("CUSTOM");

		if(!isSubObs) {
			levels.add("MEANS");
			levels.add("PLOT");
		}
		return levels;
	}
}
