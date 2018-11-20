package org.generationcp.middleware.service.api.dataset;

import com.google.common.collect.Lists;
import org.generationcp.middleware.domain.dataset.DatasetGeneratorInput;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@AutoProperty
public class ObservationUnitImportResult {

	private List<ObservationUnitRow> observationUnitRows;

	private BindingResult warnings;

	public ObservationUnitImportResult(final List<ObservationUnitRow> observationUnitRows, final BindingResult warnings) {
		this.observationUnitRows = observationUnitRows;
		this.warnings = warnings;
	}

	public ObservationUnitImportResult() {
		this.warnings = new MapBindingResult(new HashMap<String, String>(), DatasetGeneratorInput.class.getName());
		this.observationUnitRows = Lists.newArrayList();
	}

	public List<ObservationUnitRow> getObservationUnitRows() {
		return this.observationUnitRows;
	}

	public void setObservationUnitRows(final List<ObservationUnitRow> observationUnitRows) {
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
