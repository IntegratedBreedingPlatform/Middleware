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

	@Type(type = "org.hibernate.type.NumericBooleanType")
	@Basic(optional = false)
	@Column(name = "is_subobs_type", columnDefinition = "TINYINT")
	private boolean isSubObservationType;

	@Type(type = "org.hibernate.type.NumericBooleanType")
	@Basic(optional = false)
	@Column(name = "is_obs_type", columnDefinition = "TINYINT")
	private boolean isObservationType;

	public DatasetType() {
	}

	public DatasetType(final int datasetTypeId) {
		this.setDatasetTypeId(datasetTypeId);
	}

	public DatasetType(final int datasetTypeId, final String name) {
		this.setDatasetTypeId(datasetTypeId);
		this.setName(name);
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

	public boolean isSubObservationType() {
		return this.isSubObservationType;
	}

	public void setSubObservationType(final boolean subObservationType) {
		this.isSubObservationType = subObservationType;
	}

	public boolean isObservationType() {
		return this.isObservationType;
	}

	public void setObservationType(final boolean observationType) {
		this.isObservationType = observationType;
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
