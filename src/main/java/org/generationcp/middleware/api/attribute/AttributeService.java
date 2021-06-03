package org.generationcp.middleware.api.attribute;

import org.generationcp.middleware.domain.ontology.Variable;

import java.util.List;

public interface AttributeService {
	List<Variable> searchAttributes(String name, String programUUID);
}
