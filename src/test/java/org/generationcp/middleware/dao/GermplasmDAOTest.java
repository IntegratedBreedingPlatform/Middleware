/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.dao;

import java.util.Arrays;
import java.util.List;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.util.Debug;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

// TODO this test needs "proper" data setup work.
public class GermplasmDAOTest extends IntegrationTestBase {

	private static final String DUMMY_STOCK_ID = "USER-1-1";
	private static final Integer testGid1 = 1;
	private static Integer testTransactionID;
	private static String oldInventoryID;
	private boolean testDataSetup = false;

	private GermplasmDAO dao;
	private Germplasm testGermplasm;

	@Autowired
	private InventoryDataManager inventoryDM;

	@Before
	public void setUp() throws Exception {
		if (this.dao == null) {
			this.dao = new GermplasmDAO();
			this.dao.setSession(this.sessionProvder.getSession());
		}

		if (!this.testDataSetup) {
			this.updateInventory();
			this.updateProgenitors();
			this.testDataSetup = true;
		}

		if (this.testGermplasm == null) {
			this.testGermplasm = new Germplasm();
			this.testGermplasm.setMethodId(1);
			this.testGermplasm.setGnpgs(0);
			this.testGermplasm.setGpid1(0);
			this.testGermplasm.setGpid2(0);
			this.testGermplasm.setUserId(1);
			this.testGermplasm.setLgid(1);
			this.testGermplasm.setLocationId(1);
			this.testGermplasm.setGdate(20000101);
			this.testGermplasm.setGrplce(0);
			this.testGermplasm.setReferenceId(0);
			this.testGermplasm.setMgid(0);
			this.dao.save(this.testGermplasm);
		}
	}

	private void updateProgenitors() throws MiddlewareQueryException {
		/*
		 * Germplasm germplasm1 = this.germplasmDataDM.getGermplasmByGID(GermplasmDAOTest.testGid1); GermplasmDAOTest.oldGid1_Gpid1 =
		 * germplasm1.getGpid1(); GermplasmDAOTest.oldGid1_Gpid2 = germplasm1.getGpid2();
		 * this.germplasmDataDM.updateProgenitor(GermplasmDAOTest.testGid1, GermplasmDAOTest.testGid1_Gpid1, 1);
		 * this.germplasmDataDM.updateProgenitor(GermplasmDAOTest.testGid1, GermplasmDAOTest.testGid1_Gpid2, 2);
		 */
	}

	private void updateInventory() throws MiddlewareQueryException {
		final List<Transaction> transactions = this.inventoryDM.getAllTransactions(0, 1);
		if (transactions != null && !transactions.isEmpty()) {
			final Transaction transaction = transactions.get(0);
			GermplasmDAOTest.testTransactionID = transaction.getId();
			GermplasmDAOTest.oldInventoryID = transaction.getInventoryID();
			transaction.setInventoryID(GermplasmDAOTest.DUMMY_STOCK_ID);
			this.inventoryDM.updateTransaction(transaction);
		}
	}

	@Test
	public void testGetDerivativeChildren() throws Exception {
		final Integer gid = Integer.valueOf(1);
		// List<Germplasm> results = dao.getDerivativeChildren(gid);
		final List<Germplasm> results = this.dao.getChildren(gid, 'D');
		Assert.assertNotNull(results);
		Debug.println(0, "testGetDerivativeChildren(GId=" + gid + ") RESULTS:");
		for (final Germplasm g : results) {
			Debug.println(0, "  " + g.getGid() + " : " + g.getPreferredName().getNval());
		}
	}

	@Test
	public void testGetMaintenanceChildren() throws Exception {
		final Integer gid = Integer.valueOf(1);
		final List<Germplasm> results = this.dao.getChildren(gid, 'M');
		Assert.assertNotNull(results);
		Debug.println(0, "testGetMaintenanceChildren(GId=" + gid + ") RESULTS:");
		for (final Germplasm g : results) {
			Debug.println(0, "  " + g.getGid() + " : " + g.getPreferredName().getNval());
		}
	}

	@Test
	public void testSearchForGermplasmsExactMatchGID() throws Exception {

		final List<Germplasm> results = this.dao.searchForGermplasms("1", Operation.EQUAL, false, false);
		Assert.assertTrue(results.size() == 1);

	}

	@Test
	public void testSearchForGermplasmsExactMatchGermplasmName() throws Exception {

		List<Germplasm> results = this.dao.searchForGermplasms("(CML454 X CML451)-B-3-1-1", Operation.EQUAL, false, false);
		Assert.assertTrue(results.size() == 1);

		results = this.dao.searchForGermplasms("(CML454 X CML451)", Operation.EQUAL, false, false);
		Assert.assertTrue(results.isEmpty());

	}

