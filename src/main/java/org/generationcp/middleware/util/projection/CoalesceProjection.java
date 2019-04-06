package org.generationcp.middleware.util.projection;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.SimpleProjection;
import org.hibernate.type.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CoalesceProjection extends SimpleProjection {

	private final List<String> properties;

	public CoalesceProjection(final String... properties) {
		this.properties = Arrays.asList(properties);
	}

	@Override
	public String toSqlString(final Criteria criteria, final int position, final CriteriaQuery criteriaQuery) throws HibernateException {
		final List<String> aliases = new ArrayList<>();
		for (final String property : this.properties) {
			aliases.add(criteriaQuery.getColumn(criteria, property));
		}
		return String.format("coalesce(%s) as y%s_", StringUtils.join(aliases, ","), position);
	}

	@Override
	public Type[] getTypes(final Criteria criteria, final CriteriaQuery criteriaQuery) throws HibernateException {
		return new Type[] {Hibernate.STRING};
	}
}
