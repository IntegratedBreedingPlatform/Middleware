
package org.generationcp.middleware.pojos.report;

import java.util.List;

public class Occurrence {

	private String stationId;
	private String lid;
	private List<GermplasmEntry> entriesList;
	private String occCycle;
	private Integer offset;
	private Integer occ;
	private String harvDate;
	private String station;
	private String occAbbr;
	private String locationId;
	private String cooperatorId;
	private String plantDate;
	private String occName;
	private String fbClass;
	private String occNewCycle;
	private List<GermplasmEntry> entriesList2;

	// not used
	private Integer offset2;
	private Integer totalEntries;
	private Integer check1;
	private String useGeneric;
	private String organization;
	private Integer offset1;
	private Integer tid;
	private Integer checkFreq;
	private String program;
	private String newCycle;
	private Integer studyID;
	private String useAbbreviation;
	private String internatType;
	private String cycle;
	private String trialAbbr;
	private String trialName;
	private Integer check2;
	private List<Occurrence> ocurrencesList;
	private Integer trialMegaEnvironment;
	private String printMegaEnvironment;
	private Integer expansionLevel;

	public Occurrence() {
	}

	public Occurrence(Integer occ) {
		this.occ = occ;
	}

	public Occurrence(List<GermplasmEntry> entriesList) {
		this.entriesList = entriesList;
		this.entriesList2 = entriesList;
	}
	
	public Integer getOffset2() {
		return this.offset2;
	}

	public void setOffset2(Integer offset2) {
		this.offset2 = offset2;
	}

	public Integer getTotalEntries() {
		return this.totalEntries;
	}

	public void setTotalEntries(Integer totalEntries) {
		this.totalEntries = totalEntries;
	}

	public Integer getCheck1() {
		return this.check1;
	}

	public void setCheck1(Integer check1) {
		this.check1 = check1;
	}

	public String getUseGeneric() {
		return this.useGeneric;
	}

	public void setUseGeneric(String useGeneric) {
		this.useGeneric = useGeneric;
	}

	public String getOrganization() {
		return this.organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public Integer getOffset1() {
		return this.offset1;
	}

	public void setOffset1(Integer offset1) {
		this.offset1 = offset1;
	}

	public Integer getTid() {
		return this.tid;
	}

	public void setTid(Integer tid) {
		this.tid = tid;
	}

	public Integer getCheckFreq() {
		return this.checkFreq;
	}

	public void setCheckFreq(Integer checkFreq) {
		this.checkFreq = checkFreq;
	}

	public String getProgram() {
		return this.program;
	}

	public void setProgram(String program) {
		this.program = program;
	}

	public String getNewCycle() {
		return this.newCycle;
	}

	public void setNewCycle(String newCycle) {
		this.newCycle = newCycle;
	}

	public Integer getStudyID() {
		return this.studyID;
	}

	public void setStudyID(Integer studyID) {
		this.studyID = studyID;
	}

	public String getUseAbbreviation() {
		return this.useAbbreviation;
	}

	public void setUseAbbreviation(String useAbbreviation) {
		this.useAbbreviation = useAbbreviation;
	}

	public String getInternatType() {
		return this.internatType;
	}

	public void setInternatType(String internatType) {
		this.internatType = internatType;
	}

	public String getCycle() {
		return this.cycle;
	}

	public void setCycle(String cycle) {
		this.cycle = cycle;
	}

	public String getTrialAbbr() {
		return this.trialAbbr;
	}

	public void setTrialAbbr(String trialAbbr) {
		this.trialAbbr = trialAbbr;
	}

	public String getTrialName() {
		return this.trialName;
	}

	public void setTrialName(String trialName) {
		this.trialName = trialName;
	}

	public Integer getOffset() {
		return this.offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getCheck2() {
		return this.check2;
	}

	public void setCheck2(Integer check2) {
		this.check2 = check2;
	}

	public List<GermplasmEntry> getEntriesList2() {
		return this.entriesList2;
	}

	public void setEntriesList2(List<GermplasmEntry> entriesList2) {
		this.entriesList2 = entriesList2;
	}

	public List<Occurrence> getOcurrencesList() {
		return this.ocurrencesList;
	}

	public void setOcurrencesList(List<Occurrence> ocurrencesList) {
		this.ocurrencesList = ocurrencesList;
	}

	public Integer getTrialMegaEnvironment() {
		return this.trialMegaEnvironment;
	}

	public void setTrialMegaEnvironment(Integer trialMegaEnvironment) {
		this.trialMegaEnvironment = trialMegaEnvironment;
	}

	public String getPrintMegaEnvironment() {
		return this.printMegaEnvironment;
	}

	public void setPrintMegaEnvironment(String printMegaEnvironment) {
		this.printMegaEnvironment = printMegaEnvironment;
	}

	public Integer getExpansionLevel() {
		return this.expansionLevel;
	}

	public void setExpansionLevel(Integer expansionLevel) {
		this.expansionLevel = expansionLevel;
	}

	public String getStationId() {
		return this.stationId;
	}

	public void setStationId(String stationId) {
		this.stationId = stationId;
	}

	public String getLid() {
		return this.lid;
	}

	public void setLid(String lid) {
		this.lid = lid;
	}

	public List<GermplasmEntry> getEntriesList() {
		return this.entriesList;
	}

	public void setEntriesList(List<GermplasmEntry> entriesList) {
		this.entriesList = entriesList;
	}

	public String getOccCycle() {
		return this.occCycle;
	}

	public void setOccCycle(String occCycle) {
		this.occCycle = occCycle;
	}

	public Integer getOcc() {
		return this.occ;
	}

	public void setOcc(Integer occ) {
		this.occ = occ;
	}

	public String getHarvDate() {
		return this.harvDate;
	}

	public void setHarvDate(String harvDate) {
		this.harvDate = harvDate;
	}

	public String getStation() {
		return this.station;
	}

	public void setStation(String station) {
		this.station = station;
	}

	public String getOccAbbr() {
		return this.occAbbr;
	}

	public void setOccAbbr(String occAbbr) {
		this.occAbbr = occAbbr;
	}

	public String getLocationId() {
		return this.locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getCooperatorId() {
		return this.cooperatorId;
	}

	public void setCooperatorId(String cooperatorId) {
		this.cooperatorId = cooperatorId;
	}

	public String getPlantDate() {
		return this.plantDate;
	}

	public void setPlantDate(String plantDate) {
		this.plantDate = plantDate;
	}

	public String getOccName() {
		return this.occName;
	}

	public void setOccName(String occName) {
		this.occName = occName;
	}

	public String getFbClass() {
		return this.fbClass;
	}

	public void setFbClass(String fbClass) {
		this.fbClass = fbClass;
	}

	public String getOccNewCycle() {
		return this.occNewCycle;
	}

	public void setOccNewCycle(String occNewCycle) {
		this.occNewCycle = occNewCycle;
	}

}
