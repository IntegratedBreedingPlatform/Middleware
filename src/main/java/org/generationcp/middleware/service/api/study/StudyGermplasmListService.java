
package org.generationcp.middleware.service.api.study;

import java.util.List;
import java.util.Set;

public interface StudyGermplasmListService {

	List<StudyGermplasmDto> getGermplasmList(final int studyBusinessIdentifier);

	List<StudyGermplasmDto> getGermplasmListFromPlots(final int studyBusinessIdentifier, Set<Integer> plotNos);

}
