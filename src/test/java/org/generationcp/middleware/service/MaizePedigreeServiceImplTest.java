
package org.generationcp.middleware.service;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.service.api.PedigreeService;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Please note that this test requires a wheat database with a gid dump to actually run.
 *
 * @author Akhil
 *
 */
public class MaizePedigreeServiceImplTest extends IntegrationTestBase {

	// TODO pedigree service parameters need to be set in testContext.xml
	@Autowired
	private PedigreeService pedigreeService;

	@Autowired
	private PedigreeService configurablePedigreeService;

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	private CrossExpansionProperties crossExpansionProperties;

	private final String testGids = "417759,417764,417769,434404,434405,434406,434415,434416,434417,434426,434427,434428,434436,434437,434438,434447,434448,434449,434456,434457,434458,434467,434468,434469,434475,434476,434477,434486,434487,434496,434497,434498,434505,434506,434507,434515,434516,434517,434523,434524,434525,"
			+ "434533,434534,434535,434544,434545,434553,434554,434555,434563,434564,434565,434573,434574,434575,434583,434584,434585,434592,"
			+ "434593,434594,434601,434602,434603,434612,434613,434614,434623,434624,434625,434634,434635,434636,434645,434646,434653,434654,"
			+ "434662,434663,434671,434672,434679,434686,434687,434695,434696,434704,434705,434706,457149,457234";

	@Before
	public void setup() {

		final Properties mockProperties = Mockito.mock(Properties.class);
		this.crossExpansionProperties = new CrossExpansionProperties(mockProperties);
		this.crossExpansionProperties.setDefaultLevel(3);
		Mockito.when(mockProperties.getProperty("maize.generation.level")).thenReturn("3");
		Mockito.when(mockProperties.getProperty("maize.nametype.order")).thenReturn("43,42,41");

	}

