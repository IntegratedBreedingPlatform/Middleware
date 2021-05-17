
package org.generationcp.middleware.domain.dms;

import org.generationcp.middleware.dao.dms.InstanceMetadata;
import org.generationcp.middleware.service.api.user.ContactDto;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AutoProperty
public class StudySummary implements Serializable {

	private static final long serialVersionUID = -515769070343491680L;

	private Integer trialDbId;

	private String name;

	private String description;

	private String observationUnitId;

	private String locationId;

	private String programDbId;

	private String programName;

	private Date startDate;

	private Date endDate;

	private boolean active;

	private List<InstanceMetadata> instanceMetaData = new ArrayList<>();

	private Map<String, String> optionalInfo = new HashMap<>();

	private List<ContactDto> contacts = new ArrayList<>();

	public Integer getTrialDbId() {
		return this.trialDbId;
	}

	public StudySummary setTrialDbId(final Integer trialDbId) {
		this.trialDbId = trialDbId;
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

	public StudySummary setDescription(final String description) {
		this.description = description;
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

	public StudySummary setContacts(final List<ContactDto> contacts) {
		this.contacts = contacts;
		return this;
	}

	public StudySummary setInstanceMetaData(final List<InstanceMetadata> instanceMetaData) {
		this.instanceMetaData = instanceMetaData;
		return this;
	}

	public String getObservationUnitId() {
		return this.observationUnitId;
	}

	public StudySummary setObservationUnitId(final String observationUnitId) {
		this.observationUnitId = observationUnitId;
		return this;
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}


}
