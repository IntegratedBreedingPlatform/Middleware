package org.generationcp.middleware.service.releasenote;

import org.generationcp.middleware.pojos.workbench.releasenote.ReleaseNote;

import java.util.Optional;

public interface ReleaseNoteService {

	Optional<ReleaseNote> showReleaseNote(Integer personId);

}
