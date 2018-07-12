package org.generationcp.middleware.util.projection;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.SimpleProjection;
import org.hibernate.type.Type;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ConcatProperties extends SimpleProjection {

	private final List<String> properties = new ArrayList<>();
	private String separator = "";

	public ConcatProperties(final String separator, final String... property) {
		for (final String item : property) {
			this.properties.add(item);
		}
		this.separator = separator;
	}

	@Override
	public String toSqlString(final Criteria criteria, final int i, final CriteriaQuery criteriaQuery) {

		final StringBuilder builder = new StringBuilder();
		builder.append("concat(");

		final Iterator<String> iterator = properties.iterator();
		while (iterator.hasNext()) {
			builder.append(criteriaQuery.getColumn(criteria, iterator.next()));
			if (iterator.hasNext()) {
				builder.append(String.format(",'%s',", separator));
			}
		}
		builder.append(") as y").append(i).append('_');

		return builder.toString();
	}

	@Override
	public Type[] getTypes(final Criteria criteria, final CriteriaQuery criteriaQuery) {
		return new Type[] {Hibernate.STRING};
	}
}
