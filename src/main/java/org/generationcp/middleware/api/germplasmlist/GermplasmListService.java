package org.generationcp.middleware.api.germplasmlist;

import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;

import java.util.Optional;

public interface GermplasmListService {

	GermplasmListGeneratorDTO create(GermplasmListGeneratorDTO request, int status, String programUUID,
		WorkbenchUser loggedInUser);

	Optional<GermplasmList> getGermplasmListById(Integer id);

	Optional<GermplasmList> getGermplasmListByIdAndProgramUUID(Integer id, String programUUID);

	Optional<GermplasmList> getGermplasmListByParentAndName(String germplasmListName, Integer parentId, String programUUID);

	Integer createGermplasmListFolder(Integer userId, String folderName, Integer parentId, String programUUID);

	Integer updateGermplasmListFolder(Integer userId, String folderName, Integer folderId, String programUUID);

	Integer moveGermplasmListFolder(Integer germplasmListId, Integer newParentFolderId, String programUUID);

	void deleteGermplasmListFolder(Integer folderId);

}
