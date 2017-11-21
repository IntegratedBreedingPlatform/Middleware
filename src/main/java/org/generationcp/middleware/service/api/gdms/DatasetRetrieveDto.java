package org.generationcp.middleware.service.api.gdms;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Map;

/**
 * Created by clarysabel on 11/16/17.
 */
@AutoProperty
public class DatasetRetrieveDto {

	@AutoProperty
	public class CharValueDto {

		private String sampleUID;

		private Integer accession;

		private String sampleName;

		private Integer gid;

		private String designation;

		private Integer plantNumber;

		private Map<String, String> charValues = new HashMap<>();

		public String getSampleUID() {
			return sampleUID;
		}

		public void setSampleUID(final String sampleUID) {
			this.sampleUID = sampleUID;
		}

		public Integer getAccession() {
			return accession;
		}

		public void setAccession(final Integer accession) {
			this.accession = accession;
		}

		public String getSampleName() {
			return sampleName;
		}

		public void setSampleName(final String sampleName) {
			this.sampleName = sampleName;
		}

		public Integer getGid() {
			return gid;
		}

		public void setGid(final Integer gid) {
			this.gid = gid;
		}

		public String getDesignation() {
			return designation;
		}

		public void setDesignation(final String designation) {
			this.designation = designation;
		}

		public Integer getPlantNumber() {
			return plantNumber;
		}

		public void setPlantNumber(final Integer plantNumber) {
			this.plantNumber = plantNumber;
		}

		public Map<String, String> getCharValues() {
			return charValues;
		}

		public void setCharValues(final Map<String, String> charValues) {
			this.charValues = charValues;
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
		public boolean equals(Object o) {
			return Pojomatic.equals(this, o);
		}

	}

	private String name;

	private String description;

	private String type;

	private String genus;

	private String remarks;

	private String dataType;

	private String missingData;

	private String method;

	private String score;

	private Integer userId;

	private String species;

	private Date uploadDate;

	private List<CharValueDto> charValueDtos = new ArrayList<>();

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(final String type) {
		this.type = type;
	}

	public String getGenus() {
		return genus;
	}

	public void setGenus(final String genus) {
		this.genus = genus;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(final String remarks) {
		this.remarks = remarks;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(final String dataType) {
		this.dataType = dataType;
	}

	public String getMissingData() {
		return missingData;
	}

	public void setMissingData(final String missingData) {
		this.missingData = missingData;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(final String method) {
		this.method = method;
	}

	public String getScore() {
		return score;
	}

	public void setScore(final String score) {
		this.score = score;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(final Integer userId) {
		this.userId = userId;
	}

	public String getSpecies() {
		return species;
	}

	public void setSpecies(final String species) {
		this.species = species;
	}

	public Date getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(final Date uploadDate) {
		this.uploadDate = uploadDate;
	}

	public List<CharValueDto> getCharValueDtos() {
		return charValueDtos;
	}

	public void setCharValueDtos(final List<CharValueDto> charValueDtos) {
		this.charValueDtos = charValueDtos;
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
	public boolean equals(Object o) {
		return Pojomatic.equals(this, o);
	}

}
