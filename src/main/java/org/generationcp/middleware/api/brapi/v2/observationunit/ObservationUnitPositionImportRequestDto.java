package org.generationcp.middleware.api.brapi.v2.observationunit;

import org.generationcp.middleware.api.location.Coordinate;

import java.util.List;

public class ObservationUnitPositionImportRequestDto {

	private String entryType;
	private Coordinate geoCoordinates;
	private String observationLevel;
	private List<ObservationLevelRelationship> observationLevelRelationships;
	private String positionCoordinateX;
	private String positionCoordinateXType;
	private String positionCoordinateY;
	private String positionCoordinateYType;

	public ObservationUnitPositionImportRequestDto() {
	}

	public String getEntryType() {
		return this.entryType;
	}

	public void setEntryType(final String entryType) {
		this.entryType = entryType;
	}

	public Coordinate getGeoCoordinates() {
		return this.geoCoordinates;
	}

	public void setGeoCoordinates(final Coordinate geoCoordinates) {
		this.geoCoordinates = geoCoordinates;
	}

	public String getObservationLevel() {
		return observationLevel;
	}

	public void setObservationLevel(final String observationLevel) {
		this.observationLevel = observationLevel;
	}

	public List<ObservationLevelRelationship> getObservationLevelRelationships() {
		return observationLevelRelationships;
	}

	public void setObservationLevelRelationships(
		final List<ObservationLevelRelationship> observationLevelRelationships) {
		this.observationLevelRelationships = observationLevelRelationships;
	}

	public String getPositionCoordinateX() {
		return this.positionCoordinateX;
	}

	public void setPositionCoordinateX(final String positionCoordinateX) {
		this.positionCoordinateX = positionCoordinateX;
	}

	public String getPositionCoordinateXType() {
		return this.positionCoordinateXType;
	}

	public void setPositionCoordinateXType(final String positionCoordinateXType) {
		this.positionCoordinateXType = positionCoordinateXType;
	}

	public String getPositionCoordinateY() {
		return this.positionCoordinateY;
	}

	public void setPositionCoordinateY(final String positionCoordinateY) {
		this.positionCoordinateY = positionCoordinateY;
	}

	public String getPositionCoordinateYType() {
		return this.positionCoordinateYType;
	}

	public void setPositionCoordinateYType(final String positionCoordinateYType) {
		this.positionCoordinateYType = positionCoordinateYType;
	}
}
