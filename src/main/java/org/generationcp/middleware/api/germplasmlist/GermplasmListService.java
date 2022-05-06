package org.generationcp.middleware.api.germplasmlist;

import org.generationcp.middleware.api.germplasm.search.GermplasmSearchRequest;
import org.generationcp.middleware.api.germplasmlist.data.GermplasmListDataSearchRequest;
import org.generationcp.middleware.api.germplasmlist.search.GermplasmListSearchRequest;
import org.generationcp.middleware.api.germplasmlist.search.GermplasmListSearchResponse;
import org.generationcp.middleware.domain.inventory.common.SearchCompositeDto;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.pojos.GermplasmList;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GermplasmListService {

	GermplasmListGeneratorDTO create(GermplasmListGeneratorDTO request, Integer loggedInUserId);

	void importUpdates(GermplasmListGeneratorDTO request);

	void addGermplasmEntriesToList(Integer germplasmListId, SearchCompositeDto<GermplasmSearchRequest, Integer> searchComposite,
		final String programUUID);

	Optional<GermplasmList> getGermplasmListById(Integer id);

	Optional<GermplasmList> getGermplasmListByIdAndProgramUUID(Integer id, String programUUID);

	Optional<GermplasmList> getGermplasmListByParentAndName(String germplasmListName, Integer parentId, String programUUID);

	long countMyLists(String programUUID, Integer userId);

	List<MyListsDTO> getMyLists(String programUUID, Pageable pageable, Integer userId);

	Integer createGermplasmListFolder(Integer userId, String folderName, Integer parentId, String programUUID);

	Integer updateGermplasmListFolder(String folderName, Integer folderId);

	GermplasmList moveGermplasmListFolder(Integer germplasmListId, Integer newParentFolderId, String programUUID);

	void deleteGermplasmListFolder(Integer folderId);

	List<GermplasmListDto> getGermplasmLists(Integer gid);

	void performGermplasmListEntriesDeletion(List<Integer> gids);

	void deleteProgramGermplasmLists(String programUUID);

	long countGermplasmLists(List<Integer> gids);

	List<GermplasmListSearchResponse> searchGermplasmList(GermplasmListSearchRequest request, Pageable pageable, String programUUID);

	long countSearchGermplasmList(GermplasmListSearchRequest request, String programUUID);

	/**
	 * Lock the list if it's unlocked and vice versa.
	 *
	 * @param listId
	 * @return {@link boolean} true if it's locked or false it's unlocked
	 */
	boolean toggleGermplasmListStatus(Integer listId);

	List<Integer> getListOntologyVariables(Integer listId, List<Integer> types);

	void addVariableToList(Integer listId, GermplasmListVariableRequestDto germplasmListVariableRequestDto);

	void removeListVariables(Integer listId, Set<Integer> variableIds);

	List<Variable> getGermplasmListVariables(String programUUID, Integer listId, Integer variableTypeId);

	Optional<GermplasmListDataDto> getGermplasmListData(Integer listDataId);

	Optional<GermplasmListObservationDto> getListDataObservation(Integer observationId);

	Integer saveListDataObservation(Integer listId, GermplasmListObservationRequestDto observationRequestDto);

	void updateListDataObservation(Integer observationId, String value, Integer cValueId);

	void deleteListDataObservation(Integer observationId);

	long countObservationsByVariables(Integer listId, List<Integer> variableIds);

	void deleteGermplasmList(Integer listId);

	void addGermplasmListEntriesToAnotherList(Integer destinationListId, Integer sourceListId, String programUUID,
		SearchCompositeDto<GermplasmListDataSearchRequest, Integer> searchComposite);

	GermplasmListDto cloneGermplasmList(Integer listId, GermplasmListDto germplasmListDto,
		Integer loggedInUserId);

	void removeGermplasmEntriesFromList(Integer germplasmListId,
		SearchCompositeDto<GermplasmListDataSearchRequest, Integer> searchComposite);

	void editListMetadata(GermplasmListDto germplasmListDto);

	long countEntryDetailsNamesAndAttributesAdded(Integer listId);
}
