
package org.generationcp.middleware.service;

import java.io.File;
import java.io.IOException;
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
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Please note that this test requires a wheat database with a gid dump to actually run.
 *
 * @author Akhil
 *
 */
public class ConfigurablePedigreeServiceImplTest extends IntegrationTestBase {

	// TODO pedigree service parameters need to be set in testContext.xml
	@Autowired
	private PedigreeService pedigreeService;

	@Autowired
	private PedigreeService configurablePedigreeService;

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	private CrossExpansionProperties newCrossExpansionProperties;

	private final String testGids = "417759,417764,417769,434404,434405,434406,434415,434416,434417,434426,434427,434428,434436,434437,434438,434447,434448,434449,434456,434457,434458,434467,434468,434469,434475,434476,434477,434486,434487,434496,434497,434498,434505,434506,434507,434515,434516,434517,434523,434524,434525,"
			+ "434533,434534,434535,434544,434545,434553,434554,434555,434563,434564,434565,434573,434574,434575,434583,434584,434585,434592,"
			+ "434593,434594,434601,434602,434603,434612,434613,434614,434623,434624,434625,434634,434635,434636,434645,434646,434653,434654,"
			+ "434662,434663,434671,434672,434679,434686,434687,434695,434696,434704,434705,434706,457149,457234";

	private CrossExpansionProperties originalCrossExpansionProperties;

	@Before
	public void setup() {

		final Properties mockProperties = Mockito.mock(Properties.class);
		this.originalCrossExpansionProperties = new CrossExpansionProperties(Mockito.mock(Properties.class));
		this.originalCrossExpansionProperties.setDefaultLevel(1);

		this.newCrossExpansionProperties = new CrossExpansionProperties(mockProperties);
		this.newCrossExpansionProperties.setDefaultLevel(3);
		Mockito.when(mockProperties.getProperty("maize.generation.level")).thenReturn("3");
		Mockito.when(mockProperties.getProperty("maize.nametype.order")).thenReturn("43,42,41");

	}

	@Test
	public void wheatPedigreeMaleBackCross() throws Exception {

		for(int i=10; i < 50; i++ ) {
			validateGermplasm((i*10000)+1+ "-" + (i+1)*10000, germplasmDataManager.getAllGermplasm((i*10000)+1, 10000));
		}


		//validateGermplasm("10000-20000", germplasmDataManager.getGermplasmByMethodName("Double cross", 0, 1000, Operation.EQUAL));
//		validateGermplasm(germplasmDataManager.getGermplasmByMethodName("Backcross", 0, 1000, Operation.EQUAL));
//		validateGermplasm(germplasmDataManager.getGermplasmByMethodName("Three-way cross", 0, 1000, Operation.EQUAL));
//		validateGermplasm(germplasmDataManager.getGermplasmByMethodName("Single cross", 0, 1000, Operation.EQUAL));
//		validateGermplasm(germplasmDataManager.getGermplasmByMethodName("Female complex top cross", 0, 1000, Operation.EQUAL));
//
//		validateGermplasm(germplasmDataManager.getGermplasmByMethodName("Unknown derivative method", 0, 1000, Operation.EQUAL));
//		validateGermplasm(germplasmDataManager.getGermplasmByMethodName("Collection population", 0, 1000, Operation.EQUAL));
//		validateGermplasm(germplasmDataManager.getGermplasmByMethodName("Unknown generative method", 0, 1000, Operation.EQUAL));
//		validateGermplasm(germplasmDataManager.getGermplasmByMethodName("Single cross heterozygotes", 0, 1000, Operation.EQUAL));
//		validateGermplasm(germplasmDataManager.getGermplasmByMethodName("Three-way cross heterozygotes", 0, 1000, Operation.EQUAL));
//		validateGermplasm(germplasmDataManager.getGermplasmByMethodName("Single cross heterozygotes", 0, 1000, Operation.EQUAL));
//		validateGermplasm(germplasmDataManager.getGermplasmByMethodName("Selected pollen cross", 0, 1000, Operation.EQUAL));


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

	private void validateGermplasm(final String fileNameSuffix, final List<Germplasm> allGermplasm ) throws IOException {
		final File passedFilePath = new File("/Users/Akhil/work/results/" + fileNameSuffix + "-Passed-" + System.currentTimeMillis() + ".csv");
		final File failedFilePath = new File("/Users/Akhil/work/results/" + fileNameSuffix + "-Failed-" + System.currentTimeMillis() + ".csv");
		passedFilePath.createNewFile();
		failedFilePath.createNewFile();

		final PrintWriter passWrite = new PrintWriter(passedFilePath, "UTF-8");
		final PrintWriter failWriter = new PrintWriter(failedFilePath, "UTF-8");




		for (Germplasm germplasm : allGermplasm) {
			final Germplasm germplasmWithMethodType = germplasmDataManager.getGermplasmWithMethodType(germplasm.getGid());
			final String crossExpansion = pedigreeService.getCrossExpansion(germplasm.getGid(), originalCrossExpansionProperties);
			final String crossExpansion2 = configurablePedigreeService.getCrossExpansion(germplasm.getGid(), newCrossExpansionProperties);
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

		passWrite.close();
		failWriter.close();

	}

	@Test
	public void wheatPedigreeList() throws Exception {
		final List<String> listOfGids = Arrays.asList(testGids.split(","));
		final List<Integer> integersGids = new ArrayList<>();
		for(String s : listOfGids) {
			integersGids.add(Integer.valueOf(s));
		}
		for (Integer gid : integersGids) {
			String crossExpansion = pedigreeService.getCrossExpansion(gid, originalCrossExpansionProperties);
			String crossExpansion2 = configurablePedigreeService.getCrossExpansion(gid, newCrossExpansionProperties);
			System.out.println("Normal Service -" + crossExpansion);
			System.out.println("New Service    -" + crossExpansion2);
			//Assert.assertEquals("Asssertion for GID " +gid+ " failed.", crossExpansion, crossExpansion2);

		}

	}


	@Test
	public void wheat476944() throws Exception {

			String crossExpansion = pedigreeService.getCrossExpansion(476944, newCrossExpansionProperties);
			String crossExpansion2 = configurablePedigreeService.getCrossExpansion(476944, newCrossExpansionProperties);
			System.out.println("Normal Service -" + crossExpansion);
			System.out.println("New Service    -" + crossExpansion2);
			//Assert.assertEquals("Asssertion for GID " +gid+ " failed.", crossExpansion, crossExpansion2);


	}

}
