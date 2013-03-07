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
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.generationcp.middleware.dao.NameDAO;
import org.generationcp.middleware.dao.UserDAO;
import org.generationcp.middleware.dao.gdms.AccMetadataSetDAO;
import org.generationcp.middleware.dao.gdms.AlleleValuesDAO;
import org.generationcp.middleware.dao.gdms.CharValuesDAO;
import org.generationcp.middleware.dao.gdms.DatasetDAO;
import org.generationcp.middleware.dao.gdms.DatasetUsersDAO;
import org.generationcp.middleware.dao.gdms.MapDAO;
import org.generationcp.middleware.dao.gdms.MappingDataDAO;
import org.generationcp.middleware.dao.gdms.MappingPopDAO;
import org.generationcp.middleware.dao.gdms.MappingPopValuesDAO;
import org.generationcp.middleware.dao.gdms.MarkerAliasDAO;
import org.generationcp.middleware.dao.gdms.MarkerDAO;
import org.generationcp.middleware.dao.gdms.MarkerDetailsDAO;
import org.generationcp.middleware.dao.gdms.MarkerInfoDAO;
import org.generationcp.middleware.dao.gdms.MarkerMetadataSetDAO;
import org.generationcp.middleware.dao.gdms.MarkerUserInfoDAO;
import org.generationcp.middleware.dao.gdms.QtlDAO;
import org.generationcp.middleware.dao.gdms.QtlDetailsDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.GenotypicDataManager;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.gdms.AccMetadataSet;
import org.generationcp.middleware.pojos.gdms.AccMetadataSetPK;
import org.generationcp.middleware.pojos.gdms.AllelicValueElement;
import org.generationcp.middleware.pojos.gdms.AllelicValueWithMarkerIdElement;
import org.generationcp.middleware.pojos.gdms.Dataset;
import org.generationcp.middleware.pojos.gdms.DatasetElement;
import org.generationcp.middleware.pojos.gdms.DatasetUsers;
import org.generationcp.middleware.pojos.gdms.GermplasmMarkerElement;
import org.generationcp.middleware.pojos.gdms.Map;
import org.generationcp.middleware.pojos.gdms.MapInfo;
import org.generationcp.middleware.pojos.gdms.MappingValueElement;
import org.generationcp.middleware.pojos.gdms.Marker;
import org.generationcp.middleware.pojos.gdms.MarkerAlias;
import org.generationcp.middleware.pojos.gdms.MarkerDetails;
import org.generationcp.middleware.pojos.gdms.MarkerIdMarkerNameElement;
import org.generationcp.middleware.pojos.gdms.MarkerInfo;
import org.generationcp.middleware.pojos.gdms.MarkerMetadataSet;
import org.generationcp.middleware.pojos.gdms.MarkerMetadataSetPK;
import org.generationcp.middleware.pojos.gdms.MarkerNameElement;
import org.generationcp.middleware.pojos.gdms.MarkerUserInfo;
import org.generationcp.middleware.pojos.gdms.ParentElement;
import org.generationcp.middleware.pojos.gdms.Qtl;
import org.generationcp.middleware.pojos.gdms.QtlDetailElement;
import org.generationcp.middleware.pojos.gdms.QtlDetails;
import org.generationcp.middleware.pojos.gdms.QtlDetailsPK;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Implementation of GenotypicDataManager
 * 
 * @author Joyce Avestro
 */
public class GenotypicDataManagerImpl extends DataManager implements GenotypicDataManager{

    public GenotypicDataManagerImpl() {
    }
    
    public GenotypicDataManagerImpl(HibernateSessionProvider sessionProviderForLocal, HibernateSessionProvider sessionProviderForCentral) {
        super(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    public GenotypicDataManagerImpl(Session sessionForLocal, Session sessionForCentral) {
        super(sessionForLocal, sessionForCentral);
    }

    @Override
    public List<Integer> getNameIdsByGermplasmIds(List<Integer> gIds) throws MiddlewareQueryException{
        AccMetadataSetDAO dao = new AccMetadataSetDAO();
        Session session = getSession(gIds.get(0));
        
        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<Integer>();
        }

        return (List<Integer>) dao.getNameIdsByGermplasmIds(gIds);
    }

    @Override
    public List<Name> getNamesByNameIds(List<Integer> nIds) throws MiddlewareQueryException {
        NameDAO nameDao = new NameDAO();
        Session session = getSession(nIds.get(0));

        if (session != null) {
            nameDao.setSession(session);
        } else {
            return new ArrayList<Name>();
        }

        return (List<Name>) nameDao.getNamesByNameIds(nIds);
    }

    @Override
    public Name getNameByNameId(Integer nId) throws MiddlewareQueryException {
        NameDAO dao = new NameDAO();
        Session session = getSession(nId);
        
        if(session != null) {
            dao.setSession(session);
        } else {
            return null;
        }
        
        return dao.getNameByNameId(nId);
    }

    @Override
    public long countAllMaps(Database instance) throws MiddlewareQueryException {
        MapDAO dao = new MapDAO();
        Session session = getSession(instance);
        
        if(session != null) {
            dao.setSession(session);
        } else {
            return 0;
        }
        
        return dao.countAll();
    }

    @Override
    public List<Map> getAllMaps(int start, int numOfRows, Database instance) throws MiddlewareQueryException {
        MapDAO dao = new MapDAO();
        Session session = getSession(instance);
        
        if(session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<Map>();
        }
        
        return dao.getAll(start, numOfRows);
    }
    
    @Override
    public List<MapInfo> getMapInfoByMapName(String mapName, Database instance) throws MiddlewareQueryException{
        MappingDataDAO dao = new MappingDataDAO();
        Session session = getSession(instance);
        
        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<MapInfo>();
        }
        return (List<MapInfo>) dao.getMapInfoByMapName(mapName);
    }
    
    @Override
    public long countDatasetNames(Database instance) throws MiddlewareQueryException{
        DatasetDAO dao = new DatasetDAO();
        Session session = getSession(instance);  
        
        if (session != null) {
            dao.setSession(session);
        } else {
            return 0;
        }
        return dao.countByName();
    }
    
