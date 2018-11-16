package org.generationcp.middleware.service.api.dataset;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;

@AutoProperty
public class ObservationUnitImportResult {

	private List<ObservationUnitRow> observationUnitRows;

	private List<String> errors;

	private Integer notFoundCounter;

	public ObservationUnitImportResult(final List<ObservationUnitRow> observationUnitRows, final List<String> errors, final Integer notFoundCounter) {
		this.observationUnitRows = observationUnitRows;
		this.errors = errors;
		this.notFoundCounter = notFoundCounter;
	}

	public ObservationUnitImportResult() {

	}

	public List<ObservationUnitRow> getObservationUnitRows() {
		return this.observationUnitRows;
	}

	public void setObservationUnitRows(final List<ObservationUnitRow> observationUnitRows) {
		this.observationUnitRows = observationUnitRows;
	}

	public List<String> getErrors() {
		return this.errors;
	}

	public void setErrors(final List<String> errors) {
		this.errors = errors;
	}

	public Integer getNotFoundCounter() {
		return this.notFoundCounter;
	}

	public void setNotFoundCounter(final Integer notFoundCounter) {
		this.notFoundCounter = notFoundCounter;
	}

	public void addNotFound() {
		this.notFoundCounter++;
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
