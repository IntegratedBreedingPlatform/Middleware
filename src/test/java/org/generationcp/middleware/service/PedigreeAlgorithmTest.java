
package org.generationcp.middleware.service;

import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.service.api.PedigreeService;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test essentially compares the pedigree generation results of the new algorithm (https://leafnode.atlassian.net/browse/BMS-2174) vs the
 * old one. This not a standard unit test. This is the best way to ensure that we have not changed anything in the old algorithm. The old class
 * is called org.generationcp.middleware.service.pedigree.PedigreeDefaultServiceImpl. Please note this test will not work if we change the
 * existing algorithm.
 *
 */
public class PedigreeAlgorithmTest extends IntegrationTestBase {

	@Autowired
	private PedigreeService pedigreeService;

	@Autowired
	private PedigreeService oldPedigreeService;

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	private CrossExpansionProperties newCrossExpansionProperties;

	private CrossExpansionProperties originalCrossExpansionProperties;

	private final Random random = new Random();

	@Before
	public void setup() {

		final Properties mockProperties = Mockito.mock(Properties.class);
		this.originalCrossExpansionProperties = new CrossExpansionProperties(Mockito.mock(Properties.class));
		this.originalCrossExpansionProperties.setDefaultLevel(1);
		this.newCrossExpansionProperties = new CrossExpansionProperties(mockProperties);
		this.newCrossExpansionProperties.setDefaultLevel(3);
		Mockito.when(mockProperties.getProperty("maize.generation.level")).thenReturn("3");

	}

	@Test
	public void compareGermplasmGeneratedByTheNewAndOldAlgorithm() {

		compareGermplasmGeneratedByDifferentAlgorithms("Backcross");
		compareGermplasmGeneratedByDifferentAlgorithms("Single cross");
		compareGermplasmGeneratedByDifferentAlgorithms("Double cross");
		compareGermplasmGeneratedByDifferentAlgorithms("Three-way cross");
		compareGermplasmGeneratedByDifferentAlgorithms("Female complex top cross");
		compareGermplasmGeneratedByDifferentAlgorithms("Unknown derivative method");
		compareGermplasmGeneratedByDifferentAlgorithms("Collection population");
		compareGermplasmGeneratedByDifferentAlgorithms("Unknown generative method");
		// A breeding method found in a CIMMYT database
		compareGermplasmGeneratedByDifferentAlgorithms("Pool");
		compareGermplasmGeneratedByDifferentAlgorithms("Selected pollen cross");

	}

	public void compareGermplasmGeneratedByDifferentAlgorithms(final String methodName) {
		final List<Germplasm> randomlySelectGermplasm = randomlySelectGermplasm(1000, methodName);
		for (Germplasm germplasm : randomlySelectGermplasm) {
			final String pedigreeStringGeneratedByOriginalAlgorithm =
					oldPedigreeService.getCrossExpansion(germplasm.getGid(), newCrossExpansionProperties);
			final String pedigreeStringGeneratedByNewAlgorithm =
					pedigreeService.getCrossExpansion(germplasm.getGid(), newCrossExpansionProperties);
			Assert.assertEquals("Asssertion for GID " + germplasm.getGid() + " failed.", pedigreeStringGeneratedByOriginalAlgorithm,
					pedigreeStringGeneratedByNewAlgorithm);
		}
	}

	private List<Germplasm> randomlySelectGermplasm(final int numberOfRows, final String methodName) {
		long numberOfGermplasmWithMethodName = germplasmDataManager.countGermplasmByMethodName("%" + methodName + "%", Operation.LIKE);
		int randomNumberUpperBound = (int) numberOfGermplasmWithMethodName - numberOfRows;
		int germplasmStartIndex = 0;
		if (randomNumberUpperBound > 0) {
			germplasmStartIndex = random.nextInt((int) randomNumberUpperBound);
		}
		System.out.println(String.format("Getting '%d' number of rows from '%d' starting at '%d' avaiable for method name like '%s'",
				numberOfRows, numberOfGermplasmWithMethodName, germplasmStartIndex, methodName));
		return germplasmDataManager.getGermplasmByMethodName("%" + methodName + "%", germplasmStartIndex, numberOfRows, Operation.LIKE);
	}

}
