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

package org.generationcp.middleware.manager.api;

import java.util.List;

import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.gdms.Map;
import org.generationcp.middleware.pojos.gdms.MapInfo;

/**
 * This is the API for retrieving and storing genotypic data 
 * 
 */
public interface GenotypicDataManager{

    public List<Integer> getNameIdsByGermplasmIds(List<Integer> gIds) throws QueryException;

    public List<Name> getNamesByNameIds(List<Integer> nIds) throws QueryException;

    public Name getNameByNameId(Integer nId) throws QueryException;
    
    public List<Map> getAllMaps(Database instance) throws QueryException;
    
    public List<Map> getAllMaps(Integer start, Integer numOfRows,  Database instance) throws QueryException;
    
    public List<MapInfo> getMapInfoByMapName(String mapName) throws QueryException;
}
