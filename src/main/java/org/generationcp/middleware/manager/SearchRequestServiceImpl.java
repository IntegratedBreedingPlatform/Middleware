
package org.generationcp.middleware.manager;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.generationcp.middleware.domain.search_request.GermplasmSearchRequestDto;
import org.generationcp.middleware.domain.search_request.SearchRequestDto;
import org.generationcp.middleware.domain.search_request.SearchRequestType;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.SearchRequestService;
import org.generationcp.middleware.pojos.search.SearchRequest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class SearchRequestServiceImpl implements SearchRequestService {

	private final ObjectMapper jacksonMapper;
	private HibernateSessionProvider sessionProvider;
	private DaoFactory daoFactory;

	public SearchRequestServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
		this.daoFactory = new DaoFactory(this.sessionProvider);
		this.jacksonMapper = new ObjectMapper();
		this.jacksonMapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
	}

	public SearchRequestServiceImpl() {
		super();
		this.jacksonMapper = new ObjectMapper();
		this.jacksonMapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
	}

	@Override
	public SearchRequest saveSearchRequest(final GermplasmSearchRequestDto searchRequestDto, final SearchRequestType type) {
		try {
			final SearchRequest searchRequest = new SearchRequest();
			searchRequest.setParameters(this.jacksonMapper.writeValueAsString(searchRequestDto));
			searchRequest.setRequestType(type.getName());
			return this.daoFactory.getSearchRequestDAO().save(searchRequest);
		} catch (final Exception e) {
			throw new MiddlewareException("Error saving search request", e);
		}
	}

	@Override
	public SearchRequestDto getSearchRequest(
		final Integer requestId,
		final Class<GermplasmSearchRequestDto> searchRequestDtoClass) {
		try {
			final SearchRequest searchRequest = this.daoFactory.getSearchRequestDAO().getById(requestId);
			final SearchRequestDto searchRequestDto = this.jacksonMapper.readValue(searchRequest.getParameters(), searchRequestDtoClass);

			return searchRequestDto;
		} catch (final Exception e) {
			throw new MiddlewareException("Error getting search request", e);
		}

	}
}
