
package org.generationcp.middleware.service.api.study;

import org.generationcp.middleware.domain.study.StudyEntryPropertyBatchUpdateRequest;
import org.generationcp.middleware.domain.study.StudyEntrySearchDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface StudyEntryService {

	List<StudyEntryDto> getStudyEntries(int studyId);

	List<StudyEntryDto> getStudyEntries(int studyId, StudyEntrySearchDto.Filter filter, Pageable pageable);

	Map<Integer, StudyEntryDto> getPlotEntriesMap(int studyBusinessIdentifier, Set<Integer> plotNos);

	long countStudyEntries(int studyId);

	void deleteStudyEntries(int studyId);

	Integer getNextEntryNumber(Integer studyId);

	List<StudyEntryDto> saveStudyEntries(Integer studyId, List<StudyEntryDto> studyEntryDtoList);

	long countStudyGermplasmByEntryTypeIds(int studyId, List<String> systemDefinedEntryTypeIds);

	StudyEntryDto replaceStudyEntry(int studyId, int entryId, int gid, String crossExpansion);

	void updateStudyEntriesProperty(StudyEntryPropertyBatchUpdateRequest studyEntryPropertyBatchUpdateRequest);

	Boolean hasUnassignedEntries(int studyId);
}
