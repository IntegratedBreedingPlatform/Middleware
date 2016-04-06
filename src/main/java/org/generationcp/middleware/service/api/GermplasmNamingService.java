
package org.generationcp.middleware.service.api;

import java.util.List;
import java.util.Set;

import org.generationcp.middleware.pojos.UserDefinedField;

public interface GermplasmNamingService {

	GermplasmGroupNamingResult applyGroupName(Integer gid, String groupName, UserDefinedField nameType, Integer userId, Integer locationId);

	List<String> getProgramIdentifiers(Integer levelCode, String programUUID);

	Set<GermplasmType> getGermplasmTypes();
}
