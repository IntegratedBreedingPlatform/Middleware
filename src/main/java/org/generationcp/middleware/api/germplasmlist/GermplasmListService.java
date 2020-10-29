package org.generationcp.middleware.api.germplasmlist;

import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;

import java.util.Optional;

public interface GermplasmListService {

	GermplasmListGeneratorDTO create(GermplasmListGeneratorDTO request, int status, String programUUID,
		WorkbenchUser loggedInUser);

	Optional<GermplasmList> getGermplasmListById(Integer parentId);

	Optional<GermplasmList> getGermplasmListByParentAndName(String germplasmListName, Integer parentId, String programUUID);

	Integer createGermplasmListFolder(Integer userId, String folderName, Integer parentId, String programUUID);

}
