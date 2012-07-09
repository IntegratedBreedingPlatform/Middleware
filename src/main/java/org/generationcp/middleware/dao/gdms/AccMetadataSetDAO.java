package org.generationcp.middleware.dao.gdms;

import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.pojos.gdms.AccMetadataSet;
import org.hibernate.SQLQuery;


public class AccMetadataSetDAO extends GenericDAO<AccMetadataSet, Integer>{
    
    @SuppressWarnings("unchecked")
    public List<Integer> getNameIdsByGermplasmIds(List<Integer> gIds) {
        SQLQuery query = getSession().createSQLQuery(AccMetadataSet.GET_NAME_IDS_BY_GERMPLASM_IDS);        
        query.setParameterList("gIdList", gIds);
        return (List<Integer>) query.list();        
    }

}
