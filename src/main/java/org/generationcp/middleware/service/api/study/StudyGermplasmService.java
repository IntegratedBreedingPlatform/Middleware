
package org.generationcp.middleware.service.api.study;

import org.generationcp.middleware.domain.study.StudyEntrySearchDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

//TODO rename to StudyEntryService
public interface StudyGermplasmService {

	@Deprecated
		// TODO: This method will be replaced with getStudyEntries. This method is only used in displaying Germplasm tab in Fieldbook. Delete this once
		// Germplasm Table is already refactored.
	List<StudyGermplasmDto> getGermplasm(int studyBusinessIdentifier);

	List<StudyEntryDto> getStudyEntries(int studyI);

	List<StudyEntryDto> getStudyEntries(int studyId, StudyEntrySearchDto.Filter filter, Pageable pageable);

	Map<Integer, StudyEntryDto> getPlotEntriesMap(int studyBusinessIdentifier, Set<Integer> plotNos);

	long countStudyEntries(int studyId);

	void deleteStudyEntries(int studyId);

	List<StudyEntryDto> saveStudyEntries(Integer studyId, List<StudyEntryDto> studyEntryDtoList);

	long countStudyGermplasmByEntryTypeIds(int studyId, List<String> systemDefinedEntryTypeIds);

	StudyEntryDto replaceStudyEntry(int studyId, int entryId, int gid, String crossExpansion);

	void updateStudyEntryProperty(int studyId, StudyEntryPropertyData studyEntryPropertyData);

	Optional<StudyEntryPropertyData> getStudyEntryPropertyData(int studyEntryPropertyId);
}
