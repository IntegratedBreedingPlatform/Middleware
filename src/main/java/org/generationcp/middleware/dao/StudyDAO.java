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

import java.math.BigInteger;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.Season;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Study;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class StudyDAO extends GenericDAO<Study, Integer>{

    @SuppressWarnings("unchecked")
    public List<Study> getByNameUsingEqual(String name, int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Query query = getSession().getNamedQuery(Study.GET_BY_NAME_USING_EQUAL);
            query.setParameter("name", name);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);
            return (List<Study>) query.list();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getByNameUsingEqual(name=" + name + ") query from Study: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Study> getBySDateUsingEqual(Integer sdate, int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Query query = getSession().getNamedQuery(Study.GET_BY_SDATE_USING_EQUAL);
            query.setParameter("startDate", sdate);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);
            return (List<Study>) query.list();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getBySDateUsingEqual(sdate=" + sdate + ") query from Study: " + e.getMessage(),
                    e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Study> getByEDateUsingEqual(Integer edate, int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Query query = getSession().getNamedQuery(Study.GET_BY_EDATE_USING_EQUAL);
            query.setParameter("endDate", edate);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);
            return (List<Study>) query.list();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getByEDateUsingEqual(sdate=" + edate + ") query from Study: " + e.getMessage(),
                    e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Study> getByNameUsingLike(String name, int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Query query = getSession().getNamedQuery(Study.GET_BY_NAME_USING_LIKE);
            query.setParameter("name", name);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);
            return (List<Study>) query.list();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getByNameUsingLike(name=" + name + ") query from Study: " + e.getMessage(), e);
        }
    }

    public long countByName(String name, Operation operation) throws MiddlewareQueryException {

        try {
            // if operation == null or operation = Operation.EQUAL
            Query query = getSession().getNamedQuery(Study.COUNT_BY_NAME_USING_EQUAL);
            if (operation == Operation.LIKE) {
                query = getSession().getNamedQuery(Study.COUNT_BY_NAME_USING_LIKE);
            }
            query.setParameter("name", name);
            return ((Long) query.uniqueResult()).longValue();

        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with countByName(name=" + name + ", operation=" + operation + ") query from Study: "
                    + e.getMessage(), e);
        }

    }

    @SuppressWarnings("unchecked")
    public List<Study> getByCountryUsingEqual(String country, int start, int numOfRows) throws MiddlewareQueryException {
        try {
            SQLQuery query = getSession().createSQLQuery(Study.GET_BY_COUNTRY_USING_EQUAL);
            query.setParameter("country", country);
            query.addEntity("s", Study.class);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);

            return query.list();

        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getByCountryUsingEqual(country=" + country + ") query from Study: "
                    + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Study> getByCountryUsingLike(String country, int start, int numOfRows) throws MiddlewareQueryException {
        try {
            SQLQuery query = getSession().createSQLQuery(Study.GET_BY_COUNTRY_USING_LIKE);
            query.setParameter("country", country);
            query.addEntity("s", Study.class);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);

            return query.list();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getByCountryUsingLike(country=" + country + ") query from Study: "
                    + e.getMessage(), e);
        }
    }

    public long countByCountry(String country, Operation operation) throws MiddlewareQueryException {

        try {
            // if operation == null or operation = Operation.EQUAL
            Query query = getSession().createSQLQuery(Study.COUNT_BY_COUNTRY_USING_EQUAL);
            if (operation == Operation.LIKE) {
                query = getSession().createSQLQuery(Study.COUNT_BY_COUNTRY_USING_LIKE);
            }
            query.setParameter("country", country);
            return ((BigInteger) query.uniqueResult()).longValue();

        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with countByCountry(country=" + country + ", operation=" + operation
                    + ") query from Study: " + e.getMessage(), e);
        }

    }

    @SuppressWarnings("unchecked")
    public List<Study> getBySeason(Season season, int start, int numOfRows) throws MiddlewareQueryException {
        try {
            SQLQuery query = getSession().createSQLQuery(Study.GET_BY_SEASON);
            
            if (season == Season.DRY){
                query = getSession().createSQLQuery(Study.GET_BY_SEASON + Study.DRY_SEASON_CONDITION);
            } else if (season == Season.WET){
                query = getSession().createSQLQuery(Study.GET_BY_SEASON + Study.WET_SEASON_CONDITION);
            }  
            query.addEntity("s", Study.class);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);

            return query.list();
            
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getBySeason(season=" + season + ") query from Study: " + e.getMessage(), e);
        }
    }

    public long countBySeason(Season season) throws MiddlewareQueryException {
        try {
            SQLQuery query = getSession().createSQLQuery(Study.COUNT_BY_SEASON);
            
            if (season == Season.DRY){
                query = getSession().createSQLQuery(Study.COUNT_BY_SEASON + Study.DRY_SEASON_CONDITION);
            } else if (season == Season.WET){
                query = getSession().createSQLQuery(Study.COUNT_BY_SEASON + Study.WET_SEASON_CONDITION);
            }  

            return ((BigInteger) query.uniqueResult()).longValue();

        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with countBySeason(season=" + season + ") query from Study: " + e.getMessage(), e);
        }

    }

    public long countBySDate(Integer sdate, Operation operation) throws MiddlewareQueryException {

        try {
            // if operation == null or operation = Operation.EQUAL
            Query query = getSession().getNamedQuery(Study.COUNT_BY_SDATE_USING_EQUAL);

            query.setParameter("startDate", sdate);
            return ((Long) query.uniqueResult()).longValue();

        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with countBySDate(sdate=" + sdate + ", operation=" + operation
                    + ") query from Study: " + e.getMessage(), e);
        }

    }

    public long countByEDate(Integer edate, Operation operation) throws MiddlewareQueryException {

        try {
            // if operation == null or operation = Operation.EQUAL
            Query query = getSession().getNamedQuery(Study.COUNT_BY_EDATE_USING_EQUAL);

            query.setParameter("endDate", edate);
            return ((Long) query.uniqueResult()).longValue();

        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with countByEDate(edate=" + edate + ", operation=" + operation
                    + ") query from Study: " + e.getMessage(), e);
        }

    }

    @SuppressWarnings("unchecked")
    public List<Study> getTopLevelStudies(int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Study.class);
            // top level studies are studies without parent folders (shierarchy = 0)
            criteria.add(Restrictions.eq("hierarchy", Integer.valueOf(0)));
            criteria.setFirstResult(start);
            criteria.setMaxResults(numOfRows);
            return (List<Study>) criteria.list();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getTopLevelStudies() query from Study: " + e.getMessage(), e);
        }
    }

    public long countAllTopLevelStudies() throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Study.class);
            // top level studies are studies without parent folders (shierarchy = 0)
            criteria.add(Restrictions.eq("hierarchy", Integer.valueOf(0)));
            criteria.setProjection(Projections.countDistinct("id"));
            return ((Long) criteria.uniqueResult()).longValue();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with countAllTopLevelStudies() query from Study: " + e.getMessage(), e);
        }
    }

    public long countAllStudyByParentFolderID(Integer parentFolderId) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Study.class);
            // top level studies are studies without parent folders (shierarchy = 0)
            criteria.add(Restrictions.eq("hierarchy", parentFolderId));
            criteria.setProjection(Projections.countDistinct("id"));
            return ((Long) criteria.uniqueResult()).longValue();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with countAllStudyByParentFolderID(parentFolderId=" + parentFolderId
                    + ") query from Study: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Study> getByParentFolderID(Integer parentFolderId, int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Study.class);
            // studies with parent folder = parentFolderId
            criteria.add(Restrictions.eq("hierarchy", parentFolderId));
            criteria.setFirstResult(start);
            criteria.setMaxResults(numOfRows);
            return (List<Study>) criteria.list();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getByParentFolderID(parentFolderId=" + parentFolderId + ") query from Study: "
                    + e.getMessage(), e);
        }
    }

}
