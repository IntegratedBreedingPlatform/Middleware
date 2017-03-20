
package org.generationcp.middleware.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.service.api.PedigreeService;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.hibernate.SQLQuery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * Test essentially compares the pedigree generation results of the new algorithm (https://leafnode.atlassian.net/browse/BMS-2174) vs the
 * old one. This not a standard unit test. This is the best way to ensure that we have not changed anything in the old algorithm. The old
 * class is called org.generationcp.middleware.service.pedigree.PedigreeDefaultServiceImpl. Please note this test will not work if we change
 * the existing algorithm.
 *
 */
public class PedigreeAlgorithmTest extends IntegrationTestBase {

	@Autowired
	private PedigreeService pedigreeService;

	@Autowired
	private PedigreeService oldPedigreeService;

	private CrossExpansionProperties newCrossExpansionProperties;

	private CrossExpansionProperties originalCrossExpansionProperties;

	private final Random random = new Random();
	
	final ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(30));


	@Before
	public void setup() {

		final Properties mockProperties = Mockito.mock(Properties.class);
		this.originalCrossExpansionProperties = new CrossExpansionProperties(Mockito.mock(Properties.class));
		this.originalCrossExpansionProperties.setDefaultLevel(1);
		this.newCrossExpansionProperties = new CrossExpansionProperties(mockProperties);
		this.newCrossExpansionProperties.setDefaultLevel(3);
		Mockito.when(mockProperties.getProperty("wheat.generation.level")).thenReturn("3");

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

	public List<ListenableFuture<Void>> compareGermplasmGeneratedByDifferentAlgorithms(final String methodName) {
		
		
		final List<Germplasm> randomlySelectGermplasm = randomlySelectGermplasm(1000, methodName);
		List<ListenableFuture<Void>> futures = new ArrayList<>();

		for (final Germplasm germplasm : randomlySelectGermplasm) {
			ListenableFuture<Void> pedigreeStringGeneration = service.submit(new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					final String pedigreeStringGeneratedByOriginalAlgorithm =
							oldPedigreeService.getCrossExpansion(germplasm.getGid(), newCrossExpansionProperties);
					final String pedigreeStringGeneratedByNewAlgorithm =
							pedigreeService.getCrossExpansion(germplasm.getGid(), newCrossExpansionProperties);
					Assert.assertEquals("Asssertion for GID " + germplasm.getGid() + " failed.", pedigreeStringGeneratedByOriginalAlgorithm,
							pedigreeStringGeneratedByNewAlgorithm);
					return null;
				}
			});
			futures.add(pedigreeStringGeneration);


		}
		return futures;
	}

	private List<Germplasm> randomlySelectGermplasm(final int numberOfRows, final String methodName) {
		long numberOfGermplasmWithMethodName = countGermplasmByMethodName("%" + methodName + "%", Operation.LIKE);
		int randomNumberUpperBound = (int) numberOfGermplasmWithMethodName - numberOfRows;
		int germplasmStartIndex = 0;
		if (randomNumberUpperBound > 0) {
			germplasmStartIndex = random.nextInt((int) randomNumberUpperBound);
		}
		System.out.println(String.format("Getting '%d' number of rows from '%d' starting at '%d' avaiable for method name like '%s'",
				numberOfRows, numberOfGermplasmWithMethodName, germplasmStartIndex, methodName));
		return getGermplasmByMethodName("%" + methodName + "%", germplasmStartIndex, numberOfRows, Operation.LIKE);
	}

	private List<Germplasm> getGermplasmByMethodName(String string, int germplasmStartIndex, int numberOfRows, Operation like) {
		SQLQuery createSQLQuery = this.sessionProvder.getSession().createSQLQuery("Select * from germplsm g "
				+ "INNER JOIN methods m ON g.methn = m.mid "
				+ "where m.mname LIKE :mname and  g.deleted = 0  LIMIT " + numberOfRows);
		createSQLQuery.setParameter("mname", string);
		createSQLQuery.addEntity(Germplasm.class);
		return createSQLQuery.list();
	}

	private long countGermplasmByMethodName(String string, Operation like) {
		SQLQuery createSQLQuery = this.sessionProvder.getSession().createSQLQuery("Select count(*) from germplsm g "
							+ "INNER JOIN methods m ON g.methn = m.mid "
							+ "where m.mname LIKE :mname and  g.deleted = 0 ");
		createSQLQuery.setParameter("mname", string);

		return ((BigInteger) createSQLQuery.uniqueResult()).longValue();
	}

}
