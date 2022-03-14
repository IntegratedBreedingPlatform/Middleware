
package org.generationcp.middleware.service.api.study;

import org.generationcp.middleware.domain.study.StudyEntryPropertyBatchUpdateRequest;
import org.generationcp.middleware.domain.study.StudyEntrySearchDto;
import org.generationcp.middleware.pojos.dms.StockProperty;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface StudyEntryService {

	List<StudyEntryDto> getStudyEntries(int studyId);

	List<StudyEntryDto> getStudyEntries(int studyId, StudyEntrySearchDto.Filter filter, Pageable pageable);

	Map<Integer, StudyEntryDto> getPlotEntriesMap(int studyBusinessIdentifier, Set<Integer> plotNos);

	long countFilteredStudyEntries(int studyId, StudyEntrySearchDto.Filter filter);

	long countStudyEntries(int studyId);

	void deleteStudyEntries(int studyId);

	Integer getNextEntryNumber(Integer studyId);

	void saveStudyEntries(Integer studyId, Integer listId);

	void saveStudyEntries(Integer studyId, List<Integer> gids, final Integer entryTypeId);

	long countStudyGermplasmByEntryTypeIds(int studyId, List<String> systemDefinedEntryTypeIds);

	StudyEntryDto replaceStudyEntry(int studyId, int entryId, int gid, String crossExpansion);

	void replaceStudyEntries(List<Integer> gidsToReplace, Integer replaceWithGid, String crossExpansion);

	void updateStudyEntriesProperty(StudyEntryPropertyBatchUpdateRequest studyEntryPropertyBatchUpdateRequest);

	Boolean hasUnassignedEntries(int studyId);

	Optional<StockProperty> getByStockIdAndTypeId(Integer stockId, Integer typeId);
}
