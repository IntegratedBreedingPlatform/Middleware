package org.generationcp.middleware.dao;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.pojos.CropParameter;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public class CropParameterDAOTest extends IntegrationTestBase {

	private CropParameterDAO cropParameterDAO;
	private static final String SECRET_PHRASE = "33##samplePhrase@iopweirut!";

	private CropParameter cropParameterTest;
	private CropParameter cropParameterEncrypted;
	private static final String TEST_PARAM_KEY = "DUMMY_URL";
	private static final String TEST_ENCRYPTED_PARAM_KEY = "DUMMY_PASSWORD";
	private static final String TEST_ENCRYPTED_PARAM_VALUE = "password";
	private static final String TEST_GROUP_NAME = "sampleGroup";

	@Before
	public void setUp() throws Exception {
		this.cropParameterDAO = new CropParameterDAO(this.sessionProvder.getSession());

		if (this.cropParameterTest == null && this.cropParameterEncrypted == null) {
			this.createCropParameters();
		}
	}

	private void createCropParameters() {
		final CropParameter newCropParameter = new CropParameter();
		newCropParameter.setKey(TEST_PARAM_KEY);
		newCropParameter.setValue("https://dummy-url.com");
		newCropParameter.setDescription("dummy url crop parameter for integration test");
		newCropParameter.setGroupName(TEST_GROUP_NAME);
		newCropParameter.setIsEncrypted(false);

		this.cropParameterTest = this.cropParameterDAO.save(newCropParameter);

		final CropParameter encCropParameter = new CropParameter();
		encCropParameter.setKey(TEST_ENCRYPTED_PARAM_KEY);
		encCropParameter.setDescription("dummy password parameter for integration test");
		encCropParameter.setGroupName(TEST_GROUP_NAME);
		encCropParameter.setIsEncrypted(true);

		this.cropParameterEncrypted = this.cropParameterDAO.save(encCropParameter);
	}

	@Test
	public void testGetAllCropParameters() {
		List<CropParameter> cropParamList = this.cropParameterDAO.getAllCropParameters(new PageRequest(0, 10), SECRET_PHRASE);
		MatcherAssert.assertThat("Expected list of crop parameters size > zero",
			cropParamList != null && cropParamList.size() > 0);
	}

	@Test
	public void testGetCropParametersByGroupName() {
		List<CropParameter> cropParamList = this.cropParameterDAO.getCropParametersByGroupName(new PageRequest(0, 10), SECRET_PHRASE,
			TEST_GROUP_NAME);
		MatcherAssert.assertThat("Expected list of crop parameters size = 2 given the group name",
			cropParamList != null && cropParamList.size() == 2);
	}

	@Test
	public void testGetCropParameterByKey() {
		CropParameter cropParameter = this.cropParameterDAO.getCropParameterByKey(TEST_PARAM_KEY, SECRET_PHRASE);
		Assert.assertNotNull(cropParameter);
		Assert.assertEquals(this.cropParameterTest.getValue(), cropParameter.getValue());
	}

	@Test
	public void testGetCropParameterByKey_Encrypted() {
		this.sessionProvder.getSession().flush();
		this.sessionProvder.getSession().refresh(this.cropParameterEncrypted);
		this.cropParameterDAO.updateEncryptedValue(TEST_ENCRYPTED_PARAM_KEY, TEST_ENCRYPTED_PARAM_VALUE, SECRET_PHRASE);

		CropParameter cropParameter = this.cropParameterDAO.getCropParameterByKey(TEST_ENCRYPTED_PARAM_KEY, SECRET_PHRASE);
		Assert.assertNotNull(cropParameter);
		Assert.assertEquals(TEST_ENCRYPTED_PARAM_VALUE, cropParameter.getEncryptedValue());
	}
}
