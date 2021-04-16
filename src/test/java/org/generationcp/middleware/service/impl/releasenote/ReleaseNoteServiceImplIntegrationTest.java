package org.generationcp.middleware.service.impl.releasenote;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.pojos.workbench.releasenote.ReleaseNote;
import org.generationcp.middleware.pojos.workbench.releasenote.ReleaseNoteUser;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.Random;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ReleaseNoteServiceImplIntegrationTest extends IntegrationTestBase {

	private static final String RELEASE_NOTE_VERSION = "version_" + new Random().nextInt();

	private ReleaseNoteServiceImpl releaseNoteService;
	private WorkbenchDaoFactory workbenchDaoFactory;
	private Integer userId;

	@Before
	public void setUp() throws Exception {
		this.releaseNoteService = new ReleaseNoteServiceImpl(this.workbenchSessionProvider);
		this.workbenchDaoFactory = new WorkbenchDaoFactory(this.workbenchSessionProvider);

		this.userId = this.findAdminUser();
	}

	@Test
	public void shouldShowReleaseNote_OK() {

		//Should not be any release note yet
		assertFalse(this.releaseNoteService.getLatestReleaseNote().isPresent());

		//Create a release note
		final LocalDate localDate = LocalDate.now().minusDays(1);
		this.workbenchDaoFactory.getReleaseNoteDAO().save(
			new ReleaseNote(RELEASE_NOTE_VERSION, Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())));

		//Check the release note was created
		final Optional<ReleaseNote> optionalLatestReleaseNote = this.releaseNoteService.getLatestReleaseNote();
		assertTrue(optionalLatestReleaseNote.isPresent());

		final ReleaseNote releaseNote = optionalLatestReleaseNote.get();
		assertThat(releaseNote.getId(), is(releaseNote.getId()));
		assertNotNull(releaseNote.getReleaseDate());

		//Check that the user has not already seen the release note
		assertFalse(this.workbenchDaoFactory.getReleaseNoteUserDAO().getByReleaseNoteIdAndUserId(releaseNote.getId(), this.userId)
			.isPresent());

		assertTrue(this.releaseNoteService.shouldShowReleaseNote(this.userId));

		//Check that the user has release note person row
		final Optional<ReleaseNoteUser> releaseNoteIdAndUserId =
			this.workbenchDaoFactory.getReleaseNoteUserDAO().getByReleaseNoteIdAndUserId(releaseNote.getId(), this.userId);
		assertTrue(releaseNoteIdAndUserId.isPresent());
		assertTrue(releaseNoteIdAndUserId.get().getShowAgain());

		//Mark the release note so it won't be shown again
		this.releaseNoteService.dontShowAgain(this.userId);

		//Should not return the release note 'cause the user don't want to see it again
		assertFalse(this.releaseNoteService.shouldShowReleaseNote(this.userId));
	}

}
