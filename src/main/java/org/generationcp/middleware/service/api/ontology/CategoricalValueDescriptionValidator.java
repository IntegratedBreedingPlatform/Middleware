package org.generationcp.middleware.service.api.ontology;

import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.MeasurementVariable;

import java.util.List;
import java.util.stream.Collectors;

public class CategoricalValueDescriptionValidator extends  CategoricalValueNameValidator {

	@Override
	List<String> getPossibleValues(final MeasurementVariable variable) {
		return variable.getPossibleValues().stream().map(ValueReference::getDisplayDescription).collect(
			Collectors.toList());
	}
}
