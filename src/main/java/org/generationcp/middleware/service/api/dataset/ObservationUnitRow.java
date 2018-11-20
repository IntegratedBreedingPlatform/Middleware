package org.generationcp.middleware.service.api.dataset;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AutoProperty
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

	public Map<String, ObservationUnitData> getMeasuredVariables(final List<Integer> selectionMethodsAndTraitsIds) {
		Map<String, ObservationUnitData> observationUnitDataCollection = new HashMap<>();
		for (String variable : this.variables.keySet()) {
			final ObservationUnitData observationUnitData = this.variables.get(variable);
			final Integer variableId = observationUnitData.getVariableId();
			if (selectionMethodsAndTraitsIds.contains(variableId)) {
				observationUnitDataCollection.put(variable, observationUnitData);
			}
		}

		return observationUnitDataCollection;
	}

	public void setVariables(final Map<String, ObservationUnitData> variables) {
		this.variables = variables;
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}
}


