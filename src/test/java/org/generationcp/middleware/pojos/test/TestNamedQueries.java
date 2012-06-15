/***************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the 
 * GNU General Public License (http://bit.ly/8Ztv8M) and the 
 * provisions of Part F of the Generation Challenge Programme 
 * Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 **************************************************************/
package org.generationcp.middleware.pojos.test;


import java.math.BigInteger;
import java.util.List;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.util.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestNamedQueries 
{
	private static final String CONFIG = "test-hibernate.cfg.xml";
	private HibernateUtil hibernateUtil;

	@Before
	public void setUp() throws Exception 
	{
		hibernateUtil = new HibernateUtil(CONFIG);
	}

	@Test
	public void testFindGermplasmByPrefName()
	{
		Session session = hibernateUtil.getCurrentSession();
		long start = System.currentTimeMillis();
		Query query = session.getNamedQuery(Germplasm.FIND_BY_PREF_NAME);
		query.setParameter("name", "IR 64");
		query.setMaxResults(5);
		List results = query.list();
		long end = System.currentTimeMillis();
		System.out.println("QUERY TIME: " + (end - start) + " ms");
		System.out.println("SEARCH RESULTS:");
		for(Object obj : results)
		{
			Assert.assertTrue(obj instanceof Germplasm);
			Assert.assertTrue(obj != null);
			Germplasm holder = (Germplasm) obj;
			System.out.println(holder);
		}
	}
	
	@Test
	public void testFindAllGermplasm()
	{
		Session session = hibernateUtil.getCurrentSession();
		long start = System.currentTimeMillis();
		Query query = session.getNamedQuery(Germplasm.FIND_ALL);
		query.setMaxResults(5);
		List results = query.list();
		long end = System.currentTimeMillis();
		System.out.println("QUERY TIME: " + (end - start) + " ms");
		System.out.println("SEARCH RESULTS:");
		for(Object obj : results)
		{
			Assert.assertTrue(obj instanceof Germplasm);
			Assert.assertTrue(obj != null);
			Germplasm holder = (Germplasm) obj;
			System.out.println(holder);
		}
	}
	
	@Test
	public void testCountAllGermplasm()
	{
		Session session = hibernateUtil.getCurrentSession();
		long start = System.currentTimeMillis();
		Query query = session.getNamedQuery(Germplasm.COUNT_ALL);
		Long result = (Long) query.uniqueResult();
		long end = System.currentTimeMillis();
		System.out.println("QUERY TIME: " + (end - start) + " ms");
		
		Assert.assertTrue(result != null);
		System.out.println(result);
	}
	
	@Test
	public void testCountGermplasmByPrefName()
	{
		Session session = hibernateUtil.getCurrentSession();
		long start = System.currentTimeMillis();
		Query query = session.createSQLQuery(Germplasm.COUNT_BY_PREF_NAME);
		query.setString("name", "IR 64");
		BigInteger result = (BigInteger) query.uniqueResult();
		long end = System.currentTimeMillis();
		System.out.println("QUERY TIME: " + (end - start) + " ms");
		
		Assert.assertTrue(result != null);
		System.out.println(result);
	}
	
	@After
	public void tearDown() throws Exception 
	{
		hibernateUtil.shutdown();
	}

}
