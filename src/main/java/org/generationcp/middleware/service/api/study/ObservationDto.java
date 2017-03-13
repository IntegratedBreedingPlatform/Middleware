
package org.generationcp.middleware.service.api.study;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class ObservationDto {

	private final Integer measurementId;

	private final String trialInstance;

	private final String entryType;
	
	private final Integer gid;

	private final String designation;

	private final String entryNo;

	private final String entryCode;

	private final String repitionNumber;

	private final String plotNumber;

	private String plotId;

	private final String blockNumber;

	private String rowNumber;
	private String columnNumber;

	private final List<MeasurementDto> traitMeasurements;

	private final List<Pair<String, String>> additionalGermplasmDescriptors = new ArrayList<>();

	private transient int hashCode;

	public ObservationDto(final Integer measurementId, final String trialInstance, final String entryType, final Integer gid,
			final String designation, final String entryNo, final String entryCode, final String repitionNumber, final String plotNumber,
			final String blockNumber, final List<MeasurementDto> traitMeasurements) {
		this.measurementId = measurementId;
		this.trialInstance = trialInstance;
		this.entryType = entryType;
		this.gid = gid;
		this.designation = designation;
		this.entryNo = entryNo;
		this.entryCode = entryCode;
		this.repitionNumber = repitionNumber;
		this.plotNumber = plotNumber;
		this.blockNumber = blockNumber;
		this.traitMeasurements = traitMeasurements;
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
		return this.repitionNumber;
	}

	public String getPlotNumber() {
		return this.plotNumber;
	}

	public String getPlotId() {
		return this.plotId;
	}

	public void setPlotId(String plotId) {
		this.plotId = plotId;
	}

	public String getBlockNumber() {
		return this.blockNumber;
	}

	public String getColumnNumber() {
		return this.columnNumber;
	}

	public void setColumnNumber(String columnNumber) {
		this.columnNumber = columnNumber;
	}

	public String getRowNumber() {
		return this.rowNumber;
	}

	public void setRowNumber(String rowNumber) {
		this.rowNumber = rowNumber;
	}

	public List<MeasurementDto> getTraitMeasurements() {
		return this.traitMeasurements;
	}

	public void additionalGermplasmDescriptor(final String name, final String value) {
		this.additionalGermplasmDescriptors.add(new ImmutablePair<String, String>(name, value));
	}

	public List<Pair<String, String>> getAdditionalGermplasmDescriptors() {
		return this.additionalGermplasmDescriptors;
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof ObservationDto))
			return false;
		ObservationDto castOther = (ObservationDto) other;
		return new EqualsBuilder().append(measurementId, castOther.measurementId).isEquals();
	}

	@Override
	public int hashCode() {
		if (hashCode == 0) {
			hashCode = new HashCodeBuilder().append(measurementId).toHashCode();
		}
		return hashCode;
	}

}
