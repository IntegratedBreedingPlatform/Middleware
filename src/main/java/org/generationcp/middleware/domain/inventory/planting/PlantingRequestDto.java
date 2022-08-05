package org.generationcp.middleware.domain.inventory.planting;

import org.generationcp.middleware.domain.inventory.common.SearchCompositeDto;
import org.generationcp.middleware.service.api.dataset.ObservationUnitsSearchDTO;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;
import java.util.Map;

@AutoProperty
public class PlantingRequestDto {

	@AutoProperty
	public static class WithdrawalInstruction {

		private boolean groupTransactions;

		private boolean withdrawAllAvailableBalance;

		private boolean withdrawUsingEntryDetail;

		private Integer entryDetailVariableId;

		private Double withdrawalAmount;

		public boolean isGroupTransactions() {
			return groupTransactions;
		}

		public void setGroupTransactions(final boolean groupTransactions) {
			this.groupTransactions = groupTransactions;
		}

		public boolean isWithdrawAllAvailableBalance() {
			return withdrawAllAvailableBalance;
		}

		public void setWithdrawAllAvailableBalance(final boolean withdrawAllAvailableBalance) {
			this.withdrawAllAvailableBalance = withdrawAllAvailableBalance;
		}

		public Double getWithdrawalAmount() {
			return withdrawalAmount;
		}

		public void setWithdrawalAmount(final Double withdrawalAmount) {
			this.withdrawalAmount = withdrawalAmount;
		}

		public boolean isWithdrawUsingEntryDetail() {
			return this.withdrawUsingEntryDetail;
		}

		public void setWithdrawUsingEntryDetail(final boolean withdrawUsingEntryDetail) {
			this.withdrawUsingEntryDetail = withdrawUsingEntryDetail;
		}

		public Integer getEntryDetailVariableId() {
			return this.entryDetailVariableId;
		}

		public void setEntryDetailVariableId(final Integer entryDetailVariableId) {
			this.entryDetailVariableId = entryDetailVariableId;
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

		public boolean isValid() {
			return (!isWithdrawAllAvailableBalance() && !isWithdrawUsingEntryDetail() && entryDetailVariableId == null && withdrawalAmount != null && withdrawalAmount > 0)
				|| (groupTransactions && isWithdrawAllAvailableBalance() && !isWithdrawUsingEntryDetail() && entryDetailVariableId == null && withdrawalAmount == null)
				|| (!isWithdrawAllAvailableBalance() && isWithdrawUsingEntryDetail() && entryDetailVariableId != null && withdrawalAmount == null);
		}
	}


	@AutoProperty
	public static class LotEntryNumber {

		private Integer entryNo;
		private Integer lotId;

		public Integer getEntryNo() {
			return entryNo;
		}

		public void setEntryNo(final Integer entryNo) {
			this.entryNo = entryNo;
		}

		public Integer getLotId() {
			return lotId;
		}

		public void setLotId(final Integer lotId) {
			this.lotId = lotId;
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


	private SearchCompositeDto<ObservationUnitsSearchDTO, Integer> selectedObservationUnits;

	private Map<String, PlantingRequestDto.WithdrawalInstruction> withdrawalsPerUnit;

	private List<LotEntryNumber> lotPerEntryNo;

	private String notes;

	public SearchCompositeDto<ObservationUnitsSearchDTO, Integer> getSelectedObservationUnits() {
		return selectedObservationUnits;
	}

	public void setSelectedObservationUnits(
		final SearchCompositeDto<ObservationUnitsSearchDTO, Integer> selectedObservationUnits) {
		this.selectedObservationUnits = selectedObservationUnits;
	}

	public Map<String, PlantingRequestDto.WithdrawalInstruction> getWithdrawalsPerUnit() {
		return withdrawalsPerUnit;
	}

	public void setWithdrawalsPerUnit(
		final Map<String, PlantingRequestDto.WithdrawalInstruction> withdrawalsPerUnit) {
		this.withdrawalsPerUnit = withdrawalsPerUnit;
	}

	public List<LotEntryNumber> getLotPerEntryNo() {
		return lotPerEntryNo;
	}

	public void setLotPerEntryNo(final List<LotEntryNumber> lotPerEntryNo) {
		this.lotPerEntryNo = lotPerEntryNo;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(final String notes) {
		this.notes = notes;
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
