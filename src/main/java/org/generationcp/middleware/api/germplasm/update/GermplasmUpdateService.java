package org.generationcp.middleware.api.germplasm.update;

import org.generationcp.middleware.domain.germplasm.GermplasmUpdateDTO;
import org.generationcp.middleware.exceptions.GermplasmUpdateConflictException;

import java.util.List;

public interface GermplasmUpdateService {

	void saveGermplasmUpdates(List<GermplasmUpdateDTO> germplasmUpdateDTOList) throws GermplasmUpdateConflictException;

}
