package org.generationcp.middleware.domain.study;

import org.generationcp.middleware.domain.inventory.common.SearchCompositeDto;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;

@AutoProperty
public class StudyEntryPropertyBatchUpdateRequest {

	private SearchCompositeDto<Integer, Integer> searchComposite;

	private Integer variableId;

	private String value;

	public StudyEntryPropertyBatchUpdateRequest() {

	}

	public StudyEntryPropertyBatchUpdateRequest(final SearchCompositeDto<Integer, Integer> searchComposite, final Integer variableId, final String value) {
		this.searchComposite = searchComposite;
		this.variableId = variableId;
		this.value = value;
	}

	public SearchCompositeDto<Integer, Integer> getSearchComposite() {
		return this.searchComposite;
	}

	public void setSearchComposite(final SearchCompositeDto<Integer, Integer> searchComposite) {
		this.searchComposite = searchComposite;
	}

	public Integer getVariableId() {
		return this.variableId;
	}

	public void setVariableId(final Integer variableId) {
		this.variableId = variableId;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(final String value) {
		this.value = value;
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
