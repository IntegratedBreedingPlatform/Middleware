package org.generationcp.middleware.api.germplasmlist;

import org.generationcp.middleware.api.germplasm.search.GermplasmSearchRequest;
import org.generationcp.middleware.domain.inventory.common.SearchCompositeDto;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface GermplasmListService {

	GermplasmListGeneratorDTO create(GermplasmListGeneratorDTO request, int status, String programUUID,
		WorkbenchUser loggedInUser);

	/**
	 * Inserts a list of multiple {@code GermplasmListData} objects into the database.
	 *
	 * @param data - A list of {@code GermplasmListData} objects to be persisted to the database. {@code GermplasmListData}
	 *                           objects must be valid.
	 * @return Returns the ids of the {@code GermplasmListData} records inserted in the database.
	 */
	List<GermplasmListData> addGermplasmListData(List<GermplasmListData> data);

	void addGermplasmEntriesToList(Integer germplasmListId, SearchCompositeDto<GermplasmSearchRequest, Integer> searchComposite,
		final String programUUID);

	Optional<GermplasmList> getGermplasmListById(Integer id);

	Optional<GermplasmList> getGermplasmListByIdAndProgramUUID(Integer id, String programUUID);

	Optional<GermplasmList> getGermplasmListByParentAndName(String germplasmListName, Integer parentId, String programUUID);

	long countMyLists(String programUUID, Integer userId);

	List<MyListsDTO> getMyLists(String programUUID, Pageable pageable, Integer userId);

	Integer createGermplasmListFolder(Integer userId, String folderName, Integer parentId, String programUUID);

	Integer updateGermplasmListFolder(Integer userId, String folderName, Integer folderId, String programUUID);

	Integer moveGermplasmListFolder(Integer germplasmListId, Integer newParentFolderId, String programUUID);

	void deleteGermplasmListFolder(Integer folderId);

	List<GermplasmListDto> getGermplasmLists(Integer gid);

	void performGermplasmListEntriesDeletion(List<Integer> gids);

	void deleteProgramGermplasmLists(String programUUID);

	long countGermplasmLists(List<Integer> gids);

}
