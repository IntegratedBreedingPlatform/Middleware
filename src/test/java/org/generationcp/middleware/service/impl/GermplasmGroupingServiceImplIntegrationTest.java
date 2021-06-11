package org.generationcp.middleware.service.impl;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.service.api.GermplasmGroup;
import org.generationcp.middleware.service.api.GermplasmGroupingService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class GermplasmGroupingServiceImplIntegrationTest extends IntegrationTestBase  {

	@Autowired
	private GermplasmGroupingService germplasmGroupingService;

	private DaoFactory daoFactory;

	private String creationDate;
	private Integer userId;

	@Before
	public void setup() {
		this.daoFactory = new DaoFactory(this.sessionProvder);
		this.userId = this.findAdminUser();
		this.creationDate = "20201212";
	}

	@Test
	public void testMarkFixed_Derivative(){
		final Germplasm germplasm1 = this.createGermplasm("DER", -1, 0, 0);
		this.daoFactory.getGermplasmDao().save(germplasm1);
		final Germplasm germplasm2 =this.createGermplasm("DER", -1, germplasm1.getGid(), 0);
		this.daoFactory.getGermplasmDao().save(germplasm2);
		final List<GermplasmGroup> germplasmGroups =
			this.germplasmGroupingService.markFixed(Arrays.asList(germplasm1.getGid(), germplasm2.getGid()), false, false);
		Assert.assertEquals(2, germplasmGroups.size());
		this.verifyGermplasmGroupWasFixed(germplasm1,germplasmGroups.get(0));
		this.verifyGermplasmGroupWasFixed(germplasm2,germplasmGroups.get(1));
	}

	@Test
	public void testMarkFixed_Generative(){
		final Germplasm germplasm1 = this.createGermplasm("GEN", 2, 0, 0);
		this.daoFactory.getGermplasmDao().save(germplasm1);
		final Germplasm germplasm2 =this.createGermplasm("GEN", 2, germplasm1.getGid(), 0);
		this.daoFactory.getGermplasmDao().save(germplasm2);
		final List<GermplasmGroup> germplasmGroups =
			this.germplasmGroupingService.markFixed(Arrays.asList(germplasm1.getGid(), germplasm2.getGid()), false, false);
		Assert.assertEquals(2, germplasmGroups.size());
		this.verifyGermplasmGroupWasNotFixed(germplasm1,germplasmGroups.get(0));
		this.verifyGermplasmGroupWasNotFixed(germplasm2,germplasmGroups.get(1));
	}

	private void verifyGermplasmGroupWasFixed(final Germplasm germplasm, final GermplasmGroup germplasmGroup) {
		Assert.assertEquals(germplasm.getGid(), germplasmGroup.getFounderGid());
		Assert.assertEquals(germplasm.getGid(), germplasmGroup.getGroupId());
		Assert.assertFalse(germplasmGroup.isGenerative());
		// Verify getting germplasm anew that MGID was updated
		final Germplasm latestGermplasm = this.daoFactory.getGermplasmDao().getById(germplasm.getGid());
		Assert.assertEquals(germplasmGroup.getGroupId(), latestGermplasm.getMgid());
		Assert.assertEquals(1, germplasmGroup.getGroupMembers().size());
		Assert.assertEquals(germplasm.getGid(), germplasmGroup.getGroupMembers().get(0).getGid());
		Assert.assertEquals(germplasm.getPreferredName().getNval(), germplasmGroup.getGroupMembers().get(0).getPreferredName());
	}

	private void verifyGermplasmGroupWasNotFixed(final Germplasm germplasm, final GermplasmGroup germplasmGroup) {
		Assert.assertEquals(germplasm.getGid(), germplasmGroup.getFounderGid());
		Assert.assertEquals(0, germplasmGroup.getGroupId().intValue());
		Assert.assertTrue(germplasmGroup.isGenerative());
		// Verify getting germplasm anew that MGID was not updated
		final Germplasm latestGermplasm = this.daoFactory.getGermplasmDao().getById(germplasm.getGid());
		Assert.assertEquals(0, latestGermplasm.getMgid().intValue());
		Assert.assertEquals(0, germplasmGroup.getGroupMembers().size());
	}

	private Germplasm createGermplasm(final String breedingMethodType, final Integer gnpgs,
		final Integer gpid1, final Integer gpid2) {
		final Method method = this.createBreedingMethod(breedingMethodType);
		final Germplasm germplasm = new Germplasm(null, method.getMid(), gnpgs, gpid1, gpid2,
			0, 0, Integer.parseInt(this.creationDate), 0,
			0, 0, null, null, method);

		this.daoFactory.getGermplasmDao().save(germplasm);
		final Name name = new Name(null, germplasm, 1, 1, RandomStringUtils.randomAlphabetic(20), 0, 0, 0);
		this.daoFactory.getNameDao().save(name);
		germplasm.setPreferredName(name);
		this.sessionProvder.getSession().flush();

		assertThat(germplasm.getCreatedBy(), is(this.userId));
		assertNotNull(germplasm.getCreatedBy());
		assertNull(germplasm.getModifiedBy());
		assertNull(germplasm.getModifiedDate());

		return germplasm;
	}

	private Method createBreedingMethod(final String breedingMethodType) {
		final Method method =
			new Method(null, breedingMethodType, "G", RandomStringUtils.randomAlphanumeric(4).toUpperCase(),
				RandomStringUtils.randomAlphanumeric(10),
				RandomStringUtils.randomAlphanumeric(10), 0, 1, 1, 0, 1490, 1, 0, 19980708, "");
		this.daoFactory.getMethodDAO().save(method);
		this.sessionProvder.getSession().flush();
		this.daoFactory.getMethodDAO().refresh(method);
		return method;
	}

}
