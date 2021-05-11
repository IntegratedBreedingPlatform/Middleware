package org.generationcp.middleware.api.germplasm.pedigree;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.germplasm.GermplasmDto;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.MethodType;
import org.generationcp.middleware.pojos.Name;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class GermplasmPedigreeServiceImplIntegrationTest extends IntegrationTestBase {

	private DaoFactory daoFactory;

	private Method derivativeMethod;

	private Method generativeMethod;

	private Method maintenanceMethod;

	private Integer userId;
	@Autowired
	private GermplasmPedigreeService germplasmPedigreeService;

	@Before
	public void setUp() {
		this.daoFactory = new DaoFactory(this.sessionProvder);
		this.derivativeMethod = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		this.generativeMethod = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), 2);
		this.maintenanceMethod = this.createBreedingMethod(MethodType.MAINTENANCE.getCode(), -1);
		this.userId = this.findAdminUser();
	}

	@Test
	public void testGetGenerationHistory_Success() {
		final Germplasm parentGermplasm = this.createGermplasm(this.derivativeMethod, null, -1, 0, 0, 0);
		final Germplasm germplasm = this.createGermplasm(this.derivativeMethod, null, -1,
			parentGermplasm.getGid(), parentGermplasm.getGid(), 0);

		final List<GermplasmDto> generationHistory = this.germplasmPedigreeService.getGenerationHistory(germplasm.getGid());
		Assert.assertEquals(2, generationHistory.size());
		Assert.assertEquals(generationHistory.get(0).getGid(), germplasm.getGid());
		Assert.assertEquals(generationHistory.get(1).getGid(), parentGermplasm.getGid());
	}

	@Test
	public void testGetManagementNeighbors_Success() {
		final Germplasm germplasm = this.createGermplasm(this.derivativeMethod, null, -1, 0, 0, 0);
		final Germplasm managementNeighbor = this.createGermplasm(this.derivativeMethod, null, -1,
			germplasm.getGid(), germplasm.getGid(), germplasm.getGid());

		final List<GermplasmDto> managementNeighbors = this.germplasmPedigreeService.getManagementNeighbors(germplasm.getGid());
		Assert.assertEquals(1, managementNeighbors.size());
		Assert.assertEquals(managementNeighbors.get(0).getGid(), managementNeighbor.getGid());
	}

	@Test
	public void testGetGroupRelatives_Success() {
		final Germplasm parentGermplasm = this.createGermplasm(this.derivativeMethod, null, -1, 0, 0, 0);
		final Germplasm germplasm = this.createGermplasm(this.derivativeMethod, null, -1,
			parentGermplasm.getGid(), parentGermplasm.getGid(), 0);
		final Germplasm relative = this.createGermplasm(this.derivativeMethod, null, -1,
			parentGermplasm.getGid(), parentGermplasm.getGid(), 0);

		final List<GermplasmDto> groupRelatives = this.germplasmPedigreeService.getGroupRelatives(germplasm.getGid());
		Assert.assertEquals(1, groupRelatives.size());
		Assert.assertEquals(groupRelatives.get(0).getGid(), relative.getGid());
	}

	private Germplasm createGermplasm(final Method method, final String germplasmUUID, final Integer gnpgs,
		final Integer gpid1, final Integer gpid2, final Integer mgid) {
		final Germplasm germplasm = new Germplasm(null, method.getMid(), gnpgs, gpid1, gpid2,
			1, 0, 0, 20201212, 0,
			0, mgid, null, null, method);
		if (StringUtils.isNotEmpty(germplasmUUID)) {
			germplasm.setGermplasmUUID(germplasmUUID);
		}
		this.daoFactory.getGermplasmDao().save(germplasm);
		return germplasm;
	}

	private Method createBreedingMethod(final String breedingMethodType, final int numberOfProgenitors) {
		final Method method =
			new Method(null, breedingMethodType, "G", RandomStringUtils.randomAlphanumeric(5).toUpperCase(),
				RandomStringUtils.randomAlphanumeric(10),
				RandomStringUtils.randomAlphanumeric(10), 0, numberOfProgenitors, 1, 0, 1490, 1, 0, 19980708, "");
		this.daoFactory.getMethodDAO().save(method);
		this.sessionProvder.getSession().flush();
		this.daoFactory.getMethodDAO().refresh(method);
		return method;
	}

}
