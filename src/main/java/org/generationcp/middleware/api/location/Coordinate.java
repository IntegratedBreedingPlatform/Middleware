package org.generationcp.middleware.api.location;

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
		return geometry;
	}

	public Coordinate setGeometry(final Geometry geometry) {
		this.geometry = geometry;
		return this;
	}

	public String getType() {
		return type;
	}

	public Coordinate setType(final String type) {
		this.type = type;
		return this;
	}

}
