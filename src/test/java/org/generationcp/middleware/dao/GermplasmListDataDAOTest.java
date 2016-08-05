
package org.generationcp.middleware.dao;

import java.util.Arrays;
import java.util.List;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.data.initializer.GermplasmListDataTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.Name;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GermplasmListDataDAOTest extends IntegrationTestBase {

	private static final Integer TEST_ENTRY_ID = 1;
	private GermplasmListDataDAO germplasmListDataDAO;
	private Session hibernateSession;
	private GermplasmDAO germplasmDAO;
	private NameDAO nameDAO;
	private GermplasmListDAO germplasmListDAO;


	public static final String DUMMY_STRING = "DUMMY STRING";
	public static final Integer TEST_VALUE = 1;

	@Before
	public void beforeTest() {
		this.germplasmListDataDAO = new GermplasmListDataDAO();
		this.germplasmDAO = new GermplasmDAO();
		this.nameDAO = new NameDAO();
		this.germplasmListDAO = new GermplasmListDAO();
		this.hibernateSession = this.sessionProvder.getSession();

		this.germplasmListDataDAO.setSession(this.hibernateSession);
		this.germplasmDAO.setSession(this.hibernateSession);
		this.nameDAO.setSession(this.hibernateSession);
		this.germplasmListDAO.setSession(this.hibernateSession);
	}

	@Test
	public void testCountByListId() throws Exception {
		// insert a new list data record from a newly-created list and germplasm records
		final GermplasmListData testGermplasmListData = this.createTestListData(null, null);
		final int listId = testGermplasmListData.getList().getId();
		final long numOfListData = this.germplasmListDataDAO.countByListId(listId);
		Assert.assertTrue("There should be at least 1 list data under the given list with id " + listId, numOfListData >= 1);
		// create a germplasm list object with the newly created list id then create a new list data
		final GermplasmList germplasmList = GermplasmListTestDataInitializer.createGermplasmList(listId, true);
		this.createTestListData(null, germplasmList);
		final long newNumOfListData = this.germplasmListDataDAO.countByListId(listId);
		Assert.assertEquals("There should be " + (numOfListData + 1) + "  list data under the given list with id " + listId,
				numOfListData + 1, newNumOfListData);
	}

	@Test
	public void testDeleteByListId() throws Exception {
		// insert a new list data record from a newly-created list and germplasm records
		final GermplasmListData testGermplasmListData = this.createTestListData(null, null);
		final int listId = testGermplasmListData.getList().getId();
		final long numOfListData = this.germplasmListDataDAO.countByListId(listId);
		Assert.assertTrue("There should be at least 1 list data under the given list with id " + listId, numOfListData >= 1);
		// delete all list data under the list id
		this.germplasmListDataDAO.deleteByListId(listId);
		final long newNumOfListData = this.germplasmListDataDAO.countByListId(listId);
		Assert.assertEquals("There should be no list data under the given list with id " + listId, 0, newNumOfListData);
	}

	@Test
	public void testGetByIds() throws Exception {
		// insert a new list data record from a newly-created list and germplasm records
		final GermplasmListData testGermplasmListData1 = this.createTestListData(null, null);
		// create another list data
		final GermplasmListData testGermplasmListData2 = this.createTestListData(null, null);
		// get the 2 list data records from the database
		final List<Integer> listDataIds = Arrays.asList(new Integer[] {testGermplasmListData1.getId(), testGermplasmListData2.getId()});
		final List<GermplasmListData> listDataRecords = this.germplasmListDataDAO.getByIds(listDataIds);
		Assert.assertEquals("There should be 2 list data records returned", 2, listDataRecords.size());
		for (final GermplasmListData germplasmListData : listDataRecords) {
			Assert.assertTrue("The list data record id " + germplasmListData.getId() + " should be found in " + listDataIds,
					listDataIds.contains(germplasmListData.getId()));
			if (germplasmListData.getId() == testGermplasmListData1.getId()) {
				Assert.assertEquals("The list id should be " + testGermplasmListData1.getList().getId(), testGermplasmListData1.getList()
						.getId(), germplasmListData.getList().getId());
				Assert.assertEquals("The gid should be " + testGermplasmListData1.getGid(), testGermplasmListData1.getGid(),
						germplasmListData.getGid());
			} else if (germplasmListData.getId() == testGermplasmListData2.getId()) {
				Assert.assertEquals("The list id should be " + testGermplasmListData2.getList().getId(), testGermplasmListData2.getList()
						.getId(), germplasmListData.getList().getId());
				Assert.assertEquals("The gid id should be " + testGermplasmListData2.getGid(), testGermplasmListData2.getGid(),
						germplasmListData.getGid());
			}
		}
	}

	@Test
	public void testGetByListId() {
		// insert a new list data record from a newly-created list and germplasm records
		final GermplasmListData testGermplasmListData = this.createTestListData(null, null);
		// get the list data record from the database
		final List<GermplasmListData> listDataRecords = this.germplasmListDataDAO.getByListId(testGermplasmListData.getList().getId());
		Assert.assertEquals("There should be 1 list data record returned", 1, listDataRecords.size());
		final GermplasmListData germplasmListData = listDataRecords.get(0);
		Assert.assertEquals("The id should be " + testGermplasmListData.getId(), testGermplasmListData.getId(), germplasmListData.getId());
		Assert.assertEquals("The list id should be " + testGermplasmListData.getList().getId(), testGermplasmListData.getList().getId(),
				germplasmListData.getList().getId());
		Assert.assertEquals("The gid should be " + testGermplasmListData.getGid(), testGermplasmListData.getGid(),
				germplasmListData.getGid());
	}

	@Test
	public void testGetByListIdAndEntryId() throws Exception {
		// insert a new list data record from a newly-created list and germplasm records
		final GermplasmListData testGermplasmListData = this.createTestListData(null, null);
		// get the list data record from the database
		final GermplasmListData germplasmListData =
				this.germplasmListDataDAO.getByListIdAndEntryId(testGermplasmListData.getList().getId(),
						GermplasmListDataDAOTest.TEST_ENTRY_ID);
		Assert.assertNotNull("The germplasm list data should not be null", germplasmListData);
		Assert.assertEquals("The id should be " + testGermplasmListData.getId(), testGermplasmListData.getId(), germplasmListData.getId());
		Assert.assertEquals("The list id should be " + testGermplasmListData.getList().getId(), testGermplasmListData.getList().getId(),
				germplasmListData.getList().getId());
		Assert.assertEquals("The entry id should be " + testGermplasmListData.getEntryId(), testGermplasmListData.getEntryId(),
				germplasmListData.getEntryId());
		Assert.assertEquals("The gid should be " + testGermplasmListData.getGid(), testGermplasmListData.getGid(),
				germplasmListData.getGid());
	}

	@Test
	public void testGetByListIdAndLrecId() throws Exception {
		// insert a new list data record from a newly-created list and germplasm records
		final GermplasmListData testGermplasmListData = this.createTestListData(null, null);
		// get the list data record from the database
		final GermplasmListData germplasmListData =
				this.germplasmListDataDAO.getByListIdAndLrecId(testGermplasmListData.getList().getId(), testGermplasmListData.getId());
		Assert.assertNotNull("The germplasm list data should not be null", germplasmListData);
		Assert.assertEquals("The id should be " + testGermplasmListData.getId(), testGermplasmListData.getId(), germplasmListData.getId());
		Assert.assertEquals("The list id should be " + testGermplasmListData.getList().getId(), testGermplasmListData.getList().getId(),
				germplasmListData.getList().getId());
		Assert.assertEquals("The entry id should be " + testGermplasmListData.getEntryId(), testGermplasmListData.getEntryId(),
				germplasmListData.getEntryId());
		Assert.assertEquals("The gid should be " + testGermplasmListData.getGid(), testGermplasmListData.getGid(),
				germplasmListData.getGid());
	}

	@Test
	public void testGetListDataWithParents() {

		final Germplasm parentGermplasm = this.createTestParentGermplasmWithPreferredAndNonpreferredNames();
		final Germplasm childGermplasm = this.createTestGermplasm(parentGermplasm);
		final GermplasmListData listData = this.createTestListData(childGermplasm, null);
		final List<GermplasmListData> listDataList = this.germplasmListDataDAO.getListDataWithParents(listData.getList().getId());
		Assert.assertEquals("There should be only 1 list data under the list with id" + listData.getList().getId(), 1, listDataList.size());
		for (final GermplasmListData currentGermplasmListData : listDataList) {
			Assert.assertEquals("List data id should be " + listData.getId(), listData.getId(), currentGermplasmListData.getId());
			Assert.assertEquals("Entry id should be " + listData.getEntryId(), listData.getEntryId(), currentGermplasmListData.getEntryId());
			Assert.assertEquals("Desig should be " + listData.getDesignation(), listData.getDesignation(),
					currentGermplasmListData.getDesignation());
			Assert.assertEquals("Group name should be " + listData.getGroupName(), listData.getGroupName(),
					currentGermplasmListData.getGroupName());
			Assert.assertEquals("Female gid should be " + listData.getFgid(), listData.getFgid(), currentGermplasmListData.getFgid());
			Assert.assertEquals("Male gid should be " + listData.getMgid(), listData.getMgid(), currentGermplasmListData.getMgid());
			Assert.assertEquals("Gid should be " + listData.getGid(), listData.getGid(), currentGermplasmListData.getGid());
			Assert.assertEquals("Seed source should be " + listData.getSeedSource(), listData.getSeedSource(),
					currentGermplasmListData.getSeedSource());
			Assert.assertEquals("Female Parent designation should be " + parentGermplasm.getPreferredName().getNval(), parentGermplasm
					.getPreferredName().getNval(), currentGermplasmListData.getFemaleParent());
			Assert.assertEquals("Male Parent designation should be " + parentGermplasm.getPreferredName().getNval(), parentGermplasm
					.getPreferredName().getNval(), currentGermplasmListData.getMaleParent());
		}
	}

	private GermplasmListData createTestListData(final Germplasm germplasm, final GermplasmList germplasmList) {
		Germplasm listDataGermplasm = null;
		if (germplasm != null) {
			listDataGermplasm = germplasm;
		} else {
			listDataGermplasm = this.createTestGermplasm(null);
		}
		GermplasmList listDataGermplasmList = null;
		if (germplasmList != null) {
			listDataGermplasmList = germplasmList;
		} else {
			listDataGermplasmList = this.createTestList();
		}
		final GermplasmListData listData =
				new GermplasmListDataTestDataInitializer().createGermplasmListData(listDataGermplasmList, listDataGermplasm.getGid(),
						GermplasmListDataDAOTest.TEST_ENTRY_ID);
		listData.setFgid(listDataGermplasm.getGpid1());
		listData.setMgid(listDataGermplasm.getGpid2());
		this.germplasmListDataDAO.save(listData);
		return listData;
	}

	private GermplasmList createTestList() {
		final GermplasmList germplasmList = GermplasmListTestDataInitializer.createGermplasmList(null, false);
		this.germplasmListDAO.saveOrUpdate(germplasmList);
		return germplasmList;
	}

	private Germplasm createTestParentGermplasmWithPreferredAndNonpreferredNames() {
		final Germplasm germplasm = new GermplasmTestDataInitializer().createGermplasmWithPreferredName();
		this.germplasmDAO.save(germplasm);
		final Name preferredName = germplasm.getPreferredName();
		preferredName.setGermplasmId(germplasm.getGid());
		final Name otherName = GermplasmTestDataInitializer.createGermplasmName(germplasm.getGid(), "Other Name ");
		otherName.setNstat(0);
		this.nameDAO.save(preferredName);
		this.nameDAO.save(otherName);
		return germplasm;
	}

	private Germplasm createTestGermplasm(final Germplasm parentGermplasm) {
		final Germplasm germplasm = new GermplasmTestDataInitializer().createGermplasmWithPreferredName();
		if (parentGermplasm != null) {
			germplasm.setGpid1(parentGermplasm.getGid());
			germplasm.setGpid2(parentGermplasm.getGid());
		}
		this.germplasmDAO.save(germplasm);
		return germplasm;
	}


}
