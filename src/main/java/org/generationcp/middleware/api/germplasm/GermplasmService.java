package org.generationcp.middleware.api.germplasm;

import org.generationcp.middleware.domain.germplasm.GermplasmImportRequestDto;
import org.generationcp.middleware.domain.germplasm.GermplasmImportResponseDto;
import org.generationcp.middleware.domain.germplasm.GermplasmUpdateDTO;
import org.generationcp.middleware.pojos.Germplasm;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface GermplasmService {

	List<Germplasm> getGermplasmByGUIDs(List<String> guids);

	List<Germplasm> getGermplasmByGIDs(List<Integer> gids);

	Map<Integer, GermplasmImportResponseDto> importGermplasm(Integer userId, String cropName,
		List<GermplasmImportRequestDto> germplasmImportRequestDto);

	Set<Integer> importGermplasmUpdates(Integer userId, List<GermplasmUpdateDTO> germplasmUpdateDTOList);

}
