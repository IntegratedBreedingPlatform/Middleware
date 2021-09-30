package org.generationcp.middleware.domain.sqlfilter;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SqlTextFilter {

	public enum Type {
		STARTSWITH, EXACTMATCH, CONTAINS, ENDSWITH
	}

	private String value;
	private Type type = Type.STARTSWITH;

	public String getValue() {
		return this.value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public Type getType() {
		return this.type;
	}

	public void setType(final Type type) {
		this.type = type;
	}

	@JsonIgnore
	public boolean isEmpty() {
		return this.value == null && this.type == null;
	}

}
