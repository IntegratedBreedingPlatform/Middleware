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
import org.generationcp.middleware.dao.gdms.AccMetadataSetDAO;
import org.generationcp.middleware.dao.gdms.AlleleValuesDAO;
import org.generationcp.middleware.dao.gdms.CharValuesDAO;
import org.generationcp.middleware.dao.gdms.DartValuesDAO;
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
import org.generationcp.middleware.dao.gdms.MarkerOnMapDAO;
import org.generationcp.middleware.dao.gdms.MarkerUserInfoDAO;
import org.generationcp.middleware.dao.gdms.QtlDAO;
import org.generationcp.middleware.dao.gdms.QtlDetailsDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.GenotypicDataManager;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.gdms.AccMetadataSet;
import org.generationcp.middleware.pojos.gdms.AccMetadataSetPK;
import org.generationcp.middleware.pojos.gdms.AlleleValues;
import org.generationcp.middleware.pojos.gdms.AllelicValueElement;
import org.generationcp.middleware.pojos.gdms.AllelicValueWithMarkerIdElement;
import org.generationcp.middleware.pojos.gdms.CharValues;
import org.generationcp.middleware.pojos.gdms.DartValues;
import org.generationcp.middleware.pojos.gdms.Dataset;
import org.generationcp.middleware.pojos.gdms.DatasetElement;
import org.generationcp.middleware.pojos.gdms.DatasetUsers;
import org.generationcp.middleware.pojos.gdms.GermplasmMarkerElement;
import org.generationcp.middleware.pojos.gdms.Map;
import org.generationcp.middleware.pojos.gdms.MapDetailElement;
import org.generationcp.middleware.pojos.gdms.MapInfo;
import org.generationcp.middleware.pojos.gdms.MappingPop;
import org.generationcp.middleware.pojos.gdms.MappingPopValues;
import org.generationcp.middleware.pojos.gdms.MappingValueElement;
import org.generationcp.middleware.pojos.gdms.Marker;
import org.generationcp.middleware.pojos.gdms.MarkerAlias;
import org.generationcp.middleware.pojos.gdms.MarkerDetails;
import org.generationcp.middleware.pojos.gdms.MarkerIdMarkerNameElement;
import org.generationcp.middleware.pojos.gdms.MarkerInfo;
import org.generationcp.middleware.pojos.gdms.MarkerMetadataSet;
import org.generationcp.middleware.pojos.gdms.MarkerMetadataSetPK;
import org.generationcp.middleware.pojos.gdms.MarkerNameElement;
import org.generationcp.middleware.pojos.gdms.MarkerOnMap;
import org.generationcp.middleware.pojos.gdms.MarkerUserInfo;
import org.generationcp.middleware.pojos.gdms.ParentElement;
import org.generationcp.middleware.pojos.gdms.Qtl;
import org.generationcp.middleware.pojos.gdms.QtlDetailElement;
import org.generationcp.middleware.pojos.gdms.QtlDetails;
import org.generationcp.middleware.pojos.gdms.QtlDetailsPK;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of GenotypicDataManager
 * 
 * @author Joyce Avestro
 */
public class GenotypicDataManagerImpl extends DataManager implements GenotypicDataManager{

    private static final Logger LOG = LoggerFactory.getLogger(GenotypicDataManagerImpl.class);

    private NameDAO nameDao;
    private AccMetadataSetDAO accMetadataSetDao;
    private AlleleValuesDAO alleleValuesDao;
    private CharValuesDAO charValuesDao;
    private DartValuesDAO dartValuesDao;
    private DatasetDAO datasetDao;
    private DatasetUsersDAO datasetUsersDao;
    private MapDAO mapDao;
    private MappingDataDAO mappingDataDao;
    private MappingPopDAO mappingPopDao;
    private MappingPopValuesDAO mappingPopValuesDao;
    private MarkerAliasDAO markerAliasDao;
    private MarkerDAO markerDao;
    private MarkerDetailsDAO markerDetailsDao;
    private MarkerInfoDAO markerInfoDao;
    private MarkerMetadataSetDAO markerMetadataSetDao;
    private MarkerOnMapDAO markerOnMapDao;
    private MarkerUserInfoDAO markerUserInfoDao;
    private QtlDAO qtlDao;
    private QtlDetailsDAO qtlDetailsDao;

    public GenotypicDataManagerImpl() {
    }

    public GenotypicDataManagerImpl(HibernateSessionProvider sessionProviderForLocal, HibernateSessionProvider sessionProviderForCentral) {
        super(sessionProviderForLocal, sessionProviderForCentral);
    }

    public GenotypicDataManagerImpl(Session sessionForLocal, Session sessionForCentral) {
        super(sessionForLocal, sessionForCentral);
    }

    private NameDAO getNameDao() {
        if (nameDao == null) {
            nameDao = new NameDAO();
        }
        nameDao.setSession(getActiveSession());
        return nameDao;
    }

    private AccMetadataSetDAO getAccMetadataSetDao() {
        if (accMetadataSetDao == null) {
            accMetadataSetDao = new AccMetadataSetDAO();
        }
        accMetadataSetDao.setSession(getActiveSession());
        return accMetadataSetDao;
    }

    private AlleleValuesDAO getAlleleValuesDao() {
        if (alleleValuesDao == null) {
            alleleValuesDao = new AlleleValuesDAO();
        }
        alleleValuesDao.setSession(getActiveSession());
        return alleleValuesDao;
    }

    private CharValuesDAO getCharValuesDao() {
        if (charValuesDao == null) {
            charValuesDao = new CharValuesDAO();
        }
        charValuesDao.setSession(getActiveSession());
        return charValuesDao;
    }

    private DartValuesDAO getDartValuesDao() {
        if (dartValuesDao == null) {
            dartValuesDao = new DartValuesDAO();
        }
        dartValuesDao.setSession(getActiveSession());
        return dartValuesDao;
    }

    private DatasetDAO getDatasetDao() {
        if (datasetDao == null) {
            datasetDao = new DatasetDAO();
        }
        datasetDao.setSession(getActiveSession());
        return datasetDao;
    }

    private DatasetUsersDAO getDatasetUsersDao() {
        if (datasetUsersDao == null) {
            datasetUsersDao = new DatasetUsersDAO();
        }
        datasetUsersDao.setSession(getActiveSession());
        return datasetUsersDao;
    }

    private MapDAO getMapDao() {
        if (mapDao == null) {
            mapDao = new MapDAO();
        }
        mapDao.setSession(getActiveSession());
        return mapDao;
    }

    private MappingDataDAO getMappingDataDao() {
        if (mappingDataDao == null) {
            mappingDataDao = new MappingDataDAO();
        }
        mappingDataDao.setSession(getActiveSession());
        return mappingDataDao;
    }

    private MappingPopDAO getMappingPopDao() {
        if (mappingPopDao == null) {
            mappingPopDao = new MappingPopDAO();
        }
        mappingPopDao.setSession(getActiveSession());
        return mappingPopDao;
    }

    private MappingPopValuesDAO getMappingPopValuesDao() {
        if (mappingPopValuesDao == null) {
            mappingPopValuesDao = new MappingPopValuesDAO();
        }
        mappingPopValuesDao.setSession(getActiveSession());
        return mappingPopValuesDao;
    }

    private MarkerAliasDAO getMarkerAliasDao() {
        if (markerAliasDao == null) {
            markerAliasDao = new MarkerAliasDAO();
        }
        markerAliasDao.setSession(getActiveSession());
        return markerAliasDao;
    }

    private MarkerDAO getMarkerDao() {
        if (markerDao == null) {
            markerDao = new MarkerDAO();
        }
        markerDao.setSession(getActiveSession());
        return markerDao;
    }

    private MarkerDetailsDAO getMarkerDetailsDao() {
        if (markerDetailsDao == null) {
            markerDetailsDao = new MarkerDetailsDAO();
        }
        markerDetailsDao.setSession(getActiveSession());
        return markerDetailsDao;
    }

    private MarkerInfoDAO getMarkerInfoDao() {
        if (markerInfoDao == null) {
            markerInfoDao = new MarkerInfoDAO();
        }
        markerInfoDao.setSession(getActiveSession());
        return markerInfoDao;
    }

    private MarkerMetadataSetDAO getMarkerMetadataSetDao() {
        if (markerMetadataSetDao == null) {
            markerMetadataSetDao = new MarkerMetadataSetDAO();
        }
        markerMetadataSetDao.setSession(getActiveSession());
        return markerMetadataSetDao;
    }

    private MarkerOnMapDAO getMarkerOnMapDao() {
        if (markerOnMapDao == null) {
            markerOnMapDao = new MarkerOnMapDAO();
        }
        markerOnMapDao.setSession(getActiveSession());
        return markerOnMapDao;
    }

    private MarkerUserInfoDAO getMarkerUserInfoDao() {
        if (markerUserInfoDao == null) {
            markerUserInfoDao = new MarkerUserInfoDAO();
        }
        markerUserInfoDao.setSession(getActiveSession());
        return markerUserInfoDao;
    }

    private QtlDAO getQtlDao() {
        if (qtlDao == null) {
            qtlDao = new QtlDAO();
        }
        qtlDao.setSession(getActiveSession());
        return qtlDao;
    }

    private QtlDetailsDAO getQtlDetailsDao() {
        if (qtlDetailsDao == null) {
            qtlDetailsDao = new QtlDetailsDAO();
        }
        qtlDetailsDao.setSession(getActiveSession());
        return qtlDetailsDao;
    }

