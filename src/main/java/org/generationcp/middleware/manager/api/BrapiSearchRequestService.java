package org.generationcp.middleware.manager.api;

import org.generationcp.middleware.pojos.search.BrapiSearchRequest;

public interface BrapiSearchRequestService {

	BrapiSearchRequest saveSearchRequest(BrapiSearchRequest brapiSearchRequest);

	BrapiSearchRequest getSearchRequest(Integer requestId);

}
