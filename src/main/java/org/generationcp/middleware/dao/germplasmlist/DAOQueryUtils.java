package org.generationcp.middleware.dao.germplasmlist;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.util.SQLQueryBuilder;
import org.springframework.data.domain.Pageable;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

class DAOQueryUtils {

	static String addOrder(final Function<String, String> sortValueFunction, final SQLQueryBuilder sqlQueryBuilder, final Pageable pageable) {
		if (pageable != null && pageable.getSort() != null) {
			final String orderClause = StreamSupport.stream(pageable.getSort().spliterator(), false)
				.map(order -> String.format(" %s %s ", sortValueFunction.apply(order.getProperty()), order.getDirection()))
				.collect(Collectors.joining(", "));
			if (!StringUtils.isEmpty(orderClause)) {
				sqlQueryBuilder.append(" ORDER BY ").append(orderClause);
			}
		}

		return null;
	}

}
