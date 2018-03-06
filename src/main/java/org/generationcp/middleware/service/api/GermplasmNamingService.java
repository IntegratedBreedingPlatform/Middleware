
package org.generationcp.middleware.service.api;

import java.util.Map;
import java.util.Set;

import org.generationcp.middleware.exceptions.InvalidGermplasmNameSettingException;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.germplasm.GermplasmNameSetting;
import org.generationcp.middleware.pojos.workbench.NamingConfiguration;

public interface GermplasmNamingService {

	void addName(Germplasm germplasm, String groupName, UserDefinedField nameType, Integer userId, Integer locationId,
			GermplasmGroupNamingResult result);

	String getNextNameInSequence(GermplasmNameSetting setting) throws InvalidGermplasmNameSettingException;

	GermplasmGroupNamingResult applyGroupName(Integer gid, GermplasmNameSetting setting, UserDefinedField nameType, Integer userId, Integer locationId);
	
	Map<Integer, GermplasmGroupNamingResult> applyGroupNames(Set<Integer> gids, GermplasmNameSetting setting, UserDefinedField nameType, Integer userId, Integer locationId);

}
