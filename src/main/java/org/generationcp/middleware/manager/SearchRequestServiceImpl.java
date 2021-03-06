package org.generationcp.middleware.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.generationcp.middleware.domain.search_request.SearchRequestDto;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.SearchRequestService;
import org.generationcp.middleware.pojos.search.SearchRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.TimeZone;

@Transactional
public class SearchRequestServiceImpl implements SearchRequestService {

	private ObjectMapper jacksonMapper;
	private final HibernateSessionProvider sessionProvider;
	private DaoFactory daoFactory;

	public SearchRequestServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
		this.daoFactory = new DaoFactory(this.sessionProvider);
		this.jacksonMapper = new ObjectMapper();
		this.jacksonMapper.setTimeZone(TimeZone.getDefault());
	}

	@Override
	public Integer saveSearchRequest(final SearchRequestDto searchRequestDto,  final Class<? extends SearchRequestDto> searchRequestDtoClass) {
		try {
			final SearchRequest searchRequest = new SearchRequest();
			searchRequest.setParameters(this.jacksonMapper.writeValueAsString(searchRequestDtoClass.cast(searchRequestDto)));
			return this.daoFactory.getSearchRequestDAO().save(searchRequest).getRequestId();
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
			throw new MiddlewareException("Error when trying to get the search request. Id is null or does not exist");
		}

	}

	public ObjectMapper getJacksonMapper() {
		return this.jacksonMapper;
	}

	public HibernateSessionProvider getSessionProvider() {
		return this.sessionProvider;
	}

	public void setJacksonMapper(final ObjectMapper jacksonMapper) {
		this.jacksonMapper = jacksonMapper;
	}

	public DaoFactory getDaoFactory() {
		return this.daoFactory;
	}

	public void setDaoFactory(final DaoFactory daoFactory) {
		this.daoFactory = daoFactory;
	}
}
