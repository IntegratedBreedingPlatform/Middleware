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
package org.generationcp.middleware.dao.gdms;

import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.pojos.gdms.MapInfo;
import org.generationcp.middleware.pojos.gdms.MappingData;
import org.hibernate.SQLQuery;

/**
 * The Class MappingDataDAO.
 * 
 * @author Joyce Avestro
 * 
 */
public class MappingDataDAO extends GenericDAO<MappingData, Integer>{
    
    /**
     * Gets the map info by map name.
     *
     * @param mapName the map name
     * @return the map info by map name
     */
    @SuppressWarnings("unchecked")
    public List<MapInfo> getMapInfoByMapName(String mapName) {
        SQLQuery query = getSession().createSQLQuery(MappingData.GET_MAP_INFO_BY_MAP_NAME);        
        query.setParameter("mapName", mapName);
        return (List<MapInfo>) query.list(); // return map info        
    }

}
