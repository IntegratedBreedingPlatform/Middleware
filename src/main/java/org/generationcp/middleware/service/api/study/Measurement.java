package org.generationcp.middleware.service.api.study;

import java.util.List;

public class Measurement {
	private final Integer measurementId;
	
	private final Integer trialInstance;
	
	private final String entryType;
	
	private final Integer gid;
	
	private final String designation;
	
	private final String entryNo;
	
	private final String seedSource;
	
	private final String repitionNumber;
	
	private final Integer plotNumber;
	
	private final List<Trait> traits;
	
	public Measurement(final Integer measurementId, final Integer trialInstance, final String entryType,
			final Integer gid, final String designation, final String entryNo, final String seedSource,
			final String repitionNumber, final Integer plotNumber, final List<Trait> traits) {
		this.measurementId = measurementId;
		this.trialInstance = trialInstance;
		this.entryType = entryType;
		this.gid = gid;
		this.designation = designation;
		this.entryNo = entryNo;
		this.seedSource = seedSource;
		this.repitionNumber = repitionNumber;
		this.plotNumber = plotNumber;
		this.traits = traits;
	}

	public Integer getMeasurementId() {
		return measurementId;
	}

	public Integer getTrialInstance() {
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

	public Integer getPlotNumber() {
		return plotNumber;
	}

	public List<Trait> getTraits() {
		return traits;
	}
		
}
