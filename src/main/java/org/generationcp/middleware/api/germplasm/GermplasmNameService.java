package org.generationcp.middleware.api.germplasm;

import org.generationcp.middleware.domain.germplasm.GermplasmNameDto;
import org.generationcp.middleware.domain.germplasm.GermplasmNameRequestDto;
import org.generationcp.middleware.pojos.Name;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface GermplasmNameService {

	Name getNameById(Integer nameId);

	void deleteName(Integer nameId);

	void updateName(GermplasmNameRequestDto germplasmNameRequestDto, Integer gid, Integer nameId);

	Integer createName(GermplasmNameRequestDto germplasmNameRequestDto, Integer gid);

	List<GermplasmNameDto> getGermplasmNamesByGids(List<Integer> gids);

	/**
	 * @return a map of common name codes and names for all the gids,
	 * with a special key for preferred name {@link GermplasmNameServiceImpl#COMMON_NAMES_PREFERRED_KEY}.
	 * map of name code to map of gid to name.
	 */
	Map<String, Map<Integer, String>> getGermplasmCommonNamesMap(ArrayList<Integer> gids);

	List<String> getExistingGermplasmPUIs(List<String> germplasmPUIs);

	boolean isNameTypeUsedAsGermplasmName(Integer nameTypeId);

	boolean isLocationUsedInGermplasmName(Integer locationId);

	Map<Integer, String> getPreferredNamesByGIDs(List<Integer> gids);

	boolean isNameTypeUsedInStudies(Integer nameTypeId);

	boolean isNameTypeUsedInGermplasmList(Integer nameTypeId);
}
