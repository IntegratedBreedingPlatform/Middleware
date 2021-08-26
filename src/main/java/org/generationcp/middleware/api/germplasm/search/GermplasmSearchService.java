package org.generationcp.middleware.api.germplasm.search;

import org.generationcp.middleware.pojos.UserDefinedField;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface GermplasmSearchService {

	List<GermplasmSearchResponse> searchGermplasm(GermplasmSearchRequest germplasmSearchRequest, Pageable pageable,
		final String programUUID);

	long countSearchGermplasm(GermplasmSearchRequest germplasmSearchRequest, String programUUID);

}
