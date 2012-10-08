
package org.generationcp.middleware.dao;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.workbench.IbdbUserMap;
import org.hibernate.HibernateException;
import org.hibernate.Query;

public class IbdbUserMapDAO extends GenericDAO<IbdbUserMap, Long>{

    public Integer getLocalIbdbUserId(Integer workbenchUserId, Long projectId) throws MiddlewareQueryException {
        try {
            Query query = getSession().createSQLQuery(IbdbUserMap.GET_LOCAL_IBDB_USER_ID);
            query.setParameter("workbenchUserId", workbenchUserId);
            query.setParameter("projectId", projectId);
            return (Integer) query.uniqueResult();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getLocalIbdbUserId(workbenchUserId=" + workbenchUserId + ", projectId="
                    + projectId + ") query from IbdbUserMap: " + e.getMessage(), e);
        }
    }

}
