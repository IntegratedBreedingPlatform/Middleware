package org.generationcp.middleware.dao;

import java.util.List;

import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.pojos.Method;
import org.hibernate.HibernateException;
import org.hibernate.Query;

public class MethodDAO extends GenericDAO<Method, Integer>
{
	@SuppressWarnings("unchecked")
	public List<Method> getAll() throws QueryException
	{
		try
		{
			Query query = getSession().getNamedQuery(Method.GET_ALL);
			
			List<Method> results = query.list();
			return results;
		}
		catch(HibernateException ex)
		{
			throw new QueryException("Error with find all query for Method: " + ex.getMessage());
		}
	}
}
