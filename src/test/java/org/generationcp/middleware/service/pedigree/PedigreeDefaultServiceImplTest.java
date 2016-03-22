
package org.generationcp.middleware.service.pedigree;

import java.util.Properties;

import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PedigreeDefaultServiceImplTest {

	private static int GERMPLASM_ID_TO_EXPAND = 99;

	public static int SINGLE_CROSS_METHOD_ID = 101;
	public static String SINGLE_CROSS_METHOD_NAME = "Single cross";

	public static int DOUBLE_CROSS_METHOD_ID = 103;
	public static String DOUBLE_CROSS_METHOD_NAME = "Double cross";

	public static int THREE_WAY_CROSS_METHOD_ID = 102;
	public static String THREE_WAY_CROSS_METHOD_NAME = "Three-way cross";

	public static int UNKNOWN_GENERATIVE_METHOD_ID = 1;
	public static String UNKNOWN_GENERATIVE_METHOD_NAME = "Unknown generative method";

	private Germplasm germplasmToExpand;
	private Germplasm firstParent;
	private Germplasm secondParent;
	private Germplasm firstGrandparent1;
	private Germplasm firstGrandparent2;
	private Germplasm secondGrandparent1;
	private Germplasm secondGrandparent2;

	private Germplasm firstGrandparent1firstParent;
	private Germplasm firstGrandparent1secondParent;
	private Germplasm firstGrandparent2firstParent;
	private Germplasm firstGrandparent2secondParent;

	private Germplasm secondGrandparent1firstParent;
	private Germplasm secondGrandparent1secondParent;
	private Germplasm secondGrandparent2firstParent;
	private Germplasm secondGrandparent2secondParent;

	@Mock
	private PedigreeDataManagerFactory pedigreeDataManagerFactory;

	@Mock
	private GermplasmDataManager germplasmDataManager;

	@InjectMocks
	private PedigreeDefaultServiceImpl pedigreeDefaultService = new PedigreeDefaultServiceImpl();

	@Before
	public void setUp() {
		Mockito.when(this.pedigreeDataManagerFactory.getGermplasmDataManager()).thenReturn(this.germplasmDataManager);
		Mockito.when(this.germplasmDataManager.getMethodByID(SINGLE_CROSS_METHOD_ID)).thenReturn(
				this.createMethod(SINGLE_CROSS_METHOD_ID, SINGLE_CROSS_METHOD_NAME));
		Mockito.when(this.germplasmDataManager.getMethodByID(UNKNOWN_GENERATIVE_METHOD_ID)).thenReturn(
				this.createMethod(UNKNOWN_GENERATIVE_METHOD_ID, UNKNOWN_GENERATIVE_METHOD_NAME));
		Mockito.when(this.germplasmDataManager.getMethodByID(DOUBLE_CROSS_METHOD_ID)).thenReturn(
				this.createMethod(DOUBLE_CROSS_METHOD_ID, DOUBLE_CROSS_METHOD_NAME));
		Mockito.when(this.germplasmDataManager.getMethodByID(THREE_WAY_CROSS_METHOD_ID)).thenReturn(
				this.createMethod(THREE_WAY_CROSS_METHOD_ID, THREE_WAY_CROSS_METHOD_NAME));
	}

	@Test
	public void getCrossExpansionUnsupportedMethodGermplasmHasPreferredName() {

		this.initializeGermplasmToExpand(UNKNOWN_GENERATIVE_METHOD_ID, 2);

		Mockito.when(this.germplasmDataManager.getGermplasmWithPrefName(GERMPLASM_ID_TO_EXPAND)).thenReturn(this.germplasmToExpand);

		CrossExpansionProperties crossExpansionProperties = this.createCrossExpansionProperties();

		String result = this.pedigreeDefaultService.getCrossExpansion(GERMPLASM_ID_TO_EXPAND, crossExpansionProperties);

		/**
		 * If the BREEDING METHOD NAME of the germplasm used has no "single cross", "three-way cross", "complex" and "cross","backcross" and
		 * "cross", Preferred Name will be returned if the germplasm has preferred name (Designation name)
		 */
		Assert.assertEquals("ABC-123", result);

	}

	@Test
	public void getCrossExpansionNoGermplasmFound() {

		Mockito.when(this.germplasmDataManager.getGermplasmWithPrefName(9999)).thenReturn(null);

		CrossExpansionProperties crossExpansionProperties = this.createCrossExpansionProperties();

		String result = this.pedigreeDefaultService.getCrossExpansion(9999, crossExpansionProperties);

		Assert.assertEquals("", result);

	}

	@Test
	public void getCrossExpansionUnknownParents() {

		this.initializeGermplasmToExpand(SINGLE_CROSS_METHOD_ID, 2);
		this.germplasmToExpand.setGpid1(0);
		this.germplasmToExpand.setGpid2(0);

		CrossExpansionProperties crossExpansionProperties = this.createCrossExpansionProperties();

		String result = this.pedigreeDefaultService.getCrossExpansion(GERMPLASM_ID_TO_EXPAND, crossExpansionProperties);

		Assert.assertEquals("Unknown/Unknown", result);

	}

	@Test
	public void getCrossExpansionSingleCrossLevel1() {

		this.initializeGermplasmToExpand(SINGLE_CROSS_METHOD_ID, 2);

		CrossExpansionProperties crossExpansionProperties = this.createCrossExpansionProperties();

		String result = this.pedigreeDefaultService.getCrossExpansion(GERMPLASM_ID_TO_EXPAND, crossExpansionProperties);

		Assert.assertEquals("XYZ-789/QRS-456", result);

	}

	@Test
	public void getCrossExpansionSingleCrossLevel2() {

		this.initializeGermplasmToExpand(SINGLE_CROSS_METHOD_ID, 2);

		CrossExpansionProperties crossExpansionProperties = this.createCrossExpansionProperties();
		crossExpansionProperties.setDefaultLevel(2);

		String result = this.pedigreeDefaultService.getCrossExpansion(GERMPLASM_ID_TO_EXPAND, crossExpansionProperties);

		Assert.assertEquals("CCC-333/DDD-444//AAA-111/BBB-222", result);

	}

	@Test
	public void getCrossExpansionSingleCrossLevel3() {

		this.initializeGermplasmToExpand(SINGLE_CROSS_METHOD_ID, 2);

		CrossExpansionProperties crossExpansionProperties = this.createCrossExpansionProperties();
		crossExpansionProperties.setDefaultLevel(3);

		String result = this.pedigreeDefaultService.getCrossExpansion(GERMPLASM_ID_TO_EXPAND, crossExpansionProperties);

		Assert.assertEquals("III-111/JJJ-222//KKK-111/KKK-222///EEE-111/FFF-222//GGG-111/HHH-222", result);

	}

	@Test
	public void getCrossExpansionDoubleCross() {

		this.initializeGermplasmToExpand(DOUBLE_CROSS_METHOD_ID, 2);

		CrossExpansionProperties crossExpansionProperties = this.createCrossExpansionProperties();
		crossExpansionProperties.setDefaultLevel(1);

		String result = this.pedigreeDefaultService.getCrossExpansion(GERMPLASM_ID_TO_EXPAND, crossExpansionProperties);

		Assert.assertEquals("CCC-333/DDD-444//AAA-111/BBB-222", result);

	}

	@Test
	public void getCrossExpansionThreeWayCrossCreatedByFirstParent() {

		this.initializeGermplasmToExpand(THREE_WAY_CROSS_METHOD_ID, 1);

		CrossExpansionProperties crossExpansionProperties = this.createCrossExpansionProperties();
		crossExpansionProperties.setDefaultLevel(1);

		String result = this.pedigreeDefaultService.getCrossExpansion(GERMPLASM_ID_TO_EXPAND, crossExpansionProperties);

		Assert.assertEquals("CCC-333/DDD-444//QRS-456", result);

	}

	@Test
	public void getCrossExpansionThreeWayCrossCreatedBySecondParent() {

		this.initializeGermplasmToExpand(THREE_WAY_CROSS_METHOD_ID, 1);

		// Override default mocks to set firstParent = null scenario so that else condition in threeway cross logic can be triggered.
		Mockito.when(this.germplasmDataManager.getGermplasmByGID(this.germplasmToExpand.getGpid1())).thenReturn(null);

		CrossExpansionProperties crossExpansionProperties = this.createCrossExpansionProperties();
		crossExpansionProperties.setDefaultLevel(1);

		String result = this.pedigreeDefaultService.getCrossExpansion(GERMPLASM_ID_TO_EXPAND, crossExpansionProperties);

		Assert.assertEquals("AAA-111/BBB-222//XYZ-789", result);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void getCrossExpansionUnimplementedVariant() {
		this.pedigreeDefaultService.getCrossExpansion(new Germplasm(), 1, new CrossExpansionProperties());
	}

	@Test
	public void getCrossExpansionNonExistantGid() {
		int nonExistantGermplasm = -987654321;
		String result = this.pedigreeDefaultService.getCrossExpansion(nonExistantGermplasm, this.createCrossExpansionProperties());
		Assert.assertEquals("", result);
	}

	@Test
	public void getCrossExpansionForDerivativeOrMaintenanceMethods() {
		// -ve value pf gnpgs indicates derivative process
		this.initializeGermplasmToExpand(SINGLE_CROSS_METHOD_ID, -1);

		CrossExpansionProperties crossExpansionProperties = this.createCrossExpansionProperties();
		String result = this.pedigreeDefaultService.getCrossExpansion(GERMPLASM_ID_TO_EXPAND, crossExpansionProperties);

		Assert.assertEquals("III-111", result);
	}

	private void initializeGermplasmToExpand(int methodId, int gnpgs) {

		this.germplasmToExpand = this.createGermplasm(GERMPLASM_ID_TO_EXPAND, gnpgs, 98, 97, "ABC-123", methodId);
		this.firstParent = this.createGermplasm(98, gnpgs, 96, 95, "QRS-456", methodId);
		this.secondParent = this.createGermplasm(97, gnpgs, 94, 93, "XYZ-789", methodId);
		this.firstGrandparent1 = this.createGermplasm(96, gnpgs, 92, 91, "AAA-111", methodId);
		this.firstGrandparent2 = this.createGermplasm(95, gnpgs, 90, 89, "BBB-222", methodId);
		this.secondGrandparent1 = this.createGermplasm(94, gnpgs, 88, 87, "CCC-333", methodId);
		this.secondGrandparent2 = this.createGermplasm(93, gnpgs, 86, 85, "DDD-444", methodId);

		this.firstGrandparent1firstParent = this.createGermplasm(92, gnpgs, 0, 0, "EEE-111", methodId);
		this.firstGrandparent1secondParent = this.createGermplasm(91, gnpgs, 0, 0, "FFF-222", methodId);
		this.firstGrandparent2firstParent = this.createGermplasm(90, gnpgs, 0, 0, "GGG-111", methodId);
		this.firstGrandparent2secondParent = this.createGermplasm(89, gnpgs, 0, 0, "HHH-222", methodId);

		this.secondGrandparent1firstParent = this.createGermplasm(88, gnpgs, 0, 0, "III-111", methodId);
		this.secondGrandparent1secondParent = this.createGermplasm(87, gnpgs, 0, 0, "JJJ-222", methodId);
		this.secondGrandparent2firstParent = this.createGermplasm(86, gnpgs, 0, 0, "KKK-111", methodId);
		this.secondGrandparent2secondParent = this.createGermplasm(85, gnpgs, 0, 0, "KKK-222", methodId);

		Mockito.when(this.germplasmDataManager.getGermplasmWithPrefName(99)).thenReturn(this.germplasmToExpand);
		Mockito.when(this.germplasmDataManager.getGermplasmWithPrefName(98)).thenReturn(this.secondParent);
		Mockito.when(this.germplasmDataManager.getGermplasmWithPrefName(97)).thenReturn(this.firstParent);
		Mockito.when(this.germplasmDataManager.getGermplasmWithPrefName(96)).thenReturn(this.firstGrandparent1);
		Mockito.when(this.germplasmDataManager.getGermplasmWithPrefName(95)).thenReturn(this.firstGrandparent2);
		Mockito.when(this.germplasmDataManager.getGermplasmWithPrefName(94)).thenReturn(this.secondGrandparent1);
		Mockito.when(this.germplasmDataManager.getGermplasmWithPrefName(93)).thenReturn(this.secondGrandparent2);
		Mockito.when(this.germplasmDataManager.getGermplasmWithPrefName(92)).thenReturn(this.firstGrandparent1firstParent);
		Mockito.when(this.germplasmDataManager.getGermplasmWithPrefName(91)).thenReturn(this.firstGrandparent1secondParent);
		Mockito.when(this.germplasmDataManager.getGermplasmWithPrefName(90)).thenReturn(this.firstGrandparent2firstParent);
		Mockito.when(this.germplasmDataManager.getGermplasmWithPrefName(89)).thenReturn(this.firstGrandparent2secondParent);
		Mockito.when(this.germplasmDataManager.getGermplasmWithPrefName(88)).thenReturn(this.secondGrandparent1firstParent);
		Mockito.when(this.germplasmDataManager.getGermplasmWithPrefName(87)).thenReturn(this.secondGrandparent1secondParent);
		Mockito.when(this.germplasmDataManager.getGermplasmWithPrefName(86)).thenReturn(this.secondGrandparent2firstParent);
		Mockito.when(this.germplasmDataManager.getGermplasmWithPrefName(85)).thenReturn(this.secondGrandparent2secondParent);

		Mockito.when(this.germplasmDataManager.getGermplasmByGID(99)).thenReturn(this.germplasmToExpand);
		Mockito.when(this.germplasmDataManager.getGermplasmByGID(98)).thenReturn(this.secondParent);
		Mockito.when(this.germplasmDataManager.getGermplasmByGID(97)).thenReturn(this.firstParent);
		Mockito.when(this.germplasmDataManager.getGermplasmByGID(96)).thenReturn(this.firstGrandparent1);
		Mockito.when(this.germplasmDataManager.getGermplasmByGID(95)).thenReturn(this.firstGrandparent2);
		Mockito.when(this.germplasmDataManager.getGermplasmByGID(94)).thenReturn(this.secondGrandparent1);
		Mockito.when(this.germplasmDataManager.getGermplasmByGID(93)).thenReturn(this.secondGrandparent2);
		Mockito.when(this.germplasmDataManager.getGermplasmByGID(92)).thenReturn(this.firstGrandparent1firstParent);
		Mockito.when(this.germplasmDataManager.getGermplasmByGID(91)).thenReturn(this.firstGrandparent1secondParent);
		Mockito.when(this.germplasmDataManager.getGermplasmByGID(90)).thenReturn(this.firstGrandparent2firstParent);
		Mockito.when(this.germplasmDataManager.getGermplasmByGID(89)).thenReturn(this.firstGrandparent2secondParent);
		Mockito.when(this.germplasmDataManager.getGermplasmByGID(88)).thenReturn(this.secondGrandparent1firstParent);
		Mockito.when(this.germplasmDataManager.getGermplasmByGID(87)).thenReturn(this.secondGrandparent1secondParent);
		Mockito.when(this.germplasmDataManager.getGermplasmByGID(86)).thenReturn(this.secondGrandparent2firstParent);
		Mockito.when(this.germplasmDataManager.getGermplasmByGID(85)).thenReturn(this.secondGrandparent2secondParent);

	}

	private Germplasm createGermplasm(int gid, int gnpgs, int gpid1, int gpid2, String germplasmName, int methodId) {

		Germplasm g = new Germplasm();
		g.setGid(gid);
		g.setGdate(Integer.valueOf(20141014));
		g.setGnpgs(Integer.valueOf(gnpgs));
		g.setGpid1(gpid1);
		g.setGpid2(gpid2);
		g.setLgid(Integer.valueOf(0));
		g.setGrplce(Integer.valueOf(0));
		g.setLocationId(Integer.valueOf(1));
		g.setMethodId(methodId);
		g.setMgid(Integer.valueOf(1));
		g.setUserId(Integer.valueOf(1));
		g.setReferenceId(Integer.valueOf(1));
		g.setLgid(Integer.valueOf(1));

		Name n = new Name();
		n.setLocationId(Integer.valueOf(1));
		n.setNdate(Integer.valueOf(20141014));
		n.setNval(germplasmName);
		n.setReferenceId(Integer.valueOf(1));
		n.setTypeId(Integer.valueOf(1));
		n.setUserId(Integer.valueOf(1));

		g.setPreferredName(n);

		return g;

	}

	private Method createMethod(int id, String name) {
		Method method = new Method();
		method.setMid(id);
		method.setMname(name);
		return method;
	}

	private CrossExpansionProperties createCrossExpansionProperties() {
		final Properties mockProperties = Mockito.mock(Properties.class);
		Mockito.when(mockProperties.getProperty("wheat.generation.level")).thenReturn("0");

		CrossExpansionProperties crossExpansionProperties = new CrossExpansionProperties();
		crossExpansionProperties.setDefaultLevel(1);
		crossExpansionProperties.setProfile(null);
		return crossExpansionProperties;
	}

}
