package org.generationcp.middleware.api.crop;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class CropServiceImplIntegrationTest extends IntegrationTestBase {

	@Autowired
	private CropService cropService;

	private WorkbenchDaoFactory workbenchDaoFactory;

	@Before
	public void setUp() throws Exception {
		this.workbenchDaoFactory = new WorkbenchDaoFactory(this.workbenchSessionProvider);
	}

	@Test
	public void testCropType() {
		final String cropName = "Coconut";
		final CropType cropType = new CropType(cropName);
		this.workbenchDaoFactory.getCropTypeDAO().saveOrUpdate(cropType);

		final List<CropType> cropTypes = this.cropService.getInstalledCropDatabases();
		Assert.assertNotNull(cropTypes);
		Assert.assertTrue(cropTypes.size() >= 1);

		final CropType cropTypeRead = this.cropService.getCropTypeByName(cropName);
		Assert.assertNotNull(cropTypeRead);
		Assert.assertEquals(cropType, cropTypeRead);
	}

}
