package org.generationcp.middleware.dao.test;


import java.util.HashSet;
import java.util.Set;

import org.generationcp.middleware.dao.FactorDAO;
import org.generationcp.middleware.util.HibernateUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestFactorDAO
{
	private static final String CONFIG = "test-hibernate.cfg.xml";
	private HibernateUtil hibernateUtil;
	private FactorDAO dao;

	@Before
	public void setUp() throws Exception
	{
		hibernateUtil = new HibernateUtil(CONFIG);
		dao = new FactorDAO();
		dao.setSession(hibernateUtil.getCurrentSession());
	}

	@Test
	public void testGetGIDSGivenObservationUnitIds() throws Exception
	{
		Set<Integer> ounitIds = new HashSet<Integer>();
		ounitIds.add(403);
		ounitIds.add(644);
		ounitIds.add(686);
		ounitIds.add(2598);
		ounitIds.add(3377);
		ounitIds.add(10878);
		ounitIds.add(11141);
		ounitIds.add(11200);
		ounitIds.add(11201);
		ounitIds.add(11362);

		Set<Integer> gids = dao.getGIDSGivenObservationUnitIds(ounitIds, 0, 10);
		System.out.println("RESULTS");
		for(Integer gid : gids)
			System.out.println(gid);
	}
	
	@After
	public void tearDown() throws Exception
	{
		dao.setSession(null);
		dao = null;
		hibernateUtil.shutdown();
	}
}
