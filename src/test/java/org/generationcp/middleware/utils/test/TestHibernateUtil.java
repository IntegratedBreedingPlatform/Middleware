package org.generationcp.middleware.utils.test;

import java.util.List;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.util.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.Test;


public class TestHibernateUtil
{
	@Test
	public void testHibernateUtil() throws Exception
	{
		HibernateUtil util = new HibernateUtil("localhost", "3306", "iris_myisam_20100330", "root", "lich27king");
		Session session = util.getCurrentSession();
		Query query = session.createQuery("FROM Germplasm");
		query.setFirstResult(0);
		query.setMaxResults(5);
		List<Germplasm> results = query.list();
		
		for(Germplasm g : results)
		{
			System.out.println(g);
		}
	}
}
