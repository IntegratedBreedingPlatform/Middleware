package org.generationcp.middleware.service.api.study.germplasm.source;

import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface GermplasmStudySourceService {

	List<GermplasmStudySourceDto> getGermplasmStudySources(GermplasmStudySourceSearchRequest searchParameters, PageRequest pageRequest);

	long countGermplasmStudySources(GermplasmStudySourceSearchRequest searchParameters);

	long countFilteredGermplasmStudySources(GermplasmStudySourceSearchRequest germplasmStudySourceSearchRequest);

	void saveGermplasmStudySources(List<GermplasmStudySourceInput> germplasmStudySourceInputList);

}
