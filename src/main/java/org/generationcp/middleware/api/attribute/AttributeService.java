package org.generationcp.middleware.api.attribute;

import java.util.List;

public interface AttributeService {
	List<AttributeDTO> searchAttributes(String name);
}
