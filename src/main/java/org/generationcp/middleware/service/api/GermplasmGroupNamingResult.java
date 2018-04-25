
package org.generationcp.middleware.service.api;

import java.util.ArrayList;
import java.util.List;

// FIXME First cut, more structured DTO later.
public class GermplasmGroupNamingResult {

	private final List<String> success = new ArrayList<>();
	private final List<String> errors = new ArrayList<>();

	public List<String> getMessages() {
		List<String> messages = new ArrayList<>();
		messages.addAll(errors);
		messages.addAll(success);
		return messages;
	}

	/**
	 * Get error messages
	 */
	public List<String> getErrors() {
		return errors;
	}

	/**
	 * Get success messages
	 */
	public List<String> getSuccess() {
		return success;
	}

	/**
	 * Add error message
	 */
	public void addError(String error) {
		this.errors.add(error);
	}

	/**
	 * Add success message
	 */
	public void addSuccess(String success) {
		this.success.add(success);
	}

}
