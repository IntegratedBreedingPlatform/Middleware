package org.generationcp.middleware.audit;

import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.oms.CVTerm;
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

public class AttributeAuditIntegrationTest extends AuditIntegrationTestBase {

	private static final String PRIMARY_KEY_FIELD = "aid";

	private DaoFactory daoFactory;

	public AttributeAuditIntegrationTest() {
		super(Attribute.class.getAnnotation(Table.class).name(), PRIMARY_KEY_FIELD);
	}

	@Before
	public void setUp() {
		this.daoFactory = new DaoFactory(this.sessionProvder);
	}

	@Test
	public void shouldTriggersExists() {
		this.checkTriggerExists("trigger_atributs_aud_insert", "INSERT");
		this.checkTriggerExists("trigger_atributs_aud_update", "UPDATE");
		this.checkTriggerExists("trigger_atributs_aud_delete", "DELETE");
	}

	@Test
	public void shouldAuditInsertAndUpdate() {
		this.enableEntityAudit();

		//Create a attribute 1
		Map<String, Object> insertAttribute1QueryParams = this.createQueryParams(null, null);
		this.insertEntity(insertAttribute1QueryParams);

		final Integer attribute1Aid = this.getLastInsertIdFromEntity();
		assertThat(this.countEntityAudit(attribute1Aid), is(1));

		final LinkedHashSet<String> fieldNames = this.getSelectAuditFieldNames(insertAttribute1QueryParams.keySet());

		//Assert recently created attribute
		final Map<String, Object> insertAudit = this.getLastAudit(fieldNames);
		this.assertAudit(insertAudit, insertAttribute1QueryParams, 0, attribute1Aid);

		//Disable audit
		this.disableEntityAudit();

		//Insert another attribute
		Map<String, Object> insertAttribute2QueryParams = this.createQueryParams(null, null);
		this.insertEntity(insertAttribute2QueryParams);

		//Because the audit was disabled, the attribute shouldn't be audited
		final Integer attribute2Aid = this.getLastInsertIdFromEntity();
		assertThat(this.countEntityAudit(attribute2Aid), is(0));

		//Enable audit
		this.enableEntityAudit();

		//Update the attribute 1
		final Map<String, Object> updateAttribute1QueryParams1 =
			this.createQueryParams(new Random().nextInt(), new Date());
		this.updateEntity(updateAttribute1QueryParams1, attribute1Aid);

		assertThat(this.countEntityAudit(attribute1Aid), is(2));

		//Assert recently updated attribute
		final Map<String, Object> updateAudit = this.getLastAudit(fieldNames);
		this.assertAudit(updateAudit, updateAttribute1QueryParams1, 1, attribute1Aid);

		//Disable audit
		this.disableEntityAudit();

		//Update again the entity, because the audit was disable shouldn't be audited
		final Map<String, Object> updateAttribute1QueryParams2 =
			this.createQueryParams(new Random().nextInt(), new Date());
		this.updateEntity(updateAttribute1QueryParams2, attribute1Aid);

		assertThat(this.countEntityAudit(attribute1Aid), is(2));

		//Enable audit
		this.enableEntityAudit();

		this.deleteEntity(attribute1Aid);
		assertThat(this.countEntityAudit(attribute1Aid), is(3));

		//Assert recently deleted attribute
		final Map<String, Object> deletedAudit = this.getLastAudit(fieldNames);
		this.assertAudit(deletedAudit, updateAttribute1QueryParams2, 2, attribute1Aid);

		//Disable audit
		this.disableEntityAudit();

		//Delete attribute 2, because the audit was disable shouldn't be audited
		this.deleteEntity(attribute2Aid);
		assertThat(this.countEntityAudit(attribute2Aid), is(0));
	}

	protected Map<String, Object> createQueryParams(final Integer modifiedBy, final Date modifiedDate) {
		final CVTerm cvTerm = daoFactory.getCvTermDao().getTermsByCvId(CvId.VARIABLES, 0, 1).get(0);
		final Map<String, Object> queryParams = new LinkedHashMap<>();
		queryParams.put("gid", new Random().nextInt());
		queryParams.put("atype", cvTerm.getCvTermId());
		queryParams.put("created_by", new Random().nextInt());
		queryParams.put("aval", UUID.randomUUID().toString());
		queryParams.put("cval_id", cvTerm.getCvTermId());
		queryParams.put("alocn", new Random().nextInt());
		queryParams.put("aref", new Random().nextInt());
		queryParams.put("adate", new Random().nextInt());
		queryParams.put("modified_by", modifiedBy);
		queryParams.put("created_date", new Date());
		queryParams.put("modified_date", modifiedDate);
		return queryParams;
	}

	private void assertAudit(final Map<String, Object> audit, final Map<String, Object> entity, final int revType,
		final int aid) {
		assertThat(new Integer(audit.get("rev_type").toString()), is(revType));
		assertThat(audit.get("gid"), is(entity.get("gid")));
		assertThat(audit.get("atype"), is(entity.get("atype")));
		assertThat(audit.get("created_by"), is(entity.get("created_by")));
		assertThat(audit.get("aval"), is(entity.get("aval")));
		assertThat(audit.get("cval_id"), is(entity.get("cval_id")));
		assertThat(audit.get("alocn"), is(entity.get("alocn")));
		assertThat(audit.get("aref"), is(entity.get("aref")));
		assertThat(audit.get("adate"), is(entity.get("adate")));
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