	@Test
	public void wheatPedigreeMaleBackCross() throws Exception {

		final File passedFilePath = File.createTempFile("/tmp/" + "Passed-" + System.currentTimeMillis() + "-", ".csv");
		final File failedFilePath = File.createTempFile("/tmp/" + "Failed-" + System.currentTimeMillis() + "-", ".csv");

		final PrintWriter passWrite = new PrintWriter(passedFilePath, "UTF-8");
		final PrintWriter failWriter = new PrintWriter(failedFilePath, "UTF-8");

		validateGermplasm(germplasmDataManager.getAllGermplasm(0, 10000), passWrite, failWriter);

		validateGermplasm(germplasmDataManager.getAllGermplasm(20000, 10000), passWrite, failWriter);

		validateGermplasm(germplasmDataManager.getAllGermplasm(30000, 10000), passWrite, failWriter);

		validateGermplasm(germplasmDataManager.getAllGermplasm(40000, 10000), passWrite, failWriter);

		validateGermplasm(germplasmDataManager.getAllGermplasm(50000, 10000), passWrite, failWriter);

		validateGermplasm(germplasmDataManager.getAllGermplasm(60000, 10000), passWrite, failWriter);

		validateGermplasm(germplasmDataManager.getAllGermplasm(70000, 10000), passWrite, failWriter);

		validateGermplasm(germplasmDataManager.getAllGermplasm(80000, 10000), passWrite, failWriter);

		validateGermplasm(germplasmDataManager.getAllGermplasm(90000, 10000), passWrite, failWriter);

		validateGermplasm(germplasmDataManager.getAllGermplasm(100000, 10000), passWrite, failWriter);
		validateGermplasm(germplasmDataManager.getAllGermplasm(110000, 10000), passWrite, failWriter);
		validateGermplasm(germplasmDataManager.getAllGermplasm(120000, 10000), passWrite, failWriter);
		validateGermplasm(germplasmDataManager.getAllGermplasm(130000, 10000), passWrite, failWriter);
		validateGermplasm(germplasmDataManager.getAllGermplasm(140000, 10000), passWrite, failWriter);
		validateGermplasm(germplasmDataManager.getAllGermplasm(150000, 10000), passWrite, failWriter);
		validateGermplasm(germplasmDataManager.getAllGermplasm(160000, 10000), passWrite, failWriter);
		validateGermplasm(germplasmDataManager.getAllGermplasm(170000, 10000), passWrite, failWriter);

		validateGermplasm(germplasmDataManager.getAllGermplasm(180000, 10000), passWrite, failWriter);
		validateGermplasm(germplasmDataManager.getAllGermplasm(190000, 10000), passWrite, failWriter);
		validateGermplasm(germplasmDataManager.getAllGermplasm(200000, 10000), passWrite, failWriter);
		validateGermplasm(germplasmDataManager.getAllGermplasm(210000, 10000), passWrite, failWriter);
		validateGermplasm(germplasmDataManager.getAllGermplasm(220000, 10000), passWrite, failWriter);
		validateGermplasm(germplasmDataManager.getAllGermplasm(230000, 10000), passWrite, failWriter);
		validateGermplasm(germplasmDataManager.getAllGermplasm(240000, 10000), passWrite, failWriter);
		validateGermplasm(germplasmDataManager.getAllGermplasm(250000, 10000), passWrite, failWriter);
		validateGermplasm(germplasmDataManager.getAllGermplasm(260000, 10000), passWrite, failWriter);
		validateGermplasm(germplasmDataManager.getAllGermplasm(270000, 10000), passWrite, failWriter);
		validateGermplasm(germplasmDataManager.getAllGermplasm(280000, 10000), passWrite, failWriter);
		validateGermplasm(germplasmDataManager.getAllGermplasm(290000, 10000), passWrite, failWriter);
		validateGermplasm(germplasmDataManager.getAllGermplasm(300000, 10000), passWrite, failWriter);
		validateGermplasm(germplasmDataManager.getAllGermplasm(310000, 10000), passWrite, failWriter);
		validateGermplasm(germplasmDataManager.getAllGermplasm(320000, 10000), passWrite, failWriter);
		validateGermplasm(germplasmDataManager.getAllGermplasm(330000, 10000), passWrite, failWriter);
		validateGermplasm(germplasmDataManager.getAllGermplasm(340000, 10000), passWrite, failWriter);
		validateGermplasm(germplasmDataManager.getAllGermplasm(350000, 10000), passWrite, failWriter);
		validateGermplasm(germplasmDataManager.getAllGermplasm(360000, 10000), passWrite, failWriter);
		validateGermplasm(germplasmDataManager.getAllGermplasm(370000, 10000), passWrite, failWriter);
		validateGermplasm(germplasmDataManager.getAllGermplasm(380000, 10000), passWrite, failWriter);
		validateGermplasm(germplasmDataManager.getAllGermplasm(390000, 10000), passWrite, failWriter);

		validateGermplasm(germplasmDataManager.getAllGermplasm(400000, 10000), passWrite, failWriter);
		validateGermplasm(germplasmDataManager.getAllGermplasm(410000, 10000), passWrite, failWriter);
		validateGermplasm(germplasmDataManager.getAllGermplasm(420000, 10000), passWrite, failWriter);
		validateGermplasm(germplasmDataManager.getAllGermplasm(430000, 10000), passWrite, failWriter);
		validateGermplasm(germplasmDataManager.getAllGermplasm(440000, 10000), passWrite, failWriter);
		validateGermplasm(germplasmDataManager.getAllGermplasm(450000, 10000), passWrite, failWriter);
		validateGermplasm(germplasmDataManager.getAllGermplasm(460000, 10000), passWrite, failWriter);
		validateGermplasm(germplasmDataManager.getAllGermplasm(470000, 10000), passWrite, failWriter);
		validateGermplasm(germplasmDataManager.getAllGermplasm(480000, 10000), passWrite, failWriter);
		validateGermplasm(germplasmDataManager.getAllGermplasm(490000, 10000), passWrite, failWriter);
		validateGermplasm(germplasmDataManager.getGermplasmByMethodName("Double cross", 0, 1000, Operation.EQUAL), passWrite, failWriter);
//		validateGermplasm(germplasmDataManager.getGermplasmByMethodName("Backcross", 0, 1000, Operation.EQUAL), passWrite, failWriter);
//		validateGermplasm(germplasmDataManager.getGermplasmByMethodName("Three-way cross", 0, 1000, Operation.EQUAL), passWrite, failWriter);
//		validateGermplasm(germplasmDataManager.getGermplasmByMethodName("Single cross", 0, 1000, Operation.EQUAL), passWrite, failWriter);
//		validateGermplasm(germplasmDataManager.getGermplasmByMethodName("Female complex top cross", 0, 1000, Operation.EQUAL), passWrite, failWriter);
//
//		validateGermplasm(germplasmDataManager.getGermplasmByMethodName("Unknown derivative method", 0, 1000, Operation.EQUAL), passWrite, failWriter);
//		validateGermplasm(germplasmDataManager.getGermplasmByMethodName("Collection population", 0, 1000, Operation.EQUAL), passWrite, failWriter);
//		validateGermplasm(germplasmDataManager.getGermplasmByMethodName("Unknown generative method", 0, 1000, Operation.EQUAL), passWrite, failWriter);
//		validateGermplasm(germplasmDataManager.getGermplasmByMethodName("Single cross heterozygotes", 0, 1000, Operation.EQUAL), passWrite, failWriter);
//		validateGermplasm(germplasmDataManager.getGermplasmByMethodName("Three-way cross heterozygotes", 0, 1000, Operation.EQUAL), passWrite, failWriter);
//		validateGermplasm(germplasmDataManager.getGermplasmByMethodName("Single cross heterozygotes", 0, 1000, Operation.EQUAL), passWrite, failWriter);
//		validateGermplasm(germplasmDataManager.getGermplasmByMethodName("Selected pollen cross", 0, 1000, Operation.EQUAL), passWrite, failWriter);

		passWrite.close();
		failWriter.close();
		// validateGermplasm(germplasmDataManager.getGermplasmByMethodName("Pool", 0, 1000, Operation.EQUAL));
		// validateGermplasm(germplasmDataManager.getGermplasmByMethodName("Collection wild spp population", 0, 1000, Operation.EQUAL));
		// validateGermplasm(germplasmDataManager.getGermplasmByMethodName("Groups", 0, 1000, Operation.EQUAL));
		// validateGermplasm(germplasmDataManager.getGermplasmByMethodName("Plant identification", 0, 1000, Operation.EQUAL));
		// validateGermplasm(germplasmDataManager.getGermplasmByMethodName("Import", 0, 1000, Operation.EQUAL));
		// validateGermplasm(germplasmDataManager.getGermplasmByMethodName("Half mass selection", 0, 1000, Operation.EQUAL));
		// validateGermplasm(germplasmDataManager.getGermplasmByMethodName("Random bulk", 0, 1000, Operation.EQUAL));
		// validateGermplasm(germplasmDataManager.getGermplasmByMethodName("Open pollination", 0, 1000, Operation.EQUAL));
		// validateGermplasm(germplasmDataManager.getGermplasmByMethodName("Single plant selection heterozygote", 0, 1000,
		// Operation.EQUAL));
		// validateGermplasm(germplasmDataManager.getGermplasmByMethodName("Selected pollen cross", 0, 1000, Operation.EQUAL));
		// validateGermplasm(germplasmDataManager.getGermplasmByMethodName("Full mass selection", 0, 1000, Operation.EQUAL));
		// validateGermplasm(germplasmDataManager.getGermplasmByMethodName("Double  haploid line", 0, 1000, Operation.EQUAL));
		// validateGermplasm(germplasmDataManager.getGermplasmByMethodName("Single plant selection heterozygote", 0, 1000,
		// Operation.EQUAL));

	}

