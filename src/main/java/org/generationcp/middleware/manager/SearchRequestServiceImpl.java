
package org.generationcp.middleware.manager;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.generationcp.middleware.domain.search_request.GermplasmSearchRequestDto;
import org.generationcp.middleware.domain.search_request.SearchRequestDto;
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
	public SearchRequest saveSearchRequest(final GermplasmSearchRequestDto searchRequest, final Class type) {
		try {
			final String des = this.jacksonMapper.writeValueAsString(searchRequest);
			final SearchRequestDto s = (SearchRequestDto) this.jacksonMapper.readValue(des, type);
			System.out.printf("");
		} catch (final Exception e ) {

		}
		return null;
//		return this.daoFactory.getSearchRequestDAO().save(searchRequest);
	}

	@Override
	public SearchRequest getSearchRequest(final Integer requestId) {
		return this.daoFactory.getSearchRequestDAO().getById(requestId);
	}
}
