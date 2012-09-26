package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.workbench.IbdbUserMap;
import org.hibernate.Query;

public class IbdbUserMapDAO extends GenericDAO<IbdbUserMap, Long>{
    
    public Integer getLocalIbdbUserId(Integer workbenchUserId, Long projectId) {
        Query query = getSession().createSQLQuery(IbdbUserMap.GET_LOCAL_IBDB_USER_ID);
        query.setParameter("workbenchUserId", workbenchUserId);
        query.setParameter("projectId", projectId);
        return (Integer) query.uniqueResult();
    }

}
