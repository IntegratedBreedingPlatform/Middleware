
package org.generationcp.middleware.pojos.report;

import java.util.List;

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

	private String station_id;
	private String lid;
	private List<GermplasmEntry> entriesList;
	private String occ_cycle;
	private Integer offset;
	private Integer occ;
	private String harvDate;
	private String station;
	private String occ_abbr;
	private String location_id;
	private String cooperator_id;
	private String plantDate;
	private String occ_name;
	private String fb_class;
	private String occ_new_cycle;
	private List<GermplasmEntry> entriesList2;

	// not used
	private Integer offset_2;
	private Integer totalEntries;
	private Integer check_1;
	private String use_generic;
	private String organization;
	private Integer offset_1;
	private Integer tid;
	private Integer check_freq;
	private String program;
	private String new_cycle;
	private Integer studyID;
	private String use_abreviation;
	private String internat_type;
	private String cycle;
	private String trial_abbr;
	private String trial_name;
	private Integer check_2;
	private List<Occurrence> ocurrencesList;
	private Integer trial_mega_environment;
	private String print_mega_environment;
	private Integer expansion_level;

	public Integer getOffset_2() {
		return this.offset_2;
	}

	public void setOffset_2(Integer offset_2) {
		this.offset_2 = offset_2;
	}

	public Integer getTotalEntries() {
		return this.totalEntries;
	}

	public void setTotalEntries(Integer totalEntries) {
		this.totalEntries = totalEntries;
	}

	public Integer getCheck_1() {
		return this.check_1;
	}

	public void setCheck_1(Integer check_1) {
		this.check_1 = check_1;
	}

	public String getUse_generic() {
		return this.use_generic;
	}

	public void setUse_generic(String use_generic) {
		this.use_generic = use_generic;
	}

	public String getOrganization() {
		return this.organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public Integer getOffset_1() {
		return this.offset_1;
	}

	public void setOffset_1(Integer offset_1) {
		this.offset_1 = offset_1;
	}

	public Integer getTid() {
		return this.tid;
	}

	public void setTid(Integer tid) {
		this.tid = tid;
	}

	public Integer getCheck_freq() {
		return this.check_freq;
	}

	public void setCheck_freq(Integer check_freq) {
		this.check_freq = check_freq;
	}

	public String getProgram() {
		return this.program;
	}

	public void setProgram(String program) {
		this.program = program;
	}

	public String getNew_cycle() {
		return this.new_cycle;
	}

	public void setNew_cycle(String new_cycle) {
		this.new_cycle = new_cycle;
	}

	public Integer getStudyID() {
		return this.studyID;
	}

	public void setStudyID(Integer studyID) {
		this.studyID = studyID;
	}

	public String getUse_abreviation() {
		return this.use_abreviation;
	}

	public void setUse_abreviation(String use_abreviation) {
		this.use_abreviation = use_abreviation;
	}

	public String getInternat_type() {
		return this.internat_type;
	}

	public void setInternat_type(String internat_type) {
		this.internat_type = internat_type;
	}

	public String getCycle() {
		return this.cycle;
	}

	public void setCycle(String cycle) {
		this.cycle = cycle;
	}

	public String getTrial_abbr() {
		return this.trial_abbr;
	}

	public void setTrial_abbr(String trial_abbr) {
		this.trial_abbr = trial_abbr;
	}

	public String getTrial_name() {
		return this.trial_name;
	}

	public void setTrial_name(String trial_name) {
		this.trial_name = trial_name;
	}

	public Integer getOffset() {
		return this.offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getCheck_2() {
		return this.check_2;
	}

	public void setCheck_2(Integer check_2) {
		this.check_2 = check_2;
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

	public Integer getTrial_mega_environment() {
		return this.trial_mega_environment;
	}

	public void setTrial_mega_environment(Integer trial_mega_environment) {
		this.trial_mega_environment = trial_mega_environment;
	}

	public String getPrint_mega_environment() {
		return this.print_mega_environment;
	}

	public void setPrint_mega_environment(String print_mega_environment) {
		this.print_mega_environment = print_mega_environment;
	}

	public Integer getExpansion_level() {
		return this.expansion_level;
	}

	public void setExpansion_level(Integer expansion_level) {
		this.expansion_level = expansion_level;
	}

	public String getStation_id() {
		return this.station_id;
	}

	public void setStation_id(String station_id) {
		this.station_id = station_id;
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

	public String getOcc_cycle() {
		return this.occ_cycle;
	}

	public void setOcc_cycle(String occ_cycle) {
		this.occ_cycle = occ_cycle;
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

	public String getOcc_abbr() {
		return this.occ_abbr;
	}

	public void setOcc_abbr(String occ_abbr) {
		this.occ_abbr = occ_abbr;
	}

	public String getLocation_id() {
		return this.location_id;
	}

	public void setLocation_id(String location_id) {
		this.location_id = location_id;
	}

	public String getCooperator_id() {
		return this.cooperator_id;
	}

	public void setCooperator_id(String cooperator_id) {
		this.cooperator_id = cooperator_id;
	}

	public String getPlantDate() {
		return this.plantDate;
	}

	public void setPlantDate(String plantDate) {
		this.plantDate = plantDate;
	}

	public String getOcc_name() {
		return this.occ_name;
	}

	public void setOcc_name(String occ_name) {
		this.occ_name = occ_name;
	}

	public String getFb_class() {
		return this.fb_class;
	}

	public void setFb_class(String fb_class) {
		this.fb_class = fb_class;
	}

	public String getOcc_new_cycle() {
		return this.occ_new_cycle;
	}

	public void setOcc_new_cycle(String occ_new_cycle) {
		this.occ_new_cycle = occ_new_cycle;
	}

}
