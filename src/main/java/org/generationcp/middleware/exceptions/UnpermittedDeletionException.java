package org.generationcp.middleware.exceptions;

/**
 * The exception is for the case when deletion operation is not permitted
 * as in case when the User is not an owner of the entity being deleted
 */

public class UnpermittedDeletionException extends Exception {

	public UnpermittedDeletionException(String message) {
		super(message);
	}

	public UnpermittedDeletionException(String message, Throwable cause) {
		super(message, cause);
	}
}
