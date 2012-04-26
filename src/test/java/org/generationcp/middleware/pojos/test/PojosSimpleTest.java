package org.generationcp.middleware.pojos.test;


import java.util.List;

import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Bibref;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Georef;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Locdes;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.Progenitor;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.util.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PojosSimpleTest 
{
	private static final String CONFIG = "test-hibernate.cfg.xml";
	private HibernateUtil hibernateUtil;

	@Before
	public void setUp() throws Exception 
	{
		hibernateUtil = new HibernateUtil(CONFIG);
	}

	@Test
	public void testAtributs()
	{
		Session session = hibernateUtil.getCurrentSession();
		Query query = session.createQuery("FROM Attribute");
		query.setMaxResults(5);
		List results = query.list();
		
		for(Object obj : results)
		{
			Assert.assertTrue(obj instanceof Attribute);
			Assert.assertTrue(obj != null);
			Attribute atributs = (Attribute) obj;
			System.out.println(atributs);
		}
	}
	
	@Test
	public void testBibrefs()
	{
		Session session = hibernateUtil.getCurrentSession();
		Query query = session.createQuery("FROM Bibref");
		query.setMaxResults(5);
		List results = query.list();
		
		for(Object obj : results)
		{
			Assert.assertTrue(obj instanceof Bibref);
			Assert.assertTrue(obj != null);
			Bibref holder = (Bibref) obj;
			System.out.println(holder);
		}
	}
	
	@Test
	public void testCntry()
	{
		Session session = hibernateUtil.getCurrentSession();
		Query query = session.createQuery("FROM Country");
		query.setMaxResults(5);
		List results = query.list();
		
		for(Object obj : results)
		{
			Assert.assertTrue(obj instanceof Country);
			Assert.assertTrue(obj != null);
			Country holder = (Country) obj;
			System.out.println(holder);
		}
	}
	
	@Test
	public void testGeoref()
	{
		Session session = hibernateUtil.getCurrentSession();
		Query query = session.createQuery("FROM Georef");
		query.setMaxResults(5);
		List results = query.list();
		
		for(Object obj : results)
		{
			Assert.assertTrue(obj instanceof Georef);
			Assert.assertTrue(obj != null);
			Georef holder = (Georef) obj;
			System.out.println(holder);
		}
	}
	
	@Test
	public void testGermplsm()
	{
		Session session = hibernateUtil.getCurrentSession();
		Query query = session.createQuery("FROM Germplasm");
		query.setMaxResults(5);
		List results = query.list();
		
		for(Object obj : results)
		{
			Assert.assertTrue(obj instanceof Germplasm);
			Assert.assertTrue(obj != null);
			Germplasm holder = (Germplasm) obj;
			System.out.println(holder);
		}
	}
	
	@Test
	public void testLocation()
	{
		Session session = hibernateUtil.getCurrentSession();
		Query query = session.createQuery("FROM Location");
		query.setMaxResults(5);
		List results = query.list();
		
		for(Object obj : results)
		{
			Assert.assertTrue(obj instanceof Location);
			Assert.assertTrue(obj != null);
			Location holder = (Location) obj;
			System.out.println(holder);
		}
	}
	
	@Test
	public void testLocdes()
	{
		Session session = hibernateUtil.getCurrentSession();
		Query query = session.createQuery("FROM Locdes");
		query.setMaxResults(5);
		List results = query.list();
		
		for(Object obj : results)
		{
			Assert.assertTrue(obj instanceof Locdes);
			Assert.assertTrue(obj != null);
			Locdes holder = (Locdes) obj;
			System.out.println(holder);
		}
	}
	
	@Test
	public void testMethods()
	{
		Session session = hibernateUtil.getCurrentSession();
		Query query = session.createQuery("FROM Method");
		query.setMaxResults(5);
		List results = query.list();
		
		for(Object obj : results)
		{
			Assert.assertTrue(obj instanceof Method);
			Assert.assertTrue(obj != null);
			Method holder = (Method) obj;
			System.out.println(holder);
		}
	}
	
	@Test
	public void testNames()
	{
		Session session = hibernateUtil.getCurrentSession();
		Query query = session.createQuery("FROM Name");
		query.setMaxResults(5);
		List results = query.list();
		
		for(Object obj : results)
		{
			Assert.assertTrue(obj instanceof Name);
			Assert.assertTrue(obj != null);
			Name holder = (Name) obj;
			System.out.println(holder);
		}
	}
	
	@Test
	public void testProgntrs()
	{
		Session session = hibernateUtil.getCurrentSession();
		Query query = session.createQuery("FROM Progenitor");
		query.setMaxResults(5);
		List results = query.list();
		
		for(Object obj : results)
		{
			Assert.assertTrue(obj instanceof Progenitor);
			Assert.assertTrue(obj != null);
			Progenitor holder = (Progenitor) obj;
			System.out.println(holder);
		}
	}
	
	@Test
	public void testUdflds()
	{
		Session session = hibernateUtil.getCurrentSession();
		Query query = session.createQuery("FROM UserDefinedField");
		query.setMaxResults(5);
		List results = query.list();
		
		for(Object obj : results)
		{
			Assert.assertTrue(obj instanceof UserDefinedField);
			Assert.assertTrue(obj != null);
			UserDefinedField holder = (UserDefinedField) obj;
			System.out.println(holder);
		}
	}
	
	@Test
	public void testUser()
	{
		Session session = hibernateUtil.getCurrentSession();
		Query query = session.createQuery("FROM User");
		query.setMaxResults(5);
		List results = query.list();
		
		for(Object obj : results)
		{
			Assert.assertTrue(obj instanceof User);
			Assert.assertTrue(obj != null);
			User holder = (User) obj;
			System.out.println(holder);
		}
	}
	
	@Test
	public void testGettingGermplasmDetails()
	{
		Session session = hibernateUtil.getCurrentSession();
		long start = System.currentTimeMillis();
		Germplasm g = (Germplasm) session.load(Germplasm.class, new Integer(50533));
		System.out.println(g);
		//System.out.println(g.getMethod());
		//System.out.println(g.getLocation());
		//System.out.println(g.getReference());
		//System.out.println(g.getUser());
		long end = System.currentTimeMillis();
		System.out.println("QUERY TIME: " + (end - start) + " ms");
	}
	
	@Test
	public void testGermplasmList()
	{
		Session session = hibernateUtil.getCurrentSession();
		Query query = session.createQuery("FROM GermplasmList");
		query.setMaxResults(5);
		List results = query.list();
		
		for(Object obj : results)
		{
			Assert.assertTrue(obj instanceof GermplasmList);
			Assert.assertTrue(obj != null);
			GermplasmList holder = (GermplasmList) obj;
			System.out.println(holder);
		}
	}
	
	@Test
	public void testGermplasmListData()
	{
		Session session = hibernateUtil.getCurrentSession();
		Query query = session.createQuery("FROM GermplasmListData");
		query.setMaxResults(5);
		List results = query.list();
		
		for(Object obj : results)
		{
			Assert.assertTrue(obj instanceof GermplasmListData);
			Assert.assertTrue(obj != null);
			GermplasmListData holder = (GermplasmListData) obj;
			System.out.println(holder);
		}
	}
	
	@After
	public void tearDown() throws Exception 
	{
		hibernateUtil.shutdown();
	}

}
