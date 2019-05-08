
package org.generationcp.middleware.manager;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.BrapiSearchRequestService;
import org.generationcp.middleware.pojos.search.BrapiSearchRequest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class BrapiSearchRequestServiceImpl implements BrapiSearchRequestService {

	private HibernateSessionProvider sessionProvider;
	private DaoFactory daoFactory;

	public BrapiSearchRequestServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
		this.daoFactory = new DaoFactory(this.sessionProvider);
	}

	public BrapiSearchRequestServiceImpl() {
		super();
	}

	@Override
	public BrapiSearchRequest saveSearchRequest(final BrapiSearchRequest brapiSearchRequest) {
		return this.daoFactory.getBrapiSearchDAO().saveOrUpdate(brapiSearchRequest);
	}

	@Override
	public BrapiSearchRequest getSearchRequest(final Integer requestId) {
		return this.daoFactory.getBrapiSearchDAO().getById(requestId);
	}
}
