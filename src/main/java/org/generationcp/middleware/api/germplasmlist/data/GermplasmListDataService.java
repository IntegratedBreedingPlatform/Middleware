package org.generationcp.middleware.api.germplasmlist.data;

import org.generationcp.middleware.api.germplasmlist.GermplasmListColumnDTO;
import org.generationcp.middleware.api.germplasmlist.GermplasmListMeasurementVariableDTO;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListDataDetail;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GermplasmListDataService {

	List<GermplasmListDataSearchResponse> searchGermplasmListData(Integer listId, GermplasmListDataSearchRequest request, Pageable pageable);

	long countSearchGermplasmListData(Integer listId, GermplasmListDataSearchRequest request);

	List<GermplasmListColumnDTO> getGermplasmListColumns(Integer listId, String programUUID);

	List<GermplasmListMeasurementVariableDTO> getGermplasmListDataTableHeader(Integer listId, String programUUID);

	void updateGermplasmListDataView(Integer listId, List<GermplasmListDataUpdateViewDTO> view);

	void fillWithCrossExpansion(Integer listId, Integer level);

	List<GermplasmListDataDetail> getGermplasmListDataDetailList(Integer listId);

	void reOrderEntries(Integer listId, List<Integer> selectedEntries, Integer entryNumberPosition);

	long countByListId(Integer listId);

	List<Integer> getGidsByListId(Integer listId);

	List<Integer> getListDataIdsByListId(Integer listId);

	void saveDefaultView(GermplasmList list);

}
