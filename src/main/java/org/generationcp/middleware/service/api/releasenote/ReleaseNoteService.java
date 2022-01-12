package org.generationcp.middleware.service.api.releasenote;

import org.generationcp.middleware.pojos.workbench.releasenote.ReleaseNote;

import java.util.Optional;

public interface ReleaseNoteService {

	boolean shouldShowReleaseNote(Integer userId);

	Optional<ReleaseNote> getLatestReleaseNote();

	void showAgain(Integer userId, boolean showAgain);

}
