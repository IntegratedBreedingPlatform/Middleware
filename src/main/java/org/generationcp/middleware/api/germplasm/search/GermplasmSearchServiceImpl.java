package org.generationcp.middleware.api.germplasm.search;

import org.generationcp.middleware.hibernate.HibernateSessionPerRequestProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class GermplasmSearchServiceImpl implements GermplasmSearchService {

	private final DaoFactory daoFactory;

	public GermplasmSearchServiceImpl(final HibernateSessionPerRequestProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<GermplasmSearchResponse> searchGermplasm(final GermplasmSearchRequest germplasmSearchRequest, final Pageable pageable) {
		return this.daoFactory.getGermplasmSearchDAO().searchGermplasm(germplasmSearchRequest, pageable);
	}
}
