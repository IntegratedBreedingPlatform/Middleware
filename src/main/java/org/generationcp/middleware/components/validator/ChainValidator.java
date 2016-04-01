package org.generationcp.middleware.components.validator;

import java.util.List;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

/**
 * Define the {@link Validator} behavior as a {@link ValidationRule} chain, meaning that all the validations done
 * by the Validator are encapsulated as ValidationRule components. Then, in order to validate a context, the
 * ChainValidator needs to iterate thru the list of rules and execute them against the context.
 *
 * Every rule returns on its name an {@link Optional} String with an error message, therefore while iterating thru
 * all the rules if the error is present then the error message is added to the {@link ErrorCollection} then the
 * iteration proceeds. Otherwise, if no error is present, the iteration also proceeds.
 *
 * If the target is valid, meaning that all the validation rules in the chain were executed and all of them return
 * an absent message then Validator returns an empty {@link ErrorCollection}.
 *
 * Concrete Validators will inject all the ValidationRules that the Validator needs to execute.
 *
 */
public abstract class ChainValidator<T> implements Validator<T> {

	/**
	 * The usage of the list is encapsulated and hidden to avoid missuse.
	 */
	private List<ValidationRule<T>> rules= Lists.newArrayList();

	/**
	 * Executes the executable chain and handles any failures that may arise.
	 *
	 * @param target is the context being validated.
	 */
	@Override
	public ErrorCollection validate(T target) {
		ErrorCollection errors = new ErrorCollection();

		for (ValidationRule<T> rule : rules) {
			Optional<ErrorMessage> errorValidationMessage = rule.validate(target);
			if (errorValidationMessage.isPresent()) {
				errors.add(errorValidationMessage.get());
			}
		}

		return errors;
	}

	/**
	 * Adds a new ValidationRule to the rules chain.
	 *
	 * @param rule the next business rule (validation) for the chain
	 */
	public void add(ValidationRule<T> rule) {
		rules.add(rule);
	}

}
