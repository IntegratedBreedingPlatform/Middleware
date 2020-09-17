package org.generationcp.middleware.api.germplasm.search;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GermplasmSearchService {

	List<GermplasmSearchResponse> searchGermplasm(GermplasmSearchRequest germplasmSearchRequest, Pageable pageable,
		final String programUUID);
}
