/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.dao;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Method;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link Method}.
 * 
 */
public class MethodDAO extends GenericDAO<Method, Integer>{
    @SuppressWarnings("unchecked")
    public List<Method> getMethodsByIds(List<Integer> ids) throws MiddlewareQueryException {
        try {
            return getSession().createCriteria(Method.class).add(Restrictions.in("mid",ids)).list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getMethodsByIds() query from Method: " + e.getMessage(),e);
        }

        return new ArrayList<Method>();

    }

    @SuppressWarnings("unchecked")
    public List<Method> getAllMethod() throws MiddlewareQueryException {
        try {
            Query query = getSession().getNamedQuery(Method.GET_ALL);
            return (List<Method>) query.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getAllMethod() query from Method: " + e.getMessage(), e);
        }
        return new ArrayList<Method>();
    }
    
    @SuppressWarnings("unchecked")
    public List<Method> getByUniqueID(String programUUID) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Method.class);
            criteria.add(Restrictions.or(Restrictions.eq("uniqueID", programUUID),Restrictions.isNull("uniqueID")));
            criteria.addOrder(Order.asc("mname"));
            return criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getMethodsByType(programUUID=" + programUUID + ") query from Method: " + e.getMessage(), e);
        }
        return new ArrayList<Method>();
    }

    @SuppressWarnings("unchecked")
    public List<Method> getByType(String type) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Method.class);
            criteria.add(Restrictions.eq("mtype", type));
            criteria.addOrder(Order.asc("mname"));
            return criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getMethodsByType(type=" + type + ") query from Method: " + e.getMessage(), e);
        }
        return new ArrayList<Method>();
    }
    
    @SuppressWarnings("unchecked")
    public List<Method> getByType(String type, String programUUID) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Method.class);
            criteria.add(Restrictions.eq("mtype", type));
            criteria.add(Restrictions.or(Restrictions.eq("uniqueID", programUUID),Restrictions.isNull("uniqueID")));
            criteria.addOrder(Order.asc("mname"));
            return criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getMethodsByType(type=" + type + ") query from Method: " + e.getMessage(), e);
        }
        return new ArrayList<Method>();
    }


    @SuppressWarnings("unchecked")
    public List<Method> getByType(String type, int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Method.class);
            criteria.add(Restrictions.eq("mtype", type));
            criteria.addOrder(Order.asc("mname"));
            criteria.setFirstResult(start);
            criteria.setMaxResults(numOfRows);            
            return criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getMethodsByType(type=" + type + ") query from Method: " + e.getMessage(), e);
        }
        return new ArrayList<Method>();
    }

    public long countByType(String type) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Method.class);
            criteria.add(Restrictions.eq("mtype", type));
            criteria.setProjection(Projections.rowCount());
            return ((Long) criteria.uniqueResult()).longValue(); // count
        } catch (HibernateException e) {
            logAndThrowException("Error with countMethodsByType(type=" + type + ") query from Method: " + e.getMessage(), e);
        }
        return 0;
    }
    
    public long countByType(String type, String programUUID) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Method.class);
            criteria.add(Restrictions.eq("mtype", type));
            criteria.add(Restrictions.or(Restrictions.eq("uniqueID", programUUID),Restrictions.isNull("uniqueID")));
            criteria.setProjection(Projections.rowCount());
            return ((Long) criteria.uniqueResult()).longValue(); // count
        } catch (HibernateException e) {
            logAndThrowException("Error with countMethodsByType(type=" + type + ") query from Method: " + e.getMessage(), e);
        }
        return 0;
    }
    
    public long countByUniqueID(String programUUID) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Method.class);
            criteria.add(Restrictions.or(Restrictions.eq("uniqueID", programUUID),Restrictions.isNull("uniqueID")));
            criteria.setProjection(Projections.rowCount());
            return ((Long) criteria.uniqueResult()).longValue(); // count
        } catch (HibernateException e) {
            logAndThrowException("Error with countMethodsByType(programUUID=" + programUUID + ") query from Method: " + e.getMessage(), e);
        }
        return 0;
    }
    


    @SuppressWarnings("unchecked")
    public List<Method> getByGroup(String group) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Method.class);
            criteria.add(Restrictions.eq("mgrp", group));
            criteria.addOrder(Order.asc("mname"));
            return criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getMethodsByGroup(group=" + group + ") query from Method: " + e.getMessage(), e);
        }
        return new ArrayList<Method>();
    }
    
    
    @SuppressWarnings("unchecked")
    public List<Method> getByGroupIncludesGgroup(String group) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Method.class);
            Criterion group1=Restrictions.eq("mgrp", group);
            Criterion group2=Restrictions.eq("mgrp", "G");
            LogicalExpression orExp=Restrictions.or(group1, group2);
            criteria.add(orExp);
            criteria.addOrder(Order.asc("mname"));
            return criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getMethodsByGroup(group=" + group + ") query from Method: " + e.getMessage(), e);
        }
        return new ArrayList<Method>();
    }

    @SuppressWarnings("unchecked")
    public List<Method> getByGroup(String group, int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Method.class);
            criteria.add(Restrictions.eq("mgrp", group));
            criteria.addOrder(Order.asc("mname"));
            criteria.setFirstResult(start);
            criteria.setMaxResults(numOfRows);            
            return criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getMethodsByGroup(group=" + group + ") query from Method: " + e.getMessage(), e);
        }
        return new ArrayList<Method>();
    }
    
    
    @SuppressWarnings("unchecked")
    public List<Method> getByGroupAndType(String group,String type) throws MiddlewareQueryException {
        try {
           
            Criteria criteria = getSession().createCriteria(Method.class);
            Criterion group1=Restrictions.eq("mgrp", group);
            Criterion group2=Restrictions.eq("mgrp", "G");
            LogicalExpression orExp=Restrictions.or(group1, group2);
            Criterion filterType=Restrictions.eq("mtype", type);
            LogicalExpression andExp=Restrictions.and(orExp,filterType );
            
            criteria.add(andExp);
            criteria.addOrder(Order.asc("mname"));
            return criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getMethodsByGroupAndType(group=" + group + " and "+type+") query from Method: " + e.getMessage(), e);
        }
        return new ArrayList<Method>();
    }
    
    @SuppressWarnings("unchecked")
    public List<Method> getByGroupAndTypeAndName(String group,String type, String name) throws MiddlewareQueryException {
        try {
           
            Criteria criteria = getSession().createCriteria(Method.class);

            if (type != null && !type.isEmpty()) {
                criteria.add(Restrictions.eq("mtype", type));
            }

            if (name != null && !name.isEmpty()) {
                criteria.add(Restrictions.like("mname", "%" + name.trim() + "%"));
            }


            if (group != null && !group.isEmpty()) {
                Criterion group1=Restrictions.eq("mgrp", group);
                Criterion group2=Restrictions.eq("mgrp", "G");
                LogicalExpression orExp=Restrictions.or(group1, group2);

                criteria.add(orExp);
            }

            criteria.addOrder(Order.asc("mname"));
            return criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getMethodsByGroupAndType(group=" + group + " and "+type+") query from Method: " + e.getMessage(), e);
        }
        return new ArrayList<Method>();
    }

    @SuppressWarnings("unchecked")
	public List<Method> getAllMethodsNotGenerative()  throws MiddlewareQueryException {
        try {
        	List<Integer> validMethodClasses = new ArrayList<Integer>();
        	validMethodClasses.addAll(Method.BULKED_CLASSES);
        	validMethodClasses.addAll(Method.NON_BULKED_CLASSES);
        	
            Criteria criteria = getSession().createCriteria(Method.class);
            criteria.add(Restrictions.ne("mtype","GEN"));
            criteria.add(Restrictions.in("geneq", validMethodClasses));
            criteria.addOrder(Order.asc("mname"));

            return criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getAllMethodsNotGenerative() query from Method: " + e.getMessage(), e);
        }
        return new ArrayList<Method>();
    }
    
    
    public long countByGroup(String group) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Method.class);
            criteria.add(Restrictions.eq("mgrp", group));
            criteria.setProjection(Projections.rowCount());
            return ((Long) criteria.uniqueResult()).longValue(); // count
        } catch (HibernateException e) {
            logAndThrowException("Error with countMethodsByGroup(group=" + group + ") query from Method: " + e.getMessage(), e);
        }
        return 0;
    }   
    
    public Method getByCode(String code) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Method.class);
            criteria.add(Restrictions.eq("mcode", code));
            return (Method) criteria.uniqueResult();
        } catch (HibernateException e) {
            logAndThrowException("Error with getMethodByCode(code=" + code + ") query from Method: " + e.getMessage(), e);
        }
        return new Method();
    }
    
    @SuppressWarnings("unchecked")
    public List<Method> getByName(String name) throws MiddlewareQueryException {
        List<Method> methods = new ArrayList<Method>();
        try {
            StringBuilder queryString = new StringBuilder();
            queryString.append("SELECT mid, mtype, mgrp, mcode, mname, mdesc, mref, mprgn, mfprg, mattr, geneq, muid, lmid, mdate, program_uuid ")
                    .append("FROM methods m WHERE m.mname = :mname");
            SQLQuery query = getSession().createSQLQuery(queryString.toString());
            query.setParameter("mname", name);

            List<Object[]> list = query.list();
            
            for (Object[] row : list){
                Integer mid = (Integer) row[0];
                String mtype = (String) row[1];
                String mgrp = (String) row[2];
                String mcode = (String) row[3];
                String mname = (String) row[4];
                String mdesc = (String) row[5];
                Integer mref = (Integer) row[6];
                Integer mprgn = (Integer) row[7];
                Integer mfprg = (Integer) row[8];
                Integer mattr = (Integer) row[9];
                Integer geneq = (Integer) row[10];
                Integer muid = (Integer) row[11];
                Integer lmid = (Integer) row[12];
                Integer mdate = (Integer) row[13];
                String programUUID = (String) row[14];
                
                Method method = new Method(mid, mtype, mgrp, mcode, mname, mdesc, mref, mprgn, mfprg, mattr, geneq, muid, lmid, mdate,programUUID);
                methods.add(method);
            }
            
            return methods;
        } catch (HibernateException e) {
            logAndThrowException("Error with getByName(name=" + name + ") query from Method: " + e.getMessage(), e);
        }
        return new ArrayList<Method>();
    }

	@SuppressWarnings("unchecked")
	public List<Method> getProgramMethods(String programUUID) throws MiddlewareQueryException {
		List<Method> method = new ArrayList<Method>();
		try{
			Criteria criteria = getSession().createCriteria(Method.class);
    		criteria.add(Restrictions.eq("uniqueID", programUUID));
    		method = (List<Method>) criteria.list();
    	} catch (HibernateException e) {
            logAndThrowException(
                    "Error in getProgramMethods(" + programUUID + ") in MethodDao: " + e.getMessage(), e);
		}
    	return method;
	}
}
