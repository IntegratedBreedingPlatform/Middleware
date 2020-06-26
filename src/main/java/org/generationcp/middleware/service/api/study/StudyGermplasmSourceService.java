package org.generationcp.middleware.service.api.study;

import java.util.List;

public interface StudyGermplasmSourceService {

	public List<StudyGermplasmSourceDto> getStudyGermplasmSourceList(final StudyGermplasmSourceRequest searchParameters);

	public long countStudyGermplasmSourceList(final StudyGermplasmSourceRequest searchParameters);

	public long countFilteredStudyGermplasmSourceList(StudyGermplasmSourceRequest studyGermplasmSourceRequest);
}
