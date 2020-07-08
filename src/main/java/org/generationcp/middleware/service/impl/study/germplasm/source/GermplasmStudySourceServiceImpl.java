package org.generationcp.middleware.service.impl.study.germplasm.source;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.GermplasmStudySource;
import org.generationcp.middleware.service.api.study.germplasm.source.GermplasmStudySourceDto;
import org.generationcp.middleware.service.api.study.germplasm.source.GermplasmStudySourceInput;
import org.generationcp.middleware.service.api.study.germplasm.source.GermplasmStudySourceRequest;
import org.generationcp.middleware.service.api.study.germplasm.source.GermplasmStudySourceService;

import java.util.List;

public class GermplasmStudySourceServiceImpl implements GermplasmStudySourceService {

	private final DaoFactory daoFactory;

	public GermplasmStudySourceServiceImpl(final HibernateSessionProvider hibernateSessionProvider) {
		this.daoFactory = new DaoFactory(hibernateSessionProvider);
	}

	@Override
	public List<GermplasmStudySourceDto> getGermplasmStudySourceList(final GermplasmStudySourceRequest germplasmStudySourceRequest) {
		return this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(germplasmStudySourceRequest);
	}

	@Override
	public long countGermplasmStudySourceList(final GermplasmStudySourceRequest germplasmStudySourceRequest) {
		return this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(germplasmStudySourceRequest);
	}

	@Override
	public long countFilteredGermplasmStudySourceList(final GermplasmStudySourceRequest germplasmStudySourceRequest) {
		return this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(germplasmStudySourceRequest);
	}

	@Override
	public void saveGermplasmStudySource(List<GermplasmStudySourceInput> germplasmStudySourceInputList) {
		for (final GermplasmStudySourceInput input : germplasmStudySourceInputList) {
			this.daoFactory.getGermplasmStudySourceDAO().save(new GermplasmStudySource(input));
		}
	}
}
