package org.generationcp.middleware.api.attribute;

import org.generationcp.middleware.domain.germplasm.AttributeDTO;

import java.util.List;

public interface AttributeService {
	List<AttributeDTO> searchAttributes(String name);
}
