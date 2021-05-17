package org.generationcp.middleware.audit;

import org.generationcp.middleware.IntegrationTestBase;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public abstract class AuditIntegrationTestBase extends IntegrationTestBase {

	protected final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	protected final static String AUDIT_PRIMARY_KEY_FIELD = "aud_id";
	protected final static String REV_TYPE_FIELD = "rev_type";

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

	protected void enableEntityAudit() {
		this.changeEntityAuditConfiguration(true);
	}

	protected void disableEntityAudit() {
		this.changeEntityAuditConfiguration(false);
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

	protected Map<String, Object> getLastAudit(final Set<String> fieldNames) {
		final String fields = fieldNames.stream().collect(Collectors.joining(","));
		final String sql = String.format("SELECT %s FROM %s_aud order by aud_id DESC LIMIT 1", fields, this.tableName);
		final SQLQuery sqlQuery = this.sessionProvder.getSession().createSQLQuery(sql);
		sqlQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return (Map<String, Object>) sqlQuery.uniqueResult();
	}

	protected void insertEntity(final Map<String, Object> queryParams) {
		List<String> fields = new ArrayList<>();
		List<String> values = new ArrayList<>();

		queryParams.entrySet().forEach(e -> {
			fields.add(e.getKey());
			values.add(this.formatValue(e.getValue()));
		});

		final String sql = new StringBuilder("INSERT INTO ")
			.append(this.tableName)
			.append("(")
			.append(fields.stream().collect(Collectors.joining(", ")))
			.append(") VALUES (")
			.append(values.stream().collect(Collectors.joining(", ")))
			.append(")")
			.toString();

		this.sessionProvder.getSession().createSQLQuery(sql).executeUpdate();
	}

	protected void updateEntity(final Map<String, Object> queryParams, final Integer primaryKeyValue) {
		StringBuilder sqlBuilder = new StringBuilder("UPDATE ")
			.append(this.tableName)
			.append(" SET ");

		String assignments = queryParams.entrySet().stream().map(e -> String.format("%s = %s", e.getKey(), this.formatValue(e.getValue()))).collect(
			Collectors.joining(", "));

		sqlBuilder.append(assignments)
			.append(" WHERE ")
			.append(String.format("%s = %s", this.primaryKeyField, primaryKeyValue));

		this.sessionProvder.getSession().createSQLQuery(sqlBuilder.toString()).executeUpdate();
	}

	protected LinkedHashSet<String> getFieldNames(final Set<String> fields) {
		final LinkedHashSet<String> fieldNames = new LinkedHashSet<>();
		fieldNames.addAll(fields);
		fieldNames.add(this.primaryKeyField);
		fieldNames.add(AUDIT_PRIMARY_KEY_FIELD);
		fieldNames.add(REV_TYPE_FIELD);
		return fieldNames;
	}

	private String formatValue(final Object value) {
		if (value instanceof String) {
			return String.format("'%s'", value);
		}
		if (value instanceof Date) {
			return String.format("'%s'", DATE_FORMAT.format(value));
		}
		return String.valueOf(value);
	}

	private void changeEntityAuditConfiguration(final boolean isAudited) {
		final SQLQuery sqlQuery =
			this.sessionProvder.getSession().createSQLQuery("UPDATE audit_cfg SET is_audited = :isAudited WHERE entity_name = :entityName");
		sqlQuery.setParameter("isAudited", isAudited);
		sqlQuery.setParameter("entityName", this.tableName);
		sqlQuery.executeUpdate();
	}

}
