package org.generationcp.middleware.dao;

import java.util.List;

import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.pojos.Oindex;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;

public class OindexDAO extends GenericDAO<OindexDAO, Integer> {

	@SuppressWarnings("unchecked")
	public List<Integer> getOunitIDsByRepresentationId (Integer representationId) throws QueryException {
		try {
			SQLQuery query = getSession().createSQLQuery(Oindex.GET_BY_REPRESENTATION_ID);
			query.setParameter("representationId", representationId);
			
			List<Integer> ounitIDs = query.list();
			
			return ounitIDs;
		} catch(HibernateException ex) {
			throw new QueryException("Error with get Ounit IDs by Representation ID query: " + ex.getMessage());
		}
	}
}
