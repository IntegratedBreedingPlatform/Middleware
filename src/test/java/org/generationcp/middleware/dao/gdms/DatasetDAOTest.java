package org.generationcp.middleware.dao.gdms;

import org.generationcp.middleware.pojos.Name;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class DatasetDAOTest {
	
	@Mock
	private Session mockSession;
	
	@Mock
	private SQLQuery mockQuery;
	
	private DatasetDAO dao;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		this.dao = new DatasetDAO(this.mockSession);
	}
	
	@Test
	public void testGetGermplasmNamesByMarkerId() {
		Mockito.when(this.mockSession.createSQLQuery(DatasetDAO.GET_GERMPLASM_NAMES_BY_MARKER_ID)).thenReturn(this.mockQuery);
		final int markerId = 112;
		this.dao.getGermplasmNamesByMarkerId(markerId);
		Mockito.verify(this.mockQuery).addScalar("nid");
		Mockito.verify(this.mockQuery).addScalar("germplasmId");
		Mockito.verify(this.mockQuery).addScalar("typeId");
		Mockito.verify(this.mockQuery).addScalar("nstat");
		Mockito.verify(this.mockQuery).addScalar("userId");
		Mockito.verify(this.mockQuery).addScalar("nval");
		Mockito.verify(this.mockQuery).addScalar("locationId");
		Mockito.verify(this.mockQuery).addScalar("ndate");
		Mockito.verify(this.mockQuery).addScalar("referenceId");
		Mockito.verify(this.mockQuery).setParameter("markerId", markerId);
		Mockito.verify(this.mockQuery).setResultTransformer(Transformers.aliasToBean(Name.class));
	}

}
