package org.generationcp.middleware.service.api.study.germplasm.source;

import java.util.List;

public interface GermplasmStudySourceService {

	List<GermplasmStudySourceDto> getGermplasmStudySources(GermplasmStudySourceSearchRequest searchParameters);

	long countGermplasmStudySources(GermplasmStudySourceSearchRequest searchParameters);

	long countFilteredGermplasmStudySources(GermplasmStudySourceSearchRequest germplasmStudySourceSearchRequest);

	void saveGermplasmStudySource(List<GermplasmStudySourceInput> germplasmStudySourceInputList);

}
