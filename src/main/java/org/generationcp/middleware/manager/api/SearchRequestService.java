package org.generationcp.middleware.manager.api;

import org.generationcp.middleware.domain.search_request.GermplasmSearchRequestDto;
import org.generationcp.middleware.domain.search_request.SearchRequestDto;
import org.generationcp.middleware.domain.search_request.SearchRequestType;
import org.generationcp.middleware.pojos.search.SearchRequest;

public interface SearchRequestService {

	SearchRequest saveSearchRequest(GermplasmSearchRequestDto searchRequestDto, final SearchRequestType type);

	SearchRequestDto getSearchRequest(
		Integer requestId,
		final Class<GermplasmSearchRequestDto> germplasmSearchRequestDtoClass);

}
