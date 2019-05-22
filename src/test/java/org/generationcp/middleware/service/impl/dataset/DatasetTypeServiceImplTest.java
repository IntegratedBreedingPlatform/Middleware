package org.generationcp.middleware.service.impl.dataset;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.dms.DatasetTypeDTO;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

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
		final Map<Integer, DatasetTypeDTO> datasetTypeMap = this.datasetTypeService.getAllDatasetTypes();
		Assert.assertEquals(11, datasetTypeMap.size());
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
	}

}
