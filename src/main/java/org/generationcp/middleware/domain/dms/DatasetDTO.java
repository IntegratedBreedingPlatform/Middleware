package org.generationcp.middleware.domain.dms;

import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.service.impl.study.StudyInstance;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.io.Serializable;
import java.util.List;

@AutoProperty
public class DatasetDTO extends DatasetBasicDTO implements Serializable {

	private static final long serialVersionUID = 736579292676142736L;


	private List<StudyInstance> instances;
	private List<MeasurementVariable> variables;
	private Boolean hasPendingData;
	private Boolean hasOutOfSyncData;

	public DatasetDTO(){
		super();
	}

	public DatasetDTO(final Integer datasetId) {
		super(datasetId);
	}

	public List<StudyInstance> getInstances() {
		return this.instances;
	}

	public void setInstances(final List<StudyInstance> instances) {
		this.instances = instances;
	}

	public List<MeasurementVariable> getVariables() {
		return this.variables;
	}

	public void setVariables(final List<MeasurementVariable> variables) {
		this.variables = variables;
	}

	public Boolean getHasPendingData() {
		return this.hasPendingData;
	}

	public void setHasPendingData(final Boolean hasPendingData) {
		this.hasPendingData = hasPendingData;
	}

	public Boolean getHasOutOfSyncData() {
		return this.hasOutOfSyncData;
	}

	public void setHasOutOfSyncData(final Boolean hasOutOfSyncData) {
		this.hasOutOfSyncData = hasOutOfSyncData;
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
