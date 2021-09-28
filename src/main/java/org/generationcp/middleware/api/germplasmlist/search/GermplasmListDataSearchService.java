package org.generationcp.middleware.api.germplasmlist.search;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GermplasmListDataSearchService {

	List<GermplasmListDataSearchResponse> searchGermplasmListData(Integer listId, GermplasmListDataSearchRequest request, Pageable pageable);

	long countSearchGermplasmListData(Integer listId, GermplasmListDataSearchRequest request);

}
