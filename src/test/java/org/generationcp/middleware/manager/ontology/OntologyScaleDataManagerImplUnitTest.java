
package org.generationcp.middleware.manager.ontology;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

import org.generationcp.middleware.dao.oms.CvTermPropertyDao;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.Scale;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class OntologyScaleDataManagerImplUnitTest {

	private OntologyScaleDataManagerImpl newOsdmi;
	private CvTermPropertyDao cvTermPropertyDao;
	private Scale scale;
	private CVTermProperty property;

	@Before
	public void before() {
		this.newOsdmi = new OntologyScaleDataManagerImpl();
		this.cvTermPropertyDao = Mockito.mock(CvTermPropertyDao.class);
		this.scale = new Scale();
		this.scale.setId(10);
		this.property = new CVTermProperty();
	}

	@Test
	public void updatingValuesIfStringIsNotNullOrEmptyTest() throws Exception {
		this.scale.setMaxValue("100");

		this.newOsdmi.updatingValues(this.cvTermPropertyDao, this.scale, "100", TermId.MAX_VALUE.getId());

		Mockito.verify(this.cvTermPropertyDao).save(this.scale.getId(), TermId.MAX_VALUE.getId(), String.valueOf(this.scale.getMaxValue()),
				0);
	}

	@Test
	public void updatingValuesIfStringIsNullOrEmptyTest() throws Exception {
		this.scale.setMaxValue("");

		when(this.cvTermPropertyDao.getOneByCvTermAndType(this.scale.getId(), TermId.MAX_VALUE.getId())).thenReturn(this.property);

		this.newOsdmi.updatingValues(this.cvTermPropertyDao, this.scale, "", TermId.MAX_VALUE.getId());
		Mockito.verify(this.cvTermPropertyDao).makeTransient(this.property);

	}

	@Test
	public void updatingValuesIfStringIsNullOrEmptyAndPropertyIsNullTest() throws Exception {
		this.scale.setMaxValue("");
		this.property = null;

		when(this.cvTermPropertyDao.getOneByCvTermAndType(this.scale.getId(), TermId.MAX_VALUE.getId())).thenReturn(this.property);

		this.newOsdmi.updatingValues(this.cvTermPropertyDao, this.scale, "", TermId.MAX_VALUE.getId());
		Mockito.verify(this.cvTermPropertyDao, never()).makeTransient(this.property);

	}

	@After
	public void after() {
		this.newOsdmi = null;
		this.cvTermPropertyDao = null;
		this.scale = null;
		this.property = null;
	}

}