	private void validateGermplasm(List<Germplasm> allGermplasm,final PrintWriter passWrite, final PrintWriter failWriter ) {
		for (Germplasm germplasm : allGermplasm) {
			final Germplasm germplasmWithMethodType = germplasmDataManager.getGermplasmWithMethodType(germplasm.getGid());
			final String crossExpansion = pedigreeService.getCrossExpansion(germplasm.getGid(), crossExpansionProperties);
			final String crossExpansion2 = configurablePedigreeService.getCrossExpansion(germplasm.getGid(), crossExpansionProperties);
			System.out.println("Normal Service -" + germplasm.getGid() + ". " + crossExpansion);
			System.out.println("New Service -" + germplasm.getGid() + " . " + crossExpansion2);
			Method method;
			final String methodName;

			if (germplasmWithMethodType != null) {
				method = germplasmWithMethodType.getMethod();
				if (method != null) {
					methodName = method.getMname();

				} else {
					methodName = "";
				}
			} else {
				methodName = "";
			}
			if(crossExpansion.equals(crossExpansion2)) {
				passWrite.append("\"" +germplasm.getGid() + "\",\"" + methodName + "\",\"" + crossExpansion + "\",\"" + crossExpansion2 +"\"\n");
			} else {
				failWriter.append("\"" +germplasm.getGid() + "\",\"" + methodName + "\",\"" + crossExpansion + "\",\"" + crossExpansion2 +"\"\n");

			}

			//Assert.assertEquals("Asssertion for GID " + germplasm.getGid() + " failed.", crossExpansion, crossExpansion2);
		}
	}

