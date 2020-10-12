
package org.generationcp.middleware.domain.dms;

import com.google.common.collect.Lists;
import org.generationcp.middleware.dao.dms.InstanceMetadata;
import org.generationcp.middleware.service.api.user.ContactDto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudySummary implements Serializable {

	private static final long serialVersionUID = -515769070343491680L;

	private Integer studyDbid;

	private String name;

	private String description;

	private String type;

	private String observationUnitId;

	private List<String> years = Lists.newArrayList();

	private List<String> seasons = Lists.newArrayList();

	private String locationId;

	private String programDbId;

	private String programName;

	private Date startDate;

	private Date endDate;

	private boolean active;

	private List<InstanceMetadata> instanceMetaData = new ArrayList<>();

	private Map<String, String> optionalInfo = new HashMap<>();

	private List<ContactDto> contacts = new ArrayList<>();

	public Integer getStudyDbid() {
		return this.studyDbid;
	}

	public StudySummary setStudyDbid(final Integer studyDbid) {
		this.studyDbid = studyDbid;
		return this;
	}

	public String getName() {
		return this.name;
	}

	public StudySummary setName(final String name) {
		this.name = name;
		return this;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getType() {
		return this.type;
	}

	public StudySummary setType(final String type) {
		this.type = type;
		return this;
	}

	public List<String> getYears() {
		return this.years;
	}

	public StudySummary setYears(final List<String> years) {
		this.years = years;
		return this;
	}

	public StudySummary addYear(final String year) {
		this.years.add(year);
		return this;
	}

	public List<String> getSeasons() {
		return this.seasons;
	}

	public StudySummary setSeasons(final List<String> seasons) {
		this.seasons = seasons;
		return this;
	}

	public StudySummary addSeason(final String season) {
		this.seasons.add(season);
		return this;
	}

	public String getLocationId() {
		return this.locationId;
	}

	public StudySummary setLocationId(final String locationId) {
		this.locationId = locationId;
		return this;
	}

	public String getProgramDbId() {
		return this.programDbId;
	}

	public StudySummary setProgramDbId(final String programDbId) {
		this.programDbId = programDbId;
		return this;
	}

	public String getProgramName() {
		return this.programName;
	}

	public StudySummary setProgramName(final String programName) {
		this.programName = programName;
		return this;
	}

	public Date getStartDate() {
		return this.startDate;
	}

	public StudySummary setStartDate(final Date startDate) {
		this.startDate = startDate;
		return this;
	}

	public Date getEndDate() {
		return this.endDate;
	}

	public StudySummary setEndDate(final Date endDate) {
		this.endDate = endDate;
		return this;
	}

	public boolean isActive() {
		return this.active;
	}

	public StudySummary setActive(final boolean active) {
		this.active = active;
		return this;
	}

	public Map<String, String> getOptionalInfo() {
		return this.optionalInfo;
	}

	public StudySummary setOptionalInfo(final Map<String, String> optionalInfo) {
		this.optionalInfo = optionalInfo;
		return this;
	}

	public List<InstanceMetadata> getInstanceMetaData() {
		return this.instanceMetaData;
	}

	public List<ContactDto> getContacts() {
		return this.contacts;
	}

	public void setContacts(final List<ContactDto> contacts) {
		this.contacts = contacts;
	}

	public void setInstanceMetaData(final List<InstanceMetadata> instanceMetaData) {
		this.instanceMetaData = instanceMetaData;
	}

	public String getObservationUnitId() {
		return this.observationUnitId;
	}

	public void setObservationUnitId(final String observationUnitId) {
		this.observationUnitId = observationUnitId;
	}
}