    @Override
    public List<Integer> getMapIDsByQTLName(String qtlName, int start, int numOfRows) throws MiddlewareQueryException {
        if ((qtlName == null) || (qtlName.isEmpty())) {
            return new ArrayList<Integer>();
        }

        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;

        List<Integer> qtl = new ArrayList<Integer>();

        if (setWorkingDatabase(Database.CENTRAL)) {
            QtlDAO dao = getQtlDao();
            centralCount = dao.countMapIDsByQTLName(qtlName);

            if (centralCount > start) {
                qtl.addAll(dao.getMapIDsByQTLName(qtlName, start, numOfRows));
                relativeLimit = numOfRows - (centralCount - start);
                if (relativeLimit > 0 && setWorkingDatabase(Database.LOCAL)) {
                    dao = getQtlDao();
                    localCount = dao.countMapIDsByQTLName(qtlName);
                    if (localCount > 0) {
                        qtl.addAll(dao.getMapIDsByQTLName(qtlName, 0, (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (setWorkingDatabase(Database.LOCAL)) {
                    dao = getQtlDao();
                    localCount = dao.countMapIDsByQTLName(qtlName);
                    if (localCount > relativeLimit) {
                        qtl.addAll(dao.getMapIDsByQTLName(qtlName, (int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if (setWorkingDatabase(Database.LOCAL)) {
            QtlDAO dao = getQtlDao();
            localCount = dao.countMapIDsByQTLName(qtlName);
            if (localCount > start) {
                qtl.addAll(dao.getMapIDsByQTLName(qtlName, start, numOfRows));
            }
        }
        return qtl;
    }

    @Override
    public long countMapIDsByQTLName(String qtlName) throws MiddlewareQueryException {
        long count = 0;
        if (setWorkingDatabase(Database.LOCAL)) {
            count = count + getQtlDao().countMapIDsByQTLName(qtlName);
        }
        if (setWorkingDatabase(Database.CENTRAL)) {
            count = count + getQtlDao().countMapIDsByQTLName(qtlName);
        }
        return count;
    }

    @Override
    public List<Integer> getNameIdsByGermplasmIds(List<Integer> gIds) throws MiddlewareQueryException {
        if (setWorkingDatabase(gIds.get(0))) {
            return (List<Integer>) getAccMetadataSetDao().getNameIdsByGermplasmIds(gIds);
        }
        return new ArrayList<Integer>();
    }

    @Override
    public List<Name> getNamesByNameIds(List<Integer> nIds) throws MiddlewareQueryException {
        if (setWorkingDatabase(nIds.get(0))) {
            return (List<Name>) getNameDao().getNamesByNameIds(nIds);
        }
        return new ArrayList<Name>();
    }

    @Override
    public Name getNameByNameId(Integer nId) throws MiddlewareQueryException {
        if (setWorkingDatabase(nId)) {
            return getNameDao().getNameByNameId(nId);
        }
        return null;
    }

    @Override
    public long countAllMaps(Database instance) throws MiddlewareQueryException {
        if (setWorkingDatabase(instance)) {
            return getMapDao().countAll();
        }
        return 0;
    }

    @Override
    public List<Map> getAllMaps(int start, int numOfRows, Database instance) throws MiddlewareQueryException {
        if (setWorkingDatabase(instance)) {
            return getMapDao().getAll(start, numOfRows);
        }
        return new ArrayList<Map>();
    }

    @Override
    public List<MapInfo> getMapInfoByMapName(String mapName, Database instance) throws MiddlewareQueryException {
        if (setWorkingDatabase(instance)) {
            return (List<MapInfo>) getMappingDataDao().getMapInfoByMapName(mapName);
        }
        return new ArrayList<MapInfo>();
    }

    @Override
    public long countDatasetNames(Database instance) throws MiddlewareQueryException {
        if (setWorkingDatabase(instance)) {
            return getDatasetDao().countByName();
        }
        return 0;
    }

    @Override
    public List<String> getDatasetNames(int start, int numOfRows, Database instance) throws MiddlewareQueryException {
        if (setWorkingDatabase(instance)) {
            return (List<String>) getDatasetDao().getDatasetNames(start, numOfRows);
        }
        return new ArrayList<String>();
    }

    @Override
    public List<DatasetElement> getDatasetDetailsByDatasetName(String datasetName, Database instance) throws MiddlewareQueryException {
        if (setWorkingDatabase(instance)) {
            return (List<DatasetElement>) getDatasetDao().getDetailsByName(datasetName);
        }
        return new ArrayList<DatasetElement>();
    }

    @Override
    public List<Integer> getMarkerIdsByMarkerNames(List<String> markerNames, int start, int numOfRows, Database instance)
            throws MiddlewareQueryException {
        if (setWorkingDatabase(instance)) {
            return getMarkerDao().getIdsByNames(markerNames, start, numOfRows);
        }
        return new ArrayList<Integer>();
    }

    @Override
    public Set<Integer> getMarkerIDsByMapIDAndLinkageBetweenStartPosition(int mapId, String linkageGroup, double startPos, double endPos, int start, int numOfRows) 
            throws MiddlewareQueryException {
        if (setWorkingDatabase(mapId)) {
            return getMarkerDao().getMarkerIDsByMapIDAndLinkageBetweenStartPosition(mapId, linkageGroup, startPos, endPos, start, numOfRows);
        }
        return new TreeSet<Integer>();
    }

    @Override
    public long countMarkerIDsByMapIDAndLinkageBetweenStartPosition(int mapId, String linkageGroup, double startPos, double endPos) 
            throws MiddlewareQueryException {
        if (setWorkingDatabase(mapId)) {
            return getMarkerDao().countMarkerIDsByMapIDAndLinkageBetweenStartPosition(mapId, linkageGroup, startPos, endPos);
        }
        return 0l;
    }

    @Override
    public List<Integer> getMarkerIdsByDatasetId(Integer datasetId) throws MiddlewareQueryException {
        if (setWorkingDatabase(datasetId)) {
            return (List<Integer>) getMarkerMetadataSetDao().getMarkerIdByDatasetId(datasetId);
        }
        return new ArrayList<Integer>();
    }

    @Override
    public List<ParentElement> getParentsByDatasetId(Integer datasetId) throws MiddlewareQueryException {
        if (setWorkingDatabase(datasetId)) {
            return (List<ParentElement>) getMappingPopDao().getParentsByDatasetId(datasetId);
        }
        return new ArrayList<ParentElement>();
    }

    @Override
    public List<String> getMarkerTypesByMarkerIds(List<Integer> markerIds) throws MiddlewareQueryException {
        if (setWorkingDatabase(markerIds.get(0))) {
            return (List<String>) getMarkerDao().getMarkerTypeByMarkerIds(markerIds);
        }
        return new ArrayList<String>();
    }

    @Override
    public List<MarkerNameElement> getMarkerNamesByGIds(List<Integer> gIds) throws MiddlewareQueryException {
        if (setWorkingDatabase(gIds.get(0))) {
            return (List<MarkerNameElement>) getMarkerDao().getMarkerNamesByGIds(gIds);
        }
        return new ArrayList<MarkerNameElement>();
    }

    @Override
    public List<GermplasmMarkerElement> getGermplasmNamesByMarkerNames(List<String> markerNames, Database instance)
            throws MiddlewareQueryException {
        if (setWorkingDatabase(instance)) {
            return (List<GermplasmMarkerElement>) getMarkerDao().getGermplasmNamesByMarkerNames(markerNames);
        }
        return new ArrayList<GermplasmMarkerElement>();
    }

    @Override
    public List<MappingValueElement> getMappingValuesByGidsAndMarkerNames(List<Integer> gids, List<String> markerNames, int start,
            int numOfRows) throws MiddlewareQueryException {
        if (setWorkingDatabase(gids.get(0))) {
            List<Integer> markerIds = getMarkerDao().getIdsByNames(markerNames, start, numOfRows);
            List<MappingValueElement> mappingValues = getMappingPopDao().getMappingValuesByGidAndMarkerIds(gids, markerIds);
            return mappingValues;
        }
        return new ArrayList<MappingValueElement>();
    }

    @Override
    public List<AllelicValueElement> getAllelicValuesByGidsAndMarkerNames(List<Integer> gids, List<String> markerNames)
            throws MiddlewareQueryException {
        if (setWorkingDatabase(gids.get(0))) {
            return getMarkerDao().getAllelicValuesByGidsAndMarkerNames(gids, markerNames);
        }
        return new ArrayList<AllelicValueElement>();
    }

    @Override
    public List<AllelicValueWithMarkerIdElement> getAllelicValuesFromCharValuesByDatasetId(Integer datasetId, int start, int numOfRows)
            throws MiddlewareQueryException {
        if (setWorkingDatabase(datasetId)) {
            return getCharValuesDao().getAllelicValuesByDatasetId(datasetId, start, numOfRows);
        }
        return new ArrayList<AllelicValueWithMarkerIdElement>();
    }

    @Override
    public long countAllelicValuesFromCharValuesByDatasetId(Integer datasetId) throws MiddlewareQueryException {
        if (setWorkingDatabase(datasetId)) {
            return getCharValuesDao().countByDatasetId(datasetId);
        }
        return 0;
    }

    @Override
    public List<AllelicValueWithMarkerIdElement> getAllelicValuesFromAlleleValuesByDatasetId(Integer datasetId, int start, int numOfRows)
            throws MiddlewareQueryException {
        if (setWorkingDatabase(datasetId)) {
            return getAlleleValuesDao().getAllelicValuesByDatasetId(datasetId, start, numOfRows);
        }
        return new ArrayList<AllelicValueWithMarkerIdElement>();
    }

    @Override
    public long countAllelicValuesFromAlleleValuesByDatasetId(Integer datasetId) throws MiddlewareQueryException {
        if (setWorkingDatabase(datasetId)) {
            return getAlleleValuesDao().countByDatasetId(datasetId);
        }
        return 0;
    }

    @Override
    public List<AllelicValueWithMarkerIdElement> getAllelicValuesFromMappingPopValuesByDatasetId(Integer datasetId, int start, int numOfRows)
            throws MiddlewareQueryException {
        if (setWorkingDatabase(datasetId)) {
            return getMappingPopValuesDao().getAllelicValuesByDatasetId(datasetId, start, numOfRows);
        }
        return new ArrayList<AllelicValueWithMarkerIdElement>();
    }

    @Override
    public long countAllelicValuesFromMappingPopValuesByDatasetId(Integer datasetId) throws MiddlewareQueryException {
        if (setWorkingDatabase(datasetId)) {
            return getMappingPopValuesDao().countByDatasetId(datasetId);
        }
        return 0;
    }

    @Override
    public List<MarkerInfo> getMarkerInfoByMarkerName(String markerName, int start, int numOfRows) throws MiddlewareQueryException {
        List<MarkerInfo> markerInfoList = new ArrayList<MarkerInfo>();

        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;

        if (setWorkingDatabase(Database.CENTRAL)) {
            MarkerInfoDAO dao = getMarkerInfoDao();
            centralCount = dao.countByMarkerName(markerName);

            if (centralCount > start) {

                markerInfoList.addAll((List<MarkerInfo>) dao.getByMarkerName(markerName, start, numOfRows));
                relativeLimit = numOfRows - (centralCount - start);

                if (relativeLimit > 0) {
                    if (setWorkingDatabase(Database.LOCAL)) {
                        dao = getMarkerInfoDao();
                        localCount = dao.countByMarkerName(markerName);
                        if (localCount > 0) {
                            markerInfoList.addAll((List<MarkerInfo>) dao.getByMarkerName(markerName, 0, (int) relativeLimit));
                        }
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (setWorkingDatabase(Database.LOCAL)) {
                    dao = getMarkerInfoDao();
                    localCount = dao.countByMarkerName(markerName);
                    if (localCount > relativeLimit) {
                        markerInfoList.addAll((List<MarkerInfo>) dao.getByMarkerName(markerName, (int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if (setWorkingDatabase(Database.LOCAL)) {
            MarkerInfoDAO dao = getMarkerInfoDao();
            localCount = dao.countByMarkerName(markerName);
            if (localCount > start) {
                markerInfoList.addAll((List<MarkerInfo>) dao.getByMarkerName(markerName, start, numOfRows));
            }
        }
        return markerInfoList;
    }

    @Override
    public long countMarkerInfoByMarkerName(String markerName) throws MiddlewareQueryException {
        long count = 0;
        if (setWorkingDatabase(Database.LOCAL)) {
            count = count + getMarkerInfoDao().countByMarkerName(markerName);
        }
        if (setWorkingDatabase(Database.CENTRAL)) {
            count = count + getMarkerInfoDao().countByMarkerName(markerName);
        }
        return count;
    }

    @Override
    public List<MarkerInfo> getMarkerInfoByGenotype(String genotype, int start, int numOfRows) throws MiddlewareQueryException {
        List<MarkerInfo> markerInfoList = new ArrayList<MarkerInfo>();

        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;

        if (setWorkingDatabase(Database.CENTRAL)) {
            MarkerInfoDAO dao = getMarkerInfoDao();
            centralCount = dao.countByGenotype(genotype);
            if (centralCount > start) {
                markerInfoList.addAll((List<MarkerInfo>) dao.getByGenotype(genotype, start, numOfRows));
                relativeLimit = numOfRows - (centralCount - start);
                if (relativeLimit > 0) {
                    if (setWorkingDatabase(Database.LOCAL)) {
                        dao = getMarkerInfoDao();
                        localCount = dao.countByGenotype(genotype);
                        if (localCount > 0) {
                            markerInfoList.addAll((List<MarkerInfo>) dao.getByGenotype(genotype, 0, (int) relativeLimit));
                        }
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (setWorkingDatabase(Database.LOCAL)) {
                    dao = getMarkerInfoDao();
                    localCount = dao.countByGenotype(genotype);

                    if (localCount > relativeLimit) {
                        markerInfoList.addAll((List<MarkerInfo>) dao.getByGenotype(genotype, (int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if (setWorkingDatabase(Database.LOCAL)) {
            MarkerInfoDAO dao = getMarkerInfoDao();
            localCount = dao.countByGenotype(genotype);
            if (localCount > start) {
                markerInfoList.addAll((List<MarkerInfo>) dao.getByGenotype(genotype, start, numOfRows));
            }
        }
        return markerInfoList;
    }

    @Override
    public long countMarkerInfoByGenotype(String genotype) throws MiddlewareQueryException {
        long count = 0;
        if (setWorkingDatabase(Database.LOCAL)) {
            count = count + getMarkerInfoDao().countByGenotype(genotype);
        }
        if (setWorkingDatabase(Database.CENTRAL)) {
            count = count + getMarkerInfoDao().countByGenotype(genotype);
        }
        return count;
    }

    @Override
    public List<MarkerInfo> getMarkerInfoByDbAccessionId(String dbAccessionId, int start, int numOfRows) throws MiddlewareQueryException {
        List<MarkerInfo> markerInfoList = new ArrayList<MarkerInfo>();

        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;

        if (setWorkingDatabase(Database.CENTRAL)) {
            MarkerInfoDAO dao = getMarkerInfoDao();
            centralCount = dao.countByDbAccessionId(dbAccessionId);
            if (centralCount > start) {
                markerInfoList.addAll((List<MarkerInfo>) dao.getByDbAccessionId(dbAccessionId, start, numOfRows));
                relativeLimit = numOfRows - (centralCount - start);
                if (relativeLimit > 0) {
                    if (setWorkingDatabase(Database.LOCAL)) {
                        dao = getMarkerInfoDao();
                        localCount = dao.countByDbAccessionId(dbAccessionId);
                        if (localCount > 0) {
                            markerInfoList.addAll((List<MarkerInfo>) dao.getByDbAccessionId(dbAccessionId, 0, (int) relativeLimit));
                        }
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (setWorkingDatabase(Database.LOCAL)) {
                    dao = getMarkerInfoDao();
                    localCount = dao.countByDbAccessionId(dbAccessionId);
                    if (localCount > relativeLimit) {
                        markerInfoList.addAll((List<MarkerInfo>) dao.getByDbAccessionId(dbAccessionId, (int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if (setWorkingDatabase(Database.LOCAL)) {
            MarkerInfoDAO dao = getMarkerInfoDao();
            localCount = dao.countByDbAccessionId(dbAccessionId);
            if (localCount > start) {
                markerInfoList.addAll((List<MarkerInfo>) dao.getByDbAccessionId(dbAccessionId, start, numOfRows));
            }
        }
        return markerInfoList;
    }

    @Override
    public long countMarkerInfoByDbAccessionId(String dbAccessionId) throws MiddlewareQueryException {
        long count = 0;
        if (setWorkingDatabase(Database.LOCAL)) {
            count = count + getMarkerInfoDao().countByDbAccessionId(dbAccessionId);
        }
        if (setWorkingDatabase(Database.CENTRAL)) {
            count = count + getMarkerInfoDao().countByDbAccessionId(dbAccessionId);
        }
        return count;
    }

    @Override
    public List<MarkerIdMarkerNameElement> getMarkerNamesByMarkerIds(List<Integer> markerIds) throws MiddlewareQueryException {
        if (setWorkingDatabase(markerIds.get(0))) {
            return getMarkerDao().getNamesByIds(markerIds);
        }
        return new ArrayList<MarkerIdMarkerNameElement>();

    }

        @Override
        public List<String> getAllMarkerTypes(int start, int numOfRows) throws MiddlewareQueryException {
        List<String> markerTypes = new ArrayList<String>();
        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;

        if (setWorkingDatabase(Database.CENTRAL)) {
            MarkerDAO dao = getMarkerDao();
            centralCount = countAllMarkerTypes(Database.CENTRAL);

            if (centralCount > start) {
                markerTypes.addAll(dao.getAllMarkerTypes(start, numOfRows));
                relativeLimit = numOfRows - markerTypes.size();
                if (relativeLimit > 0 && (setWorkingDatabase(Database.LOCAL))) {
                    dao = getMarkerDao();
                    localCount = countAllMarkerTypes(Database.LOCAL);
                    if (localCount > 0) {
                        markerTypes.addAll(dao.getAllMarkerTypes(0, (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (setWorkingDatabase(Database.LOCAL)) {
                    dao = getMarkerDao();
                    localCount = countAllMarkerTypes(Database.LOCAL);
                    if (localCount > relativeLimit) {
                        markerTypes.addAll(dao.getAllMarkerTypes((int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if (setWorkingDatabase(Database.LOCAL)) {
            MarkerDAO dao = getMarkerDao();
            localCount = countAllMarkerTypes(Database.LOCAL);
            if (localCount > start) {
                markerTypes.addAll(dao.getAllMarkerTypes(start, numOfRows));
            }
        }
        return markerTypes;
    }

    @Override
    public long countAllMarkerTypes(Database instance) throws MiddlewareQueryException {
        if (setWorkingDatabase(instance)) {
            return getMarkerDao().countAllMarkerTypes();
        }
        return 0;
    }

    @Override
    public List<String> getMarkerNamesByMarkerType(String markerType, int start, int numOfRows) throws MiddlewareQueryException {
        List<String> markerNames = new ArrayList<String>();

        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;

        if (setWorkingDatabase(Database.CENTRAL)) {
            MarkerDAO dao = getMarkerDao();
            centralCount = dao.countMarkerNamesByMarkerType(markerType);

            if (centralCount > start) {
                markerNames.addAll(dao.getMarkerNamesByMarkerType(markerType, start, numOfRows));
                relativeLimit = numOfRows - markerNames.size();
                if (relativeLimit > 0 && setWorkingDatabase(Database.LOCAL)) {
                    dao = getMarkerDao();
                    localCount = dao.countMarkerNamesByMarkerType(markerType);
                    if (localCount > 0) {
                        markerNames.addAll(dao.getMarkerNamesByMarkerType(markerType, 0, (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (setWorkingDatabase(Database.LOCAL)) {
                    dao = getMarkerDao();
                    localCount = dao.countMarkerNamesByMarkerType(markerType);
                    if (localCount > relativeLimit) {
                        markerNames.addAll(dao.getMarkerNamesByMarkerType(markerType, (int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if (setWorkingDatabase(Database.LOCAL)) {
            MarkerDAO dao = getMarkerDao();
            localCount = dao.countMarkerNamesByMarkerType(markerType);
            if (localCount > start) {
                markerNames.addAll(dao.getMarkerNamesByMarkerType(markerType, start, numOfRows));
            }
        }
        return markerNames;
    }

    @Override
    public long countMarkerNamesByMarkerType(String markerType) throws MiddlewareQueryException {
        long result = 0;
        if (setWorkingDatabase(Database.LOCAL)) {
            result = getMarkerDao().countMarkerNamesByMarkerType(markerType);
        }
        if (setWorkingDatabase(Database.CENTRAL)) {
            result += getMarkerDao().countMarkerNamesByMarkerType(markerType);
        }
        return result;
    }

    @Override
    public List<Integer> getGIDsFromCharValuesByMarkerId(Integer markerId, int start, int numOfRows) throws MiddlewareQueryException {
        if (setWorkingDatabase(markerId)) {
            return getCharValuesDao().getGIDsByMarkerId(markerId, start, numOfRows);
        }
        return new ArrayList<Integer>();
    }

    @Override
    public long countGIDsFromCharValuesByMarkerId(Integer markerId) throws MiddlewareQueryException {
        if (setWorkingDatabase(markerId)) {
            return getCharValuesDao().countGIDsByMarkerId(markerId);
        }
        return 0;
    }

    @Override
    public List<Integer> getGIDsFromAlleleValuesByMarkerId(Integer markerId, int start, int numOfRows) throws MiddlewareQueryException {
        if (setWorkingDatabase(markerId)) {
            return getAlleleValuesDao().getGIDsByMarkerId(markerId, start, numOfRows);
        }
        return new ArrayList<Integer>();
    }

    @Override
    public long countGIDsFromAlleleValuesByMarkerId(Integer markerId) throws MiddlewareQueryException {
        if (setWorkingDatabase(markerId)) {
            return getAlleleValuesDao().countGIDsByMarkerId(markerId);
        }
        return 0;
    }

    @Override
    public List<Integer> getGIDsFromMappingPopValuesByMarkerId(Integer markerId, int start, int numOfRows) throws MiddlewareQueryException {
        if (setWorkingDatabase(markerId)) {
            return getMappingPopValuesDao().getGIDsByMarkerId(markerId, start, numOfRows);
        }
        return new ArrayList<Integer>();
    }

    @Override
    public long countGIDsFromMappingPopValuesByMarkerId(Integer markerId) throws MiddlewareQueryException {
        if (setWorkingDatabase(markerId)) {
            return getMappingPopValuesDao().countGIDsByMarkerId(markerId);
        }
        return 0;
    }

    @Override
    public List<String> getAllDbAccessionIdsFromMarker(int start, int numOfRows) throws MiddlewareQueryException {
        List<String> dbAccessionIds = new ArrayList<String>();
        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;

        if (setWorkingDatabase(Database.CENTRAL)) {
            MarkerDAO dao = getMarkerDao();
            centralCount = dao.countAllDbAccessionIds();

            if (centralCount > start) {
                dbAccessionIds.addAll(dao.getAllDbAccessionIds(start, numOfRows));
                relativeLimit = numOfRows - dbAccessionIds.size();
                if (relativeLimit > 0 && setWorkingDatabase(Database.LOCAL)) {
                    dao = getMarkerDao();
                    localCount = dao.countAllDbAccessionIds();
                    if (localCount > 0) {
                        dbAccessionIds.addAll(dao.getAllDbAccessionIds(0, (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (setWorkingDatabase(Database.LOCAL)) {
                    dao = getMarkerDao();
                    localCount = dao.countAllDbAccessionIds();
                    if (localCount > relativeLimit) {
                        dbAccessionIds.addAll(dao.getAllDbAccessionIds((int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if (setWorkingDatabase(Database.LOCAL)) {
            MarkerDAO dao = getMarkerDao();
            localCount = dao.countAllDbAccessionIds();
            if (localCount > start) {
                dbAccessionIds.addAll(dao.getAllDbAccessionIds(start, numOfRows));
            }
        }
        return dbAccessionIds;
    }

    @Override
    public long countAllDbAccessionIdsFromMarker() throws MiddlewareQueryException {
        long result = 0;
        if (setWorkingDatabase(Database.CENTRAL)) {
            result += getMarkerDao().countAllDbAccessionIds();
        }
        if (setWorkingDatabase(Database.LOCAL)) {
            result += getMarkerDao().countAllDbAccessionIds();
        }
        return result;
    }

    @Override
    public List<Integer> getNidsFromAccMetadatasetByDatasetIds(List<Integer> datasetIds, int start, int numOfRows)
            throws MiddlewareQueryException {
        return getNidsFromAccMetadatasetByDatasetIds(datasetIds, null, start, numOfRows);
    }

    @Override
    public List<Integer> getNidsFromAccMetadatasetByDatasetIds(List<Integer> datasetIds, List<Integer> gids, int start, int numOfRows)
            throws MiddlewareQueryException {
        if (setWorkingDatabase(datasetIds.get(0))) {
            return getAccMetadataSetDao().getNIDsByDatasetIds(datasetIds, gids, start, numOfRows);
        }
        return new ArrayList<Integer>();
    }

    @Override
    public List<Integer> getNIdsByMarkerIdsAndDatasetIdsAndNotGIds(List<Integer> datasetIds, List<Integer> markerIds, List<Integer> gIds,
            int start, int numOfRows) throws MiddlewareQueryException {
        // TODO NO start and numOfRows?
        Set<Integer> nidSet = new TreeSet<Integer>();

        if (setWorkingDatabase(Database.CENTRAL)) {
            nidSet.addAll(getAccMetadataSetDao().getNIdsByMarkerIdsAndDatasetIdsAndNotGIds(datasetIds, markerIds, gIds));
        }
        if (setWorkingDatabase(Database.LOCAL)) {
            nidSet.addAll(getAccMetadataSetDao().getNIdsByMarkerIdsAndDatasetIdsAndNotGIds(datasetIds, markerIds, gIds));
        }
        List<Integer> nidList = new ArrayList<Integer>(((TreeSet<Integer>) nidSet).descendingSet());
        return nidList.subList(start, start + numOfRows);
    }

    @Override
    public int countNIdsByMarkerIdsAndDatasetIdsAndNotGIds(List<Integer> datasetIds, List<Integer> markerIds, List<Integer> gIds)
            throws MiddlewareQueryException {
        Set<Integer> nidSet = new TreeSet<Integer>();

        if (setWorkingDatabase(Database.CENTRAL)) {
            nidSet.addAll(getAccMetadataSetDao().getNIdsByMarkerIdsAndDatasetIdsAndNotGIds(datasetIds, markerIds, gIds));
        }
        if (setWorkingDatabase(Database.LOCAL)) {
            nidSet.addAll(getAccMetadataSetDao().getNIdsByMarkerIdsAndDatasetIdsAndNotGIds(datasetIds, markerIds, gIds));
        }
        List<Integer> nidList = new ArrayList<Integer>(nidSet);
        return nidList.size();
    }

    @Override
    public int countNIdsByMarkerIdsAndDatasetIds(List<Integer> datasetIds, List<Integer> markerIds) throws MiddlewareQueryException {
        Set<Integer> nidSet = new TreeSet<Integer>();

        if (setWorkingDatabase(Database.CENTRAL)) {
            nidSet.addAll(getAccMetadataSetDao().getNIdsByMarkerIdsAndDatasetIds(datasetIds, markerIds));
        }
        if (setWorkingDatabase(Database.LOCAL)) {
            nidSet.addAll(getAccMetadataSetDao().getNIdsByMarkerIdsAndDatasetIds(datasetIds, markerIds));
        }
        List<Integer> nidList = new ArrayList<Integer>(nidSet);
        return nidList.size();
    }

    @Override
    public List<Integer> getNIdsByMarkerIdsAndDatasetIds(List<Integer> datasetIds, List<Integer> markerIds, int start, int numOfRows)
            throws MiddlewareQueryException {
        // TODO NO start and numOfRows?
        Set<Integer> nidSet = new TreeSet<Integer>();

        if (setWorkingDatabase(Database.CENTRAL)) {
            nidSet.addAll(getAccMetadataSetDao().getNIdsByMarkerIdsAndDatasetIds(datasetIds, markerIds));
        }
        if (setWorkingDatabase(Database.LOCAL)) {
            nidSet.addAll(getAccMetadataSetDao().getNIdsByMarkerIdsAndDatasetIds(datasetIds, markerIds));
        }
        List<Integer> nidList = new ArrayList<Integer>(((TreeSet<Integer>) nidSet).descendingSet());
        return nidList.subList(start, start + numOfRows);
    }

    @Override
    public List<Integer> getDatasetIdsForFingerPrinting(int start, int numOfRows) throws MiddlewareQueryException {
        List<Integer> datasetIds = new ArrayList<Integer>();
        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;

        if (setWorkingDatabase(Database.CENTRAL)) {
            DatasetDAO dao = getDatasetDao();
            centralCount = dao.countDatasetIdsForFingerPrinting();
            if (centralCount > start) {
                datasetIds.addAll(dao.getDatasetIdsForFingerPrinting(start, numOfRows));
                relativeLimit = numOfRows - datasetIds.size();
                if (relativeLimit > 0 && setWorkingDatabase(Database.LOCAL)) {
                    dao = getDatasetDao();
                    localCount = dao.countDatasetIdsForFingerPrinting();
                    if (localCount > 0) {
                        datasetIds.addAll(dao.getDatasetIdsForFingerPrinting(0, (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (setWorkingDatabase(Database.LOCAL)) {
                    dao = getDatasetDao();
                    localCount = dao.countDatasetIdsForFingerPrinting();
                    if (localCount > relativeLimit) {
                        datasetIds.addAll(dao.getDatasetIdsForFingerPrinting((int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if (setWorkingDatabase(Database.LOCAL)) {
            DatasetDAO dao = getDatasetDao();
            localCount = dao.countDatasetIdsForFingerPrinting();
            if (localCount > start) {
                datasetIds.addAll(dao.getDatasetIdsForFingerPrinting(start, numOfRows));
            }
        }
        return datasetIds;
    }

    @Override
    public long countDatasetIdsForFingerPrinting() throws MiddlewareQueryException {
        long result = 0;
        if (setWorkingDatabase(Database.CENTRAL)) {
            result = getDatasetDao().countDatasetIdsForFingerPrinting();
        }
        if (setWorkingDatabase(Database.LOCAL)) {
            result += getDatasetDao().countDatasetIdsForFingerPrinting();
        }
        return result;
    }

    @Override
    public List<Integer> getDatasetIdsForMapping(int start, int numOfRows) throws MiddlewareQueryException {
        List<Integer> datasetIds = new ArrayList<Integer>();
        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;

        if (setWorkingDatabase(Database.CENTRAL)) {
            DatasetDAO dao = getDatasetDao();
            centralCount = dao.countDatasetIdsForMapping();
            if (centralCount > start) {
                datasetIds.addAll(dao.getDatasetIdsForMapping(start, numOfRows));
                relativeLimit = numOfRows - datasetIds.size();
                if (relativeLimit > 0 && setWorkingDatabase(Database.LOCAL)) {
                    dao = getDatasetDao();
                    localCount = dao.countDatasetIdsForMapping();
                    if (localCount > 0) {
                        datasetIds.addAll(dao.getDatasetIdsForMapping(0, (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (setWorkingDatabase(Database.LOCAL)) {
                    dao = getDatasetDao();
                    localCount = dao.countDatasetIdsForMapping();
                    if (localCount > relativeLimit) {
                        datasetIds.addAll(dao.getDatasetIdsForMapping((int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if (setWorkingDatabase(Database.LOCAL)) {
            DatasetDAO dao = getDatasetDao();
            localCount = dao.countDatasetIdsForMapping();
            if (localCount > start) {
                datasetIds.addAll(dao.getDatasetIdsForMapping(start, numOfRows));
            }
        }
        return datasetIds;
    }

    @Override
    public long countDatasetIdsForMapping() throws MiddlewareQueryException {
        long result = 0;
        if (setWorkingDatabase(Database.CENTRAL)) {
            result = getDatasetDao().countDatasetIdsForMapping();
        }
        if (setWorkingDatabase(Database.LOCAL)) {
            result += getDatasetDao().countDatasetIdsForMapping();
        }
        return result;
    }

    @Override
    public List<AccMetadataSetPK> getGdmsAccMetadatasetByGid(List<Integer> gids, int start, int numOfRows) throws MiddlewareQueryException {
        List<AccMetadataSetPK> accMetadataSets = new ArrayList<AccMetadataSetPK>();
        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;

        List<Integer> positiveGids = GenotypicDataManagerUtil.getPositiveIds(gids);
        List<Integer> negativeGids = GenotypicDataManagerUtil.getNegativeIds(gids);

        if (setWorkingDatabase(Database.CENTRAL) && (positiveGids != null) && (!positiveGids.isEmpty())) {
            AccMetadataSetDAO dao = getAccMetadataSetDao();
            centralCount = dao.countAccMetadataSetByGids(positiveGids);

            if (centralCount > start) {
                accMetadataSets.addAll(dao.getAccMetadasetByGids(positiveGids, start, numOfRows));
                relativeLimit = numOfRows - accMetadataSets.size();
                if (relativeLimit > 0 && setWorkingDatabase(Database.LOCAL)) {
                    dao = getAccMetadataSetDao();
                    localCount = dao.countAccMetadataSetByGids(negativeGids);
                    if (localCount > 0) {
                        accMetadataSets.addAll(dao.getAccMetadasetByGids(negativeGids, 0, (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (setWorkingDatabase(Database.LOCAL) && (negativeGids != null) && (!negativeGids.isEmpty())) {
                    dao = getAccMetadataSetDao();
                    localCount = dao.countAccMetadataSetByGids(negativeGids);
                    if (localCount > relativeLimit) {
                        accMetadataSets.addAll(dao.getAccMetadasetByGids(negativeGids, (int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if (setWorkingDatabase(Database.LOCAL) && (negativeGids != null) && (!negativeGids.isEmpty())) {
            AccMetadataSetDAO dao = getAccMetadataSetDao();
            localCount = dao.countAccMetadataSetByGids(negativeGids);
            if (localCount > start) {
                accMetadataSets.addAll(dao.getAccMetadasetByGids(negativeGids, start, numOfRows));
            }
        }
        return accMetadataSets;
    }

    @Override
    public long countGdmsAccMetadatasetByGid(List<Integer> gids) throws MiddlewareQueryException {
        List<Integer> positiveGids = GenotypicDataManagerUtil.getPositiveIds(gids);
        List<Integer> negativeGids = GenotypicDataManagerUtil.getNegativeIds(gids);

        long result = 0;
        // Count from local
        if (setWorkingDatabase(Database.LOCAL) && (negativeGids != null) && (!negativeGids.isEmpty())) {
            result += getAccMetadataSetDao().countAccMetadataSetByGids(negativeGids);
        }
        // Count from central
        if (setWorkingDatabase(Database.CENTRAL) && (positiveGids != null) && (!positiveGids.isEmpty())) {
            result += getAccMetadataSetDao().countAccMetadataSetByGids(positiveGids);
        }

        return result;
    }

    @Override
    public List<Integer> getMarkersByGidAndDatasetIds(Integer gid, List<Integer> datasetIds, int start, int numOfRows)
            throws MiddlewareQueryException {
        List<Integer> markerIds = new ArrayList<Integer>();
        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;

        if (setWorkingDatabase(Database.CENTRAL)) {
            MarkerMetadataSetDAO dao = getMarkerMetadataSetDao();
            centralCount = dao.countMarkersByGidAndDatasetIds(gid, datasetIds);

            if (centralCount > start) {
                markerIds.addAll(dao.getMarkersByGidAndDatasetIds(gid, datasetIds, start, numOfRows));
                relativeLimit = numOfRows - markerIds.size();
                if (relativeLimit > 0 && setWorkingDatabase(Database.LOCAL)) {
                    dao = getMarkerMetadataSetDao();
                    localCount = dao.countMarkersByGidAndDatasetIds(gid, datasetIds);
                    if (localCount > 0) {
                        markerIds.addAll(dao.getMarkersByGidAndDatasetIds(gid, datasetIds, 0, (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (setWorkingDatabase(Database.LOCAL)) {
                    dao = getMarkerMetadataSetDao();
                    localCount = dao.countMarkersByGidAndDatasetIds(gid, datasetIds);
                    if (localCount > relativeLimit) {
                        markerIds.addAll(dao.getMarkersByGidAndDatasetIds(gid, datasetIds, (int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if (setWorkingDatabase(Database.LOCAL)) {
            MarkerMetadataSetDAO dao = getMarkerMetadataSetDao();
            localCount = dao.countMarkersByGidAndDatasetIds(gid, datasetIds);
            if (localCount > start) {
                markerIds.addAll(dao.getMarkersByGidAndDatasetIds(gid, datasetIds, start, numOfRows));
            }
        }
        return markerIds;
    }

    @Override
    public long countMarkersByGidAndDatasetIds(Integer gid, List<Integer> datasetIds) throws MiddlewareQueryException {
        long result = 0;
        if (setWorkingDatabase(Database.CENTRAL)) {
            result = getMarkerMetadataSetDao().countMarkersByGidAndDatasetIds(gid, datasetIds);
        }
        if (setWorkingDatabase(Database.LOCAL)) {
            result += getMarkerMetadataSetDao().countMarkersByGidAndDatasetIds(gid, datasetIds);
        }
        return result;
    }

    @Override
    public List<Marker> getMarkersByMarkerIDs(List<Integer> markerIDs, int start, int numOfRows) throws MiddlewareQueryException {
        List<Marker> markerIds = new ArrayList<Marker>();
        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;

        if (setWorkingDatabase(Database.CENTRAL)) {
            MarkerDAO dao = getMarkerDao();
            centralCount = dao.countMarkersByIds(markerIDs);

            if (centralCount > start) {
                markerIds.addAll(dao.getMarkersByIds(markerIDs, start, numOfRows));
                relativeLimit = numOfRows - (centralCount - start);
                if (relativeLimit > 0 && setWorkingDatabase(Database.LOCAL)) {
                    dao = getMarkerDao();
                    localCount = dao.countMarkersByIds(markerIDs);
                    if (localCount > 0) {
                        markerIds.addAll(dao.getMarkersByIds(markerIDs, 0, (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (setWorkingDatabase(Database.LOCAL)) {
                    dao = getMarkerDao();
                    localCount = dao.countMarkersByIds(markerIDs);
                    if (localCount > relativeLimit) {
                        markerIds.addAll(dao.getMarkersByIds(markerIDs, (int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if (setWorkingDatabase(Database.LOCAL)) {
            MarkerDAO dao = getMarkerDao();
            localCount = dao.countMarkersByIds(markerIDs);
            if (localCount > start) {
                markerIds.addAll(dao.getMarkersByIds(markerIDs, start, numOfRows));
            }
        }
        return markerIds;
    }

    @Override
    public long countMarkersByMarkerIDs(List<Integer> markerIDs) throws MiddlewareQueryException {
        long result = 0;
        if (setWorkingDatabase(Database.CENTRAL)) {
            result = getMarkerDao().countMarkersByIds(markerIDs);
        }
        if (setWorkingDatabase(Database.LOCAL)) {
            result += getMarkerDao().countMarkersByIds(markerIDs);
        }
        return result;

    }

    @Override
    public long countAlleleValuesByGids(List<Integer> gids) throws MiddlewareQueryException {
        long result = 0;
        if (setWorkingDatabase(Database.LOCAL)) {
            result += getAlleleValuesDao().countAlleleValuesByGids(gids);
        }
        if (setWorkingDatabase(Database.CENTRAL)) {
            result += getAlleleValuesDao().countAlleleValuesByGids(gids);
        }
        return result;
    }

    @Override
    public long countCharValuesByGids(List<Integer> gids) throws MiddlewareQueryException {
        long result = 0;
        if (setWorkingDatabase(Database.LOCAL)) {
            result += getCharValuesDao().countCharValuesByGids(gids);
        }
        if (setWorkingDatabase(Database.CENTRAL)) {
            result += getCharValuesDao().countCharValuesByGids(gids);
        }
        return result;
    }

    @Override
    public List<AllelicValueElement> getIntAlleleValuesForPolymorphicMarkersRetrieval(List<Integer> gids, int start, int numOfRows)
            throws MiddlewareQueryException {
        List<AllelicValueElement> allelicValueElements = new ArrayList<AllelicValueElement>();
        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;

        List<Integer> positiveGids = GenotypicDataManagerUtil.getPositiveIds(gids);
        List<Integer> negativeGids = GenotypicDataManagerUtil.getNegativeIds(gids);

        if (setWorkingDatabase(Database.CENTRAL) && (positiveGids != null) && (!positiveGids.isEmpty())) {
            AlleleValuesDAO dao = getAlleleValuesDao();
            centralCount = dao.countIntAlleleValuesForPolymorphicMarkersRetrieval(positiveGids);

            if (centralCount > start) {
                allelicValueElements.addAll(dao.getIntAlleleValuesForPolymorphicMarkersRetrieval(positiveGids, start, numOfRows));
                relativeLimit = numOfRows - allelicValueElements.size();
                if (relativeLimit > 0 && setWorkingDatabase(Database.LOCAL)) {
                    dao = getAlleleValuesDao();
                    localCount = dao.countIntAlleleValuesForPolymorphicMarkersRetrieval(negativeGids);
                    if (localCount > 0) {
                        allelicValueElements.addAll(dao.getIntAlleleValuesForPolymorphicMarkersRetrieval(negativeGids, 0,
                                (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (setWorkingDatabase(Database.LOCAL) && (negativeGids != null) && (!negativeGids.isEmpty())) {
                    dao = getAlleleValuesDao();
                    localCount = dao.countIntAlleleValuesForPolymorphicMarkersRetrieval(negativeGids);
                    if (localCount > relativeLimit) {
                        allelicValueElements.addAll(dao.getIntAlleleValuesForPolymorphicMarkersRetrieval(negativeGids, (int) relativeLimit,
                                numOfRows));
                    }
                }
            }
        } else if (setWorkingDatabase(Database.LOCAL) && (negativeGids != null) && (!negativeGids.isEmpty())) {
            AlleleValuesDAO dao = getAlleleValuesDao();
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
    public long countIntAlleleValuesForPolymorphicMarkersRetrieval(List<Integer> gids) throws MiddlewareQueryException {
        List<Integer> positiveGids = GenotypicDataManagerUtil.getPositiveIds(gids);
        List<Integer> negativeGids = GenotypicDataManagerUtil.getNegativeIds(gids);
        long result = 0;
        if (setWorkingDatabase(Database.LOCAL) && (negativeGids != null) && (!negativeGids.isEmpty())) {
            result += getAlleleValuesDao().countIntAlleleValuesForPolymorphicMarkersRetrieval(negativeGids);
        }
        if (setWorkingDatabase(Database.CENTRAL) && (positiveGids != null) && (!positiveGids.isEmpty())) {
            result += getAlleleValuesDao().countIntAlleleValuesForPolymorphicMarkersRetrieval(positiveGids);
        }
        return result;
    }

    @Override
    public List<AllelicValueElement> getCharAlleleValuesForPolymorphicMarkersRetrieval(List<Integer> gids, int start, int numOfRows)
            throws MiddlewareQueryException {

        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;

        List<Integer> positiveGids = GenotypicDataManagerUtil.getPositiveIds(gids);
        List<Integer> negativeGids = GenotypicDataManagerUtil.getNegativeIds(gids);

        List<AllelicValueElement> allelicValueElements = new ArrayList<AllelicValueElement>();

        if (setWorkingDatabase(Database.CENTRAL) && (positiveGids != null) && (!positiveGids.isEmpty())) {
            AlleleValuesDAO dao = getAlleleValuesDao();
            centralCount = dao.countCharAlleleValuesForPolymorphicMarkersRetrieval(positiveGids);

            if (centralCount > start) {
                allelicValueElements.addAll(dao.getCharAlleleValuesForPolymorphicMarkersRetrieval(positiveGids, start, numOfRows));
                relativeLimit = numOfRows - allelicValueElements.size();
                if (relativeLimit > 0 && setWorkingDatabase(Database.LOCAL)) {
                    dao = getAlleleValuesDao();
                    localCount = dao.countCharAlleleValuesForPolymorphicMarkersRetrieval(negativeGids);
                    if (localCount > 0) {
                        allelicValueElements.addAll(dao.getCharAlleleValuesForPolymorphicMarkersRetrieval(negativeGids, 0,
                                (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if ((setWorkingDatabase(Database.LOCAL)) && (negativeGids != null) && (!negativeGids.isEmpty())) {
                    dao = getAlleleValuesDao();
                    localCount = dao.countCharAlleleValuesForPolymorphicMarkersRetrieval(negativeGids);
                    if (localCount > relativeLimit) {
                        allelicValueElements.addAll(dao.getCharAlleleValuesForPolymorphicMarkersRetrieval(negativeGids,
                                (int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if ((setWorkingDatabase(Database.LOCAL)) && (negativeGids != null) && (!negativeGids.isEmpty())) {
            AlleleValuesDAO dao = getAlleleValuesDao();
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
    public long countCharAlleleValuesForPolymorphicMarkersRetrieval(List<Integer> gids) throws MiddlewareQueryException {
        List<Integer> positiveGids = GenotypicDataManagerUtil.getPositiveIds(gids);
        List<Integer> negativeGids = GenotypicDataManagerUtil.getNegativeIds(gids);

        long result = 0;
        if ((setWorkingDatabase(Database.LOCAL)) && (negativeGids != null) && (!negativeGids.isEmpty())) {
            result += getAlleleValuesDao().countCharAlleleValuesForPolymorphicMarkersRetrieval(negativeGids);
        }
        if ((setWorkingDatabase(Database.CENTRAL)) && (positiveGids != null) && (!positiveGids.isEmpty())) {
            result += getAlleleValuesDao().countCharAlleleValuesForPolymorphicMarkersRetrieval(positiveGids);
        }
        return result;
    }

    @Override
    public List<AllelicValueElement> getMappingAlleleValuesForPolymorphicMarkersRetrieval(List<Integer> gids, int start, int numOfRows)
            throws MiddlewareQueryException {
        List<AllelicValueElement> allelicValueElements = new ArrayList<AllelicValueElement>();
        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;

        List<Integer> positiveGids = GenotypicDataManagerUtil.getPositiveIds(gids);
        List<Integer> negativeGids = GenotypicDataManagerUtil.getNegativeIds(gids);

        if ((setWorkingDatabase(Database.CENTRAL)) && (positiveGids != null) && (!positiveGids.isEmpty())) {
            AlleleValuesDAO dao = getAlleleValuesDao();
            centralCount = dao.countMappingAlleleValuesForPolymorphicMarkersRetrieval(positiveGids);

            if (centralCount > start) {
                allelicValueElements.addAll(dao.getMappingAlleleValuesForPolymorphicMarkersRetrieval(positiveGids, start, numOfRows));
                relativeLimit = numOfRows - allelicValueElements.size();
                if (relativeLimit > 0 && setWorkingDatabase(Database.LOCAL)) {
                    dao = getAlleleValuesDao();
                    localCount = dao.countMappingAlleleValuesForPolymorphicMarkersRetrieval(negativeGids);
                    if (localCount > 0) {
                        allelicValueElements.addAll(dao.getMappingAlleleValuesForPolymorphicMarkersRetrieval(negativeGids, 0,
                                (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if ((setWorkingDatabase(Database.LOCAL)) && (negativeGids != null) && (!negativeGids.isEmpty())) {
                    dao = getAlleleValuesDao();
                    localCount = dao.countMappingAlleleValuesForPolymorphicMarkersRetrieval(negativeGids);
                    if (localCount > relativeLimit) {
                        allelicValueElements.addAll(dao.getMappingAlleleValuesForPolymorphicMarkersRetrieval(negativeGids,
                                (int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if ((setWorkingDatabase(Database.LOCAL)) && (negativeGids != null) && (!negativeGids.isEmpty())) {
            AlleleValuesDAO dao = getAlleleValuesDao();
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
    public long countMappingAlleleValuesForPolymorphicMarkersRetrieval(List<Integer> gids) throws MiddlewareQueryException {
        List<Integer> positiveGids = GenotypicDataManagerUtil.getPositiveIds(gids);
        List<Integer> negativeGids = GenotypicDataManagerUtil.getNegativeIds(gids);

        long result = 0;
        if (setWorkingDatabase(Database.LOCAL) && (negativeGids != null) && (!negativeGids.isEmpty())) {
            result += getAlleleValuesDao().countMappingAlleleValuesForPolymorphicMarkersRetrieval(negativeGids);
        }
        if (setWorkingDatabase(Database.CENTRAL) && (positiveGids != null) && (!positiveGids.isEmpty())) {
            result += getAlleleValuesDao().countMappingAlleleValuesForPolymorphicMarkersRetrieval(positiveGids);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Qtl> getAllQtl(int start, int numOfRows) throws MiddlewareQueryException {
        return (List<Qtl>) getAllFromCentralAndLocal(getQtlDao(), start, numOfRows);
    }

    @Override
    public long countAllQtl() throws MiddlewareQueryException {
        return countAllFromCentralAndLocal(getQtlDao());
    }

    @Override
    public List<Integer> getQtlIdByName(String name, int start, int numOfRows) throws MiddlewareQueryException {
        if ((name == null) || (name.isEmpty())) {
            return new ArrayList<Integer>();
        }

        List<Integer> qtlIds = new ArrayList<Integer>();
        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;

        if (setWorkingDatabase(Database.CENTRAL)) {
            QtlDAO dao = getQtlDao();
            centralCount = dao.countQtlIdByName(name);

            if (centralCount > start) {
                qtlIds.addAll(dao.getQtlIdByName(name, start, numOfRows));
                relativeLimit = numOfRows - qtlIds.size();
                if (relativeLimit > 0 && setWorkingDatabase(Database.LOCAL)) {
                    dao = getQtlDao();
                    localCount = dao.countQtlIdByName(name);
                    if (localCount > 0) {
                        List<Integer> qtlIdList = dao.getQtlIdByName(name, 0, (int) relativeLimit);
                        qtlIds.addAll(qtlIdList);
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (setWorkingDatabase(Database.LOCAL)) {
                    dao = getQtlDao();
                    localCount = dao.countQtlIdByName(name);
                    if (localCount > relativeLimit) {
                        List<Integer> qtlIdList = dao.getQtlIdByName(name, (int) relativeLimit, numOfRows);
                        qtlIds.addAll(qtlIdList);
                    }
                }
            }
        } else if (setWorkingDatabase(Database.LOCAL)) {
            QtlDAO dao = getQtlDao();
            localCount = dao.countQtlIdByName(name);
            if (localCount > start) {
                List<Integer> qtlIdList = dao.getQtlIdByName(name, start, numOfRows);
                qtlIds.addAll(qtlIdList);
            }
        }
        return qtlIds;
    }

    @Override
    public long countQtlIdByName(String name) throws MiddlewareQueryException {
        if ((name == null) || (name.isEmpty())) {
            return 0;
        }
        long result = 0;
        if (setWorkingDatabase(Database.LOCAL)) {
            result += getQtlDao().countQtlIdByName(name);
        }
        if (setWorkingDatabase(Database.CENTRAL)) {
            result += getQtlDao().countQtlIdByName(name);
        }
        return result;
    }

    @Override
    public List<QtlDetailElement> getQtlByName(String name, int start, int numOfRows) throws MiddlewareQueryException {
        if ((name == null) || (name.isEmpty())) {
            return new ArrayList<QtlDetailElement>();
        }
        List<QtlDetailElement> qtl = new ArrayList<QtlDetailElement>();
        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;

        if (setWorkingDatabase(Database.CENTRAL)) {
            QtlDAO dao = getQtlDao();
            centralCount = dao.countQtlDetailsByName(name);

            if (centralCount > start) {
                qtl.addAll(dao.getQtlDetailsByName(name, start, numOfRows));
                relativeLimit = numOfRows - qtl.size();
                if (relativeLimit > 0 && setWorkingDatabase(Database.LOCAL)) {
                    dao = getQtlDao();
                    localCount = dao.countQtlDetailsByName(name);
                    if (localCount > 0) {
                        qtl.addAll(dao.getQtlDetailsByName(name, 0, (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (setWorkingDatabase(Database.LOCAL)) {
                    dao = getQtlDao();
                    localCount = dao.countQtlDetailsByName(name);
                    if (localCount > relativeLimit) {
                        qtl.addAll(dao.getQtlDetailsByName(name, (int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if (setWorkingDatabase(Database.LOCAL)) {
            QtlDAO dao = getQtlDao();
            localCount = dao.countQtlDetailsByName(name);
            if (localCount > start) {
                qtl.addAll(dao.getQtlDetailsByName(name, start, numOfRows));
            }
        }
        return qtl;
    }

    @Override
    public long countQtlByName(String name) throws MiddlewareQueryException {
        if ((name == null) || (name.isEmpty())) {
            return 0;
        }

        long result = 0;
        if (setWorkingDatabase(Database.LOCAL)) {
            result += getQtlDao().countQtlDetailsByName(name);
        }
        if (setWorkingDatabase(Database.CENTRAL)) {
            result += getQtlDao().countQtlDetailsByName(name);
        }
        return result;
    }

    @Override
    public List<QtlDetailElement> getQTLByQTLIDs(List<Integer> qtlIDs, int start, int numOfRows) throws MiddlewareQueryException {
        if ((qtlIDs == null) || (qtlIDs.isEmpty())) {
            return new ArrayList<QtlDetailElement>();
        }

        List<QtlDetailElement> qtl = new ArrayList<QtlDetailElement>();
        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;

        if (setWorkingDatabase(Database.CENTRAL)) {
            QtlDAO dao = getQtlDao();
            centralCount = dao.countQtlDetailsByQTLIDs(qtlIDs);

            if (centralCount > start) {
                qtl.addAll(dao.getQtlDetailsByQTLIDs(qtlIDs, start, numOfRows));
                relativeLimit = numOfRows - qtl.size();
                if (relativeLimit > 0 && setWorkingDatabase(Database.LOCAL)) {
                    dao = getQtlDao();
                    localCount = dao.countQtlDetailsByQTLIDs(qtlIDs);
                    if (localCount > 0) {
                        qtl.addAll(dao.getQtlDetailsByQTLIDs(qtlIDs, 0, (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (setWorkingDatabase(Database.LOCAL)) {
                    dao = getQtlDao();
                    localCount = dao.countQtlDetailsByQTLIDs(qtlIDs);
                    if (localCount > relativeLimit) {
                        qtl.addAll(dao.getQtlDetailsByQTLIDs(qtlIDs, (int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if (setWorkingDatabase(Database.LOCAL)) {
            QtlDAO dao = getQtlDao();
            localCount = dao.countQtlDetailsByQTLIDs(qtlIDs);
            if (localCount > start) {
                qtl.addAll(dao.getQtlDetailsByQTLIDs(qtlIDs, start, numOfRows));
            }
        }
        return qtl;
    }

    @Override
    public long countQTLByQTLIDs(List<Integer> qtlIDs) throws MiddlewareQueryException {
        if ((qtlIDs == null) || (qtlIDs.isEmpty())) {
            return 0;
        }

        long result = 0;
        if (setWorkingDatabase(Database.LOCAL)) {
            result += getQtlDao().countQtlDetailsByQTLIDs(qtlIDs);
        }
        if (setWorkingDatabase(Database.CENTRAL)) {
            result += getQtlDao().countQtlDetailsByQTLIDs(qtlIDs);
        }
        return result;
    }

    @Override
    public List<Integer> getQtlByTrait(String trait, int start, int numOfRows) throws MiddlewareQueryException {

        List<Integer> qtl = new ArrayList<Integer>();
        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;

        if (setWorkingDatabase(Database.CENTRAL)) {
            QtlDAO dao = getQtlDao();
            centralCount = dao.countQtlByTrait(trait);

            if (centralCount > start) {
                qtl.addAll(dao.getQtlByTrait(trait, start, numOfRows));
                relativeLimit = numOfRows - qtl.size();
                if (relativeLimit > 0 && setWorkingDatabase(Database.LOCAL)) {
                    dao = getQtlDao();
                    localCount = dao.countQtlByTrait(trait);
                    if (localCount > 0) {
                        qtl.addAll(dao.getQtlByTrait(trait, 0, (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (setWorkingDatabase(Database.LOCAL)) {
                    dao = getQtlDao();
                    localCount = dao.countQtlByTrait(trait);
                    if (localCount > relativeLimit) {
                        qtl.addAll(dao.getQtlByTrait(trait, (int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if (setWorkingDatabase(Database.LOCAL)) {
            QtlDAO dao = getQtlDao();
            localCount = dao.countQtlByTrait(trait);
            if (localCount > start) {
                qtl.addAll(dao.getQtlByTrait(trait, start, numOfRows));
            }
        }

        return qtl;

    }

    @Override
    public long countQtlByTrait(String trait) throws MiddlewareQueryException {
        long result = 0;
        if (setWorkingDatabase(Database.LOCAL)) {
            result += getQtlDao().countQtlByTrait(trait);
        }
        if (setWorkingDatabase(Database.CENTRAL)) {
            result += getQtlDao().countQtlByTrait(trait);
        }
        return result;
    }

        @Override
        public List<ParentElement> getAllParentsFromMappingPopulation(int start, int numOfRows) throws MiddlewareQueryException {
        List<ParentElement> allParentsFromMappingPopulation = new ArrayList<ParentElement>();
        Long centralCount = Long.valueOf(0);
        Long localCount = Long.valueOf(0);
        int relativeLimit = 0;

        if (setWorkingDatabase(Database.CENTRAL)) {
            MappingPopDAO mappingPopDao = getMappingPopDao();
            centralCount = mappingPopDao.countAllParentsFromMappingPopulation();
            if (centralCount > start) {
                allParentsFromMappingPopulation.addAll(mappingPopDao.getAllParentsFromMappingPopulation(start, numOfRows));
                relativeLimit = numOfRows - (centralCount.intValue() - start);

                if (relativeLimit > 0) {

                    if (setWorkingDatabase(Database.LOCAL)) {
                        mappingPopDao = getMappingPopDao();
                        localCount = mappingPopDao.countAllParentsFromMappingPopulation();

                        if (localCount > 0) {
                            allParentsFromMappingPopulation.addAll(mappingPopDao.getAllParentsFromMappingPopulation(0, relativeLimit));
                        }
                    }
                }
            } else {
                relativeLimit = start - centralCount.intValue();
                if (setWorkingDatabase(Database.LOCAL)) {
                    mappingPopDao = getMappingPopDao();
                    localCount = mappingPopDao.countAllParentsFromMappingPopulation();
                    if (localCount > relativeLimit) {
                        allParentsFromMappingPopulation.addAll(mappingPopDao.getAllParentsFromMappingPopulation(relativeLimit, numOfRows));
                    }
                }
            }
        } else if (setWorkingDatabase(Database.LOCAL)) {
            MappingPopDAO mappingPopDao = getMappingPopDao();
            localCount = mappingPopDao.countAllParentsFromMappingPopulation();
            if (localCount > start) {
                allParentsFromMappingPopulation.addAll(mappingPopDao.getAllParentsFromMappingPopulation(start, numOfRows));
            }
        }

        return allParentsFromMappingPopulation;
    }

    @Override
    public Long countAllParentsFromMappingPopulation() throws MiddlewareQueryException {
        long count = 0;
        if (setWorkingDatabase(Database.CENTRAL)){
            count += getMappingPopDao().countAllParentsFromMappingPopulation();
        }
        if (setWorkingDatabase(Database.LOCAL)){
            count += getMappingPopDao().countAllParentsFromMappingPopulation();
        }
        return count;
    }

    @Override
    public List<MapDetailElement> getMapDetailsByName(String nameLike, int start, int numOfRows) throws MiddlewareQueryException {
        List<MapDetailElement> maps = new ArrayList<MapDetailElement>();
        Long centralCount = Long.valueOf(0);
        Long localCount = Long.valueOf(0);
        Long relativeLimit = Long.valueOf(0);

        if (setWorkingDatabase(Database.CENTRAL)) {
            MapDAO mapDao = getMapDao();
            centralCount = mapDao.countMapDetailsByName(nameLike);
            if (centralCount > start) {
                maps.addAll(mapDao.getMapDetailsByName(nameLike, start, numOfRows));
                relativeLimit = numOfRows - (centralCount - start);
                if (relativeLimit > 0) {
                    if (setWorkingDatabase(Database.LOCAL)) {
                        mapDao = getMapDao();
                        localCount = mapDao.countMapDetailsByName(nameLike);
                        if (localCount > 0) {
                            maps.addAll(mapDao.getMapDetailsByName(nameLike, 0, relativeLimit.intValue()));
                        }
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (setWorkingDatabase(Database.LOCAL)) {
                    mapDao = getMapDao();
                    localCount = mapDao.countMapDetailsByName(nameLike);
                    if (localCount > relativeLimit) {
                        maps.addAll(mapDao.getMapDetailsByName(nameLike, relativeLimit.intValue(), numOfRows));
                    }
                }
            }
        } else if (setWorkingDatabase(Database.LOCAL)) {
            MapDAO mapDao = getMapDao();
            localCount = mapDao.countMapDetailsByName(nameLike);
            if (localCount > start) {
                maps.addAll(mapDao.getMapDetailsByName(nameLike, start, numOfRows));
            }
        }
        return maps;
    }

    @Override
    public Long countMapDetailsByName(String nameLike) throws MiddlewareQueryException {
        long count = 0;
        if (setWorkingDatabase(Database.CENTRAL)){
            count += getMapDao().countMapDetailsByName(nameLike);
        }
        if (setWorkingDatabase(Database.LOCAL)){
            count += getMapDao().countMapDetailsByName(nameLike);
        }
        return count;
    }

    public List<MapDetailElement> getAllMapDetails(int start, int numOfRows) throws MiddlewareQueryException {
        List<MapDetailElement> maps = new ArrayList<MapDetailElement>();
        Long centralCount = Long.valueOf(0);
        Long localCount = Long.valueOf(0);
        Long relativeLimit = Long.valueOf(0);

        if (setWorkingDatabase(Database.CENTRAL)) {
            MapDAO mapDao = getMapDao();
            centralCount = mapDao.countAllMapDetails();

            if (centralCount > start) {
                maps.addAll(mapDao.getAllMapDetails(start, numOfRows));
                relativeLimit = numOfRows - (centralCount - start);

                if (relativeLimit > 0) {

                    if (setWorkingDatabase(Database.LOCAL)) {
                        mapDao = getMapDao();
                        localCount = mapDao.countAllMapDetails();

                        if (localCount > 0) {
                            maps.addAll(mapDao.getAllMapDetails(0, relativeLimit.intValue()));
                        }
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (setWorkingDatabase(Database.LOCAL)) {
                    mapDao = getMapDao();
                    localCount = mapDao.countAllMapDetails();
                    if (localCount > relativeLimit) {
                        maps.addAll(mapDao.getAllMapDetails(relativeLimit.intValue(), numOfRows));
                    }
                }
            }
        } else if (setWorkingDatabase(Database.LOCAL)) {
            MapDAO mapDao = getMapDao();
            localCount = mapDao.countAllMapDetails();
            if (localCount > start) {
                maps.addAll(mapDao.getAllMapDetails(start, numOfRows));
            }
        }

        return maps;
    }

    public long countAllMapDetails() throws MiddlewareQueryException {
        long count = 0;
        if (setWorkingDatabase(Database.CENTRAL)) {
            count += getMapDao().countAllMapDetails();
        }
        if (setWorkingDatabase(Database.LOCAL)) {
            count += getMapDao().countAllMapDetails();
        }
        return count;
    }

    @Override
    public List<Integer> getMapIdsByQtlName(String qtlName, int start, int numOfRows) throws MiddlewareQueryException {
        List<Integer> mapIds = new ArrayList<Integer>();
        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;
        if (setWorkingDatabase(Database.CENTRAL)) {
            QtlDetailsDAO dao = getQtlDetailsDao();
            centralCount = dao.countMapIdsByQtlName(qtlName);

            if (centralCount > start) {
                mapIds.addAll(dao.getMapIdsByQtlName(qtlName, start, numOfRows));
                relativeLimit = numOfRows - mapIds.size();
                if (relativeLimit > 0 && setWorkingDatabase(Database.LOCAL)) {
                     dao = getQtlDetailsDao();
                    localCount = dao.countMapIdsByQtlName(qtlName);
                    if (localCount > 0) {
                        mapIds.addAll(dao.getMapIdsByQtlName(qtlName, 0, (int) relativeLimit));
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (setWorkingDatabase(Database.LOCAL)) {
                     dao = getQtlDetailsDao();
                    localCount = dao.countMapIdsByQtlName(qtlName);
                    if (localCount > relativeLimit) {
                        mapIds.addAll(dao.getMapIdsByQtlName(qtlName, (int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if (setWorkingDatabase(Database.LOCAL)) {
            QtlDetailsDAO dao = getQtlDetailsDao();
            localCount = dao.countMapIdsByQtlName(qtlName);
            if (localCount > start) {
                mapIds.addAll(dao.getMapIdsByQtlName(qtlName, start, numOfRows));
            }
        }

        return mapIds;
    }

    @Override
    public long countMapIdsByQtlName(String qtlName) throws MiddlewareQueryException {
        long count = 0;
        if (setWorkingDatabase(Database.CENTRAL)) {
            count += getQtlDetailsDao().countMapIdsByQtlName(qtlName);
        }
        if (setWorkingDatabase(Database.LOCAL)) {
            count += getQtlDetailsDao().countMapIdsByQtlName(qtlName);
        }
        return count;
    }

    @Override
    public List<Integer> getMarkerIdsByQtl(String qtlName, String chromosome, int min, int max, int start, int numOfRows)
            throws MiddlewareQueryException {
        //TODO
        return null;

    }

    @Override
    public long countMarkerIdsByQtl(String qtlName, String chromosome, int min, int max) throws MiddlewareQueryException {
        long count = 0;
        if (setWorkingDatabase(Database.CENTRAL)) {
            count += getQtlDetailsDao().countMarkerIdsByQtl(qtlName, chromosome, min, max);
        }
        if (setWorkingDatabase(Database.LOCAL)) {
            count += getQtlDetailsDao().countMarkerIdsByQtl(qtlName, chromosome, min, max);
        }
        return count;
    }

    public List<Marker> getMarkersByIds(List<Integer> markerIds, int start, int numOfRows) throws MiddlewareQueryException {
        List<Marker> markers = new ArrayList<Marker>();
        List<Integer> positiveGids = GenotypicDataManagerUtil.getPositiveIds(markerIds);
        List<Integer> negativeGids = GenotypicDataManagerUtil.getNegativeIds(markerIds);

        if ((setWorkingDatabase(Database.LOCAL)) && (negativeGids != null) && (!negativeGids.isEmpty())) {
            markers.addAll(getMarkerDao().getMarkersByIds(negativeGids, start, numOfRows));
        }
        if ((setWorkingDatabase(Database.CENTRAL)) && (positiveGids != null) && (!positiveGids.isEmpty())) {
            markers.addAll(getMarkerDao().getMarkersByIds(positiveGids, start, numOfRows));
        }
        return markers;
    }

    @Override
    public QtlDetailsPK addQtlDetails(QtlDetails qtlDetails) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        QtlDetailsPK savedId = new QtlDetailsPK();
        try {
            trans = session.beginTransaction();
            
            // No need to auto-assign negative IDs for new local DB records
            // qtlId and mapId are foreign keys

            QtlDetails recordSaved = getQtlDetailsDao().save(qtlDetails);
            savedId = recordSaved.getId();

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while saving Qtl Details: GenotypicDataManager.addQtlDetails(qtlDetails=" + qtlDetails
                    + "): " + e.getMessage(), e, LOG);
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

        Integer savedId = null;
        try {
            trans = session.beginTransaction();

            // No need to auto-assign negative id. It should come from an existing entry in Marker.

            MarkerDetails recordSaved = getMarkerDetailsDao().save(markerDetails);
            savedId = recordSaved.getMarkerId();

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while saving Marker Details: GenotypicDataManager.addMarkerDetails(markerDetails="
                    + markerDetails + "): " + e.getMessage(), e, LOG);
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

        Integer savedId = null;

        try {
            trans = session.beginTransaction();

            // No need to auto-assign negative id. It should come from an existing entry in Marker.

            MarkerUserInfo recordSaved = getMarkerUserInfoDao().save(markerUserInfo);
            savedId = recordSaved.getMarkerId();

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while saving Marker Details: GenotypicDataManager.addMarkerUserInfo(markerUserInfo="
                    + markerUserInfo + "): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }
        return savedId;
    }

    @Override
    public AccMetadataSetPK addAccMetadataSet(AccMetadataSet accMetadataSet) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        AccMetadataSetPK savedId = new AccMetadataSetPK();
        try {
            trans = session.beginTransaction();

            // No need to auto-assign negative IDs for new local DB records
            // datasetId, germplasmId and nameId are foreign keys

            AccMetadataSet recordSaved = getAccMetadataSetDao().save(accMetadataSet);
            savedId = recordSaved.getId();

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered with addAccMetadataSet(accMetadataSet=" + accMetadataSet + "): " + e.getMessage(), e,
                    LOG);
        } finally {
            session.flush();
        }
        return savedId;
    }

    @Override
    public MarkerMetadataSetPK addMarkerMetadataSet(MarkerMetadataSet markerMetadataSet) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        MarkerMetadataSetPK savedId = new MarkerMetadataSetPK();

        try {
            trans = session.beginTransaction();

            // No need to auto-assign negative IDs for new local DB records
            // datasetId and markerId are foreign keys

            MarkerMetadataSet recordSaved = getMarkerMetadataSetDao().save(markerMetadataSet);
            savedId = recordSaved.getId();

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException(
                    "Error encountered with addMarkerMetadataSet(markerMetadataSet=" + markerMetadataSet + "): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }
        return savedId;
    }

    @Override
    public Integer addDataset(Dataset dataset) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        Integer savedId = null;
        try {
            trans = session.beginTransaction();
            DatasetDAO dao = getDatasetDao();

            Integer generatedId = dao.getNegativeId("datasetId");
            dataset.setDatasetId(generatedId);

            Dataset recordSaved = dao.save(dataset);
            savedId = recordSaved.getDatasetId();

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered with addDataset(dataset=" + dataset + "): " + e.getMessage(), e, LOG);
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

        Integer idGDMSMarkerSaved = null;
        try {
            trans = session.beginTransaction();
            MarkerDAO dao = getMarkerDao();

            Integer markerId = dao.getNegativeId("markerId");
            marker.setMarkerId(markerId);

            Marker recordSaved = dao.saveOrUpdate(marker);
            idGDMSMarkerSaved = recordSaved.getMarkerId();

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while saving Marker: addGDMSMarker(): " + e.getMessage(), e, LOG);
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

        Integer idGDMSMarkerAliasSaved = null;
        try {
            trans = session.beginTransaction();

            //Integer markerAliasId = dao.getNegativeId("marker_id");
            //markerAlias.setMarkerId(markerAliasId);

            MarkerAlias recordSaved = getMarkerAliasDao().save(markerAlias);
            idGDMSMarkerAliasSaved = recordSaved.getMarkerId();

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while saving Marker: addGDMSMarkerAlias(): " + e.getMessage(), e, LOG);
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

        Integer idDatasetUserSaved = null;
        try {
            trans = session.beginTransaction();

            DatasetUsers recordSaved = getDatasetUsersDao().save(datasetUser);
            idDatasetUserSaved = recordSaved.getUserId();

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while saving Marker: addDatasetUser(): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }
        return idDatasetUserSaved;
    }

    @Override
    public Integer addAlleleValues(AlleleValues alleleValues) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        Integer savedId = null;

        try {
            trans = session.beginTransaction();
            AlleleValuesDAO dao = getAlleleValuesDao();

            Integer generatedId = dao.getNegativeId("anId");
            alleleValues.setAnId(generatedId);

            AlleleValues recordSaved = dao.save(alleleValues);
            savedId = recordSaved.getAnId();

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered with addAlleleValues(alleleValues=" + alleleValues + "): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }
        return savedId;
    }

    @Override
    public Integer addCharValues(CharValues charValues) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        Integer savedId = null;

        try {
            trans = session.beginTransaction();
            CharValuesDAO dao = getCharValuesDao();

            Integer generatedId = dao.getNegativeId("acId");
            charValues.setAcId(generatedId);

            CharValues recordSaved = dao.save(charValues);
            savedId = recordSaved.getAcId();

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered with addCharValues(charValues=" + charValues + "): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }
        return savedId;
    }

    @Override
    public Integer addMappingPop(MappingPop mappingPop) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        Integer idSaved = null;
        try {
            trans = session.beginTransaction();

            MappingPop recordSaved = getMappingPopDao().save(mappingPop);
            idSaved = recordSaved.getDatasetId();

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while saving Marker: addMappingPop(): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }
        return idSaved;
    }

    @Override
    public Integer addMappingPopValue(MappingPopValues mappingPopValue) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        Integer idSaved = null;
        try {
            trans = session.beginTransaction();
            MappingPopValuesDAO dao = getMappingPopValuesDao();

            Integer mpId = dao.getNegativeId("mpId");
            mappingPopValue.setMpId(mpId);

            MappingPopValues recordSaved = dao.saveOrUpdate(mappingPopValue);
            idSaved = recordSaved.getMpId();

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while saving Marker: addMappingPopValue(): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }
        return idSaved;
    }

    @Override
    public Integer addMarkerOnMap(MarkerOnMap markerOnMap) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        Integer idSaved = null;
        try {
            trans = session.beginTransaction();
            MarkerOnMapDAO dao = getMarkerOnMapDao();

            //Integer mapId = dao.getNegativeId("mapId");
            //mappingPopValue.setMpId(mpId);

            MarkerOnMap recordSaved = dao.save(markerOnMap);
            idSaved = recordSaved.getMapId();

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while saving Marker: addMarkerOnMap(): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }

        return idSaved;
    }

    @Override
    public Integer addDartValue(DartValues dartValue) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        Integer idSaved = null;
        try {
            trans = session.beginTransaction();
            DartValuesDAO dao = getDartValuesDao();

            Integer adId = dao.getNegativeId("adId");
            dartValue.setAdId(adId);

            DartValues recordSaved = dao.save(dartValue);
            idSaved = recordSaved.getAdId();

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while saving Marker: addDartValue(): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }

        return idSaved;
    }

    @Override
    public Integer addQtl(Qtl qtl) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        Integer idSaved = null;
        try {
            trans = session.beginTransaction();
            QtlDAO dao = getQtlDao();

            Integer qtlId = dao.getNegativeId("qtlId");
            qtl.setQtlId(qtlId);

            Qtl recordSaved = dao.saveOrUpdate(qtl);
            idSaved = recordSaved.getQtlId();

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while saving Qtl: addQtl(): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }

        return idSaved;
    }

    @Override
    public Integer addMap(Map map) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        Integer idSaved = null;
        try {
            trans = session.beginTransaction();
            MapDAO dao = getMapDao();

            Integer mapId = dao.getNegativeId("mapId");
            map.setMapId(mapId);

            Map recordSaved = dao.saveOrUpdate(map);
            idSaved = recordSaved.getMapId();

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while saving Map: addMap(): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }

        return idSaved;
    }

    @Override
    public Boolean setSSRMarkers(Marker marker, MarkerAlias markerAlias, MarkerDetails markerDetails, MarkerUserInfo markerUserInfo)
            throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        Boolean transactionStatus = true;
        try {
            trans = session.beginTransaction();

            // Add GDMS Marker
            MarkerDAO markerDao = getMarkerDao();

            Integer markerId = markerDao.getNegativeId("markerId");
            marker.setMarkerId(markerId);
            
            marker.setMarkerType("SSR");

            Marker markerRecordSaved = markerDao.saveOrUpdate(marker);
            Integer idGDMSMarkerSaved = markerRecordSaved.getMarkerId();
            if (idGDMSMarkerSaved == null)
                transactionStatus = false;

            // Add GDMS Marker Alias
            MarkerAliasDAO markerAliasDao = getMarkerAliasDao();
            markerAlias.setMarkerId(idGDMSMarkerSaved);

            MarkerAlias markerAliasRecordSaved = markerAliasDao.saveOrUpdate(markerAlias);
            Integer markerAliasRecordSavedMarkerId = markerAliasRecordSaved.getMarkerId();
            if (markerAliasRecordSavedMarkerId == null)
                transactionStatus = false;

            // Add Marker Details
            MarkerDetailsDAO markerDetailsDao = getMarkerDetailsDao();
            markerDetails.setMarkerId(idGDMSMarkerSaved);

            MarkerDetails markerDetailsRecordSaved = markerDetailsDao.saveOrUpdate(markerDetails);
            Integer markerDetailsSavedMarkerId = markerDetailsRecordSaved.getMarkerId();
            if (markerDetailsSavedMarkerId == null)
                transactionStatus = false;

            // Add marker user info
            MarkerUserInfoDAO dao = getMarkerUserInfoDao();
            markerUserInfo.setMarkerId(idGDMSMarkerSaved);

            MarkerUserInfo markerUserInfoRecordSaved = dao.saveOrUpdate(markerUserInfo);
            Integer markerUserInfoSavedId = markerUserInfoRecordSaved.getMarkerId();
            if (markerUserInfoSavedId == null)
                transactionStatus = false;

            if (transactionStatus == true) {
                trans.commit();
            } else {
                rollbackTransaction(trans);
            }
        } catch (Exception e) {
            transactionStatus = false;
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while saving Marker: setSSRMarkers(): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }

        return transactionStatus;
    }

    @Override
    public Boolean setSNPMarkers(Marker marker, MarkerAlias markerAlias, MarkerDetails markerDetails, MarkerUserInfo markerUserInfo)
            throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;
        
        Boolean transactionStatus = true;
        try {
            trans = session.beginTransaction();

            // Add GDMS Marker
            MarkerDAO markerDao = getMarkerDao();

            Integer markerId = markerDao.getNegativeId("markerId");
            marker.setMarkerId(markerId);

            marker.setMarkerType("SNP");

            Marker markerRecordSaved = markerDao.saveOrUpdate(marker);
            Integer idGDMSMarkerSaved = markerRecordSaved.getMarkerId();
            if (idGDMSMarkerSaved == null)
                transactionStatus = false;

            // Add GDMS Marker Alias
            MarkerAliasDAO markerAliasDao = getMarkerAliasDao();
            markerAlias.setMarkerId(idGDMSMarkerSaved);

            MarkerAlias markerAliasRecordSaved = markerAliasDao.save(markerAlias);
            Integer markerAliasRecordSavedMarkerId = markerAliasRecordSaved.getMarkerId();
            if (markerAliasRecordSavedMarkerId == null)
                transactionStatus = false;

            // Add Marker Details
            MarkerDetailsDAO markerDetailsDao = getMarkerDetailsDao();
            markerDetails.setMarkerId(idGDMSMarkerSaved);

            MarkerDetails markerDetailsRecordSaved = markerDetailsDao.save(markerDetails);
            Integer markerDetailsSavedMarkerId = markerDetailsRecordSaved.getMarkerId();
            if (markerDetailsSavedMarkerId == null)
                transactionStatus = false;

            // Add marker user info
            MarkerUserInfoDAO dao = getMarkerUserInfoDao();
            markerUserInfo.setMarkerId(idGDMSMarkerSaved);

            MarkerUserInfo markerUserInfoRecordSaved = dao.save(markerUserInfo);
            Integer markerUserInfoSavedId = markerUserInfoRecordSaved.getMarkerId();
            if (markerUserInfoSavedId == null)
                transactionStatus = false;

            if (transactionStatus == true) {
                trans.commit();
            } else {
                trans.rollback();
            }
        } catch (Exception e) {
            transactionStatus = false;
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while saving Marker: setSNPMarkers(): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }
        return transactionStatus;
    }

    @Override
    public Boolean setCAPMarkers(Marker marker, MarkerAlias markerAlias, MarkerDetails markerDetails, MarkerUserInfo markerUserInfo)
            throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;
        
        Boolean transactionStatus = true;
        try {
            trans = session.beginTransaction();

            // Add GDMS Marker
            MarkerDAO markerDao = getMarkerDao();

            Integer markerId = markerDao.getNegativeId("markerId");
            marker.setMarkerId(markerId);

            marker.setMarkerType("CAP");

            Marker markerRecordSaved = markerDao.saveOrUpdate(marker);
            Integer idGDMSMarkerSaved = markerRecordSaved.getMarkerId();
            if (idGDMSMarkerSaved == null)
                transactionStatus = false;

            // Add GDMS Marker Alias
            MarkerAliasDAO markerAliasDao = getMarkerAliasDao();
            markerAlias.setMarkerId(idGDMSMarkerSaved);

            MarkerAlias markerAliasRecordSaved = markerAliasDao.save(markerAlias);
            Integer markerAliasRecordSavedMarkerId = markerAliasRecordSaved.getMarkerId();
            if (markerAliasRecordSavedMarkerId == null)
                transactionStatus = false;

            // Add Marker Details
            MarkerDetailsDAO markerDetailsDao = getMarkerDetailsDao();
            markerDetails.setMarkerId(idGDMSMarkerSaved);

            MarkerDetails markerDetailsRecordSaved = markerDetailsDao.save(markerDetails);
            Integer markerDetailsSavedMarkerId = markerDetailsRecordSaved.getMarkerId();
            if (markerDetailsSavedMarkerId == null)
                transactionStatus = false;

            // Add marker user info
            MarkerUserInfoDAO dao = getMarkerUserInfoDao();
            markerUserInfo.setMarkerId(idGDMSMarkerSaved);

            MarkerUserInfo markerUserInfoRecordSaved = dao.save(markerUserInfo);
            Integer markerUserInfoSavedId = markerUserInfoRecordSaved.getMarkerId();
            if (markerUserInfoSavedId == null)
                transactionStatus = false;

            if (transactionStatus == true) {
                trans.commit();
            } else {
                trans.rollback();
            }
        } catch (Exception e) {
            transactionStatus = false;
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while saving Marker: setSNPMarkers(): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }

        return transactionStatus;
    }

    @Override
    public Boolean setCISRMarkers(Marker marker, MarkerAlias markerAlias, MarkerDetails markerDetails, MarkerUserInfo markerUserInfo)
            throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;
        
        Boolean transactionStatus = true;
        try {
            // begin save transaction
            trans = session.beginTransaction();

            // Add GDMS Marker
            MarkerDAO markerDao = getMarkerDao();

            Integer markerId = markerDao.getNegativeId("markerId");
            marker.setMarkerId(markerId);

            marker.setMarkerType("CISR");

            Marker markerRecordSaved = markerDao.saveOrUpdate(marker);
            Integer idGDMSMarkerSaved = markerRecordSaved.getMarkerId();
            if (idGDMSMarkerSaved == null)
                transactionStatus = false;

            // Add GDMS Marker Alias
            MarkerAliasDAO markerAliasDao = getMarkerAliasDao();
            markerAlias.setMarkerId(idGDMSMarkerSaved);

            MarkerAlias markerAliasRecordSaved = markerAliasDao.save(markerAlias);
            Integer markerAliasRecordSavedMarkerId = markerAliasRecordSaved.getMarkerId();
            if (markerAliasRecordSavedMarkerId == null)
                transactionStatus = false;

            // Add Marker Details
            MarkerDetailsDAO markerDetailsDao = getMarkerDetailsDao();
            markerDetails.setMarkerId(idGDMSMarkerSaved);

            MarkerDetails markerDetailsRecordSaved = markerDetailsDao.save(markerDetails);
            Integer markerDetailsSavedMarkerId = markerDetailsRecordSaved.getMarkerId();
            if (markerDetailsSavedMarkerId == null)
                transactionStatus = false;

            // Add marker user info

            MarkerUserInfoDAO dao = getMarkerUserInfoDao();
            markerUserInfo.setMarkerId(idGDMSMarkerSaved);

            MarkerUserInfo markerUserInfoRecordSaved = dao.save(markerUserInfo);
            Integer markerUserInfoSavedId = markerUserInfoRecordSaved.getMarkerId();
            if (markerUserInfoSavedId == null)
                transactionStatus = false;

            if (transactionStatus == true) {
                trans.commit();
            } else {
                trans.rollback();
            }
        } catch (Exception e) {
            transactionStatus = false;
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while saving Marker: setSNPMarkers(): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }

        return transactionStatus;
    }

    @Override
    public Boolean setQTL(DatasetUsers datasetUser, Dataset dataset, QtlDetails qtlDetails, Qtl qtl) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;
        
        Boolean transactionStatus = true;
        try {
            // begin save transaction
            trans = session.beginTransaction();

            // Add Dataset
            DatasetDAO datasetDao = getDatasetDao();

            Integer generatedId = datasetDao.getNegativeId("datasetId");
            dataset.setDatasetId(generatedId);

            dataset.setDatasetType("QTL");

            Dataset datasetRecordSaved = datasetDao.saveOrUpdate(dataset);
            Integer datasetSavedId = datasetRecordSaved.getDatasetId();

            if (datasetSavedId == null)
                transactionStatus = false;

            // Add Dataset User
            DatasetUsersDAO datasetUserDao = getDatasetUsersDao();
            datasetUser.setDatasetId(datasetSavedId);

            DatasetUsers recordSaved = datasetUserDao.saveOrUpdate(datasetUser);
            Integer idDatasetUserSaved = recordSaved.getUserId();

            if (idDatasetUserSaved == null)
                transactionStatus = false;

            // Add Qtl            
            QtlDAO qtlDao = getQtlDao();

            Integer qtlId = qtlDao.getNegativeId("qtlId");
            qtl.setQtlId(qtlId);
            qtl.setDatasetId(datasetSavedId);

            Qtl qtlRecordSaved = qtlDao.saveOrUpdate(qtl);
            Integer qtlIdSaved = qtlRecordSaved.getQtlId();

            if (qtlIdSaved == null)
                transactionStatus = false;

            // Add QtlDetails
            QtlDetailsDAO qtlDetailsDao = getQtlDetailsDao();

            QtlDetailsPK qtlDetailsPK = new QtlDetailsPK(qtlIdSaved, qtlDetails.getId().getMapId());

            qtlDetails.setQtlId(qtlDetailsPK);

            QtlDetails qtlDetailsRecordSaved = qtlDetailsDao.saveOrUpdate(qtlDetails);
            QtlDetailsPK qtlDetailsSavedId = qtlDetailsRecordSaved.getId();

            if (qtlDetailsSavedId == null)
                transactionStatus = false;

            if (transactionStatus == true) {
                trans.commit();
            } else {
                trans.rollback();
            }
        } catch (Exception e) {
            transactionStatus = false;
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while saving Marker: setQTL(): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }

        return transactionStatus;
    }

    public Boolean setDart(AccMetadataSet accMetadataSet, MarkerMetadataSet markerMetadataSet, DatasetUsers datasetUser,
            AlleleValues alleleValues, Dataset dataset, DartValues dartValues) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;
        
        Boolean transactionStatus = true;
        try {
            // Begin save transaction
            trans = session.beginTransaction();

            // Add Dataset
            DatasetDAO datasetDao = getDatasetDao();

            Integer datasetGeneratedId = datasetDao.getNegativeId("datasetId");
            dataset.setDatasetId(datasetGeneratedId);

            dataset.setDatasetType("DArT");
            dataset.setDataType("int");

            Dataset datasetRecordSaved = datasetDao.saveOrUpdate(dataset);
            Integer datasetId = datasetRecordSaved.getDatasetId();

            if (datasetId == null) {
                throw new Exception(); // To immediately roll back and to avoid executing the other insert functions
            }

            // Add AccMetadataSet
            AccMetadataSetDAO accMetadataSetDao = getAccMetadataSetDao();

            accMetadataSet.setDatasetId(datasetId);

            // No need to generate id, AccMetadataSetPK(datasetId, gId, nId) are foreign keys

            AccMetadataSet accMetadataSetRecordSaved = accMetadataSetDao.saveOrUpdate(accMetadataSet);
            AccMetadataSetPK accMetadatasetSavedId = accMetadataSetRecordSaved.getId();

            if (accMetadatasetSavedId == null) {
                throw new Exception();
            }

            // Add MarkerMetadataSet
            MarkerMetadataSetDAO markerMetadataSetDao = getMarkerMetadataSetDao();

            markerMetadataSet.setDatasetId(datasetId);

            // No need to generate id, MarkerMetadataSetPK(datasetId, markerId) are foreign keys

            MarkerMetadataSet markerMetadataSetRecordSaved = markerMetadataSetDao.saveOrUpdate(markerMetadataSet);
            MarkerMetadataSetPK markerMetadataSetSavedId = markerMetadataSetRecordSaved.getId();

            if (markerMetadataSetSavedId == null) {
                throw new Exception();
            }

            // Add DatasetUser
            DatasetUsersDAO datasetUserDao = getDatasetUsersDao();
            
            datasetUser.setDatasetId(datasetId);

            DatasetUsers datasetUserSaved = datasetUserDao.saveOrUpdate(datasetUser);
            Integer datasetUserSavedId = datasetUserSaved.getUserId();

            if (datasetUserSavedId == null) {
                throw new Exception();
            }

            // Add AlleleValues
            AlleleValuesDAO alleleValuesDao = getAlleleValuesDao();
            
            alleleValues.setDatasetId(datasetId);

            Integer alleleValuesGeneratedId = alleleValuesDao.getNegativeId("anId");
            alleleValues.setAnId(alleleValuesGeneratedId);

            AlleleValues alleleValuesRecordSaved = alleleValuesDao.save(alleleValues);
            Integer alleleValuesSavedId = alleleValuesRecordSaved.getAnId();

            if (alleleValuesSavedId == null) {
                throw new Exception();
            }

            // Add DArT Values

            DartValuesDAO dartValuesDao = getDartValuesDao();
            
            dartValues.setDatasetId(datasetId);

            Integer adId = dartValuesDao.getNegativeId("adId");
            dartValues.setAdId(adId);

            DartValues dartValuesRecordSaved = dartValuesDao.save(dartValues);
            Integer dartValuesSavedId = dartValuesRecordSaved.getAdId();

            if (dartValuesSavedId == null) {
                //transactionStatus = false;
                throw new Exception();
            }

            if (transactionStatus == true) {
                trans.commit();
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            transactionStatus = false;
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while setting DArT: setDArT(): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }

        return transactionStatus;
    }

    @Override
    public Boolean setSSR(AccMetadataSet accMetadataSet, MarkerMetadataSet markerMetadataSet, DatasetUsers datasetUser,
            AlleleValues alleleValues, Dataset dataset) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;
        
        Boolean transactionStatus = true;
        try {
            // Begin save transaction
            trans = session.beginTransaction();

            // Add Dataset
            DatasetDAO datasetDao = getDatasetDao();

            Integer datasetGeneratedId = datasetDao.getNegativeId("datasetId");
            dataset.setDatasetId(datasetGeneratedId);

            dataset.setDatasetType("SSR");
            dataset.setDataType("int");

            Dataset datasetRecordSaved = datasetDao.saveOrUpdate(dataset);
            Integer datasetId = datasetRecordSaved.getDatasetId();

            if (datasetId == null) {
                throw new Exception(); // To immediately roll back and to avoid executing the other insert functions
            }

            // Add AccMetadataSet
            AccMetadataSetDAO accMetadataSetDao = getAccMetadataSetDao();

            accMetadataSet.setDatasetId(datasetId);

            // No need to generate id, AccMetadataSetPK(datasetId, gId, nId) are foreign keys

            AccMetadataSet accMetadataSetRecordSaved = accMetadataSetDao.saveOrUpdate(accMetadataSet);
            AccMetadataSetPK accMetadatasetSavedId = accMetadataSetRecordSaved.getId();

            if (accMetadatasetSavedId == null) {
                throw new Exception();
            }

            // Add MarkerMetadataSet
            MarkerMetadataSetDAO markerMetadataSetDao = getMarkerMetadataSetDao();

            markerMetadataSet.setDatasetId(datasetId);

            // No need to generate id, MarkerMetadataSetPK(datasetId, markerId) are foreign keys

            MarkerMetadataSet markerMetadataSetRecordSaved = markerMetadataSetDao.saveOrUpdate(markerMetadataSet);
            MarkerMetadataSetPK markerMetadataSetSavedId = markerMetadataSetRecordSaved.getId();

            if (markerMetadataSetSavedId == null) {
                throw new Exception();
            }

            // Add DatasetUser

            DatasetUsersDAO datasetUserDao = getDatasetUsersDao();

            datasetUser.setDatasetId(datasetId);

            DatasetUsers datasetUserSaved = datasetUserDao.saveOrUpdate(datasetUser);
            Integer datasetUserSavedId = datasetUserSaved.getUserId();

            if (datasetUserSavedId == null) {
                throw new Exception();
            }

            // Add AlleleValues
            AlleleValuesDAO alleleValuesDao = getAlleleValuesDao();

            alleleValues.setDatasetId(datasetId);

            Integer alleleValuesGeneratedId = alleleValuesDao.getNegativeId("anId");
            alleleValues.setAnId(alleleValuesGeneratedId);

            AlleleValues alleleValuesRecordSaved = alleleValuesDao.save(alleleValues);
            Integer alleleValuesSavedId = alleleValuesRecordSaved.getAnId();

            if (alleleValuesSavedId == null) {
                throw new Exception();
            }

            if (transactionStatus == true) {
                trans.commit();
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            transactionStatus = false;
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while setting SSR: setSSR(): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }
        return transactionStatus;
    }

    @Override
    public Boolean setSNP(AccMetadataSet accMetadataSet, MarkerMetadataSet markerMetadataSet, DatasetUsers datasetUser,
            CharValues charValues, Dataset dataset) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;
        
        Boolean transactionStatus = true;
        try {
            // Begin save transaction
            trans = session.beginTransaction();

            // Add Dataset
            DatasetDAO datasetDao = getDatasetDao();

            Integer datasetGeneratedId = datasetDao.getNegativeId("datasetId");
            dataset.setDatasetId(datasetGeneratedId);

            dataset.setDatasetType("SNP");
            dataset.setDataType("int");

            Dataset datasetRecordSaved = datasetDao.saveOrUpdate(dataset);
            Integer datasetId = datasetRecordSaved.getDatasetId();

            if (datasetId == null) {
                throw new Exception(); // To immediately roll back and to avoid executing the other insert functions
            }

            // Add AccMetadataSet
            AccMetadataSetDAO accMetadataSetDao = getAccMetadataSetDao();

            accMetadataSet.setDatasetId(datasetId);

            // No need to generate id, AccMetadataSetPK(datasetId, gId, nId) are foreign keys

            AccMetadataSet accMetadataSetRecordSaved = accMetadataSetDao.saveOrUpdate(accMetadataSet);
            AccMetadataSetPK accMetadatasetSavedId = accMetadataSetRecordSaved.getId();

            if (accMetadatasetSavedId == null) {
                throw new Exception();
            }

            // Add MarkerMetadataSet
            MarkerMetadataSetDAO markerMetadataSetDao = getMarkerMetadataSetDao();

            markerMetadataSet.setDatasetId(datasetId);

            // No need to generate id, MarkerMetadataSetPK(datasetId, markerId) are foreign keys

            MarkerMetadataSet markerMetadataSetRecordSaved = markerMetadataSetDao.saveOrUpdate(markerMetadataSet);
            MarkerMetadataSetPK markerMetadataSetSavedId = markerMetadataSetRecordSaved.getId();

            if (markerMetadataSetSavedId == null) {
                throw new Exception();
            }

            // Add DatasetUser
            DatasetUsersDAO datasetUserDao = getDatasetUsersDao();

            datasetUser.setDatasetId(datasetId);

            DatasetUsers datasetUserSaved = datasetUserDao.saveOrUpdate(datasetUser);
            Integer datasetUserSavedId = datasetUserSaved.getUserId();

            if (datasetUserSavedId == null) {
                throw new Exception();
            }

            //Add CharValues
            CharValuesDAO charValuesDao = getCharValuesDao();

            Integer generatedId = charValuesDao.getNegativeId("acId");
            charValues.setAcId(generatedId);
            charValues.setDatasetId(datasetId);

            CharValues charValuesRecordSaved = charValuesDao.saveOrUpdate(charValues);
            Integer charValuesSavedId = charValuesRecordSaved.getAcId();

            if (charValuesSavedId == null) {
                throw new Exception();
            }

            if (transactionStatus == true) {
                trans.commit();
            } else {
                throw new Exception();
            }

        } catch (Exception e) {
            transactionStatus = false;
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while setting SNP: setSNP(): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }

        return transactionStatus;
    }

    @Override
    public Boolean setMappingData(AccMetadataSet accMetadataSet, MarkerMetadataSet markerMetadataSet, DatasetUsers datasetUser,
            MappingPop mappingPop, MappingPopValues mappingPopValues, Dataset dataset) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;
        
        Boolean transactionStatus = true;
        try {
            // Begin save transaction
            trans = session.beginTransaction();

            // Add Dataset
            DatasetDAO datasetDao = getDatasetDao();

            Integer datasetGeneratedId = datasetDao.getNegativeId("datasetId");
            dataset.setDatasetId(datasetGeneratedId);

            dataset.setDatasetType("mapping");
            dataset.setDataType("map");

            Dataset datasetRecordSaved = datasetDao.saveOrUpdate(dataset);
            Integer datasetId = datasetRecordSaved.getDatasetId();

            if (datasetId == null) {
                throw new Exception(); // To immediately roll back and to avoid executing the other insert functions
            }

            // Add AccMetadataSet
            AccMetadataSetDAO accMetadataSetDao = getAccMetadataSetDao();
            accMetadataSet.setDatasetId(datasetId);

            // No need to generate id, AccMetadataSetPK(datasetId, gId, nId) are foreign keys
            AccMetadataSet accMetadataSetRecordSaved = accMetadataSetDao.saveOrUpdate(accMetadataSet);
            AccMetadataSetPK accMetadatasetSavedId = accMetadataSetRecordSaved.getId();

            if (accMetadatasetSavedId == null) {
                throw new Exception();
            }

            // Add MarkerMetadataSet
            MarkerMetadataSetDAO markerMetadataSetDao = getMarkerMetadataSetDao();
            markerMetadataSet.setDatasetId(datasetId);

            // No need to generate id, MarkerMetadataSetPK(datasetId, markerId) are foreign keys
            MarkerMetadataSet markerMetadataSetRecordSaved = markerMetadataSetDao.saveOrUpdate(markerMetadataSet);
            MarkerMetadataSetPK markerMetadataSetSavedId = markerMetadataSetRecordSaved.getId();

            if (markerMetadataSetSavedId == null) {
                throw new Exception();
            }

            // Add DatasetUser
            DatasetUsersDAO datasetUserDao = getDatasetUsersDao();
            datasetUser.setDatasetId(datasetId);

            DatasetUsers datasetUserSaved = datasetUserDao.saveOrUpdate(datasetUser);
            Integer datasetUserSavedId = datasetUserSaved.getUserId();

            if (datasetUserSavedId == null) {
                throw new Exception();
            }

            // Add Mapping Population
            MappingPopDAO mappingPopDao = getMappingPopDao();
            mappingPop.setDatasetId(datasetId);

            // Mapping Pop has DatasetID as PK in Hibernate, but not in SQL
            // Integer mappingPopGeneratedId = mappingPopDao.getNegativeId("datasetId");
            // mappingPop.setDatasetIdId(mappingPopGeneratedId);

            MappingPop mappingPopRecordSaved = mappingPopDao.save(mappingPop);
            Integer mappingPopSavedId = mappingPopRecordSaved.getDatasetId();

            if (mappingPopSavedId == null) {
                throw new Exception();
            }

            // Add Mapping Population Values
            MappingPopValuesDAO mappingPopValuesDao = getMappingPopValuesDao();
            mappingPopValues.setDatasetId(datasetId);

            Integer mpId = mappingPopValuesDao.getNegativeId("mpId");
            mappingPopValues.setMpId(mpId);

            MappingPopValues mappingPopValuesRecordSaved = mappingPopValuesDao.save(mappingPopValues);
            Integer mappingPopValuesSavedId = mappingPopValuesRecordSaved.getMpId();

            if (mappingPopValuesSavedId == null) {
                throw new Exception();
            }

            if (transactionStatus == true) {
                trans.commit();
            } else {
                throw new Exception();
            }

        } catch (Exception e) {
            transactionStatus = false;
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while setting Mapping Data: setMappingData(): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }

        return transactionStatus;
    }

    @Override
    public Boolean setMaps(Marker marker, MarkerOnMap markerOnMap, Map map) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;
        
        Boolean transactionStatus = true;
        try {
            // Begin save transaction
            trans = session.beginTransaction();

            // Add GDMS Marker
            MarkerDAO markerDao = getMarkerDao();

            Integer markerGeneratedId = markerDao.getNegativeId("markerId");
            marker.setMarkerId(markerGeneratedId);

            marker.setMarkerType("UA");

            Marker markerRecordSaved = markerDao.saveOrUpdate(marker);
            Integer markerSavedId = markerRecordSaved.getMarkerId();

            if (markerSavedId == null) {
                throw new Exception(); // To immediately roll back and to avoid executing the other insert functions
            }

            // Add Map
            MapDAO mapDao = getMapDao();

            Integer mapGeneratedId = mapDao.getNegativeId("mapId");
            map.setMapId(mapGeneratedId);

            Map mapRecordSaved = mapDao.saveOrUpdate(map);
            Integer mapSavedId = mapRecordSaved.getMapId();

            if (mapSavedId == null) {
                throw new Exception(); // To immediately roll back and to avoid executing the other insert functions
            }

            // Add Marker on Map
            MarkerOnMapDAO markerOnMapDao = getMarkerOnMapDao();

            // No need to generate id, MarkerOnMap(markerId, mapId) are foreign keys
            markerOnMap.setMarkerId(markerSavedId);
            markerOnMap.setMapId(mapSavedId);

            MarkerOnMap markerOnMapRecordSaved = markerOnMapDao.saveOrUpdate(markerOnMap);
            Integer markerOnMapSavedId = markerOnMapRecordSaved.getMapId();

            if (markerOnMapSavedId == null) {
                throw new Exception();
            }

            if (transactionStatus == true) {
                trans.commit();
            } else {
                throw new Exception();
            }

        } catch (Exception e) {
            transactionStatus = false;
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while setting Maps: setMaps(): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }

        return transactionStatus;
    }
}