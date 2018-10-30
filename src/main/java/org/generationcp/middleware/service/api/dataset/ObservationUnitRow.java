package org.generationcp.middleware.service.api.dataset;

import java.util.Map;
import java.util.Objects;

public class ObservationUnitRow {

	private Integer observationUnitId;

	private Integer gid;

	private String designation;

	private String action;

	private Map<String, ObservationUnitData> variables;

	public ObservationUnitRow() {

	}

	public Integer getObservationUnitId() {
		return this.observationUnitId;
	}

	public void setObservationUnitId(final Integer observationUnitId) {
		this.observationUnitId = observationUnitId;
	}

	public Integer getGid() {
		return this.gid;
	}

	public void setGid(final Integer gid) {
		this.gid = gid;
	}

	public String getDesignation() {
		return this.designation;
	}

	public void setDesignation(final String designation) {
		this.designation = designation;
	}

	public String getAction() {
		return this.action;
	}

	public void setAction(final String action) {
		this.action = action;
	}

	public Map<String, ObservationUnitData> getVariables() {
		return this.variables;
	}

	public void setVariables(final Map<String, ObservationUnitData> variables) {
		this.variables = variables;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (!(o instanceof ObservationUnitRow))
			return false;
		final ObservationUnitRow that = (ObservationUnitRow) o;
		return Objects.equals(this.getObservationUnitId(), that.getObservationUnitId()) &&
			Objects.equals(this.getGid(), that.getGid()) &&
			Objects.equals(this.getDesignation(), that.getDesignation()) &&
			Objects.equals(this.getAction(), that.getAction()) &&
			Objects.equals(this.getVariables(), that.getVariables());
	}

	@Override
	public int hashCode() {
		return Objects.hash(
			this.getObservationUnitId(),
			this.getGid(),
			this.getDesignation(),
			this.getAction(),
			this.getVariables());
	}

	@Override
	public String toString() {
		return "ObservationUnitRow{" +
			"observationUnitId=" + this.observationUnitId +
			", gid=" + this.gid +
			", designation='" + this.designation + '\'' +
			", action='" + this.action + '\'' +
			", variables=" + this.variables +
			'}';
	}
}


