package org.generationcp.middleware.service.impl.releasenote;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.workbench.releasenote.ReleaseNote;
import org.generationcp.middleware.pojos.workbench.releasenote.ReleaseNotePerson;
import org.generationcp.middleware.service.api.releasenote.ReleaseNoteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class ReleaseNoteServiceImpl implements ReleaseNoteService {

	private final WorkbenchDaoFactory workbenchDaoFactory;

	public ReleaseNoteServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.workbenchDaoFactory = new WorkbenchDaoFactory(sessionProvider);
	}

	@Override
	public Optional<ReleaseNote> shouldShowReleaseNote(final Integer personId) {
		// Check if there is a release note available
		final Optional<ReleaseNote> optionalReleaseNote = this.getLatestReleaseNote();
		if (!optionalReleaseNote.isPresent()) {
			return Optional.empty();
		}

		// Check if the user has already seen it
		final ReleaseNote releaseNote = optionalReleaseNote.get();
		final Optional<ReleaseNotePerson> optionalReleaseNotePerson = this.getReleaseNotePerson(releaseNote.getId(), personId);
		if (!optionalReleaseNotePerson.isPresent()) {
			this.createReleaseNotePerson(releaseNote, personId);
			return optionalReleaseNote;
		}

		// Check if the user wants to see it again
		final ReleaseNotePerson releaseNotePerson = optionalReleaseNotePerson.get();
		return releaseNotePerson.getShowAgain() ? optionalReleaseNote : Optional.empty();
	}

	@Override
	public Optional<ReleaseNote> getLatestReleaseNote() {
		return this.workbenchDaoFactory.getReleaseNoteDAO().getLatestReleaseNote();
	}

	@Override
	public void dontShowAgain(final Integer userId) {
		this.getLatestReleaseNote().ifPresent(releaseNote ->
			this.getReleaseNotePerson(releaseNote.getId(), userId).ifPresent(releaseNotePerson -> {
				releaseNotePerson.dontShowAgain();
				this.workbenchDaoFactory.getReleaseNotePersonDAO().save(releaseNotePerson);
		}));
	}

	private void createReleaseNotePerson(final ReleaseNote releaseNote, final Integer personId) {
		final Person person = this.workbenchDaoFactory.getPersonDAO().getById(personId);
		final ReleaseNotePerson releaseNotePerson = new ReleaseNotePerson(releaseNote, person);
		this.workbenchDaoFactory.getReleaseNotePersonDAO().save(releaseNotePerson);
	}

	private Optional<ReleaseNotePerson> getReleaseNotePerson(final Integer releaseNoteId, final Integer personId) {
		return this.workbenchDaoFactory.getReleaseNotePersonDAO().getByReleaseNoteIdAndPersonId(releaseNoteId, personId);
	}

}
