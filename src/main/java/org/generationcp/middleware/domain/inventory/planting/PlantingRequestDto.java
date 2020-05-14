package org.generationcp.middleware.domain.inventory.planting;

import org.generationcp.middleware.domain.inventory.common.SearchCompositeDto;
import org.generationcp.middleware.service.api.dataset.ObservationUnitsSearchDTO;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Map;

@AutoProperty
public class PlantingRequestDto {

	@AutoProperty
	public static class WithdrawalInstruction {

		private boolean groupTransactions;

		private boolean withdrawAllAvailableBalance;

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

	private Map<String, String> lotPerEntryNo;

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

	public Map<String, String> getLotPerEntryNo() {
		return lotPerEntryNo;
	}

	public void setLotPerEntryNo(final Map<String, String> lotPerEntryNo) {
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
