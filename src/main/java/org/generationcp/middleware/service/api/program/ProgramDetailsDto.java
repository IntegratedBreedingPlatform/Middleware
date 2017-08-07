package org.generationcp.middleware.service.api.program;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;


public class ProgramDetailsDto implements Serializable, Comparable<ProgramDetailsDto> {

	private static final long serialVersionUID = 9163866215679883266L;
	private String programDbId;
	private String name;
	private String abbreviation;
	private String objective;
	private String leadPerson;

	public ProgramDetailsDto() {

	}

	public ProgramDetailsDto(final String programDbId, final String name, final String abbreviation, final String objective,
		final String leadPerson) {

		this.programDbId = programDbId;
		this.name = name;
		this.abbreviation = abbreviation;
		this.objective = objective;
		this.leadPerson = leadPerson;
	}

	public String getProgramDbId() {
		return programDbId;
	}

	public void setProgramDbId(String programDbId) {
		this.programDbId = programDbId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	public String getObjective() {
		return objective;
	}

	public void setObjective(String objective) {
		this.objective = objective;
	}

	public String getLeadPerson() {
		return leadPerson;
	}

	public void setLeadPerson(String leadPerson) {
		this.leadPerson = leadPerson;
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof ProgramDetailsDto)) {
			return false;
		}
		final ProgramDetailsDto castOther = (ProgramDetailsDto) other;
		return new EqualsBuilder().append(this.programDbId, castOther.programDbId).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.programDbId).hashCode();
	}

	@Override
	public String toString() {
		return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
	}

	@Override
	public int compareTo(final ProgramDetailsDto compareProgramDetails) { String id = compareProgramDetails.getProgramDbId();
		return this.programDbId.compareTo(id);
	}
}
