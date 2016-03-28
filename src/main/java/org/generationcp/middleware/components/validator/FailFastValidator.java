package org.generationcp.middleware.components.validator;

/**
 * Define the {@link Validator} behavior as a "fail fast" validator, meaning that the first failure that occurs while validating interrups
 * the chain execution. If the failure was handled by the {@link Executable} that threw it, the validator throws it again. Otherwise, the
 * Validator catches the failure and throw a managed ExecutionException with a generic validation error.
 *
 * Concrete Validators will inject all the executable ValidationRules that the Validator needs to execute.
 *
 */
public abstract class FailFastValidator<T> implements Validator<T> {

	public static final String INVALID_ELEMENT = "Invalid element";

	protected final ExecutableCollection<T> rules = new ExecutableCollection<T>();

	/**
	 * Executes the executable chain and handles any failures that may arise.
	 *
	 * @param target is the context being validated.
	 * @throws ExecutionException in case of any kind of failure the method throws this exception containing an error message.
	 */
	@Override
	public void validate(T target) throws ExecutionException {
		try {
			rules.executeAll(target);
		} catch (ExecutionException e) {
			throw new ExecutionException(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ExecutionException(INVALID_ELEMENT);
		}
	}

}
