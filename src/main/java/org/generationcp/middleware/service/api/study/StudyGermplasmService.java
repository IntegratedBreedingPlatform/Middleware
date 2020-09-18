
package org.generationcp.middleware.service.api.study;

import org.generationcp.middleware.domain.study.StudyEntrySearchDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

//TODO rename to StudyEntryService
public interface StudyGermplasmService {

	List<StudyGermplasmDto> getGermplasm(int studyBusinessIdentifier);

	List<StudyEntryDto> getStudyEntries(int studyId, StudyEntrySearchDto.Filter filter, Pageable pageable);

	List<StudyGermplasmDto> getGermplasmFromPlots(int studyBusinessIdentifier, Set<Integer> plotNos);

	long countStudyEntries(int studyId);

	void deleteStudyEntries(int studyId);

	List<StudyGermplasmDto> saveStudyEntries(Integer studyId, List<StudyGermplasmDto> studyGermplasmDtoList);

	long countStudyGermplasmByEntryTypeIds(int studyId, List<String> systemDefinedEntryTypeIds);

	Optional<StudyGermplasmDto> getStudyGermplasm(int studyId, int entryId);

	StudyGermplasmDto replaceStudyGermplasm(int studyId, int entryId, int gid, String crossExpansion);

	void updateStudyEntryProperty(int studyId, StudyEntryPropertyData studyEntryPropertyData);

	Optional<StudyEntryPropertyData> getStudyEntryPropertyData(int studyEntryPropertyId);
}
