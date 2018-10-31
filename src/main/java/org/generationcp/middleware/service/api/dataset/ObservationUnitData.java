package org.generationcp.middleware.service.api.dataset;

import org.generationcp.middleware.pojos.dms.Phenotype;

import java.util.Objects;

public class ObservationUnitData {

	private Integer observationId;

	private Integer categoricalValueId;

	private String value;

	private Phenotype.ValueStatus status;

	public ObservationUnitData(final Integer observationId, final Integer categoricalValueId, final String value,
		final Phenotype.ValueStatus status) {
		this.observationId = observationId;
		this.categoricalValueId = categoricalValueId;
		this.value = value;
		this.status = status;
	}

	public ObservationUnitData() {
	}

	public ObservationUnitData(final String value) {
		this.value = value;
	}

	public Integer getObservationId() {
		return this.observationId;
	}

	public void setObservationId(final Integer observationId) {
		this.observationId = observationId;
	}

	public Integer getCategoricalValueId() {
		return this.categoricalValueId;
	}

	public void setCategoricalValueId(final Integer categoricalValueId) {
		this.categoricalValueId = categoricalValueId;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public Phenotype.ValueStatus getStatus() {
		return this.status;
	}

	public void setStatus(final Phenotype.ValueStatus status) {
		this.status = status;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (!(o instanceof ObservationUnitData))
			return false;
		final ObservationUnitData that = (ObservationUnitData) o;
		return Objects.equals(this.getObservationId(), that.getObservationId()) &&
			Objects.equals(this.getCategoricalValueId(), that.getCategoricalValueId()) &&
			Objects.equals(this.getValue(), that.getValue()) &&
			this.getStatus() == that.getStatus();
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getObservationId(), this.getCategoricalValueId(), this.getValue(), this.getStatus());
	}

	@Override
	public String toString() {
		return "ObservationUnitData{" +
			"observationId=" + this.observationId +
			", categoricalValueId=" + this.categoricalValueId +
			", value='" + this.value + '\'' +
			", status=" + this.status +
			'}';
	}
}
