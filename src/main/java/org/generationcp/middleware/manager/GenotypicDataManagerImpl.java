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

package org.generationcp.middleware.manager;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.dao.NameDAO;
import org.generationcp.middleware.dao.gdms.AccMetadataSetDAO;
import org.generationcp.middleware.dao.gdms.DatasetDAO;
import org.generationcp.middleware.dao.gdms.MapDAO;
import org.generationcp.middleware.dao.gdms.MappingDataDAO;
import org.generationcp.middleware.dao.gdms.MarkerDAO;
import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.manager.api.GenotypicDataManager;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.gdms.DatasetElement;
import org.generationcp.middleware.pojos.gdms.Map;
import org.generationcp.middleware.pojos.gdms.MapInfo;
import org.generationcp.middleware.util.HibernateUtil;

/**
 * Implementation of GenotypicDataManager
 * 
 * @author Joyce Avestro
 */
public class GenotypicDataManagerImpl extends DataManager implements GenotypicDataManager{

    public GenotypicDataManagerImpl(HibernateUtil hibernateUtilForLocal, HibernateUtil hibernateUtilForCentral) {
        super(hibernateUtilForLocal, hibernateUtilForCentral);
    }

    @Override
    public List<Integer> getNameIdsByGermplasmIds(List<Integer> gIds) throws QueryException{
        AccMetadataSetDAO dao = new AccMetadataSetDAO();
        HibernateUtil hibernateUtil = getHibernateUtil(gIds.get(0));  //TODO: Verify DB option

        if (hibernateUtil != null) {
            dao.setSession(hibernateUtil.getCurrentSession());
        } else {
            return new ArrayList<Integer>();
        }

        return (List<Integer>) dao.getNameIdsByGermplasmIds(gIds);
    }

    @Override
    public List<Name> getNamesByNameIds(List<Integer> nIds) throws QueryException {
        NameDAO nameDao = new NameDAO();
        HibernateUtil hibernateUtil = getHibernateUtil(nIds.get(0)); //TODO: Verify DB option

        if (hibernateUtil != null) {
            nameDao.setSession(hibernateUtil.getCurrentSession());
        } else {
            return new ArrayList<Name>();
        }

        return (List<Name>) nameDao.getNamesByNameIds(nIds);
    }

    @Override
    public Name getNameByNameId(Integer nId) throws QueryException {
        NameDAO dao = new NameDAO();
        HibernateUtil util = getHibernateUtil(nId);
        
        if(util != null) {
            dao.setSession(util.getCurrentSession());
        } else {
            return null;
        }
        
        return dao.getNameByNameId(nId);
    }

    /* (non-Javadoc)
     * @see org.generationcp.middleware.manager.api.GenotypicDataManager#getAllMaps(org.generationcp.middleware.manager.Database)
     */
    @Override
    public List<Map> getAllMaps(Database instance) throws QueryException {
        return getAllMaps(null, null, instance);
    }

    @Override
    public List<Map> getAllMaps(Integer start, Integer numOfRows, Database instance) throws QueryException {
        MapDAO dao = new MapDAO();
        HibernateUtil util = getHibernateUtil(instance);
        
        if(util != null) {
            dao.setSession(util.getCurrentSession());
        } else {
            return null;
        }
        
        return dao.findAll(start, numOfRows);
    }
    
    public List<MapInfo> getMapInfoByMapName(String mapName) throws QueryException{
        MappingDataDAO dao = new MappingDataDAO();
        HibernateUtil hibernateUtil = getHibernateUtil(Database.LOCAL);  //TODO: Verify DB option
        
        if (hibernateUtil != null) {
            dao.setSession(hibernateUtil.getCurrentSession());
        } else {
            return new ArrayList<MapInfo>();
        }
        return (List<MapInfo>) dao.getMapInfoByMapName(mapName);
    }
    
    @Override
    public List<String> getDatasetNames() throws QueryException{
        DatasetDAO dao = new DatasetDAO();
        HibernateUtil hibernateUtil = getHibernateUtil(Database.LOCAL);  //TODO: Verify DB option
        
        if (hibernateUtil != null) {
            dao.setSession(hibernateUtil.getCurrentSession());
        } else {
            return new ArrayList<String>();
        }
        return (List<String>) dao.getDatasetNames();
    }
    
    
    /* (non-Javadoc)
     * @see org.generationcp.middleware.manager.api.GenotypicDataManager#getDatasetDetailsByDatasetName(java.lang.String)
     */
    @Override
    public List<DatasetElement> getDatasetDetailsByDatasetName(String datasetName) throws QueryException{
        DatasetDAO dao = new DatasetDAO();
        HibernateUtil hibernateUtil = getHibernateUtil(Database.LOCAL);  //TODO: Verify DB option
        
        if (hibernateUtil != null) {
            dao.setSession(hibernateUtil.getCurrentSession());
        } else {
            return new ArrayList<DatasetElement>();
        }
        return (List<DatasetElement>) dao.getDetailsByName(datasetName);
    }
    
    @Override
    public List<Integer> getMarkerIdsByMarkerNames(List<String> markerNames, int start, int numOfRows) throws QueryException {
        MarkerDAO dao = new MarkerDAO();
        HibernateUtil hibernateUtil = getHibernateUtil(Database.LOCAL); //TODO: Check for Local/Central handling
        
        if (hibernateUtil != null) {
            dao.setSession(hibernateUtil.getCurrentSession());
        } else {
            return new ArrayList<Integer> ();
        }
        
        List<Integer> markerIds = dao.getIdsByNames(markerNames, start, numOfRows);
        
        return markerIds;
    }
    
}