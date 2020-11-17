package org.generationcp.middleware.api.germplasmlist;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class GermplasmListServiceIntegrationTest extends IntegrationTestBase {

	private static final String TEST_LIST_1_PARENT = "Test List #1 Parent";
	private static final Integer USER_ID = new Random().nextInt();
	private static final String PROGRAM_UUID = UUID.randomUUID().toString();

	@Autowired
	private GermplasmListService germplasmListService;

	@Autowired
	private GermplasmListManager germplasmListManager;

	private Integer parentFolderId;

	@Before
	public void setUp() throws Exception {
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
		String updatedFolderName = "updatedFolderName";
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

}
