
package org.generationcp.middleware.ruleengine.stockid;

/**
 * This interface is used to represent possible implementations for generating breeder identifiers to be used in the generation of stock ID
 * values
 */
public interface BreederIdentifierGenerationStrategy {

	String generateBreederIdentifier();
}
