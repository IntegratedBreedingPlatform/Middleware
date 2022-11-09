package org.generationcp.middleware.audit;

import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.Table;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class GermplasmAuditIntegrationTest extends AuditIntegrationTestBase {

	private static final String PRIMARY_KEY_FIELD = "gid";

	private DaoFactory daoFactory;

	public GermplasmAuditIntegrationTest() {
		super(Germplasm.class.getAnnotation(Table.class).name(), PRIMARY_KEY_FIELD);
	}

	@Before
	public void setUp() {
		this.daoFactory = new DaoFactory(this.sessionProvder);
	}

	@Test
	public void shouldTriggersExists() {
		this.checkTriggerExists("trigger_germplsm_aud_insert", "INSERT");
		this.checkTriggerExists("trigger_germplsm_aud_update", "UPDATE");
		this.checkTriggerExists("trigger_germplsm_aud_delete", "DELETE");
	}

	@Test
	public void shouldAuditInsertAndUpdate() {
		this.enableEntityAudit();

		final List<Method> allMethod = this.daoFactory.getMethodDAO().getAllMethod();

		//Create a germplasm 1
		Map<String, Object> insertGermplasm1QueryParams = this.createQueryParams(allMethod.get(0).getMid(), 0, null, null);
		this.insertEntity(insertGermplasm1QueryParams);

		final Integer germplasm1Gid = this.getLastInsertIdFromEntity();
		assertThat(this.countEntityAudit(germplasm1Gid), is(1));

		final LinkedHashSet<String> fieldNames = this.getSelectAuditFieldNames(insertGermplasm1QueryParams.keySet());

		//Assert recently created germplasm
		final Map<String, Object> insertAudit = this.getLastAudit(fieldNames);
		this.assertAudit(insertAudit, insertGermplasm1QueryParams, 0,false, germplasm1Gid);

		//Disable audit
		this.disableEntityAudit();

		//Insert another germplasm
		Map<String, Object> insertGermplasm2QueryParams = this.createQueryParams(allMethod.get(0).getMid(), 0, null, null);
		this.insertEntity(insertGermplasm2QueryParams);

		//Because the audit was disabled, the germplasm shouldn't be audited
		final Integer germplasm2Gid = this.getLastInsertIdFromEntity();
		assertThat(this.countEntityAudit(germplasm2Gid), is(0));

		//Enable audit
		this.enableEntityAudit();

		//Update the germplasm 1
		final Map<String, Object> updateGermplasm1QueryParams1 =
			this.createQueryParams(allMethod.get(allMethod.size() - 1).getMid(), 1, new Random().nextInt(), new Date());
		this.updateEntity(updateGermplasm1QueryParams1, germplasm1Gid);

		assertThat(this.countEntityAudit(germplasm1Gid), is(2));

		//Assert recently updated germplasm
		final Map<String, Object> updateAudit = this.getLastAudit(fieldNames);
		this.assertAudit(updateAudit, updateGermplasm1QueryParams1, 1, true, germplasm1Gid);

		//Disable audit
		this.disableEntityAudit();

		//Update again the entity, because the audit was disable shouldn't be audited
		final Map<String, Object> updateGermplasm1QueryParams2 =
			this.createQueryParams(allMethod.get(allMethod.size() - 1).getMid(), 1, new Random().nextInt(), new Date());
		this.updateEntity(updateGermplasm1QueryParams2, germplasm1Gid);

		assertThat(this.countEntityAudit(germplasm1Gid), is(2));

		//Enable audit
		this.enableEntityAudit();

		this.deleteEntity(germplasm1Gid);
		assertThat(this.countEntityAudit(germplasm1Gid), is(3));

		//Assert recently deleted germplasm
		final Map<String, Object> deletedAudit = this.getLastAudit(fieldNames);
		this.assertAudit(deletedAudit, updateGermplasm1QueryParams2, 2, true, germplasm1Gid);

		//Disable audit
		this.disableEntityAudit();

		//Delete germplasm 2, because the audit was disable shouldn't be audited
		this.deleteEntity(germplasm2Gid);
		assertThat(this.countEntityAudit(germplasm2Gid), is(0));
	}

	protected Map<String, Object> createQueryParams(final Integer methodId, final int deleted, final Integer modifiedBy, final Date modifiedDate) {
		final Map<String, Object> queryParams = new LinkedHashMap<>();
		queryParams.put("methn", methodId);
		queryParams.put("gnpgs", new Random().nextInt());
		queryParams.put("gpid1", new Random().nextInt());
		queryParams.put("gpid2", new Random().nextInt());
		queryParams.put("created_by", new Random().nextInt());
		queryParams.put("glocn", new Random().nextInt());
		queryParams.put("gdate", new Random().nextInt());
		queryParams.put("gref", new Random().nextInt());
		queryParams.put("grplce", new Random().nextInt());
		queryParams.put("mgid", new Random().nextInt());
		queryParams.put("cid", new Random().nextInt());
		queryParams.put("sid", new Random().nextInt());
		queryParams.put("gchange", new Random().nextInt());
		queryParams.put("deleted", deleted);
		queryParams.put("germplsm_uuid", UUID.randomUUID().toString());
		queryParams.put("modified_by", modifiedBy);
		queryParams.put("created_date", new Date());
		queryParams.put("modified_date", modifiedDate);
		return queryParams;
	}

	private void assertAudit(final Map<String, Object> audit, final Map<String, Object> entity, final int revType, final boolean deleted, final int gid) {
		assertThat(new Integer(audit.get("rev_type").toString()), is(revType));
		assertThat(audit.get("methn"), is(entity.get("methn")));
		assertThat(audit.get("gnpgs"), is(entity.get("gnpgs")));
		assertThat(audit.get("gpid1"), is(entity.get("gpid1")));
		assertThat(audit.get("gpid2"), is(entity.get("gpid2")));
		assertThat(audit.get("created_by"), is(entity.get("created_by")));
		assertThat(audit.get("lgid"), is(entity.get("lgid")));
		assertThat(audit.get("glocn"), is(entity.get("glocn")));
		assertThat(audit.get("gdate"), is(entity.get("gdate")));
		assertThat(audit.get("gref"), is(entity.get("gref")));
		assertThat(audit.get("grplce"), is(entity.get("grplce")));
		assertThat(audit.get("mgid"), is(entity.get("mgid")));
		assertThat(audit.get("sid"), is(entity.get("sid")));
		assertThat(audit.get("gchange"), is(entity.get("gchange")));
		assertThat(audit.get("deleted"), is(deleted));
		assertThat(audit.get("germplsm_uuid"), is(entity.get("germplsm_uuid")));
		assertThat(audit.get("modified_by"), is(entity.get("modified_by")));
		assertThat(DATE_FORMAT.format(audit.get("created_date")), is(DATE_FORMAT.format(entity.get(("created_date")))));
		if (audit.get("modified_date") == null) {
			assertThat(audit.get("modified_date"), is(entity.get("modified_date")));
		} else {
			assertThat(DATE_FORMAT.format(audit.get("modified_date")), is(DATE_FORMAT.format(entity.get("modified_date"))));
		}
		assertThat(audit.get(PRIMARY_KEY_FIELD), is(gid));
		assertNotNull(audit.get(AUDIT_PRIMARY_KEY_FIELD));
	}

}
