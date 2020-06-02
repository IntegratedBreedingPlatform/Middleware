
package org.generationcp.middleware.service.api.study;

import org.generationcp.middleware.pojos.dms.StockModel;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface StudyGermplasmListService {

	List<StudyGermplasmDto> getGermplasmList(final int studyBusinessIdentifier);

	List<StudyGermplasmDto> getGermplasmListFromPlots(final int studyBusinessIdentifier, Set<Integer> plotNos);

	long countStudyGermplasmList(int studyId);

	void deleteStudyGermplasmList(int studyId);

	void saveStudyGermplasmList(List<StockModel> stockModelList);

	long countStudyGermplasmByEntryTypeIds(int studyId, List<String> systemDefinedEntryTypeIds);

	Map<Integer, String> getInventoryStockIdMap(List<StudyGermplasmDto> studyGermplasmDtoList);

}
