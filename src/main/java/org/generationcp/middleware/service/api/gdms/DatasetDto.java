package org.generationcp.middleware.service.api.gdms;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;
import java.util.LinkedHashSet;

/**
 * Created by clarysabel on 11/9/17.
 */
@AutoProperty
public class DatasetDto {

	@AutoProperty
	public class SampleKey {

		String sampleUID;

		Integer accesion;

		public SampleKey() {

		}

		public String getSampleUID() {
			return sampleUID;
		}

		public void setSampleUID(final String sampleUID) {
			this.sampleUID = sampleUID;
		}

		public Integer getAccesion() {
			return accesion;
		}

		public void setAccesion(final Integer accesion) {
			this.accesion = accesion;
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

	private String specie;

	private String[][] charValues;

	private List<String> markers;

	private LinkedHashSet<SampleKey> sampleAccesions;

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

	public String[][] getCharValues() {
		return charValues;
	}

	public void setCharValues(final String[][] charValues) {
		this.charValues = charValues;
	}

	public List<String> getMarkers() {
		return markers;
	}

	public void setMarkers(final List<String> markers) {
		this.markers = markers;
	}

	public LinkedHashSet<SampleKey> getSampleAccesions() {
		return sampleAccesions;
	}

	public void setSampleAccesions(final LinkedHashSet<SampleKey> sampleAccesions) {
		this.sampleAccesions = sampleAccesions;
	}

	public String getSpecie() {
		return specie;
	}

	public void setSpecie(final String specie) {
		this.specie = specie;
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
