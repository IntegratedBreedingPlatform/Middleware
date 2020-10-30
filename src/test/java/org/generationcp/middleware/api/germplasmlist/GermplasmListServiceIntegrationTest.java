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
	public void shouldCreateAndUpdateAndGetGermplasmListFolder_OK() {

		final String folderName = "NewFolder";

		assertFalse(this.germplasmListService.getGermplasmListByParentAndName(folderName, this.parentFolderId, PROGRAM_UUID).isPresent());

		final Integer germplasmListNewFolderId =
			this.germplasmListService.createGermplasmListFolder(USER_ID, folderName, this.parentFolderId, PROGRAM_UUID);
		assertNotNull(germplasmListNewFolderId);

		final Optional<GermplasmList> newGermplasmListById = this.germplasmListService.getGermplasmListById(germplasmListNewFolderId);
		assertTrue(newGermplasmListById.isPresent());
		final GermplasmList newGermplasmList = newGermplasmListById.get();
		this.assertGermplasmList(newGermplasmList, germplasmListNewFolderId, folderName);

		final Optional<GermplasmList> germplasmListByParentAndName =
			this.germplasmListService.getGermplasmListByParentAndName(folderName, this.parentFolderId, PROGRAM_UUID);
		assertTrue(germplasmListByParentAndName.isPresent());
		assertThat(germplasmListByParentAndName.get().getId(), is(germplasmListNewFolderId));

		String updatedFolderName = "updatedFolderName";
		final Integer germplasmListUpdatedFolderId =
			this.germplasmListService.updateGermplasmListFolder(USER_ID, updatedFolderName, germplasmListNewFolderId, PROGRAM_UUID);
		assertNotNull(germplasmListUpdatedFolderId);
		assertThat(germplasmListUpdatedFolderId, is(germplasmListNewFolderId));

		final Optional<GermplasmList> updatedGermplasmListById = this.germplasmListService.getGermplasmListById(germplasmListUpdatedFolderId);
		assertTrue(updatedGermplasmListById.isPresent());
		final GermplasmList updatedGermplasmList = updatedGermplasmListById.get();
		this.assertGermplasmList(updatedGermplasmList, germplasmListUpdatedFolderId, updatedFolderName);
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
