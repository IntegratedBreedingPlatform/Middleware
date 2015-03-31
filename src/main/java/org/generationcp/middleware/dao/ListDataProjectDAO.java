package org.generationcp.middleware.dao;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.ListDataProject;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.List;

public class ListDataProjectDAO extends GenericDAO<ListDataProject, Integer> {

	public void deleteByListId(int listId) throws MiddlewareQueryException {
		try {
			this.flush();
			
			SQLQuery statement = getSession().createSQLQuery("delete ldp " +
	                   "from listdata_project ldp " + 
					   "where ldp.list_id = " + listId);
			statement.executeUpdate();	   
			
	        this.flush();
	        this.clear();
	
		} catch(HibernateException e) {
			logAndThrowException("Error in deleteByListId=" + listId + " in ListDataProjectDAO: " + e.getMessage(), e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<ListDataProject> getByListId(int listId) throws MiddlewareQueryException {
    	List<ListDataProject> list = new ArrayList<ListDataProject>();
        try {
            Criteria criteria = getSession().createCriteria(ListDataProject.class);
            criteria.add(Restrictions.eq("list", new GermplasmList(listId)));
            criteria.addOrder(Order.asc("entryId"));
            return criteria.list();

        } catch (HibernateException e) {
            logAndThrowException("Error with getByListId(listId=" + listId + ") query from ListDataProjectDAO: "
                    + e.getMessage(), e);
        }
        return list;    	
	}

	@SuppressWarnings("unchecked")
	public ListDataProject getByListIdAndEntryNo(int listId,int entryNo) throws MiddlewareQueryException {
		ListDataProject result = null;

		try {
			Criteria criteria = getSession().createCriteria(ListDataProject.class);
			criteria.add(Restrictions.eq("list", new GermplasmList(listId)));
			criteria.add(Restrictions.eq("entryId",entryNo));
			criteria.addOrder(Order.asc("entryId"));
			result = (ListDataProject) criteria.uniqueResult();

		} catch (HibernateException e) {
			logAndThrowException("Error with getByListIdAndEntryNo(listId=" + listId + ") query from ListDataProjectDAO: "
					+ e.getMessage(), e);
		}
		return result;
	}

	public void deleteByListIdWithList(int listId) throws MiddlewareQueryException {
		try {
			this.flush();
			
			SQLQuery statement = getSession().createSQLQuery("delete ldp, lst " +
	                   "from listdata_project ldp, listnms lst " + 
					   "where ldp.list_id = lst.listid and ldp.list_id = " + listId);
			statement.executeUpdate();	   
			
	        this.flush();
	        this.clear();
	
		} catch(HibernateException e) {
			logAndThrowException("Error in deleteByListId=" + listId + " in ListDataProjectDAO: " + e.getMessage(), e);
		}
	}
	
    public long countByListId(Integer id) throws MiddlewareQueryException {
        try {
        	if (id != null){
	            Criteria criteria = getSession().createCriteria(ListDataProject.class);
	            criteria.createAlias("list", "l");
	            criteria.add(Restrictions.eq("l.id", id));
	            criteria.setProjection(Projections.rowCount());
	            return ((Long) criteria.uniqueResult()).longValue(); //count
        	}
        } catch (HibernateException e) {
            logAndThrowException("Error with countByListId(id=" + id + ") query from ListDataProject " + e.getMessage(), e);
        }
        return 0;
    }
	
}
