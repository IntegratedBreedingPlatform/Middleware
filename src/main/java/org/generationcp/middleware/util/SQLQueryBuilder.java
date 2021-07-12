package org.generationcp.middleware.util;

import org.hibernate.SQLQuery;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SQLQueryBuilder {

	private final StringBuilder sqlBuilder;
	private final Map<String, Object> queryParams = new HashMap<>();

	public SQLQueryBuilder(final String sql) {
		this.sqlBuilder = new StringBuilder(sql);
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

	public void addParamsToQuery(final SQLQuery query) {
		this.queryParams.forEach((k, v) -> {
			if (v instanceof Collection) {
				query.setParameterList(k, (Collection) v);
			} else {
				query.setParameter(k, v);
			}
		});
	}

}
