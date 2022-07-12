package org.generationcp.middleware.dao;

import org.generationcp.middleware.util.Scalar;
import org.hibernate.Session;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractGenericSearchDAO<T, ID extends Serializable> extends GenericDAO<T, ID>{

	public AbstractGenericSearchDAO(final Session session) {
		super(session);
	}

	protected String addSelectExpression(final List<Scalar> scalars, final String expression, final String columnAlias, final Type scalarType) {
		scalars.add(new Scalar(columnAlias, scalarType));
		return String.format("%s AS `%s`", expression, columnAlias);
	}

	protected String getJoinClause(final Set<String> joins) {
		return joins
			.stream()
			.collect(Collectors.joining("\n"));
	}

}
