
package org.generationcp.middleware.components.validator;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.common.collect.Lists;

/**
 * Creates the chain of {@link Executable}s and wraps the executable chain execution thru its executeAll method.
 *
 * By default this class provides a "fail fast" chain, this is: the first failing validation
 * will interrupt the chain.
 *
 * An iterator is provided to facilitate new implementations that does not interrupts the executable chain on execution failures,
 * thus allowing to execute all the executables and fail last.
 */
public class ExecutableCollection<T> implements Iterable<Executable<T>> {
	private ArrayList<Executable<T>> executables = Lists.newArrayList();

	/**
	 * Adds a new Executable to the executable chain
	 *
	 * @param executable the next business rule (validation) for the chain
	 */
	public void add(Executable<T> executable) {
		executables.add(executable);
	}

	/**
	 * Wraps the executable chain execution
	 *
	 * @param target the context object with data for the execution
	 * @throws ExecutionException indicates the executable chain has failed
	 */
	public T executeAll(T target) throws ExecutionException {
		for (Executable<T> executable : executables) {
			executable.execute(target);
		}
		return target;
	}

	/**
	 * Returns an iterator over a set of elements of type Executable.
	 *
	 * @return an Iterator.
	 */
	@Override
	public Iterator<Executable<T>> iterator() {
		return executables.iterator();
	}
}
