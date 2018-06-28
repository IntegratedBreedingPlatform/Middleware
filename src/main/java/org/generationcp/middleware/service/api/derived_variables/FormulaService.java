package org.generationcp.middleware.service.api.derived_variables;

import org.generationcp.middleware.pojos.derived_variables.Formula;

/**
 * Formula used by Derived Variables
 */
public interface FormulaService {

	public Formula getByTargetId(Integer targetId);

}
