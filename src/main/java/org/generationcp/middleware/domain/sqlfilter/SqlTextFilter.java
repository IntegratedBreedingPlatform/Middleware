package org.generationcp.middleware.domain.sqlfilter;

public class SqlTextFilter {

	public static enum Type {
		STARTSWITH, EXACTMATCH, CONTAINS
	}

	private String value;
	private Type type;

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
}
