
package org.generationcp.middleware.ruleengine.stockid;

import org.springframework.stereotype.Component;

/**
 * This class serves as an implementation of the BreederIdentifierGenerationStrategy that assumes that the value of the breeder identifier
 * is supplied by the user prior to the execution of the stock ID generation process instead of a system generated value.
 * 
 * The IllegalStateException thrown by the class enforces this assumption; at this point, the rule execution context should already have a
 * value for breeder identifier and hence does not need to call the implementation.
 */

@Component
public class UserSpecifiedBreederIdentifierGenerationStrategy implements BreederIdentifierGenerationStrategy {

	@Override
	public String generateBreederIdentifier() {
		throw new IllegalStateException("User specified breeder identifiers should be set into the RuleExecutionContext "
				+ "programatically before executing this rule");
	}
}
