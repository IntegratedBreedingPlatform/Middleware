package org.generationcp.middleware.audit;

import org.generationcp.middleware.pojos.Bibref;
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

public class BibrefAuditIntegrationTest extends AuditIntegrationTestBase {

	private static final String PRIMARY_KEY_FIELD = "refid";

	public BibrefAuditIntegrationTest() {
		super(Bibref.class.getAnnotation(Table.class).name(), PRIMARY_KEY_FIELD);
	}

	@Before
	public void setUp() {

	}

	@Test
	public void shouldTriggersExists() {
		this.checkTriggerExists("trigger_bibrefs_aud_insert", "INSERT");
		this.checkTriggerExists("trigger_bibrefs_aud_update", "UPDATE");
	}

	@Test
	public void shouldAuditInsertAndUpdate() {
		this.enableEntityAudit();

		//Create a bibref 1
		Map<String, Object> insertBibref1QueryParams = this.createQueryParams(null, null);
		this.insertEntity(insertBibref1QueryParams);

		final Integer bibref1RefId = this.getLastInsertIdFromEntity();
		assertThat(this.countEntityAudit(bibref1RefId), is(1));

		final LinkedHashSet<String> fieldNames = this.getSelectAuditFieldNames(insertBibref1QueryParams.keySet());

		//Assert recently created bibref
		final Map<String, Object> insertAudit = this.getLastAudit(fieldNames);
		this.assertAudit(insertAudit, insertBibref1QueryParams, 0, bibref1RefId);

		//Disable audit
		this.disableEntityAudit();

		//Insert another bibref
		Map<String, Object> insertBibref2QueryParams = this.createQueryParams(null, null);
		this.insertEntity(insertBibref2QueryParams);

		//Because the audit was disabled, the bibref shouldn't be audited
		final Integer bibref2Aid = this.getLastInsertIdFromEntity();
		assertThat(this.countEntityAudit(bibref2Aid), is(0));

		//Enable audit
		this.enableEntityAudit();

		//Update the bibref 1
		final Map<String, Object> updateBibref1QueryParams1 =
			this.createQueryParams(new Random().nextInt(), new Date());
		this.updateEntity(updateBibref1QueryParams1, bibref1RefId);

		assertThat(this.countEntityAudit(bibref1RefId), is(2));

		//Assert recently updated bibref
		final Map<String, Object> updateAudit = this.getLastAudit(fieldNames);
		this.assertAudit(updateAudit, updateBibref1QueryParams1, 1, bibref1RefId);

		//Disable audit
		this.disableEntityAudit();

		//Update again the entity, because the audit was disable shouldn't be audited
		final Map<String, Object> updateBibref1QueryParams2 =
			this.createQueryParams(new Random().nextInt(), new Date());
		this.updateEntity(updateBibref1QueryParams2, bibref1RefId);

		assertThat(this.countEntityAudit(bibref1RefId), is(2));
	}

	protected Map<String, Object> createQueryParams(final Integer modifiedBy, final Date modifiedDate) {
		final Map<String, Object> queryParams = new LinkedHashMap<>();
		queryParams.put("pubtype", new Random().nextInt());
		queryParams.put("pubdate", new Random().nextInt());
		queryParams.put("authors", UUID.randomUUID().toString());
		queryParams.put("editors", UUID.randomUUID().toString());
		queryParams.put("analyt", UUID.randomUUID().toString());
		queryParams.put("monogr", UUID.randomUUID().toString());
		queryParams.put("series", UUID.randomUUID().toString());
		queryParams.put("volume", UUID.randomUUID().toString().substring(0, 10));
		queryParams.put("issue", UUID.randomUUID().toString().substring(0, 10));
		queryParams.put("pagecol", UUID.randomUUID().toString().substring(0, 25));
		queryParams.put("publish", UUID.randomUUID().toString());
		queryParams.put("pucity", UUID.randomUUID().toString().substring(0, 30));
		queryParams.put("pucntry", UUID.randomUUID().toString());
		queryParams.put("authorlist", new Random().nextInt());
		queryParams.put("editorlist", new Random().nextInt());
		queryParams.put("created_by", new Random().nextInt());
		queryParams.put("modified_by", modifiedBy);
		queryParams.put("created_date", new Date());
		queryParams.put("modified_date", modifiedDate);
		return queryParams;
	}

	private void assertAudit(final Map<String, Object> audit, final Map<String, Object> entity, final int revType,
		final int aid) {
		assertThat(new Integer(audit.get("rev_type").toString()), is(revType));
		assertThat(audit.get("pubtype"), is(entity.get("pubtype")));
		assertThat(audit.get("pubdate"), is(entity.get("pubdate")));
		assertThat(audit.get("authors"), is(entity.get("authors")));
		assertThat(audit.get("editors"), is(entity.get("editors")));
		assertThat(audit.get("analyt"), is(entity.get("analyt")));
		assertThat(audit.get("monogr"), is(entity.get("monogr")));
		assertThat(audit.get("series"), is(entity.get("series")));
		assertThat(audit.get("volume"), is(entity.get("volume")));
		assertThat(audit.get("issue"), is(entity.get("issue")));
		assertThat(audit.get("pagecol"), is(entity.get("pagecol")));
		assertThat(audit.get("publish"), is(entity.get("publish")));
		assertThat(audit.get("pucity"), is(entity.get("pucity")));
		assertThat(audit.get("pucity"), is(entity.get("pucity")));
		assertThat(audit.get("pucntry"), is(entity.get("pucntry")));
		assertThat(audit.get("authorlist"), is(entity.get("authorlist")));
		assertThat(audit.get("editorlist"), is(entity.get("editorlist")));
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
