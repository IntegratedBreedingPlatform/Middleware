package org.generationcp.middleware.service.api.study;

import java.util.List;


public interface StudyGermplasmListService {
	
	List<StudyGermplasmDto> getGermplasmList(final int studyBusinessIdentifier);

}
