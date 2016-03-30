package org.generationcp.middleware.service.api;

import java.util.List;
import java.util.Set;

public interface GermplasmCodingService {

	/**
	 * Adds given {@code codedName} of type defined by given {@code nameType} to the germplasm identified by given {@code gid}.
	 */
	void applyCodedName(Integer gid, String codedName, Integer nameType);

	int getNextSequence(String prefix);

	List<String> getProgramIdentifiers(Integer levelCode);

	Set<GermplasmType> getGermplasmTypes();
}
