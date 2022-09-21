package org.generationcp.middleware.service.api.phenotype;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.Lists;
import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.api.brapi.v2.observationunit.ObservationUnitPosition;
import org.generationcp.middleware.api.brapi.v2.observationunit.Treatment;
import org.generationcp.middleware.service.api.BrapiView;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AutoProperty
public class ObservationUnitDto {

	@JsonIgnore
	private Integer experimentId;

	@JsonIgnore
	private Integer experimentParentId;

	@JsonView(BrapiView.BrapiV2.class)
	private Map<String, String> additionalInfo;

	@JsonView(BrapiView.BrapiV2.class)
	private List<ExternalReferenceDTO> externalReferences;

	private String observationUnitDbId;
	private String observationUnitName;
	@JsonView(BrapiView.BrapiV1_2.class)
	private String observationLevel;

	@JsonView(BrapiView.BrapiV1_2.class)
	private String observationLevels;

	@JsonView({BrapiView.BrapiV1_2.class, BrapiView.BrapiV1_3.class})
	private String plotNumber;

	@JsonView({BrapiView.BrapiV1_2.class, BrapiView.BrapiV1_3.class})
	private String plantNumber;

	@JsonView({BrapiView.BrapiV1_2.class, BrapiView.BrapiV1_3.class})
	private String blockNumber;

	@JsonView({BrapiView.BrapiV1_2.class, BrapiView.BrapiV1_3.class})
	private String replicate;
	private String germplasmDbId;
	private String germplasmName;
	private String studyDbId;
	private String studyName;

	@JsonView(BrapiView.BrapiV1_2.class)
	private String studyLocationDbId;

	@JsonView(BrapiView.BrapiV1_2.class)
	private String studyLocation;

	@JsonView({BrapiView.BrapiV1_2.class, BrapiView.BrapiV2.class})
	private String programName;

	@JsonView(BrapiView.BrapiV1_2.class)
	private String x;

	@JsonView(BrapiView.BrapiV1_2.class)
	private String y;

	@JsonView({BrapiView.BrapiV1_2.class, BrapiView.BrapiV1_3.class})
	private String entryType;

	@JsonView({BrapiView.BrapiV1_2.class, BrapiView.BrapiV1_3.class})
	private String entryNumber;

	private List<PhenotypeSearchObservationDTO> observations;

	@JsonView(BrapiView.BrapiV1_2.class)
	private String instanceNumber;

	@JsonView(BrapiView.BrapiV2.class)
	private String locationDbId;

	@JsonView(BrapiView.BrapiV2.class)
	private String locationName;

	@JsonView(BrapiView.BrapiV2.class)
	private String observationUnitPUI;

	@JsonView(BrapiView.BrapiV2.class)
	private ObservationUnitPosition observationUnitPosition;

	@JsonView(BrapiView.BrapiV1_3.class)
	private String positionCoordinateX;

	@JsonView(BrapiView.BrapiV1_3.class)
	private String positionCoordinateY;

	@JsonView(BrapiView.BrapiV1_3.class)
	private List<ObservationUnitXRef> observationUnitXRef;

	@JsonView({BrapiView.BrapiV1_3.class, BrapiView.BrapiV2.class})
	private String programDbId;

	@JsonView(BrapiView.BrapiV2.class)
	private String seedLotDbId;

	@JsonView({BrapiView.BrapiV1_3.class, BrapiView.BrapiV2.class})
	private List<Treatment> treatments;

	@JsonView({BrapiView.BrapiV1_3.class, BrapiView.BrapiV2.class})
	private String trialDbId;

	@JsonView({BrapiView.BrapiV1_3.class, BrapiView.BrapiV2.class})
	private String trialName;

	protected static class ObservationUnitXRef {

		private String id;
		private String source;

		public String getId() {
			return this.id;
		}

		public void setId(final String id) {
			this.id = id;
		}

		public String getSource() {
			return this.source;
		}

		public void setSource(final String source) {
			this.source = source;
		}
	}

	public String getObservationUnitDbId() {
		return this.observationUnitDbId;
	}

	public void setObservationUnitDbId(final String observationUnitDbId) {
		this.observationUnitDbId = observationUnitDbId;
	}

	public String getObservationLevel() {
		return this.observationLevel;
	}

	public void setObservationLevel(final String observationLevel) {
		this.observationLevel = observationLevel;
	}

	public String getObservationLevels() {
		return this.observationLevels;
	}

	public void setObservationLevels(final String observationLevels) {
		this.observationLevels = observationLevels;
	}

	public String getPlotNumber() {
		return this.plotNumber;
	}

	public void setPlotNumber(final String plotNumber) {
		this.plotNumber = plotNumber;
	}

