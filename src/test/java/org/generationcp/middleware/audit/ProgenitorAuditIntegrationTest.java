package org.generationcp.middleware.audit;

import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Progenitor;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.Table;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Random;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class ProgenitorAuditIntegrationTest extends AuditIntegrationTestBase {

	private static final String PRIMARY_KEY_FIELD = "id";

	private DaoFactory daoFactory;

	public ProgenitorAuditIntegrationTest() {
		super(Progenitor.class.getAnnotation(Table.class).name(), PRIMARY_KEY_FIELD);
	}

	@Before
	public void setUp() {
		this.daoFactory = new DaoFactory(this.sessionProvder);
	}

	@Test
	public void shouldTriggersExists() {
		this.checkTriggerExists("trigger_progntrs_aud_insert", "INSERT");
		this.checkTriggerExists("trigger_progntrs_aud_update", "UPDATE");
		this.checkTriggerExists("trigger_progntrs_aud_delete", "DELETE");
	}

	@Test
	public void shouldAuditInsertAndUpdateAndDelete() {
		final Germplasm germplasm = new GermplasmTestDataInitializer().createGermplasmWithPreferredName("LNAME");
		this.daoFactory.getGermplasmDao().save(germplasm);

		this.sessionProvder.getSession().flush();

		this.enableEntityAudit();

		//Create a progenitor 1
		Map<String, Object> insertProgenitor1QueryParams = this.createQueryParams(germplasm.getGid(), null, null);
		this.insertEntity(insertProgenitor1QueryParams);

		final Integer progenitor1Aid = this.getLastInsertIdFromEntity();
		assertThat(this.countEntityAudit(progenitor1Aid), is(1));

		final LinkedHashSet<String> fieldNames = this.getSelectAuditFieldNames(insertProgenitor1QueryParams.keySet());

		//Assert recently created progenitor
		final Map<String, Object> insertAudit = this.getLastAudit(fieldNames);
		this.assertAudit(insertAudit, insertProgenitor1QueryParams, 0, progenitor1Aid);

		//Disable audit
		this.disableEntityAudit();

		//Insert another progenitor
		Map<String, Object> insertProgenitor2QueryParams = this.createQueryParams(germplasm.getGid(), null, null);
		this.insertEntity(insertProgenitor2QueryParams);

		//Because the audit was disabled, the progenitor shouldn't be audited
		final Integer progenitor2Aid = this.getLastInsertIdFromEntity();
		assertThat(this.countEntityAudit(progenitor2Aid), is(0));

		//Enable audit
		this.enableEntityAudit();

		//Update the progenitor 1
		final Map<String, Object> updateProgenitor1QueryParams1 =
			this.createQueryParams(germplasm.getGid(), new Random().nextInt(), new Date());
		this.updateEntity(updateProgenitor1QueryParams1, progenitor1Aid);

		assertThat(this.countEntityAudit(progenitor1Aid), is(2));

		//Assert recently updated progenitor
		final Map<String, Object> updateAudit = this.getLastAudit(fieldNames);
		this.assertAudit(updateAudit, updateProgenitor1QueryParams1, 1, progenitor1Aid);

		//Disable audit
		this.disableEntityAudit();

		//Update again the entity, because the audit was disable shouldn't be audited
		final Map<String, Object> updateProgenitor1QueryParams2 =
			this.createQueryParams(germplasm.getGid(), new Random().nextInt(), new Date());
		this.updateEntity(updateProgenitor1QueryParams2, progenitor1Aid);

		assertThat(this.countEntityAudit(progenitor1Aid), is(2));

		//Enable audit
		this.enableEntityAudit();

		this.deleteEntity(progenitor1Aid);
		assertThat(this.countEntityAudit(progenitor1Aid), is(3));

		//Assert recently deleted progenitor
		final Map<String, Object> deletedAudit = this.getLastAudit(fieldNames);
		this.assertAudit(deletedAudit, updateProgenitor1QueryParams2, 2, progenitor1Aid);

		//Disable audit
		this.disableEntityAudit();

		//Delete progenitor 2, because the audit was disable shouldn't be audited
		this.deleteEntity(progenitor2Aid);
		assertThat(this.countEntityAudit(progenitor2Aid), is(0));
	}

	protected Map<String, Object> createQueryParams(final Integer gid, final Integer modifiedBy, final Date modifiedDate) {
		final Map<String, Object> queryParams = new LinkedHashMap<>();
		queryParams.put("gid", gid);
		queryParams.put("pno", new Random().nextInt());
		queryParams.put("pid", new Random().nextInt());
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
		assertThat(audit.get("pno"), is(entity.get("pno")));
		assertThat(audit.get("pid"), is(entity.get("pid")));
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
