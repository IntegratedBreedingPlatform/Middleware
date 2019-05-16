
package org.generationcp.middleware.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.generationcp.middleware.domain.search_request.SearchRequestDto;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.SearchRequestService;
import org.generationcp.middleware.pojos.search.SearchRequest;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("FieldMayBeFinal")
@Transactional
public class SearchRequestServiceImpl implements SearchRequestService {

	private final ObjectMapper jacksonMapper;
	private final HibernateSessionProvider sessionProvider;
	private DaoFactory daoFactory;

	public SearchRequestServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
		this.daoFactory = new DaoFactory(this.sessionProvider);
		this.jacksonMapper = new ObjectMapper();

	}

	@Override
	public SearchRequest saveSearchRequest(final SearchRequestDto searchRequestDto,  final Class<? extends SearchRequestDto> searchRequestDtoClass) {
		try {
			final SearchRequest searchRequest = new SearchRequest();
			searchRequest.setParameters(this.jacksonMapper.writeValueAsString(searchRequestDtoClass.cast(searchRequestDto)));
			return this.daoFactory.getSearchRequestDAO().save(searchRequest);
		} catch (final Exception e) {
			throw new MiddlewareException("Error saving search request", e);
		}
	}


	@Override
	public SearchRequestDto getSearchRequest(
		final Integer requestId,
		final Class<? extends SearchRequestDto> searchRequestDtoClass) {
		try {
			final SearchRequest searchRequest = this.daoFactory.getSearchRequestDAO().getById(requestId);
			final SearchRequestDto searchRequestDto = this.jacksonMapper.readValue(searchRequest.getParameters(), searchRequestDtoClass);

			return searchRequestDto;
		} catch (final Exception e) {
			throw new MiddlewareException("Error getting search request", e);
		}

	}
}
