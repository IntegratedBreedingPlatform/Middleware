package org.generationcp.middleware.service.api.study;

import java.util.List;

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
	
	private final List<MeasurementDto> traitMeasurements;
	
	public ObservationDto(final Integer measurementId, final String trialInstance, final String entryType,
			final Integer gid, final String designation, final String entryNo, final String seedSource,
			final String repitionNumber, final String plotNumber, final List<MeasurementDto> traitMeasurements) {
		this.measurementId = measurementId;
		this.trialInstance = trialInstance;
		this.entryType = entryType;
		this.gid = gid;
		this.designation = designation;
		this.entryNo = entryNo;
		this.seedSource = seedSource;
		this.repitionNumber = repitionNumber;
		this.plotNumber = plotNumber;
		this.traitMeasurements = traitMeasurements;
	}

	public Integer getMeasurementId() {
		return measurementId;
	}

	public String getTrialInstance() {
		return trialInstance;
	}

	public String getEntryType() {
		return entryType;
	}

	public Integer getGid() {
		return gid;
	}

	public String getDesignation() {
		return designation;
	}

	public String getEntryNo() {
		return entryNo;
	}

	public String getSeedSource() {
		return seedSource;
	}

	public String getRepitionNumber() {
		return repitionNumber;
	}

	public String getPlotNumber() {
		return plotNumber;
	}

	public List<MeasurementDto> getTraitMeasurements() {
		return traitMeasurements;
	}



	
		
}
