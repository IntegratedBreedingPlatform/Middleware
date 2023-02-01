package org.generationcp.middleware.service.api.study;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

//FIXME This class should be removed and try to use MeasurementVariable instead
public class MeasurementVariableDto {

	private Integer id;

	private String name;

	private transient int hashCode;

	public MeasurementVariableDto() {
	}

	public MeasurementVariableDto(final Integer id, final String name) {
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof MeasurementVariableDto))
			return false;
		MeasurementVariableDto castOther = (MeasurementVariableDto) other;
		return new EqualsBuilder().append(id, castOther.id).append(name, castOther.name).isEquals();
	}

	@Override
	public int hashCode() {
		if (hashCode == 0) {
			hashCode = new HashCodeBuilder().append(id).append(name).toHashCode();
		}
		return hashCode;
	}

	@Override
	public String toString() {
		return "MeasurementVariableDto [id=" + id + ", name=" + name + "]";
	}

}
