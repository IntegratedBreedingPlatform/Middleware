package org.generationcp.middleware.domain.inventory.manager;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class LotSplitRequestDto {

	private String splitLotUUID;
	private NewLotSplitDto newLot;
	private InitialLotDepositDto initialDeposit;

	public String getSplitLotUUID() {
		return splitLotUUID;
	}

	public void setSplitLotUUID(final String splitLotUUID) {
		this.splitLotUUID = splitLotUUID;
	}

	public NewLotSplitDto getNewLot() {
		return newLot;
	}

	public void setNewLot(final NewLotSplitDto newLot) {
		this.newLot = newLot;
	}

	public InitialLotDepositDto getInitialDeposit() {
		return initialDeposit;
	}

	public void setInitialDeposit(final InitialLotDepositDto initialDeposit) {
		this.initialDeposit = initialDeposit;
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

	@AutoProperty
	public static class NewLotSplitDto {

		private String stockPrefix;
		private boolean generateStock;
		private Integer locationId;
		private String notes;

		public String getStockPrefix() {
			return stockPrefix;
		}

		public void setStockPrefix(final String stockPrefix) {
			this.stockPrefix = stockPrefix;
		}

		public boolean getGenerateStock() {
			return generateStock;
		}

		public void setGenerateStock(final boolean generateStock) {
			this.generateStock = generateStock;
		}

		public Integer getLocationId() {
			return locationId;
		}

		public void setLocationId(final Integer locationId) {
			this.locationId = locationId;
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

	@AutoProperty
	public static class InitialLotDepositDto {

		private double amount;
		private String notes;

		public double getAmount() {
			return amount;
		}

		public void setAmount(final double amount) {
			this.amount = amount;
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

}
