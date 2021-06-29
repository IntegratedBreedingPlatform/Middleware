package org.generationcp.middleware.api.brapi.v2.observationunit;

import org.generationcp.middleware.service.api.phenotype.ObservationUnitDto;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;
import org.springframework.validation.ObjectError;

import java.util.List;

@AutoProperty
public class ObservationUnitImportResponse {

	private String status;
	private List<ObjectError> errors;
	private List<ObservationUnitDto> observationUnits;

	public ObservationUnitImportResponse() {

	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(final String status) {
		this.status = status;
	}

	public List<ObjectError> getErrors() {
		return this.errors;
	}

	public void setErrors(final List<ObjectError> errors) {
		this.errors = errors;
	}

	public List<ObservationUnitDto> getObservationUnits() {
		return this.observationUnits;
	}

	public void setObservationUnits(final List<ObservationUnitDto> phenotypeSearchDTOS) {
		this.observationUnits = phenotypeSearchDTOS;
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

}
