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
import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.workbench.ProjectMethod;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;

/**
 * The Class ProjectMethodDAO.
 * 
 * @author Joyce Avestro
 * 
 */
public class ProjectMethodDAO extends GenericDAO<ProjectMethod, Integer>{


    /**
     * Returns a list of {@link Method} records by project id.
     *
     * @param projectId the project id
     * @param start the start
     * @param numOfRows the num of rows
     * @return the list of {@link Method}s
     * @throws QueryException the query exception
     */
    @SuppressWarnings({ "rawtypes" })
    public List<Method> getByProjectId(Long projectId, int start, int numOfRows) 
        throws QueryException {
        
        List<Method> dataValues = new ArrayList<Method>();
        if (projectId == null){
            return dataValues;
        }
        
        try {
            SQLQuery query = getSession().createSQLQuery(ProjectMethod.GET_METHODS_BY_PROJECT_ID);
            query.setParameter("projectId", projectId.intValue());
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);
            
            List results = query.list();
            
            for (Object o : results){
                Object[] result = (Object[]) o;
                if (result != null) {
                    Integer mid = (Integer) result[0];
                    String mtype = (String) result[1];
                    String mgrp = (String) result[2];
                    String mcode = (String) result[3];
                    String mname = (String) result[4];
                    String mdesc = (String) result[5];
                    Integer mref = (Integer) result[6];
                    Integer mprgn = (Integer) result[7];
                    Integer mfprg = (Integer) result[8];
                    Integer mattr = (Integer) result[9];
                    Integer geneq = (Integer) result[10];
                    Integer muid = (Integer) result[11];
                    Integer lmid = (Integer) result[12];
                    Integer mdate = (Integer) result[13];
                    Method method = new Method(mid, mtype, mgrp, mcode, mname, mdesc, mref, mprgn,
                            mfprg, mattr, geneq, muid, lmid, mdate);
                    dataValues.add(method);
                }
            }
            return dataValues;
            
        } catch (HibernateException e) {
            throw new QueryException("Error with getByProjectId query: " + e.getMessage(), e);
        } 
    }
    
    /**
     * Returns the number of {@link Method} records by project id.
     *
     * @param projectId the project id
     * @return the number of {@link Method} records
     * @throws QueryException the query exception
     */
    public Long countByProjectId(Long projectId) throws QueryException {
        try {
            SQLQuery query = getSession().createSQLQuery(ProjectMethod.COUNT_METHODS_BY_PROJECT_ID);
            query.setParameter("projectId", projectId.intValue());
            BigInteger result = (BigInteger) query.uniqueResult();
            return Long.valueOf(result.longValue());
        } catch (HibernateException e) {
            throw new QueryException("Error with countByProjectId query: " + e.getMessage(), e);
        }
    }
}
