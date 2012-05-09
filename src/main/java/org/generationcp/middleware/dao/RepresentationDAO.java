package org.generationcp.middleware.dao;

import java.util.List;

import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.pojos.Representation;
import org.hibernate.HibernateException;
import org.hibernate.Query;

public class RepresentationDAO extends GenericDAO<RepresentationDAO, Integer>
{
	@SuppressWarnings("unchecked")
	public List<Representation> getRepresentationByEffectID(Integer effectId) throws QueryException
	{
		try
		{
			Query query = getSession().getNamedQuery(Representation.GET_REPRESENTATION_BY_EFFECT_ID);
			query.setParameter("effectId", effectId);
			
			List<Representation> results = query.list();
			return results;
		}
		catch(HibernateException ex)
		{
			throw new QueryException("Error with get representation by effect id query: " + ex.getMessage());
		}
	}

}
