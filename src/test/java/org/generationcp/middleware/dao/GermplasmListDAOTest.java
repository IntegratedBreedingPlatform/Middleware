
package org.generationcp.middleware.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.SimpleExpression;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class GermplasmListDAOTest {

	@Test
	public void testHideSnapshotListTypes() {
		GermplasmListDAO dao = new GermplasmListDAO();
		Criteria criteria = Mockito.mock(Criteria.class);
		dao.hideSnapshotListTypes(criteria);
		List<SimpleExpression> restrictedList = dao.getRestrictedSnapshopTypes();
		// this should ensure that the snapshot list types are added int he criteria object
		for (SimpleExpression restricted : restrictedList) {
			Mockito.verify(criteria, Mockito.times(1)).add(restricted);
		}

	}

	@Test
	public void testGetRestrictedSnapshopTypes() {
		GermplasmListDAO dao = new GermplasmListDAO();
		List<SimpleExpression> restrictedList = dao.getRestrictedSnapshopTypes();
		Assert.assertEquals("Should have 5 restricted snapshot types", 5, restrictedList.size());
	}

}