	@Test
	public void wheatPedigreeList() throws Exception {
		final List<String> listOfGids = Arrays.asList(testGids.split(","));
		final List<Integer> integersGids = new ArrayList<>();
		for(String s : listOfGids) {
			integersGids.add(Integer.valueOf(s));
		}
		for (Integer gid : integersGids) {
			String crossExpansion = pedigreeService.getCrossExpansion(gid, crossExpansionProperties);
			String crossExpansion2 = configurablePedigreeService.getCrossExpansion(gid, crossExpansionProperties);
			System.out.println("Normal Service -" + crossExpansion);
			System.out.println("New Service    -" + crossExpansion2);
			//Assert.assertEquals("Asssertion for GID " +gid+ " failed.", crossExpansion, crossExpansion2);

		}

	}

	@Test
	public void wheatPedigree470894() throws Exception {
		crossExpansionProperties.setDefaultLevel(3);
			String crossExpansion = pedigreeService.getCrossExpansion(470894, crossExpansionProperties);
			crossExpansionProperties.setDefaultLevel(2);
			String crossExpansionA = pedigreeService.getCrossExpansion(470894, crossExpansionProperties);
			crossExpansionProperties.setDefaultLevel(1);
			String crossExpansionb = pedigreeService.getCrossExpansion(470894, crossExpansionProperties);
			crossExpansionProperties.setDefaultLevel(3);

			String crossExpansion2 = configurablePedigreeService.getCrossExpansion(470894, crossExpansionProperties);
			System.out.println("Normal Service -" + crossExpansion);
			System.out.println("Normal Service -" + crossExpansionA);

			System.out.println("Normal Service -" + crossExpansionb);

			System.out.println("New Service    -" + crossExpansion2);

	}

	@Test
	public void wheatPedigree158204() throws Exception {
//		crossExpansionProperties.setDefaultLevel(3);
//			String crossExpansion = pedigreeService.getCrossExpansion(158204, crossExpansionProperties);
//			crossExpansionProperties.setDefaultLevel(2);
//			String crossExpansionA = pedigreeService.getCrossExpansion(158204, crossExpansionProperties);
//			crossExpansionProperties.setDefaultLevel(1);
//			String crossExpansionb = pedigreeService.getCrossExpansion(158204, crossExpansionProperties);
			crossExpansionProperties.setDefaultLevel(3);

			String crossExpansion2 = configurablePedigreeService.getCrossExpansion(158204, crossExpansionProperties);
//			System.out.println("Normal Service -" + crossExpansion);
//			System.out.println("Normal Service -" + crossExpansionA);
//
//			System.out.println("Normal Service -" + crossExpansionb);

			System.out.println("New Service    -" + crossExpansion2);

	}



	@Test
	public void wheatPedigree298207() throws Exception {
		crossExpansionProperties.setDefaultLevel(3);
			String crossExpansion = pedigreeService.getCrossExpansion(298207, crossExpansionProperties);
			crossExpansionProperties.setDefaultLevel(2);
			String crossExpansionA = pedigreeService.getCrossExpansion(298207, crossExpansionProperties);
			crossExpansionProperties.setDefaultLevel(1);
			String crossExpansionb = pedigreeService.getCrossExpansion(298207, crossExpansionProperties);
			crossExpansionProperties.setDefaultLevel(3);

			String crossExpansion2 = configurablePedigreeService.getCrossExpansion(298207, crossExpansionProperties);
			System.out.println("Normal Service -" + crossExpansion);
			System.out.println("Normal Service -" + crossExpansionA);

			System.out.println("Normal Service -" + crossExpansionb);

			System.out.println("New Service    -" + crossExpansion2);

	}



	@Test
	public void wheatPedigree237409() throws Exception {
		crossExpansionProperties.setDefaultLevel(3);


			String crossExpansion2 = configurablePedigreeService.getCrossExpansion(237409, crossExpansionProperties);


			System.out.println("New Service    -" + crossExpansion2);

	}
}
