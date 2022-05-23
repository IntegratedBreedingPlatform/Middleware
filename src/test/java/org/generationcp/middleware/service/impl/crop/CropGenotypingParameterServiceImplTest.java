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
	public void testGetCropGenotypingParameter() {
		final CropGenotypingParameterDTO cropGenotypingParameterDTO = this.createCropGenotypingParameterDTO();

		final CropType cropType = new CropType();
		cropType.setCropName(cropGenotypingParameterDTO.getCropName());
		this.workbenchDaoFactory.getCropTypeDAO().save(cropType);

		this.cropGenotypingParameterService.createCropGenotypingParameter(cropGenotypingParameterDTO);

		final Optional<CropGenotypingParameterDTO> resultOptional =
			this.cropGenotypingParameterService.getCropGenotypingParameter(cropGenotypingParameterDTO.getCropName());

		Assert.assertTrue(resultOptional.isPresent());
		final CropGenotypingParameterDTO result = resultOptional.get();
		Assert.assertNotEquals(0, result.getGenotypingParameterId());
		Assert.assertThat(result.getCropName(), is(cropGenotypingParameterDTO.getCropName()));
		Assert.assertThat(result.getEndpoint(), is(cropGenotypingParameterDTO.getEndpoint()));
		Assert.assertThat(result.getTokenEndpoint(), is(cropGenotypingParameterDTO.getTokenEndpoint()));
		Assert.assertThat(result.getUserName(), is(cropGenotypingParameterDTO.getUserName()));
		Assert.assertThat(result.getPassword(), is(cropGenotypingParameterDTO.getPassword()));
		Assert.assertThat(result.getProgramId(), is(cropGenotypingParameterDTO.getProgramId()));
	}

	@Test
	public void testCreateCropGenotypingParameter() {

		final CropGenotypingParameterDTO cropGenotypingParameterDTO = this.createCropGenotypingParameterDTO();

		final CropType cropType = new CropType();
		cropType.setCropName(cropGenotypingParameterDTO.getCropName());
		this.workbenchDaoFactory.getCropTypeDAO().save(cropType);

		final CropGenotypingParameterDTO created =
			this.cropGenotypingParameterService.createCropGenotypingParameter(cropGenotypingParameterDTO);

		Assert.assertNotEquals(0, created.getGenotypingParameterId());
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

		final CropGenotypingParameterDTO created =
			this.cropGenotypingParameterService.createCropGenotypingParameter(cropGenotypingParameterDTO);

		created.setCropName(cropGenotypingParameterDTO.getCropName());
		created.setEndpoint("updated endpoint");
		created.setTokenEndpoint("updated token endpoint");
		created.setUserName("updated username");
		created.setPassword("updated password");
		created.setProgramId("updated program id");

		this.cropGenotypingParameterService.updateCropGenotypingParameter(created);

		final Optional<CropGenotypingParameterDTO> cropGenotypingParameterDTOOptional =
			this.cropGenotypingParameterService.getCropGenotypingParameter(cropGenotypingParameterDTO.getCropName());

		Assert.assertTrue(cropGenotypingParameterDTOOptional.isPresent());
		final CropGenotypingParameterDTO updated = cropGenotypingParameterDTOOptional.get();
		Assert.assertThat(updated.getGenotypingParameterId(), is(created.getGenotypingParameterId()));
		Assert.assertThat(updated.getCropName(), is(created.getCropName()));
		Assert.assertThat(updated.getEndpoint(), is(created.getEndpoint()));
		Assert.assertThat(updated.getTokenEndpoint(), is(created.getTokenEndpoint()));
		Assert.assertThat(updated.getUserName(), is(created.getUserName()));
		Assert.assertThat(updated.getPassword(), is(created.getPassword()));
		Assert.assertThat(updated.getProgramId(), is(created.getProgramId()));

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
