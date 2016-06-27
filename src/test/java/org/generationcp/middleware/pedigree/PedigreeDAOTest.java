package org.generationcp.middleware.pedigree;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class PedigreeDAOTest extends IntegrationTestBase {

	
	private PedigreeDAO pedigreeDAO;
	private GermplasmDAO germplasmDAO;
	
	@Autowired
	private GermplasmDataManager germplasmDataManager;

	@Before
	public void setUp() throws Exception {
		final Session session = this.sessionProvder.getSession();

		pedigreeDAO = new PedigreeDAO();
		pedigreeDAO.setSession(session);
		
		germplasmDAO = new GermplasmDAO();
		germplasmDAO.setSession(session);
	}
	
	@Test
	public void testSavingAPedigreeString() {
		final Pedigree pedigree = randomPedigreeStringPopulation();
		final List<Pedigree> results = pedigreeDAO.filterByColumnValue("pedigreeString", pedigree.getPedigreeString());

		Assert.assertNotNull("Enusre ID has been generated", pedigree.getId());
		Assert.assertEquals("We should only have record returned", 1, results.size());
		Assert.assertEquals("Pedigree string must be the same.", pedigree.getPedigreeString(), results.get(0).getPedigreeString());

	}
	
	@Test
	public void testLinkingPedigreeStringToGermplasm() {
		
		final Germplasm testGermplasm = getTestGermplasm();
		final Pedigree pedigree = randomPedigreeStringPopulation();
		testGermplasm.setPedigree(pedigree);
		germplasmDAO.save(testGermplasm);
		
		final Germplasm savedGermplasm = germplasmDAO.getById(testGermplasm.getGid());
		Assert.assertNotNull("Enusre ID has been generated", savedGermplasm.getPedigree()); ;
	}
	
	@Test
	public void testForeignKeyConstraint() {
		
		final Germplasm testGermplasm = getTestGermplasm();
		final Pedigree pedigree = randomPedigreeStringPopulation();
		testGermplasm.setPedigree(pedigree);
		germplasmDAO.save(testGermplasm);
		
		final Germplasm savedGermplasm = germplasmDAO.getById(testGermplasm.getGid());
		Assert.assertNotNull("Enusre ID has been generated", savedGermplasm.getPedigree()); ;
	}
	
	@Test
	public void testSimpleSearchString() {
		
		final Germplasm testGermplasm = getTestGermplasm();
				final Pedigree pedigree = randomPedigreeStringPopulation();
		testGermplasm.setPedigree(pedigree);
		germplasmDAO.save(testGermplasm);
		
		// Making the name unique so that we can search for it and get back only one result 
		final String uniqueGermplasmName = UUID.randomUUID().toString();
		final Name testName = getTestName(uniqueGermplasmName);
		testName.setGermplasmId(testGermplasm.getGid());
		testGermplasm.getNames().add(testName);
		
		germplasmDAO.save(testGermplasm);
		
		final Germplasm simpleDatabaseQuery = germplasmDAO.searchForGermplasms(uniqueGermplasmName, Operation.LIKE, false, false, false).get(0);
		Assert.assertNotNull("Enusre Pedigree has been retrieved.", simpleDatabaseQuery.getPedigree());
		
		final Germplasm queryWithParents = germplasmDAO.searchForGermplasms(uniqueGermplasmName, Operation.LIKE, true, false, false).get(0);
		Assert.assertNotNull("Enusre Pedigree has been retrieved.", queryWithParents.getPedigree());

		// Just ensure that the query ran
		germplasmDAO.searchForGermplasms(uniqueGermplasmName, Operation.LIKE, true, true, false);
		
		List<Germplasm> searchResultsWithMgidsIncluded = germplasmDAO.searchForGermplasms(uniqueGermplasmName, Operation.LIKE, true, false, true);
		Assert.assertNotNull("Enusre Pedigree has been retrieved.", searchResultsWithMgidsIncluded.get(0).getPedigree());


	}

	@Test
	public void testIncludeParentsTestString() {
		
		final Pedigree pedigree = randomPedigreeStringPopulation();

		final Germplasm testParentGermplasm = getTestGermplasm();
		testParentGermplasm.setPedigree(pedigree);
		germplasmDAO.save(testParentGermplasm);

		final Germplasm testGermplasm = getTestGermplasm();
		testGermplasm.setGpid1(testParentGermplasm.getGid());
		testGermplasm.setPedigree(pedigree);
		germplasmDAO.save(testGermplasm);
		
		// Making the name unique so that we can search for it and get back only one result 
		final String uniqueGermplasmName = UUID.randomUUID().toString();
		final Name testName = getTestName(uniqueGermplasmName);
		testName.setGermplasmId(testGermplasm.getGid());
		testGermplasm.getNames().add(testName);
		
		germplasmDAO.save(testGermplasm);
		
		// Just ensure that the query ran
		final List<Germplasm> searchForGermplasms = germplasmDAO.searchForGermplasms(uniqueGermplasmName, Operation.LIKE, true, false, false);
		Assert.assertEquals("We should only have record returned", 2, searchForGermplasms.size());
		for (Germplasm germplasm : searchForGermplasms) {
			Assert.assertNotNull("Enusre Pedigree has been retrieved.", germplasm.getPedigree());
		}

	}


	@Test
	public void testGIDSearchString() {
		
		final Germplasm testGermplasm = getTestGermplasm();
		final Pedigree pedigree = randomPedigreeStringPopulation();
		testGermplasm.setPedigree(pedigree);
		germplasmDAO.save(testGermplasm);
		
		// Making the name unique so that we can search for it and get back only one result 
		final String uniqueGermplasmName = UUID.randomUUID().toString();
		final Name testName = getTestName(uniqueGermplasmName);
		testName.setGermplasmId(testGermplasm.getGid());
		testGermplasm.getNames().add(testName);
		
		germplasmDAO.save(testGermplasm);
		
		// LIKE GID TEST
		final Germplasm simpleDatabaseQuery = germplasmDAO.searchForGermplasms(testGermplasm.getGid().toString(), Operation.LIKE, false, false, false).get(0);
		Assert.assertNotNull("Enusre Pedigree has been retrieved.", simpleDatabaseQuery.getPedigree());

		// Equals GID test
		final Germplasm simpleEqualsDatabaseQuery = germplasmDAO.searchForGermplasms(testGermplasm.getGid().toString(), Operation.EQUAL, false, false, false).get(0);
		Assert.assertNotNull("Enusre Pedigree has been retrieved.", simpleEqualsDatabaseQuery.getPedigree());

	}
	
	@Test
	public void testAddPedigree() {
		final Germplasm testGermplasm = getTestGermplasm();

		final String pedigreeString = UUID.randomUUID().toString();
	  	HashMap<Germplasm, String> germplasmPedigreeStringMap = new HashMap<>();
	  	germplasmPedigreeStringMap.put(testGermplasm, pedigreeString);
	  	this.germplasmDataManager.addPedigreeString(germplasmPedigreeStringMap, "test", 3);

		final Germplasm simpleEqualsDatabaseQuery = germplasmDAO.searchForGermplasms(testGermplasm.getGid().toString(), Operation.EQUAL, false, false, false).get(0);
		final Pedigree savedPedigree = simpleEqualsDatabaseQuery.getPedigree();
		Assert.assertNotNull("Ensure Pedigree has been retrieved.", savedPedigree);
		Assert.assertEquals("Ensure Pedigree has been retrieved.", pedigreeString, savedPedigree.getPedigreeString());
		Assert.assertEquals("Ensure Pedigree has been retrieved.", "test", savedPedigree.getAlgorithmUsed());
		Assert.assertEquals("Ensure Pedigree has been retrieved.", 3, savedPedigree.getLevels());
	}
	
	private Germplasm getTestGermplasm() {
		
		final Germplasm testGermplasm = new Germplasm();
		testGermplasm.setGdate(Integer.valueOf(20141014));
		testGermplasm.setGnpgs(Integer.valueOf(0));
		testGermplasm.setGpid1(Integer.valueOf(0));
		testGermplasm.setGpid2(Integer.valueOf(0));
		testGermplasm.setLgid(Integer.valueOf(0));
		testGermplasm.setGrplce(Integer.valueOf(0));
		testGermplasm.setLocationId(Integer.valueOf(1));
		testGermplasm.setMethodId(Integer.valueOf(1011));
		testGermplasm.setMgid(Integer.valueOf(1));
		testGermplasm.setUserId(Integer.valueOf(1));
		testGermplasm.setReferenceId(Integer.valueOf(1));
		testGermplasm.setLgid(Integer.valueOf(1));
		

		
		return testGermplasm;
	}
	
	private Name getTestName(final String germplasmName) {
		
		final Name testName = new Name();
		testName.setLocationId(Integer.valueOf(1));
		testName.setNdate(Integer.valueOf(20141014));
		testName.setNval(germplasmName);
		testName.setReferenceId(Integer.valueOf(1));
		testName.setTypeId(Integer.valueOf(1));
		testName.setUserId(Integer.valueOf(1));
		testName.setNstat(1);
		return testName;
	}
	
	private Pedigree randomPedigreeStringPopulation() {
		final Pedigree pedigree = new Pedigree();
		pedigree.setAlgorithmUsed("default");
		pedigree.setPedigreeString(UUID.randomUUID().toString());
		pedigree.setLevels(3);
		pedigreeDAO.save(pedigree);
		return pedigree;
	}
	
}
