
package org.generationcp.middleware.operation.saver;

import org.generationcp.middleware.dao.GermplasmListDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.GermplasmList;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class ListDataProjectSaverTest {

	@Test
	public void testUpdateGermlasmListInfoStudy() throws MiddlewareQueryException {
		final Saver daoFactory = Mockito.mock(Saver.class);
		final ListDataProjectSaver dataSaver = Mockito.spy(new ListDataProjectSaver(daoFactory));
		final GermplasmList crossesList = new GermplasmList();
		final GermplasmListDAO germplasmListDao = Mockito.mock(GermplasmListDAO.class);
		Integer crossesListId = 5;
		Mockito.when(daoFactory.getGermplasmListDAO()).thenReturn(germplasmListDao);

		Mockito.when(germplasmListDao.getById(crossesListId)).thenReturn(crossesList);

		Integer studyId = 15;
		dataSaver.updateGermlasmListInfoStudy(crossesListId, studyId);
		Assert.assertEquals("The study Id in the crosses germplasm should be the same as what we set " + studyId, studyId,
				crossesList.getProjectId());
	}
}
