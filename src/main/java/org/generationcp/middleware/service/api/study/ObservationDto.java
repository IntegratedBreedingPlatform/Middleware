
package org.generationcp.middleware.service.api.study;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class ObservationDto {

	private final Integer measurementId;

	private final String trialInstance;

	private final String entryType;
	
	private final Integer gid;

	private final String designation;

	private final String entryNo;

	private final String entryCode;

	private final String repetionNumber;

	private final String plotNumber;

	private String obsUnitId;

	private final String blockNumber;

	private String rowNumber;

	private String columnNumber;

	private String fieldMapColumn;

	private String fieldMapRange;

	private String samples;

	private final List<MeasurementDto> variableMeasurements;

	private final List<Pair<String, String>> additionalGermplasmDescriptors = new ArrayList<>();
	
	private final List<Pair<String, String>> additionalDesignFactors = new ArrayList<>();

	private transient int hashCode;

	public ObservationDto(final Integer measurementId, final String trialInstance, final String entryType, final Integer gid,
			final String designation, final String entryNo, final String entryCode, final String repetionNumber, final String plotNumber,
			final String blockNumber, final List<MeasurementDto> variableMeasurements) {
		this.measurementId = measurementId;
		this.trialInstance = trialInstance;
		this.entryType = entryType;
		this.gid = gid;
		this.designation = designation;
		this.entryNo = entryNo;
		this.entryCode = entryCode;
		this.repetionNumber = repetionNumber;
		this.plotNumber = plotNumber;
		this.blockNumber = blockNumber;
		this.variableMeasurements = variableMeasurements;
	}

	public ObservationDto(final Integer measurementId, final String designation, final List<MeasurementDto> variableResults, final Integer gid) {
		this.measurementId = measurementId;
		this.designation = designation;
		this.variableMeasurements = variableResults;
		this.trialInstance = null;
		this.entryType = null;
		this.gid = gid;
		this.entryNo = null;
		this.entryCode = null;
		this.repetionNumber = null;
		this.plotNumber = null;
		this.blockNumber = null;
	}

	public Integer getMeasurementId() {
		return this.measurementId;
	}

	public String getTrialInstance() {
		return this.trialInstance;
	}

	public String getEntryType() {
		return this.entryType;
	}

	public Integer getGid() {
		return this.gid;
	}

	public String getDesignation() {
		return this.designation;
	}

	public String getEntryNo() {
		return this.entryNo;
	}

	public String getEntryCode() {
		return this.entryCode;
	}

	public String getRepitionNumber() {
		return this.repetionNumber;
	}

	public String getPlotNumber() {
		return this.plotNumber;
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

	public String getColumnNumber() {
		return this.columnNumber;
	}

	public void setColumnNumber(final String columnNumber) {
		this.columnNumber = columnNumber;
	}

	public String getRowNumber() {
		return this.rowNumber;
	}

	public void setRowNumber(final String rowNumber) {
		this.rowNumber = rowNumber;
	}

	public List<MeasurementDto> getVariableMeasurements() {
		return this.variableMeasurements;
	}

	public void additionalGermplasmDescriptor(final String name, final String value) {
		this.additionalGermplasmDescriptors.add(new ImmutablePair<String, String>(name, value));
	}
	
	public List<Pair<String, String>> getAdditionalGermplasmDescriptors() {
		return this.additionalGermplasmDescriptors;
	}
	
	public void additionalDesignFactor(final String name, final String value) {
		this.additionalDesignFactors.add(new ImmutablePair<String, String>(name, value));
	}

	public List<Pair<String, String>> getAdditionalDesignFactors() {
		return this.additionalDesignFactors;
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof ObservationDto)) {
			return false;
		}
		final ObservationDto castOther = (ObservationDto) other;
		return new EqualsBuilder().append(this.measurementId, castOther.measurementId).isEquals();
	}

	@Override
	public int hashCode() {
		if (this.hashCode == 0) {
			this.hashCode = new HashCodeBuilder().append(this.measurementId).toHashCode();
		}
		return this.hashCode;
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
}
