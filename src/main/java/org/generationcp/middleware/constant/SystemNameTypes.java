package org.generationcp.middleware.constant;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum SystemNameTypes {

	LNAME("LNAME"),
	CODE1("CODE1"),
	CODE2("CODE2"),
	CODE3("CODE3"),
	PED("PED"),
	CRSNM("CRSNM"),
	DRVNM("DRVNM"),
	SELHISFIX("SELHISFIX"),
	PUI("PUI");

	private final String type;

	SystemNameTypes(final String type) {
		this.type = type;
	}

	public String getType() {
		return this.type;
	}

	public static Set<String> getTypes() {
		return Arrays.stream(SystemNameTypes.values()).map(SystemNameTypes::getType).collect(Collectors.toSet());
	}
}


