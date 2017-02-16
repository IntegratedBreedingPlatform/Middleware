
package org.generationcp.middleware.service.api.study;

import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class ObservationDto {

	private final Integer measurementId;

	private final String trialInstance;

	private final String entryType;

	private final Integer gid;

	private final String designation;

	private final String entryNo;

	private final String seedSource;

	private final String repitionNumber;

	private final String plotNumber;

	private final String blockNumber;

	private final List<MeasurementDto> traitMeasurements;

	private transient int hashCode;

	public ObservationDto(final Integer measurementId, final String trialInstance, final String entryType, final Integer gid,
			final String designation, final String entryNo, final String seedSource, final String repitionNumber, final String plotNumber,
			final String blockNumber, final List<MeasurementDto> traitMeasurements) {
		this.measurementId = measurementId;
		this.trialInstance = trialInstance;
		this.entryType = entryType;
		this.gid = gid;
		this.designation = designation;
		this.entryNo = entryNo;
		this.seedSource = seedSource;
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

	public String getSeedSource() {
		return this.seedSource;
	}

	public String getRepitionNumber() {
		return this.repitionNumber;
	}

	public String getPlotNumber() {
		return this.plotNumber;
	}

	public String getBlockNumber() {
		return this.blockNumber;
	}

	public List<MeasurementDto> getTraitMeasurements() {
		return this.traitMeasurements;
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof ObservationDto))
			return false;
		ObservationDto castOther = (ObservationDto) other;
		return new EqualsBuilder().append(measurementId, castOther.measurementId).append(trialInstance, castOther.trialInstance)
				.append(entryType, castOther.entryType).append(gid, castOther.gid).append(designation, castOther.designation)
				.append(entryNo, castOther.entryNo).append(seedSource, castOther.seedSource)
				.append(repitionNumber, castOther.repitionNumber).append(plotNumber, castOther.plotNumber)
				.append(traitMeasurements, castOther.traitMeasurements).isEquals();
	}

	@Override
	public int hashCode() {
		if (hashCode == 0) {
			hashCode =
					new HashCodeBuilder().append(measurementId).append(trialInstance).append(entryType).append(gid).append(designation)
							.append(entryNo).append(seedSource).append(repitionNumber).append(plotNumber).append(traitMeasurements)
							.toHashCode();
		}
		return hashCode;
	}

}
