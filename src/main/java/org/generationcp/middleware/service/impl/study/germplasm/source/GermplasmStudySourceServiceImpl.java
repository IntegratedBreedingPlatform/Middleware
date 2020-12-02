package org.generationcp.middleware.service.impl.study.germplasm.source;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.GermplasmStudySource;
import org.generationcp.middleware.service.api.study.germplasm.source.GermplasmStudySourceDto;
import org.generationcp.middleware.service.api.study.germplasm.source.GermplasmStudySourceInput;
import org.generationcp.middleware.service.api.study.germplasm.source.GermplasmStudySourceSearchRequest;
import org.generationcp.middleware.service.api.study.germplasm.source.GermplasmStudySourceService;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class GermplasmStudySourceServiceImpl implements GermplasmStudySourceService {

	private final DaoFactory daoFactory;

	public GermplasmStudySourceServiceImpl(final HibernateSessionProvider hibernateSessionProvider) {
		this.daoFactory = new DaoFactory(hibernateSessionProvider);
	}

	@Override
	public List<GermplasmStudySourceDto> getGermplasmStudySources(
		final GermplasmStudySourceSearchRequest germplasmStudySourceSearchRequest, final Pageable pageable) {
		return this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(germplasmStudySourceSearchRequest, pageable);
	}

	@Override
	public long countGermplasmStudySources(final GermplasmStudySourceSearchRequest germplasmStudySourceSearchRequest) {
		return this.daoFactory.getGermplasmStudySourceDAO().countGermplasmStudySourceList(germplasmStudySourceSearchRequest);
	}

	@Override
	public long countFilteredGermplasmStudySources(final GermplasmStudySourceSearchRequest germplasmStudySourceSearchRequest) {
		return this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(germplasmStudySourceSearchRequest);
	}

	@Override
	public void saveGermplasmStudySources(final List<GermplasmStudySourceInput> germplasmStudySourceInputList) {
		for (final GermplasmStudySourceInput input : germplasmStudySourceInputList) {
			this.daoFactory.getGermplasmStudySourceDAO().save(new GermplasmStudySource(input));
		}
	}
}