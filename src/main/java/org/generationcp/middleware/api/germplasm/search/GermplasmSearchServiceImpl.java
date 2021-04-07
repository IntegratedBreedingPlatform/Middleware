package org.generationcp.middleware.api.germplasm.search;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Transactional
@Service
public class GermplasmSearchServiceImpl implements GermplasmSearchService {

	private final DaoFactory daoFactory;

	public GermplasmSearchServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<GermplasmSearchResponse> searchGermplasm(final GermplasmSearchRequest germplasmSearchRequest, final Pageable pageable,
		final String programUUID) {
		return this.daoFactory.getGermplasmSearchDAO().searchGermplasm(germplasmSearchRequest, pageable, programUUID);
	}

	@Override
	public long countSearchGermplasm(final GermplasmSearchRequest germplasmSearchRequest, final String programUUID) {
		return this.daoFactory.getGermplasmSearchDAO().countSearchGermplasm(germplasmSearchRequest, programUUID);
	}

	@Override
	public List<UserDefinedField> getGermplasmAttributeTypes(final GermplasmSearchRequest germplasmSearchRequest) {
		return this.daoFactory.getGermplasmSearchDAO().getGermplasmAttributeTypes(germplasmSearchRequest);
	}

	@Override
	public List<UserDefinedField> getGermplasmNameTypes(final GermplasmSearchRequest germplasmSearchRequest) {
		return this.daoFactory.getGermplasmSearchDAO().getGermplasmNameTypes(germplasmSearchRequest);
	}

	@Override
	public Map<Integer, Map<Integer, String>> getGermplasmAttributeValues(final GermplasmSearchRequest germplasmSearchRequest) {
		return this.daoFactory.getGermplasmSearchDAO().getGermplasmAttributeValues(germplasmSearchRequest);
	}

	@Override
	public Map<Integer, Map<Integer, String>> getGermplasmNameValues(final GermplasmSearchRequest germplasmSearchRequest) {
		return this.daoFactory.getGermplasmSearchDAO().getGermplasmNameValues(germplasmSearchRequest);
	}
}
