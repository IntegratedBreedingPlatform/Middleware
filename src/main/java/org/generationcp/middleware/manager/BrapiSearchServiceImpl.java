
package org.generationcp.middleware.manager;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.BrapiSearchService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class BrapiSearchServiceImpl implements BrapiSearchService {

	private HibernateSessionProvider sessionProvider;
	private DaoFactory daoFactory;

	public BrapiSearchServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
		this.daoFactory = new DaoFactory(this.sessionProvider);
	}

	public BrapiSearchServiceImpl() {
		super();
	}


}
