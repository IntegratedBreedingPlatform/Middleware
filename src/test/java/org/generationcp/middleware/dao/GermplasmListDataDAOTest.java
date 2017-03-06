
package org.generationcp.middleware.dao;

import java.util.Arrays;
import java.util.List;

import org.generationcp.middleware.GermplasmTestDataGenerator;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.data.initializer.GermplasmListDataTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class GermplasmListDataDAOTest extends IntegrationTestBase {

	private static final Integer TEST_ENTRY_ID = 1;

	private GermplasmListDataDAO germplasmListDataDAO;
	private GermplasmListDAO germplasmListDAO;
	private GermplasmTestDataGenerator germplasmTestDataGenerator;

	@Autowired
	private GermplasmDataManager germplasmManager;

	@Before
	public void setUp() {
		this.germplasmListDataDAO = new GermplasmListDataDAO();
		this.germplasmListDAO = new GermplasmListDAO();

		this.germplasmListDataDAO.setSession(this.sessionProvder.getSession());
		this.germplasmListDAO.setSession(this.sessionProvder.getSession());

		this.germplasmTestDataGenerator = new GermplasmTestDataGenerator(this.germplasmManager);
	}

	@Test
	public void testCountByListId() {
		// insert new list data record from a newly-created list and germplasm
		// records
		final GermplasmListData testGermplasmListData = this.createTestListWithListData();
		final int listId = testGermplasmListData.getList().getId();
		final long numOfListData = this.germplasmListDataDAO.countByListId(listId);
		Assert.assertEquals("There should be 1 list data under the given list with id " + listId, 1, numOfListData);

		// Add a new list data record to created list and check count result
		this.createTestListDataForList(listId);
		final long newNumOfListData = this.germplasmListDataDAO.countByListId(listId);
		Assert.assertEquals("There should be 2 list data under the given list with id " + listId, 2, newNumOfListData);

	}

	@Test
	public void testDeleteByListId() {
		// insert new list data record from a newly-created list and germplasm
		// records
		final GermplasmListData testGermplasmListData = this.createTestListWithListData();
		final int listId = testGermplasmListData.getList().getId();
		final long numOfListData = this.germplasmListDataDAO.countByListId(listId);
		Assert.assertEquals("There should be 1 list data under the given list with id " + listId, 1, numOfListData);

		// delete all list data under the list id
		this.germplasmListDataDAO.deleteByListId(listId);
		final long newNumOfListData = this.germplasmListDataDAO.countByListId(listId);
		Assert.assertEquals("There should be no list data under the given list with id " + listId, 0, newNumOfListData);
	}

	@Test
	public void testGetByIds() {
		// insert new list data record from a newly-created list and germplasm
		// records
		final GermplasmListData testGermplasmListData1 = this.createTestListWithListData();
		// create another list data
		final GermplasmListData testGermplasmListData2 = this
				.createTestListDataForList(testGermplasmListData1.getList().getId());

		// get the 2 list data records from the database
		final List<Integer> listDataIds = Arrays
				.asList(new Integer[] { testGermplasmListData1.getId(), testGermplasmListData2.getId() });
		final List<GermplasmListData> listDataRecords = this.germplasmListDataDAO.getByIds(listDataIds);
		Assert.assertEquals("There should be 2 list data records returned", 2, listDataRecords.size());
		for (final GermplasmListData germplasmListData : listDataRecords) {
			Assert.assertTrue(
					"The list data record id " + germplasmListData.getId() + " should be found in " + listDataIds,
					listDataIds.contains(germplasmListData.getId()));
			if (testGermplasmListData1.getId().equals(germplasmListData.getId())) {
				Assert.assertEquals("The list id should be " + testGermplasmListData1.getList().getId(),
						testGermplasmListData1.getList().getId(), germplasmListData.getList().getId());
				Assert.assertEquals("The gid should be " + testGermplasmListData1.getGid(),
						testGermplasmListData1.getGid(), germplasmListData.getGid());
			} else if (testGermplasmListData2.getId().equals(germplasmListData.getId())) {
				Assert.assertEquals("The list id should be " + testGermplasmListData2.getList().getId(),
						testGermplasmListData2.getList().getId(), germplasmListData.getList().getId());
				Assert.assertEquals("The gid id should be " + testGermplasmListData2.getGid(),
						testGermplasmListData2.getGid(), germplasmListData.getGid());
			}
		}

	}

	@Test
	public void testGetByListId() {
		// insert a new list data record from a newly-created list and germplasm
		// records
		final GermplasmListData testGermplasmListData = this.createTestListWithListData();

		// get the list data record from the database
		final List<GermplasmListData> listDataRecords = this.germplasmListDataDAO
				.getByListId(testGermplasmListData.getList().getId());
		Assert.assertEquals("There should be 1 list data record returned", 1, listDataRecords.size());
		final GermplasmListData germplasmListData = listDataRecords.get(0);
		Assert.assertEquals("The id should be " + testGermplasmListData.getId(), testGermplasmListData.getId(),
				germplasmListData.getId());
		Assert.assertEquals("The list id should be " + testGermplasmListData.getList().getId(),
				testGermplasmListData.getList().getId(), germplasmListData.getList().getId());
		Assert.assertEquals("The gid should be " + testGermplasmListData.getGid(), testGermplasmListData.getGid(),
				germplasmListData.getGid());

	}

	@Test
	public void testGetByListIdAndEntryId() throws Exception {
		// insert a new list data record from a newly-created list and germplasm
		// records
		final GermplasmListData testGermplasmListData = this.createTestListWithListData();

		// get the list data record from the database
		final GermplasmListData germplasmListData = this.germplasmListDataDAO
				.getByListIdAndEntryId(testGermplasmListData.getList().getId(), GermplasmListDataDAOTest.TEST_ENTRY_ID);

		Assert.assertNotNull("The germplasm list data should not be null", germplasmListData);
		Assert.assertEquals("The id should be " + testGermplasmListData.getId(), testGermplasmListData.getId(),
				germplasmListData.getId());
		Assert.assertEquals("The list id should be " + testGermplasmListData.getList().getId(),
				testGermplasmListData.getList().getId(), germplasmListData.getList().getId());
		Assert.assertEquals("The entry id should be " + testGermplasmListData.getEntryId(),
				testGermplasmListData.getEntryId(), germplasmListData.getEntryId());
		Assert.assertEquals("The gid should be " + testGermplasmListData.getGid(), testGermplasmListData.getGid(),
				germplasmListData.getGid());
	}

	@Test
	public void testGetByListIdAndLrecId() throws Exception {
		// insert new list data record from a newly-created list and germplasm
		// records
		final GermplasmListData testGermplasmListData = this.createTestListWithListData();

		// get the list data record from the database
		final GermplasmListData germplasmListData = this.germplasmListDataDAO
				.getByListIdAndLrecId(testGermplasmListData.getList().getId(), testGermplasmListData.getId());

		Assert.assertNotNull("The germplasm list data should not be null", germplasmListData);
		Assert.assertEquals("The id should be " + testGermplasmListData.getId(), testGermplasmListData.getId(),
				germplasmListData.getId());
		Assert.assertEquals("The list id should be " + testGermplasmListData.getList().getId(),
				testGermplasmListData.getList().getId(), germplasmListData.getList().getId());
		Assert.assertEquals("The entry id should be " + testGermplasmListData.getEntryId(),
				testGermplasmListData.getEntryId(), germplasmListData.getEntryId());
		Assert.assertEquals("The gid should be " + testGermplasmListData.getGid(), testGermplasmListData.getGid(),
				germplasmListData.getGid());
	}

	@Test
	public void testGetListDataWithParents() {
		final Germplasm parentGermplasm = this.germplasmTestDataGenerator
				.createGermplasmWithPreferredAndNonpreferredNames();
		final Germplasm childGermplasm = this.germplasmTestDataGenerator.createChildGermplasm(parentGermplasm,
				"VARIETY");
		final GermplasmListData listData = this.createTestListWithListData(childGermplasm);
		final List<GermplasmListData> listDataList = this.germplasmListDataDAO
				.getListDataWithParents(listData.getList().getId());
		Assert.assertEquals("There should be only 1 list data under the list with id" + listData.getList().getId(), 1,
				listDataList.size());

		for (final GermplasmListData currentGermplasmListData : listDataList) {
			Assert.assertEquals("List data id should be " + listData.getId(), listData.getId(),
					currentGermplasmListData.getId());
			Assert.assertEquals("Entry id should be " + listData.getEntryId(), listData.getEntryId(),
					currentGermplasmListData.getEntryId());
			Assert.assertEquals("Desig should be " + listData.getDesignation(), listData.getDesignation(),
					currentGermplasmListData.getDesignation());
			Assert.assertEquals("Group name should be " + listData.getGroupName(), listData.getGroupName(),
					currentGermplasmListData.getGroupName());
			Assert.assertEquals("Gid should be " + listData.getGid(), listData.getGid(),
					currentGermplasmListData.getGid());
			Assert.assertEquals("Seed source should be " + listData.getSeedSource(), listData.getSeedSource(),
					currentGermplasmListData.getSeedSource());
			Assert.assertEquals("Breeding method name should be " + GermplasmTestDataGenerator.TEST_METHOD_NAME,
					GermplasmTestDataGenerator.TEST_METHOD_NAME, currentGermplasmListData.getBreedingMethodName());

			// Check parent germplasm values
			Assert.assertEquals("Female Parent GID should be " + listData.getFgid(), listData.getFgid(),
					currentGermplasmListData.getFgid());
			Assert.assertEquals("Male Parent GID should be " + listData.getMgid(), listData.getMgid(),
					currentGermplasmListData.getMgid());
			Assert.assertEquals("Female Parent designation should be " + parentGermplasm.getPreferredName().getNval(),
					parentGermplasm.getPreferredName().getNval(), currentGermplasmListData.getFemaleParent());
			Assert.assertEquals("Male Parent designation should be " + parentGermplasm.getPreferredName().getNval(),
					parentGermplasm.getPreferredName().getNval(), currentGermplasmListData.getMaleParent());
		}
	}

	private GermplasmListData createTestListWithListData() {
		final Germplasm listDataGermplasm = this.germplasmTestDataGenerator
				.createGermplasmWithPreferredAndNonpreferredNames();
		return this.createTestListWithListData(listDataGermplasm);
	}

	private GermplasmListData createTestListWithListData(final Germplasm listDataGermplasm) {
		final GermplasmList listDataGermplasmList = this.createTestList();
		return this.createTestListDataForList(listDataGermplasm, listDataGermplasmList);
	}

	private GermplasmListData createTestListDataForList(final Integer listId) {
		final GermplasmList germplasmList = GermplasmListTestDataInitializer.createGermplasmList(listId, true);
		final Germplasm listDataGermplasm = this.germplasmTestDataGenerator
				.createGermplasmWithPreferredAndNonpreferredNames();
		return this.createTestListDataForList(listDataGermplasm, germplasmList);
	}

	private GermplasmListData createTestListDataForList(final Germplasm listDataGermplasm,
			final GermplasmList listDataGermplasmList) {
		final GermplasmListData listData = GermplasmListDataTestDataInitializer.createGermplasmListData(
				listDataGermplasmList, listDataGermplasm.getGid(), GermplasmListDataDAOTest.TEST_ENTRY_ID);
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

}
