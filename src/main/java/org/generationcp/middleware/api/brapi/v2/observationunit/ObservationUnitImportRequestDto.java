package org.generationcp.middleware.api.brapi.v2.observationunit;

import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;
import java.util.Map;

@AutoProperty
public class ObservationUnitImportRequestDto {

	private Map<String, String> additionalInfo;
	private List<ExternalReferenceDTO> externalReferences;
	private String germplasmDbId;
	private String germplasmName;
	private String locationDbId;
	private String locationName;
	private String observationUnitName;
	private String observationUnitPUI;
	private ObservationUnitPosition observationUnitPosition;
	private String programDbId;
	private String programName;
	private String seedLotDbId;
	private String studyDbId;
	private String studyName;
	private List<Treatment> treatments;
	private String trialDbId;
	private String trialName;

	public Map<String, String> getAdditionalInfo() {
		return this.additionalInfo;
	}

	public void setAdditionalInfo(final Map<String, String> additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public List<ExternalReferenceDTO> getExternalReferences() {
		return this.externalReferences;
	}

	public void setExternalReferences(final List<ExternalReferenceDTO> externalReferences) {
		this.externalReferences = externalReferences;
	}

	public String getGermplasmDbId() {
		return this.germplasmDbId;
	}

	public void setGermplasmDbId(final String germplasmDbId) {
		this.germplasmDbId = germplasmDbId;
	}

	public String getGermplasmName() {
		return this.germplasmName;
	}

	public void setGermplasmName(final String germplasmName) {
		this.germplasmName = germplasmName;
	}

	public String getLocationDbId() {
		return this.locationDbId;
	}

	public void setLocationDbId(final String locationDbId) {
		this.locationDbId = locationDbId;
	}

	public String getLocationName() {
		return this.locationName;
	}

	public void setLocationName(final String locationName) {
		this.locationName = locationName;
	}

	public String getObservationUnitName() {
		return this.observationUnitName;
	}

	public void setObservationUnitName(final String observationUnitName) {
		this.observationUnitName = observationUnitName;
	}

	public String getObservationUnitPUI() {
		return this.observationUnitPUI;
	}

	public void setObservationUnitPUI(final String observationUnitPUI) {
		this.observationUnitPUI = observationUnitPUI;
	}

	public ObservationUnitPosition getObservationUnitPosition() {
		return this.observationUnitPosition;
	}

	public void setObservationUnitPosition(final ObservationUnitPosition observationUnitPosition) {
		this.observationUnitPosition = observationUnitPosition;
	}

	public String getProgramDbId() {
		return this.programDbId;
	}

	public void setProgramDbId(final String programDbId) {
		this.programDbId = programDbId;
	}

	public String getProgramName() {
		return this.programName;
	}

	public void setProgramName(final String programName) {
		this.programName = programName;
	}

	public String getSeedLotDbId() {
		return this.seedLotDbId;
	}

	public void setSeedLotDbId(final String seedLotDbId) {
		this.seedLotDbId = seedLotDbId;
	}

	public String getStudyDbId() {
		return this.studyDbId;
	}

	public void setStudyDbId(final String studyDbId) {
		this.studyDbId = studyDbId;
	}

	public String getStudyName() {
		return this.studyName;
	}

	public void setStudyName(final String studyName) {
		this.studyName = studyName;
	}

	public List<Treatment> getTreatments() {
		return this.treatments;
	}

	public void setTreatments(final List<Treatment> treatments) {
		this.treatments = treatments;
	}

	public String getTrialDbId() {
		return this.trialDbId;
	}

	public void setTrialDbId(final String trialDbId) {
		this.trialDbId = trialDbId;
	}

	public String getTrialName() {
		return this.trialName;
	}

	public void setTrialName(final String trialName) {
		this.trialName = trialName;
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
