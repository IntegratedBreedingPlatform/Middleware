package org.generationcp.middleware.audit;

import org.generationcp.middleware.manager.DaoFactory;
import org.junit.Before;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public abstract class GenericExternalReferenceAuditIntegrationTest extends AuditIntegrationTestBase {

	private static final String PRIMARY_KEY_FIELD = "id";

	private DaoFactory daoFactory;
	private final String foreignKeyField;

	public GenericExternalReferenceAuditIntegrationTest(final String tableName, final String foreignKeyField) {
		super(tableName, PRIMARY_KEY_FIELD);
		this.foreignKeyField = foreignKeyField;
	}

	@Before
	public void setUp() {
		this.daoFactory = new DaoFactory(this.sessionProvder);
	}

	public void checkAllTriggers() {
		this.checkTriggerExists("trigger_" + this.tableName + "_aud_insert", "INSERT");
		this.checkTriggerExists("trigger_" + this.tableName + "_aud_update", "UPDATE");
		this.checkTriggerExists("trigger_" + this.tableName + "_aud_delete", "DELETE");
	}

	public void checkAuditInsertAndUpdate(final Integer testRecordId) {
		this.enableEntityAudit();

		//Create a externalReference 1
		final Map<String, Object> insertExternalReference1QueryParams = this.createQueryParams(testRecordId, null, null);
		this.insertEntity(insertExternalReference1QueryParams);

		final Integer externalReference1Id = this.getLastInsertIdFromEntity();
		assertThat(this.countEntityAudit(externalReference1Id), is(1));

		final LinkedHashSet<String> fieldNames = this.getSelectAuditFieldNames(insertExternalReference1QueryParams.keySet());

		//Assert recently created externalReference
		final Map<String, Object> insertAudit = this.getLastAudit(fieldNames);
		this.assertAudit(insertAudit, insertExternalReference1QueryParams, 0, externalReference1Id);

		//Disable audit
		this.disableEntityAudit();

		//Insert another externalReference
		final Map<String, Object> insertExternalReference2QueryParams = this.createQueryParams(testRecordId, null, null);
		this.insertEntity(insertExternalReference2QueryParams);

		//Because the audit was disabled, the externalReference shouldn't be audited
		final Integer externalReference2Id = this.getLastInsertIdFromEntity();
		assertThat(this.countEntityAudit(externalReference2Id), is(0));

		//Enable audit
		this.enableEntityAudit();

		//Update the externalReference 1
		final Map<String, Object> updateExternalReference1QueryParams1 =
			this.createQueryParams(testRecordId, new Random().nextInt(), new Date());
		this.updateEntity(updateExternalReference1QueryParams1, externalReference1Id);

		assertThat(this.countEntityAudit(externalReference1Id), is(2));

		//Assert recently updated externalReference
		final Map<String, Object> updateAudit = this.getLastAudit(fieldNames);
		this.assertAudit(updateAudit, updateExternalReference1QueryParams1, 1, externalReference1Id);

		//Disable audit
		this.disableEntityAudit();

		//Update again the entity, because the audit was disable shouldn't be audited
		final Map<String, Object> updateExternalReference1QueryParams2 =
			this.createQueryParams(testRecordId, new Random().nextInt(), new Date());
		this.updateEntity(updateExternalReference1QueryParams2, externalReference1Id);

		assertThat(this.countEntityAudit(externalReference1Id), is(2));

		//Enable audit
		this.enableEntityAudit();

		this.deleteEntity(externalReference1Id);
		assertThat(this.countEntityAudit(externalReference1Id), is(3));

		//Assert recently deleted externalReference
		final Map<String, Object> deletedAudit = this.getLastAudit(fieldNames);
		this.assertAudit(deletedAudit, updateExternalReference1QueryParams2, 2, externalReference1Id);

		//Disable audit
		this.disableEntityAudit();

		//Delete externalReference 2, because the audit was disable shouldn't be audited
		this.deleteEntity(externalReference2Id);
		assertThat(this.countEntityAudit(externalReference2Id), is(0));
	}

	protected Map<String, Object> createQueryParams(final Integer idVal,
		final Integer modifiedBy, final Date modifiedDate) {
		final Map<String, Object> queryParams = new LinkedHashMap<>();
		queryParams.put(this.foreignKeyField, idVal);
		queryParams.put("reference_id", UUID.randomUUID().toString());
		queryParams.put("reference_source", UUID.randomUUID().toString());
		queryParams.put("created_date", new Date());
		queryParams.put("created_by", new Random().nextInt());
		queryParams.put("modified_by", modifiedBy);
		queryParams.put("modified_date", modifiedDate);
		return queryParams;
	}

	private void assertAudit(final Map<String, Object> audit, final Map<String, Object> entity, final int revType,
		final int aid) {
		assertThat(new Integer(audit.get("rev_type").toString()), is(revType));
		assertThat(audit.get(this.foreignKeyField), is(entity.get(this.foreignKeyField)));
		assertThat(audit.get("reference_id"), is(entity.get("reference_id")));
		assertThat(audit.get("reference_source"), is(entity.get("reference_source")));
		assertThat(DATE_FORMAT.format(audit.get("created_date")), is(DATE_FORMAT.format(entity.get(("created_date")))));
		assertThat(audit.get("created_by"), is(entity.get("created_by")));
		if (audit.get("modified_date") == null) {
			assertThat(audit.get("modified_date"), is(entity.get("modified_date")));
		} else {
			assertThat(DATE_FORMAT.format(audit.get("modified_date")), is(DATE_FORMAT.format(entity.get("modified_date"))));
		}
		assertThat(audit.get("modified_by"), is(entity.get("modified_by")));
		assertThat(audit.get(PRIMARY_KEY_FIELD), is(aid));
		assertNotNull(audit.get(AUDIT_PRIMARY_KEY_FIELD));
	}

}
