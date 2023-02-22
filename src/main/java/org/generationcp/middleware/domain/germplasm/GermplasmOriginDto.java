package org.generationcp.middleware.domain.germplasm;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Map;

@AutoProperty
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GermplasmOriginDto {

	private String programUUID;
	private Integer studyId;
	private String studyName;
	private String observationUnitId;
	private Integer plotNumber;
	private Integer repNumber;
	private Integer blockNumber;
	private String positionCoordinateX;
	private String positionCoordinateY;
	private Map<String, Object> geoCoordinates;
	private String observationUnitType;
	private Integer observationUnitNumber;

	public String getProgramUUID() {
		return this.programUUID;
	}

	public void setProgramUUID(final String programUUID) {
		this.programUUID = programUUID;
	}

	public Integer getStudyId() {
		return studyId;
	}

	public void setStudyId(final Integer studyId) {
		this.studyId = studyId;
	}

	public String getStudyName() {
		return studyName;
	}

	public void setStudyName(final String studyName) {
		this.studyName = studyName;
	}

	public String getObservationUnitId() {
		return observationUnitId;
	}

	public void setObservationUnitId(final String observationUnitId) {
		this.observationUnitId = observationUnitId;
	}

	public Integer getRepNumber() {
		return repNumber;
	}

	public void setRepNumber(final Integer repNumber) {
		this.repNumber = repNumber;
	}

	public Integer getBlockNumber() {
		return blockNumber;
	}

	public void setBlockNumber(final Integer blockNumber) {
		this.blockNumber = blockNumber;
	}

	public String getPositionCoordinateX() {
		return positionCoordinateX;
	}

	public void setPositionCoordinateX(final String positionCoordinateX) {
		this.positionCoordinateX = positionCoordinateX;
	}

	public String getPositionCoordinateY() {
		return positionCoordinateY;
	}

	public void setPositionCoordinateY(final String positionCoordinateY) {
		this.positionCoordinateY = positionCoordinateY;
	}

	public Map<String, Object> getGeoCoordinates() {
		return geoCoordinates;
	}

	public void setGeoCoordinates(final Map<String, Object> geoCoordinates) {
		this.geoCoordinates = geoCoordinates;
	}

	public Integer getPlotNumber() {
		return plotNumber;
	}

	public void setPlotNumber(final Integer plotNumber) {
		this.plotNumber = plotNumber;
	}

	public String getObservationUnitType() {
		return observationUnitType;
	}

	public void setObservationUnitType(final String observationUnitType) {
		this.observationUnitType = observationUnitType;
	}

	public Integer getObservationUnitNumber() {
		return observationUnitNumber;
	}

	public void setObservationUnitNumber(final Integer observationUnitNumber) {
		this.observationUnitNumber = observationUnitNumber;
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
