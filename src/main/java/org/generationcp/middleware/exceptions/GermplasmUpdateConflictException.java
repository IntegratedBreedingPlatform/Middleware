package org.generationcp.middleware.exceptions;

import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.stream.Collectors;

public class GermplasmUpdateConflictException extends Exception {

	private final List<ObjectError> errors;

	public GermplasmUpdateConflictException(final List<String> errors) {
		this.errors = errors.stream().map(s -> new ObjectError("", s)).collect(Collectors.toList());
	}

	public List<ObjectError> getErrors() {
		return this.errors;
	}

}
