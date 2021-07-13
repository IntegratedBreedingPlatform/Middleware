package org.generationcp.middleware.domain.inventory.common;

import org.generationcp.middleware.domain.inventory.manager.LotGeneratorInputDto;

public class LotGeneratorBatchRequestDto {

	private LotGeneratorInputDto lotGeneratorInput;
	/**
	 * searchRequestId for {@link org.generationcp.middleware.domain.search_request.GidSearchDto}
	 * or list of gids
	 */
	private SearchCompositeDto<Integer, Integer> searchComposite;

	public LotGeneratorInputDto getLotGeneratorInput() {
		return this.lotGeneratorInput;
	}

	public void setLotGeneratorInput(final LotGeneratorInputDto lotGeneratorInput) {
		this.lotGeneratorInput = lotGeneratorInput;
	}

	public SearchCompositeDto<Integer, Integer> getSearchComposite() {
		return this.searchComposite;
	}

	public void setSearchComposite(
		final SearchCompositeDto<Integer, Integer> searchComposite) {
		this.searchComposite = searchComposite;
	}
}
