package org.generationcp.middleware.api.nametype;

import java.util.List;

public interface GermplasmNameTypeService {
	List<GermplasmNameTypeDTO> searchNameTypes(String name);
}
