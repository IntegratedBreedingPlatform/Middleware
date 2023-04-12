
package org.generationcp.middleware.domain.dms;

import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
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
public class TrialSummary implements Serializable {

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

	private Map<String, String> additionalInfo = new HashMap<>();

	private List<ContactDto> contacts = new ArrayList<>();

	private List<ExternalReferenceDTO> externalReferences = new ArrayList<>();

	public Integer getTrialDbId() {
		return this.trialDbId;
	}

	public void setTrialDbId(final Integer trialDbId) {
		this.trialDbId = trialDbId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getLocationId() {
		return this.locationId;
	}

	public void setLocationId(final String locationId) {
		this.locationId = locationId;
	}

	public String getProgramDbId() {
		return this.programDbId;
	}

	public void setProgramDbId(final String programDbId) {
		this.programDbId = programDbId;
	}

	public String getProgramName() {
		return this.programName;
	}

	public void setProgramName(final String programName) {
		this.programName = programName;
	}

	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(final Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(final Date endDate) {
		this.endDate = endDate;
	}

	public boolean isActive() {
		return this.active;
	}

	public void setActive(final boolean active) {
		this.active = active;
	}

	public Map<String, String> getAdditionalInfo() {
		return this.additionalInfo;
	}

	public void setAdditionalInfo(final Map<String, String> additionalInfo) {
		this.additionalInfo = additionalInfo;
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

	public List<ExternalReferenceDTO> getExternalReferences() {
		return this.externalReferences;
	}

	public void setExternalReferences(final List<ExternalReferenceDTO> externalReferences) {
		this.externalReferences = externalReferences;
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
