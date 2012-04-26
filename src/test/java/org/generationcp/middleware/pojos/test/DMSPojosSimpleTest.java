package org.generationcp.middleware.pojos.test;


import java.util.List;

import org.generationcp.middleware.pojos.CharacterData;
import org.generationcp.middleware.pojos.Factor;
import org.generationcp.middleware.pojos.NumericData;
import org.generationcp.middleware.pojos.Oindex;
import org.generationcp.middleware.pojos.Variate;
import org.generationcp.middleware.util.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DMSPojosSimpleTest
{
	private static final String CONFIG = "test-hibernate.cfg.xml";
	private HibernateUtil hibernateUtil;

	@Before
	public void setUp() throws Exception
	{
		hibernateUtil = new HibernateUtil(CONFIG);
	}

	@Test
	public void testNumericData()
	{
		Session session = hibernateUtil.getCurrentSession();
		Query query = session.createQuery("FROM NumericData");
		query.setMaxResults(5);
		List results = query.list();
		
		for(Object obj : results)
		{
			Assert.assertTrue(obj instanceof NumericData);
			Assert.assertTrue(obj != null);
			NumericData holder = (NumericData) obj;
			System.out.println(holder);
		}
	}
	
	@Test
	public void testCharacterData()
	{
		Session session = hibernateUtil.getCurrentSession();
		Query query = session.createQuery("FROM CharacterData");
		query.setMaxResults(5);
		List results = query.list();
		
		for(Object obj : results)
		{
			Assert.assertTrue(obj instanceof CharacterData);
			Assert.assertTrue(obj != null);
			CharacterData holder = (CharacterData) obj;
			System.out.println(holder);
		}
	}
	
	@Test
	public void testVariate()
	{
		Session session = hibernateUtil.getCurrentSession();
		Query query = session.createQuery("FROM Variate");
		query.setMaxResults(5);
		List results = query.list();
		
		for(Object obj : results)
		{
			Assert.assertTrue(obj instanceof Variate);
			Assert.assertTrue(obj != null);
			Variate holder = (Variate) obj;
			System.out.println(holder);
		}
	}
	
	@Test
	public void testFactor()
	{
		Session session = hibernateUtil.getCurrentSession();
		Query query = session.createQuery("FROM Factor");
		query.setMaxResults(5);
		List results = query.list();
		
		for(Object obj : results)
		{
			Assert.assertTrue(obj instanceof Factor);
			Assert.assertTrue(obj != null);
			Factor holder = (Factor) obj;
			System.out.println(holder);
		}
	}
	
	@Test
	public void testOindex()
	{
		Session session = hibernateUtil.getCurrentSession();
		Query query = session.createQuery("FROM Oindex");
		query.setMaxResults(5);
		List results = query.list();
		
		for(Object obj : results)
		{
			Assert.assertTrue(obj instanceof Oindex);
			Assert.assertTrue(obj != null);
			Oindex holder = (Oindex) obj;
			System.out.println(holder);
		}
	}
	
	@After
	public void tearDown() throws Exception
	{
		hibernateUtil.shutdown();
	}

}
