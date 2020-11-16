package org.generationcp.middleware.api.germplasm;

import org.generationcp.middleware.domain.germplasm.importation.GermplasmImportRequestDto;
import org.generationcp.middleware.domain.germplasm.importation.GermplasmImportResponseDto;
import org.generationcp.middleware.pojos.Germplasm;

import java.util.List;
import java.util.Map;

public interface GermplasmService {

	List<Germplasm> getGermplasmByGUIDs(List<String> guids);

	Map<Integer, GermplasmImportResponseDto> importGermplasm(Integer userId, String cropName,
		List<GermplasmImportRequestDto> germplasmImportRequestDto);

}
