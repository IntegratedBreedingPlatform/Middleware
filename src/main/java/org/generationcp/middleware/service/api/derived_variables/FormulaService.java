package org.generationcp.middleware.service.api.derived_variables;

import com.google.common.base.Optional;
import org.generationcp.middleware.domain.ontology.FormulaDto;

/**
 * Formula used by Derived Variables
 */
public interface FormulaService {

	public Optional<FormulaDto> getByTargetId(Integer targetId);

}
