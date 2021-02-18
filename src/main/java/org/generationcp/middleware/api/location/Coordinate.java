package org.generationcp.middleware.api.location;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Coordinate {

	private Geometry geometry;

	private String type;

	public Coordinate() {

	}

	public Coordinate(final Geometry geometry, final String type) {
		this.geometry = geometry;
		this.type = type;
	}

	public Geometry getGeometry() {
		return this.geometry;
	}

	public Coordinate setGeometry(final Geometry geometry) {
		this.geometry = geometry;
		return this;
	}

	public String getType() {
		return this.type;
	}

	public Coordinate setType(final String type) {
		this.type = type;
		return this;
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof Geometry)) {
			return false;
		}
		final Coordinate castOther = (Coordinate) other;
		return new EqualsBuilder().append(this.geometry, castOther.geometry).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.geometry).hashCode();
	}

	@Override
	public String toString() {
		return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
	}
}
