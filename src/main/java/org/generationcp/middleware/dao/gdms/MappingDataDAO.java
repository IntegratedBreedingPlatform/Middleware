package org.generationcp.middleware.dao.gdms;

import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.pojos.gdms.AccMetadataSet;
import org.generationcp.middleware.pojos.gdms.MapInfo;
import org.generationcp.middleware.pojos.gdms.MappingData;
import org.hibernate.SQLQuery;


public class MappingDataDAO extends GenericDAO<MappingData, Integer>{
    
    @SuppressWarnings("unchecked")
    public List<MapInfo> getMapInfoByMapName(String mapName) {
        SQLQuery query = getSession().createSQLQuery(MappingData.GET_MAP_INFO_BY_MAP_NAME);        
        query.setParameter("mapName", mapName);
        return (List<MapInfo>) query.list(); // return map info        
    }

}
