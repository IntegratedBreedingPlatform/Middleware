package org.generationcp.middleware.audit;

import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmExternalReference;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.Table;

public class GermplasmExternalReferenceAuditIntegrationTest extends GenericExternalReferenceAuditIntegrationTest {

	private static final String FOREIGN_KEY_FIELD = "gid";

	private DaoFactory daoFactory;
	private Integer recordId;

	public GermplasmExternalReferenceAuditIntegrationTest() {
		super(GermplasmExternalReference.class.getAnnotation(Table.class).name(), FOREIGN_KEY_FIELD);
	}

	@Before
	public void setUp() {
		this.daoFactory = new DaoFactory(this.sessionProvder);
		final Germplasm germplasm = new GermplasmTestDataInitializer().createGermplasmWithPreferredName("LNAME");
		this.daoFactory.getGermplasmDao().save(germplasm);

		this.sessionProvder.getSession().flush();

		this.recordId = germplasm.getGid();
	}

	@Test
	public void shouldTriggersExists() {
		this.checkAllTriggers();
	}

	@Test
	public void shouldAuditInsertAndUpdate() {
		this.checkAuditInsertAndUpdate(this.recordId);
	}
}
