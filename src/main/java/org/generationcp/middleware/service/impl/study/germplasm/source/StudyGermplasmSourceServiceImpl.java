package org.generationcp.middleware.service.impl.study.germplasm.source;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.GermplasmStudySource;
import org.generationcp.middleware.service.api.study.germplasm.source.StudyGermplasmSourceDto;
import org.generationcp.middleware.service.api.study.germplasm.source.StudyGermplasmSourceInput;
import org.generationcp.middleware.service.api.study.germplasm.source.StudyGermplasmSourceRequest;
import org.generationcp.middleware.service.api.study.germplasm.source.StudyGermplasmSourceService;

import java.util.List;

public class StudyGermplasmSourceServiceImpl implements StudyGermplasmSourceService {

	private final DaoFactory daoFactory;

	public StudyGermplasmSourceServiceImpl(final HibernateSessionProvider hibernateSessionProvider) {
		this.daoFactory = new DaoFactory(hibernateSessionProvider);
	}

	@Override
	public List<StudyGermplasmSourceDto> getStudyGermplasmSourceList(final StudyGermplasmSourceRequest studyGermplasmSourceRequest) {
		return this.daoFactory.getGermplasmStudySourceDAO().getGermplasmStudySourceList(studyGermplasmSourceRequest);
	}

	@Override
	public long countStudyGermplasmSourceList(final StudyGermplasmSourceRequest studyGermplasmSourceRequest) {
		return this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(studyGermplasmSourceRequest);
	}

	@Override
	public long countFilteredStudyGermplasmSourceList(final StudyGermplasmSourceRequest studyGermplasmSourceRequest) {
		return this.daoFactory.getGermplasmStudySourceDAO().countFilteredGermplasmStudySourceList(studyGermplasmSourceRequest);
	}

	@Override
	public void saveStudyGermplasmSource(List<StudyGermplasmSourceInput> studyGermplasmSourceInputList) {
		for (final StudyGermplasmSourceInput input : studyGermplasmSourceInputList) {
			this.daoFactory.getGermplasmStudySourceDAO().save(new GermplasmStudySource(input));
		}
	}
}
