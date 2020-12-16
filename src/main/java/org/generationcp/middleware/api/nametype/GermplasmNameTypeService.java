package org.generationcp.middleware.api.nametype;

import org.generationcp.middleware.api.attribute.AttributeDTO;

import java.util.List;

public interface GermplasmNameTypeService {
	List<GermplasmNameTypeDTO> searchNameTypes(String name);
}
