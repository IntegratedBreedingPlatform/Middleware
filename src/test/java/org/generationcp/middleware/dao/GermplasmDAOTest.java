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
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
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

	private static final Integer GROUP_ID = 10;

	private boolean testDataSetup = false;

	private GermplasmDAO dao;

	private Integer germplasmGID;

	private Name preferredName;

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
		this.initializeGermplasms();
	}

	private void updateProgenitors() throws MiddlewareQueryException {
		this.germplasmDataDM.updateProgenitor(GermplasmDAOTest.testGid1, GermplasmDAOTest.testGid1_Gpid1, 1);
		this.germplasmDataDM.updateProgenitor(GermplasmDAOTest.testGid1, GermplasmDAOTest.testGid1_Gpid2, 2);
	}

	private void updateInventory() throws MiddlewareQueryException {
		final List<Transaction> transactions = this.inventoryDM.getAllTransactions(0, 1);
		if (transactions != null && !transactions.isEmpty()) {
			final Transaction transaction = transactions.get(0);
			transaction.setInventoryID(GermplasmDAOTest.DUMMY_STOCK_ID);
			this.inventoryDM.updateTransaction(transaction);
		}
	}

	@Test
	public void testGetDerivativeChildren() throws Exception {
		final Integer gid = Integer.valueOf(1);
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
		final List<Germplasm> results = this.dao.searchForGermplasms(this.germplasmGID.toString(), Operation.EQUAL, false, false, false);
		Assert.assertEquals("The results should contain only one germplasm since the gid is unique.", 1, results.size());
	}

	@Test
	public void testSearchForGermplasmsExactMatchGermplasmName() throws Exception {
		final List<Germplasm> results = this.dao.searchForGermplasms(this.preferredName.getNval(), Operation.EQUAL, false, false, false);
		Assert.assertEquals(
				"The results should contain one germplasm since there's only one test data with '" + this.preferredName.getNval()
						+ "' name", 1, results.size());
	}

	@Test
	public void testSearchForGermplasmsStartsWithGID() throws Exception {
		final List<Germplasm> results =
				this.dao.searchForGermplasms(this.germplasmGID.toString() + "%", Operation.LIKE, false, false, false);
		Assert.assertEquals("The results should contain one germplasm since there's only one test data with gid that starts with "
				+ this.germplasmGID, 1, results.size());
	}

	@Test
	public void testSearchForGermplasmsStartsWithGermplasmName() throws Exception {
		final List<Germplasm> results =
				this.dao.searchForGermplasms(this.preferredName.getNval() + "%", Operation.LIKE, false, false, false);
		Assert.assertEquals("The results should contain one germplasm since there's only one test data with name that starts with "
				+ this.preferredName.getNval(), 1, results.size());
	}

	@Test
	public void testSearchForGermplasmsContainsGID() throws Exception {
		final List<Germplasm> results =
				this.dao.searchForGermplasms("%" + this.germplasmGID.toString() + "%", Operation.LIKE, false, false, false);
		Assert.assertEquals("The results should contain one germplasm since there's only one test data with gid that contains "
				+ this.germplasmGID, 1, results.size());
	}

	@Test
	public void testSearchForGermplasmsContainsGermplasmName() throws Exception {
		final List<Germplasm> results =
				this.dao.searchForGermplasms("%" + this.preferredName.getNval() + "%", Operation.LIKE, false, false, false);
		Assert.assertTrue("The results should contain one germplasm since there's only one test data with name that contains "
				+ this.preferredName.getNval(), results.size() == 1);
	}

	@Test
	public void testSearchForGermplasmsWithInventory() throws Exception {
		final List<Germplasm> results = this.dao.searchForGermplasms("1%", Operation.LIKE, false, false, false);
		final List<Germplasm> resultsWithInventoryOnly = this.dao.searchForGermplasms("1%", Operation.LIKE, false, true, false);
		Assert.assertNotEquals(results.size(), resultsWithInventoryOnly.size());
	}

	@Test
	public void testSearchForGermplasmsIncludeParents() throws Exception {
		final List<Germplasm> results = this.dao.searchForGermplasms(this.germplasmGID.toString(), Operation.EQUAL, true, false, false);
		Assert.assertEquals(
				"The result should contain three germplasms(one is the actual result and the other two is the male and female parents)", 3,
				results.size());
	}

	@Test
	public void testSearchForGermplasmsEmptyKeyword() throws Exception {
		final List<Germplasm> results = this.dao.searchForGermplasms("", Operation.EQUAL, false, false, false);
		Assert.assertTrue(results.isEmpty());
	}

	@Test
	public void testSearchForGermplasmsIncludeMGMembers() throws Exception {
		final List<Germplasm> results = this.dao.searchForGermplasms(this.germplasmGID.toString(), Operation.EQUAL, false, false, true);
		Assert.assertEquals("The result should contain 2 germplasms (one is the actual result and the other is the MG member)", 2,
				results.size());
	}

	@Test
	public void testGetAllChildren() {
		final int gid = 2425278;
		final List<Germplasm> children = this.dao.getAllChildren(gid);
		Assert.assertNotNull("getAllChildren() should never return null.", children);
	}

	@Test
	public void testGetPreviousCrosses() {
		final Germplasm female = new Germplasm(2);
		final Germplasm male = new Germplasm(1);

		final Germplasm currentCross = new Germplasm(3);
		currentCross.setGpid1(female.getGid());
		currentCross.setGpid2(male.getGid());

		final List<Germplasm> previousCrosses = this.dao.getPreviousCrosses(currentCross, female, male);
		Assert.assertNotNull("getPreviousCrosses() should never return null.", previousCrosses);
	}

	@Test
	public void testLoadEntityWithNameCollection() {
		final Germplasm germplasm = this.dao.getById(1);
		if (germplasm != null) {
			Assert.assertTrue("If germplasm exists, the name collection can not be empty.", !germplasm.getNames().isEmpty());
		}
	}

	@Test
	public void testGetManagementGroupMembers() {
		List<Germplasm> groupMembers = this.dao.getManagementGroupMembers(1);
		Assert.assertFalse("getManagementGroupMembers() should never return null when supplied with proper mgid.", groupMembers.isEmpty());

		groupMembers = this.dao.getManagementGroupMembers(null);
		Assert.assertTrue("getManagementGroupMembers() should return empty collection when supplied mgid = null.", groupMembers.isEmpty());

		groupMembers = this.dao.getManagementGroupMembers(0);
		Assert.assertTrue("getManagementGroupMembers() should return empty collection when supplied mgid = 0.", groupMembers.isEmpty());
	}

	@Test
	public void testSaveGermplasmNamesThroughHibernateCascade() {

		final Germplasm germplasm = new Germplasm();
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

		final Name name1 = new Name();
		name1.setTypeId(5);
		name1.setNstat(1);
		name1.setUserId(1);
		name1.setNval("Name1");
		name1.setLocationId(1);
		name1.setNdate(20160101);
		name1.setReferenceId(0);

		final Name name2 = new Name();
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
		for (final Name name : germplasm.getNames()) {
			name.setGermplasmId(germplasm.getGid());
		}

		// In real app flush will happen automatically on tx commit. We don't commit tx in tests, so flush manually.
		this.sessionProvder.getSession().flush();

		for (final Name name : germplasm.getNames()) {
			// No explicit save of name entity anywhere but should still be saved through cascade on flush.
			Assert.assertNotNull(name.getNid());
			Assert.assertEquals(germplasm.getGid(), name.getGermplasmId());
		}
	}

	private void initializeGermplasms() {
		final Germplasm fParent = GermplasmTestDataInitializer.createGermplasm(1001);
		final Integer fParentGID = this.germplasmDataDM.addGermplasm(fParent, fParent.getPreferredName());

		final Germplasm mParent = GermplasmTestDataInitializer.createGermplasm(1002);
		final Integer mParentGID = this.germplasmDataDM.addGermplasm(mParent, mParent.getPreferredName());

		final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(1003);
		germplasm.setMgid(GermplasmDAOTest.GROUP_ID);
		germplasm.setGpid1(fParentGID);
		germplasm.setGpid2(mParentGID);
		this.preferredName = germplasm.getPreferredName();
		this.germplasmGID = this.germplasmDataDM.addGermplasm(germplasm, germplasm.getPreferredName());

		final Germplasm mgMember = GermplasmTestDataInitializer.createGermplasm(1004);
		mgMember.setMgid(GermplasmDAOTest.GROUP_ID);
		this.germplasmDataDM.addGermplasm(mgMember, mgMember.getPreferredName());
	}

}
