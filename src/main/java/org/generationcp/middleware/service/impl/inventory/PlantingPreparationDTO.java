package org.generationcp.middleware.service.impl.inventory;

import java.util.List;
import java.util.Map;

public class PlantingPreparationDTO {

	public static class PlantingPreparationEntryDTO {

		public static class ObservationUnitDTO {

			private Integer ndExperimentId;
			private String observationUnitId;
			private Integer instanceId;

			public Integer getNdExperimentId() {
				return this.ndExperimentId;
			}

			public void setNdExperimentId(final Integer ndExperimentId) {
				this.ndExperimentId = ndExperimentId;
			}

			public String getObservationUnitId() {
				return this.observationUnitId;
			}

			public void setObservationUnitId(final String observationUnitId) {
				this.observationUnitId = observationUnitId;
			}

			public Integer getInstanceId() {
				return this.instanceId;
			}

			public void setInstanceId(final Integer instanceId) {
				this.instanceId = instanceId;
			}
		}


		public static class StockDTO {

			private String stockId;
			private String storateLocation;
			private Double availableBalance;
			private Integer unitId;

			public String getStockId() {
				return this.stockId;
			}

			public void setStockId(final String stockId) {
				this.stockId = stockId;
			}

			public String getStorateLocation() {
				return this.storateLocation;
			}

			public void setStorateLocation(final String storateLocation) {
				this.storateLocation = storateLocation;
			}

			public Double getAvailableBalance() {
				return this.availableBalance;
			}

			public void setAvailableBalance(final Double availableBalance) {
				this.availableBalance = availableBalance;
			}

			public Integer getUnitId() {
				return this.unitId;
			}

			public void setUnitId(final Integer unitId) {
				this.unitId = unitId;
			}
		}


		private Integer entryNo;
		private String entryType;
		private Integer gid;
		private String designation;
		private Map<String, StockDTO> stockByStockId;
		private List<ObservationUnitDTO> observationUnits;

		public Integer getEntryNo() {
			return this.entryNo;
		}

		public void setEntryNo(final Integer entryNo) {
			this.entryNo = entryNo;
		}

		public String getEntryType() {
			return this.entryType;
		}

		public void setEntryType(final String entryType) {
			this.entryType = entryType;
		}

		public Integer getGid() {
			return this.gid;
		}

		public void setGid(final Integer gid) {
			this.gid = gid;
		}

		public String getDesignation() {
			return this.designation;
		}

		public void setDesignation(final String designation) {
			this.designation = designation;
		}

		public Map<String, StockDTO> getStockByStockId() {
			return this.stockByStockId;
		}

		public void setStockByStockId(final Map<String, StockDTO> stockByStockId) {
			this.stockByStockId = stockByStockId;
		}

		public List<ObservationUnitDTO> getObservationUnits() {
			return this.observationUnits;
		}

		public void setObservationUnits(final List<ObservationUnitDTO> observationUnits) {
			this.observationUnits = observationUnits;
		}
	}


	private List<PlantingPreparationEntryDTO> entries;

	public List<PlantingPreparationEntryDTO> getEntries() {
		return this.entries;
	}

	public void setEntries(final List<PlantingPreparationEntryDTO> entries) {
		this.entries = entries;
	}
}
