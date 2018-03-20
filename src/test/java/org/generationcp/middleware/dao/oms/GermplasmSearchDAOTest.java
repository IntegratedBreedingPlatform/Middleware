/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.middleware.dao.oms;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.GermplasmSearchDAO;
import org.generationcp.middleware.dao.UserDefinedFieldDAO;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.domain.gms.search.GermplasmSearchParameter;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GermplasmSearchDAOTest extends IntegrationTestBase {

	private static final Integer GROUP_ID = 10;
	public static final String NOTE_ATTRIBUTE = "NOTE";

	private GermplasmSearchDAO dao;

	private UserDefinedFieldDAO userDefinedFieldDao;
	private GermplasmDAO germplasmDao;

	private Integer germplasmGID;
	private Integer femaleParentGID;
	private Integer maleParentGID;
	private Name preferredName;
	private Name preferredId;
	private Name maleParentPreferredName;
	private Name femaleParentPreferredName;
	private final int germplasmDate = 20150101;
	private String attributeValue;
	private final Map<String, Integer> attributeTypeMap = new HashMap<>();

	@Autowired
	private GermplasmDataManager germplasmDataDM;

	@Autowired
	private InventoryDataManager inventoryDataManager;

	@Before
	public void setUp() throws Exception {
		if (this.dao == null) {
			this.dao = new GermplasmSearchDAO();
			this.dao.setSession(this.sessionProvder.getSession());

		}
		if (this.germplasmDao == null) {
			this.germplasmDao = new GermplasmDAO();
			this.germplasmDao.setSession(this.sessionProvder.getSession());

		}
		if (this.userDefinedFieldDao == null) {
			this.userDefinedFieldDao = new UserDefinedFieldDAO();
			this.userDefinedFieldDao.setSession(this.sessionProvder.getSession());
		}

		this.initializeGermplasms();
		this.createTestGermplasmForSorting();
	}

	@Test
	public void testSearchForGermplasmsExactMatchGID() throws Exception {
		final List<Germplasm> results =
				this.dao.searchForGermplasms(this.createSearchParam(this.germplasmGID.toString(), Operation.EQUAL, false, false, false));
		Assert.assertEquals("The results should contain only one germplasm since the gid is unique.", 1, results.size());
		this.assertPossibleGermplasmFields(results);
		this.assertInventoryFields(results);
	}

	@Test
	public void testSearchForGermplasmsExactMatchGermplasmName() throws Exception {
		final List<Germplasm> results =
				this.dao.searchForGermplasms(this.createSearchParam(this.preferredName.getNval(), Operation.EQUAL, false, false, false));
		Assert.assertEquals(
				"The results should contain one germplasm since there's only one test data with '" + this.preferredName.getNval()
						+ "' name", 1, results.size());
		this.assertPossibleGermplasmFields(results);
		this.assertInventoryFields(results);
	}

	@Test
	public void testSearchForGermplasmsStartsWithGID() throws Exception {
		final List<Germplasm> results = this.dao.searchForGermplasms(
				this.createSearchParam(this.germplasmGID.toString() + "%", Operation.LIKE, false, false, false));
		Assert.assertEquals(
				"The results should contain one germplasm since there's only one test data with gid that starts with " + this.germplasmGID,
				1, results.size());
		this.assertPossibleGermplasmFields(results);
		this.assertInventoryFields(results);
	}

	@Test
	public void testSearchForGermplasmsStartsWithGermplasmName() throws Exception {

		final Germplasm germplasm =
				GermplasmTestDataInitializer.createGermplasm(germplasmDate, 12, 13, 1, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		germplasm.getPreferredName().setNval("GermplasmName");
		this.germplasmDataDM.addGermplasm(germplasm, germplasm.getPreferredName());

		final List<Germplasm> results = this.dao.searchForGermplasms(
				this.createSearchParam(germplasm.getPreferredName().getNval() + "%", Operation.LIKE, false, false, false));
		Assert.assertEquals(
				"The results should contain one germplasm since there's only one test data with name that starts with " + germplasm
						.getPreferredName().getNval(), 1, results.size());
		Assert.assertTrue(germplasm.getPreferredName().getNval().contains("GermplasmName"));
	}

	@Test
	public void testSearchForGermplasmsContainsGID() throws Exception {
		final List<Germplasm> results = this.dao.searchForGermplasms(
				this.createSearchParam("%" + this.germplasmGID.toString() + "%", Operation.LIKE, false, false, false));
		Assert.assertEquals(
				"The results should contain one germplasm since there's only one test data with gid that contains " + this.germplasmGID, 1,
				results.size());
		this.assertPossibleGermplasmFields(results);
		this.assertInventoryFields(results);
	}

	@Test
	public void testSearchForGermplasmsContainsGermplasmName() throws Exception {
		final List<Germplasm> results = this.dao.searchForGermplasms(
				this.createSearchParam("%" + this.preferredName.getNval() + "%", Operation.LIKE, false, false, false));
		Assert.assertTrue(
				"The results should contain one germplasm since there's only one test data with name that contains " + this.preferredName
						.getNval(), results.size() == 1);
		this.assertPossibleGermplasmFields(results);
		this.assertInventoryFields(results);
	}

	@Test
	public void testSearchForGermplasmsIncludeParents() throws Exception {

		final Germplasm parentGermplasm =
				GermplasmTestDataInitializer.createGermplasm(germplasmDate, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		final Integer parentGermplasmId = this.germplasmDataDM.addGermplasm(parentGermplasm, parentGermplasm.getPreferredName());

		final Germplasm childGermplasm = GermplasmTestDataInitializer
				.createGermplasm(germplasmDate, parentGermplasm.getGid(), 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		final Integer childGermplasmId = this.germplasmDataDM.addGermplasm(childGermplasm, childGermplasm.getPreferredName());

		final List<Germplasm> results = this.dao.searchForGermplasms(
				this.createSearchParam(childGermplasm.getGid().toString(), Operation.EQUAL, true, false, false));

		Assert.assertTrue("Result should include both child and parent germplasms", results.size() >= 2);
		final List<Integer> resultGIDs = Lists.newArrayList();
		for (final Germplasm germplasm : results) {
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

		final Germplasm actualResult = results.get(0);
		final Germplasm mgMember = results.get(1);
		Assert.assertEquals("Lot count should be 0", Integer.valueOf(1), actualResult.getInventoryInfo().getActualInventoryLotCount());
		Assert.assertEquals("Total Available Balance should be 100.0", Double.valueOf(100.0),
				actualResult.getInventoryInfo().getTotalAvailableBalance());
		Assert.assertEquals("Lot count for mgMember should be 0", Integer.valueOf(0),
				mgMember.getInventoryInfo().getActualInventoryLotCount());
		Assert.assertEquals("Total Available Balance for mgMember should be 0.0", Double.valueOf(0.0),
				mgMember.getInventoryInfo().getTotalAvailableBalance());
	}

	@Test
	public void testSearchForGermplasmsWithAllAddedColumns() throws Exception {

		final GermplasmSearchParameter searchParameter =
				this.createSearchParam(this.germplasmGID.toString(), Operation.EQUAL, false, false, false);

		final List<String> propertyIds = new LinkedList<>();

		// Create propertyId list with all addable columns.
		propertyIds.add(GermplasmSearchDAO.PREFERRED_ID);
		propertyIds.add(GermplasmSearchDAO.PREFERRED_NAME);
		propertyIds.add(GermplasmSearchDAO.GERMPLASM_DATE);
		propertyIds.add(GermplasmSearchDAO.METHOD_ABBREVIATION);
		propertyIds.add(GermplasmSearchDAO.METHOD_NUMBER);
		propertyIds.add(GermplasmSearchDAO.METHOD_GROUP);
		propertyIds.add(GermplasmSearchDAO.FEMALE_PARENT_ID);
		propertyIds.add(GermplasmSearchDAO.FEMALE_PARENT_PREFERRED_NAME);
		propertyIds.add(GermplasmSearchDAO.MALE_PARENT_ID);
		propertyIds.add(GermplasmSearchDAO.MALE_PARENT_PREFERRED_NAME);

		// Add Attributes column (NOTE attribute)
		propertyIds.add(NOTE_ATTRIBUTE);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);

		final List<Germplasm> results = this.dao.searchForGermplasms(searchParameter);

		Assert.assertEquals("The results should contain only one germplasm since the gid is unique.", 1, results.size());

		this.assertPossibleGermplasmFields(results);
		this.assertAddedGermplasmFields(results.get(0), propertyIds);

	}

	@Test
	public void testSearchForGermplasmsWithGermplasmDetailsColumnsOnly() throws Exception {

		final GermplasmSearchParameter searchParameter =
				this.createSearchParam(this.germplasmGID.toString(), Operation.EQUAL, false, false, false);

		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.PREFERRED_ID);
		propertyIds.add(GermplasmSearchDAO.PREFERRED_NAME);
		propertyIds.add(GermplasmSearchDAO.GERMPLASM_DATE);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);

		final List<Germplasm> results = this.dao.searchForGermplasms(searchParameter);

		Assert.assertEquals("The results should contain only one germplasm since the gid is unique.", 1, results.size());

		this.assertPossibleGermplasmFields(results);
		this.assertAddedGermplasmFields(results.get(0), propertyIds);

	}

	@Test
	public void testSearchForGermplasmsWithMethodDetailsColumnsOnly() throws Exception {

		final GermplasmSearchParameter searchParameter =
				this.createSearchParam(this.germplasmGID.toString(), Operation.EQUAL, false, false, false);

		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.METHOD_ABBREVIATION);
		propertyIds.add(GermplasmSearchDAO.METHOD_NUMBER);
		propertyIds.add(GermplasmSearchDAO.METHOD_GROUP);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);

		final List<Germplasm> results = this.dao.searchForGermplasms(searchParameter);

		Assert.assertEquals("The results should contain only one germplasm since the gid is unique.", 1, results.size());

		this.assertPossibleGermplasmFields(results);
		this.assertAddedGermplasmFields(results.get(0), propertyIds);

	}

	@Test
	public void testSearchForGermplasmsWithParentDetailsColumnsOnly() throws Exception {

		final GermplasmSearchParameter searchParameter =
				this.createSearchParam(this.germplasmGID.toString(), Operation.EQUAL, false, false, false);

		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.FEMALE_PARENT_ID);
		propertyIds.add(GermplasmSearchDAO.FEMALE_PARENT_PREFERRED_NAME);
		propertyIds.add(GermplasmSearchDAO.MALE_PARENT_ID);
		propertyIds.add(GermplasmSearchDAO.MALE_PARENT_PREFERRED_NAME);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);

		final List<Germplasm> results = this.dao.searchForGermplasms(searchParameter);

		Assert.assertEquals("The results should contain only one germplasm since the gid is unique.", 1, results.size());

		this.assertPossibleGermplasmFields(results);
		this.assertAddedGermplasmFields(results.get(0), propertyIds);

	}

	@Test
	public void testSearchForGermplasmsWithAttributeColumnOnly() throws Exception {

		final GermplasmSearchParameter searchParameter =
				this.createSearchParam(this.germplasmGID.toString(), Operation.EQUAL, false, false, false);

		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(NOTE_ATTRIBUTE);
		searchParameter.setAddedColumnsPropertyIds(propertyIds);

		final List<Germplasm> results = this.dao.searchForGermplasms(searchParameter);

		Assert.assertEquals("The results should contain only one germplasm since the gid is unique.", 1, results.size());

		this.assertPossibleGermplasmFields(results);
		this.assertAddedGermplasmFields(results.get(0), propertyIds);

	}

	@Test
	public void testSearchForGermplasmsPreferredIdSortAscending() {

		final GermplasmSearchParameter searchParameter =
				this.createSearchParam("GermplasmForSorting%", Operation.LIKE, false, false, false);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.PREFERRED_ID);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		searchParameter.setSortState(new Object[] {GermplasmSearchDAO.PREFERRED_ID}, new boolean[] {true});

		final List<Germplasm> results = this.dao.searchForGermplasms(searchParameter);

		final List<String> list = new ArrayList<>();
		for (final Germplasm g : results) {
			list.add(g.getGermplasmPeferredId());
		}

		// Check if the list is in ascending order
		Assert.assertTrue(Ordering.natural().isOrdered(list));
	}

	@Test
	public void testSearchForGermplasmsPreferredIdSortDescending() {

		final GermplasmSearchParameter searchParameter =
				this.createSearchParam("GermplasmForSorting%", Operation.LIKE, false, false, false);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.PREFERRED_ID);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		searchParameter.setSortState(new Object[] {GermplasmSearchDAO.PREFERRED_ID}, new boolean[] {false});

		final List<Germplasm> results = this.dao.searchForGermplasms(searchParameter);

		final List<String> list = new ArrayList<>();
		for (final Germplasm g : results) {
			list.add(g.getGermplasmPeferredId());
		}

		// Check if the list is in ascending order
		Assert.assertTrue(Ordering.natural().reverse().isOrdered(list));
	}

	@Test
	public void testSearchForGermplasmsPreferredNameSortAscending() {

		final GermplasmSearchParameter searchParameter =
				this.createSearchParam("GermplasmForSorting%", Operation.LIKE, false, false, false);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.PREFERRED_NAME);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		searchParameter.setSortState(new Object[] {GermplasmSearchDAO.PREFERRED_NAME}, new boolean[] {true});

		final List<Germplasm> results = this.dao.searchForGermplasms(searchParameter);

		final List<String> list = new ArrayList<>();
		for (final Germplasm g : results) {
			list.add(g.getGermplasmPeferredName());
		}

		// Check if the list is in ascending order
		Assert.assertTrue(Ordering.natural().isOrdered(list));

	}

	@Test
	public void testSearchForGermplasmsPreferredNameSortDescending() {

		final GermplasmSearchParameter searchParameter =
				this.createSearchParam("GermplasmForSorting%", Operation.LIKE, false, false, false);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.PREFERRED_NAME);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		searchParameter.setSortState(new Object[] {GermplasmSearchDAO.PREFERRED_NAME}, new boolean[] {false});

		final List<Germplasm> results = this.dao.searchForGermplasms(searchParameter);

		final List<String> list = new ArrayList<>();
		for (final Germplasm g : results) {
			list.add(g.getGermplasmPeferredName());
		}

		// Check if the list is in descending order
		Assert.assertTrue(Ordering.natural().reverse().isOrdered(list));

	}

	@Test
	public void testSearchForGermplasmsGermplasmDateSortAscending() {

		final GermplasmSearchParameter searchParameter =
				this.createSearchParam("GermplasmForSorting%", Operation.LIKE, false, false, false);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.GERMPLASM_DATE);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		searchParameter.setSortState(new Object[] {GermplasmSearchDAO.GERMPLASM_DATE}, new boolean[] {true});

		final List<Germplasm> results = this.dao.searchForGermplasms(searchParameter);

		final List<String> list = new ArrayList<>();
		for (final Germplasm g : results) {
			list.add(g.getGermplasmDate());
		}

		// Check if the list is in ascending order
		Assert.assertTrue(Ordering.natural().isOrdered(list));

	}

	@Test
	public void testSearchForGermplasmsGermplasmDateSortDescending() {

		final GermplasmSearchParameter searchParameter =
				this.createSearchParam("GermplasmForSorting%", Operation.LIKE, false, false, false);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.GERMPLASM_DATE);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		searchParameter.setSortState(new Object[] {GermplasmSearchDAO.GERMPLASM_DATE}, new boolean[] {false});

		final List<Germplasm> results = this.dao.searchForGermplasms(searchParameter);

		final List<String> list = new ArrayList<>();
		for (final Germplasm g : results) {
			list.add(g.getGermplasmDate());
		}

		// Check if the list is in descending order
		Assert.assertTrue(Ordering.natural().reverse().isOrdered(list));

	}

	@Test
	public void testSearchForGermplasmsGermplasmLocationSortAscending() {

		final GermplasmSearchParameter searchParameter =
				this.createSearchParam("GermplasmForSorting%", Operation.LIKE, false, false, false);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.LOCATION_NAME);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		searchParameter.setSortState(new Object[] {GermplasmSearchDAO.LOCATION_NAME}, new boolean[] {true});

		final List<Germplasm> results = this.dao.searchForGermplasms(searchParameter);

		final List<String> list = new ArrayList<>();
		for (final Germplasm g : results) {
			list.add(g.getLocationName());
		}

		// Check if the list is in ascending order
		Assert.assertTrue(Ordering.natural().isOrdered(list));

	}

	@Test
	public void testSearchForGermplasmsGermplasmLocationSortDescending() {

		final GermplasmSearchParameter searchParameter =
				this.createSearchParam("GermplasmForSorting%", Operation.LIKE, false, false, false);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.LOCATION_NAME);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		searchParameter.setSortState(new Object[] {GermplasmSearchDAO.LOCATION_NAME}, new boolean[] {false});

		final List<Germplasm> results = this.dao.searchForGermplasms(searchParameter);

		final List<String> list = new ArrayList<>();
		for (final Germplasm g : results) {
			list.add(g.getLocationName());
		}

		// Check if the list is in descending order
		Assert.assertTrue(Ordering.natural().reverse().isOrdered(list));

	}

	@Test
	public void testSearchForGermplasmsMethodNameSortAscending() {

		final GermplasmSearchParameter searchParameter =
				this.createSearchParam("GermplasmForSorting%", Operation.LIKE, false, false, false);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.METHOD_NAME);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		searchParameter.setSortState(new Object[] {GermplasmSearchDAO.METHOD_NAME}, new boolean[] {true});

		final List<Germplasm> results = this.dao.searchForGermplasms(searchParameter);

		final List<String> list = new ArrayList<>();
		for (final Germplasm g : results) {
			list.add(g.getMethodName());
		}

		// Check if the list is in ascending order
		Assert.assertTrue(Ordering.natural().isOrdered(list));

	}

	@Test
	public void testSearchForGermplasmsMethodNameSortDescending() {

		final GermplasmSearchParameter searchParameter =
				this.createSearchParam("GermplasmForSorting%", Operation.LIKE, false, false, false);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.METHOD_NAME);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		searchParameter.setSortState(new Object[] {GermplasmSearchDAO.METHOD_NAME}, new boolean[] {false});

		final List<Germplasm> results = this.dao.searchForGermplasms(searchParameter);

		final List<String> list = new ArrayList<>();
		for (final Germplasm g : results) {
			list.add(g.getMethodName());
		}

		// Check if the list is in descending order
		Assert.assertTrue(Ordering.natural().reverse().isOrdered(list));

	}

	@Test
	public void testSearchForGermplasmsMethodAbbreviationSortAscending() {

		final GermplasmSearchParameter searchParameter =
				this.createSearchParam("GermplasmForSorting%", Operation.LIKE, false, false, false);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.METHOD_ABBREVIATION);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		searchParameter.setSortState(new Object[] {GermplasmSearchDAO.METHOD_ABBREVIATION}, new boolean[] {true});

		final List<Germplasm> results = this.dao.searchForGermplasms(searchParameter);

		final List<String> list = new ArrayList<>();
		for (final Germplasm g : results) {
			list.add(g.getMethodCode());
		}

		// Check if the list is in ascending order
		Assert.assertTrue(Ordering.natural().isOrdered(list));

	}

	@Test
	public void testSearchForGermplasmsMethodAbbreviationSortDescending() {

		final GermplasmSearchParameter searchParameter =
				this.createSearchParam("GermplasmForSorting%", Operation.LIKE, false, false, false);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.METHOD_ABBREVIATION);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		searchParameter.setSortState(new Object[] {GermplasmSearchDAO.METHOD_ABBREVIATION}, new boolean[] {false});

		final List<Germplasm> results = this.dao.searchForGermplasms(searchParameter);

		final List<String> list = new ArrayList<>();
		for (final Germplasm g : results) {
			list.add(g.getMethodCode());
		}

		// Check if the list is in descending order
		Assert.assertTrue(Ordering.natural().reverse().isOrdered(list));

	}

	@Test
	public void testSearchForGermplasmsMethodNumberSortAscending() {

		final GermplasmSearchParameter searchParameter =
				this.createSearchParam("GermplasmForSorting%", Operation.LIKE, false, false, false);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.METHOD_NUMBER);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		searchParameter.setSortState(new Object[] {GermplasmSearchDAO.METHOD_NUMBER}, new boolean[] {true});

		final List<Germplasm> results = this.dao.searchForGermplasms(searchParameter);

		final List<String> list = new ArrayList<>();
		for (final Germplasm g : results) {
			list.add(g.getMethodNumber());
		}

		// Check if the list is in ascending order
		Assert.assertTrue(Ordering.natural().isOrdered(list));

	}

	@Test
	public void testSearchForGermplasmsMethodNumberSortDescending() {

		final GermplasmSearchParameter searchParameter =
				this.createSearchParam("GermplasmForSorting%", Operation.LIKE, false, false, false);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.METHOD_NUMBER);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		searchParameter.setSortState(new Object[] {GermplasmSearchDAO.METHOD_NUMBER}, new boolean[] {false});

		final List<Germplasm> results = this.dao.searchForGermplasms(searchParameter);

		final List<String> list = new ArrayList<>();
		for (final Germplasm g : results) {
			list.add(g.getMethodNumber());
		}

		// Check if the list is in descending order
		Assert.assertTrue(Ordering.natural().reverse().isOrdered(list));

	}

	@Test
	public void testSearchForGermplasmsMethodGroupSortAscending() {

		final GermplasmSearchParameter searchParameter =
				this.createSearchParam("GermplasmForSorting%", Operation.LIKE, false, false, false);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.METHOD_GROUP);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		searchParameter.setSortState(new Object[] {GermplasmSearchDAO.METHOD_GROUP}, new boolean[] {true});

		final List<Germplasm> results = this.dao.searchForGermplasms(searchParameter);

		final List<String> list = new ArrayList<>();
		for (final Germplasm g : results) {
			list.add(g.getMethodGroup());
		}

		// Check if the list is in ascending order
		Assert.assertTrue(Ordering.natural().isOrdered(list));

	}

	@Test
	public void testSearchForGermplasmsMethodGroupSortDescending() {

		final GermplasmSearchParameter searchParameter =
				this.createSearchParam("GermplasmForSorting%", Operation.LIKE, false, false, false);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.METHOD_GROUP);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		searchParameter.setSortState(new Object[] {GermplasmSearchDAO.METHOD_GROUP}, new boolean[] {false});

		final List<Germplasm> results = this.dao.searchForGermplasms(searchParameter);

		final List<String> list = new ArrayList<>();
		for (final Germplasm g : results) {
			list.add(g.getMethodGroup());
		}

		// Check if the list is in descending order
		Assert.assertTrue(Ordering.natural().reverse().isOrdered(list));

	}

	@Test
	public void testSearchForGermplasmsFemaleGidSortAscending() {

		final GermplasmSearchParameter searchParameter =
				this.createSearchParam("GermplasmForSorting%", Operation.LIKE, false, false, false);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.FEMALE_PARENT_ID);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		searchParameter.setSortState(new Object[] {GermplasmSearchDAO.FEMALE_PARENT_ID}, new boolean[] {true});

		final List<Germplasm> results = this.dao.searchForGermplasms(searchParameter);

		final List<String> list = new ArrayList<>();
		for (final Germplasm g : results) {
			list.add(g.getFemaleParentPreferredID());
		}

		// Check if the list is in ascending order
		Assert.assertTrue(Ordering.natural().isOrdered(list));

	}

	@Test
	public void testSearchForGermplasmsFemaleGidSortDescending() {

		final GermplasmSearchParameter searchParameter =
				this.createSearchParam("GermplasmForSorting%", Operation.LIKE, false, false, false);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.FEMALE_PARENT_ID);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		searchParameter.setSortState(new Object[] {GermplasmSearchDAO.FEMALE_PARENT_ID}, new boolean[] {false});

		final List<Germplasm> results = this.dao.searchForGermplasms(searchParameter);

		final List<String> list = new ArrayList<>();
		for (final Germplasm g : results) {
			list.add(g.getFemaleParentPreferredID());
		}

		// Check if the list is in descending order
		Assert.assertTrue(Ordering.natural().reverse().isOrdered(list));

	}

	@Test
	public void testSearchForGermplasmsFemalePreferredNameSortAscending() {

		final GermplasmSearchParameter searchParameter =
				this.createSearchParam("GermplasmForSorting%", Operation.LIKE, false, false, false);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.FEMALE_PARENT_PREFERRED_NAME);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		searchParameter.setSortState(new Object[] {GermplasmSearchDAO.FEMALE_PARENT_PREFERRED_NAME}, new boolean[] {true});

		final List<Germplasm> results = this.dao.searchForGermplasms(searchParameter);

		final List<String> list = new ArrayList<>();
		for (final Germplasm g : results) {
			list.add(g.getFemaleParentPreferredName());
		}

		// Check if the list is in ascending order
		Assert.assertTrue(Ordering.natural().isOrdered(list));

	}

	@Test
	public void testSearchForGermplasmsFemalePreferredNameSortDescending() {

		final GermplasmSearchParameter searchParameter =
				this.createSearchParam("GermplasmForSorting%", Operation.LIKE, false, false, false);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.FEMALE_PARENT_PREFERRED_NAME);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		searchParameter.setSortState(new Object[] {GermplasmSearchDAO.FEMALE_PARENT_PREFERRED_NAME}, new boolean[] {false});

		final List<Germplasm> results = this.dao.searchForGermplasms(searchParameter);

		final List<String> list = new ArrayList<>();
		for (final Germplasm g : results) {
			list.add(g.getFemaleParentPreferredName());
		}

		// Check if the list is in descending order
		Assert.assertTrue(Ordering.natural().reverse().isOrdered(list));

	}

	@Test
	public void testSearchForGermplasmsMaleGidSortAscending() {

		final GermplasmSearchParameter searchParameter =
				this.createSearchParam("GermplasmForSorting%", Operation.LIKE, false, false, false);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.MALE_PARENT_ID);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		searchParameter.setSortState(new Object[] {GermplasmSearchDAO.MALE_PARENT_ID}, new boolean[] {true});

		final List<Germplasm> results = this.dao.searchForGermplasms(searchParameter);

		final List<String> list = new ArrayList<>();
		for (final Germplasm g : results) {
			list.add(g.getMaleParentPreferredID());
		}

		// Check if the list is in ascending order
		Assert.assertTrue(Ordering.natural().isOrdered(list));

	}

	@Test
	public void testSearchForGermplasmsMaleGidSortDescending() {

		final GermplasmSearchParameter searchParameter =
				this.createSearchParam("GermplasmForSorting%", Operation.LIKE, false, false, false);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.MALE_PARENT_ID);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		searchParameter.setSortState(new Object[] {GermplasmSearchDAO.MALE_PARENT_ID}, new boolean[] {false});

		final List<Germplasm> results = this.dao.searchForGermplasms(searchParameter);

		final List<String> list = new ArrayList<>();
		for (final Germplasm g : results) {
			list.add(g.getMaleParentPreferredID());
		}

		// Check if the list is in descending order
		Assert.assertTrue(Ordering.natural().reverse().isOrdered(list));

	}

	@Test
	public void testSearchForGermplasmsMalePreferredNameSortAscending() {

		final GermplasmSearchParameter searchParameter =
				this.createSearchParam("GermplasmForSorting%", Operation.LIKE, false, false, false);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.MALE_PARENT_PREFERRED_NAME);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		searchParameter.setSortState(new Object[] {GermplasmSearchDAO.MALE_PARENT_PREFERRED_NAME}, new boolean[] {true});

		final List<Germplasm> results = this.dao.searchForGermplasms(searchParameter);

		final List<String> list = new ArrayList<>();
		for (final Germplasm g : results) {
			list.add(g.getMaleParentPreferredName());
		}

		// Check if the list is in ascending order
		Assert.assertTrue(Ordering.natural().isOrdered(list));

	}

	@Test
	public void testSearchForGermplasmsMalePreferredNameSortDescending() {

		final GermplasmSearchParameter searchParameter =
				this.createSearchParam("GermplasmForSorting%", Operation.LIKE, false, false, false);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(GermplasmSearchDAO.MALE_PARENT_PREFERRED_NAME);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		searchParameter.setSortState(new Object[] {GermplasmSearchDAO.MALE_PARENT_PREFERRED_NAME}, new boolean[] {false});

		final List<Germplasm> results = this.dao.searchForGermplasms(searchParameter);

		final List<String> list = new ArrayList<>();
		for (final Germplasm g : results) {
			list.add(g.getMaleParentPreferredName());
		}

		// Check if the list is in descending order
		Assert.assertTrue(Ordering.natural().reverse().isOrdered(list));

	}

	@Test
	public void testSearchForGermplasmsAttributeTypeSortAscending() {

		final GermplasmSearchParameter searchParameter =
				this.createSearchParam("GermplasmForSorting%", Operation.LIKE, false, false, false);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(NOTE_ATTRIBUTE);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		searchParameter.setSortState(new Object[] {NOTE_ATTRIBUTE}, new boolean[] {true});

		final List<Germplasm> results = this.dao.searchForGermplasms(searchParameter);

		final List<String> list = new ArrayList<>();
		for (final Germplasm g : results) {
			list.add(g.getAttributeTypesValueMap().get(NOTE_ATTRIBUTE));
		}

		// Check if the list is in ascending order
		Assert.assertTrue(Ordering.natural().isOrdered(list));

	}

	@Test
	public void testSearchForGermplasmsAttributeTypeSortDescending() {

		final GermplasmSearchParameter searchParameter =
				this.createSearchParam("GermplasmForSorting%", Operation.LIKE, false, false, false);
		final List<String> propertyIds = new LinkedList<>();

		propertyIds.add(NOTE_ATTRIBUTE);

		searchParameter.setAddedColumnsPropertyIds(propertyIds);
		searchParameter.setSortState(new Object[] {NOTE_ATTRIBUTE}, new boolean[] {false});

		final List<Germplasm> results = this.dao.searchForGermplasms(searchParameter);

		final List<String> list = new ArrayList<>();
		for (final Germplasm g : results) {
			list.add(g.getAttributeTypesValueMap().get(NOTE_ATTRIBUTE));
		}

		// Check if the list is in descending order
		Assert.assertTrue(Ordering.natural().reverse().isOrdered(list));

	}

	@Test
	public void testSearchForGermplasmsNamesSortAscending() {

		final GermplasmSearchParameter searchParameter =
				this.createSearchParam("GermplasmForSorting%", Operation.LIKE, false, false, false);

		searchParameter.setSortState(new Object[] {GermplasmSearchDAO.NAMES}, new boolean[] {true});

		final List<Germplasm> results = this.dao.searchForGermplasms(searchParameter);

		final List<String> list = new ArrayList<>();
		for (final Germplasm g : results) {
			list.add(g.getGermplasmNamesString());
		}

		// Check if the list is in ascending order
		Assert.assertTrue(Ordering.natural().isOrdered(list));

	}

	@Test
	public void testSearchForGermplasmsNamesSortDescending() {

		final GermplasmSearchParameter searchParameter =
				this.createSearchParam("GermplasmForSorting%", Operation.LIKE, false, false, false);

		searchParameter.setSortState(new Object[] {GermplasmSearchDAO.NAMES}, new boolean[] {false});

		final List<Germplasm> results = this.dao.searchForGermplasms(searchParameter);

		final List<String> list = new ArrayList<>();
		for (final Germplasm g : results) {
			list.add(g.getGermplasmNamesString());
		}

		// Check if the list is in descending order
		Assert.assertTrue(Ordering.natural().reverse().isOrdered(list));

	}

	@Test
	public void testSearchForGermplasmsGIDSortAscending() {

		final GermplasmSearchParameter searchParameter =
				this.createSearchParam("GermplasmForSorting%", Operation.LIKE, false, false, false);

		searchParameter.setSortState(new Object[] {GermplasmSearchDAO.GID}, new boolean[] {true});

		final List<Germplasm> results = this.dao.searchForGermplasms(searchParameter);

		final List<Integer> list = new ArrayList<>();
		for (final Germplasm g : results) {
			list.add(g.getGid());
		}

		// Check if the list is in ascending order
		Assert.assertTrue(Ordering.natural().isOrdered(list));

	}

	@Test
	public void testSearchForGermplasmsGIDSortDescending() {

		final GermplasmSearchParameter searchParameter =
				this.createSearchParam("GermplasmForSorting%", Operation.LIKE, false, false, false);

		searchParameter.setSortState(new Object[] {GermplasmSearchDAO.GID}, new boolean[] {false});

		final List<Germplasm> results = this.dao.searchForGermplasms(searchParameter);

		final List<Integer> list = new ArrayList<>();
		for (final Germplasm g : results) {
			list.add(g.getGid());
		}

		// Check if the list is in descending order
		Assert.assertTrue(Ordering.natural().reverse().isOrdered(list));

	}

	@Test
	public void testSearchForGermplasmsGroupIdSortAscending() {

		final GermplasmSearchParameter searchParameter =
				this.createSearchParam("GermplasmForSorting%", Operation.LIKE, false, false, false);

		searchParameter.setSortState(new Object[] {GermplasmSearchDAO.GROUP_ID}, new boolean[] {true});

		final List<Germplasm> results = this.dao.searchForGermplasms(searchParameter);

		final List<Integer> list = new ArrayList<>();
		for (final Germplasm g : results) {
			list.add(g.getMgid());
		}

		// Check if the list is in ascending order
		Assert.assertTrue(Ordering.natural().isOrdered(list));

	}

	@Test
	public void testSearchForGermplasmsGroupIdSortDescending() {

		final GermplasmSearchParameter searchParameter =
				this.createSearchParam("GermplasmForSorting%", Operation.LIKE, false, false, false);

		searchParameter.setSortState(new Object[] {GermplasmSearchDAO.GROUP_ID}, new boolean[] {false});

		final List<Germplasm> results = this.dao.searchForGermplasms(searchParameter);

		final List<Integer> list = new ArrayList<>();
		for (final Germplasm g : results) {
			list.add(g.getMgid());
		}

		// Check if the list is in descending order
		Assert.assertTrue(Ordering.natural().reverse().isOrdered(list));

	}

	@Test
	public void testSearchForGermplasmsLotSortAscending() {

		final GermplasmSearchParameter searchParameter =
				this.createSearchParam("GermplasmForSorting%", Operation.LIKE, false, false, false);

		searchParameter.setSortState(new Object[] {GermplasmSearchDAO.AVAIL_LOTS}, new boolean[] {true});

		final List<Germplasm> results = this.dao.searchForGermplasms(searchParameter);

		final List<Integer> list = new ArrayList<>();
		for (final Germplasm g : results) {
			list.add(g.getInventoryInfo().getActualInventoryLotCount());
		}

		// Check if the list is in ascending order
		Assert.assertTrue(Ordering.natural().isOrdered(list));

	}

	@Test
	public void testSearchForGermplasmsLotSortDescending() {

		final GermplasmSearchParameter searchParameter =
				this.createSearchParam("GermplasmForSorting%", Operation.LIKE, false, false, false);

		searchParameter.setSortState(new Object[] {GermplasmSearchDAO.AVAIL_LOTS}, new boolean[] {false});

		final List<Germplasm> results = this.dao.searchForGermplasms(searchParameter);

		final List<Integer> list = new ArrayList<>();
		for (final Germplasm g : results) {
			list.add(g.getInventoryInfo().getActualInventoryLotCount());
		}

		// Check if the list is in descending order
		Assert.assertTrue(Ordering.natural().reverse().isOrdered(list));

	}

	@Test
	public void testSearchForGermplasmsBalanceSortAscending() {

		final GermplasmSearchParameter searchParameter =
				this.createSearchParam("GermplasmForSorting%", Operation.LIKE, false, false, false);

		searchParameter.setSortState(new Object[] {GermplasmSearchDAO.AVAIL_BALANCE}, new boolean[] {true});

		final List<Germplasm> results = this.dao.searchForGermplasms(searchParameter);

		final List<Double> list = new ArrayList<>();
		for (final Germplasm g : results) {
			list.add(g.getInventoryInfo().getTotalAvailableBalance());
		}

		// Check if the list is in ascending order
		Assert.assertTrue(Ordering.natural().isOrdered(list));

	}

	@Test
	public void testSearchForGermplasmsBalanceSortDescending() {

		final GermplasmSearchParameter searchParameter =
				this.createSearchParam("GermplasmForSorting%", Operation.LIKE, false, false, false);

		searchParameter.setSortState(new Object[] {GermplasmSearchDAO.AVAIL_BALANCE}, new boolean[] {false});

		final List<Germplasm> results = this.dao.searchForGermplasms(searchParameter);

		final List<Double> list = new ArrayList<>();
		for (final Germplasm g : results) {
			list.add(g.getInventoryInfo().getTotalAvailableBalance());
		}

		// Check if the list is in descending order
		Assert.assertTrue(Ordering.natural().reverse().isOrdered(list));

	}

	private void initializeGermplasms() {

		final Germplasm fParent =
				GermplasmTestDataInitializer.createGermplasm(germplasmDate, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		this.femaleParentGID = this.germplasmDataDM.addGermplasm(fParent, fParent.getPreferredName());
		this.femaleParentPreferredName = fParent.getPreferredName();

		final Germplasm mParent =
				GermplasmTestDataInitializer.createGermplasm(germplasmDate, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		this.maleParentGID = this.germplasmDataDM.addGermplasm(mParent, mParent.getPreferredName());
		this.maleParentPreferredName = mParent.getPreferredName();

		final Germplasm germplasm = GermplasmTestDataInitializer
				.createGermplasm(germplasmDate, femaleParentGID, maleParentGID, 2, 0, 0, 1, 1, GermplasmSearchDAOTest.GROUP_ID, 1, 1,
						"MethodName", "LocationName");

		// Create Germplasm and add Preferred Name
		this.germplasmGID = this.germplasmDataDM.addGermplasm(germplasm, germplasm.getPreferredName());
		this.preferredName = germplasm.getPreferredName();

		// Add Preferred Id, nstat = 8 means the name is preferred Id
		this.preferredId = GermplasmTestDataInitializer.createGermplasmName(germplasmGID, "Preferred Id of " + germplasmGID);
		preferredId.setNstat(8);
		this.germplasmDataDM.addGermplasmName(preferredId);

		// Add NOTE attribute
		final UserDefinedField attributeField = userDefinedFieldDao.getByTableTypeAndCode("ATRIBUTS", "ATTRIBUTE", NOTE_ATTRIBUTE);
		attributeTypeMap.put(attributeField.getFcode(), attributeField.getFldno());

		this.attributeValue = "Attribute of " + germplasmGID;
		final Attribute attribute = new Attribute();
		attribute.setGermplasmId(germplasmGID);
		attribute.setTypeId(attributeField.getFldno());
		attribute.setAval(this.attributeValue);
		attribute.setUserId(0);
		attribute.setAdate(this.germplasmDate);

		this.germplasmDataDM.addGermplasmAttribute(attribute);

		final Lot lot = new Lot();
		lot.setEntityType("GERMPLSM");
		lot.setLocationId(0);
		lot.setScaleId(TermId.SEED_AMOUNT_G.getId());
		lot.setEntityId(germplasmGID);
		lot.setStatus(0);
		this.inventoryDataManager.addLot(lot);

		final Transaction transaction = new Transaction();
		transaction.setQuantity(100.0);
		transaction.setStatus(0);
		transaction.setLot(lot);
		this.inventoryDataManager.addTransaction(transaction);

		final Germplasm mgMember = GermplasmTestDataInitializer
				.createGermplasm(germplasmDate, femaleParentGID, maleParentGID, 2, 0, 0, 1, 1, GermplasmSearchDAOTest.GROUP_ID, 1, 1,
						"MethodName", "LocationName");
		this.germplasmDataDM.addGermplasm(mgMember, mgMember.getPreferredName());

	}

	private List<Integer> createTestGermplasmForSorting() {

		final List<Integer> testGermplasmGIDs = new ArrayList<>();
		final Integer tempGermplasmDate = 20150101;

		for (int counter = 1; counter <= 5; counter++) {

			final Germplasm fParent = GermplasmTestDataInitializer
					.createGermplasm(tempGermplasmDate, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName" + counter, "LocationName" + counter);
			final Integer tempFemaleParentGID = this.germplasmDataDM.addGermplasm(fParent, fParent.getPreferredName());

			final Germplasm mParent = GermplasmTestDataInitializer
					.createGermplasm(tempGermplasmDate, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName" + counter, "LocationName" + counter);
			final Integer tempMaleParentGID = this.germplasmDataDM.addGermplasm(mParent, mParent.getPreferredName());

			final Germplasm germplasm = GermplasmTestDataInitializer
					.createGermplasm(tempGermplasmDate, tempFemaleParentGID, tempMaleParentGID, 2, 0, 0, 1, counter, counter, 1, 1,
							"MethodName", "LocationName");

			// Create Germplasm and add Preferred Name
			germplasm.getPreferredName().setNval("GermplasmForSorting" + counter);
			final Integer tempGermplasmGid = this.germplasmDataDM.addGermplasm(germplasm, germplasm.getPreferredName());
			testGermplasmGIDs.add(tempGermplasmGid);

			// Add Preferred Id, nstat = 8 means the name is preferred Id
			final Name tempPreferredId =
					GermplasmTestDataInitializer.createGermplasmName(tempGermplasmGid, "Preferred Id of " + tempGermplasmGid);
			tempPreferredId.setNstat(8);
			this.germplasmDataDM.addGermplasmName(tempPreferredId);

			// Add NOTE attribute
			final UserDefinedField attributeField = userDefinedFieldDao.getByTableTypeAndCode("ATRIBUTS", "ATTRIBUTE", NOTE_ATTRIBUTE);
			attributeTypeMap.put(attributeField.getFcode(), attributeField.getFldno());

			final Attribute attribute = new Attribute();
			attribute.setGermplasmId(tempGermplasmGid);
			attribute.setTypeId(attributeField.getFldno());
			attribute.setAval("Attribute of " + tempGermplasmGid);
			attribute.setUserId(0);
			attribute.setAdate(tempGermplasmDate);

			this.germplasmDataDM.addGermplasmAttribute(attribute);

		}

		return testGermplasmGIDs;

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
	private void assertPossibleGermplasmFields(final List<Germplasm> germplasmSearchResults) {
		// Assert possible germplasm member fields
		for (final Germplasm germplasm : germplasmSearchResults) {
			Assert.assertNotEquals("Gpid1 should not be 0", Integer.valueOf(0), germplasm.getGpid1());
			Assert.assertNotEquals("Gpid2 should not be 0", Integer.valueOf(0), germplasm.getGpid2());
			Assert.assertNotEquals("Gnpgs should not be 0", Integer.valueOf(0), germplasm.getGnpgs());
			Assert.assertEquals("Result should contain Method Name", "Unknown generative method", germplasm.getMethodName());
			Assert.assertEquals("Result should contain Location Name", "Afghanistan", germplasm.getLocationName());
			Assert.assertEquals("Result should contain Germplasm Number of Progenitor", Integer.valueOf(2), germplasm.getGnpgs());
			Assert.assertEquals("Result should contain Germplasm Date", Integer.valueOf(germplasmDate), germplasm.getGdate());
			Assert.assertEquals("Result should contain Reference Id", Integer.valueOf(1), germplasm.getReferenceId());
		}
	}

	private void assertInventoryFields(final List<Germplasm> germplasmSearchResults) {
		for (final Germplasm germplasm : germplasmSearchResults) {
			Assert.assertEquals("Lot count should be 1", Integer.valueOf(1), germplasm.getInventoryInfo().getActualInventoryLotCount());
			Assert.assertEquals("Total Available Balance should be 100.0", Double.valueOf(100.0),
					germplasm.getInventoryInfo().getTotalAvailableBalance());
		}
	}

	private void assertAddedGermplasmFields(final Germplasm germplasm, final List<String> propertyIds) {

		if (propertyIds.contains(GermplasmSearchDAO.PREFERRED_ID)) {
			Assert.assertEquals("Result germplasm should contain Preferred ID", this.preferredId.getNval(),
					germplasm.getGermplasmPeferredId());
		} else {
			Assert.assertTrue("Result germplasm should not contain Preferred ID", StringUtils.isEmpty(germplasm.getGermplasmPeferredId()));
		}
		if (propertyIds.contains(GermplasmSearchDAO.PREFERRED_NAME)) {
			Assert.assertEquals("Result germplasm should contain Preferred Name", this.preferredName.getNval(),
					germplasm.getGermplasmPeferredName());
		} else {
			Assert.assertTrue("Result germplasm should not contain Preferred Name",
					StringUtils.isEmpty(germplasm.getGermplasmPeferredName()));
		}
		if (propertyIds.contains(GermplasmSearchDAO.GERMPLASM_DATE)) {
			Assert.assertEquals("Result germplasm should contain Germplasm Date", String.valueOf(this.germplasmDate),
					germplasm.getGermplasmDate());
		} else {
			Assert.assertTrue("Result germplasm should not contain Germplasm Date", StringUtils.isEmpty(germplasm.getGermplasmDate()));
		}
		if (propertyIds.contains(GermplasmSearchDAO.METHOD_ABBREVIATION)) {
			Assert.assertEquals("Result germplasm should contain Method Abbreviation", "UGM", germplasm.getMethodCode());
		} else {
			Assert.assertTrue("Result germplasm should not contain Method Abbreviation", StringUtils.isEmpty(germplasm.getMethodCode()));
		}
		if (propertyIds.contains(GermplasmSearchDAO.METHOD_NUMBER)) {
			Assert.assertEquals("Result germplasm should contain Method Number", Integer.valueOf(1), germplasm.getMethodId());
		} else {
			Assert.assertTrue("Result germplasm should not contain Method Number", StringUtils.isEmpty(germplasm.getMethodCode()));
		}
		if (propertyIds.contains(GermplasmSearchDAO.METHOD_GROUP)) {
			Assert.assertEquals("Result germplasm should contain Method Group", "G", germplasm.getMethodGroup());
		} else {
			Assert.assertTrue("Result germplasm should not contain Method Group", StringUtils.isEmpty(germplasm.getMethodGroup()));
		}
		if (propertyIds.contains(GermplasmSearchDAO.FEMALE_PARENT_ID)) {
			Assert.assertEquals("Result germplasm should contain Female Parent ID", String.valueOf(femaleParentGID),
					germplasm.getFemaleParentPreferredID());
		} else {
			Assert.assertTrue("Result germplasm should not contain Female Parent ID",
					StringUtils.isEmpty(germplasm.getFemaleParentPreferredID()));
		}
		if (propertyIds.contains(GermplasmSearchDAO.FEMALE_PARENT_PREFERRED_NAME)) {
			Assert.assertEquals("Result germplasm should contain Female Parent Preferred Name", femaleParentPreferredName.getNval(),
					germplasm.getFemaleParentPreferredName());
		} else {
			Assert.assertTrue("Result germplasm should not contain Female Parent Preferred Name",
					StringUtils.isEmpty(germplasm.getFemaleParentPreferredName()));
		}
		if (propertyIds.contains(GermplasmSearchDAO.MALE_PARENT_ID)) {
			Assert.assertEquals("Result germplasm should contain Male Parent ID", String.valueOf(maleParentGID),
					germplasm.getMaleParentPreferredID());
		} else {
			Assert.assertTrue("Result germplasm should not contain Male Parent ID",
					StringUtils.isEmpty(germplasm.getMaleParentPreferredID()));
		}
		if (propertyIds.contains(GermplasmSearchDAO.MALE_PARENT_PREFERRED_NAME)) {
			Assert.assertEquals("Result germplasm should contain Male Parent Preferred Name", maleParentPreferredName.getNval(),
					germplasm.getMaleParentPreferredName());
		} else {
			Assert.assertTrue("Result germplasm should not contain Male Parent Preferred Name",
					StringUtils.isEmpty(germplasm.getMaleParentPreferredName()));
		}
		if (propertyIds.contains(NOTE_ATTRIBUTE)) {
			Assert.assertEquals("Result germplasm should contain Note", this.attributeValue,
					germplasm.getAttributeTypesValueMap().get(NOTE_ATTRIBUTE));
		} else {
			Assert.assertFalse("Result germplasm should not contain Note attribute",
					germplasm.getAttributeTypesValueMap().containsKey(NOTE_ATTRIBUTE));
		}

	}

}
