package org.generationcp.middleware.service.impl.releasenote;

import org.generationcp.middleware.dao.PersonDAO;
import org.generationcp.middleware.dao.releasenote.ReleaseNoteDAO;
import org.generationcp.middleware.dao.releasenote.ReleaseNotePersonDAO;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.workbench.releasenote.ReleaseNote;
import org.generationcp.middleware.pojos.workbench.releasenote.ReleaseNotePerson;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.Times;
import org.springframework.test.util.ReflectionTestUtils;

import javax.swing.text.html.Option;
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
	private static final String RELEASE_NOTE_VERSION = "version_" + new Random().nextInt();

	@InjectMocks
	private ReleaseNoteServiceImpl releaseNoteService;

	@Mock
	private WorkbenchDaoFactory workbenchDaoFactory;

	@Mock
	private ReleaseNoteDAO releaseNoteDAO;

	@Mock
	private ReleaseNotePersonDAO releaseNotePersonDAO;

	@Mock
	private PersonDAO personDAO;

	@Captor
	private ArgumentCaptor<ReleaseNotePerson> releaseNotePersonArgumentCaptor;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		Mockito.when(this.workbenchDaoFactory.getReleaseNoteDAO()).thenReturn(this.releaseNoteDAO);
		Mockito.when(this.workbenchDaoFactory.getReleaseNotePersonDAO()).thenReturn(this.releaseNotePersonDAO);

		final Person person = Mockito.mock(Person.class);
		Mockito.when(person.getId()).thenReturn(USER_ID);
		Mockito.when(this.personDAO.getById(USER_ID)).thenReturn(person);
		Mockito.when(this.workbenchDaoFactory.getPersonDAO()).thenReturn(this.personDAO);

		ReflectionTestUtils.setField(this.releaseNoteService, "workbenchDaoFactory", this.workbenchDaoFactory);
	}

	@Test
	public void shouldShowReleaseNote_NoReleaseNote() {
		Mockito.when(this.releaseNoteDAO.getLatestReleaseNote()).thenReturn(Optional.empty());

		final Optional<ReleaseNote> releaseNote = this.releaseNoteService.shouldShowReleaseNote(USER_ID);
		assertFalse(releaseNote.isPresent());

		Mockito.verify(this.releaseNoteDAO).getLatestReleaseNote();
		Mockito.verifyNoMoreInteractions(this.releaseNoteDAO);

		Mockito.verifyZeroInteractions(this.releaseNotePersonDAO);
	}

	@Test
	public void shouldShowReleaseNote_HasNeverShownToUser() {
		final ReleaseNote releaseNote = this.mockReleaseNote();
		Mockito.when(this.releaseNoteDAO.getLatestReleaseNote()).thenReturn(Optional.of(releaseNote));

		Mockito.when(this.releaseNotePersonDAO.getByReleaseNoteIdAndPersonId(releaseNote.getId(), USER_ID)).thenReturn(Optional.empty());

		final Optional<ReleaseNote> actualReleaseNote = this.releaseNoteService.shouldShowReleaseNote(USER_ID);
		assertTrue(actualReleaseNote.isPresent());
		assertThat(actualReleaseNote.get(), is(releaseNote));

		Mockito.verify(this.releaseNoteDAO).getLatestReleaseNote();
		Mockito.verifyNoMoreInteractions(this.releaseNoteDAO);

		Mockito.verify(this.releaseNotePersonDAO).getByReleaseNoteIdAndPersonId(releaseNote.getId(), USER_ID);
		Mockito.verify(this.releaseNotePersonDAO).save(this.releaseNotePersonArgumentCaptor.capture());
		final ReleaseNotePerson actualReleaseNotePerson = this.releaseNotePersonArgumentCaptor.getValue();
		assertThat(actualReleaseNotePerson.getPerson().getId(), is(USER_ID));
		assertThat(actualReleaseNotePerson.getReleaseNote(), is(releaseNote));
		assertTrue(actualReleaseNotePerson.getShowAgain());

		Mockito.verifyNoMoreInteractions(this.releaseNotePersonDAO);
	}

	@Test
	public void shouldShowReleaseNote_UserDontWantToSeeItAgain() {
		final ReleaseNote releaseNote = this.mockReleaseNote();
		Mockito.when(this.releaseNoteDAO.getLatestReleaseNote()).thenReturn(Optional.of(releaseNote));

		final ReleaseNotePerson releaseNotePerson = this.mockReleaseNotePerson(false);
		Mockito.when(this.releaseNotePersonDAO.getByReleaseNoteIdAndPersonId(releaseNote.getId(), USER_ID)).thenReturn(Optional.of(releaseNotePerson));

		final Optional<ReleaseNote> actualReleaseNote = this.releaseNoteService.shouldShowReleaseNote(USER_ID);
		assertFalse(actualReleaseNote.isPresent());

		Mockito.verify(this.releaseNoteDAO).getLatestReleaseNote();
		Mockito.verifyNoMoreInteractions(this.releaseNoteDAO);

		Mockito.verify(this.releaseNotePersonDAO).getByReleaseNoteIdAndPersonId(releaseNote.getId(), USER_ID);
		Mockito.verify(this.releaseNotePersonDAO, new Times(0)).save(ArgumentMatchers.any(ReleaseNotePerson.class));

		Mockito.verifyNoMoreInteractions(this.releaseNotePersonDAO);
	}

	@Test
	public void shouldShowReleaseNote_UserWantsToSeeItAgain() {
		final ReleaseNote releaseNote = this.mockReleaseNote();
		Mockito.when(this.releaseNoteDAO.getLatestReleaseNote()).thenReturn(Optional.of(releaseNote));

		final ReleaseNotePerson releaseNotePerson = this.mockReleaseNotePerson(true);
		Mockito.when(this.releaseNotePersonDAO.getByReleaseNoteIdAndPersonId(releaseNote.getId(), USER_ID)).thenReturn(Optional.of(releaseNotePerson));

		final Optional<ReleaseNote> actualReleaseNote = this.releaseNoteService.shouldShowReleaseNote(USER_ID);
		assertTrue(actualReleaseNote.isPresent());
		assertThat(actualReleaseNote.get(), is(releaseNote));

		Mockito.verify(this.releaseNoteDAO).getLatestReleaseNote();
		Mockito.verifyNoMoreInteractions(this.releaseNoteDAO);

		Mockito.verify(this.releaseNotePersonDAO).getByReleaseNoteIdAndPersonId(releaseNote.getId(), USER_ID);
		Mockito.verify(this.releaseNotePersonDAO, new Times(0)).save(ArgumentMatchers.any(ReleaseNotePerson.class));

		Mockito.verifyNoMoreInteractions(this.releaseNotePersonDAO);
	}

	@Test
	public void getLatestReleaseNote() {
		final ReleaseNote releaseNote = this.mockReleaseNote();
		Mockito.when(this.releaseNoteDAO.getLatestReleaseNote()).thenReturn(Optional.of(releaseNote));

		final Optional<ReleaseNote> latestReleaseNote = this.releaseNoteService.getLatestReleaseNote();
		assertTrue(latestReleaseNote.isPresent());
		assertThat(latestReleaseNote.get(), is(releaseNote));
	}

	@Test
	public void dontShowAgain() {
		final ReleaseNote releaseNote = this.mockReleaseNote();
		Mockito.when(this.releaseNoteDAO.getLatestReleaseNote()).thenReturn(Optional.of(releaseNote));

		final ReleaseNotePerson releaseNotePerson = Mockito.mock(ReleaseNotePerson.class, Mockito.CALLS_REAL_METHODS);
		Mockito.when(this.releaseNotePersonDAO.getByReleaseNoteIdAndPersonId(releaseNote.getId(), USER_ID)).thenReturn(Optional.of(releaseNotePerson));

		this.releaseNoteService.dontShowAgain(USER_ID);

		Mockito.verify(this.releaseNotePersonDAO).save(this.releaseNotePersonArgumentCaptor.capture());
		final ReleaseNotePerson actualReleaseNotePerson = this.releaseNotePersonArgumentCaptor.getValue();
		assertFalse(actualReleaseNotePerson.getShowAgain());
	}

	private ReleaseNote mockReleaseNote() {
		final ReleaseNote mock = Mockito.mock(ReleaseNote.class);
		Mockito.when(mock.getId()).thenReturn(RELEASE_NOTE_ID);
		Mockito.when(mock.getVersion()).thenReturn(RELEASE_NOTE_VERSION);
		Mockito.when(mock.getReleaseDate()).thenReturn(new Date());
		return mock;
	}

	private ReleaseNotePerson mockReleaseNotePerson(final boolean showAgain) {
		final ReleaseNotePerson mock = Mockito.mock(ReleaseNotePerson.class, Mockito.CALLS_REAL_METHODS);
		Mockito.when(mock.getShowAgain()).thenReturn(showAgain);
		return mock;
	}

}
