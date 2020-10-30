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
	public void shouldCreateAndGetGermplasmListFolder_OK() {

		final String folderName = "NewFolder";

		assertFalse(this.germplasmListService.getGermplasmListByParentAndName(folderName, this.parentFolderId, PROGRAM_UUID).isPresent());

		final Integer germplasmListNewFolderId =
			this.germplasmListService.createGermplasmListFolder(USER_ID, folderName, this.parentFolderId, PROGRAM_UUID);
		assertNotNull(germplasmListNewFolderId);

		final Optional<GermplasmList> germplasmListById = this.germplasmListService.getGermplasmListById(germplasmListNewFolderId);
		assertTrue(germplasmListById.isPresent());
		final GermplasmList newGermplasmList = germplasmListById.get();
		assertNotNull(newGermplasmList);
		assertThat(newGermplasmList.getId(), is(germplasmListNewFolderId));
		assertNotNull(newGermplasmList.getDate());
		assertThat(newGermplasmList.getUserId(), is(USER_ID));
		assertThat(newGermplasmList.getDescription(), is(folderName));
		assertThat(newGermplasmList.getName(), is(folderName));
		assertNull(newGermplasmList.getNotes());
		assertNotNull(newGermplasmList.getParent());
		assertThat(newGermplasmList.getParent().getId(), is(this.parentFolderId));
		assertThat(newGermplasmList.getType(), is(GermplasmList.FOLDER_TYPE));
		assertThat(newGermplasmList.getProgramUUID(), is(PROGRAM_UUID));
		assertThat(newGermplasmList.getStatus(), is(GermplasmList.Status.FOLDER.getCode()));

		final Optional<GermplasmList> germplasmListByParentAndName =
			this.germplasmListService.getGermplasmListByParentAndName(folderName, this.parentFolderId, PROGRAM_UUID);
		assertTrue(germplasmListByParentAndName.isPresent());
		assertThat(germplasmListByParentAndName.get().getId(), is(germplasmListNewFolderId));
	}

}
