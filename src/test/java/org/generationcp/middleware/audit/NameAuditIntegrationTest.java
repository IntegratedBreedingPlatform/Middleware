package org.generationcp.middleware.audit;

import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.pojos.Name;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.Table;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class NameAuditIntegrationTest extends AuditIntegrationTestBase {

	private static final String PRIMARY_KEY_FIELD = "nid";

	public NameAuditIntegrationTest() {
		super(Name.class.getAnnotation(Table.class).name(), PRIMARY_KEY_FIELD);
	}

	@Before
	public void setUp() {

	}

	@Test
	public void shouldTriggersExists() {
		this.checkTriggerExists("trigger_names_aud_insert", "INSERT");
		this.checkTriggerExists("trigger_names_aud_update", "UPDATE");
		this.checkTriggerExists("trigger_names_aud_delete", "DELETE");
	}

	@Test
	public void shouldAuditInsertAndUpdate() {
		this.enableEntityAudit();

		//Create a name 1
		Map<String, Object> insertName1QueryParams = this.createQueryParams(null, null);
		this.insertEntity(insertName1QueryParams);

		final Integer name1Nid = this.getLastInsertIdFromEntity();
		assertThat(this.countEntityAudit(name1Nid), is(1));

		final LinkedHashSet<String> fieldNames = this.getSelectAuditFieldNames(insertName1QueryParams.keySet());

		//Assert recently created name
		final Map<String, Object> insertAudit = this.getLastAudit(fieldNames);
		this.assertAudit(insertAudit, insertName1QueryParams, 0, name1Nid);

		//Disable audit
		this.disableEntityAudit();

		//Insert another name
		Map<String, Object> insertName2QueryParams = this.createQueryParams(null, null);
		this.insertEntity(insertName2QueryParams);

		//Because the audit was disabled, the name shouldn't be audited
		final Integer name2Nid = this.getLastInsertIdFromEntity();
		assertThat(this.countEntityAudit(name2Nid), is(0));

		//Enable audit
		this.enableEntityAudit();

		//Update the name 1
		final Map<String, Object> updateName1QueryParams1 =
			this.createQueryParams(new Random().nextInt(), new Date());
		this.updateEntity(updateName1QueryParams1, name1Nid);

		assertThat(this.countEntityAudit(name1Nid), is(2));

		//Assert recently updated name
		final Map<String, Object> updateAudit = this.getLastAudit(fieldNames);
		this.assertAudit(updateAudit, updateName1QueryParams1, 1, name1Nid);

		//Disable audit
		this.disableEntityAudit();

		//Update again the entity, because the audit was disable shouldn't be audited
		final Map<String, Object> updateName1QueryParams2 =
			this.createQueryParams(new Random().nextInt(), new Date());
		this.updateEntity(updateName1QueryParams2, name1Nid);

		assertThat(this.countEntityAudit(name1Nid), is(2));

		//Enable audit
		this.enableEntityAudit();

		this.deleteEntity(name1Nid);
		assertThat(this.countEntityAudit(name1Nid), is(3));

		//Assert recently deleted name
		final Map<String, Object> deletedAudit = this.getLastAudit(fieldNames);
		this.assertAudit(deletedAudit, updateName1QueryParams2, 2, name1Nid);

		//Disable audit
		this.disableEntityAudit();

		//Delete name 2, because the audit was disable shouldn't be audited
		this.deleteEntity(name2Nid);
		assertThat(this.countEntityAudit(name2Nid), is(0));
	}

	protected Map<String, Object> createQueryParams(final Integer modifiedBy, final Date modifiedDate) {
		final Map<String, Object> queryParams = new LinkedHashMap<>();
		queryParams.put("gid", new Random().nextInt());
		queryParams.put("ntype", GermplasmNameType.DERIVATIVE_NAME.getUserDefinedFieldID());
		queryParams.put("nstat", new Random().nextInt());
		queryParams.put("created_by", new Random().nextInt());
		queryParams.put("nval", UUID.randomUUID().toString());
		queryParams.put("nlocn", new Random().nextInt());
		queryParams.put("ndate", new Random().nextInt());
		queryParams.put("nref", new Random().nextInt());
		queryParams.put("modified_by", modifiedBy);
		queryParams.put("created_date", new Date());
		queryParams.put("modified_date", modifiedDate);
		return queryParams;
	}

	private void assertAudit(final Map<String, Object> audit, final Map<String, Object> entity, final int revType,
		final int aid) {
		assertThat(new Integer(audit.get("rev_type").toString()), is(revType));
		assertThat(audit.get("gid"), is(entity.get("gid")));
		assertThat(audit.get("ntype"), is(entity.get("ntype")));
		assertThat(audit.get("nstat"), is(entity.get("nstat")));
		assertThat(audit.get("created_by"), is(entity.get("created_by")));
		assertThat(audit.get("nval"), is(entity.get("nval")));
		assertThat(audit.get("nlocn"), is(entity.get("nlocn")));
		assertThat(audit.get("ndate"), is(entity.get("ndate")));
		assertThat(audit.get("nref"), is(entity.get("nref")));
		assertThat(audit.get("modified_by"), is(entity.get("modified_by")));
		assertThat(DATE_FORMAT.format(audit.get("created_date")), is(DATE_FORMAT.format(entity.get(("created_date")))));
		if (audit.get("modified_date") == null) {
			assertThat(audit.get("modified_date"), is(entity.get("modified_date")));
		} else {
			assertThat(DATE_FORMAT.format(audit.get("modified_date")), is(DATE_FORMAT.format(entity.get("modified_date"))));
		}
		assertThat(audit.get(PRIMARY_KEY_FIELD), is(aid));
		assertNotNull(audit.get(AUDIT_PRIMARY_KEY_FIELD));
	}

}
