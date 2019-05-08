package org.generationcp.middleware.manager.api;

import org.generationcp.middleware.pojos.search.SearchRequest;

public interface SearchRequestService {

	SearchRequest saveSearchRequest(GermplasmSearchRequestDto brapiSearchRequest, final Class type);

	SearchRequest getSearchRequest(Integer requestId);

}
