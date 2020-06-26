package org.generationcp.middleware.service.api.study;

import org.generationcp.middleware.pojos.SortedPageRequest;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class StudyGermplasmSourceRequest {

	private int studyId;
	private SortedPageRequest sortedRequest;
	private StudyGermplasmSourceSearchDto studyGermplasmSourceSearchDto;

	public int getStudyId() {
		return this.studyId;
	}

	public void setStudyId(final int studyId) {
		this.studyId = studyId;
	}

	public StudyGermplasmSourceSearchDto getStudyGermplasmSourceSearchDto() {
		return this.studyGermplasmSourceSearchDto;
	}

	public void setStudyGermplasmSourceSearchDto(
		final StudyGermplasmSourceSearchDto studyGermplasmSourceSearchDto) {
		this.studyGermplasmSourceSearchDto = studyGermplasmSourceSearchDto;
	}

	public SortedPageRequest getSortedRequest() {
		return this.sortedRequest;
	}

	public void setSortedRequest(final SortedPageRequest sortedRequest) {
		this.sortedRequest = sortedRequest;
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}

}
