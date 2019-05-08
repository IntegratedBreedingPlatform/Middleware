
package org.generationcp.middleware.manager;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.SearchRequestService;
import org.generationcp.middleware.pojos.search.SearchRequest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class SearchRequestServiceImpl implements SearchRequestService {

	private HibernateSessionProvider sessionProvider;
	private DaoFactory daoFactory;

	public SearchRequestServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
		this.daoFactory = new DaoFactory(this.sessionProvider);
	}

	public SearchRequestServiceImpl() {
		super();
	}

	@Override
	public SearchRequest saveSearchRequest(final SearchRequest searchRequest) {
		return this.daoFactory.getSearchRequestDAO().saveOrUpdate(searchRequest);
	}

	@Override
	public SearchRequest getSearchRequest(final Integer requestId) {
		return this.daoFactory.getSearchRequestDAO().getById(requestId);
	}
}