    @Override
    public List<String> getDatasetNames(int start, int numOfRows, Database instance) throws MiddlewareQueryException{
        DatasetDAO dao = new DatasetDAO();
        Session session = getSession(instance);  
        
        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<String>();
        }
        return (List<String>) dao.getDatasetNames(start, numOfRows);
    }
    
    
    /* (non-Javadoc)
     * @see org.generationcp.middleware.manager.api.GenotypicDataManager#getDatasetDetailsByDatasetName(java.lang.String)
     */
    @Override
    public List<DatasetElement> getDatasetDetailsByDatasetName(String datasetName, Database instance) throws MiddlewareQueryException{
        DatasetDAO dao = new DatasetDAO();
        Session session = getSession(instance);  
        
        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<DatasetElement>();
        }
        return (List<DatasetElement>) dao.getDetailsByName(datasetName);
    }
    
    
    @Override
    public List<Integer> getMarkerIdsByMarkerNames(List<String> markerNames, int start, int numOfRows, Database instance) throws MiddlewareQueryException {
        MarkerDAO dao = new MarkerDAO();
        Session session = getSession(instance);
        
        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<Integer> ();
        }
        
        List<Integer> markerIds = dao.getIdsByNames(markerNames, start, numOfRows);
        
        return markerIds;
    }

    @Override
    public List<Integer> getMarkerIdsByDatasetId(Integer datasetId) throws MiddlewareQueryException{
        MarkerMetadataSetDAO dao = new MarkerMetadataSetDAO();
        Session session = getSession(datasetId);
        
        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<Integer>();
        }
        return (List<Integer>) dao.getMarkerIdByDatasetId(datasetId);
    }
    
    
    @Override
    public List<ParentElement> getParentsByDatasetId(Integer datasetId) throws MiddlewareQueryException{
        MappingPopDAO dao = new MappingPopDAO();
        Session session = getSession(datasetId);  
        
        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<ParentElement>();
        }
        return (List<ParentElement>) dao.getParentsByDatasetId(datasetId);
    }
    
    @Override
    public List<String> getMarkerTypesByMarkerIds(List<Integer> markerIds) throws MiddlewareQueryException{
        MarkerDAO dao = new MarkerDAO();
        Session session = getSession(markerIds.get(0));
        
        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<String>();
        }
        return (List<String>) dao.getMarkerTypeByMarkerIds(markerIds);
    }
    
    @Override
    public List<MarkerNameElement> getMarkerNamesByGIds(List<Integer> gIds) throws MiddlewareQueryException{
        MarkerDAO dao = new MarkerDAO();
        Session session = getSession(gIds.get(0));  
        
        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<MarkerNameElement>();
        }
        return (List<MarkerNameElement>) dao.getMarkerNamesByGIds(gIds);
    }
    
    @Override
    public List<GermplasmMarkerElement>  getGermplasmNamesByMarkerNames(List<String> markerNames, Database instance) throws MiddlewareQueryException{
        MarkerDAO dao = new MarkerDAO();
        Session session = getSession(instance);  
        
        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<GermplasmMarkerElement>();
        }
        return (List<GermplasmMarkerElement>) dao.getGermplasmNamesByMarkerNames(markerNames);
    }
    

    @Override
    public List<MappingValueElement> getMappingValuesByGidsAndMarkerNames(
            List<Integer> gids, List<String> markerNames, int start, int numOfRows) throws MiddlewareQueryException {
        MarkerDAO markerDao = new MarkerDAO();
        MappingPopDAO mappingPopDao = new MappingPopDAO();
        
        //get db connection based on the GIDs provided
        Database instance;
        if (gids.get(0) < 0) {
            instance = Database.LOCAL;
        } else {
            instance = Database.CENTRAL;
        }
        Session session = getSession(instance);
        
        if (session != null) {
            markerDao.setSession(session);
            mappingPopDao.setSession(session);
        } else {
            return new ArrayList<MappingValueElement>();
        }
        
        List<Integer> markerIds = markerDao.getIdsByNames(markerNames, start, numOfRows);
        
        List<MappingValueElement> mappingValues = mappingPopDao.getMappingValuesByGidAndMarkerIds(gids, markerIds);
        
        return mappingValues;
    }
    
    @Override
    public List<AllelicValueElement> getAllelicValuesByGidsAndMarkerNames(
            List<Integer> gids, List<String> markerNames) throws MiddlewareQueryException {
        MarkerDAO markerDao = new MarkerDAO();
        
        //get db connection based on the GIDs provided
        Database instance;
        if (gids.get(0) < 0) {
            instance = Database.LOCAL;
        } else {
            instance = Database.CENTRAL;
        }
        Session session = getSession(instance);
        
        if (session != null) {
            markerDao.setSession(session);
        } else {
            return new ArrayList<AllelicValueElement>();
        }
        
        List<AllelicValueElement> allelicValues = markerDao.getAllelicValuesByGidsAndMarkerNames(gids, markerNames);
        
        return allelicValues;
    }

    @Override
    public List<AllelicValueWithMarkerIdElement> getAllelicValuesFromCharValuesByDatasetId(
            Integer datasetId, int start, int numOfRows) throws MiddlewareQueryException{
        CharValuesDAO dao = new CharValuesDAO();
        Session session = getSession(datasetId);
        if (session == null) {
            return new ArrayList<AllelicValueWithMarkerIdElement>();
        }
        dao.setSession(session);        
        return dao.getAllelicValuesByDatasetId(datasetId, start, numOfRows);
    }
    
    @Override
    public long countAllelicValuesFromCharValuesByDatasetId(Integer datasetId) throws MiddlewareQueryException{
        CharValuesDAO dao = new CharValuesDAO();
        Session session = getSession(datasetId);
        if (session == null) {
            return 0;
        }
        dao.setSession(session);
        return dao.countByDatasetId(datasetId);
    }

    @Override
    public List<AllelicValueWithMarkerIdElement> getAllelicValuesFromAlleleValuesByDatasetId(
            Integer datasetId, int start, int numOfRows) throws MiddlewareQueryException{
        AlleleValuesDAO dao = new AlleleValuesDAO();
        Session session = getSession(datasetId);

        if (session == null) {
            return new ArrayList<AllelicValueWithMarkerIdElement>();
        }
        dao.setSession(session);
        return dao.getAllelicValuesByDatasetId(datasetId, start, numOfRows);
    }
    
    
    @Override
    public long countAllelicValuesFromAlleleValuesByDatasetId(Integer datasetId) throws MiddlewareQueryException{
        AlleleValuesDAO dao = new AlleleValuesDAO();
        Session session = getSession(datasetId);
        if (session == null) {
            return 0;
        }
        dao.setSession(session);
        return dao.countByDatasetId(datasetId);
    }

    @Override
    public List<AllelicValueWithMarkerIdElement> getAllelicValuesFromMappingPopValuesByDatasetId(
            Integer datasetId, int start, int numOfRows) throws MiddlewareQueryException{
        MappingPopValuesDAO dao = new MappingPopValuesDAO();
        Session session = getSession(datasetId);
        if (session == null) {
            return  new ArrayList<AllelicValueWithMarkerIdElement>();
        }
        dao.setSession(session);
        return dao.getAllelicValuesByDatasetId(datasetId, start, numOfRows);
    }
    
    @Override
    public long countAllelicValuesFromMappingPopValuesByDatasetId(Integer datasetId) throws MiddlewareQueryException{
        MappingPopValuesDAO dao = new MappingPopValuesDAO();
        Session session = getSession(datasetId);
        if (session == null) {
            return 0;
        }
        dao.setSession(session);
        return dao.countByDatasetId(datasetId);
    }
    @Override
    public List<MarkerInfo> getMarkerInfoByMarkerName(String markerName, int start, int numOfRows) throws MiddlewareQueryException{

        MarkerInfoDAO dao = new MarkerInfoDAO();
        List<MarkerInfo> markerInfoList = new ArrayList<MarkerInfo>();
        
        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;
        
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        if (sessionForCentral != null) {
                
            dao.setSession(sessionForCentral);
            centralCount = dao.countByMarkerName(markerName);
            
            if (centralCount > start) {  
                
                markerInfoList.addAll((List<MarkerInfo>) dao.getByMarkerName(markerName, start, numOfRows));
                relativeLimit = numOfRows - (centralCount - start);
                
                if (relativeLimit > 0) {
                        if (sessionForLocal != null) {                                
                            dao.setSession(sessionForLocal);
                            localCount = dao.countByMarkerName(markerName);
                            
                            if (localCount > 0) {
                                markerInfoList.addAll((List<MarkerInfo>) dao.getByMarkerName(markerName, 0, (int) relativeLimit));
                            }  
                        }
                }
                
            } else {
                
                relativeLimit = start - centralCount;
                
                if (sessionForLocal != null) {
                        
                    dao.setSession(sessionForLocal);
                    localCount = dao.countByMarkerName(markerName);
                    
                    if (localCount > relativeLimit) {
                        markerInfoList.addAll((List<MarkerInfo>) dao.getByMarkerName(markerName, (int) relativeLimit, numOfRows));
                    }  
                }
            }
            
        } else if (sessionForLocal != null) {
                
            dao.setSession(sessionForLocal);
            localCount = dao.countByMarkerName(markerName);

            if (localCount > start) {
                markerInfoList.addAll((List<MarkerInfo>) dao.getByMarkerName(markerName, start, numOfRows));
            }
        }
        
        return markerInfoList;
    }
    
    @Override
    public long countMarkerInfoByMarkerName(String markerName) throws MiddlewareQueryException{
        long count = 0;        
        MarkerInfoDAO dao = new MarkerInfoDAO();
        
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            count = count + dao.countByMarkerName(markerName);
        }
        
        if (sessionForCentral != null) {
            dao.setSession(sessionForCentral);
            count = count + dao.countByMarkerName(markerName);
        }
        
        return count;
    }
    
    @Override
   public List<MarkerInfo> getMarkerInfoByGenotype(String genotype, int start, int numOfRows) throws MiddlewareQueryException{

        MarkerInfoDAO dao = new MarkerInfoDAO();
        List<MarkerInfo> markerInfoList = new ArrayList<MarkerInfo>();
        
        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;
        
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        if (sessionForCentral != null) {
                
            dao.setSession(sessionForCentral);
            centralCount = dao.countByGenotype(genotype);
            
            if (centralCount > start) {  
                
                markerInfoList.addAll((List<MarkerInfo>) dao.getByGenotype(genotype, start, numOfRows));
                relativeLimit = numOfRows - (centralCount - start);
                
                if (relativeLimit > 0) {
                        if (sessionForLocal != null) {                                
                            dao.setSession(sessionForLocal);
                            localCount = dao.countByGenotype(genotype);
                            
                            if (localCount > 0) {
                                markerInfoList.addAll((List<MarkerInfo>) dao.getByGenotype(genotype, 0, (int) relativeLimit));
                            }  
                        }
                }
                
            } else {
                
                relativeLimit = start - centralCount;
                
                if (sessionForLocal != null) {
                        
                    dao.setSession(sessionForLocal);
                    localCount = dao.countByGenotype(genotype);
                    
                    if (localCount > relativeLimit) {
                        markerInfoList.addAll((List<MarkerInfo>) dao.getByGenotype(genotype, (int) relativeLimit, numOfRows));
                    }  
                }
            }
            
        } else if (sessionForLocal != null) {
                
            dao.setSession(sessionForLocal);
            localCount = dao.countByGenotype(genotype);

            if (localCount > start) {
                markerInfoList.addAll((List<MarkerInfo>) dao.getByGenotype(genotype, start, numOfRows));
            }
        }
        
        return markerInfoList;
    }
    
    @Override
    public long countMarkerInfoByGenotype(String genotype) throws MiddlewareQueryException{
        long count = 0;        
        MarkerInfoDAO dao = new MarkerInfoDAO();
        
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            count = count + dao.countByGenotype(genotype);
        }
        
        if (sessionForCentral != null) {
            dao.setSession(sessionForCentral);
            count = count + dao.countByGenotype(genotype);
        }
        
        return count;
    }

    @Override
   public List<MarkerInfo> getMarkerInfoByDbAccessionId(String dbAccessionId, int start, int numOfRows) throws MiddlewareQueryException{

        MarkerInfoDAO dao = new MarkerInfoDAO();
        List<MarkerInfo> markerInfoList = new ArrayList<MarkerInfo>();
        
        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;
        
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        if (sessionForCentral != null) {
                
            dao.setSession(sessionForCentral);
            centralCount = dao.countByDbAccessionId(dbAccessionId);
            
            if (centralCount > start) {  
                
                markerInfoList.addAll((List<MarkerInfo>) dao.getByDbAccessionId(dbAccessionId, start, numOfRows));
                relativeLimit = numOfRows - (centralCount - start);
                
                if (relativeLimit > 0) {
                        if (sessionForLocal != null) {                                
                            dao.setSession(sessionForLocal);
                            localCount = dao.countByDbAccessionId(dbAccessionId);
                            
                            if (localCount > 0) {
                                markerInfoList.addAll((List<MarkerInfo>) dao.getByDbAccessionId(dbAccessionId, 0, (int) relativeLimit));
                            }  
                        }
                }
                
            } else {
                
                relativeLimit = start - centralCount;
                
                if (sessionForLocal != null) {
                        
                    dao.setSession(sessionForLocal);
                    localCount = dao.countByDbAccessionId(dbAccessionId);
                    
                    if (localCount > relativeLimit) {
                        markerInfoList.addAll((List<MarkerInfo>) dao.getByDbAccessionId(dbAccessionId, (int) relativeLimit, numOfRows));
                    }  
                }
            }
            
        } else if (sessionForLocal != null) {
                
            dao.setSession(sessionForLocal);
            localCount = dao.countByDbAccessionId(dbAccessionId);

            if (localCount > start) {
                markerInfoList.addAll((List<MarkerInfo>) dao.getByDbAccessionId(dbAccessionId, start, numOfRows));
            }
        }
        
        return markerInfoList;
    }
    
    @Override
    public long countMarkerInfoByDbAccessionId(String dbAccessionId) throws MiddlewareQueryException{
        long count = 0;        
        MarkerInfoDAO dao = new MarkerInfoDAO();
        
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            count = count + dao.countByDbAccessionId(dbAccessionId);
        }
        
        if (sessionForCentral != null) {
            dao.setSession(sessionForCentral);
            count = count + dao.countByDbAccessionId(dbAccessionId);
        }
        
        return count;
    }

    
    @Override
    public List<MarkerIdMarkerNameElement> getMarkerNamesByMarkerIds(List<Integer> markerIds)
            throws MiddlewareQueryException {
        MarkerDAO dao = new MarkerDAO();
        Session session = getSession(markerIds.get(0));
        
        if (session != null) {
            dao.setSession(session);
        } else {
            return new ArrayList<MarkerIdMarkerNameElement>();
        }
        
        List<MarkerIdMarkerNameElement> dataValues = dao.getNamesByIds(markerIds);
        return dataValues;
    }

    @Override
    public List<String> getAllMarkerTypes(int start, int numOfRows) throws MiddlewareQueryException {
        MarkerDAO dao = new MarkerDAO();
        List<String> markerTypes = new ArrayList<String>();
        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;
        
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        if(sessionForCentral != null) {
            
            dao.setSession(sessionForCentral);
            centralCount = countAllMarkerTypes(Database.CENTRAL);
            
            if(centralCount > start) {
                markerTypes.addAll(dao.getAllMarkerTypes(start, numOfRows));
                relativeLimit = numOfRows - markerTypes.size();
                if(relativeLimit > 0 && sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = countAllMarkerTypes(Database.LOCAL);
                    if(localCount > 0) {
                        markerTypes.addAll(dao.getAllMarkerTypes(0, (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = countAllMarkerTypes(Database.LOCAL);
                    if (localCount > relativeLimit) {
                        markerTypes.addAll(dao.getAllMarkerTypes((int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            localCount = countAllMarkerTypes(Database.LOCAL);
            if (localCount > start) {
                markerTypes.addAll(dao.getAllMarkerTypes(start, numOfRows));
            }
        }
        
        return markerTypes;
    }

    @Override
    public long countAllMarkerTypes(Database instance) throws MiddlewareQueryException {
        MarkerDAO dao = new MarkerDAO();
        Session session = getSession(instance);
        long result = 0;
        
        if(session != null) {
            dao.setSession(session);
            result = dao.countAllMarkerTypes();
        }
        
        return result;
    }

    @Override
    public List<String> getMarkerNamesByMarkerType(String markerType, int start, int numOfRows) throws MiddlewareQueryException {
        MarkerDAO dao = new MarkerDAO();
        
        List<String> markerNames = new ArrayList<String>();
        
        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;
        
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        if(sessionForCentral != null) {
            
            dao.setSession(sessionForCentral);
            centralCount = dao.countMarkerNamesByMarkerType(markerType);
            
            if(centralCount > start) {
                markerNames.addAll(dao.getMarkerNamesByMarkerType(markerType, start, numOfRows));
                relativeLimit = numOfRows - markerNames.size();
                if(relativeLimit > 0 && sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countMarkerNamesByMarkerType(markerType);
                    if(localCount > 0) {
                        markerNames.addAll(dao.getMarkerNamesByMarkerType(markerType, 0, (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countMarkerNamesByMarkerType(markerType);
                    if (localCount > relativeLimit) {
                        markerNames.addAll(dao.getMarkerNamesByMarkerType(markerType, (int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            localCount = dao.countMarkerNamesByMarkerType(markerType);
            if (localCount > start) {
                markerNames.addAll(dao.getMarkerNamesByMarkerType(markerType, start, numOfRows));
            }
        }
        
        return markerNames;
    }

    @Override
    public long countMarkerNamesByMarkerType(String markerType) 
        throws MiddlewareQueryException {

        MarkerDAO dao = new MarkerDAO();
        long result = 0;
        
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        if(sessionForCentral != null) {
            dao.setSession(sessionForCentral);
            result = dao.countMarkerNamesByMarkerType(markerType);
        }
        
        if(sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            result += dao.countMarkerNamesByMarkerType(markerType);
        }
        
        return result;
    }

    @Override
    public List<Integer> getGIDsFromCharValuesByMarkerId(Integer markerId, int start, int numOfRows)
        throws MiddlewareQueryException {
        
        CharValuesDAO dao = new CharValuesDAO();
        Session session = getSession(markerId);
        List<Integer> gids;
        
        if(session != null) {
            dao.setSession(session);
            gids = dao.getGIDsByMarkerId(markerId, start, numOfRows);
        } else {
            gids = new ArrayList<Integer>();
        }
        
        return gids;
    }

    @Override
    public long countGIDsFromCharValuesByMarkerId(Integer markerId) throws MiddlewareQueryException {
        CharValuesDAO dao = new CharValuesDAO();
        Session session = getSession(markerId);
        long result = 0;
        
        if(session != null) {
            dao.setSession(session);
            result = dao.countGIDsByMarkerId(markerId);
        } 
        
        return result;
    }

    @Override
    public List<Integer> getGIDsFromAlleleValuesByMarkerId(Integer markerId, int start, int numOfRows) throws MiddlewareQueryException {
        AlleleValuesDAO dao = new AlleleValuesDAO();
        Session session = getSession(markerId);
        List<Integer> gids;
        
        if(session != null) {
            dao.setSession(session);
            gids = dao.getGIDsByMarkerId(markerId, start, numOfRows);
        } else {
            gids = new ArrayList<Integer>();
        }
        
        return gids;
    }

    @Override
    public long countGIDsFromAlleleValuesByMarkerId(Integer markerId) throws MiddlewareQueryException {
        AlleleValuesDAO dao = new AlleleValuesDAO();
        Session session = getSession(markerId);
        long result = 0;
        
        if(session != null) {
            dao.setSession(session);
            result = dao.countGIDsByMarkerId(markerId);
        } 
        
        return result;
    }
    
    @Override
    public List<Integer> getGIDsFromMappingPopValuesByMarkerId(Integer markerId, int start, int numOfRows) throws MiddlewareQueryException {
        MappingPopValuesDAO dao = new MappingPopValuesDAO();
        Session session = getSession(markerId);
        List<Integer> gids;
        
        if(session != null) {
            dao.setSession(session);
            gids = dao.getGIDsByMarkerId(markerId, start, numOfRows);
        } else {
            gids = new ArrayList<Integer>();
        }
        
        return gids;
    }

    @Override
    public long countGIDsFromMappingPopValuesByMarkerId(Integer markerId) throws MiddlewareQueryException {
        MappingPopValuesDAO dao = new MappingPopValuesDAO();
        Session session = getSession(markerId);
        long result = 0;
        
        if(session != null) {
            dao.setSession(session);
            result = dao.countGIDsByMarkerId(markerId);
        } 
        
        return result;
    }

    @Override
    public List<String> getAllDbAccessionIdsFromMarker(int start, int numOfRows) throws MiddlewareQueryException {
        MarkerDAO dao = new MarkerDAO();
        List<String> dbAccessionIds = new ArrayList<String>();
        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;
        
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        if(sessionForCentral != null) {
            
            dao.setSession(sessionForCentral);
            centralCount = dao.countAllDbAccessionIds();
            
            if(centralCount > start) {
                dbAccessionIds.addAll(dao.getAllDbAccessionIds(start, numOfRows));
                relativeLimit = numOfRows - dbAccessionIds.size();
                if(relativeLimit > 0 && sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countAllDbAccessionIds();
                    if(localCount > 0) {
                        dbAccessionIds.addAll(dao.getAllDbAccessionIds(0, (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countAllDbAccessionIds();
                    if (localCount > relativeLimit) {
                        dbAccessionIds.addAll(dao.getAllDbAccessionIds((int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            localCount = dao.countAllDbAccessionIds();
            if (localCount > start) {
                dbAccessionIds.addAll(dao.getAllDbAccessionIds(start, numOfRows));
            }
        }
        
        return dbAccessionIds;
    }

    @Override
    public long countAllDbAccessionIdsFromMarker() throws MiddlewareQueryException {
        MarkerDAO dao = new MarkerDAO();
        long result = 0;
        
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        if(sessionForCentral != null) {
            dao.setSession(sessionForCentral);
            result = dao.countAllDbAccessionIds();
        }
        
        if(sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            result += dao.countAllDbAccessionIds();
        }
        
        return result;
    }

    @Override
    public List<Integer> getNidsFromAccMetadatasetByDatasetIds(List<Integer> datasetIds, 
                                                               int start, int numOfRows) throws MiddlewareQueryException {
        return getNidsFromAccMetadatasetByDatasetIds(datasetIds, null, start, numOfRows);
    }

    @Override
    public List<Integer> getNidsFromAccMetadatasetByDatasetIds(List<Integer> datasetIds, 
                                                               List<Integer> gids, 
                                                               int start, int numOfRows) throws MiddlewareQueryException {
        
        AccMetadataSetDAO dao = new AccMetadataSetDAO();
        Session session = getSession(datasetIds.get(0));
        List<Integer> nids;
        
        if(session != null) {
            dao.setSession(session);
            nids = dao.getNIDsByDatasetIds(datasetIds, gids, start, numOfRows);
        } else {
            nids = new ArrayList<Integer>();
        }
        
        return nids;
    }
    
    @Override
    public List<Integer> getNIdsByMarkerIdsAndDatasetIdsAndNotGIds(List<Integer> datasetIds, 
        List<Integer> markerIds, List<Integer> gIds,
        int start, int numOfRows) throws MiddlewareQueryException {
        
        Set<Integer> nidSet = new TreeSet<Integer>();
        
        AccMetadataSetDAO dao = new AccMetadataSetDAO();

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        if(sessionForCentral != null) {
            dao.setSession(sessionForCentral);
            nidSet.addAll(dao.getNIdsByMarkerIdsAndDatasetIdsAndNotGIds(datasetIds, markerIds, gIds));
        }
        
        if(sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            nidSet.addAll(dao.getNIdsByMarkerIdsAndDatasetIdsAndNotGIds(datasetIds, markerIds, gIds));
        }
        
        List<Integer> nidList = new ArrayList<Integer>(((TreeSet<Integer>)nidSet).descendingSet());
        
        return nidList.subList(start, start+numOfRows);
    }
    
    @Override
    public int countNIdsByMarkerIdsAndDatasetIdsAndNotGIds(List<Integer> datasetIds, 
        List<Integer> markerIds, List<Integer> gIds) throws MiddlewareQueryException {
        
        Set<Integer> nidSet = new TreeSet<Integer>();
        
        AccMetadataSetDAO dao = new AccMetadataSetDAO();

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        if(sessionForCentral != null) {
            dao.setSession(sessionForCentral);
            nidSet.addAll(dao.getNIdsByMarkerIdsAndDatasetIdsAndNotGIds(datasetIds, markerIds, gIds));
        }
        
        if(sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            nidSet.addAll(dao.getNIdsByMarkerIdsAndDatasetIdsAndNotGIds(datasetIds, markerIds, gIds));
        }
        
        List<Integer> nidList = new ArrayList<Integer>(nidSet);
        
        return nidList.size();
    }
    
    @Override
    public int countNIdsByMarkerIdsAndDatasetIds(List<Integer> datasetIds, 
        List<Integer> markerIds) throws MiddlewareQueryException {
        
        Set<Integer> nidSet = new TreeSet<Integer>();
        
        AccMetadataSetDAO dao = new AccMetadataSetDAO();

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        if(sessionForCentral != null) {
            dao.setSession(sessionForCentral);
            nidSet.addAll(dao.getNIdsByMarkerIdsAndDatasetIds(datasetIds, markerIds));
        }
        
        if(sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            nidSet.addAll(dao.getNIdsByMarkerIdsAndDatasetIds(datasetIds, markerIds));
        }
        
        List<Integer> nidList = new ArrayList<Integer>(nidSet);
        
        return nidList.size();
    }
    
    @Override
    public List<Integer> getNIdsByMarkerIdsAndDatasetIds(List<Integer> datasetIds, 
        List<Integer> markerIds,
        int start, int numOfRows) throws MiddlewareQueryException {
        
        Set<Integer> nidSet = new TreeSet<Integer>();
        
        AccMetadataSetDAO dao = new AccMetadataSetDAO();

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        if(sessionForCentral != null) {
            dao.setSession(sessionForCentral);
            nidSet.addAll(dao.getNIdsByMarkerIdsAndDatasetIds(datasetIds, markerIds));
        }
        
        if(sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            nidSet.addAll(dao.getNIdsByMarkerIdsAndDatasetIds(datasetIds, markerIds));
        }
        
        List<Integer> nidList = new ArrayList<Integer>(((TreeSet<Integer>)nidSet).descendingSet());
        
        return nidList.subList(start, start+numOfRows);
    }

    @Override
    public List<Integer> getDatasetIdsForFingerPrinting(int start, int numOfRows) throws MiddlewareQueryException{
        DatasetDAO dao = new DatasetDAO();
        List<Integer> datasetIds = new ArrayList<Integer>();
        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;
        
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        if(sessionForCentral != null) {
            
            dao.setSession(sessionForCentral);
            centralCount = dao.countDatasetIdsForFingerPrinting();
            
            if(centralCount > start) {
                datasetIds.addAll(dao.getDatasetIdsForFingerPrinting(start, numOfRows));
                relativeLimit = numOfRows - datasetIds.size();
                if(relativeLimit > 0 && sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countDatasetIdsForFingerPrinting();
                    if(localCount > 0) {
                        datasetIds.addAll(dao.getDatasetIdsForFingerPrinting(0, (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countDatasetIdsForFingerPrinting();
                    if (localCount > relativeLimit) {
                        datasetIds.addAll(dao.getDatasetIdsForFingerPrinting((int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            localCount = dao.countDatasetIdsForFingerPrinting();
            if (localCount > start) {
                datasetIds.addAll(dao.getDatasetIdsForFingerPrinting(start, numOfRows));
            }
        }
        
        return datasetIds;
        
    }
    
    @Override
    public long countDatasetIdsForFingerPrinting() throws MiddlewareQueryException{
        DatasetDAO dao = new DatasetDAO();
        long result = 0;
        
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        if(sessionForCentral != null) {
            dao.setSession(sessionForCentral);
            result = dao.countDatasetIdsForFingerPrinting();
        }
        
        if(sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            result += dao.countDatasetIdsForFingerPrinting();
        }
        
        return result;
    }

    @Override
    public List<Integer> getDatasetIdsForMapping(int start, int numOfRows) throws MiddlewareQueryException{
        DatasetDAO dao = new DatasetDAO();
        List<Integer> datasetIds = new ArrayList<Integer>();
        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;
        
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        if(sessionForCentral != null) {
            
            dao.setSession(sessionForCentral);
            centralCount = dao.countDatasetIdsForMapping();
            
            if(centralCount > start) {
                datasetIds.addAll(dao.getDatasetIdsForMapping(start, numOfRows));
                relativeLimit = numOfRows - datasetIds.size();
                if(relativeLimit > 0 && sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countDatasetIdsForMapping();
                    if(localCount > 0) {
                        datasetIds.addAll(dao.getDatasetIdsForMapping(0, (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countDatasetIdsForMapping();
                    if (localCount > relativeLimit) {
                        datasetIds.addAll(dao.getDatasetIdsForMapping((int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            localCount = dao.countDatasetIdsForMapping();
            if (localCount > start) {
                datasetIds.addAll(dao.getDatasetIdsForMapping(start, numOfRows));
            }
        }
        
        return datasetIds;
        
    }
    
    @Override
    public long countDatasetIdsForMapping() throws MiddlewareQueryException{
        DatasetDAO dao = new DatasetDAO();
        long result = 0;
        
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        if(sessionForCentral != null) {
            dao.setSession(sessionForCentral);
            result = dao.countDatasetIdsForMapping();
        }
        
        if(sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            result += dao.countDatasetIdsForMapping();
        }
        
        return result;
    }

    @Override
    public List<AccMetadataSetPK> getGdmsAccMetadatasetByGid(List<Integer> gids, int start, int numOfRows) throws MiddlewareQueryException{
       
        AccMetadataSetDAO dao = new AccMetadataSetDAO();

        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        List<Integer> positiveGids = new ArrayList<Integer>();
        List<Integer> negativeGids = new ArrayList<Integer>();
        for (Integer gid : gids){
            if (gid < 0) {
                negativeGids.add(gid);
            } else {
                positiveGids.add(gid);
            }
        }

        List<AccMetadataSetPK> accMetadataSets = new ArrayList<AccMetadataSetPK>();

        if ((sessionForCentral != null) && (positiveGids != null) && (!positiveGids.isEmpty())) {
            
            dao.setSession(sessionForCentral);
            centralCount = dao.countAccMetadataSetByGids(positiveGids);
            
            if(centralCount > start) {
                accMetadataSets.addAll(dao.getAccMetadasetByGids(positiveGids, start, numOfRows));
                relativeLimit = numOfRows - accMetadataSets.size();
                if(relativeLimit > 0 && sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countAccMetadataSetByGids(negativeGids);
                    if(localCount > 0) {
                        accMetadataSets.addAll(dao.getAccMetadasetByGids(negativeGids, 0, (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if ((sessionForLocal != null) && (negativeGids != null) && (!negativeGids.isEmpty())){
                    dao.setSession(sessionForLocal);
                    localCount = dao.countAccMetadataSetByGids(negativeGids);
                    if (localCount > relativeLimit) {
                        accMetadataSets.addAll(dao.getAccMetadasetByGids(negativeGids, (int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if ((sessionForLocal != null) && (negativeGids != null) && (!negativeGids.isEmpty())) {
            dao.setSession(sessionForLocal);
            localCount = dao.countAccMetadataSetByGids(negativeGids);
            if (localCount > start) {
                accMetadataSets.addAll(dao.getAccMetadasetByGids(negativeGids, start, numOfRows));
            }
        }
        
        return accMetadataSets;

    }
    
    @Override
    public long countGdmsAccMetadatasetByGid(List<Integer> gids) throws MiddlewareQueryException{
        
        AccMetadataSetDAO dao = new AccMetadataSetDAO();

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        List<Integer> positiveGids = new ArrayList<Integer>();
        List<Integer> negativeGids = new ArrayList<Integer>();
        for (Integer gid : gids){
            if (gid < 0) {
                negativeGids.add(gid);
            } else {
                positiveGids.add(gid);
            }
        }

        long result = 0;

        // Count from local
        if ((sessionForLocal != null) && (negativeGids != null) && (!negativeGids.isEmpty())){
            dao.setSession(sessionForLocal);
            result += dao.countAccMetadataSetByGids(negativeGids);
        }

        // Count from central
        if ((sessionForCentral != null) && (positiveGids != null) && (!positiveGids.isEmpty())) {
            dao.setSession(sessionForCentral);
            result += dao.countAccMetadataSetByGids(positiveGids);
        }

        return result;

    }

    @Override
    public List<Integer> getMarkersByGidAndDatasetIds(Integer gid, List<Integer> datasetIds, int start, int numOfRows) throws MiddlewareQueryException{
        MarkerMetadataSetDAO dao = new MarkerMetadataSetDAO();
        List<Integer> markerIds = new ArrayList<Integer>();
        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;
        
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        if(sessionForCentral != null) {
            
            dao.setSession(sessionForCentral);
            centralCount = dao.countMarkersByGidAndDatasetIds(gid, datasetIds);
            
            if(centralCount > start) {
                markerIds.addAll(dao.getMarkersByGidAndDatasetIds(gid, datasetIds, start, numOfRows));
                relativeLimit = numOfRows - markerIds.size();
                if(relativeLimit > 0 && sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countMarkersByGidAndDatasetIds(gid, datasetIds);
                    if(localCount > 0) {
                        markerIds.addAll(dao.getMarkersByGidAndDatasetIds(gid, datasetIds, 0, (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countMarkersByGidAndDatasetIds(gid, datasetIds);
                    if (localCount > relativeLimit) {
                        markerIds.addAll(dao.getMarkersByGidAndDatasetIds(gid, datasetIds, (int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            localCount = dao.countMarkersByGidAndDatasetIds(gid, datasetIds);
            if (localCount > start) {
                markerIds.addAll(dao.getMarkersByGidAndDatasetIds(gid, datasetIds, start, numOfRows));
            }
        }
        
        return markerIds;
    }

    @Override
    public long countMarkersByGidAndDatasetIds(Integer gid, List<Integer> datasetIds) throws MiddlewareQueryException{
        MarkerMetadataSetDAO dao = new MarkerMetadataSetDAO();
        long result = 0;
        
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        if(sessionForCentral != null) {
            dao.setSession(sessionForCentral);
            result = dao.countMarkersByGidAndDatasetIds(gid, datasetIds);
        }
        
        if(sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            result += dao.countMarkersByGidAndDatasetIds(gid, datasetIds);
        }
        
        return result;
        
    }
    
    @Override
    public long countAlleleValuesByGids(List<Integer> gids) throws MiddlewareQueryException{

        AlleleValuesDAO dao = new AlleleValuesDAO();

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        long result = 0;

        // Count from local
        if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            result += dao.countAlleleValuesByGids(gids);
        }

        // Count from central
        if (sessionForCentral != null) {
            dao.setSession(sessionForCentral);
            result += dao.countAlleleValuesByGids(gids);
        }

        return result;
    }


    @Override
    public long countCharValuesByGids(List<Integer> gids) throws MiddlewareQueryException{

        CharValuesDAO dao = new CharValuesDAO();

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        long result = 0;

        // Count from local
        if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            result += dao.countCharValuesByGids(gids);
        }

        // Count from central
        if (sessionForCentral != null) {
            dao.setSession(sessionForCentral);
            result += dao.countCharValuesByGids(gids);
        }

        return result;
    }


        

    @Override
    public List<AllelicValueElement> getIntAlleleValuesForPolymorphicMarkersRetrieval(List<Integer> gids, int start, int numOfRows) throws MiddlewareQueryException{
        AlleleValuesDAO dao = new AlleleValuesDAO();

        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        List<Integer> positiveGids = new ArrayList<Integer>();
        List<Integer> negativeGids = new ArrayList<Integer>();
        for (Integer gid : gids){
            if (gid < 0) {
                negativeGids.add(gid);
            } else {
                positiveGids.add(gid);
            }
        }

        List<AllelicValueElement> allelicValueElements = new ArrayList<AllelicValueElement>();

        if ((sessionForCentral != null) && (positiveGids != null) && (!positiveGids.isEmpty())) {
            
            dao.setSession(sessionForCentral);
            centralCount = dao.countIntAlleleValuesForPolymorphicMarkersRetrieval(positiveGids);
            
            if(centralCount > start) {
                allelicValueElements.addAll(dao.getIntAlleleValuesForPolymorphicMarkersRetrieval(positiveGids, start, numOfRows));
                relativeLimit = numOfRows - allelicValueElements.size();
                if(relativeLimit > 0 && sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countIntAlleleValuesForPolymorphicMarkersRetrieval(negativeGids);
                    if(localCount > 0) {
                        allelicValueElements.addAll(dao.getIntAlleleValuesForPolymorphicMarkersRetrieval(negativeGids, 0, (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if ((sessionForLocal != null) && (negativeGids != null) && (!negativeGids.isEmpty())){
                    dao.setSession(sessionForLocal);
                    localCount = dao.countIntAlleleValuesForPolymorphicMarkersRetrieval(negativeGids);
                    if (localCount > relativeLimit) {
                        allelicValueElements.addAll(dao.getIntAlleleValuesForPolymorphicMarkersRetrieval(negativeGids, (int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if ((sessionForLocal != null) && (negativeGids != null) && (!negativeGids.isEmpty())) {
            dao.setSession(sessionForLocal);
            localCount = dao.countIntAlleleValuesForPolymorphicMarkersRetrieval(negativeGids);
            if (localCount > start) {
                allelicValueElements.addAll(dao.getIntAlleleValuesForPolymorphicMarkersRetrieval(negativeGids, start, numOfRows));
            }
        }
        
        //Sort by gid, markerName
        Collections.sort(allelicValueElements, AllelicValueElement.AllelicValueElementComparator);

        return allelicValueElements;
    }

    
    @Override
    public long countIntAlleleValuesForPolymorphicMarkersRetrieval(List<Integer> gids) throws MiddlewareQueryException{
        AlleleValuesDAO dao = new AlleleValuesDAO();

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        List<Integer> positiveGids = new ArrayList<Integer>();
        List<Integer> negativeGids = new ArrayList<Integer>();
        for (Integer gid : gids){
            if (gid < 0) {
                negativeGids.add(gid);
            } else {
                positiveGids.add(gid);
            }
        }

        long result = 0;

        // Count from local
        if ((sessionForLocal != null) && (negativeGids != null) && (!negativeGids.isEmpty())){
            dao.setSession(sessionForLocal);
            result += dao.countIntAlleleValuesForPolymorphicMarkersRetrieval(negativeGids);
        }

        // Count from central
        if ((sessionForCentral != null) && (positiveGids != null) && (!positiveGids.isEmpty())) {
            dao.setSession(sessionForCentral);
            result += dao.countIntAlleleValuesForPolymorphicMarkersRetrieval(positiveGids);
        }

        return result;
    }


    @Override
    public List<AllelicValueElement> getCharAlleleValuesForPolymorphicMarkersRetrieval(List<Integer> gids, int start, int numOfRows) throws MiddlewareQueryException{
        AlleleValuesDAO dao = new AlleleValuesDAO();

        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        List<Integer> positiveGids = new ArrayList<Integer>();
        List<Integer> negativeGids = new ArrayList<Integer>();
        for (Integer gid : gids){
            if (gid < 0) {
                negativeGids.add(gid);
            } else {
                positiveGids.add(gid);
            }
        }

        List<AllelicValueElement> allelicValueElements = new ArrayList<AllelicValueElement>();

        if ((sessionForCentral != null) && (positiveGids != null) && (!positiveGids.isEmpty())) {
            
            dao.setSession(sessionForCentral);
            centralCount = dao.countCharAlleleValuesForPolymorphicMarkersRetrieval(positiveGids);
            
            if(centralCount > start) {
                allelicValueElements.addAll(dao.getCharAlleleValuesForPolymorphicMarkersRetrieval(positiveGids, start, numOfRows));
                relativeLimit = numOfRows - allelicValueElements.size();
                if(relativeLimit > 0 && sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countCharAlleleValuesForPolymorphicMarkersRetrieval(negativeGids);
                    if(localCount > 0) {
                        allelicValueElements.addAll(dao.getCharAlleleValuesForPolymorphicMarkersRetrieval(negativeGids, 0, (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if ((sessionForLocal != null) && (negativeGids != null) && (!negativeGids.isEmpty())){
                    dao.setSession(sessionForLocal);
                    localCount = dao.countCharAlleleValuesForPolymorphicMarkersRetrieval(negativeGids);
                    if (localCount > relativeLimit) {
                        allelicValueElements.addAll(dao.getCharAlleleValuesForPolymorphicMarkersRetrieval(negativeGids, (int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if ((sessionForLocal != null) && (negativeGids != null) && (!negativeGids.isEmpty())){
            dao.setSession(sessionForLocal);
            localCount = dao.countCharAlleleValuesForPolymorphicMarkersRetrieval(negativeGids);
            if (localCount > start) {
                allelicValueElements.addAll(dao.getCharAlleleValuesForPolymorphicMarkersRetrieval(negativeGids, start, numOfRows));
            }
        }
        
        //Sort by gid, markerName
        Collections.sort(allelicValueElements, AllelicValueElement.AllelicValueElementComparator);

        return allelicValueElements;
    }

    @Override
    public long countCharAlleleValuesForPolymorphicMarkersRetrieval(List<Integer> gids) throws MiddlewareQueryException{
        AlleleValuesDAO dao = new AlleleValuesDAO();

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        List<Integer> positiveGids = new ArrayList<Integer>();
        List<Integer> negativeGids = new ArrayList<Integer>();
        for (Integer gid : gids){
            if (gid < 0) {
                negativeGids.add(gid);
            } else {
                positiveGids.add(gid);
            }
        }

        long result = 0;

        // Count from local
        if ((sessionForLocal != null) && (negativeGids != null) && (!negativeGids.isEmpty())){
            dao.setSession(sessionForLocal);
            result += dao.countCharAlleleValuesForPolymorphicMarkersRetrieval(negativeGids);
        }

        // Count from central
        if ((sessionForCentral != null) && (positiveGids != null) && (!positiveGids.isEmpty())) {
            dao.setSession(sessionForCentral);
            result += dao.countCharAlleleValuesForPolymorphicMarkersRetrieval(positiveGids);
        }

        return result;
    }

    
    @Override
    public List<AllelicValueElement> getMappingAlleleValuesForPolymorphicMarkersRetrieval(List<Integer> gids, int start, int numOfRows) throws MiddlewareQueryException{
        AlleleValuesDAO dao = new AlleleValuesDAO();

        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        List<Integer> positiveGids = new ArrayList<Integer>();
        List<Integer> negativeGids = new ArrayList<Integer>();
        for (Integer gid : gids){
            if (gid < 0) {
                negativeGids.add(gid);
            } else {
                positiveGids.add(gid);
            }
        }

        List<AllelicValueElement> allelicValueElements = new ArrayList<AllelicValueElement>();

        if ((sessionForCentral != null) && (positiveGids != null) && (!positiveGids.isEmpty())) {
            
            dao.setSession(sessionForCentral);
            centralCount = dao.countMappingAlleleValuesForPolymorphicMarkersRetrieval(positiveGids);
            
            if(centralCount > start) {
                allelicValueElements.addAll(dao.getMappingAlleleValuesForPolymorphicMarkersRetrieval(positiveGids, start, numOfRows));
                relativeLimit = numOfRows - allelicValueElements.size();
                if(relativeLimit > 0 && sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countMappingAlleleValuesForPolymorphicMarkersRetrieval(negativeGids);
                    if(localCount > 0) {
                        allelicValueElements.addAll(dao.getMappingAlleleValuesForPolymorphicMarkersRetrieval(negativeGids, 0, (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if ((sessionForLocal != null) && (negativeGids != null) && (!negativeGids.isEmpty())){
                    dao.setSession(sessionForLocal);
                    localCount = dao.countMappingAlleleValuesForPolymorphicMarkersRetrieval(negativeGids);
                    if (localCount > relativeLimit) {
                        allelicValueElements.addAll(dao.getMappingAlleleValuesForPolymorphicMarkersRetrieval(negativeGids, (int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if ((sessionForLocal != null) && (negativeGids != null) && (!negativeGids.isEmpty())){
            dao.setSession(sessionForLocal);
            localCount = dao.countMappingAlleleValuesForPolymorphicMarkersRetrieval(negativeGids);
            if (localCount > start) {
                allelicValueElements.addAll(dao.getMappingAlleleValuesForPolymorphicMarkersRetrieval(negativeGids, start, numOfRows));
            }
        }
        
        //Sort by gid, markerName
        Collections.sort(allelicValueElements, AllelicValueElement.AllelicValueElementComparator);

        return allelicValueElements;
    }

    @Override
    public long countMappingAlleleValuesForPolymorphicMarkersRetrieval(List<Integer> gids) throws MiddlewareQueryException{
        AlleleValuesDAO dao = new AlleleValuesDAO();

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        List<Integer> positiveGids = new ArrayList<Integer>();
        List<Integer> negativeGids = new ArrayList<Integer>();
        for (Integer gid : gids){
            if (gid < 0) {
                negativeGids.add(gid);
            } else {
                positiveGids.add(gid);
            }
        }

        long result = 0;

        // Count from local
        if ((sessionForLocal != null) && (negativeGids != null) && (!negativeGids.isEmpty())){
            dao.setSession(sessionForLocal);
            result += dao.countMappingAlleleValuesForPolymorphicMarkersRetrieval(negativeGids);
        }

        // Count from central
        if ((sessionForCentral != null) && (positiveGids != null) && (!positiveGids.isEmpty())) {
            dao.setSession(sessionForCentral);
            result += dao.countMappingAlleleValuesForPolymorphicMarkersRetrieval(positiveGids);
        }

        return result;
    }
    
    @Override
    public List<Qtl> getAllQtl(int start, int numOfRows) throws MiddlewareQueryException{
        QtlDAO dao = new QtlDAO();

        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        List<Qtl> qtl = new ArrayList<Qtl>();

        if(sessionForCentral != null) {
            
            dao.setSession(sessionForCentral);
            centralCount = dao.countAll();
            
            if(centralCount > start) {
                qtl.addAll(dao.getAll(start, numOfRows));
                relativeLimit = numOfRows - qtl.size();
                if(relativeLimit > 0 && sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countAll();
                    if(localCount > 0) {
                        qtl.addAll(dao.getAll( 0, (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countAll();
                    if (localCount > relativeLimit) {
                        qtl.addAll(dao.getAll((int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            localCount = dao.countAll();
            if (localCount > start) {
                qtl.addAll(dao.getAll(start, numOfRows));
            }
        }
        
        return qtl;
    }
    
    @Override
    public long countAllQtl() throws MiddlewareQueryException{
        QtlDAO dao = new QtlDAO();

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        long result = 0;

        // Count from local
        if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            result += dao.countAll();
        }

        // Count from central
        if (sessionForCentral != null) {
            dao.setSession(sessionForCentral);
            result += dao.countAll();
        }

        return result;

    }

    @Override
    public List<QtlDetailElement> getQtlByName(String name, int start, int numOfRows) throws MiddlewareQueryException{
        
        if ((name == null) || (name.isEmpty())){
            return new ArrayList<QtlDetailElement>();
        }
        
        QtlDAO dao = new QtlDAO();
        
        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        List<QtlDetailElement> qtl = new ArrayList<QtlDetailElement>();

        if(sessionForCentral != null) {
            
            dao.setSession(sessionForCentral);
            centralCount = dao.countQtlDetailsByName(name);
            
            if(centralCount > start) {
                qtl.addAll(dao.getQtlDetailsByName(name, start, numOfRows));
                relativeLimit = numOfRows - qtl.size();
                if(relativeLimit > 0 && sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countQtlDetailsByName(name);
                    if(localCount > 0) {
                        qtl.addAll(dao.getQtlDetailsByName(name, 0, (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countQtlDetailsByName(name);
                    if (localCount > relativeLimit) {
                        qtl.addAll(dao.getQtlDetailsByName(name, (int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            localCount = dao.countQtlDetailsByName(name);
            if (localCount > start) {
                qtl.addAll(dao.getQtlDetailsByName(name, start, numOfRows));
            }
        }
        
        return qtl;    
    }
    
    @Override
    public long countQtlByName(String name) throws MiddlewareQueryException{
        
        if ((name == null) || (name.isEmpty())){
            return 0;
        }
                
        QtlDAO dao = new QtlDAO();

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        long result = 0;

        // Count from local
        if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            result += dao.countQtlDetailsByName(name);
        }

        // Count from central
        if (sessionForCentral != null) {
            dao.setSession(sessionForCentral);
            result += dao.countQtlDetailsByName(name);
        }

        return result;
    }
    
    @Override
    public List<Integer> getQtlByTrait(String trait, int start, int numOfRows) throws MiddlewareQueryException{
        QtlDAO dao = new QtlDAO();

        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        List<Integer> qtl = new ArrayList<Integer>();

        if(sessionForCentral != null) {
            
            dao.setSession(sessionForCentral);
            centralCount = dao.countQtlByTrait(trait);
            
            if(centralCount > start) {
                qtl.addAll(dao.getQtlByTrait(trait, start, numOfRows));
                relativeLimit = numOfRows - qtl.size();
                if(relativeLimit > 0 && sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countQtlByTrait(trait);
                    if(localCount > 0) {
                        qtl.addAll(dao.getQtlByTrait(trait, 0, (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (sessionForLocal != null) {
                    dao.setSession(sessionForLocal);
                    localCount = dao.countQtlByTrait(trait);
                    if (localCount > relativeLimit) {
                        qtl.addAll(dao.getQtlByTrait(trait, (int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            localCount = dao.countQtlByTrait(trait);
            if (localCount > start) {
                qtl.addAll(dao.getQtlByTrait(trait, start, numOfRows));
            }
        }
        
        return qtl;    

    }
    
    @Override
    public long countQtlByTrait(String trait) throws MiddlewareQueryException{
        QtlDAO dao = new QtlDAO();
        
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        long result = 0;

        // Count from local
        if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            result += dao.countQtlByTrait(trait);
        }

        // Count from central
        if (sessionForCentral != null) {
            dao.setSession(sessionForCentral);
            result += dao.countQtlByTrait(trait);
        }

        return result;

    }
    @Override
    public List<ParentElement> getAllParentsFromMappingPopulation(
            int start, int numOfRows) throws MiddlewareQueryException {

        Long centralCount = Long.valueOf(0);
        Long localCount = Long.valueOf(0);
        int relativeLimit = 0;    	
    	
        MappingPopDAO mappingPopDao = new MappingPopDAO();
       
        List<ParentElement> allParentsFromMappingPopulation = new ArrayList<ParentElement>();
   
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        if (sessionForCentral != null) {
            mappingPopDao.setSession(sessionForCentral);
            centralCount = mappingPopDao.countAllParentsFromMappingPopulation();
            
            if (centralCount > start) {
                allParentsFromMappingPopulation.addAll(mappingPopDao.getAllParentsFromMappingPopulation(start, numOfRows));
                relativeLimit = numOfRows - (centralCount.intValue() - start);

                if (relativeLimit > 0) {
                	
                    if (sessionForLocal != null) {
                        mappingPopDao.setSession(sessionForLocal);
                        localCount = mappingPopDao.countAllParentsFromMappingPopulation();
                        
                        if (localCount > 0) {
                            allParentsFromMappingPopulation.addAll(mappingPopDao.getAllParentsFromMappingPopulation(0, relativeLimit));
                        }
                    }
                }
            } else {
                relativeLimit = start - centralCount.intValue();
                if (sessionForLocal != null) {
                    mappingPopDao.setSession(sessionForLocal);
                    localCount = mappingPopDao.countAllParentsFromMappingPopulation();
                    if (localCount > relativeLimit) {
                        allParentsFromMappingPopulation.addAll(mappingPopDao.getAllParentsFromMappingPopulation(relativeLimit, numOfRows));
                    }
                }
            }
        } else if (sessionForLocal != null) {
            mappingPopDao.setSession(sessionForLocal);
            localCount = mappingPopDao.countAllParentsFromMappingPopulation();
            if (localCount > start) {
                allParentsFromMappingPopulation.addAll(mappingPopDao.getAllParentsFromMappingPopulation(start, numOfRows));
            }
        }
   
        return allParentsFromMappingPopulation;
    }

    @Override
    public Long countAllParentsFromMappingPopulation() throws MiddlewareQueryException {
    	
        MappingPopDAO mappingPopDao = new MappingPopDAO();
           
        Database centralInstance = Database.CENTRAL;
        Session centralSession = getSession(centralInstance);
        mappingPopDao.setSession(centralSession);
        Long centralCountParentsFromMappingPopulation = mappingPopDao.countAllParentsFromMappingPopulation();
        
        Database localInstance = Database.LOCAL;
        Session localSession = getSession(localInstance);
        mappingPopDao.setSession(localSession);
        Long localCountParentsFromMappingPopulation = mappingPopDao.countAllParentsFromMappingPopulation();
        
        Long totalCountParentsFromMappingPopulation = localCountParentsFromMappingPopulation + centralCountParentsFromMappingPopulation;
        
        return totalCountParentsFromMappingPopulation;
    }
    
    @Override
    public List<org.generationcp.middleware.pojos.gdms.Map> getMapDetailsByName(
            String nameLike, int start, int numOfRows) throws MiddlewareQueryException {

        Long centralCount = Long.valueOf(0);
        Long localCount = Long.valueOf(0);
        Long relativeLimit = Long.valueOf(0);    	
    	
        MapDAO mapDao = new MapDAO();
       
        List<org.generationcp.middleware.pojos.gdms.Map> maps = new ArrayList<org.generationcp.middleware.pojos.gdms.Map>();
   
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        if (sessionForCentral != null) {
            mapDao.setSession(sessionForCentral);
            centralCount = mapDao.countMapDetailsByName(nameLike);
            
            if (centralCount > start) {
                maps.addAll(mapDao.getMapDetailsByName(nameLike, start, numOfRows));
                relativeLimit = numOfRows - (centralCount - start);

                if (relativeLimit > 0) {
                	
                    if (sessionForLocal != null) {
                        mapDao.setSession(sessionForLocal);
                        localCount = mapDao.countMapDetailsByName(nameLike);
                        
                        if (localCount > 0) {
                            maps.addAll(mapDao.getMapDetailsByName(nameLike, 0, relativeLimit.intValue()));
                        }
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (sessionForLocal != null) {
                    mapDao.setSession(sessionForLocal);
                    localCount = mapDao.countMapDetailsByName(nameLike);
                    if (localCount > relativeLimit) {
                        maps.addAll(mapDao.getMapDetailsByName(nameLike, relativeLimit.intValue(), numOfRows));
                    }
                }
            }
        } else if (sessionForLocal != null) {
            mapDao.setSession(sessionForLocal);
            localCount = mapDao.countMapDetailsByName(nameLike);
            if (localCount > start) {
                maps.addAll(mapDao.getMapDetailsByName(nameLike, start, numOfRows));
            }
        }
     
        return maps;
    }

    @Override
    public Long countMapDetailsByName(String nameLike) throws MiddlewareQueryException {
    	
        MapDAO mapDao = new MapDAO();
           
        Database centralInstance = Database.CENTRAL;
        Session centralSession = getSession(centralInstance);
        mapDao.setSession(centralSession);
        Long centralCount = mapDao.countMapDetailsByName(nameLike);
        
        Database localInstance = Database.LOCAL;
        Session localSession = getSession(localInstance);
        mapDao.setSession(localSession);
        Long localCount = mapDao.countMapDetailsByName(nameLike);
        
        Long totalCount = centralCount + localCount;
        
        return totalCount;
    }

    
    @Override
    public List<Marker> getMarkersByQtl(String qtlName, String chromosome, int min, int max, int start, int numOfRows)
            throws MiddlewareQueryException {
        
        MarkerDAO markerDao = new MarkerDAO();
        QtlDetailsDAO qtlDetailsDao = new QtlDetailsDAO();

        long centralCount = 0;
        long negativeLocalCount = 0;
        long positiveLocalCount = 0;
        long relativeLimit = 0;

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        List<Marker> markers = new ArrayList<Marker>();        
        
        // Get from Central
        if (sessionForCentral != null){
            
            // Get the markerIds
            qtlDetailsDao.setSession(sessionForCentral);
            List<Integer> markerIds = qtlDetailsDao.getMarkerIdsByQtl(qtlName, chromosome, min, max);
            centralCount = qtlDetailsDao.countMarkerIdsByQtl(qtlName, chromosome, min, max);

            markerDao.setSession(sessionForCentral);
            
            if(centralCount > start) {
                markers.addAll(markerDao.getMarkersByIds(markerIds, start, numOfRows));
                relativeLimit = numOfRows - markers.size();
                if(relativeLimit > 0 && sessionForLocal != null) {

                    // Get the markerIds and separate the negative from positive ids
                    qtlDetailsDao.setSession(sessionForLocal);
                    List<Integer> localMarkerIds = qtlDetailsDao.getMarkerIdsByQtl(qtlName, chromosome, min, max);
                    
                    List<Integer> positiveMarkerIds = new ArrayList<Integer>();
                    List<Integer> negativeMarkerIds = new ArrayList<Integer>();
                    
                    for (Integer markerId : localMarkerIds){
                        if (markerId < 0) {
                            negativeMarkerIds.add(markerId);
                        } else {
                            positiveMarkerIds.add(markerId);
                        }
                    }
                    
                    // Get the markers corresponding to the negative marker ids from local database
                    markerDao.setSession(sessionForLocal);
                    //negativeLocalCount = markerDao.countMarkersByIds(negativeMarkerIds); 
                    negativeLocalCount = negativeMarkerIds.size();
                    if (negativeLocalCount > 0){
                        markers.addAll(markerDao.getMarkersByIds(negativeMarkerIds, 0, (int) relativeLimit));
                        relativeLimit = numOfRows - markers.size();
                        if (relativeLimit > 0) {
                            // Get the markers corresponding to the positive marker ids from central database
                            markerDao.setSession(sessionForCentral);
                            //positiveLocalCount = markerDao.countMarkersByIds(positiveMarkerIds);
                            positiveLocalCount = positiveMarkerIds.size();
                            if (positiveLocalCount > 0){
                                markers.addAll(markerDao.getMarkersByIds(positiveMarkerIds, 0, (int) relativeLimit));
                            }
                        }
                    }
                }
            } else {
                relativeLimit = start - centralCount;

                // Get the markerIds and separate the negative from positive ids
                qtlDetailsDao.setSession(sessionForLocal);
                List<Integer> localMarkerIds = qtlDetailsDao.getMarkerIdsByQtl(qtlName, chromosome, min, max);
                
                List<Integer> positiveMarkerIds = new ArrayList<Integer>();
                List<Integer> negativeMarkerIds = new ArrayList<Integer>();
                
                for (Integer markerId : localMarkerIds){
                    if (markerId < 0) {
                        negativeMarkerIds.add(markerId);
                    } else {
                        positiveMarkerIds.add(markerId);
                    }
                }
                
                // Get the markers corresponding to the negative marker ids from local database
                markerDao.setSession(sessionForLocal);
                negativeLocalCount = negativeMarkerIds.size();
                if (negativeLocalCount > 0){
                    markers.addAll(markerDao.getMarkersByIds(negativeMarkerIds, 0, (int) relativeLimit));
                    relativeLimit = numOfRows - markers.size();
                    if (relativeLimit > 0) {
                        // Get the markers corresponding to the positive marker ids from central database
                        markerDao.setSession(sessionForCentral);
                        positiveLocalCount = positiveMarkerIds.size();
                        if (positiveLocalCount > 0){
                            markers.addAll(markerDao.getMarkersByIds(positiveMarkerIds, 0, (int) relativeLimit));
                        }
                    }
                }
            }
            
        } else if (sessionForLocal != null){  // Get from Local
            
            // Get the markerIds
            qtlDetailsDao.setSession(sessionForLocal);
            List<Integer> markerIds = qtlDetailsDao.getMarkerIdsByQtl(qtlName, chromosome, min, max);
            
            List<Integer> positiveMarkerIds = new ArrayList<Integer>();
            List<Integer> negativeMarkerIds = new ArrayList<Integer>();
            
            for (Integer markerId : markerIds){
                if (markerId < 0) {
                    negativeMarkerIds.add(markerId);
                } else {
                    positiveMarkerIds.add(markerId);
                }
            }
            

            // Get the markers corresponding to the negative marker ids from local database
            markerDao.setSession(sessionForLocal);
            negativeLocalCount = negativeMarkerIds.size();
            if (negativeLocalCount > 0){
                markers.addAll(markerDao.getMarkersByIds(negativeMarkerIds, start, numOfRows));
                relativeLimit = numOfRows - markers.size();
                if (relativeLimit > 0) {
                    // Get the markers corresponding to the positive marker ids from central database
                    markerDao.setSession(sessionForCentral);
                    positiveLocalCount = positiveMarkerIds.size();
                    if (positiveLocalCount > 0){
                        markers.addAll(markerDao.getMarkersByIds(positiveMarkerIds, 0, (int) relativeLimit));
                    }
                }
            }
            
        }

        return markers;

    }

    @Override
    public long countMarkersByQtl(String qtlName, String chromosome, int min, int max) throws MiddlewareQueryException {
        QtlDetailsDAO qtlDetailsDao = new QtlDetailsDAO();

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        long count = 0;        

        // Count from Central
        if (sessionForCentral != null) {
            qtlDetailsDao.setSession(sessionForLocal);
            count += qtlDetailsDao.countMarkerIdsByQtl(qtlName, chromosome, min, max);
        }

        // Count from Local
        if (sessionForLocal != null) {
            qtlDetailsDao.setSession(sessionForLocal);
            count += qtlDetailsDao.countMarkerIdsByQtl(qtlName, chromosome, min, max);
        }
        
        return count;
    }
    
    
    @Override
    public QtlDetailsPK addQtlDetails(QtlDetails qtlDetails) throws MiddlewareQueryException {

        requireLocalDatabaseInstance();
        
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        QtlDetailsPK savedId = new QtlDetailsPK();
        
        try {
            trans = session.beginTransaction();
            QtlDetailsDAO dao = new QtlDetailsDAO();
            dao.setSession(session);

            // No need to auto-assign negative IDs for new local DB records
            // qtlId and mapId are foreign keys
            
            QtlDetails recordSaved = dao.save(qtlDetails);
            savedId = recordSaved.getId();
            
            trans.commit();

        } catch (Exception e) {
            // Rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new MiddlewareQueryException("Error encountered while saving Qtl Details: GenotypicDataManager.addQtlDetails(qtlDetails="
                    + qtlDetails + "): " + e.getMessage(), e);
        } finally {
            session.flush();
        }
        return savedId;

    }

    @Override
    public Integer addMarkerDetails(MarkerDetails markerDetails) throws MiddlewareQueryException {

        requireLocalDatabaseInstance();
        
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        Integer savedId;
        
        try {
            trans = session.beginTransaction();
            MarkerDetailsDAO dao = new MarkerDetailsDAO();
            dao.setSession(session);

            // No need to auto-assign negative id. It should come from an existing entry in Marker.
            
            MarkerDetails recordSaved = dao.save(markerDetails);
            savedId = recordSaved.getMarkerId();
            
            trans.commit();

        } catch (Exception e) {
            // Rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new MiddlewareQueryException("Error encountered while saving Marker Details: GenotypicDataManager.addMarkerDetails(markerDetails="
                    + markerDetails + "): " + e.getMessage(), e);
        } finally {
            session.flush();
        }
        return savedId;    
    }

    @Override
    public Integer addMarkerUserInfo(MarkerUserInfo markerUserInfo) throws MiddlewareQueryException {

        requireLocalDatabaseInstance();
        
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        Integer savedId;
        
        try {
            trans = session.beginTransaction();
            MarkerUserInfoDAO dao = new MarkerUserInfoDAO();
            dao.setSession(session);

            // No need to auto-assign negative id. It should come from an existing entry in Marker.
            
            MarkerUserInfo recordSaved = dao.save(markerUserInfo);
            savedId = recordSaved.getMarkerId();
            
            trans.commit();

        } catch (Exception e) {
            // Rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new MiddlewareQueryException("Error encountered while saving Marker Details: GenotypicDataManager.addMarkerUserInfo(markerUserInfo="
                    + markerUserInfo + "): " + e.getMessage(), e);
        } finally {
            session.flush();
        }
        return savedId;    
    }

    @Override
    public AccMetadataSetPK addAccMetadataSet(AccMetadataSet accMetadataSet) throws MiddlewareQueryException{
        
        requireLocalDatabaseInstance();
        
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        AccMetadataSetPK savedId = new AccMetadataSetPK();
        
        try {
            trans = session.beginTransaction();
            AccMetadataSetDAO dao = new AccMetadataSetDAO();
            dao.setSession(session);

            // No need to auto-assign negative IDs for new local DB records
            // datasetId, germplasmId and nameId are foreign keys
            
            AccMetadataSet recordSaved = dao.save(accMetadataSet);
            savedId = recordSaved.getId();
            
            trans.commit();

        } catch (Exception e) {
            // Rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new MiddlewareQueryException("Error encountered with addAccMetadataSet(accMetadataSet="
                    + accMetadataSet + "): " + e.getMessage(), e);
        } finally {
            session.flush();
        }
        return savedId;
    }
    
    @Override
    public MarkerMetadataSetPK addMarkerMetadataSet(MarkerMetadataSet markerMetadataSet) throws MiddlewareQueryException{

        requireLocalDatabaseInstance();
        
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        MarkerMetadataSetPK savedId = new MarkerMetadataSetPK();
        
        try {
            trans = session.beginTransaction();
            MarkerMetadataSetDAO dao = new MarkerMetadataSetDAO();
            dao.setSession(session);

            // No need to auto-assign negative IDs for new local DB records
            // datasetId and markerId are foreign keys
            
            MarkerMetadataSet recordSaved = dao.save(markerMetadataSet);
            savedId = recordSaved.getId();
            
            trans.commit();

        } catch (Exception e) {
            // Rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new MiddlewareQueryException("Error encountered with addMarkerMetadataSet(markerMetadataSet="
                    + markerMetadataSet + "): " + e.getMessage(), e);
        } finally {
            session.flush();
        }
        return savedId;
    }

    @Override
    public Integer addDataset(Dataset dataset) throws MiddlewareQueryException{

        requireLocalDatabaseInstance();
        
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        Integer savedId;
        
        try {
            trans = session.beginTransaction();
            DatasetDAO dao = new DatasetDAO();
            dao.setSession(session);

            Integer generatedId = dao.getNegativeId("datasetId");
            dataset.setDatasetId(generatedId);
            
            Dataset recordSaved = dao.save(dataset);
            savedId = recordSaved.getDatasetId();
            
            trans.commit();

        } catch (Exception e) {
            // Rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new MiddlewareQueryException("Error encountered with addDataset(dataset="
                    + dataset + "): " + e.getMessage(), e);
        } finally {
            session.flush();
        }
        return savedId;   
    }
   
    @Override
    public Integer addGDMSMarker(Marker marker) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();

        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        Integer idGDMSMarkerSaved;
        try {
            // begin save transaction
            trans = session.beginTransaction();

            MarkerDAO dao = new MarkerDAO();
            dao.setSession(session);

            Integer markerId = dao.getNegativeId("markerId");
            marker.setMarkerId(markerId);

            Marker recordSaved = dao.saveOrUpdate(marker);
            idGDMSMarkerSaved = recordSaved.getMarkerId();

            trans.commit();
        } catch (Exception e) {
            // rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new MiddlewareQueryException("Error encountered while saving Marker: addGDMSMarker(): "
                    + e.getMessage(), e);
        } finally {
            session.flush();
        }
        
        return idGDMSMarkerSaved;
    }
    
    @Override
    public Integer addGDMSMarkerAlias(MarkerAlias markerAlias) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();

        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        Integer idGDMSMarkerAliasSaved;
        try {
            // begin save transaction
            trans = session.beginTransaction();

            MarkerAliasDAO dao = new MarkerAliasDAO();
            dao.setSession(session);

            //Integer markerAliasId = dao.getNegativeId("marker_id");
            //markerAlias.setMarkerId(markerAliasId);

            MarkerAlias recordSaved = dao.save(markerAlias);
            idGDMSMarkerAliasSaved = recordSaved.getMarkerId();

            trans.commit();
        } catch (Exception e) {
            // rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new MiddlewareQueryException("Error encountered while saving Marker: addGDMSMarkerAlias(): "
                    + e.getMessage(), e);
        } finally {
            session.flush();
        }
        
        return idGDMSMarkerAliasSaved;
    }    
    
    
    @Override
    public Integer addDatasetUser(DatasetUsers datasetUser) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();

        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        Integer idDatasetUserSaved;
        try {
            // begin save transaction
            trans = session.beginTransaction();

            DatasetUsersDAO dao = new DatasetUsersDAO();
            dao.setSession(session);

            DatasetUsers recordSaved = dao.save(datasetUser);
            idDatasetUserSaved = recordSaved.getUserId();

            trans.commit();
        } catch (Exception e) {
            // rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new MiddlewareQueryException("Error encountered while saving Marker: addDatasetUser(): "
                    + e.getMessage(), e);
        } finally {
            session.flush();
        }
        
        return idDatasetUserSaved;
    }    
}