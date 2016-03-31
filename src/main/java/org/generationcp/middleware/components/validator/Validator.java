package org.generationcp.middleware.components.validator;

import com.google.common.base.Optional;

/**
 * This interface marks a given class as a validator.
 * A Validator responsibility is to validate a given context and fail if the context is invalid.
 * The Validator makes sure that it will fail if and only if at least one of the validations rules applied fails.
 *
 * A Validator is a special kind of process, in which the target does not evolve, for this reason and for semantic clarity
 * it is differentiated of the processors used in the germplasm file importation refactor.
 *
 * @param <T> Is the target/context to be validated. This context contain all the data needed by a future process.
 *           This context should be immutable which is expected as validations should not change the object to being validated.
 */
public interface Validator<T> {

	ErrorCollection validate (T context);
}
