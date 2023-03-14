package org.generationcp.middleware.service.impl.study;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.service.api.study.StudyTreeService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class StudyTreeServiceImplIntegrationTest extends IntegrationTestBase {

	private static final String PROGRAM_UUID = UUID.randomUUID().toString();

	@Autowired
	private StudyTreeService studyTreeService;

	private DmsProjectDao dmsProjectDao;

	@Before
	public void setUp() {
		if (this.dmsProjectDao == null) {
			this.dmsProjectDao = new DmsProjectDao(this.sessionProvder.getSession());
		}

		ContextHolder.setLoggedInUserId(this.findAdminUser());
	}

	@Test
	public void createAndUpdateAndDeleteStudyFolder_OK() {
		final String folderName = RandomStringUtils.randomAlphabetic(10);
		final Integer newFolderId = this.studyTreeService.createStudyTreeFolder(DmsProject.SYSTEM_FOLDER_ID, folderName, PROGRAM_UUID);

		final DmsProject newFolder = this.dmsProjectDao.getById(newFolderId);
		this.assertFolder(newFolder, folderName);

		final String updatedFolderName = RandomStringUtils.randomAlphabetic(10);
		final Integer updatedFolderId = this.studyTreeService.updateStudyTreeFolder(newFolderId, updatedFolderName);

		final DmsProject updatedFolder = this.dmsProjectDao.getById(updatedFolderId);
		this.assertFolder(updatedFolder, updatedFolderName);

		this.studyTreeService.deleteStudyFolder(updatedFolderId);

		final DmsProject deletedFolder = this.dmsProjectDao.getById(newFolderId);
		assertNull(deletedFolder);

		// Should create a folder with the updated name because the previous was deleted
		final Integer newFolderId2 =
			this.studyTreeService.createStudyTreeFolder(DmsProject.SYSTEM_FOLDER_ID, updatedFolderName, PROGRAM_UUID);

		final DmsProject newFolder2 = this.dmsProjectDao.getById(newFolderId2);
		this.assertFolder(newFolder2, updatedFolderName);
	}

	@Test
	public void moveStudyFolder_OK() {
		final Integer newFolderId =
			this.studyTreeService.createStudyTreeFolder(DmsProject.SYSTEM_FOLDER_ID, RandomStringUtils.randomAlphabetic(10), PROGRAM_UUID);

		final DmsProject newFolder = this.dmsProjectDao.getById(newFolderId);
		assertThat(newFolder.getParent().getProjectId(), is(DmsProject.SYSTEM_FOLDER_ID));

		final Integer newParentFolderId =
			this.studyTreeService.createStudyTreeFolder(DmsProject.SYSTEM_FOLDER_ID, RandomStringUtils.randomAlphabetic(10), PROGRAM_UUID);
		final Integer movedFolderId = this.studyTreeService.moveStudyNode(newFolderId, newParentFolderId);
		final DmsProject movedFolder = this.dmsProjectDao.getById(movedFolderId);
		assertNotNull(movedFolder);
		assertNotNull(movedFolder.getParent());
		assertThat(movedFolder.getParent().getProjectId(), is(newParentFolderId));
	}

	private void assertFolder(final DmsProject folder, final String name) {
		assertNotNull(folder);
		assertThat(folder.getName(), is(name));
		assertThat(folder.getDescription(), is(name));
		assertThat(folder.getObjective(), is(name));
		assertThat(folder.getCreatedBy(), is(this.findAdminUser().toString()));
		assertTrue(folder.isFolder());
		assertFalse(folder.getDeleted());
	}

}
