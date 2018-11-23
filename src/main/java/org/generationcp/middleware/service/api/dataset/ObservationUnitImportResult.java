package org.generationcp.middleware.service.api.dataset;

import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;
import org.springframework.validation.BindingResult;

import java.util.List;

@AutoProperty
public class ObservationUnitImportResult {

	private Table<String, String, String> observationUnitRows;

	private List<String> warnings;

	public ObservationUnitImportResult(final Table observationUnitRows, final List<String>  warnings) {
		this.observationUnitRows = observationUnitRows;
		this.warnings = warnings;
	}

	public ObservationUnitImportResult() {
		this.warnings = Lists.newArrayList();
	}

	public Table getObservationUnitRows() {
		return this.observationUnitRows;
	}

	public void setObservationUnitRows(final Table observationUnitRows) {
		this.observationUnitRows = observationUnitRows;
	}

	public List<String> getWarnings() {
		return this.warnings;
	}

	public void setWarnings(final BindingResult warnings) {
		this.warnings = (List<String>) warnings;
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
		this.warnings.add(s);
	}
}
