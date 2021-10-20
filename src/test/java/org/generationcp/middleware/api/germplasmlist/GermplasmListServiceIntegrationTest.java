package org.generationcp.middleware.api.germplasmlist;

import com.google.common.collect.Sets;
import org.generationcp.middleware.DataSetupTest;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.api.germplasm.search.GermplasmSearchRequest;
import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.domain.inventory.common.SearchCompositeDto;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.ListDataProperty;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class GermplasmListServiceIntegrationTest extends IntegrationTestBase {

	private static final String SINGLE_CROSS_METHOD = "C2W";
	private static final String DOUBLE_CROSS_METHOD = "CDC";
	private static final String TEST_LIST_1_PARENT = "Test List #1 Parent";
	private static final Integer USER_ID = new Random().nextInt();
	private static final String PROGRAM_UUID = UUID.randomUUID().toString();

	@Autowired
	private GermplasmListService germplasmListService;

	@Autowired
	private GermplasmListManager germplasmListManager;

	private Integer parentFolderId;

	private DaoFactory daoFactory;

	@Before
	public void setUp() throws Exception {
		this.daoFactory = new DaoFactory(this.sessionProvder);

		//Create parent folder
		final GermplasmListTestDataInitializer germplasmListTDI = new GermplasmListTestDataInitializer();
		final GermplasmList germplasmListParent = germplasmListTDI
			.createGermplasmList(TEST_LIST_1_PARENT, USER_ID, "Test Parent List #1", null, 1,
				PROGRAM_UUID);
		this.parentFolderId = this.germplasmListManager.addGermplasmList(germplasmListParent);
	}

	@Test
	public void shouldCreateAndUpdateAndGetAndDeleteGermplasmListFolder_OK() {

		final String folderName = "NewFolder";

		assertFalse(this.germplasmListService.getGermplasmListByParentAndName(folderName, this.parentFolderId, PROGRAM_UUID).isPresent());

		//Create germplasm folder
		final Integer germplasmListNewFolderId =
			this.germplasmListService.createGermplasmListFolder(USER_ID, folderName, this.parentFolderId, PROGRAM_UUID);
		assertNotNull(germplasmListNewFolderId);

		//Get the created germplasm folder by id
		final Optional<GermplasmList> newGermplasmListById = this.germplasmListService.getGermplasmListById(germplasmListNewFolderId);
		assertTrue(newGermplasmListById.isPresent());
		final GermplasmList newGermplasmList = newGermplasmListById.get();
		this.assertGermplasmList(newGermplasmList, germplasmListNewFolderId, folderName);

		//Get the created germplasm folder by folder name and parent id
		final Optional<GermplasmList> germplasmListByParentAndName =
			this.germplasmListService.getGermplasmListByParentAndName(folderName, this.parentFolderId, PROGRAM_UUID);
		assertTrue(germplasmListByParentAndName.isPresent());
		assertThat(germplasmListByParentAndName.get().getId(), is(germplasmListNewFolderId));

		//Update germplasm folder
		final String updatedFolderName = "updatedFolderName";
		final Integer germplasmListUpdatedFolderId =
			this.germplasmListService.updateGermplasmListFolder(USER_ID, updatedFolderName, germplasmListNewFolderId, PROGRAM_UUID);
		assertNotNull(germplasmListUpdatedFolderId);
		assertThat(germplasmListUpdatedFolderId, is(germplasmListNewFolderId));

		//Get the updated germplasm folder by id
		final Optional<GermplasmList> updatedGermplasmListById = this.germplasmListService.getGermplasmListById(germplasmListUpdatedFolderId);
		assertTrue(updatedGermplasmListById.isPresent());
		final GermplasmList updatedGermplasmList = updatedGermplasmListById.get();
		this.assertGermplasmList(updatedGermplasmList, germplasmListUpdatedFolderId, updatedFolderName);

		//Delete the germplasm folder
		this.germplasmListService.deleteGermplasmListFolder(germplasmListUpdatedFolderId);

		//Should not get the deleted germplasm folder
		final Optional<GermplasmList> deletedGermplasmListById = this.germplasmListService.getGermplasmListById(germplasmListUpdatedFolderId);
		assertFalse(deletedGermplasmListById.isPresent());
	}

	@Test
	public void shouldMoveGermplasmListFolder_OK() {

		//Create germplasm folder 1
		final String folderName1 = "folderName1";
		final Integer newFolderId1 =
			this.germplasmListService.createGermplasmListFolder(USER_ID, folderName1, this.parentFolderId, PROGRAM_UUID);
		assertNotNull(newFolderId1);

		//Get the created germplasm folder by id
		final Optional<GermplasmList> newGermplasmListById1 = this.germplasmListService.getGermplasmListById(newFolderId1);
		assertTrue(newGermplasmListById1.isPresent());
		final GermplasmList newGermplasmList1 = newGermplasmListById1.get();
		this.assertGermplasmList(newGermplasmList1, newFolderId1, folderName1);
		assertThat(newGermplasmList1.getParentId(), is(this.parentFolderId));

		//Create germplasm folder 2
		final String folderName2 = "folderName2";
		final Integer newFolderId2 =
			this.germplasmListService.createGermplasmListFolder(USER_ID, folderName2, this.parentFolderId, PROGRAM_UUID);
		assertNotNull(newFolderId2);

		//Get the created germplasm folder by id
		final Optional<GermplasmList> newGermplasmListById2 = this.germplasmListService.getGermplasmListById(newFolderId2);
		assertTrue(newGermplasmListById2.isPresent());
		final GermplasmList newGermplasmList2 = newGermplasmListById2.get();
		this.assertGermplasmList(newGermplasmList2, newFolderId2, folderName2);
		assertThat(newGermplasmList2.getParentId(), is(this.parentFolderId));

		//Move folder 1 to folder 2
		final Integer movedListId =
			this.germplasmListService.moveGermplasmListFolder(newFolderId1, newFolderId2, PROGRAM_UUID);
		assertNotNull(movedListId);
		assertThat(movedListId, is(newFolderId1));

		//Get the moved folder
		final Optional<GermplasmList> movedFolderById = this.germplasmListService.getGermplasmListById(newFolderId1);
		assertTrue(movedFolderById.isPresent());
		final GermplasmList movedFolder = movedFolderById.get();
		assertThat(movedFolder.getParentId(), is(newFolderId2));
	}

	@Test
	public void shouldGetGermplasmListByIdAndProgramUUID_OK() {
		final Optional<GermplasmList> germplasmListByIdAndProgramUUID =
			this.germplasmListService.getGermplasmListByIdAndProgramUUID(this.parentFolderId, PROGRAM_UUID);
		assertTrue(germplasmListByIdAndProgramUUID.isPresent());
		final GermplasmList parentGermplasmList = germplasmListByIdAndProgramUUID.get();
		assertThat(parentGermplasmList.getId(), is(this.parentFolderId));
		assertThat(parentGermplasmList.getProgramUUID(), is(PROGRAM_UUID));
	}

	@Test
	public void shouldGetGermplasmListByIdAndNullProgramUUID_OK() {
		final String folderName1 = "folderName1";
		final Integer newFolderId1 =
			this.germplasmListService.createGermplasmListFolder(USER_ID, folderName1, this.parentFolderId, null);
		assertNotNull(newFolderId1);

		final Optional<GermplasmList> germplasmListByIdAndProgramUUID =
			this.germplasmListService.getGermplasmListByIdAndProgramUUID(newFolderId1, null);
		assertTrue(germplasmListByIdAndProgramUUID.isPresent());
		final GermplasmList newGermplasmList = germplasmListByIdAndProgramUUID.get();
		assertThat(newGermplasmList.getId(), is(newFolderId1));
		assertNull(newGermplasmList.getProgramUUID());
	}

	@Test
	public void shouldAddGermplasmListData_OK() {
		final Method method = this.daoFactory.getMethodDAO().getByCode(SINGLE_CROSS_METHOD);
		final Germplasm germplasm = this.createGermplasm(method);

		final int randomInt = new Random().nextInt(100);

		// Germplasm list
		final GermplasmList germplasmList = new GermplasmList(null, "Test Germplasm List " + randomInt,
			Long.valueOf(20141014), "LST", Integer.valueOf(1), "Test Germplasm List", null, 1);
		this.daoFactory.getGermplasmListDAO().saveOrUpdate(germplasmList);

		assertThat(germplasmList.getListData(), hasSize(0));

		// Germplasm list data
		final GermplasmListData germplasmListData = this.createGermplasmListData(germplasmList, germplasm.getGid(), 1);
		final List<GermplasmListData> germplasmListsData = Arrays.asList(germplasmListData);

		assertNull(germplasmListData.getListDataId());

		this.germplasmListService.addGermplasmListData(germplasmListsData);

		assertNotNull(germplasmListData.getListDataId());

		this.sessionProvder.getSession().flush();
		this.sessionProvder.getSession().clear();

		final Optional<GermplasmList> germplasmListById = this.germplasmListService.getGermplasmListById(germplasmList.getId());
		assertTrue(germplasmListById.isPresent());

		final GermplasmList actualGermplasmList = germplasmListById.get();
		assertThat(actualGermplasmList.getListData(), hasSize(1));

		final GermplasmListData actualGermplasmListData = actualGermplasmList.getListData().get(0);
		assertNotNull(actualGermplasmListData);
		assertThat(actualGermplasmListData.getListDataId(), is(germplasmListData.getListDataId()));
	}

	@Test
	public void testAddGermplasmEntriesToList_WithSelectedItems_OK() {
		final int randomInt = new Random().nextInt(100);

		// Germplasm list
		final GermplasmList germplasmList = new GermplasmList(null, "Test Germplasm List " + randomInt,
			Long.valueOf(20141014), "LST", Integer.valueOf(1), "Test Germplasm List", null, 1);
		this.daoFactory.getGermplasmListDAO().saveOrUpdate(germplasmList);

		// Add entry to list
		final Method singleCrossMethod = this.daoFactory.getMethodDAO().getByCode(SINGLE_CROSS_METHOD);
		final Germplasm existingGermplasmEntry = this.createGermplasm(singleCrossMethod);
		final GermplasmListData germplasmListData = this.createGermplasmListData(germplasmList, existingGermplasmEntry.getGid(), 1);
		this.daoFactory.getGermplasmListDataDAO().saveOrUpdate(germplasmListData);

		final ListDataProperty existingEntryProperty = new ListDataProperty(germplasmListData.getListDataId(),
			germplasmListData,
			GermplasmListServiceImpl.GermplasmListDataPropertyName.BREEDING_METHOD_NAME.getName(),
			existingGermplasmEntry.getMethod().getMname());
		this.daoFactory.getListDataPropertyDAO().save(existingEntryProperty);

		//Create entry to add into existing list
		final Method doubleCrossMethod = this.daoFactory.getMethodDAO().getByCode(DOUBLE_CROSS_METHOD);
		final Germplasm addGermplasmEntry = this.createGermplasm(doubleCrossMethod);

		final SearchCompositeDto<GermplasmSearchRequest, Integer> searchComposite = new SearchCompositeDto<>();
		searchComposite.setItemIds(Sets.newHashSet(addGermplasmEntry.getGid()));
		this.germplasmListService.addGermplasmEntriesToList(germplasmList.getId(), searchComposite, PROGRAM_UUID);

		this.sessionProvder.getSession().flush();
		this.sessionProvder.getSession().clear();

		final Optional<GermplasmList> germplasmListById = this.germplasmListService.getGermplasmListById(germplasmList.getId());
		assertTrue(germplasmListById.isPresent());

		final GermplasmList actualGermplasmList = germplasmListById.get();
		assertThat(actualGermplasmList.getListData(), hasSize(2));

		this.assertGermplasmListDataAndProperty(actualGermplasmList.getListData().get(0),
			GermplasmListServiceImpl.GermplasmListDataPropertyName.BREEDING_METHOD_NAME.getName(),
			singleCrossMethod.getMname());
		this.assertGermplasmListDataAndProperty(actualGermplasmList.getListData().get(1),
			GermplasmListServiceImpl.GermplasmListDataPropertyName.BREEDING_METHOD_NAME.getName(),
			doubleCrossMethod.getMname());
	}

	@Test
	public void testAddGermplasmEntriesToList_WithoutSelectedItems_OK() {
		final int randomInt = new Random().nextInt(100);

		// Germplasm list
		final GermplasmList germplasmList = new GermplasmList(null, "Test Germplasm List " + randomInt,
			Long.valueOf(20141014), "LST", Integer.valueOf(1), "Test Germplasm List", null, 1);
		this.daoFactory.getGermplasmListDAO().saveOrUpdate(germplasmList);

		// Add entry to list
		final Method singleCrossMethod = this.daoFactory.getMethodDAO().getByCode(SINGLE_CROSS_METHOD);
		final Germplasm existingGermplasmEntry = this.createGermplasm(singleCrossMethod);
		final GermplasmListData germplasmListData = this.createGermplasmListData(germplasmList, existingGermplasmEntry.getGid(), 1);
		this.daoFactory.getGermplasmListDataDAO().saveOrUpdate(germplasmListData);

		final ListDataProperty existingEntryProperty = new ListDataProperty(germplasmListData.getListDataId(),
			germplasmListData,
			GermplasmListServiceImpl.GermplasmListDataPropertyName.BREEDING_METHOD_NAME.getName(),
			existingGermplasmEntry.getMethod().getMname());
		this.daoFactory.getListDataPropertyDAO().save(existingEntryProperty);

		//Create entry to add into existing list
		final Method doubleCrossMethod = this.daoFactory.getMethodDAO().getByCode(DOUBLE_CROSS_METHOD);
		final Germplasm addGermplasmEntry = this.createGermplasm(doubleCrossMethod);

		final GermplasmSearchRequest germplasmSearchRequest = new GermplasmSearchRequest();
		germplasmSearchRequest.setGids(Arrays.asList(addGermplasmEntry.getGid()));
		final SearchCompositeDto<GermplasmSearchRequest, Integer> searchComposite = new SearchCompositeDto<>();
		searchComposite.setSearchRequest(germplasmSearchRequest);
		this.germplasmListService.addGermplasmEntriesToList(germplasmList.getId(), searchComposite, PROGRAM_UUID);

		this.sessionProvder.getSession().flush();
		this.sessionProvder.getSession().clear();

		final Optional<GermplasmList> germplasmListById = this.germplasmListService.getGermplasmListById(germplasmList.getId());
		assertTrue(germplasmListById.isPresent());

		final GermplasmList actualGermplasmList = germplasmListById.get();
		assertThat(actualGermplasmList.getListData(), hasSize(2));

		this.assertGermplasmListDataAndProperty(actualGermplasmList.getListData().get(0),
			GermplasmListServiceImpl.GermplasmListDataPropertyName.BREEDING_METHOD_NAME.getName(),
			singleCrossMethod.getMname());
		this.assertGermplasmListDataAndProperty(actualGermplasmList.getListData().get(1),
			GermplasmListServiceImpl.GermplasmListDataPropertyName.BREEDING_METHOD_NAME.getName(),
			doubleCrossMethod.getMname());
	}

	@Test
	public void testGetGermplasmLists_OK() {
		//create germplasm
		final Method singleCrossMethod = this.daoFactory.getMethodDAO().getByCode(SINGLE_CROSS_METHOD);
		final Germplasm germplasm = this.createGermplasm(singleCrossMethod);

		final List<GermplasmListDto>  germplasmListDtos = this.germplasmListService.getGermplasmLists(germplasm.getGid());

		//create germplasm list
		final int randomInt = new Random().nextInt(100);
		final GermplasmList germplasmList = new GermplasmList(null, "Test Germplasm List " + randomInt,
			Long.valueOf(20141014), "LST", Integer.valueOf(1), "Test Germplasm List", null, 1);
		this.daoFactory.getGermplasmListDAO().saveOrUpdate(germplasmList);

		// Add entry to list
		final GermplasmListData germplasmListData = this.createGermplasmListData(germplasmList, germplasm.getGid(), 1);
		this.daoFactory.getGermplasmListDataDAO().saveOrUpdate(germplasmListData);

		final List<GermplasmListDto>  updatedGermplasmListDtos = this.germplasmListService.getGermplasmLists(germplasm.getGid());

		Assert.assertEquals(germplasmListDtos.size() + 1, updatedGermplasmListDtos.size());
	}

	@Test
	public void testToggleGermplasmListStatus_OK() {
		final GermplasmList germplasmList = new GermplasmList(null, "Test Germplasm List ",
			Long.valueOf(20141014), "LST", Integer.valueOf(1), "Test Germplasm List", null, GermplasmList.Status.LIST.getCode());
		this.daoFactory.getGermplasmListDAO().saveOrUpdate(germplasmList);

		assertTrue(this.germplasmListService.toggleGermplasmListStatus(germplasmList.getId()));
		assertFalse(this.germplasmListService.toggleGermplasmListStatus(germplasmList.getId()));
	}

	private Germplasm createGermplasm(final Method method) {
		final Germplasm germplasm = new Germplasm(null, method.getMid(), 0, 0, 0,
			0, 0, 0, 0,
			0, 0, null, null, method);

		final Germplasm savedGermplasm = this.daoFactory.getGermplasmDao().save(germplasm);

		final Name name = new Name(null, savedGermplasm, 1, 1, "Name", 0, 0, 0);
		this.daoFactory.getNameDao().save(name);

		this.sessionProvder.getSession().flush();
		this.sessionProvder.getSession().clear();

		return savedGermplasm;
	}

	private GermplasmListData createGermplasmListData(final GermplasmList germplasmList, final int gid, final int entryNo) {
		return new GermplasmListData(null, germplasmList, gid, entryNo, "EntryCode" + entryNo,
			DataSetupTest.GERMPLSM_PREFIX + entryNo + " Source", DataSetupTest.GERMPLSM_PREFIX + entryNo,
			DataSetupTest.GERMPLSM_PREFIX + "Group A", 0, 0);
	}

	private void assertGermplasmList(final GermplasmList germplasmList, final Integer id, final String name) {
		assertNotNull(germplasmList);
		assertThat(germplasmList.getId(), is(id));
		assertNotNull(germplasmList.getDate());
		assertThat(germplasmList.getUserId(), is(USER_ID));
		assertThat(germplasmList.getDescription(), is(name));
		assertThat(germplasmList.getName(), is(name));
		assertNull(germplasmList.getNotes());
		assertNotNull(germplasmList.getParent());
		assertThat(germplasmList.getParent().getId(), is(this.parentFolderId));
		assertThat(germplasmList.getType(), is(GermplasmList.FOLDER_TYPE));
		assertThat(germplasmList.getProgramUUID(), is(PROGRAM_UUID));
		assertThat(germplasmList.getStatus(), is(GermplasmList.Status.FOLDER.getCode()));
	}

	private void assertGermplasmListDataAndProperty(final GermplasmListData actualGermplasmListData,
		final String expectedPropertyName, final String expectedPropertyValue) {
		assertNotNull(actualGermplasmListData);
		assertNotNull(actualGermplasmListData.getListDataId());

		final List<ListDataProperty> properties = actualGermplasmListData.getProperties();
		assertThat(properties, hasSize(1));
		assertThat(properties.get(0).getColumn(), is(expectedPropertyName));
		assertThat(properties.get(0).getValue(), is(expectedPropertyValue));
	}

}
