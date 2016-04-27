
package org.generationcp.middleware.service.api;

import java.util.ArrayList;
import java.util.List;

// FIXME First cut, more structured DTO later.
public class GermplasmGroupNamingResult {

	private final List<String> messages = new ArrayList<>();

	public List<String> getMessages() {
		return messages;
	}

	public void addMessage(String message) {
		this.messages.add(message);
	}

}
