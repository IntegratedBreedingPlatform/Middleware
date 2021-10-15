
package org.generationcp.middleware.dao.germplasmlist;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.GermplasmTestDataGenerator;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.NameDAO;
import org.generationcp.middleware.data.initializer.GermplasmListDataTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.germplasm.GermplasmParent;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

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

		this.germplasmTestDataGenerator = new GermplasmTestDataGenerator(this.germplasmManager, new NameDAO(this.sessionProvder
			.getSession()));
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
			.asList(new Integer[] {testGermplasmListData1.getId(), testGermplasmListData2.getId()});
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
	public void testGetGermplasmDataListMapByListIds() {
		// insert a new list data record from a newly-created list and germplasm
		// records
		final GermplasmListData testGermplasmListData = this.createTestListWithListData();

		final Map<Integer, List<GermplasmListData>> listDataMap = this.germplasmListDataDAO.getGermplasmDataListMapByListIds(
			Collections.singletonList(testGermplasmListData.getList().getId()));
		Assert.assertNotNull(listDataMap);
		final List<GermplasmListData> listDataRecords = listDataMap.get(testGermplasmListData.getList().getId());
		Assert.assertTrue(CollectionUtils.isNotEmpty(listDataRecords));
		final GermplasmListData germplasmListData = listDataRecords.get(0);
		Assert.assertEquals("The id should be " + testGermplasmListData.getId(), testGermplasmListData.getId(),
			germplasmListData.getId());
		Assert.assertEquals("The list id should be " + testGermplasmListData.getList().getId(),
			testGermplasmListData.getList().getId(), germplasmListData.getList().getId());
		Assert.assertEquals("The gid should be " + testGermplasmListData.getGid(), testGermplasmListData.getGid(),
			germplasmListData.getGid());

	}

	@Test
	public void testGetByListIdAndEntryId() {
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
	public void testGetByListIdAndLrecId() {
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
	public void testRetrieveCrossListDataWithImmediateParents() {
		final Germplasm parentGermplasm = this.germplasmTestDataGenerator
			.createGermplasmWithPreferredAndNonpreferredNames();
		final Germplasm childGermplasm = this.germplasmTestDataGenerator.createChildGermplasm(parentGermplasm,
			"VARIETY");
		final GermplasmListData listData = this.createTestListWithListData(childGermplasm);
		final List<GermplasmListData> listDataList = this.germplasmListDataDAO
			.retrieveGermplasmListDataWithImmediateParents(listData.getList().getId());
		Assert.assertEquals("There should be only 1 list data under the list with id" + listData.getList().getId(), 1,
			listDataList.size());

		for (final GermplasmListData currentGermplasmListData : listDataList) {
			Assert.assertEquals("List data id should be " + listData.getId(), listData.getId(),
				currentGermplasmListData.getId());
			Assert.assertEquals("Entry id should be " + listData.getEntryId(), listData.getEntryId(),
				currentGermplasmListData.getEntryId());
			Assert.assertEquals("Desig should be " + listData.getDesignation(), listData.getDesignation(),
				currentGermplasmListData.getDesignation());
			Assert.assertEquals("Gid should be " + listData.getGid(), listData.getGid(),
				currentGermplasmListData.getGid());
			Assert.assertEquals("Seed source should be " + listData.getSeedSource(), listData.getSeedSource(),
				currentGermplasmListData.getSeedSource());
			Assert.assertEquals("Breeding method name should be " + GermplasmTestDataGenerator.TEST_METHOD_NAME,
				GermplasmTestDataGenerator.TEST_METHOD_NAME, currentGermplasmListData.getBreedingMethodName());

			// Check parent germplasm values
			Assert.assertEquals("Female Parent GID should be " + listData.getFemaleGid(), listData.getFemaleGid(),
				currentGermplasmListData.getFemaleGid());
			Assert.assertEquals("Male Parent GID should be " + listData.getMaleGid(), listData.getMaleGid(),
				currentGermplasmListData.getMaleGid());
			Assert.assertEquals("Female Parent designation should be " + parentGermplasm.getPreferredName().getNval(),
				parentGermplasm.getPreferredName().getNval(), currentGermplasmListData.getFemaleParentDesignation());
			Assert.assertEquals("Male Parent designation should be " + parentGermplasm.getPreferredName().getNval(),
				parentGermplasm.getPreferredName().getNval(), currentGermplasmListData.getMaleParents().get(0).getDesignation());
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
		listData.setFemaleParent(new GermplasmParent(listDataGermplasm.getGpid1(), "", ""));
		listData.addMaleParent(new GermplasmParent(listDataGermplasm.getGpid2(), "", ""));
		this.germplasmListDataDAO.save(listData);

		return listData;
	}

	private GermplasmList createTestList() {
		final GermplasmList germplasmList = GermplasmListTestDataInitializer.createGermplasmList(null, false);
		this.germplasmListDAO.saveOrUpdate(germplasmList);
		return germplasmList;
	}

	@Test
	public void testGetByListIdAndGid() {
		// insert new list data record from a newly-created list and germplasm
		// records
		final Germplasm germplasm = this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames();
		final GermplasmListData testGermplasmListData = this.createTestListWithListData(germplasm);

		// get the list data record from the database
		final GermplasmListData germplasmListData = this.germplasmListDataDAO
			.getByListIdAndGid(testGermplasmListData.getList().getId(), germplasm.getGid());
		Assert.assertNotNull(germplasmListData);

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
	public void testSearchForGermplasmListsWhereOperationIsEqual() {
		final GermplasmList testList = this.createTestList();
		final List<GermplasmList> resultLists = this.germplasmListDAO.searchForGermplasmLists(testList.getName(),
			testList.getProgramUUID(), Operation.EQUAL);

		Assert.assertEquals("The results array should contain 1 germplasm list.", 1, resultLists.size());
		Assert.assertEquals("The germplasm lists should have the same name.", testList.getName(),
			resultLists.get(0).getName());
		Assert.assertEquals("The germplasm lists should have the same type.", testList.getType(),
			resultLists.get(0).getType());
		Assert.assertEquals("The germplasm lists should have the same id.", testList.getId(),
			resultLists.get(0).getId());
	}

	@Test
	public void testSearchForGermplasmListsWhereOperationIsLike() {
		final GermplasmList testList = this.createTestList();
		List<GermplasmList> resultLists = this.germplasmListDAO.searchForGermplasmLists(testList.getName(),
			testList.getProgramUUID(), Operation.LIKE);

		Assert.assertEquals("The results array should contain 1 germplasm list.", 1, resultLists.size());
		Assert.assertEquals("The germplasm lists should have the same name.", testList.getName(),
			resultLists.get(0).getName());
		Assert.assertEquals("The germplasm lists should have the same type.", testList.getType(),
			resultLists.get(0).getType());
		Assert.assertEquals("The germplasm lists should have the same id.", testList.getId(),
			resultLists.get(0).getId());

		// With percent sign
		resultLists = this.germplasmListDAO.searchForGermplasmLists(testList.getName() + "%", testList.getProgramUUID(),
			Operation.LIKE);

		Assert.assertEquals("The results array should contain 1 germplasm list.", 1, resultLists.size());
		Assert.assertEquals("The germplasm lists should have the same name.", testList.getName(),
			resultLists.get(0).getName());
		Assert.assertEquals("The germplasm lists should have the same type.", testList.getType(),
			resultLists.get(0).getType());
		Assert.assertEquals("The germplasm lists should have the same id.", testList.getId(),
			resultLists.get(0).getId());
	}

	@Test
	public void testReplaceGermplasm() {

		final String crossExpansion = RandomStringUtils.randomAlphabetic(10);
		final Germplasm targetGermplasm = this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames();
		final Germplasm germplasm1 = this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames();
		final GermplasmListData germplasmListData1 = this.createTestListWithListData(germplasm1);
		final Germplasm germplasm2 = this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames();
		final GermplasmListData germplasmListData2 = this.createTestListWithListData(germplasm2);
		final Germplasm germplasm3 = this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames();
		final GermplasmListData germplasmListData3 = this.createTestListWithListData(germplasm3);

		this.germplasmListDataDAO.replaceGermplasm(Arrays.asList(germplasm1.getGid(), germplasm2.getGid(), germplasm3.getGid()),
			targetGermplasm, crossExpansion);

		this.sessionProvder.getSession().refresh(germplasmListData1);
		this.sessionProvder.getSession().refresh(germplasmListData2);
		this.sessionProvder.getSession().refresh(germplasmListData3);

		Assert.assertEquals(germplasmListData1.getGermplasmId(), targetGermplasm.getGid());
		Assert.assertEquals(germplasmListData2.getGermplasmId(), targetGermplasm.getGid());
		Assert.assertEquals(germplasmListData3.getGermplasmId(), targetGermplasm.getGid());
		Assert.assertEquals(germplasmListData1.getDesignation(), targetGermplasm.getPreferredName().getNval());
		Assert.assertEquals(germplasmListData2.getDesignation(), targetGermplasm.getPreferredName().getNval());
		Assert.assertEquals(germplasmListData3.getDesignation(), targetGermplasm.getPreferredName().getNval());
		Assert.assertEquals(germplasmListData1.getGroupName(), crossExpansion);
		Assert.assertEquals(germplasmListData2.getGroupName(), crossExpansion);
		Assert.assertEquals(germplasmListData3.getGroupName(), crossExpansion);
	}

	@Test
	public void testGetGidsByListId() {
		final Germplasm germplasm = this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames();
		final GermplasmListData germplasmListData = this.createTestListWithListData(germplasm);

		final List<Integer> gidsByListId = this.germplasmListDataDAO.getGidsByListId(germplasmListData.getList().getId());
		assertThat(gidsByListId, Matchers.hasSize(1));
		assertThat(gidsByListId.get(0), is(germplasm.getGid()));
	}

}
