package org.generationcp.middleware.service.api.study.germplasm.source;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GermplasmStudySourceService {

	List<GermplasmStudySourceDto> getGermplasmStudySources(GermplasmStudySourceSearchRequest searchParameters, Pageable pageable);

	long countGermplasmStudySources(GermplasmStudySourceSearchRequest searchParameters);

	long countFilteredGermplasmStudySources(GermplasmStudySourceSearchRequest germplasmStudySourceSearchRequest);

	void saveGermplasmStudySources(List<GermplasmStudySourceInput> germplasmStudySourceInputList);

}
