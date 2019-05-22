package org.generationcp.middleware.manager.api;

import org.generationcp.middleware.domain.search_request.SearchRequestDto;

public interface SearchRequestService {

	Integer saveSearchRequest(SearchRequestDto searchRequestDto, Class<? extends SearchRequestDto> searchRequestDtoClass);

	public SearchRequestDto getSearchRequest(Integer requestId, Class<? extends SearchRequestDto> searchRequestDtoClass);
}