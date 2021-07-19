package org.generationcp.middleware.domain.inventory.common;

import org.generationcp.middleware.domain.inventory.manager.LotGeneratorInputDto;

public class LotGeneratorBatchRequestDto {

	private LotGeneratorInputDto lotGeneratorInput;
	/**
	 * searchRequestId for {@link SearchOriginCompositeDto}
	 * or list of gids
	 */
	private SearchCompositeDto<SearchOriginCompositeDto, Integer> searchComposite;

	public LotGeneratorInputDto getLotGeneratorInput() {
		return this.lotGeneratorInput;
	}

	public void setLotGeneratorInput(final LotGeneratorInputDto lotGeneratorInput) {
		this.lotGeneratorInput = lotGeneratorInput;
	}

	public SearchCompositeDto<SearchOriginCompositeDto, Integer> getSearchComposite() {
		return this.searchComposite;
	}

	public void setSearchComposite(
		final SearchCompositeDto<SearchOriginCompositeDto, Integer> searchComposite) {
		this.searchComposite = searchComposite;
	}
}
