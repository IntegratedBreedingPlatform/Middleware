package org.generationcp.middleware.audit;

import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.ExternalReference;
import org.generationcp.middleware.pojos.Germplasm;
import org.hibernate.FlushMode;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.Table;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class EntityDeleteStoreProcedureTest extends AuditIntegrationTestBase {

	private static final String PRIMARY_KEY_FIELD = "id";

	private DaoFactory daoFactory;

	public EntityDeleteStoreProcedureTest() {
		super(ExternalReference.class.getAnnotation(Table.class).name(), PRIMARY_KEY_FIELD);
	}

	@Before
	public void setup() {
		ContextHolder.setLoggedInUserId(new Random().nextInt());

		this.daoFactory = new DaoFactory(this.sessionProvder);
	}

	@Test
	public void shouldDeleteEntity() {
		this.enableEntityAudit();

		final Germplasm germplasm = new GermplasmTestDataInitializer().createGermplasmWithPreferredName("LNAME");
		this.daoFactory.getGermplasmDao().save(germplasm);

		final ExternalReference externalReference =
			new ExternalReference(germplasm, UUID.randomUUID().toString(), UUID.randomUUID().toString());
		this.daoFactory.getExternalReferenceDAO().save(externalReference);

		final ExternalReference actualExternalReference = this.daoFactory.getExternalReferenceDAO().getById(externalReference.getId());
		assertNull(actualExternalReference.getModifiedBy());
		assertNull(actualExternalReference.getModifiedDate());

		assertThat(this.countEntityAudit(actualExternalReference.getId()), is(1));

		String deleteQuery = String.format("DELETE FROM external_reference WHERE id = %s", externalReference.getId());
		String spQuery = String.format("CALL entity_delete(%s, '%s')", ContextHolder.getLoggedInUserId(), deleteQuery);
		this.sessionProvder.getSession()
			.createSQLQuery(spQuery)
			.executeUpdate();

		assertThat(this.countEntityAudit(actualExternalReference.getId()), is(2));

		Set<String> fields = new HashSet<>();
		fields.add("modified_by");
		fields.add("modified_date");
		Set<String> fieldNames = this.getSelectAuditFieldNames(fields);

		final Map<String, Object> deleteAudit = this.getLastAudit(fieldNames);
		assertThat(deleteAudit.get(PRIMARY_KEY_FIELD), is(externalReference.getId()));
		assertThat(new Integer(deleteAudit.get("rev_type").toString()), is(2));
		assertNotNull(deleteAudit.get("modified_date"));
		assertThat(deleteAudit.get("modified_by"), is(ContextHolder.getLoggedInUserId()));
	}

}
