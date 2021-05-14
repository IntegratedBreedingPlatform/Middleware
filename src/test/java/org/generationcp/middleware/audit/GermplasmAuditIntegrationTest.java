package org.generationcp.middleware.audit;

import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.Table;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
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
	}

	@Test
	public void shouldAuditInsertAndUpdate() {
		final List<Method> allMethod = this.daoFactory.getMethodDAO().getAllMethod();

		List<QueryParam> queryParams = this.createQueryParams(allMethod.get(0).getMid(), 0, null, null);

		final String insertQuery = this.generateInsertQuery(queryParams);
		this.sessionProvder.getSession().createSQLQuery(insertQuery).executeUpdate();

		final Integer gid = this.getLastInsertIdFromEntity();
		assertThat(this.countEntityAudit(gid), is(1));
	}

	protected List<QueryParam> createQueryParams(final Integer methodId, final int deleted, final Integer modifiedBy, final Date modifiedDate) {
		return Arrays.asList(
			new QueryParam("methn", methodId),
			new QueryParam("gnpgs", new Random().nextInt()),
			new QueryParam("gpid1", new Random().nextInt()),
			new QueryParam("gpid2", new Random().nextInt()),
			new QueryParam("created_by", new Random().nextInt()),
			new QueryParam("lgid", new Random().nextInt()),
			new QueryParam("glocn", new Random().nextInt()),
			new QueryParam("gdate", new Random().nextInt()),
			new QueryParam("gref", new Random().nextInt()),
			new QueryParam("grplce", new Random().nextInt()),
			new QueryParam("mgid", new Random().nextInt()),
			new QueryParam("cid", new Random().nextInt()),
			new QueryParam("sid", new Random().nextInt()),
			new QueryParam("gchange", new Random().nextInt()),
			new QueryParam("deleted", deleted),
			new QueryParam("germplsm_uuid", UUID.randomUUID().toString()),
			new QueryParam("modified_by", modifiedBy),
			new QueryParam("created_date", new Date()),
			new QueryParam("modified_date", modifiedDate)
		);
	}

}
