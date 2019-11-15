package org.generationcp.middleware.service.impl.rpackage;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.pojos.workbench.RCall;
import org.generationcp.middleware.pojos.workbench.RCallParameter;
import org.generationcp.middleware.pojos.workbench.RPackage;
import org.generationcp.middleware.service.api.rpackage.RPackageService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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
	public void testGetAllRCalls() {

		final List<RCall> rCalls = this.rPackageService.getAllRCalls();
		Assert.assertFalse(rCalls.isEmpty());

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
		rCall.setrCallParameters(new ArrayList<RCallParameter>());
		this.workbenchDaoFactory.getRCallDao().save(rCall);

		final List<RCall> rCalls = this.rPackageService.getRCallsByPackageId(rPackage.getId());

		Assert.assertEquals(1, rCalls.size());
		final RCall savedRCall = rCalls.get(0);
		Assert.assertEquals(rCall.getDescription(), savedRCall.getDescription());
		Assert.assertEquals(rCall.getrPackage(), savedRCall.getrPackage());

	}

}
