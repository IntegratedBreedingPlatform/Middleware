package org.generationcp.middleware.service.impl.releasenote;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.pojos.workbench.releasenote.ReleaseNote;
import org.generationcp.middleware.pojos.workbench.releasenote.ReleaseNoteUser;
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
	public Optional<ReleaseNote> shouldShowReleaseNote(final Integer userId) {
		// Check if there is a release note available
		final Optional<ReleaseNote> optionalReleaseNote = this.getLatestReleaseNote();
		if (!optionalReleaseNote.isPresent()) {
			return Optional.empty();
		}

		// Check if the user has already seen it
		final ReleaseNote releaseNote = optionalReleaseNote.get();
		final Optional<ReleaseNoteUser> optionalReleaseNoteUser = this.getReleaseNoteUser(releaseNote.getId(), userId);
		if (!optionalReleaseNoteUser.isPresent()) {
			this.createReleaseNoteUser(releaseNote, userId);
			return optionalReleaseNote;
		}

		// Check if the user wants to see it again
		final ReleaseNoteUser releaseNoteUser = optionalReleaseNoteUser.get();
		return releaseNoteUser.getShowAgain() ? optionalReleaseNote : Optional.empty();
	}

	@Override
	public Optional<ReleaseNote> getLatestReleaseNote() {
		return this.workbenchDaoFactory.getReleaseNoteDAO().getLatestReleaseNote();
	}

	@Override
	public void dontShowAgain(final Integer userId) {
		this.getLatestReleaseNote().ifPresent(releaseNote ->
			this.getReleaseNoteUser(releaseNote.getId(), userId).ifPresent(releaseNoteUser -> {
				releaseNoteUser.dontShowAgain();
				this.workbenchDaoFactory.getReleaseNoteUserDAO().save(releaseNoteUser);
		}));
	}

	private void createReleaseNoteUser(final ReleaseNote releaseNote, final Integer userId) {
		final WorkbenchUser user = this.workbenchDaoFactory.getWorkbenchUserDAO().getById(userId);
		final ReleaseNoteUser releaseNoteUser = new ReleaseNoteUser(releaseNote, user);
		this.workbenchDaoFactory.getReleaseNoteUserDAO().save(releaseNoteUser);
	}

	private Optional<ReleaseNoteUser> getReleaseNoteUser(final Integer releaseNoteId, final Integer userId) {
		return this.workbenchDaoFactory.getReleaseNoteUserDAO().getByReleaseNoteIdAndUserId(releaseNoteId, userId);
	}

}
