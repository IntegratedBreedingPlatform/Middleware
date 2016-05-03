package org.generationcp.middleware.dao;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.ListDataProject;
import org.hibernate.Criteria;
import org.hibernate.classic.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;

public class ListDataProjectDAOTest {

	private ListDataProjectDAO listDataProjectDAO;

	private Session mockHibernateSession;

	@Before
	public void beforeTest() {
		this.listDataProjectDAO = new ListDataProjectDAO();
		this.mockHibernateSession = Mockito.mock(Session.class);
		this.listDataProjectDAO.setSession(this.mockHibernateSession);
	}

	@Test
	public void testGetByListId() throws Exception {

		int listId = 100;
		Integer germplasmId = 1;
		Integer MgId = 500;

		List<Germplasm> germplasms = new ArrayList<>();
		List<ListDataProject> listDataProjects = new ArrayList<>();

		Germplasm germplasm = new Germplasm();
		germplasm.setGid(germplasmId);
		germplasm.setMgid(MgId);
		germplasms.add(germplasm);

		ListDataProject listDataProject = new ListDataProject();
		listDataProject.setGermplasmId(germplasmId);
		listDataProjects.add(listDataProject);

		Criteria mockCriteriaGermplasm = Mockito.mock(Criteria.class);
		when(this.mockHibernateSession.createCriteria(Germplasm.class)).thenReturn(mockCriteriaGermplasm);

		Criteria mockCriteriaListDataProject = Mockito.mock(Criteria.class);
		when(this.mockHibernateSession.createCriteria(ListDataProject.class, "listDataProject")).thenReturn(mockCriteriaListDataProject);

		Criteria germplasmCriteria = this.mockHibernateSession.createCriteria(Germplasm.class);
		Criteria listDataProjectCriteria = this.mockHibernateSession.createCriteria(ListDataProject.class, "listDataProject");

		when(germplasmCriteria.list()).thenReturn(germplasms);
		when(listDataProjectCriteria.list()).thenReturn(listDataProjects);

		List<ListDataProject> listDataProjectList = this.listDataProjectDAO.getByListId(listId);

		Assert.assertEquals(listDataProjects.size(), listDataProjectList.size());
		Assert.assertEquals(germplasm.getMgid(), listDataProjectList.get(0).getGroupId());

	}
}
