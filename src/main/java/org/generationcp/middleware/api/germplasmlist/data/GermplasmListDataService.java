package org.generationcp.middleware.api.germplasmlist.data;

import org.generationcp.middleware.api.germplasmlist.GermplasmListColumnDTO;
import org.generationcp.middleware.api.germplasmlist.GermplasmListMeasurementVariableDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GermplasmListDataService {

	List<GermplasmListDataSearchResponse> searchGermplasmListData(Integer listId, GermplasmListDataSearchRequest request, Pageable pageable);

	long countSearchGermplasmListData(Integer listId, GermplasmListDataSearchRequest request);

	List<GermplasmListColumnDTO> getGermplasmListColumns(Integer listId, final String programUUID);

	List<GermplasmListMeasurementVariableDTO> getGermplasmListDataTableHeader(Integer listId, final String programUUID);

	void saveGermplasmListDataView(final Integer listId, final List<GermplasmListDataUpdateViewDTO> view);

}
