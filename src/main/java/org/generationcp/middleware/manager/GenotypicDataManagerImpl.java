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
import org.generationcp.middleware.dao.gdms.MappingPopDAO;
import org.generationcp.middleware.dao.gdms.MarkerDAO;
import org.generationcp.middleware.dao.gdms.MarkerMetadataSetDAO;
import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.manager.api.GenotypicDataManager;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.gdms.DatasetElement;
import org.generationcp.middleware.pojos.gdms.GermplasmMarkerElement;
import org.generationcp.middleware.pojos.gdms.Map;
import org.generationcp.middleware.pojos.gdms.MapInfo;
import org.generationcp.middleware.pojos.gdms.MappingValueElement;
import org.generationcp.middleware.pojos.gdms.MarkerNameElement;
import org.generationcp.middleware.pojos.gdms.ParentElement;
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
        HibernateUtil hibernateUtil = getHibernateUtil(gIds.get(0));  

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
        HibernateUtil hibernateUtil = getHibernateUtil(nIds.get(0)); 

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

    @Override
    public Long countAllMaps(Database instance) throws QueryException {
        MapDAO dao = new MapDAO();
        HibernateUtil util = getHibernateUtil(instance);
        
        if(util != null) {
            dao.setSession(util.getCurrentSession());
        } else {
            return (long) 0;
        }
        
        return dao.countAll();
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
    
    @Override
    public List<MapInfo> getMapInfoByMapName(String mapName, Database instance) throws QueryException{
        MappingDataDAO dao = new MappingDataDAO();
        HibernateUtil hibernateUtil = getHibernateUtil(instance);  
        
        if (hibernateUtil != null) {
            dao.setSession(hibernateUtil.getCurrentSession());
        } else {
            return new ArrayList<MapInfo>();
        }
        return (List<MapInfo>) dao.getMapInfoByMapName(mapName);
    }
    
    @Override
    public int countDatasetNames(Database instance) throws QueryException{
        DatasetDAO dao = new DatasetDAO();
        HibernateUtil hibernateUtil = getHibernateUtil(instance);  
        
        if (hibernateUtil != null) {
            dao.setSession(hibernateUtil.getCurrentSession());
        } else {
            return 0;
        }
        return dao.countByName();
    }
    
    @Override
    public List<String> getDatasetNames(Integer start, Integer numOfRows, Database instance) throws QueryException{
        DatasetDAO dao = new DatasetDAO();
        HibernateUtil hibernateUtil = getHibernateUtil(instance);  
        
        if (hibernateUtil != null) {
            dao.setSession(hibernateUtil.getCurrentSession());
        } else {
            return new ArrayList<String>();
        }
        return (List<String>) dao.getDatasetNames(start, numOfRows);
    }
    
    
    /* (non-Javadoc)
     * @see org.generationcp.middleware.manager.api.GenotypicDataManager#getDatasetDetailsByDatasetName(java.lang.String)
     */
    @Override
    public List<DatasetElement> getDatasetDetailsByDatasetName(String datasetName, Database instance) throws QueryException{
        DatasetDAO dao = new DatasetDAO();
        HibernateUtil hibernateUtil = getHibernateUtil(instance);  
        
        if (hibernateUtil != null) {
            dao.setSession(hibernateUtil.getCurrentSession());
        } else {
            return new ArrayList<DatasetElement>();
        }
        return (List<DatasetElement>) dao.getDetailsByName(datasetName);
    }
    
    
    @Override
    public List<Integer> getMarkerIdsByMarkerNames(List<String> markerNames, int start, int numOfRows, Database instance) throws QueryException {
        MarkerDAO dao = new MarkerDAO();
        HibernateUtil hibernateUtil = getHibernateUtil(instance);
        
        if (hibernateUtil != null) {
            dao.setSession(hibernateUtil.getCurrentSession());
        } else {
            return new ArrayList<Integer> ();
        }
        
        List<Integer> markerIds = dao.getIdsByNames(markerNames, start, numOfRows);
        
        return markerIds;
    }

    @Override
    public List<Integer> getMarkerIdsByDatasetId(Integer datasetId) throws QueryException{
        MarkerMetadataSetDAO dao = new MarkerMetadataSetDAO();
        HibernateUtil hibernateUtil = getHibernateUtil(datasetId);  
        
        if (hibernateUtil != null) {
            dao.setSession(hibernateUtil.getCurrentSession());
        } else {
            return new ArrayList<Integer>();
        }
        return (List<Integer>) dao.getMarkerIdByDatasetId(datasetId);
    }
    
    
    @Override
    public List<ParentElement> getParentsByDatasetId(Integer datasetId) throws QueryException{
        MappingPopDAO dao = new MappingPopDAO();
        HibernateUtil hibernateUtil = getHibernateUtil(datasetId);  
        
        if (hibernateUtil != null) {
            dao.setSession(hibernateUtil.getCurrentSession());
        } else {
            return new ArrayList<ParentElement>();
        }
        return (List<ParentElement>) dao.getParentsByDatasetId(datasetId);
    }
    
    @Override
    public List<String> getMarkerTypesByMarkerIds(List<Integer> markerIds) throws QueryException{
        MarkerDAO dao = new MarkerDAO();
        HibernateUtil hibernateUtil = getHibernateUtil(markerIds.get(0));  
        
        if (hibernateUtil != null) {
            dao.setSession(hibernateUtil.getCurrentSession());
        } else {
            return new ArrayList<String>();
        }
        return (List<String>) dao.getMarkerTypeByMarkerIds(markerIds);
    }
    
    @Override
    public List<MarkerNameElement> getMarkerNamesByGIds(List<Integer> gIds) throws QueryException{
        MarkerDAO dao = new MarkerDAO();
        HibernateUtil hibernateUtil = getHibernateUtil(gIds.get(0));  
        
        if (hibernateUtil != null) {
            dao.setSession(hibernateUtil.getCurrentSession());
        } else {
            return new ArrayList<MarkerNameElement>();
        }
        return (List<MarkerNameElement>) dao.getMarkerNamesByGIds(gIds);
    }
    
    @Override
    public List<GermplasmMarkerElement>  getGermplasmNamesByMarkerNames(List<String> markerNames, Database instance) throws QueryException{
        MarkerDAO dao = new MarkerDAO();
        HibernateUtil hibernateUtil = getHibernateUtil(instance);  
        
        if (hibernateUtil != null) {
            dao.setSession(hibernateUtil.getCurrentSession());
        } else {
            return new ArrayList<GermplasmMarkerElement>();
        }
        return (List<GermplasmMarkerElement>) dao.getGermplasmNamesByMarkerNames(markerNames);
    }
    

    @Override
    public List<MappingValueElement> getMappingValuesByGidsAndMarkerNames(
            List<Integer> gids, List<String> markerNames, int start, int numOfRows) throws QueryException {
        MarkerDAO markerDao = new MarkerDAO();
        MappingPopDAO mappingPopDao = new MappingPopDAO();
        
        //get db connection based on the GIDs provided
        Database instance;
        if (gids.get(0) < 0) {
            instance = Database.LOCAL;
        } else {
            instance = Database.CENTRAL;
        }
        HibernateUtil hibernateUtil = getHibernateUtil(instance);
        
        if (hibernateUtil != null) {
            markerDao.setSession(hibernateUtil.getCurrentSession());
            mappingPopDao.setSession(hibernateUtil.getCurrentSession());
        } else {
            return new ArrayList<MappingValueElement> ();
        }
        
        List<Integer> markerIds = markerDao.getIdsByNames(markerNames, start, numOfRows);
        
        List<MappingValueElement> mappingValues = mappingPopDao.getMappingValuesByGidAndMarkerIds(gids, markerIds);
        
        return mappingValues;
    }
    
}