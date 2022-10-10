package org.generationcp.middleware.ruleengine.resolver;

/**
 * Interface to be implemented by classes that act as value resolvers for various placeholders in key code templates.
 *
 */
public interface KeyComponentValueResolver {

	/**
	 * Revolve the value of the key code component.
	 */
	String resolve();

	/**
	 * Specify whether the value that this resolver resolves, is required or optional. If optional, the key code generation service will do
	 * the substitution (with a separator) only when the value returned by the {@link #resolve()} contract is not null/empty. Example of
	 * such optional behavior is SELECTION_NUMBER component used in seed source where ear/plants selected is only added to the key in case
	 * multiple plant/ears are selected from a single plot.
	 */
	boolean isOptional();
}
