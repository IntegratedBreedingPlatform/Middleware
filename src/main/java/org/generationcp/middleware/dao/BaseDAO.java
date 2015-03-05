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

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BaseDAO {

    private static final Logger LOG = LoggerFactory.getLogger(BaseDAO.class);

    protected final String SHOULD_NOT_OBSOLETE = "is_obsolete = 0";

    private Session session;

    public void setSession(Session session) {
        this.session = session;
    }

    protected Session getSession() {
        return this.session;
    }

    protected Object getById(Class clazz, Long id) {
        return session.get(clazz, id);
    }

    protected List getAll(Class clazz) {
        Query query = session.createQuery("from " + clazz.getName());
        return query.list();
    }
    
    public <TE> List<TE> filterByColumnValue(Class<TE> type, String columnName, Object value) throws MiddlewareQueryException {
        Criterion criterion = value == null ? Restrictions.isNull(columnName) : Restrictions.eq(columnName, value);
        return getByCriteria(type, new ArrayList<>(Arrays.asList(criterion)));
    }
    
    @SuppressWarnings("unchecked")
    protected <TE> List<TE> getByCriteria(Class<TE> type, List<Criterion> criterion) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(type);
            for (Criterion c : criterion) {
                criteria.add(c);
            }
            return criteria.list();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error in getByCriteria(" + criterion + "): " + e.getMessage(), e);
        }
    }

    /*         Type casting for HQL                 */
    
    protected boolean typeSafeObjectToBoolean(Object val){
        if(val == null) return false;
        if(val instanceof Integer) return (Integer) val != 0;
        if(val instanceof Boolean) return (Boolean) val;
        return false;
    }
    
    /*          END TYPE CASTING                    */
    
    protected void logAndThrowException(String message, Throwable e) throws MiddlewareQueryException{
        LOG.error(message, e);
        throw new MiddlewareQueryException(message, e);
    }
}
