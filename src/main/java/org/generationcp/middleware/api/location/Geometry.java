package org.generationcp.middleware.api.location;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;

@AutoProperty
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"coordinates", "type"})
public class Geometry {

	private List<Double> coordinates;

	private String type;

	public Geometry() {

	}

	public Geometry(final List<Double> coordinates, final String type) {
		this.coordinates = coordinates;
		this.type = type;
	}

	public List<Double> getCoordinates() {
		return this.coordinates;
	}

	public Geometry setCoordinates(final List<Double> coordinates) {
		this.coordinates = coordinates;
		return this;
	}

	public String getType() {
		return this.type;
	}

	public Geometry setType(final String type) {
		this.type = type;
		return this;
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof Geometry)) {
			return false;
		}
		final Geometry castOther = (Geometry) other;
		return new EqualsBuilder().append(this.coordinates, castOther.coordinates).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.coordinates).hashCode();
	}

	@Override
	public String toString() {
		return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
	}

}
