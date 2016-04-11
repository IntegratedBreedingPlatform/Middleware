package org.generationcp.middleware.service.api;

import java.util.List;
import java.util.Set;

import org.generationcp.middleware.pojos.UserDefinedField;

public interface GermplasmNamingReferenceDataResolver {

	UserDefinedField resolveNameType(int level);

	List<String> getCategoryValues(String variableName, String programUUID);

	Set<GermplasmType> getGermplasmTypes();
}
