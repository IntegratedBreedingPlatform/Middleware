package org.generationcp.middleware.service.impl.inventory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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
			private Integer lotId;
			private String storageLocation;
			private Double availableBalance;
			private Integer unitId;

			public String getStockId() {
				return this.stockId;
			}

			public void setStockId(final String stockId) {
				this.stockId = stockId;
			}

			public Integer getLotId() {
				return this.lotId;
			}

			public void setLotId(final Integer lotId) {
				this.lotId = lotId;
			}

			public String getStorageLocation() {
				return this.storageLocation;
			}

			public void setStorageLocation(final String storageLocation) {
				this.storageLocation = storageLocation;
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
		private Map<String, StockDTO> stockByStockId = new LinkedHashMap<>();
		private List<ObservationUnitDTO> observationUnits = new ArrayList<>();
		private Map<Integer, VariableDTO> entryDetailByVariableId = new LinkedHashMap<>();

		public Map<Integer, VariableDTO> getEntryDetailByVariableId() {
			return this.entryDetailByVariableId;
		}

		public void setEntryDetailByVariableId(
			final Map<Integer, VariableDTO> entryDetailByVariableId) {
			this.entryDetailByVariableId = entryDetailByVariableId;
		}

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

	public static class VariableDTO {

		private Integer variableId;

		private String name;

		private Integer value;

		public Integer getVariableId() {
			return this.variableId;
		}

		public void setVariableId(final Integer variableId) {
			this.variableId = variableId;
		}

		public String getName() {
			return this.name;
		}

		public void setName(final String name) {
			this.name = name;
		}

		public Integer getValue() {
			return this.value;
		}

		public void setValue(final Integer value) {
			this.value = value;
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
