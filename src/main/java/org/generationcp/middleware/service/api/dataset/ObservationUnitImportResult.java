package org.generationcp.middleware.service.api.dataset;

import org.generationcp.middleware.domain.dataset.DatasetGeneratorInput;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@AutoProperty
public class ObservationUnitImportResult {

	private Map<String, Map<String, String>> observationUnitRows;

	private BindingResult warnings;

	public ObservationUnitImportResult(final Map<String, Map<String, String>> observationUnitRows, final BindingResult warnings) {
		this.observationUnitRows = observationUnitRows;
		this.warnings = warnings;
	}

	public ObservationUnitImportResult() {
		this.warnings = new MapBindingResult(new HashMap<String, String>(), DatasetGeneratorInput.class.getName());
		this.observationUnitRows = Collections.emptyMap();
	}

	public Map<String, Map<String, String>> getObservationUnitRows() {
		return this.observationUnitRows;
	}

	public void setObservationUnitRows(final Map<String, Map<String, String>> observationUnitRows) {
		this.observationUnitRows = observationUnitRows;
	}

	public BindingResult getWarnings() {
		return this.warnings;
	}

	public void setWarnings(final BindingResult warnings) {
		this.warnings = warnings;
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

	public void addWarning(final String s) {
		this.warnings.reject(s);
	}
}
