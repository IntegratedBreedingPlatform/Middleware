package org.generationcp.middleware.domain.dms;

import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.service.impl.study.StudyInstance;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.io.Serializable;
import java.util.List;

@AutoProperty
public class DatasetDTO implements Serializable {

	private static final long serialVersionUID = 736579292676142736L;

	private Integer datasetId;
	private Integer datasetTypeId;
	private String name;
	private Integer parentDatasetId;
	private List<StudyInstance> instances;
	private List<MeasurementVariable> variables;

	public DatasetDTO(){

	}

	public DatasetDTO(final Integer datasetId) {
		this();
		this.datasetId = datasetId;
	}

	public Integer getDatasetId() {
		return datasetId;
	}

	public void setDatasetId(final Integer datasetId) {
		this.datasetId = datasetId;
	}

	public Integer getDatasetTypeId() {
		return datasetTypeId;
	}

	public void setDatasetTypeId(final Integer datasetTypeId) {
		this.datasetTypeId = datasetTypeId;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Integer getParentDatasetId() {
		return parentDatasetId;
	}

	public void setParentDatasetId(final Integer parentDatasetId) {
		this.parentDatasetId = parentDatasetId;
	}

	public List<StudyInstance> getInstances() {
		return instances;
	}

	public void setInstances(final List<StudyInstance> instances) {
		this.instances = instances;
	}

	public List<MeasurementVariable> getVariables() {
		return variables;
	}

	public void setVariables(final List<MeasurementVariable> variables) {
		this.variables = variables;
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
