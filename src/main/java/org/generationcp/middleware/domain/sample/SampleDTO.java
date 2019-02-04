package org.generationcp.middleware.domain.sample;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

public class SampleDTO {

	private Integer sampleId;
	private Integer sampleNumber;
	private Integer entryNo;
	private Integer gid;
	private String designation;
	private String sampleName;
	private String sampleBusinessKey;
	private String takenBy;
	private String sampleList;
	private String plateId;
	private String well;
	private String datasetType;
	private String studyName;
	private Integer studyId;
	private String enumerator;
	private String observationUnitId;
	private Set<Dataset> datasets = new HashSet<>();

	// FIXME Jackson use UTC as default timezone
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	private Date samplingDate;

	public class Dataset {
		private String name;
		private int datasetId;

		public Dataset(final Integer datasetId, final String name) {
			this.datasetId = datasetId;
			this.name = name;
		}


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
		final String sampleList, final Integer sampleId) {
		this.sampleName = sampleName;
		this.sampleBusinessKey = sampleBusinessKey;
		this.takenBy = takenBy;
		this.samplingDate = samplingDate;
		this.sampleList = sampleList;
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

	public Integer getSampleId() { return sampleId; }

	public void setSampleId(final Integer sampleId) { this.sampleId = sampleId;}

	public Integer getEntryNo() {
		return entryNo;
	}

	public void setEntryNo(Integer entryNo) {
		this.entryNo = entryNo;
	}

	public Integer getGid() {
		return gid;
	}

	public void setGid(Integer gid) {
		this.gid = gid;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getPlateId() {
		return plateId;
	}

	public void setPlateId(String plateId) {
		this.plateId = plateId;
	}

	public String getWell() {
		return well;
	}

	public void setWell(String well) {
		this.well = well;
	}

	public String getDatasetType() {
		return datasetType;
	}

	public void setDatasetType(final String datasetType) {
		this.datasetType = datasetType;
	}

	public String getStudyName() {
		return studyName;
	}

	public void setStudyName(final String studyName) {
		this.studyName = studyName;
	}

	public String getEnumerator() {
		return enumerator;
	}

	public void setEnumerator(final String enumerator) {
		this.enumerator = enumerator;
	}

	public String getObservationUnitId() {
		return observationUnitId;
	}

	public void setObservationUnitId(final String observationUnitId) {
		this.observationUnitId = observationUnitId;
	}

	public Set<Dataset> getDatasets() {
		if (datasets == null) {
			return new HashSet<>();
		}
		return datasets;
	}

	public void setDatasets(final Set<Dataset> datasets) {
		this.datasets = datasets;
	}

	public void addDataset(final Integer datasetId, final String datasetName) {

		if (null == this.datasets) {
			this.datasets = new HashSet<>();
		}

		final Dataset dataset = new Dataset(datasetId, datasetName);
		if (!this.datasets.contains(dataset)) {
			this.datasets.add(dataset);
		}
	}

	public Integer getStudyId() {
		return studyId;
	}

	public void setStudyId(final Integer studyId) {
		this.studyId = studyId;
	}

	public Integer getSampleNumber() {
		return sampleNumber;
	}

	public void setSampleNumber(final Integer sampleNumber) {
		this.sampleNumber = sampleNumber;
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

		return new EqualsBuilder()
			.append(sampleId, sampleDTO.sampleId)
			.append(entryNo, sampleDTO.entryNo)
			.append(gid, sampleDTO.gid)
			.append(designation, sampleDTO.designation)
			.append(sampleName, sampleDTO.sampleName)
			.append(sampleBusinessKey, sampleDTO.sampleBusinessKey)
			.append(takenBy, sampleDTO.takenBy)
			.append(sampleList, sampleDTO.sampleList)
			.append(datasets, sampleDTO.datasets)
			.append(samplingDate, sampleDTO.samplingDate)
			.append(datasetType, sampleDTO.datasetType)
			.append(studyName, sampleDTO.studyName)
			.append(enumerator, sampleDTO.enumerator)
			.append(observationUnitId, sampleDTO.observationUnitId)
			.append(studyId, sampleDTO.studyId)
			.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
			.append(sampleId)
			.append(entryNo)
			.append(gid)
			.append(designation)
			.append(sampleName)
			.append(sampleBusinessKey)
			.append(takenBy)
			.append(sampleList)
			.append(datasets)
			.append(samplingDate)
			.append(datasetType)
			.append(studyName)
			.append(enumerator)
			.append(observationUnitId)
			.append(studyId)
			.toHashCode();
	}
}
