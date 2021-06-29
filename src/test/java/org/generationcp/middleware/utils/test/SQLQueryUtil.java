package org.generationcp.middleware.utils.test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SQLQueryUtil {

	private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static String generateInsertQuery(final String tableName, final Map<String, Object> queryParams) {
		List<String> fields = new ArrayList<>();
		List<String> values = new ArrayList<>();

		queryParams.entrySet().forEach(e -> {
			fields.add(e.getKey());
			values.add(formatValue(e.getValue()));
		});

		return new StringBuilder("INSERT INTO ")
			.append(tableName)
			.append("(")
			.append(fields.stream().collect(Collectors.joining(", ")))
			.append(") VALUES (")
			.append(values.stream().collect(Collectors.joining(", ")))
			.append(")")
			.toString();
	}

	public static String formatValue(final Object value) {
		if (value instanceof String) {
			return String.format("'%s'", value);
		}
		if (value instanceof Date) {
			return String.format("'%s'", DATE_FORMAT.format(value));
		}
		return String.valueOf(value);
	}

}
