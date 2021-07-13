package org.generationcp.middleware.domain.inventory.common;

import org.generationcp.middleware.domain.inventory.manager.LotGeneratorInputDto;

public class LotGeneratorBatchRequestDto {

	private LotGeneratorInputDto lotGeneratorInput;
	/**
	 * searchRequestId for {@link org.generationcp.middleware.domain.inventory.common.SearchTypeComposeDto}
	 * or list of gids
	 */
	private SearchCompositeDto<SearchTypeComposeDto, Integer> searchComposite;

	public LotGeneratorInputDto getLotGeneratorInput() {
		return this.lotGeneratorInput;
	}

	public void setLotGeneratorInput(final LotGeneratorInputDto lotGeneratorInput) {
		this.lotGeneratorInput = lotGeneratorInput;
	}

	public SearchCompositeDto<SearchTypeComposeDto, Integer> getSearchComposite() {
		return this.searchComposite;
	}

	public void setSearchComposite(
		final SearchCompositeDto<SearchTypeComposeDto, Integer> searchComposite) {
		this.searchComposite = searchComposite;
	}
}
