
package org.generationcp.middleware.pojos.report;

import java.util.List;

/**
 * This is being used in JasperReport, any changes in the attribute names should be reflected in the corresponding JasperReports
 * 
 * @author EfficioDaniel
 * 
 */
public class Occurrence {

	public Occurrence() {
	}

	public Occurrence(Integer occ) {
		this.occ = occ;
	}

	public Occurrence(List<GermplasmEntry> entriesList) {
		this.entriesList = entriesList;
		this.entriesList2 = entriesList;
	}

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
	private String useAbreviation;
	private String internatType;
	private String cycle;
	private String trialAbbr;
	private String trialName;
	private Integer check2;
	private List<Occurrence> ocurrencesList;
	private Integer trialMegaEnvironment;
	private String printMegaEnvironment;
	private Integer expansionLevel;

	public String getStationId() {
		return stationId;
	}

	public void setStationId(String stationId) {
		this.stationId = stationId;
	}

	public String getLid() {
		return lid;
	}

	public void setLid(String lid) {
		this.lid = lid;
	}

	public List<GermplasmEntry> getEntriesList() {
		return entriesList;
	}

	public void setEntriesList(List<GermplasmEntry> entriesList) {
		this.entriesList = entriesList;
	}

	public String getOccCycle() {
		return occCycle;
	}

	public void setOccCycle(String occCycle) {
		this.occCycle = occCycle;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getOcc() {
		return occ;
	}

	public void setOcc(Integer occ) {
		this.occ = occ;
	}

	public String getHarvDate() {
		return harvDate;
	}

	public void setHarvDate(String harvDate) {
		this.harvDate = harvDate;
	}

	public String getStation() {
		return station;
	}

	public void setStation(String station) {
		this.station = station;
	}

	public String getOccAbbr() {
		return occAbbr;
	}

	public void setOccAbbr(String occAbbr) {
		this.occAbbr = occAbbr;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getCooperatorId() {
		return cooperatorId;
	}

	public void setCooperatorId(String cooperatorId) {
		this.cooperatorId = cooperatorId;
	}

	public String getPlantDate() {
		return plantDate;
	}

	public void setPlantDate(String plantDate) {
		this.plantDate = plantDate;
	}

	public String getOccName() {
		return occName;
	}

	public void setOccName(String occName) {
		this.occName = occName;
	}

	public String getFbClass() {
		return fbClass;
	}

	public void setFbClass(String fbClass) {
		this.fbClass = fbClass;
	}

	public String getOccNewCycle() {
		return occNewCycle;
	}

	public void setOccNewCycle(String occNewCycle) {
		this.occNewCycle = occNewCycle;
	}

	public List<GermplasmEntry> getEntriesList2() {
		return entriesList2;
	}

	public void setEntriesList2(List<GermplasmEntry> entriesList2) {
		this.entriesList2 = entriesList2;
	}

	public Integer getOffset2() {
		return offset2;
	}

	public void setOffset2(Integer offset2) {
		this.offset2 = offset2;
	}

	public Integer getTotalEntries() {
		return totalEntries;
	}

	public void setTotalEntries(Integer totalEntries) {
		this.totalEntries = totalEntries;
	}

	public Integer getCheck1() {
		return check1;
	}

	public void setCheck1(Integer check1) {
		this.check1 = check1;
	}

	public String getUseGeneric() {
		return useGeneric;
	}

	public void setUseGeneric(String useGeneric) {
		this.useGeneric = useGeneric;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public Integer getOffset1() {
		return offset1;
	}

	public void setOffset1(Integer offset1) {
		this.offset1 = offset1;
	}

	public Integer getTid() {
		return tid;
	}

	public void setTid(Integer tid) {
		this.tid = tid;
	}

	public Integer getCheckFreq() {
		return checkFreq;
	}

	public void setCheckFreq(Integer checkFreq) {
		this.checkFreq = checkFreq;
	}

	public String getProgram() {
		return program;
	}

	public void setProgram(String program) {
		this.program = program;
	}

	public String getNewCycle() {
		return newCycle;
	}

	public void setNewCycle(String newCycle) {
		this.newCycle = newCycle;
	}

	public Integer getStudyID() {
		return studyID;
	}

	public void setStudyID(Integer studyID) {
		this.studyID = studyID;
	}

	public String getUseAbreviation() {
		return useAbreviation;
	}

	public void setUseAbreviation(String useAbreviation) {
		this.useAbreviation = useAbreviation;
	}

	public String getInternatType() {
		return internatType;
	}

	public void setInternatType(String internatType) {
		this.internatType = internatType;
	}

	public String getCycle() {
		return cycle;
	}

	public void setCycle(String cycle) {
		this.cycle = cycle;
	}

	public String getTrialAbbr() {
		return trialAbbr;
	}

	public void setTrialAbbr(String trialAbbr) {
		this.trialAbbr = trialAbbr;
	}

	public String getTrialName() {
		return trialName;
	}

	public void setTrialName(String trialName) {
		this.trialName = trialName;
	}

	public Integer getCheck2() {
		return check2;
	}

	public void setCheck2(Integer check2) {
		this.check2 = check2;
	}

	public List<Occurrence> getOcurrencesList() {
		return ocurrencesList;
	}

	public void setOcurrencesList(List<Occurrence> ocurrencesList) {
		this.ocurrencesList = ocurrencesList;
	}

	public Integer getTrialMegaEnvironment() {
		return trialMegaEnvironment;
	}

	public void setTrialMegaEnvironment(Integer trialMegaEnvironment) {
		this.trialMegaEnvironment = trialMegaEnvironment;
	}

	public String getPrintMegaEnvironment() {
		return printMegaEnvironment;
	}

	public void setPrintMegaEnvironment(String printMegaEnvironment) {
		this.printMegaEnvironment = printMegaEnvironment;
	}

	public Integer getExpansionLevel() {
		return expansionLevel;
	}

	public void setExpansionLevel(Integer expansionLevel) {
		this.expansionLevel = expansionLevel;
	}

}
