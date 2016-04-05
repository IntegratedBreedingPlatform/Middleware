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

import java.util.List;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
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
	private static final Integer testGid1_Gpid1 = 2;
	private static final Integer testGid1_Gpid2 = 3;

	private static Integer testTransactionID;
	private static String oldInventoryID;
	private static Integer oldGid1_Gpid1;
	private static Integer oldGid1_Gpid2;

	private boolean testDataSetup = false;

	private GermplasmDAO dao;

	@Autowired
	private InventoryDataManager inventoryDM;

	@Autowired
	private GermplasmDataManager germplasmDataDM;

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
	}

	private void updateProgenitors() throws MiddlewareQueryException {
		/*Germplasm germplasm1 = this.germplasmDataDM.getGermplasmByGID(GermplasmDAOTest.testGid1);
		GermplasmDAOTest.oldGid1_Gpid1 = germplasm1.getGpid1();
		GermplasmDAOTest.oldGid1_Gpid2 = germplasm1.getGpid2();
		this.germplasmDataDM.updateProgenitor(GermplasmDAOTest.testGid1, GermplasmDAOTest.testGid1_Gpid1, 1);
		this.germplasmDataDM.updateProgenitor(GermplasmDAOTest.testGid1, GermplasmDAOTest.testGid1_Gpid2, 2);*/
	}

	private void updateInventory() throws MiddlewareQueryException {
		List<Transaction> transactions = this.inventoryDM.getAllTransactions(0, 1);
		if (transactions != null && !transactions.isEmpty()) {
			Transaction transaction = transactions.get(0);
			GermplasmDAOTest.testTransactionID = transaction.getId();
			GermplasmDAOTest.oldInventoryID = transaction.getInventoryID();
			transaction.setInventoryID(GermplasmDAOTest.DUMMY_STOCK_ID);
			this.inventoryDM.updateTransaction(transaction);
		}
	}

	@Test
	public void testGetDerivativeChildren() throws Exception {
		Integer gid = Integer.valueOf(1);
		// List<Germplasm> results = dao.getDerivativeChildren(gid);
		List<Germplasm> results = this.dao.getChildren(gid, 'D');
		Assert.assertNotNull(results);
		Debug.println(0, "testGetDerivativeChildren(GId=" + gid + ") RESULTS:");
		for (Germplasm g : results) {
			Debug.println(0, "  " + g.getGid() + " : " + g.getPreferredName().getNval());
		}
	}

	@Test
	public void testGetMaintenanceChildren() throws Exception {
		Integer gid = Integer.valueOf(1);
		List<Germplasm> results = this.dao.getChildren(gid, 'M');
		Assert.assertNotNull(results);
		Debug.println(0, "testGetMaintenanceChildren(GId=" + gid + ") RESULTS:");
		for (Germplasm g : results) {
			Debug.println(0, "  " + g.getGid() + " : " + g.getPreferredName().getNval());
		}
	}

	@Test
	public void testSearchForGermplasmsExactMatchGID() throws Exception {

		List<Germplasm> results = this.dao.searchForGermplasms("1", Operation.EQUAL, false, false, false);
		Assert.assertTrue(results.size() == 1);

	}

	@Test
	public void testSearchForGermplasmsExactMatchGermplasmName() throws Exception {

		List<Germplasm> results = this.dao.searchForGermplasms("(CML454 X CML451)-B-3-1-1", Operation.EQUAL, false, false, false);
		Assert.assertTrue(results.size() == 1);

		results = this.dao.searchForGermplasms("(CML454 X CML451)", Operation.EQUAL, false, false, false);
		Assert.assertTrue(results.isEmpty());

	}

	@Test
	public void testSearchForGermplasmsStartsWithGID() throws Exception {

		List<Germplasm> results = this.dao.searchForGermplasms("1%", Operation.LIKE, false, false, false);
		Assert.assertFalse(results.isEmpty());
	}

	@Test
	public void testSearchForGermplasmsStartsWithGermplasmName() throws Exception {

		List<Germplasm> results = this.dao.searchForGermplasms("(CML454%", Operation.LIKE, false, false, false);
		Assert.assertFalse(results.isEmpty());

	}

	@Test
	public void testSearchForGermplasmsContainsGID() throws Exception {

		List<Germplasm> results = this.dao.searchForGermplasms("%1%", Operation.LIKE, false, false, false);
		Assert.assertFalse(results.isEmpty());

		List<Germplasm> startsWithResults = this.dao.searchForGermplasms("1%", Operation.LIKE, false, false, false);
		Assert.assertTrue(results.containsAll(startsWithResults));
	}

	@Test
	public void testSearchForGermplasmsContainsGermplasmName() throws Exception {

		List<Germplasm> results = this.dao.searchForGermplasms("%CML454%", Operation.LIKE, false, false, false);
		Assert.assertFalse(results.isEmpty());

		List<Germplasm> startsWithResults = this.dao.searchForGermplasms("CML454%", Operation.LIKE, false, false, false);
		Assert.assertTrue(results.containsAll(startsWithResults));

	}

	@Test
	public void testSearchForGermplasmsByInventoryId_ExactMatch() throws Exception {
		List<Germplasm> results = this.dao.searchForGermplasmsByInventoryId(GermplasmDAOTest.DUMMY_STOCK_ID, Operation.EQUAL, "");
		Assert.assertNotNull(results);
		Assert.assertTrue(results.size() == 1);
	}

	@Test
	public void testSearchForGermplasmsByInventoryId_StartsWith() throws Exception {
		String inventoryID = GermplasmDAOTest.DUMMY_STOCK_ID.substring(0, 3) + "%";
		List<Germplasm> results = this.dao.searchForGermplasmsByInventoryId(inventoryID, Operation.LIKE, "");
		Assert.assertNotNull(results);
		Assert.assertFalse(results.isEmpty());
	}

	@Test
	public void testSearchForGermplasmsByInventoryId_Contains() throws Exception {
		String inventoryID = "%" + GermplasmDAOTest.DUMMY_STOCK_ID.substring(0, 3) + "%";
		List<Germplasm> results = this.dao.searchForGermplasmsByInventoryId(inventoryID, Operation.LIKE, "");
		Assert.assertNotNull(results);
		Assert.assertFalse(results.isEmpty());

		List<Germplasm> startsWithResults =
				this.dao.searchForGermplasms(GermplasmDAOTest.DUMMY_STOCK_ID.substring(0, 3) + "%", Operation.LIKE, false, false, false);
		Assert.assertTrue(results.containsAll(startsWithResults));
	}

	@Test
	public void testSearchForGermplasmsWithInventory() throws Exception {
		List<Germplasm> results = this.dao.searchForGermplasms("1%", Operation.LIKE, false, false, false);
		List<Germplasm> resultsWithInventoryOnly = this.dao.searchForGermplasms("1%", Operation.LIKE, false, true, false);
		Assert.assertNotEquals(results.size(), resultsWithInventoryOnly.size());
	}

	@Test
	public void testSearchForGermplasmsIncludeParents() throws Exception {
		List<Germplasm> results = this.dao.searchForGermplasms(GermplasmDAOTest.testGid1.toString(), Operation.EQUAL, false, false, false);
		List<Germplasm> resultsWithParents =
				this.dao.searchForGermplasms(GermplasmDAOTest.testGid1.toString(), Operation.EQUAL, true, false, false);
		Assert.assertNotEquals(results.size(), resultsWithParents.size());
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(3, resultsWithParents.size());

		results = this.dao.searchForGermplasms("2", Operation.EQUAL, false, false, false);
		resultsWithParents = this.dao.searchForGermplasms("2", Operation.EQUAL, true, false, false);
		Assert.assertEquals(results.size(), resultsWithParents.size());
	}

	@Test
	public void testSearchForGermplasmsEmptyKeyword() throws Exception {
		List<Germplasm> results = this.dao.searchForGermplasms("", Operation.EQUAL, false, false, false);
		Assert.assertTrue(results.isEmpty());
	}

	@Test
	public void testGetAllChildren() {
		final int gid = 2425278;
		List<Germplasm> children = this.dao.getAllChildren(gid);
		Assert.assertNotNull("getAllChildren() should never return null.", children);
	}

	@Test
	public void testGetPreviousCrosses() {
		Germplasm female = new Germplasm(2);
		Germplasm male = new Germplasm(1);

		Germplasm currentCross = new Germplasm(3);
		currentCross.setGpid1(female.getGid());
		currentCross.setGpid2(male.getGid());

		List<Germplasm> previousCrosses = this.dao.getPreviousCrosses(currentCross, female, male);
		Assert.assertNotNull("getPreviousCrosses() should never return null.", previousCrosses);
	}

	@Test
	public void testLoadEntityWithNameCollection() {
		Germplasm germplasm = this.dao.getById(1);
		if (germplasm != null) {
			Assert.assertTrue("If germplasm exists, the name collection can not be empty.", !germplasm.getNames().isEmpty());
		}
	}

	@Test
	public void testGetManagementGroupMembers() {
		List<Germplasm> groupMembers = this.dao.getManagementGroupMembers(1);
		Assert.assertNotNull("getManagementGroupMembers() should never return null when supplied with proper mgid.", groupMembers);

		this.dao.getManagementGroupMembers(null);
		Assert.assertTrue("getManagementGroupMembers() should return empty collection when supplied mgid = null.", groupMembers.isEmpty());

		this.dao.getManagementGroupMembers(0);
		Assert.assertTrue("getManagementGroupMembers() should return empty collection when supplied mgid = 0.", groupMembers.isEmpty());
	}

	@Test
	public void testSaveGermplasmNamesThroughHibernateCascade() {

		Germplasm germplasm = new Germplasm();
		germplasm.setMethodId(1);
		germplasm.setGnpgs(-1);
		germplasm.setGpid1(0);
		germplasm.setGpid2(0);
		germplasm.setUserId(1);
		germplasm.setLgid(0);
		germplasm.setLocationId(1);
		germplasm.setGdate(20160101);
		germplasm.setReferenceId(0);
		germplasm.setGrplce(0);
		germplasm.setMgid(0);

		this.dao.save(germplasm);
		Assert.assertNotNull(germplasm.getGid());

		Name name1 = new Name();
		name1.setTypeId(5);
		name1.setNstat(1);
		name1.setUserId(1);
		name1.setNval("Name1");
		name1.setLocationId(1);
		name1.setNdate(20160101);
		name1.setReferenceId(0);

		Name name2 = new Name();
		name2.setTypeId(5);
		name2.setNstat(1);
		name2.setUserId(1);
		name2.setNval("Name2");
		name2.setLocationId(1);
		name2.setNdate(20160101);
		name2.setReferenceId(0);

		germplasm.getNames().add(name1);
		germplasm.getNames().add(name2);

		// Name collection mapping is uni-directional OneToMany right now, so the other side of the relationship has to be managed manually.
		for (Name name : germplasm.getNames()) {
			name.setGermplasmId(germplasm.getGid());
		}

		// In real app flush will happen automatically on tx commit. We don't commit tx in tests, so flush manually.
		this.sessionProvder.getSession().flush();

		for (Name name : germplasm.getNames()) {
			// No explicit save of name entity anywhere but should still be saved through cascade on flush.
			Assert.assertNotNull(name.getNid());
			Assert.assertEquals(germplasm.getGid(), name.getGermplasmId());
		}
	}

}
