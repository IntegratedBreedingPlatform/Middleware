package org.generationcp.middleware.util;

import org.hibernate.type.Type;

public class Scalar {

	private final String columnAlias;
	private final Type type;

	public Scalar(final String columnAlias) {
		this.columnAlias = columnAlias;
		this.type = null;
	}

	public Scalar(final String columnAlias, final Type type) {
		this.columnAlias = columnAlias;
		this.type = type;
	}

	public String getColumnAlias() {
		return columnAlias;
	}

	public Type getType() {
		return type;
	}

}
