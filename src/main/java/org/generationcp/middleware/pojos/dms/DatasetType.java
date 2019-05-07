package org.generationcp.middleware.pojos.dms;

import org.hibernate.annotations.Type;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "dataset_type")
@AutoProperty
public class DatasetType {

	public static final int STUDY_CONDITIONS = 1;
	public static final int MEANS_DATA = 2;
	public static final int SUMMARY_DATA = 3;
	public static final int PLOT_DATA = 4;
	public static final int PLANT_SUBOBSERVATIONS = 5;
	public static final int QUADRAT_SUBOBSERVATIONS = 6;
	public static final int TIME_SERIES_SUBOBSERVATIONS = 7;
	public static final int CUSTOM_SUBOBSERVATIONS = 8;
	public static final int SUB_SAMPLE_DATA = 9;
	public static final int WEATHER_DATA = 10;
	public static final int MEANS_OVER_TRIAL_INSTANCES = 11;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "dataset_type_id")
	private Integer datasetTypeId;

	@Basic(optional = false)
	@Column(name = "name")
	private String name;

	@Basic(optional = false)
	@Column(name = "description")
	private String description;

	@Basic(optional = true)
	@Column(name = "cvterm_id")
	private Integer cvTermId;

	@Type(type = "org.hibernate.type.NumericBooleanType")
	@Basic(optional = false)
	@Column(name = "is_subobs_type", columnDefinition = "TINYINT")
	private boolean isSubObservationType;

	public DatasetType() { }

	public DatasetType(final int datasetTypeId) {
		this.setDatasetTypeId(datasetTypeId);
	}

	public Integer getDatasetTypeId() {
		return this.datasetTypeId;
	}

	public void setDatasetTypeId(final Integer datasetTypeId) {
		this.datasetTypeId = datasetTypeId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public Integer getCvTermId() {
		return this.cvTermId;
	}

	public void setCvTermId(final Integer cvTermId) {
		this.cvTermId = cvTermId;
	}

	public boolean isSubObservationType() {
		return this.isSubObservationType;
	}

	public boolean isObservationType() {
		return this.datasetTypeId == PLOT_DATA;
	}

	public void setSubObservationType(final boolean subObservationType) {
		this.isSubObservationType = subObservationType;
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
