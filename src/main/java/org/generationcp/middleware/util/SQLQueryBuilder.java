package org.generationcp.middleware.util;

import org.hibernate.SQLQuery;
import org.hibernate.type.Type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLQueryBuilder {

	private final StringBuilder sqlBuilder;
	private final Map<String, Object> queryParams = new HashMap<>();
	private final List<Scalar> scalars;

	public SQLQueryBuilder(final String sql) {
		this.sqlBuilder = new StringBuilder(sql);
		this.scalars = new ArrayList<>();
	}

	public SQLQueryBuilder(final String sql, final List<Scalar> scalars) {
		this.sqlBuilder = new StringBuilder(sql);
		this.scalars = scalars;
	}

	public void setParameter(final String name, final Object value) {
		this.queryParams.put(name, value);
	}

	public SQLQueryBuilder append(final String query) {
		this.sqlBuilder.append(query);
		return this;
	}

	public String build() {
		return this.sqlBuilder.toString();
	}

	public void addScalarsToQuery(final SQLQuery query) {
		this.scalars.forEach(scalar -> query.addScalar(scalar.getColumnAlias(), scalar.getType()));
	}

	public void addParamsToQuery(final SQLQuery query) {
		this.queryParams.forEach((k, v) -> {
			if (v instanceof Collection) {
				query.setParameterList(k, (Collection) v);
			} else {
				query.setParameter(k, v);
			}
		});
	}

	public List<Scalar> getScalars() {
		return scalars;
	}

	public static class Scalar {

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

}
