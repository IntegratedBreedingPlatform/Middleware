package org.generationcp.middleware.domain.search_request;

import java.util.List;

public class GidSearchDto extends SearchRequestDto {

	private List<Integer> gids;

	public List<Integer> getGids() {
		return this.gids;
	}

	public void setGids(final List<Integer> gids) {
		this.gids = gids;
	}
}
