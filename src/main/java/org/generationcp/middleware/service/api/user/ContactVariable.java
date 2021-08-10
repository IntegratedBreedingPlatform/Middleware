package org.generationcp.middleware.service.api.user;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ContactVariable {

	CONTACT_NAME(8116, "CONTACT_NAME"),
	CONTACT_EMAIL(8117, "CONTACT_EMAIL"),
	CONTACT_ORG(8118, "CONTACT_ORG"),
	CONTACT_TYPE(8119, "CONTACT_TYPE");

	private String name;
	private Integer id;

	private ContactVariable(final Integer id, final String name) {
		this.id = id;
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public static List<Integer> getIds() {
		return Arrays.stream(values()).map(ContactVariable::getId).collect(Collectors.toList());
	}

	public static List<String> getNames() {
		return Arrays.stream(values()).map(ContactVariable::getName).collect(Collectors.toList());
	}
}
