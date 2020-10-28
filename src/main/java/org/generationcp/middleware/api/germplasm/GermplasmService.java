package org.generationcp.middleware.api.germplasm;

import org.generationcp.middleware.domain.germplasm.GermplasmImportRequestDto;

import java.util.Map;

public interface GermplasmService {

	Map<Integer, Integer> importGermplasmSet(Integer userId, String cropName, GermplasmImportRequestDto germplasmImportRequestDto);

}
