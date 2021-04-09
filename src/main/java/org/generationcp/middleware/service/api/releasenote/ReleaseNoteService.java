package org.generationcp.middleware.service.api.releasenote;

import org.generationcp.middleware.pojos.workbench.releasenote.ReleaseNote;

import java.util.Optional;

public interface ReleaseNoteService {

	Optional<ReleaseNote> shouldShowReleaseNote(Integer personId);

	Optional<ReleaseNote> getLatestReleaseNote();

	void dontShowAgain(Integer userId);

}
