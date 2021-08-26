package org.generationcp.middleware.api.germplasm.search;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class GermplasmSearchServiceImpl implements GermplasmSearchService {

	private final DaoFactory daoFactory;

	@Value("${export.max.total.results}")
	public int maxTotalResults;

	public GermplasmSearchServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<GermplasmSearchResponse> searchGermplasm(final GermplasmSearchRequest germplasmSearchRequest, final Pageable pageable,
		final String programUUID) {
		return this.daoFactory.getGermplasmSearchDAO().searchGermplasm(germplasmSearchRequest, pageable, programUUID, null);
	}

	@Override
	public List<GermplasmSearchResponse> searchGermplasmApplyExportResultsLimit(final GermplasmSearchRequest germplasmSearchRequest, final Pageable pageable,
		final String programUUID) {
		return this.daoFactory.getGermplasmSearchDAO().searchGermplasm(germplasmSearchRequest, pageable, programUUID, this.maxTotalResults);
	}

	@Override
	public long countSearchGermplasm(final GermplasmSearchRequest germplasmSearchRequest, final String programUUID) {
		return this.daoFactory.getGermplasmSearchDAO().countSearchGermplasm(germplasmSearchRequest, programUUID);
	}

}
