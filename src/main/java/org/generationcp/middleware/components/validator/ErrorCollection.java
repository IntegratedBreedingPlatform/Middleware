package org.generationcp.middleware.components.validator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The ErrorCollection contains a list of errors found in a {@link Validator}
 *
 */
public class ErrorCollection implements Iterable<String>{
	private List<String> errors = new ArrayList<>();

	public int size() {
		return errors.size();
	}

	public boolean isEmpty() {
		return errors.isEmpty();
	}

	public boolean add(String s) {
		return errors.add(s);
	}

	@Override
	public Iterator<String> iterator() {
		return errors.iterator();
	}
}
