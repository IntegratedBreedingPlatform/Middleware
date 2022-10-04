package org.generationcp.middleware.api.brapi.v2.study;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;

@AutoProperty
public class StudyUpdateRequestDTO extends StudyImportRequestDTO {

	private List<String> observationVariableDbIds;

	public List<String> getObservationVariableDbIds() {
		return this.observationVariableDbIds;
	}

	public void setObservationVariableDbIds(final List<String> observationVariableDbIds) {
		this.observationVariableDbIds = observationVariableDbIds;
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
