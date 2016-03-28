package org.generationcp.middleware.components.validator;

/**
 * Represents a minimal execution that has meaning by itself.
 *
 * @param <T> target is the context object that contains all the data needed for the execution.
 *           This target content may evolve/change accordingly to each executable in the {@link ExecutableCollection}.
 */
public interface Executable<T> {

	public T execute(T target) throws ExecutionException;
}
