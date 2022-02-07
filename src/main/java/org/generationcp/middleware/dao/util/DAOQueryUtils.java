package org.generationcp.middleware.dao.util;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class DAOQueryUtils {

	public static String getOrderClause(final Function<String, String> sortValueFunction, final Pageable pageable) {
		if (pageable != null && pageable.getSort() != null) {
			final String orderClause = StreamSupport.stream(pageable.getSort().spliterator(), false)
				.map(order -> String.format(" %s %s ", sortValueFunction.apply(order.getProperty()), order.getDirection()))
				.collect(Collectors.joining(", "));
			if (!StringUtils.isEmpty(orderClause)) {
				return " ORDER BY " + orderClause;
			}
		}
		return "";
	}

	public static void addParamsToQuery(final SQLQuery query, final Map<String, Object> queryParams) {
		queryParams.forEach((k, v) -> {
			if (v instanceof Collection) {
				query.setParameterList(k, (Collection) v);
			} else {
				query.setParameter(k, v);
			}
		});
	}

}
