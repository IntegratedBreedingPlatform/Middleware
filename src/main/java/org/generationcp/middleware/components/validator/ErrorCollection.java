package org.generationcp.middleware.components.validator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The ErrorCollection contains a list of errors found in a {@link Validator}
 *
 */
public class ErrorCollection implements Iterable<ErrorMessage>{
	private List<ErrorMessage> errors = new ArrayList<>();

	public int size() {
		return errors.size();
	}

	public boolean isEmpty() {
		return errors.isEmpty();
	}

	public boolean add(ErrorMessage errorMessage) {
		return errors.add(errorMessage);
	}

	@Override
	public Iterator<ErrorMessage> iterator() {
		return errors.iterator();
	}
}
