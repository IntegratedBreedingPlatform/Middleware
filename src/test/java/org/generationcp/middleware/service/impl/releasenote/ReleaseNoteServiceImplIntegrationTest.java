package org.generationcp.middleware.service.impl.releasenote;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.pojos.workbench.releasenote.ReleaseNote;
import org.generationcp.middleware.pojos.workbench.releasenote.ReleaseNoteUser;
import org.generationcp.middleware.service.api.releasenote.ReleaseNoteService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ReleaseNoteServiceImplIntegrationTest extends IntegrationTestBase {

	private WorkbenchDaoFactory workbenchDaoFactory;
	private Integer userId;

	@Value("${bms.version}")
	private String bmsVersion;

	@Autowired
	private ReleaseNoteService releaseNoteService;

	@Before
	public void setUp() throws Exception {
		this.workbenchDaoFactory = new WorkbenchDaoFactory(this.workbenchSessionProvider);

		this.userId = this.findAdminUser();
	}

	@Test
	public void shouldShowAndGetLatestReleaseNote_OK() {

		//Should not be any release note yet
		assertFalse(this.releaseNoteService.getLatestReleaseNote().isPresent());

		this.insertReleaseNote("16.3", LocalDate.now().minusMonths(1));
		this.insertReleaseNote(this.bmsVersion, LocalDate.now().minusDays(4));
		this.insertReleaseNote("17.1.0", LocalDate.now().minusDays(3));

		final ReleaseNote expectedReleaseNote = this.insertReleaseNote("17.2", LocalDate.now().minusDays(1));

		this.insertReleaseNote("18.3", LocalDate.now().plusDays(1));

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

		final Optional<ReleaseNote> latestReleaseNote = this.releaseNoteService.getLatestReleaseNote();
		assertTrue(latestReleaseNote.isPresent());

		final ReleaseNote actualReleaseNote = latestReleaseNote.get();
		assertThat(actualReleaseNote.getId(), is(expectedReleaseNote.getId()));

		//Mark the release note so it won't be shown again
		this.releaseNoteService.showAgain(this.userId, false);

		//Should not return the release note 'cause the user don't want to see it again
		assertFalse(this.releaseNoteService.shouldShowReleaseNote(this.userId));
	}

	private ReleaseNote insertReleaseNote(final String version, final LocalDate date) {
		return this.workbenchDaoFactory.getReleaseNoteDAO().save(
			new ReleaseNote(version, Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()), version));
	}

}
