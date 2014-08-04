package org.generationcp.middleware.dao;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.ListDataProject;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

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
            return criteria.list();

        } catch (HibernateException e) {
            logAndThrowException("Error with getByListId(listId=" + listId + ") query from ListDataProjectDAO: "
                    + e.getMessage(), e);
        }
        return list;    	
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
	
}
