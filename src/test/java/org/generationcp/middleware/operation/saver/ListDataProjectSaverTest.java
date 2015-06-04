
package org.generationcp.middleware.operation.saver;

import org.generationcp.middleware.dao.GermplasmListDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.GermplasmList;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class ListDataProjectSaverTest {

	@Test
	public void testUpdateGermlasmListInfoStudy() throws MiddlewareQueryException {
		ListDataProjectSaver dataSaver = Mockito.spy(new ListDataProjectSaver(Mockito.mock(HibernateSessionProvider.class)));
		GermplasmList crossesList = new GermplasmList();
		GermplasmListDAO germplasmListDao = Mockito.mock(GermplasmListDAO.class);
		Integer crossesListId = 5;
		dataSaver.setGermplasmListDao(germplasmListDao);
		Mockito.when(germplasmListDao.getById(crossesListId)).thenReturn(crossesList);

		Integer studyId = 15;
		dataSaver.updateGermlasmListInfoStudy(crossesListId, studyId);
		Assert.assertEquals("The study Id in the crosses germplasm should be the same as what we set " + studyId, studyId,
				crossesList.getProjectId());
	}
}
