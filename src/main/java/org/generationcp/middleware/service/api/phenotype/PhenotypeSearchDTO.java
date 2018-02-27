package org.generationcp.middleware.service.api.phenotype;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.ArrayList;
import java.util.List;

@AutoProperty
public class PhenotypeSearchDTO {
	private String observationUnitDbId;
	private String observationUnitName;
	private String observationLevel;
	private String observationLevels;
	private String plotNumber;
	private String plantNumber;
	private String blockNumber;
	private String replicate;
	private String germplasmDbId;
	private String germplasmName;
	private String studyDbId;
	private String studyName;
	private String studyLocationDbId;
	private String studyLocation;
	private String programName;
	private String x;
	private String y;
	private String entryType;
	private String entryNumber;

	private List<PhenotypeSearchObservationDTO> observations;

	public String getObservationUnitDbId() {
		return observationUnitDbId;
	}

	public void setObservationUnitDbId(String observationUnitDbId) {
		this.observationUnitDbId = observationUnitDbId;
	}

	public String getObservationLevel() {
		return observationLevel;
	}

	public void setObservationLevel(String observationLevel) {
		this.observationLevel = observationLevel;
	}

	public String getObservationLevels() {
		return observationLevels;
	}

	public void setObservationLevels(String observationLevels) {
		this.observationLevels = observationLevels;
	}

	public String getPlotNumber() {
		return plotNumber;
	}

	public void setPlotNumber(String plotNumber) {
		this.plotNumber = plotNumber;
	}

	public String getPlantNumber() {
		return plantNumber;
	}

	public void setPlantNumber(String plantNumber) {
		this.plantNumber = plantNumber;
	}

	public String getBlockNumber() {
		return blockNumber;
	}

	public void setBlockNumber(String blockNumber) {
		this.blockNumber = blockNumber;
	}

	public String getReplicate() {
		return replicate;
	}

	public void setReplicate(String replicate) {
		this.replicate = replicate;
	}

	public String getGermplasmDbId() {
		return germplasmDbId;
	}

	public void setGermplasmDbId(String germplasmDbId) {
		this.germplasmDbId = germplasmDbId;
	}

	public String getGermplasmName() {
		return germplasmName;
	}

	public void setGermplasmName(String germplasmName) {
		this.germplasmName = germplasmName;
	}

	public String getStudyDbId() {
		return studyDbId;
	}

	public void setStudyDbId(String studyDbId) {
		this.studyDbId = studyDbId;
	}

	public String getStudyName() {
		return studyName;
	}

	public void setStudyName(String studyName) {
		this.studyName = studyName;
	}

	public String getStudyLocationDbId() {
		return studyLocationDbId;
	}

	public void setStudyLocationDbId(String studyLocationDbId) {
		this.studyLocationDbId = studyLocationDbId;
	}

	public String getStudyLocation() {
		return studyLocation;
	}

	public void setStudyLocation(String studyLocation) {
		this.studyLocation = studyLocation;
	}

	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	public String getX() {
		return x;
	}

	public void setX(String x) {
		this.x = x;
	}

	public String getY() {
		return y;
	}

	public void setY(String y) {
		this.y = y;
	}

	public String getEntryType() {
		return entryType;
	}

	public void setEntryType(String entryType) {
		this.entryType = entryType;
	}

	public List<PhenotypeSearchObservationDTO> getObservations() {
		if (this.observations == null) {
			this.observations = new ArrayList<>();
		}
		return observations;
	}

	public void setObservations(List<PhenotypeSearchObservationDTO> observations) {
		this.observations = observations;
	}

	public String getEntryNumber() {
		return entryNumber;
	}

	public void setEntryNumber(String entryNumber) {
		this.entryNumber = entryNumber;
	}

	public String getObservationUnitName() {
		return observationUnitName;
	}

	public void setObservationUnitName(String observationUnitName) {
		this.observationUnitName = observationUnitName;
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}
}
