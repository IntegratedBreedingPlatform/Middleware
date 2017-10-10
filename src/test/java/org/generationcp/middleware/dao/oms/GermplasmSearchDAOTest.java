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

package org.generationcp.middleware.dao.oms;

import com.google.common.collect.Lists;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.GermplasmListDAO;
import org.generationcp.middleware.dao.GermplasmSearchDAO;
import org.generationcp.middleware.dao.ListDataProjectDAO;
import org.generationcp.middleware.dao.MethodDAO;
import org.generationcp.middleware.dao.ims.LotDAO;
import org.generationcp.middleware.dao.ims.TransactionDAO;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.data.initializer.InventoryDetailsTestDataInitializer;
import org.generationcp.middleware.domain.gms.search.GermplasmSearchParameter;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.ListDataProject;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GermplasmSearchDAOTest extends IntegrationTestBase {

	private static final Integer GROUP_ID = 10;

	private GermplasmSearchDAO dao;

	private Integer germplasmGID;

	private Name preferredName;

	@Autowired
	private GermplasmDataManager germplasmDataDM;

	@Before
	public void setUp() throws Exception {
		if (this.dao == null) {
			this.dao = new GermplasmSearchDAO();
			this.dao.setSession(this.sessionProvder.getSession());

		}

		this.initializeGermplasms();
	}

	@Test
	public void testSearchForGermplasmsExactMatchGID() throws Exception {
		final List<Germplasm> results =
				this.dao.searchForGermplasms(this.createSearchParam(this.germplasmGID.toString(), Operation.EQUAL, false, false, false));
		Assert.assertEquals("The results should contain only one germplasm since the gid is unique.", 1, results.size());
		this.assertPossibleGermplasmFields(results);
	}


	@Test
	public void testSearchForGermplasmsExactMatchGermplasmName() throws Exception {
		final List<Germplasm> results =
				this.dao.searchForGermplasms(this.createSearchParam(this.preferredName.getNval(), Operation.EQUAL, false, false, false));
		Assert.assertEquals(
				"The results should contain one germplasm since there's only one test data with '" + this.preferredName.getNval()
						+ "' name", 1, results.size());
		this.assertPossibleGermplasmFields(results);
	}

	@Test
	public void testSearchForGermplasmsStartsWithGID() throws Exception {
		final List<Germplasm> results =
				this.dao.searchForGermplasms(this.createSearchParam(this.germplasmGID.toString() + "%", Operation.LIKE, false, false, false));
		Assert.assertEquals("The results should contain one germplasm since there's only one test data with gid that starts with "
				+ this.germplasmGID, 1, results.size());
		this.assertPossibleGermplasmFields(results);
	}

	@Test
	public void testSearchForGermplasmsStartsWithGermplasmName() throws Exception {

		final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(20150101, 12, 13, 1, 0, 0 , 1 ,1 ,0, 1 ,1 , "MethodName",
				"LocationName");
		germplasm.getPreferredName().setNval("GermplasmName");
		this.germplasmDataDM.addGermplasm(germplasm, germplasm.getPreferredName());

		final List<Germplasm> results =
				this.dao.searchForGermplasms(this.createSearchParam(germplasm.getPreferredName().getNval() + "%", Operation.LIKE, false, false, false));
		Assert.assertEquals("The results should contain one germplasm since there's only one test data with name that starts with "
				+ germplasm.getPreferredName().getNval(), 1, results.size());
		Assert.assertTrue(germplasm.getPreferredName().getNval().contains("GermplasmName"));
	}

	@Test
	public void testSearchForGermplasmsContainsGID() throws Exception {
		final List<Germplasm> results =
				this.dao.searchForGermplasms(this.createSearchParam("%" + this.germplasmGID.toString() + "%", Operation.LIKE, false, false,
						false));
		Assert.assertEquals("The results should contain one germplasm since there's only one test data with gid that contains "
				+ this.germplasmGID, 1, results.size());
		this.assertPossibleGermplasmFields(results);
	}

	@Test
	public void testSearchForGermplasmsContainsGermplasmName() throws Exception {
		final List<Germplasm> results =
				this.dao.searchForGermplasms(this.createSearchParam("%" + this.preferredName.getNval() + "%", Operation.LIKE, false, false,
						false));
		Assert.assertTrue("The results should contain one germplasm since there's only one test data with name that contains "
				+ this.preferredName.getNval(), results.size() == 1);
		this.assertPossibleGermplasmFields(results);
	}

	@Test
	public void testSearchForGermplasmsIncludeParents() throws Exception {

		final Germplasm parentGermplasm = GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0 , 1 ,1 ,0, 1 ,1 ,
				"MethodName", "LocationName");
		final Integer parentGermplasmId = this.germplasmDataDM.addGermplasm(parentGermplasm, parentGermplasm.getPreferredName());

		final Germplasm childGermplasm = GermplasmTestDataInitializer.createGermplasm(20150101, parentGermplasm.getGid(), 2, 2, 0, 0 , 1 ,1 ,
				0, 1 ,1 , "MethodName", "LocationName");
		final Integer childGermplasmId = this.germplasmDataDM.addGermplasm(childGermplasm, childGermplasm.getPreferredName());

		final List<Germplasm> results =
				this.dao.searchForGermplasms(this.createSearchParam(childGermplasm.getGid().toString(), Operation.EQUAL, true, false,
						false));

		Assert.assertTrue("Result should include both child and parent germplasms", results.size() >=2 );
		List<Integer> resultGIDs = Lists.newArrayList();
		for(Germplasm germplasm : results) {
			resultGIDs.add(germplasm.getGid());
		}

		Assert.assertTrue("Parent germplasm should be included in search result", resultGIDs.contains(parentGermplasmId));
		Assert.assertTrue("Child germplasm should be included in search result", resultGIDs.contains(childGermplasmId));
	}

	@Test
	public void testSearchForGermplasmsEmptyKeyword() throws Exception {
		final List<Germplasm> results = this.dao.searchForGermplasms(this.createSearchParam("", Operation.EQUAL, false, false, false));
		Assert.assertTrue(results.isEmpty());
	}

	@Test
	public void testSearchForGermplasmsIncludeMGMembers() throws Exception {
		final List<Germplasm> results =
				this.dao.searchForGermplasms(this.createSearchParam(this.germplasmGID.toString(), Operation.EQUAL, false, false, true));
		Assert.assertEquals("The result should contain 2 germplasms (one is the actual result and the other is the MG member)", 2,
				results.size());
		this.assertPossibleGermplasmFields(results);
	}

	private void initializeGermplasms() {
		final Germplasm fParent = GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0 , 1 ,1 ,0, 1 ,1 , "MethodName", "LocationName");
		final Integer fParentGID = this.germplasmDataDM.addGermplasm(fParent, fParent.getPreferredName());

		final Germplasm mParent = GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0 , 1 ,1 ,0, 1 ,1 , "MethodName", "LocationName");
		final Integer mParentGID = this.germplasmDataDM.addGermplasm(mParent, mParent.getPreferredName());

		final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(20150101, fParentGID, mParentGID, 2, 0, 0 , 1 ,1 ,
				GermplasmSearchDAOTest.GROUP_ID, 1 ,1 , "MethodName", "LocationName");
		this.preferredName = germplasm.getPreferredName();
		this.germplasmGID = this.germplasmDataDM.addGermplasm(germplasm, germplasm.getPreferredName());

		final Germplasm mgMember = GermplasmTestDataInitializer.createGermplasm(20150101, fParentGID, mParentGID, 2, 0, 0 , 1 ,1 ,
				GermplasmSearchDAOTest.GROUP_ID, 1 ,1 , "MethodName", "LocationName");
		this.germplasmDataDM.addGermplasm(mgMember, mgMember.getPreferredName());
	}

	private GermplasmSearchParameter createSearchParam(final String searchKeyword, final Operation operation, final boolean includeParents,
			final boolean withInventoryOnly, final boolean includeMGMembers) {
		final GermplasmSearchParameter searchParam =
				new GermplasmSearchParameter(searchKeyword, operation, includeParents, withInventoryOnly, includeMGMembers);
		searchParam.setStartingRow(0);
		searchParam.setNumberOfEntries(25);
		return searchParam;
	}

	/**
	 * Method to assert fields contained by germplasm search germplasmSearchResults.
	 * Tried to assert general possible fields for Germplasm.
	 *
	 * @param germplasmSearchResults Germplasm Search Results
	 */
	private void assertPossibleGermplasmFields(List<Germplasm> germplasmSearchResults) {
		// Assert possible germplasm member fields
		for (Germplasm germplasm : germplasmSearchResults) {
			Assert.assertNotEquals("Gpid1 should not be 0", Integer.valueOf(0), germplasm.getGpid1());
			Assert.assertNotEquals("Gpid2 should not be 0", Integer.valueOf(0), germplasm.getGpid2());
			Assert.assertNotEquals("Gnpgs should not be 0", Integer.valueOf(0), germplasm.getGnpgs());
			Assert.assertEquals("Result should contain Method Name", "Unknown generative method", germplasm.getMethodName());
			Assert.assertEquals("Result should contain Location Name", "Afghanistan", germplasm.getLocationName());
			Assert.assertEquals("Result should contain Germplasm Number of Progenitor", Integer.valueOf(2), germplasm.getGnpgs());
			Assert.assertEquals("Result should contain Germplasm Date", Integer.valueOf(20150101), germplasm.getGdate());
			Assert.assertEquals("Result should contain Reference Id", Integer.valueOf(1), germplasm.getReferenceId());
		}
	}

}
