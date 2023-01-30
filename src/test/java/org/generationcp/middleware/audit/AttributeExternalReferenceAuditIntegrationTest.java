package org.generationcp.middleware.audit;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.AttributeExternalReference;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.Table;

public class AttributeExternalReferenceAuditIntegrationTest extends GenericExternalReferenceAuditIntegrationTest {

	private static final String FOREIGN_KEY_FIELD = "aid";

	private DaoFactory daoFactory;
	private Integer recordId;

	public AttributeExternalReferenceAuditIntegrationTest() {
		super(AttributeExternalReference.class.getAnnotation(Table.class).name(), FOREIGN_KEY_FIELD);
	}

	@Before
	public void setUp() {
		this.daoFactory = new DaoFactory(this.sessionProvder);
		final CVTerm attributeType1 =
			new CVTerm(null, CvId.VARIABLES.getId(), RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10), null,
				0, 0, false);
		this.daoFactory.getCvTermDao().save(attributeType1);
		final Attribute attribute =
			new Attribute(null, 1, attributeType1.getCvTermId(), RandomStringUtils.randomAlphabetic(100), null, null,
				null,
				null);
		this.daoFactory.getAttributeDAO().save(attribute);

		this.sessionProvder.getSession().flush();

		this.recordId = attribute.getAid();
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
