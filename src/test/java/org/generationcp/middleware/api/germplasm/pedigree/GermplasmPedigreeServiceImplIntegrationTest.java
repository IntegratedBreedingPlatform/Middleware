package org.generationcp.middleware.api.germplasm.pedigree;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.germplasm.GermplasmDto;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.MethodType;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.Progenitor;
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

	private Integer DRVNM_ID;

	@Autowired
	private GermplasmPedigreeService germplasmPedigreeService;

	@Before
	public void setUp() {
		this.daoFactory = new DaoFactory(this.sessionProvder);
		this.derivativeMethod = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		this.generativeMethod = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), 2);
		this.maintenanceMethod = this.createBreedingMethod(MethodType.MAINTENANCE.getCode(), -1);
		this.userId = this.findAdminUser();
		this.DRVNM_ID = this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode("NAMES", "NAME", "DRVNM").getFldno();
	}

	@Test
	public void testGetGermplasmPedigreeTree_DerivativeAndIncludeDerivativeLinesIsFalse() {
		final Germplasm rootGermplasm = this.createGermplasm(this.derivativeMethod, -1, 0, 0, 0);
		final Germplasm parentGermplasm = this.createGermplasm(this.maintenanceMethod, -1,
			rootGermplasm.getGid(), rootGermplasm.getGid(), 0);
		final Germplasm germplasm = this.createGermplasm(this.maintenanceMethod, -1,
			rootGermplasm.getGid(), parentGermplasm.getGid(), 0);

		final GermplasmTreeNode rootNode = this.germplasmPedigreeService.getGermplasmPedigreeTree(germplasm.getGid(), 3, false);
		Assert.assertEquals(germplasm.getGid(), rootNode.getGid());
		Assert.assertNull(rootNode.getFemaleParentNode());
		Assert.assertNull(rootNode.getMaleParentNode());
		Assert.assertTrue(rootNode.getOtherProgenitors().isEmpty());
	}

	@Test
	public void testGetGermplasmPedigreeTree_DerivativeAndIncludeDerivativeLinesIsTrue() {
		final Germplasm ancestorGermplasm = this.createGermplasm(this.derivativeMethod, -1, 0, 0, 0);
		final Germplasm parentGermplasm = this.createGermplasm(this.maintenanceMethod, -1,
			ancestorGermplasm.getGid(), ancestorGermplasm.getGid(), 0);
		final Germplasm germplasm = this.createGermplasm(this.maintenanceMethod, -1,
			ancestorGermplasm.getGid(), parentGermplasm.getGid(), 0);

		final GermplasmTreeNode rootNode = this.germplasmPedigreeService.getGermplasmPedigreeTree(germplasm.getGid(), 3, true);
		Assert.assertEquals(germplasm.getGid(), rootNode.getGid());
		Assert.assertNull(rootNode.getFemaleParentNode());
		Assert.assertTrue(rootNode.getOtherProgenitors().isEmpty());
		Assert.assertNotNull(rootNode.getMaleParentNode());

		final GermplasmTreeNode parentNode = rootNode.getMaleParentNode();
		Assert.assertEquals(parentGermplasm.getGid(), parentNode.getGid());
		Assert.assertNull(parentNode.getFemaleParentNode());
		Assert.assertTrue(parentGermplasm.getOtherProgenitors().isEmpty());
		Assert.assertNotNull(parentNode.getMaleParentNode());

		final GermplasmTreeNode ancestorNode = parentNode.getMaleParentNode();
		Assert.assertEquals(ancestorGermplasm.getGid(), ancestorNode.getGid());
		Assert.assertNull(ancestorNode.getFemaleParentNode());
		Assert.assertNull(ancestorNode.getMaleParentNode());
		Assert.assertTrue(ancestorNode.getOtherProgenitors().isEmpty());
	}

	@Test
	public void testGetGermplasmPedigreeTree_CrossWithKnownParents() {
		final Germplasm femaleParent = this.createGermplasm(this.maintenanceMethod, -1,
			0, 0, 0);
		final Germplasm maleParent = this.createGermplasm(this.maintenanceMethod, -1,
			0, 0, 0);
		final Germplasm germplasm = this.createGermplasm(this.generativeMethod, 2,
			femaleParent.getGid(), maleParent.getGid(), 0);

		final GermplasmTreeNode rootNode = this.germplasmPedigreeService.getGermplasmPedigreeTree(germplasm.getGid(), 2, true);
		Assert.assertEquals(germplasm.getGid(), rootNode.getGid());
		Assert.assertNotNull(rootNode.getFemaleParentNode());
		Assert.assertNotNull(rootNode.getMaleParentNode());
		Assert.assertTrue(rootNode.getOtherProgenitors().isEmpty());

		final GermplasmTreeNode maleParentNode = rootNode.getMaleParentNode();
		Assert.assertEquals(maleParent.getGid(), maleParentNode.getGid());

		final GermplasmTreeNode femaleParentNode = rootNode.getFemaleParentNode();
		Assert.assertEquals(femaleParent.getGid(), femaleParentNode.getGid());
	}

	@Test
	public void testGetGermplasmPedigreeTree_PolyCross() {
		final Germplasm femaleParent = this.createGermplasm(this.maintenanceMethod, -1,
			0, 0, 0);
		final Germplasm maleParent1 = this.createGermplasm(this.maintenanceMethod, -1,
			0, 0, 0);
		final Germplasm maleParent2 = this.createGermplasm(this.maintenanceMethod, -1,
			0, 0, 0);
		final Germplasm germplasm = this.createGermplasm(this.generativeMethod, 3,
			femaleParent.getGid(), maleParent1.getGid(), 0);
		this.addProgenitor(germplasm, maleParent2);

		final GermplasmTreeNode rootNode = this.germplasmPedigreeService.getGermplasmPedigreeTree(germplasm.getGid(), 2, true);
		Assert.assertEquals(germplasm.getGid(), rootNode.getGid());
		Assert.assertNotNull(rootNode.getFemaleParentNode());
		Assert.assertNotNull(rootNode.getMaleParentNode());

		final GermplasmTreeNode maleParentNode = rootNode.getMaleParentNode();
		Assert.assertEquals(maleParent1.getGid(), maleParentNode.getGid());

		final GermplasmTreeNode femaleParentNode = rootNode.getFemaleParentNode();
		Assert.assertEquals(femaleParent.getGid(), femaleParentNode.getGid());

		Assert.assertEquals(1, rootNode.getOtherProgenitors().size());
		final GermplasmTreeNode maleParentNode2 = rootNode.getOtherProgenitors().get(0);
		Assert.assertEquals(maleParent2.getGid(), maleParentNode2.getGid());
	}

	@Test
	public void testGetGermplasmPedigreeTree_CrossWithUnknownMaleParent() {
		final Germplasm femaleParent = this.createGermplasm(this.maintenanceMethod, -1,
			0, 0, 0);
		final Germplasm germplasm = this.createGermplasm(this.generativeMethod, 2,
			femaleParent.getGid(), 0, 0);

		final GermplasmTreeNode rootNode = this.germplasmPedigreeService.getGermplasmPedigreeTree(germplasm.getGid(), 2, true);
		Assert.assertEquals(germplasm.getGid(), rootNode.getGid());
		Assert.assertNotNull(rootNode.getFemaleParentNode());
		Assert.assertNotNull(rootNode.getMaleParentNode());
		Assert.assertTrue(rootNode.getOtherProgenitors().isEmpty());

		final GermplasmTreeNode maleParentNode = rootNode.getMaleParentNode();
		Assert.assertEquals(new Integer(0), maleParentNode.getGid());

		final GermplasmTreeNode femaleParentNode = rootNode.getFemaleParentNode();
		Assert.assertEquals(femaleParent.getGid(), femaleParentNode.getGid());
	}

	@Test
	public void testCountGenerations_DerivativeAndIncludeDerivativeLinesIsFalse() {
		final Germplasm rootGermplasm = this.createGermplasm(this.derivativeMethod, -1, 0, 0, 0);
		final Germplasm parentGermplasm = this.createGermplasm(this.maintenanceMethod, -1,
			rootGermplasm.getGid(), rootGermplasm.getGid(), 0);
		final Germplasm germplasm = this.createGermplasm(this.maintenanceMethod, -1,
			rootGermplasm.getGid(), parentGermplasm.getGid(), 0);

		final Integer numberOfGenerations = this.germplasmPedigreeService.countGenerations(germplasm.getGid(), false, true);
		Assert.assertEquals(new Integer(1), numberOfGenerations);
	}

	@Test
	public void testCountGenerations_DerivativeAndIncludeDerivativeLinesIsTrue() {
		final Germplasm ancestorGermplasm = this.createGermplasm(this.derivativeMethod, -1, 0, 0, 0);
		final Germplasm parentGermplasm = this.createGermplasm(this.maintenanceMethod, -1,
			ancestorGermplasm.getGid(), ancestorGermplasm.getGid(), 0);
		final Germplasm germplasm = this.createGermplasm(this.maintenanceMethod, -1,
			ancestorGermplasm.getGid(), parentGermplasm.getGid(), 0);

		final Integer numberOfGenerations = this.germplasmPedigreeService.countGenerations(germplasm.getGid(), true, true);
		Assert.assertEquals(new Integer(3), numberOfGenerations);
	}

	@Test
	public void testCountGenerations_CrossWithKnownParents() {
		final Germplasm femaleParent = this.createGermplasm(this.maintenanceMethod, -1,
			0, 0, 0);
		final Germplasm maleParent = this.createGermplasm(this.maintenanceMethod, -1,
			0, 0, 0);
		final Germplasm germplasm = this.createGermplasm(this.generativeMethod, 2,
			femaleParent.getGid(), maleParent.getGid(), 0);

		final Integer numberOfGenerations = this.germplasmPedigreeService.countGenerations(germplasm.getGid(), false, true);
		Assert.assertEquals(new Integer(2), numberOfGenerations);
	}

	@Test
	public void testCountGenerations_PolyCross() {
		final Germplasm femaleParent = this.createGermplasm(this.maintenanceMethod, -1,
			0, 0, 0);
		final Germplasm maleParent1 = this.createGermplasm(this.maintenanceMethod, -1,
			0, 0, 0);
		final Germplasm maleParent2 = this.createGermplasm(this.maintenanceMethod, -1,
			0, 0, 0);
		final Germplasm germplasm = this.createGermplasm(this.generativeMethod, 3,
			femaleParent.getGid(), maleParent1.getGid(), 0);
		this.addProgenitor(germplasm, maleParent2);

		final Integer numberOfGenerations = this.germplasmPedigreeService.countGenerations(germplasm.getGid(), false, true);
		Assert.assertEquals(new Integer(2), numberOfGenerations);
	}

	@Test
	public void testCountGenerations_CrossWithUnknownMaleParent() {
		final Germplasm femaleParent = this.createGermplasm(this.maintenanceMethod, -1,
			0, 0, 0);
		final Germplasm germplasm = this.createGermplasm(this.generativeMethod, 2,
			femaleParent.getGid(), 0, 0);

		final Integer numberOfGenerations = this.germplasmPedigreeService.countGenerations(germplasm.getGid(), false, true);
		Assert.assertEquals(new Integer(2), numberOfGenerations);
	}

	@Test
	public void testGetMaintenanceNeighborhood_Success() {
		final Germplasm rootGermplasm = this.createGermplasm(this.maintenanceMethod, -1, 0, 0, 0);
		final Germplasm germplasm = this.createGermplasm(this.maintenanceMethod, -1,
			rootGermplasm.getGid(), rootGermplasm.getGid(), 0);

		this.sessionProvder.getSession().flush();

		final GermplasmNeighborhoodNode rootNode = this.germplasmPedigreeService.getGermplasmMaintenanceNeighborhood(germplasm.getGid(),
			1, 2);
		Assert.assertEquals(rootGermplasm.getGid(), rootNode.getGid());
		Assert.assertEquals(1, rootNode.getLinkedNodes().size());

		final GermplasmNeighborhoodNode germplasmNode = rootNode.getLinkedNodes().get(0);
		Assert.assertEquals(germplasm.getGid(), germplasmNode.getGid());
		Assert.assertTrue(germplasmNode.getLinkedNodes().isEmpty());
	}

	@Test
	public void testGetDerivativeNeighborhood_Success() {
		final Germplasm rootGermplasm = this.createGermplasm(this.derivativeMethod, -1, 0, 0, 0);
		final Germplasm germplasm = this.createGermplasm(this.derivativeMethod, -1,
			rootGermplasm.getGid(), rootGermplasm.getGid(), 0);

		this.sessionProvder.getSession().flush();

		final GermplasmNeighborhoodNode rootNode =
			this.germplasmPedigreeService.getGermplasmDerivativeNeighborhood(germplasm.getGid(), 1, 2);
		Assert.assertEquals(rootGermplasm.getGid(), rootNode.getGid());
		Assert.assertEquals(1, rootNode.getLinkedNodes().size());

		final GermplasmNeighborhoodNode germplasmNode = rootNode.getLinkedNodes().get(0);
		Assert.assertEquals(germplasm.getGid(), germplasmNode.getGid());
		Assert.assertTrue(germplasmNode.getLinkedNodes().isEmpty());
	}

	@Test
	public void testGetGenerationHistory_Success() {
		final Germplasm parentGermplasm = this.createGermplasm(this.derivativeMethod, -1, 0, 0, 0);
		final Germplasm germplasm = this.createGermplasm(this.derivativeMethod, -1,
			parentGermplasm.getGid(), parentGermplasm.getGid(), 0);

		final List<GermplasmDto> generationHistory = this.germplasmPedigreeService.getGenerationHistory(germplasm.getGid());
		Assert.assertEquals(2, generationHistory.size());
		Assert.assertEquals(generationHistory.get(0).getGid(), germplasm.getGid());
		Assert.assertEquals(generationHistory.get(1).getGid(), parentGermplasm.getGid());
	}

	@Test
	public void testGetManagementNeighbors_Success() {
		final Germplasm germplasm = this.createGermplasm(this.derivativeMethod, -1, 0, 0, 0);
		final Germplasm managementNeighbor = this.createGermplasm(this.derivativeMethod, -1,
			germplasm.getGid(), germplasm.getGid(), germplasm.getGid());

		final List<GermplasmDto> managementNeighbors = this.germplasmPedigreeService.getManagementNeighbors(germplasm.getGid());
		Assert.assertEquals(1, managementNeighbors.size());
		Assert.assertEquals(managementNeighbors.get(0).getGid(), managementNeighbor.getGid());
	}

	@Test
	public void testGetGroupRelatives_Success() {
		final Germplasm parentGermplasm = this.createGermplasm(this.derivativeMethod, -1, 0, 0, 0);
		final Germplasm germplasm = this.createGermplasm(this.derivativeMethod, -1,
			parentGermplasm.getGid(), parentGermplasm.getGid(), 0);
		final Germplasm relative = this.createGermplasm(this.derivativeMethod, -1,
			parentGermplasm.getGid(), parentGermplasm.getGid(), 0);

		final List<GermplasmDto> groupRelatives = this.germplasmPedigreeService.getGroupRelatives(germplasm.getGid());
		Assert.assertEquals(1, groupRelatives.size());
		Assert.assertEquals(groupRelatives.get(0).getGid(), relative.getGid());
	}

	private Germplasm createGermplasm(final Method method, final Integer gnpgs,
		final Integer gpid1, final Integer gpid2, final Integer mgid) {
		final Germplasm germplasm = new Germplasm(null, gnpgs, gpid1, gpid2, 0, 20201212, 0,
			0, mgid, null, null, method);
		this.daoFactory.getGermplasmDao().save(germplasm);
		this.sessionProvder.getSession().flush();
		this.daoFactory.getGermplasmDao().refresh(germplasm);

		//Add preferred name
		final Name name =
			new Name(null, germplasm, this.DRVNM_ID, 1, RandomStringUtils.randomAlphabetic(10), 0, 20201212, 0);
		this.daoFactory.getNameDao().save(name);
		this.sessionProvder.getSession().flush();
		this.daoFactory.getNameDao().refresh(name);

		return germplasm;
	}

	private Method createBreedingMethod(final String breedingMethodType, final int numberOfProgenitors) {
		final Method method =
			new Method(null, breedingMethodType, "G", RandomStringUtils.randomAlphanumeric(5).toUpperCase(),
				RandomStringUtils.randomAlphanumeric(10),
				RandomStringUtils.randomAlphanumeric(10), 0, numberOfProgenitors, 1, 0, 1490, 1, 0, 19980708);
		this.daoFactory.getMethodDAO().save(method);
		this.sessionProvder.getSession().flush();
		this.daoFactory.getMethodDAO().refresh(method);
		return method;
	}

	private void addProgenitor(final Germplasm son, final Germplasm parent) {
		final Progenitor progenitor = new Progenitor(son, 3, parent.getGid());
		this.daoFactory.getProgenitorDao().save(progenitor);
		this.sessionProvder.getSession().flush();
		this.daoFactory.getProgenitorDao().refresh(progenitor);
	}

}
