package org.generationcp.middleware.components.validator;

/**
 * Handle a failure providing a human readable error message.
 */
public class ExecutionException extends Exception {

	/**
	 * Create the exception providing a message
	 * @param message human readable error message
	 */
	public ExecutionException(String message) {
		super(message);
	}
}
