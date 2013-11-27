package org.generationcp.middleware.dao;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.ListDataProperty;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for ListDataProperty POJO
 * 
 * @author Darla Ani
 *
 */
public class ListDataPropertyDAO extends GenericDAO<ListDataProperty, Integer> {
	
	public ListDataPropertyDAO(){
		
	}

	/**
	 * Retrieves listdataprop record with given listDataId and columnName
	 * 
	 * @param listDataId
	 * @param columnName
	 * @return null if no record found for given parameters
	 * @throws MiddlewareQueryException
	 */
	public ListDataProperty getByListDataIDAndColumnName(Integer listDataId, 
			String columnName)throws MiddlewareQueryException{
		try {
        	if (listDataId != null && columnName != null && !columnName.isEmpty()){
	            Criteria criteria = getSession().createCriteria(ListDataProperty.class);
	            criteria.createAlias("listData", "l");
	            criteria.add(Restrictions.eq("l.id", listDataId));
	            criteria.add(Restrictions.eq("column", columnName));
	            return (ListDataProperty) criteria.uniqueResult();
        	}
        	
        } catch (HibernateException e) {
            logAndThrowException("Error with getByListDataIDAndColumnName(listdata ID=" + 
            		listDataId + ", column= " + columnName + ") " +
            		"query from ListDataProperty " + e.getMessage(), e);
        }
		return null;
	}
}