	@Test
	public void testSearchForGermplasmsStartsWithGID() throws Exception {

		final List<Germplasm> results = this.dao.searchForGermplasms("1%", Operation.LIKE, false, false);
		Assert.assertFalse(results.isEmpty());
	}

	@Test
	public void testSearchForGermplasmsStartsWithGermplasmName() throws Exception {

		final List<Germplasm> results = this.dao.searchForGermplasms("(CML454%", Operation.LIKE, false, false);
		Assert.assertFalse(results.isEmpty());

	}

	@Test
	public void testSearchForGermplasmsContainsGID() throws Exception {

		final List<Germplasm> results = this.dao.searchForGermplasms("%1%", Operation.LIKE, false, false);
		Assert.assertFalse(results.isEmpty());

		final List<Germplasm> startsWithResults = this.dao.searchForGermplasms("1%", Operation.LIKE, false, false);
		Assert.assertTrue(results.containsAll(startsWithResults));
	}

	@Test
	public void testSearchForGermplasmsContainsGermplasmName() throws Exception {

		final List<Germplasm> results = this.dao.searchForGermplasms("%CML454%", Operation.LIKE, false, false);
		Assert.assertFalse(results.isEmpty());

		final List<Germplasm> startsWithResults = this.dao.searchForGermplasms("CML454%", Operation.LIKE, false, false);
		Assert.assertTrue(results.containsAll(startsWithResults));

	}

	@Test
	public void testSearchForGermplasmsByInventoryId_ExactMatch() throws Exception {
		final List<Germplasm> results = this.dao.searchForGermplasmsByInventoryId(GermplasmDAOTest.DUMMY_STOCK_ID, Operation.EQUAL, "");
		Assert.assertNotNull(results);
		Assert.assertTrue(results.size() == 1);
	}

	@Test
	public void testSearchForGermplasmsByInventoryId_StartsWith() throws Exception {
		final String inventoryID = GermplasmDAOTest.DUMMY_STOCK_ID.substring(0, 3) + "%";
		final List<Germplasm> results = this.dao.searchForGermplasmsByInventoryId(inventoryID, Operation.LIKE, "");
		Assert.assertNotNull(results);
		Assert.assertFalse(results.isEmpty());
	}

	@Test
	public void testSearchForGermplasmsByInventoryId_Contains() throws Exception {
		final String inventoryID = "%" + GermplasmDAOTest.DUMMY_STOCK_ID.substring(0, 3) + "%";
		final List<Germplasm> results = this.dao.searchForGermplasmsByInventoryId(inventoryID, Operation.LIKE, "");
		Assert.assertNotNull(results);
		Assert.assertFalse(results.isEmpty());

		final List<Germplasm> startsWithResults =
				this.dao.searchForGermplasms(GermplasmDAOTest.DUMMY_STOCK_ID.substring(0, 3) + "%", Operation.LIKE, false, false);
		Assert.assertTrue(results.containsAll(startsWithResults));
	}

	@Test
	public void testSearchForGermplasmsWithInventory() throws Exception {
		final List<Germplasm> results = this.dao.searchForGermplasms("1%", Operation.LIKE, false, false);
		final List<Germplasm> resultsWithInventoryOnly = this.dao.searchForGermplasms("1%", Operation.LIKE, false, true);
		Assert.assertNotEquals(results.size(), resultsWithInventoryOnly.size());
	}

	@Test
	public void testSearchForGermplasmsIncludeParents() throws Exception {
		List<Germplasm> results = this.dao.searchForGermplasms(GermplasmDAOTest.testGid1.toString(), Operation.EQUAL, false, false);
		List<Germplasm> resultsWithParents =
				this.dao.searchForGermplasms(GermplasmDAOTest.testGid1.toString(), Operation.EQUAL, true, false);
		Assert.assertNotEquals(results.size(), resultsWithParents.size());
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(3, resultsWithParents.size());

		results = this.dao.searchForGermplasms("2", Operation.EQUAL, false, false);
		resultsWithParents = this.dao.searchForGermplasms("2", Operation.EQUAL, true, false);
		Assert.assertEquals(results.size(), resultsWithParents.size());
	}

	@Test
	public void testSearchForGermplasmsEmptyKeyword() throws Exception {
		final List<Germplasm> results = this.dao.searchForGermplasms("", Operation.EQUAL, false, false);
		Assert.assertTrue(results.isEmpty());
	}

	@Test
	public void testGetExistingGIDs() {
		final List<Integer> gidsToVerify = Arrays.asList(new Integer[] {12345678, 8765432, this.testGermplasm.getGid()});
		final List<Integer> existingGids = this.dao.getExistingGIDs(gidsToVerify);
		Assert.assertFalse(existingGids.isEmpty());
		Assert.assertEquals(1, existingGids.size());
		Assert.assertEquals(this.testGermplasm.getGid(), existingGids.get(0));
	}
}
