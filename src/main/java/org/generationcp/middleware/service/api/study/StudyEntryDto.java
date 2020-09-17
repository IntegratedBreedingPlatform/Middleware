package org.generationcp.middleware.service.api.study;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.HashMap;
import java.util.Map;

@AutoProperty
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudyEntryDto {

	private Integer entryId;

	private Integer entryNumber;

	private String entryCode;

	private Integer gid;

	private String designation;

	private Integer lotCount;

	private String available;

	private String unit;

	private Map<String, StudyEntryPropertyData> variables = new HashMap<>();

	public Integer getEntryId() {
		return entryId;
	}

	public void setEntryId(final Integer entryId) {
		this.entryId = entryId;
	}

	public Integer getGid() {
		return gid;
	}

	public void setGid(final Integer gid) {
		this.gid = gid;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(final String designation) {
		this.designation = designation;
	}

	public Integer getEntryNumber() {
		return entryNumber;
	}

	public void setEntryNumber(final Integer entryNumber) {
		this.entryNumber = entryNumber;
	}

	public String getEntryCode() {
		return entryCode;
	}

	public void setEntryCode(final String entryCode) {
		this.entryCode = entryCode;
	}

	public Integer getLotCount() {
		return lotCount;
	}

	public void setLotCount(final Integer lotCount) {
		this.lotCount = lotCount;
	}

	public String getAvailable() {
		return available;
	}

	public void setAvailable(final String available) {
		this.available = available;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(final String unit) {
		this.unit = unit;
	}

	public Map<String, StudyEntryPropertyData> getVariables() {
		return variables;
	}

	public void setVariables(final Map<String, StudyEntryPropertyData> variables) {
		this.variables = variables;
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
