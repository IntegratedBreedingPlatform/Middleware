package org.generationcp.middleware.service.api.germplasm;

import org.generationcp.middleware.domain.germplasm.GermplasmUpdateDTO;

import java.util.List;

public interface GermplasmService {

	void importGermplasmUpdates(List<GermplasmUpdateDTO> germplasmUpdateDTOList);

}
