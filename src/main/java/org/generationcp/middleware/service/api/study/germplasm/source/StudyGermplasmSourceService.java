package org.generationcp.middleware.service.api.study.germplasm.source;

import java.util.List;

public interface StudyGermplasmSourceService {

	List<StudyGermplasmSourceDto> getStudyGermplasmSourceList(final StudyGermplasmSourceRequest searchParameters);

	long countStudyGermplasmSourceList(final StudyGermplasmSourceRequest searchParameters);

	long countFilteredStudyGermplasmSourceList(StudyGermplasmSourceRequest studyGermplasmSourceRequest);

	void saveStudyGermplasmSource(List<StudyGermplasmSourceInput> studyGermplasmSourceInputList);




}
