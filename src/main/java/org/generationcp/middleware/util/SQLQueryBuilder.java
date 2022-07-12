package org.generationcp.middleware.util;

import org.generationcp.middleware.dao.util.DAOQueryUtils;
import org.hibernate.SQLQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLQueryBuilder {

	private final StringBuilder sqlBuilder;
	private final List<Scalar> scalars = new ArrayList<>();
	private final Map<String, Object> queryParams = new HashMap<>();

	public SQLQueryBuilder(final String sql) {
		this.sqlBuilder = new StringBuilder(sql);
	}

	public void addScalar(final Scalar scalar) {
		this.scalars.add(scalar);
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
		DAOQueryUtils.addParamsToQuery(query, this.queryParams);
	}

	public void addScalarsToQuery(final SQLQuery query) {
		this.scalars.forEach(scalar -> query.addScalar(scalar.getColumnAlias(), scalar.getType()));
	}

}
