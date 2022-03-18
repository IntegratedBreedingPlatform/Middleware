package org.generationcp.middleware.service.impl.rpackage;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.rpackage.RCallDTO;
import org.generationcp.middleware.domain.rpackage.RPackageDTO;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.pojos.workbench.RCall;
import org.generationcp.middleware.pojos.workbench.RCallParameter;
import org.generationcp.middleware.pojos.workbench.RPackage;
import org.generationcp.middleware.service.api.rpackage.RPackageService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class RPackageServiceImplTest extends IntegrationTestBase {

	public static final int BOUND = 10;
	private RPackageService rPackageService;
	private WorkbenchDaoFactory workbenchDaoFactory;

	@Before
	public void init() {
		this.rPackageService = new RPackageServiceImpl(this.workbenchSessionProvider);
		this.workbenchDaoFactory = new WorkbenchDaoFactory(this.workbenchSessionProvider);
	}

	@Test
	public void testGetRCallsByPackageId() {

		final RPackage rPackage = new RPackage();
		rPackage.setEndpoint(RandomStringUtils.randomAlphanumeric(BOUND));
		rPackage.setDescription(RandomStringUtils.randomAlphanumeric(BOUND));
		this.workbenchDaoFactory.getRPackageDao().save(rPackage);

		final RCall rCall = new RCall();
		rCall.setrPackage(rPackage);
		rCall.setDescription(RandomStringUtils.randomAlphanumeric(BOUND));
		rCall.setAggregate(true);
		final RCallParameter rCallParameter = new RCallParameter();
		rCallParameter.setKey(RandomStringUtils.randomAlphanumeric(BOUND));
		rCallParameter.setValue(RandomStringUtils.randomAlphanumeric(BOUND));
		rCall.setrCallParameters(Arrays.asList(rCallParameter));
		this.workbenchDaoFactory.getRCallDao().save(rCall);

		final List<RCallDTO> rCalls = this.rPackageService.getRCallsByPackageId(rPackage.getId());

		Assert.assertEquals(1, rCalls.size());
		final RCallDTO savedRCall = rCalls.get(0);
		Assert.assertNotNull(savedRCall.getrCallId());
		Assert.assertEquals(rCall.getrPackage().getEndpoint(), savedRCall.getEndpoint());
		Assert.assertEquals(rCall.getDescription(), savedRCall.getDescription());
		Assert.assertEquals(rCall.isAggregate(), savedRCall.isAggregate());
		Assert.assertEquals(rCallParameter.getValue(), savedRCall.getParameters().get(rCallParameter.getKey()));

	}

	@Test
	public void testGetRPackageById() {

		final RPackage rPackage = new RPackage();
		rPackage.setEndpoint(RandomStringUtils.randomAlphanumeric(BOUND));
		rPackage.setDescription(RandomStringUtils.randomAlphanumeric(BOUND));
		this.workbenchDaoFactory.getRPackageDao().save(rPackage);

		final Optional<RPackageDTO> result = this.rPackageService.getRPackageById(rPackage.getId());

		Assert.assertTrue(result.isPresent());
		Assert.assertEquals(rPackage.getDescription(), result.get().getDescription());
		Assert.assertEquals(rPackage.getEndpoint(), result.get().getEndpoint());

	}

}
