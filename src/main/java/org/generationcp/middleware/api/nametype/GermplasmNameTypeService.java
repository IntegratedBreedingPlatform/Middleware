package org.generationcp.middleware.api.nametype;

import java.util.List;
import java.util.Optional;

public interface GermplasmNameTypeService {

	List<GermplasmNameTypeDTO> searchNameTypes(String name);

	Optional<GermplasmNameTypeDTO> getNameTypeByCode(final String code);
}
