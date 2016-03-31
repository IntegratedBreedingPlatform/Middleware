package org.generationcp.middleware.service.api;

import org.generationcp.middleware.pojos.UserDefinedField;

public interface GermplasmNameTypeResolver {

	UserDefinedField resolve(int level);
}
