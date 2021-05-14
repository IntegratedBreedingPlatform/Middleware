package org.generationcp.middleware.audit;

import org.generationcp.middleware.IntegrationTestBase;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public abstract class AuditIntegrationTestBase extends IntegrationTestBase {

	protected final String tableName;
	protected final String primaryKeyField;

	public AuditIntegrationTestBase(final String tableName, final String primaryKeyField) {
		this.tableName = tableName;
		this.primaryKeyField = primaryKeyField;
	}

	protected void checkTriggerExists(final String triggerName, final String action) {
		final Object[] trigger = (Object[]) this.sessionProvder
			.getSession()
			.createSQLQuery(
				String.format(
					"SELECT EVENT_OBJECT_TABLE, TRIGGER_NAME, EVENT_MANIPULATION, ACTION_TIMING FROM information_schema.TRIGGERS WHERE TRIGGER_NAME = '%s'",
					triggerName))
			.uniqueResult();
		assertNotNull(trigger);
		assertThat(trigger.length, is(4));
		assertThat(trigger[0], is(this.tableName));
		assertThat(trigger[1], is(triggerName));
		assertThat(trigger[2], is(action));
		assertThat(trigger[3], is("AFTER"));
	}

	protected int countEntityAudit(final Integer primaryKeyValue) {
		final String sql =
			String.format("SELECT COUNT(1) FROM %s_aud WHERE %s = %s", this.tableName, this.primaryKeyField, primaryKeyValue);
		return ((BigInteger) this.sessionProvder.getSession().createSQLQuery(sql).uniqueResult()).intValue();
	}

	protected int getLastInsertIdFromEntity() {
		final String sql = String.format("SELECT MAX(%s) FROM %s", this.primaryKeyField, this.tableName);
		return (Integer) this.sessionProvder.getSession().createSQLQuery(sql).uniqueResult();
	}

	protected String generateInsertQuery(final List<QueryParam> queryParams) {
		List<String> fields = new ArrayList<>();
		List<String> values = new ArrayList<>();

		queryParams.forEach(queryParam -> {
			fields.add(queryParam.fieldName);
			values.add(queryParam.formatValue());
		});

		return new StringBuilder("INSERT INTO ")
			.append(this.tableName)
			.append("(")
			.append(fields.stream().collect(Collectors.joining(",")))
			.append(") VALUES (")
			.append(values.stream().collect(Collectors.joining(",")))
			.append(")")
			.toString();
	}

	protected static class QueryParam {

		private final String fieldName;
		private final Object value;

		public QueryParam(final String fieldName, final Object value) {
			this.fieldName = fieldName;
			this.value = value;
		}

		public String formatValue() {
			if (value instanceof String) {
				return String.format("'%s'", value);
			}
			if (value instanceof Date) {
				return String.format("'%s'", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value));
			}
			return String.valueOf(value);
		}

	}

}
