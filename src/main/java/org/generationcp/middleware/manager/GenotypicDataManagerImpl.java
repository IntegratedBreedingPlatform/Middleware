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
import org.generationcp.middleware.dao.gdms.AlleleValuesDAO;
import org.generationcp.middleware.dao.gdms.CharValuesDAO;
import org.generationcp.middleware.dao.gdms.DatasetDAO;
import org.generationcp.middleware.dao.gdms.MapDAO;
import org.generationcp.middleware.dao.gdms.MappingDataDAO;
import org.generationcp.middleware.dao.gdms.MappingPopDAO;
import org.generationcp.middleware.dao.gdms.MappingPopValuesDAO;
import org.generationcp.middleware.dao.gdms.MarkerDAO;
import org.generationcp.middleware.dao.gdms.MarkerMetadataSetDAO;
import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.manager.api.GenotypicDataManager;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.gdms.AllelicValueElement;
import org.generationcp.middleware.pojos.gdms.AllelicValueWithMarkerIdElement;
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
            return new ArrayList<Map>();
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
            return new ArrayList<MappingValueElement>();
        }
        
        List<Integer> markerIds = markerDao.getIdsByNames(markerNames, start, numOfRows);
        
        List<MappingValueElement> mappingValues = mappingPopDao.getMappingValuesByGidAndMarkerIds(gids, markerIds);
        
        return mappingValues;
    }
    
    @Override
    public List<AllelicValueElement> getAllelicValuesByGidsAndMarkerNames(
            List<Integer> gids, List<String> markerNames) throws QueryException {
        MarkerDAO markerDao = new MarkerDAO();
        
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
        } else {
            return new ArrayList<AllelicValueElement>();
        }
        
        List<AllelicValueElement> allelicValues = markerDao.getAllelicValuesByGidsAndMarkerNames(gids, markerNames);
        
        return allelicValues;
    }

    @Override
    public List<AllelicValueWithMarkerIdElement> getAllelicValuesFromCharValuesByDatasetId(
            Integer datasetId, int start, int numOfRows) throws QueryException{
        CharValuesDAO dao = new CharValuesDAO();
        HibernateUtil util = getHibernateUtil(datasetId);
        if (util == null) {
            return new ArrayList<AllelicValueWithMarkerIdElement>();
        }
        dao.setSession(util.getCurrentSession());        
        return dao.getAllelicValuesByDatasetId(datasetId, start, numOfRows);
    }
    
    @Override
    public int countAllelicValuesFromCharValuesByDatasetId(Integer datasetId) throws QueryException{
        CharValuesDAO dao = new CharValuesDAO();
        HibernateUtil util = getHibernateUtil(datasetId);
        if (util == null) {
            return 0;
        }
        dao.setSession(util.getCurrentSession());
        return dao.countByDatasetId(datasetId);
    }

    @Override
    public List<AllelicValueWithMarkerIdElement> getAllelicValuesFromAlleleValuesByDatasetId(
            Integer datasetId, int start, int numOfRows) throws QueryException{
        AlleleValuesDAO dao = new AlleleValuesDAO();
        HibernateUtil util = getHibernateUtil(datasetId);

        if (util == null) {
            return new ArrayList<AllelicValueWithMarkerIdElement>();
        }
        dao.setSession(util.getCurrentSession());
        return dao.getAllelicValuesByDatasetId(datasetId, start, numOfRows);
    }
    
    
    @Override
    public int countAllelicValuesFromAlleleValuesByDatasetId(Integer datasetId) throws QueryException{
        AlleleValuesDAO dao = new AlleleValuesDAO();
        HibernateUtil util = getHibernateUtil(datasetId);
        if (util == null) {
            return 0;
        }
        dao.setSession(util.getCurrentSession());
        return dao.countByDatasetId(datasetId);
    }

    @Override
    public List<AllelicValueWithMarkerIdElement> getAllelicValuesFromMappingPopValuesByDatasetId(
            Integer datasetId, int start, int numOfRows) throws QueryException{
        MappingPopValuesDAO dao = new MappingPopValuesDAO();
        HibernateUtil util = getHibernateUtil(datasetId);
        if (util == null) {
            return  new ArrayList<AllelicValueWithMarkerIdElement>();
        }
        dao.setSession(util.getCurrentSession());
        return dao.getAllelicValuesByDatasetId(datasetId, start, numOfRows);
    }
    
    @Override
    public int countAllelicValuesFromMappingPopValuesByDatasetId(Integer datasetId) throws QueryException{
        MappingPopValuesDAO dao = new MappingPopValuesDAO();
        HibernateUtil util = getHibernateUtil(datasetId);
        if (util == null) {
            return 0;
        }
        dao.setSession(util.getCurrentSession());
        return dao.countByDatasetId(datasetId);
    }
    
    @Override
    public List<String> getMarkerNamesByMarkerIds(List<Integer> markerIds)
            throws QueryException {
        MarkerDAO dao = new MarkerDAO();
        HibernateUtil hibernateUtil = getHibernateUtil(markerIds.get(0));
        
        if (hibernateUtil != null) {
            dao.setSession(hibernateUtil.getCurrentSession());
        } else {
            return new ArrayList<String>();
        }
        
        List<String> markerNames = dao.getNamesByIds(markerIds);
        return markerNames;
    }

    @Override
    public List<String> getAllMarkerTypes(int start, int numOfRows) throws QueryException {
        MarkerDAO dao = new MarkerDAO();
        List<String> markerTypes = new ArrayList<String>();
        int centralCount = 0;
        int localCount = 0;
        int relativeLimit = 0;
        
        if(hibernateUtilForCentral != null) {
            
            dao.setSession(hibernateUtilForCentral.getCurrentSession());
            centralCount = countAllMarkerTypes(Database.CENTRAL).intValue();
            
            if(centralCount > start) {
                markerTypes.addAll(dao.getAllMarkerTypes(start, numOfRows));
                relativeLimit = numOfRows - markerTypes.size();
                if(relativeLimit > 0 && hibernateUtilForLocal != null) {
                    dao.setSession(hibernateUtilForLocal.getCurrentSession());
                    localCount = countAllMarkerTypes(Database.LOCAL).intValue();
                    if(localCount > 0) {
                        markerTypes.addAll(dao.getAllMarkerTypes(0, relativeLimit));
                    }
                }
            } else {
                relativeLimit = (int) (start - centralCount);
                if (hibernateUtilForLocal != null) {
                    dao.setSession(hibernateUtilForLocal.getCurrentSession());
                    localCount = countAllMarkerTypes(Database.LOCAL).intValue();
                    if (localCount > relativeLimit) {
                        markerTypes.addAll(dao.getAllMarkerTypes(relativeLimit, numOfRows));
                    }
                }
            }
        } else if (hibernateUtilForLocal != null) {
            dao.setSession(hibernateUtilForLocal.getCurrentSession());
            localCount = countAllMarkerTypes(Database.LOCAL).intValue();
            if (localCount > start) {
                markerTypes.addAll(dao.getAllMarkerTypes(start, numOfRows));
            }
        }
        
        return markerTypes;
    }

    @Override
    public Long countAllMarkerTypes(Database instance) throws QueryException {
        MarkerDAO dao = new MarkerDAO();
        HibernateUtil util = getHibernateUtil(instance);
        Long result = 0L;
        
        if(util != null) {
            dao.setSession(util.getCurrentSession());
            result = dao.countAllMarkerTypes();
        }
        
        return result;
    }

    @Override
    public List<String> getMarkerNamesByMarkerType(String markerType, int start, int numOfRows) throws QueryException {
        MarkerDAO dao = new MarkerDAO();
        
        List<String> markerNames = new ArrayList<String>();
        
        int centralCount = 0;
        int localCount = 0;
        int relativeLimit = 0;
        
        if(hibernateUtilForCentral != null) {
            
            dao.setSession(hibernateUtilForCentral.getCurrentSession());
            centralCount = dao.countAll().intValue();
            
            if(centralCount > start) {
                markerNames.addAll(dao.getMarkerNamesByMarkerType(markerType, start, numOfRows));
                relativeLimit = numOfRows - markerNames.size();
                if(relativeLimit > 0 && hibernateUtilForLocal != null) {
                    dao.setSession(hibernateUtilForLocal.getCurrentSession());
                    localCount = dao.countAll().intValue();
                    if(localCount > 0) {
                        markerNames.addAll(dao.getMarkerNamesByMarkerType(markerType, 0, relativeLimit));
                    }
                }
            } else {
                relativeLimit = (int) (start - centralCount);
                if (hibernateUtilForLocal != null) {
                    dao.setSession(hibernateUtilForLocal.getCurrentSession());
                    localCount = dao.countAll().intValue();
                    if (localCount > relativeLimit) {
                        markerNames.addAll(dao.getMarkerNamesByMarkerType(markerType, relativeLimit, numOfRows));
                    }
                }
            }
        } else if (hibernateUtilForLocal != null) {
            dao.setSession(hibernateUtilForLocal.getCurrentSession());
            localCount = dao.countAll().intValue();
            if (localCount > start) {
                markerNames.addAll(dao.getMarkerNamesByMarkerType(markerType, start, numOfRows));
            }
        }
        
        return markerNames;
    }

    @Override
    public Long countMarkerNamesByMarkerType(String markerType, Database instance) 
        throws QueryException {

        MarkerDAO dao = new MarkerDAO();
        HibernateUtil util = getHibernateUtil(instance);
        Long result = 0L;
        
        if(util != null) {
            dao.setSession(util.getCurrentSession());
            result = dao.countMarkerNamesByMarkerType(markerType);
        }
        
        return result;
    }

    @Override
    public List<Integer> getGIDsFromCharValuesByMarkerId(Integer markerId, int start, int numOfRows)
        throws QueryException {
        
        CharValuesDAO dao = new CharValuesDAO();
        HibernateUtil util = getHibernateUtil(markerId);
        List<Integer> gids;
        
        if(util != null) {
            dao.setSession(util.getCurrentSession());
            gids = dao.getGIDsByMarkerId(markerId, start, numOfRows);
        } else {
            gids = new ArrayList<Integer>();
        }
        
        return gids;
    }

    @Override
    public Long countGIDsFromCharValuesByMarkerId(Integer markerId) throws QueryException {
        CharValuesDAO dao = new CharValuesDAO();
        HibernateUtil util = getHibernateUtil(markerId);
        Long result = 0L;
        
        if(util != null) {
            dao.setSession(util.getCurrentSession());
            result = dao.countGIDsByMarkerId(markerId);
        } 
        
        return result;
    }

    @Override
    public List<Integer> getGIDsFromAlleleValuesByMarkerId(Integer markerId, int start, int numOfRows) throws QueryException {
        AlleleValuesDAO dao = new AlleleValuesDAO();
        HibernateUtil util = getHibernateUtil(markerId);
        List<Integer> gids;
        
        if(util != null) {
            dao.setSession(util.getCurrentSession());
            gids = dao.getGIDsByMarkerId(markerId, start, numOfRows);
        } else {
            gids = new ArrayList<Integer>();
        }
        
        return gids;
    }

    @Override
    public Long countGIDsFromAlleleValuesByMarkerId(Integer markerId, int start, int numOfRows) throws QueryException {
        AlleleValuesDAO dao = new AlleleValuesDAO();
        HibernateUtil util = getHibernateUtil(markerId);
        Long result = 0L;
        
        if(util != null) {
            dao.setSession(util.getCurrentSession());
            result = dao.countGIDsByMarkerId(markerId);
        } 
        
        return result;
    }
    
    
}