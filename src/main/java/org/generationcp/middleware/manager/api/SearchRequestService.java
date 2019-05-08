package org.generationcp.middleware.manager.api;

import org.generationcp.middleware.dao.germplasm.GermplasmSearchRequestDTO;
import org.generationcp.middleware.domain.search_request.GermplasmSearchRequestDto;
import org.generationcp.middleware.domain.search_request.SearchRequestDto;
import org.generationcp.middleware.pojos.search.SearchRequest;

public interface SearchRequestService {

	SearchRequest saveSearchRequest(GermplasmSearchRequestDto brapiSearchRequest, final Class type);

	SearchRequest getSearchRequest(Integer requestId);

}
