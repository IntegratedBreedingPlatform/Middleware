package org.generationcp.middleware.service.api.ontology;

import org.generationcp.middleware.domain.ontology.DataType;

import java.util.Optional;

public interface VariableDataValidatorFactory {

	Optional<VariableValueValidator> getValidator(final DataType dataType);

	void registerDataTypeValidator(final DataType dataType, final VariableValueValidator validator);

}
