
package org.generationcp.middleware.dao.germplasmlist;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.GermplasmTestDataGenerator;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.data.initializer.GermplasmListDataTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.germplasm.GermplasmParent;
import org.generationcp.middleware.util.CrossExpansionUtil;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class GermplasmListDataDAOTest extends IntegrationTestBase {

	private static final Integer TEST_ENTRY_ID = 1;

	private GermplasmListDataDAO germplasmListDataDAO;
	private GermplasmListDAO germplasmListDAO;
	private GermplasmTestDataGenerator germplasmTestDataGenerator;

	@Autowired
	private GermplasmDataManager germplasmManager;

	private DaoFactory daoFactory;

	@Before
	public void setUp() {
		this.germplasmListDataDAO = new GermplasmListDataDAO();
		this.germplasmListDAO = new GermplasmListDAO();

		this.germplasmListDataDAO.setSession(this.sessionProvder.getSession());
		this.germplasmListDAO.setSession(this.sessionProvder.getSession());

		this.daoFactory = new DaoFactory(this.sessionProvder);

		this.germplasmTestDataGenerator = new GermplasmTestDataGenerator(daoFactory);
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

	@Test
	public void testTruncateGroupName() {
		final GermplasmListData listData = this.createTestListWithListData();
		final String groupName = randomAlphanumeric(CrossExpansionUtil.MAX_CROSS_NAME_SIZE + 1);
		listData.setGroupName(groupName);
		listData.truncateGroupNameIfNeeded();

		// should not thrown exception
		this.germplasmListDataDAO.saveOrUpdate(listData);

		Assert.assertThat(listData.getGroupName(), is(groupName.substring(0, CrossExpansionUtil.MAX_CROSS_NAME_SIZE - 1) + CrossExpansionUtil.CROSS_NAME_TRUNCATED_SUFFIX));

	}

	private GermplasmListData createTestListWithListData() {
		final Germplasm listDataGermplasm = this.germplasmTestDataGenerator
			.createGermplasmWithPreferredAndNonpreferredNames();
		return this.createTestListWithListData(listDataGermplasm);
	}

	private GermplasmListData createTestListWithListData(final Germplasm listDataGermplasm) {
		final GermplasmList listDataGermplasmList = this.createTestList();
		return this.createTestListDataForList(listDataGermplasm, listDataGermplasmList, GermplasmListDataDAOTest.TEST_ENTRY_ID);
	}

	private GermplasmListData createTestListDataForList(final Integer listId) {
		final GermplasmList germplasmList = GermplasmListTestDataInitializer.createGermplasmList(listId, true);
		final Germplasm listDataGermplasm = this.germplasmTestDataGenerator
			.createGermplasmWithPreferredAndNonpreferredNames();
		return this.createTestListDataForList(listDataGermplasm, germplasmList, GermplasmListDataDAOTest.TEST_ENTRY_ID);
	}

	private GermplasmListData createTestListDataForList(final Germplasm listDataGermplasm,
		final GermplasmList listDataGermplasmList, int entryNumber) {
		final GermplasmListData listData = GermplasmListDataTestDataInitializer.createGermplasmListData(
			listDataGermplasmList, listDataGermplasm.getGid(), entryNumber);
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

	@Test
	public void testReOrderEntries_moveEntryToSamePosition() {
		int entryNumber = 1;
		final GermplasmList germplasmList = this.createTestList();
		final GermplasmListData entry1 =
			this.createTestListDataForList(this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames(),
				germplasmList, entryNumber++);
		final GermplasmListData entry2 =
			this.createTestListDataForList(this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames(),
				germplasmList, entryNumber++);
		final GermplasmListData entry3 =
			this.createTestListDataForList(this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames(),
				germplasmList, entryNumber++);

		this.germplasmListDataDAO.reOrderEntries(germplasmList.getId(), Arrays.asList(entry2.getId()), 2);

		this.sessionProvder.getSession().clear();
		this.sessionProvder.getSession().flush();

		final List<GermplasmListData> actualList = this.germplasmListDataDAO.getByListId(germplasmList.getId());
		assertThat(actualList, hasSize(3));
		assertThat(actualList.get(0).getId(), is(entry1.getListDataId()));
		assertThat(actualList.get(0).getEntryId(), is(1));
		assertThat(actualList.get(1).getId(), is(entry2.getListDataId()));
		assertThat(actualList.get(1).getEntryId(), is(2));
		assertThat(actualList.get(2).getId(), is(entry3.getListDataId()));
		assertThat(actualList.get(2).getEntryId(), is(3));
	}

	@Test
	public void testReOrderEntries_moveFirstEntryToLastPosition() {
		int entryNumber = 1;
		final GermplasmList germplasmList = this.createTestList();
		final GermplasmListData entry1 =
			this.createTestListDataForList(this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames(),
				germplasmList, entryNumber++);
		final GermplasmListData entry2 =
			this.createTestListDataForList(this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames(),
				germplasmList, entryNumber++);
		final GermplasmListData entry3 =
			this.createTestListDataForList(this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames(),
				germplasmList, entryNumber++);
		final GermplasmListData entry4 =
			this.createTestListDataForList(this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames(),
				germplasmList, entryNumber++);

		this.germplasmListDataDAO.reOrderEntries(germplasmList.getId(), Arrays.asList(entry1.getId()), 4);

		this.sessionProvder.getSession().clear();
		this.sessionProvder.getSession().flush();

		final List<GermplasmListData> actualList = this.germplasmListDataDAO.getByListId(germplasmList.getId());
		assertThat(actualList, hasSize(4));
		assertThat(actualList.get(0).getId(), is(entry2.getListDataId()));
		assertThat(actualList.get(0).getEntryId(), is(1));
		assertThat(actualList.get(1).getId(), is(entry3.getListDataId()));
		assertThat(actualList.get(1).getEntryId(), is(2));
		assertThat(actualList.get(2).getId(), is(entry4.getListDataId()));
		assertThat(actualList.get(2).getEntryId(), is(3));
		assertThat(actualList.get(3).getId(), is(entry1.getListDataId()));
		assertThat(actualList.get(3).getEntryId(), is(4));
	}

	@Test
	public void testReOrderEntries_moveLastEntryToFirstPosition() {
		int entryNumber = 1;
		final GermplasmList germplasmList = this.createTestList();
		final GermplasmListData entry1 =
			this.createTestListDataForList(this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames(),
				germplasmList, entryNumber++);
		final GermplasmListData entry2 =
			this.createTestListDataForList(this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames(),
				germplasmList, entryNumber++);
		final GermplasmListData entry3 =
			this.createTestListDataForList(this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames(),
				germplasmList, entryNumber++);
		final GermplasmListData entry4 =
			this.createTestListDataForList(this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames(),
				germplasmList, entryNumber++);

		this.germplasmListDataDAO.reOrderEntries(germplasmList.getId(), Arrays.asList(entry4.getId()), 1);

		this.sessionProvder.getSession().clear();
		this.sessionProvder.getSession().flush();

		final List<GermplasmListData> actualList = this.germplasmListDataDAO.getByListId(germplasmList.getId());
		assertThat(actualList, hasSize(4));
		assertThat(actualList.get(0).getId(), is(entry4.getListDataId()));
		assertThat(actualList.get(0).getEntryId(), is(1));
		assertThat(actualList.get(1).getId(), is(entry1.getListDataId()));
		assertThat(actualList.get(1).getEntryId(), is(2));
		assertThat(actualList.get(2).getId(), is(entry2.getListDataId()));
		assertThat(actualList.get(2).getEntryId(), is(3));
		assertThat(actualList.get(3).getId(), is(entry3.getListDataId()));
		assertThat(actualList.get(3).getEntryId(), is(4));
	}

	/**
	 * Select entries 3, 5 and 8 and move them to position 4
	 */
	@Test
	public void testReOrderEntries_moveSeveralEntriesToMiddlePosition_1() {
		int entryNumber = 1;
		final GermplasmList germplasmList = this.createTestList();
		final GermplasmListData entry1 =
			this.createTestListDataForList(this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames(),
				germplasmList, entryNumber++);
		final GermplasmListData entry2 =
			this.createTestListDataForList(this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames(),
				germplasmList, entryNumber++);
		final GermplasmListData entry3 =
			this.createTestListDataForList(this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames(),
				germplasmList, entryNumber++);
		final GermplasmListData entry4 =
			this.createTestListDataForList(this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames(),
				germplasmList, entryNumber++);
		final GermplasmListData entry5 =
			this.createTestListDataForList(this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames(),
				germplasmList, entryNumber++);
		final GermplasmListData entry6 =
			this.createTestListDataForList(this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames(),
				germplasmList, entryNumber++);
		final GermplasmListData entry7 =
			this.createTestListDataForList(this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames(),
				germplasmList, entryNumber++);
		final GermplasmListData entry8 =
			this.createTestListDataForList(this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames(),
				germplasmList, entryNumber++);
		final GermplasmListData entry9 =
			this.createTestListDataForList(this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames(),
				germplasmList, entryNumber++);
		final GermplasmListData entry10 =
			this.createTestListDataForList(this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames(),
				germplasmList, entryNumber++);

		this.germplasmListDataDAO.reOrderEntries(germplasmList.getId(), Arrays.asList(entry3.getId(), entry5.getId(), entry8.getId()), 4);

		this.sessionProvder.getSession().clear();
		this.sessionProvder.getSession().flush();

		final List<GermplasmListData> actualList = this.germplasmListDataDAO.getByListId(germplasmList.getId());
		assertThat(actualList, hasSize(10));
		assertThat(actualList.get(0).getId(), is(entry1.getListDataId()));
		assertThat(actualList.get(0).getEntryId(), is(1));
		assertThat(actualList.get(1).getId(), is(entry2.getListDataId()));
		assertThat(actualList.get(1).getEntryId(), is(2));
		assertThat(actualList.get(2).getId(), is(entry4.getListDataId()));
		assertThat(actualList.get(2).getEntryId(), is(3));
		assertThat(actualList.get(3).getId(), is(entry3.getListDataId()));
		assertThat(actualList.get(3).getEntryId(), is(4));
		assertThat(actualList.get(4).getId(), is(entry5.getListDataId()));
		assertThat(actualList.get(4).getEntryId(), is(5));
		assertThat(actualList.get(5).getId(), is(entry8.getListDataId()));
		assertThat(actualList.get(5).getEntryId(), is(6));
		assertThat(actualList.get(6).getId(), is(entry6.getListDataId()));
		assertThat(actualList.get(6).getEntryId(), is(7));
		assertThat(actualList.get(7).getId(), is(entry7.getListDataId()));
		assertThat(actualList.get(7).getEntryId(), is(8));
		assertThat(actualList.get(8).getId(), is(entry9.getListDataId()));
		assertThat(actualList.get(8).getEntryId(), is(9));
		assertThat(actualList.get(9).getId(), is(entry10.getListDataId()));
		assertThat(actualList.get(9).getEntryId(), is(10));
	}

	/**
	 * Select entries 4, 6 and 7 and move them to position 7
	 */
	@Test
	public void testReOrderEntries_moveSeveralEntriesToMiddlePosition_2() {
		int entryNumber = 1;
		final GermplasmList germplasmList = this.createTestList();
		final GermplasmListData entry1 =
			this.createTestListDataForList(this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames(),
				germplasmList, entryNumber++);
		final GermplasmListData entry2 =
			this.createTestListDataForList(this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames(),
				germplasmList, entryNumber++);
		final GermplasmListData entry3 =
			this.createTestListDataForList(this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames(),
				germplasmList, entryNumber++);
		final GermplasmListData entry4 =
			this.createTestListDataForList(this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames(),
				germplasmList, entryNumber++);
		final GermplasmListData entry5 =
			this.createTestListDataForList(this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames(),
				germplasmList, entryNumber++);
		final GermplasmListData entry6 =
			this.createTestListDataForList(this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames(),
				germplasmList, entryNumber++);
		final GermplasmListData entry7 =
			this.createTestListDataForList(this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames(),
				germplasmList, entryNumber++);
		final GermplasmListData entry8 =
			this.createTestListDataForList(this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames(),
				germplasmList, entryNumber++);
		final GermplasmListData entry9 =
			this.createTestListDataForList(this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames(),
				germplasmList, entryNumber++);
		final GermplasmListData entry10 =
			this.createTestListDataForList(this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames(),
				germplasmList, entryNumber++);

		this.germplasmListDataDAO.reOrderEntries(germplasmList.getId(), Arrays.asList(entry4.getId(), entry6.getId(), entry7.getId()), 7);

		this.sessionProvder.getSession().clear();
		this.sessionProvder.getSession().flush();

		final List<GermplasmListData> actualList = this.germplasmListDataDAO.getByListId(germplasmList.getId());
		assertThat(actualList, hasSize(10));
		assertThat(actualList.get(0).getId(), is(entry1.getListDataId()));
		assertThat(actualList.get(0).getEntryId(), is(1));
		assertThat(actualList.get(1).getId(), is(entry2.getListDataId()));
		assertThat(actualList.get(1).getEntryId(), is(2));
		assertThat(actualList.get(2).getId(), is(entry3.getListDataId()));
		assertThat(actualList.get(2).getEntryId(), is(3));
		assertThat(actualList.get(3).getId(), is(entry5.getListDataId()));
		assertThat(actualList.get(3).getEntryId(), is(4));
		assertThat(actualList.get(4).getId(), is(entry8.getListDataId()));
		assertThat(actualList.get(4).getEntryId(), is(5));
		assertThat(actualList.get(5).getId(), is(entry9.getListDataId()));
		assertThat(actualList.get(5).getEntryId(), is(6));
		assertThat(actualList.get(6).getId(), is(entry4.getListDataId()));
		assertThat(actualList.get(6).getEntryId(), is(7));
		assertThat(actualList.get(7).getId(), is(entry6.getListDataId()));
		assertThat(actualList.get(7).getEntryId(), is(8));
		assertThat(actualList.get(8).getId(), is(entry7.getListDataId()));
		assertThat(actualList.get(8).getEntryId(), is(9));
		assertThat(actualList.get(9).getId(), is(entry10.getListDataId()));
		assertThat(actualList.get(9).getEntryId(), is(10));
	}

	@Test
	public void testGetListDataIdsByListId() {
		final GermplasmList germplasmList = this.createTestList();
		final GermplasmListData entry1 =
			this.createTestListDataForList(this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames(),
				germplasmList, 1);
		final GermplasmListData entry2 =
			this.createTestListDataForList(this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames(),
				germplasmList, 2);

		final List<Integer> listDataIdsByListId = this.germplasmListDataDAO.getListDataIdsByListId(germplasmList.getId());
		assertThat(listDataIdsByListId, hasSize(2));
		assertThat(listDataIdsByListId, containsInAnyOrder(entry2.getId(), entry1.getId()));
	}

}
