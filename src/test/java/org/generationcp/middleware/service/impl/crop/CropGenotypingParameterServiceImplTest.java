package org.generationcp.middleware.service.impl.crop;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.crop.CropGenotypingParameterService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.hamcrest.Matchers.is;

public class CropGenotypingParameterServiceImplTest extends IntegrationTestBase {

	@Autowired
	private CropGenotypingParameterService cropGenotypingParameterService;

	private WorkbenchDaoFactory workbenchDaoFactory;

	@Before
	public void setUp() {
		this.workbenchDaoFactory = new WorkbenchDaoFactory(this.workbenchSessionProvider);
	}

	@Test
	public void testCreateCropGenotypingParameter() {

		final CropGenotypingParameterDTO cropGenotypingParameterDTO = this.createCropGenotypingParameterDTO();

		final CropType cropType = new CropType();
		cropType.setCropName(cropGenotypingParameterDTO.getCropName());
		this.workbenchDaoFactory.getCropTypeDAO().save(cropType);

		this.cropGenotypingParameterService.createCropGenotypingParameter(cropGenotypingParameterDTO);

		final Optional<CropGenotypingParameterDTO> cropGenotypingParameterDTOOptional =
			this.cropGenotypingParameterService.getCropGenotypingParameter(cropGenotypingParameterDTO.getCropName());

		Assert.assertTrue(cropGenotypingParameterDTOOptional.isPresent());
		final CropGenotypingParameterDTO created = cropGenotypingParameterDTOOptional.get();
		Assert.assertThat(created.getCropName(), is(cropGenotypingParameterDTO.getCropName()));
		Assert.assertThat(created.getEndpoint(), is(cropGenotypingParameterDTO.getEndpoint()));
		Assert.assertThat(created.getTokenEndpoint(), is(cropGenotypingParameterDTO.getTokenEndpoint()));
		Assert.assertThat(created.getUserName(), is(cropGenotypingParameterDTO.getUserName()));
		Assert.assertThat(created.getPassword(), is(cropGenotypingParameterDTO.getPassword()));
		Assert.assertThat(created.getProgramId(), is(cropGenotypingParameterDTO.getProgramId()));

	}

	@Test
	public void testUpdateCropGenotypingParameter() {

		final CropGenotypingParameterDTO cropGenotypingParameterDTO = this.createCropGenotypingParameterDTO();

		final CropType cropType = new CropType();
		cropType.setCropName(cropGenotypingParameterDTO.getCropName());
		this.workbenchDaoFactory.getCropTypeDAO().save(cropType);

		this.cropGenotypingParameterService.createCropGenotypingParameter(cropGenotypingParameterDTO);

		cropGenotypingParameterDTO.setCropName(cropGenotypingParameterDTO.getCropName());
		cropGenotypingParameterDTO.setEndpoint("updated endpoint");
		cropGenotypingParameterDTO.setTokenEndpoint("updated token endpoint");
		cropGenotypingParameterDTO.setUserName("updated username");
		cropGenotypingParameterDTO.setPassword("updated password");
		cropGenotypingParameterDTO.setProgramId("updated program id");

		this.cropGenotypingParameterService.updateCropGenotypingParameter(cropGenotypingParameterDTO);

		final Optional<CropGenotypingParameterDTO> cropGenotypingParameterDTOOptional =
			this.cropGenotypingParameterService.getCropGenotypingParameter(cropGenotypingParameterDTO.getCropName());

		Assert.assertTrue(cropGenotypingParameterDTOOptional.isPresent());
		final CropGenotypingParameterDTO updated = cropGenotypingParameterDTOOptional.get();
		Assert.assertThat(updated.getCropName(), is(cropGenotypingParameterDTO.getCropName()));
		Assert.assertThat(updated.getEndpoint(), is(cropGenotypingParameterDTO.getEndpoint()));
		Assert.assertThat(updated.getTokenEndpoint(), is(cropGenotypingParameterDTO.getTokenEndpoint()));
		Assert.assertThat(updated.getUserName(), is(cropGenotypingParameterDTO.getUserName()));
		Assert.assertThat(updated.getPassword(), is(cropGenotypingParameterDTO.getPassword()));
		Assert.assertThat(updated.getProgramId(), is(cropGenotypingParameterDTO.getProgramId()));

	}

	private CropGenotypingParameterDTO createCropGenotypingParameterDTO() {
		final CropGenotypingParameterDTO cropGenotypingParameterDTO = new CropGenotypingParameterDTO();
		cropGenotypingParameterDTO.setCropName(RandomStringUtils.randomAlphabetic(10));
		cropGenotypingParameterDTO.setEndpoint(RandomStringUtils.randomAlphanumeric(10));
		cropGenotypingParameterDTO.setTokenEndpoint(RandomStringUtils.randomAlphanumeric(10));
		cropGenotypingParameterDTO.setUserName(RandomStringUtils.randomAlphanumeric(10));
		cropGenotypingParameterDTO.setPassword(RandomStringUtils.randomAlphanumeric(10));
		cropGenotypingParameterDTO.setProgramId(RandomStringUtils.randomAlphanumeric(10));
		return cropGenotypingParameterDTO;
	}

}
