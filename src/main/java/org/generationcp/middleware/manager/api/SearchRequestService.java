package org.generationcp.middleware.manager.api;

import org.generationcp.middleware.domain.search_request.SearchRequestDto;
import org.generationcp.middleware.pojos.search.SearchRequest;

public interface SearchRequestService {

	SearchRequest saveSearchRequest(SearchRequestDto searchRequestDto, Class<? extends SearchRequestDto> searchRequestDtoClass);

	SearchRequestDto getSearchRequest(
		Integer requestId,
		Class<? extends SearchRequestDto> germplasmSearchRequestDtoClass);

}
