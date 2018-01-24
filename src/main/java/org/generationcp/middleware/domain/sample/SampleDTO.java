package org.generationcp.middleware.domain.sample;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class SampleDTO {

	private Integer sampleId;
	private String sampleName;
	private String sampleBusinessKey;
	private String takenBy;
	private String sampleList;
	private Integer plantNumber;
	private String plantBusinessKey;
	private Set<Dataset> datasets = new HashSet<>();

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	private Date samplingDate;

	public class Dataset {
		private String name;
		private int datasetId;

		public String getName() {
			return name;
		}

		public void setName(final String name) {
			this.name = name;
		}

		public int getDatasetId() {
			return datasetId;
		}

		public void setDatasetId(final int datasetId) {
			this.datasetId = datasetId;
		}

		@Override
		public boolean equals(final Object o) {
			if (this == o) {
				return true;
			}

			if (o == null || getClass() != o.getClass()) {
				return false;
			}

			final Dataset dataset = (Dataset) o;

			return new EqualsBuilder().append(name, dataset.name).append(datasetId, dataset.datasetId)
					.isEquals();
		}

		@Override
		public int hashCode() {
			return new HashCodeBuilder(17, 37).append(name)
					.append(datasetId)
					.toHashCode();
		}
	}

	public SampleDTO() {
	}

	public SampleDTO(final String sampleName, final String sampleBusinessKey, final String takenBy, final Date samplingDate,
		final String sampleList, final Integer plantNumber, final String plantBusinessKey, final Integer sampleId) {
		this.sampleName = sampleName;
		this.sampleBusinessKey = sampleBusinessKey;
		this.takenBy = takenBy;
		this.samplingDate = samplingDate;
		this.sampleList = sampleList;
		this.plantNumber = plantNumber;
		this.plantBusinessKey = plantBusinessKey;
		this.sampleId = sampleId;
	}

	public String getSampleName() {
		return sampleName;
	}

	public void setSampleName(final String sampleName) {
		this.sampleName = sampleName;
	}

	public String getSampleBusinessKey() {
		return sampleBusinessKey;
	}

	public void setSampleBusinessKey(final String sampleBusinessKey) {
		this.sampleBusinessKey = sampleBusinessKey;
	}

	public String getTakenBy() {
		return takenBy;
	}

	public void setTakenBy(final String takenBy) {
		this.takenBy = takenBy;
	}

	public Date getSamplingDate() {
		return samplingDate;
	}

	public void setSamplingDate(final Date samplingDate) {
		this.samplingDate = samplingDate;
	}

	public String getSampleList() {
		return sampleList;
	}

	public void setSampleList(final String sampleList) {
		this.sampleList = sampleList;
	}

	public Integer getPlantNumber() {
		return plantNumber;
	}

	public void setPlantNumber(final Integer plantNumber) {
		this.plantNumber = plantNumber;
	}

	public String getPlantBusinessKey() {
		return plantBusinessKey;
	}

	public void setPlantBusinessKey(final String plantBusinessKey) {
		this.plantBusinessKey = plantBusinessKey;
	}

	public Integer getSampleId() { return sampleId; }

	public void setSampleId(final Integer sampleId) { this.sampleId = sampleId;}

	public Set<Dataset> getDatasets() {
		if (datasets == null) {
			return new HashSet<>();
		}
		return datasets;
	}

	public void setDatasets(final Set<Dataset> datasets) {
		this.datasets = datasets;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final SampleDTO sampleDTO = (SampleDTO) o;

		return new EqualsBuilder().append(sampleName, sampleDTO.sampleName)
			.append(sampleBusinessKey, sampleDTO.sampleBusinessKey)
			.append(takenBy, sampleDTO.takenBy)
			.append(samplingDate, sampleDTO.samplingDate)
			.append(sampleList, sampleDTO.sampleList)
			.append(plantNumber, sampleDTO.plantNumber)
			.append(plantBusinessKey, sampleDTO.plantBusinessKey)
			.append(sampleId, sampleDTO.sampleId)
			.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(sampleName)
			.append(sampleBusinessKey)
			.append(takenBy)
			.append(samplingDate)
			.append(sampleList)
			.append(plantNumber)
			.append(plantBusinessKey)
			.append(sampleId)
			.toHashCode();
	}
}
