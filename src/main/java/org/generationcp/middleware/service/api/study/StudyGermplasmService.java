
package org.generationcp.middleware.service.api.study;

import org.generationcp.middleware.pojos.dms.StockModel;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface StudyGermplasmService {

	List<StudyGermplasmDto> getGermplasm(final int studyBusinessIdentifier);

	List<StudyGermplasmDto> getGermplasmFromPlots(final int studyBusinessIdentifier, Set<Integer> plotNos);

	long countStudyGermplasm(int studyId);

	void deleteStudyGermplasm(int studyId);

	void saveStudyGermplasm(List<StockModel> stockModelList);

	long countStudyGermplasmByEntryTypeIds(int studyId, List<String> systemDefinedEntryTypeIds);

	Map<Integer, String> getInventoryStockIdMap(List<StudyGermplasmDto> studyGermplasmDtoList);

}
