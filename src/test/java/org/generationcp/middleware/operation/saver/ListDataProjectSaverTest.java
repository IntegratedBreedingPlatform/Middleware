
package org.generationcp.middleware.operation.saver;

import org.generationcp.middleware.dao.GermplasmListDAO;
import org.generationcp.middleware.dao.ListDataProjectDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.ListDataProject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

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

	@Test
	public void testPerformListDataProjectEntriesDeletion() {
		final Saver daoFactory = Mockito.mock(Saver.class);
		final ListDataProjectSaver dataSaver = Mockito.spy(new ListDataProjectSaver(daoFactory));

		final ListDataProjectDAO listDataProjectDAO = Mockito.mock(ListDataProjectDAO.class);
		List<Integer> germplasmList = new ArrayList<>();
		germplasmList.add(1);
		final Integer listId = 5;

		ListDataProject listDataProject1 = new ListDataProject();
		listDataProject1.setEntryId(1);
		ListDataProject listDataProject2 = new ListDataProject();
		listDataProject2.setEntryId(2);

		List<ListDataProject> listDataProjects = new ArrayList<>();
		listDataProjects.add(listDataProject2);

		Mockito.when(listDataProjectDAO.getByListIdAndGid(listId, 1)).thenReturn(listDataProject1);

		Mockito.when(listDataProjectDAO.getByListId(listId)).thenReturn(listDataProjects);

		Mockito.when(daoFactory.getListDataProjectDAO()).thenReturn(listDataProjectDAO);

		Mockito.when(listDataProjectDAO.saveOrUpdate(listDataProject2)).thenReturn(listDataProject2);

		dataSaver.performListDataProjectEntriesDeletion(germplasmList, listId);

		Assert.assertEquals("The new entry for list listDataProject2 should be 1", (Integer) 1, listDataProject2.getEntryId());

	}
}
