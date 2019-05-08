package org.generationcp.middleware.manager.api;

import org.generationcp.middleware.pojos.search.SearchRequest;

public interface SearchRequestService {

	SearchRequest saveSearchRequest(SearchRequest brapiSearchRequest);

	SearchRequest getSearchRequest(Integer requestId);

}
