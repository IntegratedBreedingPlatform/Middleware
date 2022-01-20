package org.generationcp.middleware.service.impl.releasenote;

import org.generationcp.middleware.dao.WorkbenchUserDAO;
import org.generationcp.middleware.dao.releasenote.ReleaseNoteDAO;
import org.generationcp.middleware.dao.releasenote.ReleaseNoteUserDAO;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.pojos.workbench.releasenote.ReleaseNote;
import org.generationcp.middleware.pojos.workbench.releasenote.ReleaseNoteUser;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.Times;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.Optional;
import java.util.Random;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ReleaseNoteServiceImplTest {

	private static final Integer USER_ID = new Random().nextInt();
	private static final Integer RELEASE_NOTE_ID = new Random().nextInt();
	private static final String RELEASE_NOTE_VERSION = "17." + new Random().nextInt();

	@InjectMocks
	private ReleaseNoteServiceImpl releaseNoteService;

	@Mock
	private WorkbenchDaoFactory workbenchDaoFactory;

	@Mock
	private ReleaseNoteDAO releaseNoteDAO;

	@Mock
	private ReleaseNoteUserDAO releaseNoteUserDAO;

	@Mock
	private WorkbenchUserDAO userDAO;

	@Captor
	private ArgumentCaptor<ReleaseNoteUser> releaseNoteUserArgumentCaptor;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		Mockito.when(this.workbenchDaoFactory.getReleaseNoteDAO()).thenReturn(this.releaseNoteDAO);
		Mockito.when(this.workbenchDaoFactory.getReleaseNoteUserDAO()).thenReturn(this.releaseNoteUserDAO);

		final WorkbenchUser person = Mockito.mock(WorkbenchUser.class);
		Mockito.when(person.getUserid()).thenReturn(USER_ID);
		Mockito.when(this.userDAO.getById(USER_ID)).thenReturn(person);
		Mockito.when(this.workbenchDaoFactory.getWorkbenchUserDAO()).thenReturn(this.userDAO);

		ReflectionTestUtils.setField(this.releaseNoteService, "workbenchDaoFactory", this.workbenchDaoFactory);
		ReflectionTestUtils.setField(this.releaseNoteService, "bmsVersion", RELEASE_NOTE_VERSION);
	}

	@Test
	public void shouldShowReleaseNote_NoReleaseNote() {
		Mockito.when(this.releaseNoteDAO.getLatestByMajorVersion(ArgumentMatchers.anyString())).thenReturn(Optional.empty());

		final boolean shouldShowReleaseNote = this.releaseNoteService.shouldShowReleaseNote(USER_ID);
		assertFalse(shouldShowReleaseNote);

		Mockito.verify(this.releaseNoteDAO).getLatestByMajorVersion(ArgumentMatchers.anyString());
		Mockito.verifyNoMoreInteractions(this.releaseNoteDAO);

		Mockito.verifyZeroInteractions(this.releaseNoteUserDAO);
	}

	@Test
	public void shouldShowReleaseNote_HasNeverShownToUser() {
		final ReleaseNote releaseNote = this.mockReleaseNote();
		Mockito.when(this.releaseNoteDAO.getLatestByMajorVersion(ArgumentMatchers.anyString())).thenReturn(Optional.of(releaseNote));

		Mockito.when(this.releaseNoteUserDAO.getByReleaseNoteIdAndUserId(releaseNote.getId(), USER_ID)).thenReturn(Optional.empty());

		final boolean shouldShowReleaseNote = this.releaseNoteService.shouldShowReleaseNote(USER_ID);
		assertTrue(shouldShowReleaseNote);

		Mockito.verify(this.releaseNoteDAO).getLatestByMajorVersion(ArgumentMatchers.anyString());
		Mockito.verifyNoMoreInteractions(this.releaseNoteDAO);

		Mockito.verify(this.releaseNoteUserDAO).getByReleaseNoteIdAndUserId(releaseNote.getId(), USER_ID);
		Mockito.verify(this.releaseNoteUserDAO).save(this.releaseNoteUserArgumentCaptor.capture());
		final ReleaseNoteUser actualReleaseNoteUser = this.releaseNoteUserArgumentCaptor.getValue();
		assertThat(actualReleaseNoteUser.getUser().getUserid(), is(USER_ID));
		assertThat(actualReleaseNoteUser.getReleaseNote(), is(releaseNote));
		assertTrue(actualReleaseNoteUser.getShowAgain());

		Mockito.verifyNoMoreInteractions(this.releaseNoteUserDAO);
	}

	@Test
	public void shouldShowReleaseNote_UserDontWantToSeeItAgain() {
		final ReleaseNote releaseNote = this.mockReleaseNote();
		Mockito.when(this.releaseNoteDAO.getLatestByMajorVersion(ArgumentMatchers.anyString())).thenReturn(Optional.of(releaseNote));

		final ReleaseNoteUser releaseNoteUser = this.mockReleaseNoteUser(false);
		Mockito.when(this.releaseNoteUserDAO.getByReleaseNoteIdAndUserId(releaseNote.getId(), USER_ID)).thenReturn(Optional.of(releaseNoteUser));

		final boolean shouldShowReleaseNote = this.releaseNoteService.shouldShowReleaseNote(USER_ID);
		assertFalse(shouldShowReleaseNote);

		Mockito.verify(this.releaseNoteDAO).getLatestByMajorVersion(ArgumentMatchers.anyString());
		Mockito.verifyNoMoreInteractions(this.releaseNoteDAO);

		Mockito.verify(this.releaseNoteUserDAO).getByReleaseNoteIdAndUserId(releaseNote.getId(), USER_ID);
		Mockito.verify(this.releaseNoteUserDAO, new Times(0)).save(ArgumentMatchers.any(ReleaseNoteUser.class));

		Mockito.verifyNoMoreInteractions(this.releaseNoteUserDAO);
	}

	@Test
	public void shouldShowReleaseNote_UserWantsToSeeItAgain() {
		final ReleaseNote releaseNote = this.mockReleaseNote();
		Mockito.when(this.releaseNoteDAO.getLatestByMajorVersion(ArgumentMatchers.anyString())).thenReturn(Optional.of(releaseNote));

		final ReleaseNoteUser releaseNoteUser = this.mockReleaseNoteUser(true);
		Mockito.when(this.releaseNoteUserDAO.getByReleaseNoteIdAndUserId(releaseNote.getId(), USER_ID)).thenReturn(Optional.of(releaseNoteUser));

		final boolean shouldShowReleaseNote = this.releaseNoteService.shouldShowReleaseNote(USER_ID);
		assertTrue(shouldShowReleaseNote);

		Mockito.verify(this.releaseNoteDAO).getLatestByMajorVersion(ArgumentMatchers.anyString());
		Mockito.verifyNoMoreInteractions(this.releaseNoteDAO);

		Mockito.verify(this.releaseNoteUserDAO).getByReleaseNoteIdAndUserId(releaseNote.getId(), USER_ID);
		Mockito.verify(this.releaseNoteUserDAO, new Times(0)).save(ArgumentMatchers.any(ReleaseNoteUser.class));

		Mockito.verifyNoMoreInteractions(this.releaseNoteUserDAO);
	}

	@Test
	public void getLatestReleaseNote() {
		final ReleaseNote releaseNote = this.mockReleaseNote();
		Mockito.when(this.releaseNoteDAO.getLatestByMajorVersion(ArgumentMatchers.anyString())).thenReturn(Optional.of(releaseNote));

		final Optional<ReleaseNote> latestReleaseNote = this.releaseNoteService.getLatestReleaseNote();
		assertTrue(latestReleaseNote.isPresent());
		assertThat(latestReleaseNote.get(), is(releaseNote));
	}

	@Test
	public void dontShowAgain() {
		final ReleaseNote releaseNote = this.mockReleaseNote();
		Mockito.when(this.releaseNoteDAO.getLatestByMajorVersion(ArgumentMatchers.anyString())).thenReturn(Optional.of(releaseNote));

		final ReleaseNoteUser releaseNoteUser = Mockito.mock(ReleaseNoteUser.class, Mockito.CALLS_REAL_METHODS);
		Mockito.when(this.releaseNoteUserDAO.getByReleaseNoteIdAndUserId(releaseNote.getId(), USER_ID)).thenReturn(Optional.of(releaseNoteUser));

		this.releaseNoteService.showAgain(USER_ID, false);

		Mockito.verify(this.releaseNoteUserDAO).save(this.releaseNoteUserArgumentCaptor.capture());
		final ReleaseNoteUser actualReleaseNoteUser = this.releaseNoteUserArgumentCaptor.getValue();
		assertFalse(actualReleaseNoteUser.getShowAgain());
	}

	private ReleaseNote mockReleaseNote() {
		final ReleaseNote mock = Mockito.mock(ReleaseNote.class);
		Mockito.when(mock.getId()).thenReturn(RELEASE_NOTE_ID);
		Mockito.when(mock.getVersion()).thenReturn(RELEASE_NOTE_VERSION);
		Mockito.when(mock.getReleaseDate()).thenReturn(new Date());
		return mock;
	}

	private ReleaseNoteUser mockReleaseNoteUser(final boolean showAgain) {
		final ReleaseNoteUser mock = Mockito.mock(ReleaseNoteUser.class, Mockito.CALLS_REAL_METHODS);
		Mockito.when(mock.getShowAgain()).thenReturn(showAgain);
		return mock;
	}

}
