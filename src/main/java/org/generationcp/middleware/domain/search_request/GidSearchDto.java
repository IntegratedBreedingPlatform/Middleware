package org.generationcp.middleware.domain.search_request;

import java.util.List;

public class GidSearchDto extends SearchRequestDto {

	private List<Integer> gids;

	private Integer studyId;

	public Integer getStudyId() {
		return this.studyId;
	}

	public void setStudyId(final Integer studyId) {
		this.studyId = studyId;
	}

	public List<Integer> getGids() {
		return this.gids;
	}

	public void setGids(final List<Integer> gids) {
		this.gids = gids;
	}
}
