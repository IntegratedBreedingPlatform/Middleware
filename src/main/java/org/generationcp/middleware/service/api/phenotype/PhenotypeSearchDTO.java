

package org.generationcp.middleware.service.api.phenotype;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.Lists;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.ArrayList;
import java.util.List;

@AutoProperty
public class PhenotypeSearchDTO {

	public static class View {

		public static class PhenotypeBrapiV2 {

		}

		public static class PhenotypeBrapiV1_2 {

		}
	}

	private String observationUnitDbId;
	private String observationUnitName;
	private String observationLevel;

	@JsonView(View.PhenotypeBrapiV1_2.class)
	private String observationLevels;

	private String plotNumber;
	private String plantNumber;
	private String blockNumber;
	private String replicate;
	private String germplasmDbId;
	private String germplasmName;
	private String studyDbId;

	private String studyName;

	@JsonView(View.PhenotypeBrapiV1_2.class)
	private String studyLocationDbId;

	@JsonView(View.PhenotypeBrapiV1_2.class)
	private String studyLocation;
	private String programName;

	@JsonView(View.PhenotypeBrapiV1_2.class)
	private String x;

	@JsonView(View.PhenotypeBrapiV1_2.class)
	private String y;
	private String entryType;
	private String entryNumber;

	@JsonView(View.PhenotypeBrapiV1_2.class)
	private List<PhenotypeSearchObservationDTO> observations;

	@JsonView(View.PhenotypeBrapiV1_2.class)
	private String instanceNumber;

	@JsonView(View.PhenotypeBrapiV2.class)
	private String additionalInfo;

	@JsonView(View.PhenotypeBrapiV2.class)
	private String locationDbId;

	@JsonView(View.PhenotypeBrapiV2.class)
	private String locationName;

	@JsonView(View.PhenotypeBrapiV2.class)
	private String observationUnitPUI;

	@JsonView(View.PhenotypeBrapiV2.class)
	private ObservationUnitPosition observationUnitPosition;

	@JsonView(View.PhenotypeBrapiV2.class)
	private List<ObservationUnitXRef> observationUnitXRef;

	@JsonView(View.PhenotypeBrapiV2.class)
	private String programDbId;

	@JsonView(View.PhenotypeBrapiV2.class)
	private List<Treatment> treatments;

	@JsonView(View.PhenotypeBrapiV2.class)
	private String trialDbId;

	@JsonView(View.PhenotypeBrapiV2.class)
	private String trialName;

	public static class ObservationUnitPosition {
		private String blockNumber;
		private String entryNumber;
		private List<String> entryType;
		private GeooCordinates geoCoordinates;
		private String positionCoordinateX;
		private String positionCoordinateXType;
		private String positionCoordinateY;
		private String positionCoordinateYType;
		private String replicate;

		public ObservationUnitPosition() {
		}

		protected class GeooCordinates {
			private GeooCordinates.Geometry geometry;
			private String type;

			protected class Geometry {
				private List<Integer> coordinates;
				private String type;

				public List<Integer> getCoordinates() {
					return this.coordinates;
				}

				public void setCoordinates(final List<Integer> coordinates) {
					this.coordinates = coordinates;
				}

				public String getType() {
					return this.type;
				}

				public void setType(final String type) {
					this.type = type;
				}
			}

			public GeooCordinates.Geometry getGeometry() {
				return this.geometry;
			}

			public void setGeometry(
				final GeooCordinates.Geometry geometry) {
				this.geometry = geometry;
			}

			public String getType() {
				return this.type;
			}

			public void setType(final String type) {
				this.type = type;
			}
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

		public List<String> getEntryType() {
			return this.entryType;
		}

		public void setEntryType(final List<String> entryType) {
			this.entryType = entryType;
		}

		public GeooCordinates getGeoCoordinates() {
			return this.geoCoordinates;
		}

		public void setGeoCoordinates(
			final GeooCordinates geoCoordinates) {
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

	protected class ObservationUnitXRef {
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

	public static class Treatment {
		private String factor;
		private String modality;

		public String getFactor() {
			return this.factor;
		}

		public void setFactor(final String factor) {
			this.factor = factor;
		}

		public String getModality() {
			return this.modality;
		}

		public void setModality(final String modality) {
			this.modality = modality;
		}
	}

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

	public String getInstanceNumber() {
		return this.instanceNumber;
	}

	public void setInstanceNumber(final String instanceNumber) {
		this.instanceNumber = instanceNumber;
	}

	public String getAdditionalInfo() {
		return this.additionalInfo;
	}

	public void setAdditionalInfo(final String additionalInfo) {
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
