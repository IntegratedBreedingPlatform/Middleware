
package org.generationcp.middleware.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class InvalidRecords {

	private Map<String, Set<String>> invalidRecordsMap;
	private List<Message> errorMessages;

	public Map<String, Set<String>> getInvalidRecordsMap() {
		return this.invalidRecordsMap;
	}

	public void setInvalidRecordsMap(Map<String, Set<String>> invalidRecordsMap) {
		this.invalidRecordsMap = invalidRecordsMap;
	}

	public List<Message> getErrorMessages() {
		return this.errorMessages;
	}

	public void setErrorMessages(List<Message> errorMessages) {
		this.errorMessages = errorMessages;
	}
}
