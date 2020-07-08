package org.generationcp.middleware.service.api.study.germplasm.source;

import java.util.List;

public interface GermplasmStudySourceService {

	List<GermplasmStudySourceDto> getGermplasmStudySourceList(final GermplasmStudySourceRequest searchParameters);

	long countGermplasmStudySourceList(final GermplasmStudySourceRequest searchParameters);

	long countFilteredGermplasmStudySourceList(GermplasmStudySourceRequest germplasmStudySourceRequest);

	void saveGermplasmStudySource(List<GermplasmStudySourceInput> germplasmStudySourceInputList);




}