	public String getPlantNumber() {
		return this.plantNumber;
	}

	public void setPlantNumber(final String plantNumber) {
		this.plantNumber = plantNumber;
	}

	public String getBlockNumber() {
		return this.blockNumber;
	}

	public void setBlockNumber(final String blockNumber) {
		this.blockNumber = blockNumber;
	}

	public String getReplicate() {
		return this.replicate;
	}

	public void setReplicate(final String replicate) {
		this.replicate = replicate;
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

	public String getStudyLocationDbId() {
		return this.studyLocationDbId;
	}

	public void setStudyLocationDbId(final String studyLocationDbId) {
		this.studyLocationDbId = studyLocationDbId;
	}

	public String getStudyLocation() {
		return this.studyLocation;
	}

	public void setStudyLocation(final String studyLocation) {
		this.studyLocation = studyLocation;
	}

	public String getProgramName() {
		return this.programName;
	}

	public void setProgramName(final String programName) {
		this.programName = programName;
	}

	public String getX() {
		return this.x;
	}

	public void setX(final String x) {
		this.x = x;
	}

	public String getY() {
		return this.y;
	}

	public void setY(final String y) {
		this.y = y;
	}

	public String getEntryType() {
		return this.entryType;
	}

	public void setEntryType(final String entryType) {
		this.entryType = entryType;
	}

	public List<PhenotypeSearchObservationDTO> getObservations() {
		if (this.observations == null) {
			this.observations = new ArrayList<>();
		}
		return this.observations;
	}

	public void setObservations(final List<PhenotypeSearchObservationDTO> observations) {
		this.observations = observations;
	}

	public String getEntryNumber() {
		return this.entryNumber;
	}

	public void setEntryNumber(final String entryNumber) {
		this.entryNumber = entryNumber;
	}

	public String getObservationUnitName() {
		return this.observationUnitName;
	}

	public void setObservationUnitName(final String observationUnitName) {
		this.observationUnitName = observationUnitName;
	}

	public String getInstanceNumber() {
		return this.instanceNumber;
	}

	public void setInstanceNumber(final String instanceNumber) {
		this.instanceNumber = instanceNumber;
	}

	public Map<String, String> getAdditionalInfo() {
		return this.additionalInfo;
	}

	public void setAdditionalInfo(final Map<String, String> additionalInfo) {
		this.additionalInfo = additionalInfo;
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

	public String getObservationUnitPUI() {
		return this.observationUnitPUI;
	}

	public void setObservationUnitPUI(final String observationUnitPUI) {
		this.observationUnitPUI = observationUnitPUI;
	}

	public ObservationUnitPosition getObservationUnitPosition() {
		return this.observationUnitPosition;
	}

	public void setObservationUnitPosition(
		final ObservationUnitPosition observationUnitPosition) {
		this.observationUnitPosition = observationUnitPosition;
	}

	public List<ObservationUnitXRef> getObservationUnitXRef() {
		return this.observationUnitXRef;
	}

	public void setObservationUnitXRef(
		final List<ObservationUnitXRef> observationUnitXRef) {
		this.observationUnitXRef = observationUnitXRef;
	}

	public String getProgramDbId() {
		return this.programDbId;
	}

	public void setProgramDbId(final String programDbId) {
		this.programDbId = programDbId;
	}

	public List<Treatment> getTreatments() {
		if (this.treatments == null) {
			this.treatments = Lists.newArrayList();
		}
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

	public String getPositionCoordinateX() {
		return this.positionCoordinateX;
	}

	public void setPositionCoordinateX(final String positionCoordinateX) {
		this.positionCoordinateX = positionCoordinateX;
	}

	public String getPositionCoordinateY() {
		return this.positionCoordinateY;
	}

	public void setPositionCoordinateY(final String positionCoordinateY) {
		this.positionCoordinateY = positionCoordinateY;
	}

	public List<ExternalReferenceDTO> getExternalReferences() {
		return this.externalReferences;
	}

	public void setExternalReferences(final List<ExternalReferenceDTO> externalReferences) {
		this.externalReferences = externalReferences;
	}

	public String getSeedLotDbId() {
		return this.seedLotDbId;
	}

	public void setSeedLotDbId(final String seedLotDbId) {
		this.seedLotDbId = seedLotDbId;
	}

	public Integer getExperimentId() {
		return this.experimentId;
	}

	public void setExperimentId(final Integer experimentId) {
		this.experimentId = experimentId;
	}

	public Integer getExperimentParentId() {
		return experimentParentId;
	}

	public void setExperimentParentId(Integer experimentParentId) {
		this.experimentParentId = experimentParentId;
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
