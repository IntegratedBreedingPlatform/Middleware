package org.generationcp.middleware.api.germplasm;

import org.generationcp.middleware.domain.germplasm.GermplasmImportRequestDto;
import org.generationcp.middleware.pojos.Germplasm;

import java.util.List;
import java.util.Map;

public interface GermplasmService {

	List<Germplasm> getGermplasmByGUIDs(List<String> guids);

	Map<Integer, Integer> importGermplasm(Integer userId, String cropName, GermplasmImportRequestDto germplasmImportRequestDto);

}
