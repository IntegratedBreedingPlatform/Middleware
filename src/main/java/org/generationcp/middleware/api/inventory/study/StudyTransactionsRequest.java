package org.generationcp.middleware.api.inventory.study;

import org.generationcp.middleware.domain.inventory.manager.TransactionsSearchDto;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;

@AutoProperty
public class StudyTransactionsRequest {

	// transaction filters
	private TransactionsSearchDto transactionsSearch;

	// study filters
	private List<Integer> instanceNoList;
	private List<Integer> plotNoList;
	private String entryType;
	private List<Integer> entryNoList;
	private List<Integer> observationUnitIds;

	private SortedPageRequest sortedPageRequest;
	private String draw;

	public TransactionsSearchDto getTransactionsSearch() {
		return this.transactionsSearch;
	}

	public void setTransactionsSearch(final TransactionsSearchDto transactionsSearch) {
		this.transactionsSearch = transactionsSearch;
	}

	public List<Integer> getInstanceNoList() {
		return this.instanceNoList;
	}

	public void setInstanceNoList(final List<Integer> instanceNoList) {
		this.instanceNoList = instanceNoList;
	}

	public List<Integer> getPlotNoList() {
		return this.plotNoList;
	}

	public void setPlotNoList(final List<Integer> plotNoList) {
		this.plotNoList = plotNoList;
	}

	public String getEntryType() {
		return this.entryType;
	}

	public void setEntryType(final String entryType) {
		this.entryType = entryType;
	}

	public List<Integer> getEntryNoList() {
		return this.entryNoList;
	}

	public void setEntryNoList(final List<Integer> entryNoList) {
		this.entryNoList = entryNoList;
	}

	public List<Integer> getObservationUnitIds() {
		return observationUnitIds;
	}

	public void setObservationUnitIds(final List<Integer> observationUnitIds) {
		this.observationUnitIds = observationUnitIds;
	}

	public SortedPageRequest getSortedPageRequest() {
		return this.sortedPageRequest;
	}

	public void setSortedPageRequest(final SortedPageRequest sortedPageRequest) {
		this.sortedPageRequest = sortedPageRequest;
	}

	public String getDraw() {
		return this.draw;
	}

	public void setDraw(final String draw) {
		this.draw = draw;
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
