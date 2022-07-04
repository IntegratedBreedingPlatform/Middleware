package org.generationcp.middleware.service.api.study;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.StringUtils;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@AutoProperty
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudyEntryDto implements Serializable {

	private Integer entryId;

	private Integer entryNumber;

	private Integer gid;

	private String designation;

	private Integer lotCount;

	private String availableBalance;

	private String unit;

	private String cross;

	private Integer groupGid;

	private String guid;

	private Map<Integer, StudyEntryPropertyData> properties = new HashMap<>();

	public StudyEntryDto(){

	}

	public StudyEntryDto(final Integer entryId, final Integer gid, final String designation) {
		this.entryId = entryId;
		this.gid = gid;
		this.designation = designation;
	}

	public StudyEntryDto(final Integer entryId, final Integer entryNumber, final Integer gid, final String designation) {
		this.entryId = entryId;
		this.entryNumber = entryNumber;
		this.gid = gid;
		this.designation = designation;
	}

	public StudyEntryDto(final Integer entryId, final Integer entryNumber, final Integer gid,
		final String designation, final Integer lotCount, final String availableBalance, final String unit, final String cross,
		final Integer groupGid, final String guid){
		this.entryId = entryId;
		this.entryNumber = entryNumber;
		this.gid = gid;
		this.designation = designation;
		this.lotCount = lotCount;
		this.availableBalance = availableBalance;
		this.unit = unit;
		this.cross = cross;
		this.groupGid = groupGid;
		this.guid = guid;
	}

	public Integer getEntryId() {
		return this.entryId;
	}

	public void setEntryId(final Integer entryId) {
		this.entryId = entryId;
	}

	public Integer getGid() {
		return this.gid;
	}

	public void setGid(final Integer gid) {
		this.gid = gid;
	}

	public String getDesignation() {
		return this.designation;
	}

	public void setDesignation(final String designation) {
		this.designation = designation;
	}

	public Integer getEntryNumber() {
		return this.entryNumber;
	}

	public void setEntryNumber(final Integer entryNumber) {
		this.entryNumber = entryNumber;
	}

	public Integer getLotCount() {
		return this.lotCount;
	}

	public void setLotCount(final Integer lotCount) {
		this.lotCount = lotCount;
	}

	public String getAvailableBalance() {
		return this.availableBalance;
	}

	public void setAvailableBalance(final String availableBalance) {
		this.availableBalance = availableBalance;
	}

	public String getUnit() {
		return this.unit;
	}

	public void setUnit(final String unit) {
		this.unit = unit;
	}

	public String getCross() {
		return cross;
	}

	public void setCross(final String cross) {
		this.cross = StringUtils.isEmpty(cross) ? "-" : cross;
	}

	public Integer getGroupGid() {
		return groupGid;
	}

	public void setGroupGid(final Integer groupGid) {
		this.groupGid = groupGid;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(final String guid) {
		this.guid = guid;
	}

	public Map<Integer, StudyEntryPropertyData> getProperties() {
		return this.properties;
	}

	public void setProperties(final Map<Integer, StudyEntryPropertyData> properties) {
		this.properties = properties;
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


	public Optional<String> getStudyEntryPropertyValue(final Integer variableId) {
		if (this.properties.containsKey(variableId) && this.properties.get(variableId) != null
			&& this.properties.get(variableId).getVariableValue() != null) {
			return Optional.of(this.properties.get(variableId).getVariableValue());
		}
		return Optional.empty();
	}

}
