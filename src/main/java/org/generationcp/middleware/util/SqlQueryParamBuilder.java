package org.generationcp.middleware.util;

import org.hibernate.SQLQuery;

import java.util.Collection;

/**
 * <p>
 *     Utility object to simplify SQLQuery optional params building.
 *     Allows to reuse the same method for building the query
 *     and setting the parameters after the query has been built
 * </p>
 * <pre>
 *     if (!paramList.isEmpty()) {
 *     		builder.append(" and param in (:paramList)");
 *     		builder.setParameterList("paramList", paramList);
 *     }
 * </pre>
 */
public class SqlQueryParamBuilder {

	private SQLQuery sqlQuery;
	private StringBuilder sqlBuilder;

	public SqlQueryParamBuilder(final SQLQuery sqlQuery) {
		this.sqlQuery = sqlQuery;
	}

	public SqlQueryParamBuilder(final StringBuilder sqlBuilder) {
		this.sqlBuilder = sqlBuilder;
	}

	public SqlQueryParamBuilder append(final String sql) {
		if (this.sqlBuilder != null) {
			this.sqlBuilder.append(sql);
		}
		return this;
	}

	public void setParameter(final String name, final Object val) {
		if (this.sqlQuery != null) {
			this.sqlQuery.setParameter(name, val);
		}
	}

	public void setParameterList(final String name, final Collection vals) {
		if (this.sqlQuery != null) {
			this.sqlQuery.setParameterList(name, vals);
		}
	}
}
