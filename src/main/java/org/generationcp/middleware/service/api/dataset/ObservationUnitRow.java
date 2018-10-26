package org.generationcp.middleware.service.api.dataset;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AutoProperty
public class ObservationUnitRow {

	private Integer observationUnitId;

	private Integer gid;

	private String designation;

	private String action;

	private Map<String, Object> variables;

	private String repetionNumber;

	private String plotNumber;

	private String obsUnitId;

	private String blockNumber;

	private String rowNumber;

	private String columnNumber;

	private String fieldMapColumn;

	private String fieldMapRange;

	private String samples;

	private String entryNo;

	private String entryCode;

	private String trialInstance;

	private String entryType;

	private final List<Pair<String, String>> additionalGermplasmDescriptors = new ArrayList<>();

	private final List<Pair<String, String>> additionalDesignFactors = new ArrayList<>();

	public ObservationUnitRow() {

	}

	public void additionalGermplasmDescriptor(final String name, final String value) {
		this.additionalGermplasmDescriptors.add(new ImmutablePair<String, String>(name, value));
	}

	public void additionalDesignFactor(final String name, final String value) {
		this.additionalDesignFactors.add(new ImmutablePair<String, String>(name, value));
	}

	public String getEntryNo() {
		return this.entryNo;
	}

	public void setEntryNo(final String entryNo) {
		this.entryNo = entryNo;
	}

	public String getEntryCode() {
		return this.entryCode;
	}

	public void setEntryCode(final String entryCode) {
		this.entryCode = entryCode;
	}

	public String getTrialInstance() {
		return this.trialInstance;
	}

	public void setTrialInstance(final String trialInstance) {
		this.trialInstance = trialInstance;
	}

	public String getEntryType() {
		return this.entryType;
	}

	public void setEntryType(final String entryType) {
		this.entryType = entryType;
	}

	public Integer getObservationUnitId() {
		return this.observationUnitId;
	}

	public void setObservationUnitId(final Integer observationUnitId) {
		this.observationUnitId = observationUnitId;
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

	public String getAction() {
		return this.action;
	}

	public void setAction(final String action) {
		this.action = action;
	}

	public Map<String, Object> getVariables() {
		return this.variables;
	}

	public void setVariables(final Map<String, Object> variables) {
		this.variables = variables;
	}

	public String getRepetionNumber() {
		return this.repetionNumber;
	}

	public void setRepetionNumber(final String repetionNumber) {
		this.repetionNumber = repetionNumber;
	}

	public String getPlotNumber() {
		return this.plotNumber;
	}

	public void setPlotNumber(final String plotNumber) {
		this.plotNumber = plotNumber;
	}

	public String getObsUnitId() {
		return this.obsUnitId;
	}

	public void setObsUnitId(final String obsUnitId) {
		this.obsUnitId = obsUnitId;
	}

	public String getBlockNumber() {
		return this.blockNumber;
	}

	public void setBlockNumber(final String blockNumber) {
		this.blockNumber = blockNumber;
	}

	public String getRowNumber() {
		return this.rowNumber;
	}

	public void setRowNumber(final String rowNumber) {
		this.rowNumber = rowNumber;
	}

	public String getColumnNumber() {
		return this.columnNumber;
	}

	public void setColumnNumber(final String columnNumber) {
		this.columnNumber = columnNumber;
	}

	public String getFieldMapColumn() {
		return this.fieldMapColumn;
	}

	public void setFieldMapColumn(final String fieldMapColumn) {
		this.fieldMapColumn = fieldMapColumn;
	}

	public String getFieldMapRange() {
		return this.fieldMapRange;
	}

	public void setFieldMapRange(final String fieldMapRange) {
		this.fieldMapRange = fieldMapRange;
	}

	public String getSamples() {
		return this.samples;
	}

	public void setSamples(final String samples) {
		this.samples = samples;
	}

	public List<Pair<String, String>> getAdditionalGermplasmDescriptors() {
		return this.additionalGermplasmDescriptors;
	}

	public List<Pair<String, String>> getAdditionalDesignFactors() {
		return this.additionalDesignFactors;
	}

	public static class ObservationUnitData {

		private Integer observationId;

		private Integer categoricalValueId;

		private String value;

		private Phenotype.ValueStatus status;

		public ObservationUnitData(final Integer observationId, final Integer categoricalValueId, final String value,
			final Phenotype.ValueStatus status) {
			this.observationId = observationId;
			this.categoricalValueId = categoricalValueId;
			this.value = value;
			this.status = status;
		}

		public ObservationUnitData(final String value) {
			this.value = value;
		}

		public Integer getObservationId() {
			return this.observationId;
		}

		public void setObservationId(final Integer observationId) {
			this.observationId = observationId;
		}

		public Integer getCategoricalValueId() {
			return this.categoricalValueId;
		}

		public void setCategoricalValueId(final Integer categoricalValueId) {
			this.categoricalValueId = categoricalValueId;
		}

		public String getValue() {
			return this.value;
		}

		public void setValue(final String value) {
			this.value = value;
		}

		public Phenotype.ValueStatus getStatus() {
			return this.status;
		}

		public void setStatus(final Phenotype.ValueStatus status) {
			this.status = status;
		}

		@Override
		public boolean equals(final Object o) {
			return Pojomatic.equals(this, o);
		}

		@Override
		public int hashCode() {
			return Pojomatic.hashCode(this);
		}

		@Override
		public String toString() {
			return Pojomatic.toString(this);
		}
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}
}


