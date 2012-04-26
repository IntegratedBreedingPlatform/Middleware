package org.generationcp.middleware.dao;

import java.util.List;

import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.pojos.Lot;
import org.generationcp.middleware.pojos.Transaction;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class LotDAO extends GenericDAO<Lot, Integer>
{
	@SuppressWarnings("unchecked")
	public List<Lot> findByEntityType(String type, int start, int numOfRows)
	{
		Criteria crit = getSession().createCriteria(Lot.class);
		crit.add(Restrictions.eq("entityType", type));
		crit.setFirstResult(start);
		crit.setMaxResults(numOfRows);
		return crit.list();
	}
	
	public Long countByEntityType(String type)
	{
		Criteria crit = getSession().createCriteria(Lot.class);
		crit.setProjection(Projections.rowCount());
		crit.add(Restrictions.eq("entityType", type));
		Long count = (Long) crit.uniqueResult();
		return count;
	}
	
	@SuppressWarnings("unchecked")
	public List<Lot> findByEntityTypeAndEntityId(String type, Integer entityId, int start, int numOfRows)
	{
		Criteria crit = getSession().createCriteria(Lot.class);
		crit.add(Restrictions.eq("entityType", type));
		crit.add(Restrictions.eq("entityId", entityId));
		crit.setFirstResult(start);
		crit.setMaxResults(numOfRows);
		return crit.list();
	}
	
	public Long countByEntityTypeAndEntityId(String type, Integer entityId)
	{
		Criteria crit = getSession().createCriteria(Lot.class);
		crit.setProjection(Projections.rowCount());
		crit.add(Restrictions.eq("entityType", type));
		crit.add(Restrictions.eq("entityId", entityId));
		Long count = (Long) crit.uniqueResult();
		return count;
	}
	
	@SuppressWarnings("unchecked")
	public List<Lot> findByEntityTypeAndLocationId(String type, Integer locationId, int start, int numOfRows)
	{
		Criteria crit = getSession().createCriteria(Lot.class);
		crit.add(Restrictions.eq("entityType", type));
		crit.add(Restrictions.eq("locationId", locationId));
		crit.setFirstResult(start);
		crit.setMaxResults(numOfRows);
		return crit.list();
	}
	
	public Long countByEntityTypeAndLocationId(String type, Integer locationId)
	{
		Criteria crit = getSession().createCriteria(Lot.class);
		crit.setProjection(Projections.rowCount());
		crit.add(Restrictions.eq("entityType", type));
		crit.add(Restrictions.eq("locationId", locationId));
		Long count = (Long) crit.uniqueResult();
		return count;
	}
	
	@SuppressWarnings("unchecked")
	public List<Lot> findByEntityTypeAndEntityIdAndLocationId(String type, Integer entityId, Integer locationId, int start, int numOfRows)
	{
		Criteria crit = getSession().createCriteria(Lot.class);
		crit.add(Restrictions.eq("entityType", type));
		crit.add(Restrictions.eq("entityId", entityId));
		crit.add(Restrictions.eq("locationId", locationId));
		crit.setFirstResult(start);
		crit.setMaxResults(numOfRows);
		return crit.list();
	}
	
	public Long countByEntityTypeAndEntityIdAndLocationId(String type, Integer entityId, Integer locationId)
	{
		Criteria crit = getSession().createCriteria(Lot.class);
		crit.setProjection(Projections.rowCount());
		crit.add(Restrictions.eq("entityType", type));
		crit.add(Restrictions.eq("entityId", entityId));
		crit.add(Restrictions.eq("locationId", locationId));
		Long count = (Long) crit.uniqueResult();
		return count;
	}
	
	public Long getActualLotBalance(Integer lotId)
	{
		Lot lot = findById(lotId, false);
		Criteria crit = getSession().createCriteria(Transaction.class);
		crit.setProjection(Projections.sum("quantity"));
		crit.add(Restrictions.eq("lot", lot));
		//get only committed transactions
		crit.add(Restrictions.eq("status", 1));
		return (Long) crit.uniqueResult();
	}
	
	public Long getAvailableLotBalance(Integer lotId)
	{
		Lot lot = findById(lotId, false);
		Criteria crit = getSession().createCriteria(Transaction.class);
		crit.setProjection(Projections.sum("quantity"));
		crit.add(Restrictions.eq("lot", lot));
		//get all non-cancelled transactions
		crit.add(Restrictions.ne("status", 9));
		return (Long) crit.uniqueResult();
	}
	
	public void validateId(Lot lot) throws QueryException 
	{
		//Check if not a local record (has negative ID)
		Integer id = lot.getId();
		if (id != null && id.intValue() > 0) {
			throw new QueryException("Cannot update a Central Database record. " +
					"Attribute object to update must be a Local Record (ID must be negative)");
		}
	}
}
