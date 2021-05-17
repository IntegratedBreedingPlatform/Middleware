package org.generationcp.middleware.audit;

import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.ExternalReference;
import org.generationcp.middleware.pojos.Germplasm;
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

public class ExternalReferenceAuditIntegrationTest extends AuditIntegrationTestBase {

	private static final String PRIMARY_KEY_FIELD = "id";

	private DaoFactory daoFactory;

	public ExternalReferenceAuditIntegrationTest() {
		super(ExternalReference.class.getAnnotation(Table.class).name(), PRIMARY_KEY_FIELD);
	}

	@Before
	public void setUp() {
		this.daoFactory = new DaoFactory(this.sessionProvder);
	}

	@Test
	public void shouldTriggersExists() {
		this.checkTriggerExists("trigger_external_reference_aud_insert", "INSERT");
		this.checkTriggerExists("trigger_external_reference_aud_update", "UPDATE");
	}

	@Test
	public void shouldAuditInsertAndUpdate() {
		final Germplasm germplasm = new GermplasmTestDataInitializer().createGermplasmWithPreferredName("LNAME");
		this.daoFactory.getGermplasmDao().save(germplasm);

		this.enableEntityAudit();

		//Create a externalReference 1
		Map<String, Object> insertExternalReference1QueryParams = this.createQueryParams(germplasm.getGid(), null, null);
		this.insertEntity(insertExternalReference1QueryParams);

		final Integer externalReference1Aid = this.getLastInsertIdFromEntity();
		assertThat(this.countEntityAudit(externalReference1Aid), is(1));

		final LinkedHashSet<String> fieldNames = this.getSelectAuditFieldNames(insertExternalReference1QueryParams.keySet());

		//Assert recently created externalReference
		final Map<String, Object> insertAudit = this.getLastAudit(fieldNames);
		this.assertAudit(insertAudit, insertExternalReference1QueryParams, 0, externalReference1Aid);

		//Disable audit
		this.disableEntityAudit();

		//Insert another externalReference
		Map<String, Object> insertExternalReference2QueryParams = this.createQueryParams(germplasm.getGid(), null, null);
		this.insertEntity(insertExternalReference2QueryParams);

		//Because the audit was disabled, the externalReference shouldn't be audited
		final Integer externalReference2Aid = this.getLastInsertIdFromEntity();
		assertThat(this.countEntityAudit(externalReference2Aid), is(0));

		//Enable audit
		this.enableEntityAudit();

		//Update the externalReference 1
		final Map<String, Object> updateExternalReference1QueryParams1 =
			this.createQueryParams(germplasm.getGid(), new Random().nextInt(), new Date());
		this.updateEntity(updateExternalReference1QueryParams1, externalReference1Aid);

		assertThat(this.countEntityAudit(externalReference1Aid), is(2));

		//Assert recently updated externalReference
		final Map<String, Object> updateAudit = this.getLastAudit(fieldNames);
		this.assertAudit(updateAudit, updateExternalReference1QueryParams1, 1, externalReference1Aid);

		//Disable audit
		this.disableEntityAudit();

		//Update again the entity, because the audit was disable shouldn't be audited
		final Map<String, Object> updateExternalReference1QueryParams2 =
			this.createQueryParams(germplasm.getGid(), new Random().nextInt(), new Date());
		this.updateEntity(updateExternalReference1QueryParams2, externalReference1Aid);

		assertThat(this.countEntityAudit(externalReference1Aid), is(2));
	}

	protected Map<String, Object> createQueryParams(final Integer gid, final Integer modifiedBy, final Date modifiedDate) {
		final Map<String, Object> queryParams = new LinkedHashMap<>();
		queryParams.put("gid", gid);
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
		assertThat(audit.get("gid"), is(entity.get("gid")));
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
