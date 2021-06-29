package org.generationcp.middleware.api.brapi.v2.observationunit;

import java.util.Map;

public class ObservationUnitPosition {

	private String blockNumber;
	private String entryNumber;
	private String entryType;
	private Map<String, Object> geoCoordinates;
	private String positionCoordinateX;
	private String positionCoordinateXType;
	private String positionCoordinateY;
	private String positionCoordinateYType;
	private String replicate;

	public ObservationUnitPosition() {
	}

	public String getBlockNumber() {
		return this.blockNumber;
	}

	public void setBlockNumber(final String blockNumber) {
		this.blockNumber = blockNumber;
	}

	public String getEntryNumber() {
		return this.entryNumber;
	}

	public void setEntryNumber(final String entryNumber) {
		this.entryNumber = entryNumber;
	}

	public String getEntryType() {
		return this.entryType;
	}

	public void setEntryType(final String entryType) {
		this.entryType = entryType;
	}

	public Map<String, Object> getGeoCoordinates() {
		return this.geoCoordinates;
	}

	public void setGeoCoordinates(final Map<String, Object> geoCoordinates) {
		this.geoCoordinates = geoCoordinates;
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

	public String getReplicate() {
		return this.replicate;
	}

	public void setReplicate(final String replicate) {
		this.replicate = replicate;
	}
}
