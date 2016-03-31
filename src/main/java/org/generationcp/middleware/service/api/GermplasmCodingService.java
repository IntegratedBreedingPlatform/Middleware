
package org.generationcp.middleware.service.api;

import java.util.List;
import java.util.Set;

public interface GermplasmCodingService {

	void applyGroupName(Integer gid, String groupName, NameTypeResolver nameTypeResolver, Integer userId, Integer locationId);

	int getNextSequence(String prefix);

	List<String> getProgramIdentifiers(Integer levelCode);

	Set<GermplasmType> getGermplasmTypes();
}
