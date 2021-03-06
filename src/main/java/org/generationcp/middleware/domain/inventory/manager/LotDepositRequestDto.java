package org.generationcp.middleware.domain.inventory.manager;

import org.generationcp.middleware.domain.inventory.common.SearchCompositeDto;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Map;

@AutoProperty
public class LotDepositRequestDto {

	private SearchCompositeDto<Integer, String> selectedLots;

	private Map<String, Double> depositsPerUnit;

	private String notes;

	private Integer sourceStudyId;

	public SearchCompositeDto<Integer, String> getSelectedLots() {
		return selectedLots;
	}

	public void setSelectedLots(final SearchCompositeDto<Integer, String> selectedLots) {
		this.selectedLots = selectedLots;
	}

	public Map<String, Double> getDepositsPerUnit() {
		return depositsPerUnit;
	}

	public void setDepositsPerUnit(final Map<String, Double> depositsPerUnit) {
		this.depositsPerUnit = depositsPerUnit;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(final String notes) {
		this.notes = notes;
	}

	public Integer getSourceStudyId() {
		return this.sourceStudyId;
	}

	public void setSourceStudyId(final Integer sourceStudyId) {
		this.sourceStudyId = sourceStudyId;
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
