package org.generationcp.middleware.exceptions;

public class PersonNotFoundException extends Exception {

	public PersonNotFoundException() {
		super();
	}

	public PersonNotFoundException(final String message) {
		super(message);
	}
}
