package org.generationcp.middleware.service.api.dataset;

import org.pojomatic.annotations.AutoProperty;

import java.util.List;

@AutoProperty
public class ObservationUnitDto {

	private String additionalInfo;
	private String germplasmDbId;
	private String germplasmName;
	private String locationDbId;
	private String locationName;
	private String observationLevel;
	private String observationUnitDbId;
	private String observationUnitName;
	private String observationUnitPUI;
	private ObservationUnitPosition observationUnitPosition;
	private List<ObservationUnitXRef> observationUnitXRef;
	private String plantNumber;
	private String plotNumber;
	private String programDbId;
	private String programName;
	private String studyDbId;
	private String studyName;
	private List<Treatment> treatments;
	private String trialDbId;
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
			private Geometry geometry;
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

			public Geometry getGeometry() {
				return this.geometry;
			}

			public void setGeometry(
				final Geometry geometry) {
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

	protected class Treatment {
		private String factor;
		private String modality;
	}

	public String getAdditionalInfo() {
		return this.additionalInfo;
	}

	public void setAdditionalInfo(final String additionalInfo) {
		this.additionalInfo = additionalInfo;
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

	public String getObservationLevel() {
		return this.observationLevel;
	}

	public void setObservationLevel(final String observationLevel) {
		this.observationLevel = observationLevel;
	}

	public String getObservationUnitDbId() {
		return this.observationUnitDbId;
	}

	public void setObservationUnitDbId(final String observationUnitDbId) {
		this.observationUnitDbId = observationUnitDbId;
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

	public String getPlantNumber() {
		return this.plantNumber;
	}

	public void setPlantNumber(final String plantNumber) {
		this.plantNumber = plantNumber;
	}

	public String getPlotNumber() {
		return this.plotNumber;
	}

	public void setPlotNumber(final String plotNumber) {
		this.plotNumber = plotNumber;
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
}
