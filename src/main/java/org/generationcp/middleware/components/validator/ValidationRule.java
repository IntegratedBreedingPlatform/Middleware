package org.generationcp.middleware.components.validator;

import com.google.common.base.Optional;

/**
 * This interface marks a class as a component in a {@link Validator}.
 *
 * A Validator may have more than one validation rules. Each ValidationRule validates just one part of the validation problem.
 * Meaning that if we were to validate the unicity of imported germplasm, the format of coded names if any, its genealogy,
 * its heritage, each one of those would be a different ValidationRule.
 *
 * When a ValidationRule validates a 'T' target/context then returns an {@link Optional} error message. If the optional
 * message contains a message then the rule failed and the context is invalid. Otherwise, (if the  optional message is absent)
 * the context is valid. This way each validation rule can return a custom error message if needed.
 *
 * In the future the error messages might a evolve into messages-key that might need parameters, in that case the only
 * action required is to change the content of the optional for another which contains the key and a list of parameters.
 *
 * @param <T> target to be validated
 */
public interface ValidationRule<T> {

	public Optional<String> validate(T target);
}
