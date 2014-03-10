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
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.dao.gdms.AccMetadataSetDAO;
import org.generationcp.middleware.dao.gdms.AlleleValuesDAO;
import org.generationcp.middleware.dao.gdms.CharValuesDAO;
import org.generationcp.middleware.dao.gdms.DartValuesDAO;
import org.generationcp.middleware.dao.gdms.DatasetDAO;
import org.generationcp.middleware.dao.gdms.DatasetUsersDAO;
import org.generationcp.middleware.dao.gdms.MapDAO;
import org.generationcp.middleware.dao.gdms.MappingPopDAO;
import org.generationcp.middleware.dao.gdms.MappingPopValuesDAO;
import org.generationcp.middleware.dao.gdms.MarkerAliasDAO;
import org.generationcp.middleware.dao.gdms.MarkerDAO;
import org.generationcp.middleware.dao.gdms.MarkerDetailsDAO;
import org.generationcp.middleware.dao.gdms.MarkerMetadataSetDAO;
import org.generationcp.middleware.dao.gdms.MarkerOnMapDAO;
import org.generationcp.middleware.dao.gdms.MarkerUserInfoDAO;
import org.generationcp.middleware.dao.gdms.QtlDAO;
import org.generationcp.middleware.dao.gdms.QtlDetailsDAO;
import org.generationcp.middleware.exceptions.MiddlewareException;
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
import org.generationcp.middleware.pojos.gdms.DartDataRow;
import org.generationcp.middleware.pojos.gdms.DartValues;
import org.generationcp.middleware.pojos.gdms.Dataset;
import org.generationcp.middleware.pojos.gdms.DatasetElement;
import org.generationcp.middleware.pojos.gdms.DatasetUsers;
import org.generationcp.middleware.pojos.gdms.GermplasmMarkerElement;
import org.generationcp.middleware.pojos.gdms.Map;
import org.generationcp.middleware.pojos.gdms.MapDetailElement;
import org.generationcp.middleware.pojos.gdms.MapInfo;
import org.generationcp.middleware.pojos.gdms.MappingABHRow;
import org.generationcp.middleware.pojos.gdms.MappingAllelicSNPRow;
import org.generationcp.middleware.pojos.gdms.MappingAllelicSSRDArTRow;
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
import org.generationcp.middleware.pojos.gdms.Mta;
import org.generationcp.middleware.pojos.gdms.ParentElement;
import org.generationcp.middleware.pojos.gdms.Qtl;
import org.generationcp.middleware.pojos.gdms.QtlDataElement;
import org.generationcp.middleware.pojos.gdms.QtlDataRow;
import org.generationcp.middleware.pojos.gdms.QtlDetailElement;
import org.generationcp.middleware.pojos.gdms.QtlDetails;
import org.generationcp.middleware.pojos.gdms.QtlDetailsPK;
import org.generationcp.middleware.pojos.gdms.SNPDataRow;
import org.generationcp.middleware.pojos.gdms.SSRDataRow;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the GenotypicDataManager interface.  To instantiate this
 * class, a Hibernate Session must be passed to its constructor.
 * 
 * @author Joyce Avestro
 */
@SuppressWarnings("unchecked")
public class GenotypicDataManagerImpl extends DataManager implements GenotypicDataManager{

    private static final Logger LOG = LoggerFactory.getLogger(GenotypicDataManagerImpl.class);
    
    private static final String TYPE_SSR = "SSR";
    private static final String TYPE_SNP = "SNP";
    private static final String TYPE_DART = "DArT";
    private static final String TYPE_MAPPING = "mapping";
    private static final String TYPE_MTA = "MTA";
    private static final String TYPE_QTL = "QTL";
    private static final String TYPE_CAP = "CAP";
    private static final String TYPE_CISR = "CISR";
    private static final String TYPE_UA = "UA"; // Unassigned
    
    private static final String DATA_TYPE_INT = "int";
    private static final String DATA_TYPE_MAP = "map";
    
    public GenotypicDataManagerImpl() {
    }

    public GenotypicDataManagerImpl(HibernateSessionProvider sessionProviderForLocal, HibernateSessionProvider sessionProviderForCentral) {
        super(sessionProviderForLocal, sessionProviderForCentral);
    }

    public GenotypicDataManagerImpl(Session sessionForLocal, Session sessionForCentral) {
        super(sessionForLocal, sessionForCentral);
    }

    @Override
    public List<Integer> getMapIDsByQTLName(String qtlName, int start, int numOfRows) throws MiddlewareQueryException {
        if ((qtlName == null) || (qtlName.isEmpty())) {
            return new ArrayList<Integer>();
        }

        List<String> methods = Arrays.asList("countMapIDsByQTLName", "getMapIDsByQTLName");
        return (List<Integer>) super.getFromCentralAndLocalByMethod(getQtlDao(), methods, start, numOfRows, new Object[] { qtlName },
                new Class[] { String.class });
    }

    @Override
    public long countMapIDsByQTLName(String qtlName) throws MiddlewareQueryException {
        return super.countAllFromCentralAndLocalByMethod(getQtlDao(), "countMapIDsByQTLName", new Object[] { qtlName },
                new Class[] { String.class });
    }

    @Override
    public List<Integer> getNameIdsByGermplasmIds(List<Integer> gIds) throws MiddlewareQueryException {
        return (List<Integer>) super.getFromInstanceByIdAndMethod(getAccMetadataSetDao(), gIds.get(0), "getNameIdsByGermplasmIds",
                new Object[] { gIds }, new Class[] { List.class });
    }

    @Override
    public List<Name> getNamesByNameIds(List<Integer> nIds) throws MiddlewareQueryException {
        return (List<Name>) super.getFromInstanceByIdAndMethod(getNameDao(), nIds.get(0), "getNamesByNameIds", new Object[] { nIds },
                new Class[] { List.class });
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
        return super.countFromInstance(getMapDao(), instance);
    }

    @Override
    public List<Map> getAllMaps(int start, int numOfRows, Database instance) throws MiddlewareQueryException {
        return (List<Map>) super.getFromInstanceByMethod(getMapDao(), instance, "getAll", new Object[] { start, numOfRows }, new Class[] {
                Integer.TYPE, Integer.TYPE });
    }

    @Override
    public List<MapInfo> getMapInfoByMapName(String mapName, Database instance) throws MiddlewareQueryException {
        List<MapInfo> mapInfoList = new ArrayList<MapInfo>();
        setWorkingDatabase(instance);

        // Step 1: Get map id by map name
        Map map = getMapDao().getByName(mapName);
        if (map == null){
            return new ArrayList<MapInfo>();
        }
        
        // Step 2: Get markerId, linkageGroup, startPosition from gdms_markers_onmap
        List<MarkerOnMap> markersOnMap = getMarkerOnMapDao().getMarkersOnMapByMapId(map.getMapId());

        // Step 3: Get marker name from gdms_marker and build MapInfo
        for (MarkerOnMap markerOnMap : markersOnMap){
            setWorkingDatabase(instance);
            Integer markerId = markerOnMap.getMarkerId();
            String markerName = getMarkerNameByMarkerId(markerId);
            MapInfo mapInfo = new MapInfo(markerId, markerName, markerOnMap.getMapId(), map.getMapName(), 
                    markerOnMap.getLinkageGroup(), markerOnMap.getStartPosition(),
                    map.getMapType(), map.getMapUnit());
            mapInfoList.add(mapInfo);
        }
        
        Collections.sort(mapInfoList);
        return mapInfoList;
        
    }

    @Override
    public List<MapInfo> getMapInfoByMapName(String mapName) throws MiddlewareQueryException {
        List<MapInfo> mapInfoList = getMapInfoByMapName(mapName, Database.CENTRAL); 
        mapInfoList.addAll(getMapInfoByMapName(mapName, Database.LOCAL));
        Collections.sort(mapInfoList);
        return mapInfoList;
    }

    @Override
    public long countDatasetNames(Database instance) throws MiddlewareQueryException {
        return super.countFromInstanceByMethod(getDatasetDao(), instance, "countByName", new Object[] {}, new Class[] {});
    }

    @Override
    public List<String> getDatasetNames(int start, int numOfRows, Database instance) throws MiddlewareQueryException {
        return (List<String>) super.getFromInstanceByMethod(getDatasetDao(), instance, "getDatasetNames",
                new Object[] { start, numOfRows }, new Class[] { Integer.TYPE, Integer.TYPE });
    }

    @Override
    public List<String> getDatasetNamesByQtlId(Integer qtlId, int start, int numOfRows) throws MiddlewareQueryException {
        List<String> methods = Arrays.asList("countDatasetNamesByQtlId", "getDatasetNamesByQtlId");
        return (List<String>) super.getFromCentralAndLocalByMethod(getDatasetDao(), methods, start, numOfRows,
        		new Object[] { qtlId }, new Class[] { Integer.class });
    }

    @Override
    public long countDatasetNamesByQtlId(Integer qtlId) throws MiddlewareQueryException {
        return super.countAllFromCentralAndLocalByMethod(getDatasetDao(), "countDatasetNamesByQtlId", 
        		new Object[] { qtlId }, new Class[] { Integer.class });
    }

    @Override
    public List<DatasetElement> getDatasetDetailsByDatasetName(String datasetName, Database instance) throws MiddlewareQueryException {
        return (List<DatasetElement>) super.getFromInstanceByMethod(getDatasetDao(), instance, "getDetailsByName",
                new Object[] { datasetName }, new Class[] { String.class });
    }

    @Override
    public List<Integer> getMarkerIdsByMarkerNames(List<String> markerNames, int start, int numOfRows, Database instance)
            throws MiddlewareQueryException {
        return (List<Integer>) super.getFromInstanceByMethod(getMarkerDao(), instance, "getIdsByNames", new Object[] { markerNames, start, numOfRows },
                new Class[] { List.class, Integer.TYPE, Integer.TYPE });
    }

    @Override
    public Set<Integer> getMarkerIDsByMapIDAndLinkageBetweenStartPosition(int mapId, String linkageGroup, double startPos, double endPos,
            int start, int numOfRows) throws MiddlewareQueryException {
        if (setWorkingDatabase(mapId)) {
            return getMarkerDao()
                    .getMarkerIDsByMapIDAndLinkageBetweenStartPosition(mapId, linkageGroup, startPos, endPos, start, numOfRows);
        }
        return new TreeSet<Integer>();
    }

    @Override
    public long countMarkerIDsByMapIDAndLinkageBetweenStartPosition(int mapId, String linkageGroup, double startPos, double endPos)
            throws MiddlewareQueryException {
        return super.countFromInstanceByIdAndMethod(getMarkerDao(), mapId, "countMarkerIDsByMapIDAndLinkageBetweenStartPosition", 
                new Object[]{mapId, linkageGroup, startPos, endPos}, new Class[]{Integer.TYPE, String.class, Double.TYPE, Double.TYPE});
    }

    @Override
    public List<Integer> getMarkerIdsByDatasetId(Integer datasetId) throws MiddlewareQueryException {
        return (List<Integer>) super.getFromInstanceByIdAndMethod(getMarkerMetadataSetDao(), datasetId, "getMarkerIdByDatasetId", 
                new Object[]{datasetId}, new Class[]{Integer.class});
    }

    @Override
    public List<ParentElement> getParentsByDatasetId(Integer datasetId) throws MiddlewareQueryException {
        return (List<ParentElement>) super.getFromInstanceByIdAndMethod(getMappingPopDao(), datasetId, "getParentsByDatasetId", 
                new Object[]{datasetId}, new Class[]{Integer.class});
    }

    @Override
    public List<String> getMarkerTypesByMarkerIds(List<Integer> markerIds) throws MiddlewareQueryException {
        return (List<String>) super.getFromInstanceByIdAndMethod(getMarkerDao(), markerIds.get(0), "getMarkerTypeByMarkerIds",
                new Object[] { markerIds }, new Class[] { List.class });
    }

    @Override
    public List<MarkerNameElement> getMarkerNamesByGIds(List<Integer> gIds) throws MiddlewareQueryException {
        return (List<MarkerNameElement>) super.getFromInstanceByIdAndMethod(getMarkerDao(), gIds.get(0), "getMarkerNamesByGIds", 
                new Object[]{gIds}, new Class[]{List.class});
    }

    @Override
    public List<GermplasmMarkerElement> getGermplasmNamesByMarkerNames(List<String> markerNames, Database instance)
            throws MiddlewareQueryException {
        return (List<GermplasmMarkerElement>) super.getFromInstanceByMethod(getMarkerDao(), instance, 
                "getGermplasmNamesByMarkerNames", new Object[]{markerNames}, new Class[]{List.class});
    }

    @Override
    public List<MappingValueElement> getMappingValuesByGidsAndMarkerNames(List<Integer> gids, List<String> markerNames, int start,
            int numOfRows) throws MiddlewareQueryException {
    	List<MappingValueElement> mappingValueElementLists = new ArrayList<MappingValueElement>();
    	
    	int gid = gids.get(0);
    	
    	if(setWorkingDatabase(gid)){
    		List<Integer> markerIds = getMarkerDao().getIdsByNames(markerNames, start, numOfRows);
    		
    		mappingValueElementLists =  super.getFromInstanceByIdAndMethod(getMappingPopDao(), gids.get(0), 
                    "getMappingValuesByGidAndMarkerIds", new Object[]{gids, markerIds}, new Class[]{List.class, List.class});
    	}
    	
        return mappingValueElementLists;
    }

    @Override
    public List<AllelicValueElement> getAllelicValuesByGidsAndMarkerNames(List<Integer> gids, List<String> markerNames)
            throws MiddlewareQueryException {
        List<AllelicValueElement> allelicValues = new ArrayList<AllelicValueElement>();
        
        // Get from CENTRAL
        allelicValues.addAll( super.getFromInstanceByMethod(getMarkerDao(), Database.CENTRAL, 
                "getAllelicValuesByGidsAndMarkerNames", new Object[]{gids, markerNames}, new Class[]{List.class, List.class}));
        
        // Get from LOCAL
        List<AllelicValueElement> allelicValuesLocal = super.getFromInstanceByMethod(getMarkerDao(), Database.LOCAL, 
                "getAllelicValuesFromLocal", new Object[]{gids}, new Class[]{List.class});
        
        // Get marker names by marker ids
        List<Integer> markerIdsLocal = new ArrayList<Integer>();
        for(AllelicValueElement allelicValue : allelicValuesLocal){
            markerIdsLocal.add(allelicValue.getMarkerId());
        }
        java.util.Map<Integer, String> markerNamesLocal = new HashMap<Integer, String>();
        setWorkingDatabase(Database.CENTRAL);
        markerNamesLocal.putAll(getMarkerDao().getNamesByIdsMap(markerIdsLocal));
        setWorkingDatabase(Database.LOCAL);
        markerNamesLocal.putAll(getMarkerDao().getNamesByIdsMap(markerIdsLocal));

        for(AllelicValueElement allelicValue : allelicValuesLocal){            
            allelicValue.setMarkerName(markerNamesLocal.get(allelicValue.getMarkerId()));
        }
        
        allelicValues.addAll(allelicValuesLocal);
        return allelicValues;
    }

    @Override
    public List<AllelicValueWithMarkerIdElement> getAllelicValuesFromCharValuesByDatasetId(Integer datasetId, int start, int numOfRows)
            throws MiddlewareQueryException {
        return (List<AllelicValueWithMarkerIdElement>) super.getFromInstanceByIdAndMethod(getCharValuesDao(), datasetId, 
                "getAllelicValuesByDatasetId", new Object[]{datasetId, start, numOfRows}, 
                new Class[]{Integer.class, Integer.TYPE, Integer.TYPE});
    }

    @Override
    public long countAllelicValuesFromCharValuesByDatasetId(Integer datasetId) throws MiddlewareQueryException {
        return super.countFromInstanceByIdAndMethod(getCharValuesDao(), datasetId, "countByDatasetId", 
                new Object[]{datasetId}, new Class[]{Integer.class});
    }

    @Override
    public List<AllelicValueWithMarkerIdElement> getAllelicValuesFromAlleleValuesByDatasetId(Integer datasetId, int start, int numOfRows)
            throws MiddlewareQueryException {
        return (List<AllelicValueWithMarkerIdElement>) super.getFromInstanceByIdAndMethod(getAlleleValuesDao(), datasetId, 
                "getAllelicValuesByDatasetId", new Object[]{datasetId, start, numOfRows}, 
                new Class[]{Integer.class, Integer.TYPE, Integer.TYPE});
    }

    @Override
    public long countAllelicValuesFromAlleleValuesByDatasetId(Integer datasetId) throws MiddlewareQueryException {
        return super.countFromInstanceByIdAndMethod(getAlleleValuesDao(), datasetId, "countByDatasetId", 
                new Object[]{datasetId}, new Class[]{Integer.class});
    }

    @Override
    public List<AllelicValueWithMarkerIdElement> getAllelicValuesFromMappingPopValuesByDatasetId(Integer datasetId, int start, int numOfRows)
            throws MiddlewareQueryException {
        return (List<AllelicValueWithMarkerIdElement>) super.getFromInstanceByIdAndMethod(getMappingPopValuesDao(), datasetId, 
                "getAllelicValuesByDatasetId", new Object[]{datasetId, start, numOfRows}, 
                new Class[]{Integer.class, Integer.TYPE, Integer.TYPE});
    }

    @Override
    public long countAllelicValuesFromMappingPopValuesByDatasetId(Integer datasetId) throws MiddlewareQueryException {
        return super.countFromInstanceByIdAndMethod(getMappingPopValuesDao(), datasetId, "countByDatasetId", 
                new Object[]{datasetId}, new Class[]{Integer.class});
    }

    @Override
    public List<MarkerInfo> getMarkerInfoByMarkerName(String markerName, int start, int numOfRows) throws MiddlewareQueryException {
        List<String> methods = Arrays.asList("countByMarkerName", "getByMarkerName");
        return (List<MarkerInfo>) super.getFromCentralAndLocalByMethod(getMarkerInfoDao(), methods, start, numOfRows,
                new Object[] { markerName }, new Class[] { String.class });
    }

    @Override
    public long countMarkerInfoByMarkerName(String markerName) throws MiddlewareQueryException {
        return super.countAllFromCentralAndLocalByMethod(getMarkerInfoDao(), "countByMarkerName", new Object[] { markerName },
                new Class[] { String.class });
    }

    @Override
    public List<MarkerInfo> getMarkerInfoByGenotype(String genotype, int start, int numOfRows) throws MiddlewareQueryException {
        List<String> methods = Arrays.asList("countByGenotype", "getByGenotype");
        return (List<MarkerInfo>) super.getFromCentralAndLocalByMethod(getMarkerInfoDao(), methods, start, numOfRows,
                new Object[] { genotype }, new Class[] { String.class });
    }

    @Override
    public long countMarkerInfoByGenotype(String genotype) throws MiddlewareQueryException {
        return super.countAllFromCentralAndLocalByMethod(getMarkerInfoDao(), "countByGenotype", new Object[]{genotype}, new Class[]{String.class});
    }
    
    @Override
    public List<MarkerInfo> getMarkerInfoByDbAccessionId(String dbAccessionId, int start, int numOfRows) throws MiddlewareQueryException {
        List<String> methods = Arrays.asList("countByDbAccessionId", "getByDbAccessionId");
        return (List<MarkerInfo>) super.getFromCentralAndLocalByMethod(getMarkerInfoDao(), methods, start, numOfRows, new Object[]{dbAccessionId}, new Class[]{String.class});
    }

    @Override
    public long countMarkerInfoByDbAccessionId(String dbAccessionId) throws MiddlewareQueryException {
        return super.countAllFromCentralAndLocalByMethod(getMarkerInfoDao(), "countByDbAccessionId", new Object[]{dbAccessionId}, new Class[]{String.class});
    }

    @Override
    public List<MarkerIdMarkerNameElement> getMarkerNamesByMarkerIds(List<Integer> markerIds) throws MiddlewareQueryException {
        List<MarkerIdMarkerNameElement> markers = super.getFromInstanceByMethod(getMarkerDao(), Database.CENTRAL, "getNamesByIds", 
                new Object[]{markerIds}, new Class[]{List.class});
        markers.addAll(super.getFromInstanceByMethod(getMarkerDao(), Database.LOCAL, "getNamesByIds", 
                new Object[]{markerIds}, new Class[]{List.class}));
        
        // Sort based on the given input order
        List<MarkerIdMarkerNameElement> markersToReturn = new ArrayList<MarkerIdMarkerNameElement>(); 
        for (Integer markerId : markerIds){
            for (MarkerIdMarkerNameElement element: markers){
                if (element.getMarkerId() == markerId){
                    markersToReturn.add(element);
                    break;
                }
            }
        }
        
        return markersToReturn;
    }
    
    private String getMarkerNameByMarkerId(Integer markerId) throws MiddlewareQueryException{
        setWorkingDatabase(markerId);
        return getMarkerDao().getNameById(markerId);
    }

    @Override
    public List<String> getAllMarkerTypes(int start, int numOfRows) throws MiddlewareQueryException {
        List<String> methods = Arrays.asList("countAllMarkerTypes", "getAllMarkerTypes");
        return (List<String>) super.getFromCentralAndLocalByMethod(getMarkerDao(), methods, start, numOfRows, new Object[]{}, new Class[]{});
    }

    @Override
    public long countAllMarkerTypes(Database instance) throws MiddlewareQueryException {
        return super.countFromInstanceByMethod(getMarkerDao(), instance, "countAllMarkerTypes", new Object[]{}, new Class[]{});
    }

    @Override
    public List<String> getMarkerNamesByMarkerType(String markerType, int start, int numOfRows) throws MiddlewareQueryException {
        List<String> methods = Arrays.asList("countMarkerNamesByMarkerType", "getMarkerNamesByMarkerType");
        return (List<String>) super.getFromCentralAndLocalByMethod(getMarkerDao(), methods, start, numOfRows, new Object[]{markerType}, new Class[]{String.class});
    }
    
    @Override
    public long countMarkerNamesByMarkerType(String markerType) throws MiddlewareQueryException {
        return super.countAllFromCentralAndLocalByMethod(getMarkerDao(), "countMarkerNamesByMarkerType", new Object[]{markerType}, new Class[]{String.class});
    }

    @Override
    public List<Integer> getGIDsFromCharValuesByMarkerId(Integer markerId, int start, int numOfRows) throws MiddlewareQueryException {
        return (List<Integer>) super.getFromInstanceByIdAndMethod(getCharValuesDao(), markerId, "getGIDsByMarkerId", 
                new Object[]{markerId, start, numOfRows}, new Class[]{Integer.class, Integer.TYPE, Integer.TYPE});
    }

    @Override
    public long countGIDsFromCharValuesByMarkerId(Integer markerId) throws MiddlewareQueryException {
        return super.countFromInstanceByIdAndMethod(getCharValuesDao(), markerId, "countGIDsByMarkerId", new Object[] { markerId },
                new Class[] { Integer.class });
    }

    @Override
    public List<Integer> getGIDsFromAlleleValuesByMarkerId(Integer markerId, int start, int numOfRows) throws MiddlewareQueryException {
        return (List<Integer>) super.getFromInstanceByIdAndMethod(getAlleleValuesDao(), markerId, "getGIDsByMarkerId", new Object[] {
                markerId, start, numOfRows }, new Class[] { Integer.class, Integer.TYPE, Integer.TYPE });
    }

    @Override
    public long countGIDsFromAlleleValuesByMarkerId(Integer markerId) throws MiddlewareQueryException {
        return super.countFromInstanceByIdAndMethod(getAlleleValuesDao(), markerId, "countGIDsByMarkerId", new Object[] { markerId },
                new Class[] { Integer.class });
    }

    @Override
    public List<Integer> getGIDsFromMappingPopValuesByMarkerId(Integer markerId, int start, int numOfRows) throws MiddlewareQueryException {
        return (List<Integer>) super.getFromInstanceByIdAndMethod(getMappingPopValuesDao(), markerId, "getGIDsByMarkerId", new Object[] {
            markerId, start, numOfRows }, new Class[] { Integer.class, Integer.TYPE, Integer.TYPE });
    }

    @Override
    public long countGIDsFromMappingPopValuesByMarkerId(Integer markerId) throws MiddlewareQueryException {
        return super.countFromInstanceByIdAndMethod(getMappingPopValuesDao(), markerId, "countGIDsByMarkerId", new Object[] { markerId },
                new Class[] { Integer.class });
    }

    @Override
    public List<String> getAllDbAccessionIdsFromMarker(int start, int numOfRows) throws MiddlewareQueryException {
        List<String> methods = Arrays.asList("countAllDbAccessionIds", "getAllDbAccessionIds");
        return (List<String>) super.getFromCentralAndLocalByMethod(getMarkerDao(), methods, start, numOfRows, new Object[]{}, new Class[]{});
    }

    @Override
    public long countAllDbAccessionIdsFromMarker() throws MiddlewareQueryException {
        return super.countAllFromCentralAndLocalByMethod(getMarkerDao(), "countAllDbAccessionIds", new Object[]{}, new Class[]{});
    }

    @Override
    public List<Integer> getNidsFromAccMetadatasetByDatasetIds(List<Integer> datasetIds, int start, int numOfRows)
            throws MiddlewareQueryException {
        return getNidsFromAccMetadatasetByDatasetIds(datasetIds, null, start, numOfRows);
    }

    @Override
    public List<Integer> getNidsFromAccMetadatasetByDatasetIds(List<Integer> datasetIds, List<Integer> gids, int start, int numOfRows)
            throws MiddlewareQueryException {
        return (List<Integer>) super.getFromInstanceByIdAndMethod(getAccMetadataSetDao(), datasetIds.get(0), "getNIDsByDatasetIds", 
                new Object[]{datasetIds, gids, start, numOfRows}, new Class[]{List.class, List.class, Integer.TYPE, Integer.TYPE});
    }

    
    private List<Integer> getNIdsByMarkerIdsAndDatasetIdsAndNotGIdsFromDB(List<Integer> datasetIds, List<Integer> markerIds,
            List<Integer> gIds, int start, int numOfRows) throws MiddlewareQueryException {
        Set<Integer> nidSet = new TreeSet<Integer>();
        
        if (setWorkingDatabase(Database.CENTRAL)){
            nidSet.addAll(getAccMetadataSetDao().getNIdsByMarkerIdsAndDatasetIdsAndNotGIds(datasetIds, markerIds, gIds, start, numOfRows));
        }
        if (setWorkingDatabase(Database.LOCAL)){
            nidSet.addAll(getAccMetadataSetDao().getNIdsByMarkerIdsAndDatasetIdsAndNotGIds(datasetIds, markerIds, gIds, start, numOfRows));
        }

        return new ArrayList<Integer>(((TreeSet<Integer>)nidSet).descendingSet());
    }
    
    @Override
    public List<Integer> getNIdsByMarkerIdsAndDatasetIdsAndNotGIds(List<Integer> datasetIds, List<Integer> markerIds, List<Integer> gIds,
            int start, int numOfRows) throws MiddlewareQueryException {
        return (List<Integer>) getNIdsByMarkerIdsAndDatasetIdsAndNotGIdsFromDB(datasetIds, markerIds, gIds, start, numOfRows);
    }

    @Override
    public int countNIdsByMarkerIdsAndDatasetIdsAndNotGIds(List<Integer> datasetIds, List<Integer> markerIds, List<Integer> gIds)
            throws MiddlewareQueryException {
        return (int) countAllFromCentralAndLocalByMethod(getAccMetadataSetDao(), "countNIdsByMarkerIdsAndDatasetIdsAndNotGIds", 
                new Object[]{datasetIds, markerIds, gIds}, new Class[]{List.class, List.class, List.class});
    }

    private List<Integer> getNIdsByMarkerIdsAndDatasetIds(List<Integer> datasetIds, List<Integer> markerIds) throws MiddlewareQueryException {
        Set<Integer> nidSet = new TreeSet<Integer>();
        
        if (setWorkingDatabase(Database.CENTRAL)){
            nidSet.addAll(getAccMetadataSetDao().getNIdsByMarkerIdsAndDatasetIds(datasetIds, markerIds));            
        }
        
        if (setWorkingDatabase(Database.LOCAL)){
            nidSet.addAll(getAccMetadataSetDao().getNIdsByMarkerIdsAndDatasetIds(datasetIds, markerIds));            
        }
        
        return new ArrayList<Integer>(((TreeSet<Integer>)nidSet).descendingSet());
    }

    @Override
    public List<Integer> getNIdsByMarkerIdsAndDatasetIds(List<Integer> datasetIds, List<Integer> markerIds, int start, int numOfRows)
            throws MiddlewareQueryException {
        List<Integer> nidList = getNIdsByMarkerIdsAndDatasetIds(datasetIds, markerIds);
        return nidList.subList(start, start+numOfRows);
    }

    @Override
    public int countNIdsByMarkerIdsAndDatasetIds(List<Integer> datasetIds, List<Integer> markerIds) throws MiddlewareQueryException {
        List<Integer> nidList = getNIdsByMarkerIdsAndDatasetIds(datasetIds, markerIds);
        return nidList.size();
    }

    @Override
    public List<Integer> getDatasetIdsForFingerPrinting(int start, int numOfRows) throws MiddlewareQueryException {
        List<String> methods = Arrays.asList("countDatasetIdsForFingerPrinting", "getDatasetIdsForFingerPrinting");
        return (List<Integer>) super.getFromCentralAndLocalByMethod(getDatasetDao(), methods, start, numOfRows, new Object[]{}, new Class[]{});
    }

    @Override
    public long countDatasetIdsForFingerPrinting() throws MiddlewareQueryException {
        return super.countAllFromCentralAndLocalByMethod(getDatasetDao(), "countDatasetIdsForFingerPrinting", new Object[]{}, new Class[]{});
    }

    @Override
    public List<Integer> getDatasetIdsForMapping(int start, int numOfRows) throws MiddlewareQueryException {
        List<String> methods = Arrays.asList("countDatasetIdsForMapping", "getDatasetIdsForMapping");
        return (List<Integer>) super.getFromCentralAndLocalByMethod(getDatasetDao(), methods, start, numOfRows, new Object[]{}, new Class[]{});
    }

    @Override
    public long countDatasetIdsForMapping() throws MiddlewareQueryException {
        return super.countAllFromCentralAndLocalByMethod(getDatasetDao(), "countDatasetIdsForMapping", new Object[]{}, new Class[]{});
    }

    @Override
    public List<AccMetadataSetPK> getGdmsAccMetadatasetByGid(List<Integer> gids, int start, int numOfRows) throws MiddlewareQueryException {
        List<String> methods = Arrays.asList("countAccMetadataSetByGids", "getAccMetadataSetByGids");
        return (List<AccMetadataSetPK>) super.getFromCentralAndLocalBySignedIdAndMethod(getAccMetadataSetDao(), methods, start, numOfRows,
                new Object[] { gids }, new Class[] { List.class });
    }

    @Override
    public long countGdmsAccMetadatasetByGid(List<Integer> gids) throws MiddlewareQueryException {
        return super.countAllFromCentralAndLocalBySignedIdAndMethod(getAccMetadataSetDao(), "countAccMetadataSetByGids",
                new Object[] { gids }, new Class[] { List.class });
    }

    @Override
    public List<Integer> getMarkersByGidAndDatasetIds(Integer gid, List<Integer> datasetIds, int start, int numOfRows)
            throws MiddlewareQueryException {
        List<String> methods = Arrays.asList("countMarkersByGidAndDatasetIds", "getMarkersByGidAndDatasetIds");
        return (List<Integer>) super.getFromCentralAndLocalByMethod(getMarkerMetadataSetDao(), methods, start, numOfRows, 
                new Object[]{gid, datasetIds}, new Class[]{Integer.class, List.class});
    }

    @Override
    public long countMarkersByGidAndDatasetIds(Integer gid, List<Integer> datasetIds) throws MiddlewareQueryException {
        return super.countAllFromCentralAndLocalByMethod(getMarkerMetadataSetDao(), "countMarkersByGidAndDatasetIds", 
                new Object[]{gid, datasetIds}, new Class[]{Integer.class, List.class});
    }

    @Override
    public List<Marker> getMarkersByMarkerIds(List<Integer> markerIds, int start, int numOfRows) throws MiddlewareQueryException {
        List<String> methods = Arrays.asList("countMarkersByIds", "getMarkersByIds");
        return (List<Marker>) super.getFromCentralAndLocalByMethod(getMarkerDao(), methods, start, numOfRows, new Object[]{markerIds}, new Class[]{List.class});
    }

    @Override
    public long countMarkersByMarkerIds(List<Integer> markerIds) throws MiddlewareQueryException {
        return super.countAllFromCentralAndLocalByMethod(getMarkerDao(), "countMarkersByIds", new Object[]{markerIds}, new Class[]{List.class});
    }

    @Override
    public long countAlleleValuesByGids(List<Integer> gids) throws MiddlewareQueryException {
        return super.countAllFromCentralAndLocalByMethod(getAlleleValuesDao(), "countAlleleValuesByGids", new Object[]{gids}, new Class[]{List.class});
    }

    @Override
    public long countCharValuesByGids(List<Integer> gids) throws MiddlewareQueryException {
        return super.countAllFromCentralAndLocalByMethod(getCharValuesDao(), "countCharValuesByGids", new Object[]{gids}, new Class[]{List.class});
    }
    
    @Override
    public List<AllelicValueElement> getIntAlleleValuesForPolymorphicMarkersRetrieval(List<Integer> gids, int start, int numOfRows)
            throws MiddlewareQueryException {
        return getForPolyMorphicMarkersRetrieval("countIntAlleleValuesForPolymorphicMarkersRetrieval", 
                "getIntAlleleValuesForPolymorphicMarkersRetrieval", gids, start, numOfRows);
    }

    @Override
    public long countIntAlleleValuesForPolymorphicMarkersRetrieval(List<Integer> gids) throws MiddlewareQueryException {
        return super.countAllFromCentralAndLocalBySignedIdAndMethod(getAlleleValuesDao(),
                "countIntAlleleValuesForPolymorphicMarkersRetrieval", new Object[] { gids }, new Class[] { List.class });
    }

    @Override
    public List<AllelicValueElement> getCharAlleleValuesForPolymorphicMarkersRetrieval(List<Integer> gids, int start, int numOfRows)
            throws MiddlewareQueryException {
        return getForPolyMorphicMarkersRetrieval("countCharAlleleValuesForPolymorphicMarkersRetrieval", 
                "getCharAlleleValuesForPolymorphicMarkersRetrieval", gids, start, numOfRows);
    }

    @Override
    public long countCharAlleleValuesForPolymorphicMarkersRetrieval(List<Integer> gids) throws MiddlewareQueryException {
        return super.countAllFromCentralAndLocalBySignedIdAndMethod(getAlleleValuesDao(), 
                "countCharAlleleValuesForPolymorphicMarkersRetrieval", new Object[]{gids}, new Class[]{List.class});
    }

    @Override
    public List<AllelicValueElement> getMappingAlleleValuesForPolymorphicMarkersRetrieval(List<Integer> gids, int start, int numOfRows)
            throws MiddlewareQueryException {
        return getForPolyMorphicMarkersRetrieval("countMappingAlleleValuesForPolymorphicMarkersRetrieval", 
                "getMappingAlleleValuesForPolymorphicMarkersRetrieval", gids, start, numOfRows);
    }

    @Override
    public long countMappingAlleleValuesForPolymorphicMarkersRetrieval(List<Integer> gids) throws MiddlewareQueryException {
        return super.countAllFromCentralAndLocalBySignedIdAndMethod(getAlleleValuesDao(), 
                "countMappingAlleleValuesForPolymorphicMarkersRetrieval", new Object[]{gids}, new Class[]{List.class});
    }

    private List<AllelicValueElement> getForPolyMorphicMarkersRetrieval(String countMethodName, String getMethodName, 
            List<Integer> gids, int start, int numOfRows) throws MiddlewareQueryException{
        List<String> methods = Arrays.asList(countMethodName, getMethodName);
        List<AllelicValueElement> allelicValueElements = (List<AllelicValueElement>) super.getFromCentralAndLocalBySignedIdAndMethod(
                getAlleleValuesDao(), methods, start, numOfRows, new Object[] { gids }, new Class[] { List.class });
        
        //Sort by gid, markerName
        Collections.sort(allelicValueElements, AllelicValueElement.AllelicValueElementComparator);

        return allelicValueElements;

    }
    
    @Override
    public List<Qtl> getAllQtl(int start, int numOfRows) throws MiddlewareQueryException {
        return (List<Qtl>) getFromCentralAndLocal(getQtlDao(), start, numOfRows);
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

        List<String> methods = Arrays.asList("countQtlIdByName", "getQtlIdByName");
        return (List<Integer>) super.getFromCentralAndLocalByMethod(getQtlDao(), methods, start, numOfRows, 
                new Object[]{name}, new Class[]{String.class});
    }

    @Override
    public long countQtlIdByName(String name) throws MiddlewareQueryException {
        if ((name == null) || (name.isEmpty())) {
            return 0;
        }
        return super.countAllFromCentralAndLocalByMethod(getQtlDao(), "countQtlIdByName", new Object[]{name}, new Class[]{String.class});
    }

    @Override
    public List<QtlDetailElement> getQtlByName(String name, int start, int numOfRows) throws MiddlewareQueryException {
        List<QtlDetailElement> qtlDetailElements = new ArrayList<QtlDetailElement>();
        if ((name == null) || (name.isEmpty())) {
            return qtlDetailElements;
        }

        // Get records from CENTRAL
        qtlDetailElements.addAll(
                (List<QtlDetailElement>) getFromInstanceByMethod(getQtlDao(), Database.CENTRAL, "getQtlAndQtlDetailsByName", 
                new Object[]{name, start, numOfRows}, new Class[]{String.class, Integer.TYPE, Integer.TYPE}));
        
        // Get records from LOCAL
        // 1. Get gdms_qtl and gdms_qtl_details based on name from LOCAL
        List<Qtl> qtlLocal = getFromInstanceByMethod(getQtlDao(), Database.LOCAL, "getQtlByName", 
                new Object[]{name}, new Class[]{String.class});
        
        List<Integer> qtlIds = new ArrayList<Integer>();
        for(Qtl qtl : qtlLocal){
            qtlIds.add(qtl.getQtlId());
        }
        
        if (qtlIds != null && qtlIds.size() > 0){
            List<QtlDetails> qtlDetailsLocal = getFromInstanceByMethod(getQtlDetailsDao(), Database.LOCAL, 
                    "getQtlDetailsByQtlIds", new Object[]{qtlIds}, new Class[]{List.class});

            qtlDetailElements.addAll(getQtlDetailElementsFromLocal(qtlDetailsLocal, qtlLocal));
        }
        return qtlDetailElements;

    }

    @Override
    public long countQtlByName(String name) throws MiddlewareQueryException {
        if ((name == null) || (name.isEmpty())) {
            return 0;
        }
        return super.countAllFromCentralAndLocalByMethod(getQtlDao(), "countQtlAndQtlDetailsByName"
                , new Object[]{name}, new Class[]{String.class});
    }
    
    @Override
    public java.util.Map<Integer, String> getQtlNamesByQtlIds(List<Integer> qtlIds) throws MiddlewareQueryException{
        java.util.Map<Integer, String> qtlNames = new HashMap<Integer, String>();

        setWorkingDatabase(Database.CENTRAL);
        qtlNames.putAll(getQtlDao().getQtlNameByQtlIds(qtlIds));

        setWorkingDatabase(Database.LOCAL);
        qtlNames.putAll(getQtlDao().getQtlNameByQtlIds(qtlIds));
        
        return qtlNames;
    }

    @Override
    public List<QtlDetailElement> getQtlByQtlIds(List<Integer> qtlIds, int start, int numOfRows) throws MiddlewareQueryException {
        List<QtlDetailElement> qtlDetailElements = new ArrayList<QtlDetailElement>();

        if ((qtlIds == null) || (qtlIds.isEmpty())) {
            return qtlDetailElements;
        }
        
        // Get records from CENTRAL
        qtlDetailElements.addAll(
                (List<QtlDetailElement>) getFromInstanceByMethod(getQtlDao(), Database.CENTRAL, "getQtlAndQtlDetailsByQtlIds", 
                new Object[]{qtlIds, start, numOfRows}, new Class[]{List.class, Integer.TYPE, Integer.TYPE}));

        //Get records from LOCAL
        // 1. Get gdms_qtl and gdms_qtl_details based on qtl_id from LOCAL
        List<QtlDetails> qtlDetailsLocal = getFromInstanceByMethod(getQtlDetailsDao(), Database.LOCAL, "getQtlDetailsByQtlIds", 
                new Object[]{qtlIds}, new Class[]{List.class});
        List<Qtl> qtlLocal = getFromInstanceByMethod(getQtlDao(), Database.LOCAL, "getQtlsByIds", 
                new Object[]{qtlIds}, new Class[]{List.class});

        qtlDetailElements.addAll(getQtlDetailElementsFromLocal(qtlDetailsLocal, qtlLocal));

        return qtlDetailElements;
    }
    
    
    private List<QtlDetailElement> getQtlDetailElementsFromLocal(List<QtlDetails> qtlDetailsLocal, List<Qtl> qtlLocal) 
            throws MiddlewareQueryException{

        //2. Get mapId and traitId from QtlDetails
        Set<Integer> mapIdSet = new HashSet<Integer>();
        Set<Integer> traitIdSet = new HashSet<Integer>();
        for (QtlDetails details : qtlDetailsLocal){
            mapIdSet.add(details.getId().getMapId());
            traitIdSet.add(details.getTraitId());
        }
        List<Integer> mapIds = new ArrayList<Integer>(mapIdSet);
        List<Integer> traitIds = new ArrayList<Integer>(traitIdSet);
        
        // 3. With retrieved gdms_qtl_details.map_id, get maps from gdms_map central and local 
        List<Map> maps = new ArrayList<Map>();
        if (mapIds != null && mapIds.size() > 0){
            maps = super.getAllFromCentralAndLocalByMethod(getMapDao()
                , "getMapsByIds", new Object[]{mapIds}, new Class[]{List.class});
        }
        // 4. With retrieved gdms_qtl_details.tid, retrieve from cvterm & cvtermprop - central and local 
        
        List<CVTerm> cvTerms = new ArrayList<CVTerm>();
        List<CVTermProperty> cvTermProperties = new ArrayList<CVTermProperty>();
        if (traitIds != null && traitIds.size() > 0){
            cvTerms = super.getAllFromCentralAndLocalByMethod(super.getCvTermDao(), "getByIds"
                , new Object[]{traitIds}, new Class[]{List.class});
            cvTermProperties = super.getAllFromCentralAndLocalByMethod(super.getCvTermPropertyDao()
                    , "getByCvTermIds" , new Object[]{traitIds}, new Class[]{List.class});
        }
        
        
        // Construct qtlDetailsElement        
            // qtlDetailsLocal
            // inner join with qtlLocal - on qtlId
            // inner join with maps.mapId
            // inner join with cvTerm.traitId
            // left join with cvTermProperties

        Set<QtlDetailElement> qtlDetailElementsLocal = new HashSet<QtlDetailElement>();
        
        for (QtlDetails details : qtlDetailsLocal){
            for (Qtl qtl : qtlLocal){
                if (details.getQtlId().equals(qtl.getQtlId())){
                    for(Map map : maps){
                        if (details.getMapId().equals(map.getMapId())){
                            QtlDetailElement element = new QtlDetailElement(qtl.getQtlName(), map.getMapName(), details);
                            for(CVTerm term : cvTerms){
                                if (details.getTraitId().equals(term.getCvTermId())){
                                    element.settRName(term.getName());
                                    break;
                                }
                            }
                            for(CVTermProperty property: cvTermProperties){
                                if (details.getTraitId().equals(property.getCvTermId())){
                                    element.setOntology(property.getValue());
                                    break;
                                }
                            }
                            qtlDetailElementsLocal.add(element);
                        }
                    }
                } else {
                    continue;
                }

            }
        }
        
        return new ArrayList<QtlDetailElement>(qtlDetailElementsLocal);
    }

    @Override
    public long countQtlByQtlIds(List<Integer> qtlIds) throws MiddlewareQueryException {
        if ((qtlIds == null) || (qtlIds.isEmpty())) {
            return 0;
        }
        return super.countAllFromCentralAndLocalByMethod(getQtlDao(), "countQtlAndQtlDetailsByQtlIds"
                , new Object[]{qtlIds}, new Class[]{List.class});
    }

    @Override
    public List<Integer> getQtlByTrait(Integer trait, int start, int numOfRows) throws MiddlewareQueryException {
        List<String> methods = Arrays.asList("countQtlByTrait", "getQtlByTrait");
        return (List<Integer>) super.getFromCentralAndLocalByMethod(getQtlDao(), methods, start, numOfRows, 
                new Object[]{trait}, new Class[]{Integer.class});
    }

    @Override
    public long countQtlByTrait(Integer trait) throws MiddlewareQueryException {
        return super.countAllFromCentralAndLocalByMethod(getQtlDao(), "countQtlByTrait", new Object[]{trait}, new Class[]{Integer.class});
    }

    @Override
    public List<Integer> getQtlTraitsByDatasetId(Integer datasetId, int start, int numOfRows) throws MiddlewareQueryException{
        return (List<Integer>) super.getFromInstanceByIdAndMethod(getQtlDetailsDao(), datasetId, "getQtlTraitsByDatasetId", 
        		new Object[]{datasetId, start, numOfRows}, new Class[]{Integer.class, Integer.TYPE, Integer.TYPE});
    }

    @Override
    public long countQtlTraitsByDatasetId(Integer datasetId) throws MiddlewareQueryException{
    	return super.countFromInstanceByIdAndMethod(getQtlDetailsDao(), datasetId, "countQtlTraitsByDatasetId", 
    			new Object[]{datasetId}, new Class[]{Integer.class});
    }

    @Override
    public List<ParentElement> getAllParentsFromMappingPopulation(int start, int numOfRows) throws MiddlewareQueryException {
        List<String> methods = Arrays.asList("countAllParentsFromMappingPopulation", "getAllParentsFromMappingPopulation");
        return (List<ParentElement>) super.getFromCentralAndLocalByMethod(getMappingPopDao(), methods, start, numOfRows, 
                new Object[]{}, new Class[]{});
    }

    @Override
    public Long countAllParentsFromMappingPopulation() throws MiddlewareQueryException {
        return super.countAllFromCentralAndLocalByMethod(getMappingPopDao(), "countAllParentsFromMappingPopulation", new Object[]{}, new Class[]{});
    }

    @Override
    public List<MapDetailElement> getMapDetailsByName(String nameLike, int start, int numOfRows) throws MiddlewareQueryException {
        List<String> methods = Arrays.asList("countMapDetailsByName", "getMapDetailsByName");
        return (List<MapDetailElement>) super.getFromCentralAndLocalByMethod(getMapDao(), methods, start, numOfRows, 
                new Object[]{nameLike}, new Class[]{String.class});
    }

    @Override
    public Long countMapDetailsByName(String nameLike) throws MiddlewareQueryException {
        return super.countAllFromCentralAndLocalByMethod(getMapDao(), "countMapDetailsByName", new Object[]{nameLike}, new Class[]{String.class});
    }
    
    @Override
    public java.util.Map<Integer, List<String>> getMapNamesByMarkerIds(List<Integer> markerIds) throws MiddlewareQueryException{

        java.util.Map<Integer, List<String>> markerMaps = new HashMap<Integer, List<String>>();

        if (markerIds == null || markerIds.size() == 0){
            return markerMaps;
        }
        
        // Get from Central
        setWorkingDatabase(Database.CENTRAL);
        markerMaps.putAll(getMarkerOnMapDao().getMapNameByMarkerIds(markerIds));
        
        setWorkingDatabase(Database.LOCAL);
        markerMaps.putAll(getMarkerOnMapDao().getMapNameByMarkerIds(markerIds));

        return markerMaps;
    }

    @Override
    public List<MapDetailElement> getAllMapDetails(int start, int numOfRows) throws MiddlewareQueryException {
        List<String> methods = Arrays.asList("countAllMapDetails", "getAllMapDetails");
        return (List<MapDetailElement>) super.getFromCentralAndLocalByMethod(getMapDao(), methods, start, numOfRows, 
                new Object[]{}, new Class[]{});
    }

    @Override
    public long countAllMapDetails() throws MiddlewareQueryException {
        return super.countAllFromCentralAndLocalByMethod(getMapDao(), "countAllMapDetails", new Object[]{}, new Class[]{});
    }

    @Override
    public List<Integer> getMapIdsByQtlName(String qtlName, int start, int numOfRows) throws MiddlewareQueryException {
        List<String> methods = Arrays.asList("countMapIdsByQtlName", "getMapIdsByQtlName");
        return (List<Integer>) super.getFromCentralAndLocalByMethod(getQtlDetailsDao(), methods, start, numOfRows, 
                new Object[]{qtlName}, new Class[]{String.class});
    }

    @Override
    public long countMapIdsByQtlName(String qtlName) throws MiddlewareQueryException {
        return super.countAllFromCentralAndLocalByMethod(getQtlDetailsDao(), "countMapIdsByQtlName", new Object[]{qtlName}, new Class[]{String.class});
    }

    @Override
    public List<Integer> getMarkerIdsByQtl(String qtlName, String chromosome, int min, int max, int start, int numOfRows)
            throws MiddlewareQueryException {
        //TODO
        return null;
    }

    @Override
    public long countMarkerIdsByQtl(String qtlName, String chromosome, int min, int max) throws MiddlewareQueryException {
        return super.countAllFromCentralAndLocalByMethod(getQtlDetailsDao(), "countMarkerIdsByQtl", 
                new Object[]{qtlName, chromosome, min, max}, new Class[]{String.class, String.class, Integer.TYPE, Integer.TYPE});
    }

    @Override
    public List<Marker> getMarkersByIds(List<Integer> markerIds, int start, int numOfRows) throws MiddlewareQueryException {
        List<Marker> markers = new ArrayList<Marker>();
        List<Integer> positiveGids = getPositiveIds(markerIds);
        List<Integer> negativeGids = getNegativeIds(markerIds);

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
        Session session = requireLocalDatabaseInstance();
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
        // No need to auto-assign negative id. It should come from an existing entry in Marker.
        return ((MarkerDetails) super.save(getMarkerDetailsDao(), markerDetails)).getMarkerId();
    }

    @Override
    public Integer addMarkerUserInfo(MarkerUserInfo markerUserInfo) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        // No need to auto-assign negative id. It should come from an existing entry in Marker.
        return ((MarkerUserInfo) super.save(getMarkerUserInfoDao(), markerUserInfo)).getMarkerId();
    }

    @Override
    public AccMetadataSetPK addAccMetadataSet(AccMetadataSet accMetadataSet) throws MiddlewareQueryException {
        Session session = requireLocalDatabaseInstance();
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
        Session session = requireLocalDatabaseInstance();
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
        dataset.setDatasetId(getDatasetDao().getNegativeId("datasetId"));
        return ((Dataset) super.save(getDatasetDao(), dataset)).getDatasetId();
    }

    @Override
    public Integer addGDMSMarker(Marker marker) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        marker.setMarkerId(getMarkerDao().getNegativeId("markerId"));
        return ((Marker) super.saveOrUpdate(getMarkerDao(), marker)).getMarkerId();
    }

    @Override
    public Integer addGDMSMarkerAlias(MarkerAlias markerAlias) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        // No need to auto-assign negative id. It should come from an existing entry in Marker.
        return ((MarkerAlias) super.save(getMarkerAliasDao(), markerAlias)).getMarkerId();
    }

    @Override
    public Integer addDatasetUser(DatasetUsers datasetUser) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        return ((DatasetUsers) super.save(getDatasetUsersDao(), datasetUser)).getUserId();
    }

    @Override
    public Integer addAlleleValues(AlleleValues alleleValues) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        alleleValues.setAnId(getAlleleValuesDao().getNegativeId("anId"));
        return ((AlleleValues) super.saveOrUpdate(getAlleleValuesDao(), alleleValues)).getAnId();
    }

    @Override
    public Integer addCharValues(CharValues charValues) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        charValues.setAcId(getCharValuesDao().getNegativeId("acId"));
        return ((CharValues) super.saveOrUpdate(getCharValuesDao(), charValues)).getAcId();
    }

    @Override
    public Integer addMappingPop(MappingPop mappingPop) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        return ((MappingPop) super.save(getMappingPopDao(), mappingPop)).getDatasetId();
    }

    @Override
    public Integer addMappingPopValue(MappingPopValues mappingPopValue) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        mappingPopValue.setMpId(getMappingPopValuesDao().getNegativeId("mpId"));
        return ((MappingPopValues) super.saveOrUpdate(getMappingPopValuesDao(), mappingPopValue)).getMpId();
    }

    @Override
    public Integer addMarkerOnMap(MarkerOnMap markerOnMap) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        if (getMapDao().getById(markerOnMap.getMapId()) == null){
            throw new MiddlewareQueryException("Map Id not found: " + markerOnMap.getMapId());
        }

        return ((MarkerOnMap) super.save(getMarkerOnMapDao(), markerOnMap)).getMapId();
    }

    @Override
    public Integer addDartValue(DartValues dartValue) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        dartValue.setAdId(getDartValuesDao().getNegativeId("adId"));
        return ((DartValues) super.save(getDartValuesDao(), dartValue)).getAdId();
    }

    @Override
    public Integer addQtl(Qtl qtl) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        qtl.setQtlId(getQtlDao().getNegativeId("qtlId"));
        return ((Qtl) super.saveOrUpdate(getQtlDao(), qtl)).getQtlId();
    }

    @Override
    public Integer addMap(Map map) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        map.setMapId(getMapDao().getNegativeId("mapId"));
        return ((Map) super.saveOrUpdate(getMapDao(), map)).getMapId();
    }

    @Override
    public Boolean setSSRMarkers(Marker marker, MarkerAlias markerAlias, MarkerDetails markerDetails, MarkerUserInfo markerUserInfo)
            throws MiddlewareQueryException {
        return setMarker(marker, TYPE_SSR, markerAlias, markerDetails, markerUserInfo);
    }

    @Override
    public Boolean setSNPMarkers(Marker marker, MarkerAlias markerAlias, MarkerDetails markerDetails, MarkerUserInfo markerUserInfo)
            throws MiddlewareQueryException{
        return setMarker(marker, TYPE_SNP, markerAlias, markerDetails, markerUserInfo);
    }

    @Override
    public Boolean setCAPMarkers(Marker marker, MarkerAlias markerAlias, MarkerDetails markerDetails, MarkerUserInfo markerUserInfo)
            throws MiddlewareQueryException {
        return setMarker(marker, TYPE_CAP, markerAlias, markerDetails, markerUserInfo);
    }


    @Override
    public Boolean setCISRMarkers(Marker marker, MarkerAlias markerAlias, MarkerDetails markerDetails, MarkerUserInfo markerUserInfo)
            throws MiddlewareQueryException {
        return setMarker(marker, TYPE_CISR, markerAlias, markerDetails, markerUserInfo);
    }

    private Boolean setMarker(Marker marker, String markerType, MarkerAlias markerAlias, MarkerDetails markerDetails, MarkerUserInfo markerUserInfo) 
            throws MiddlewareQueryException{
        Session session = requireLocalDatabaseInstance();
        Transaction trans = null;

        try {
            // begin save transaction
            trans = session.beginTransaction();

            // Add GDMS Marker
            Integer idGDMSMarkerSaved = saveMarker(marker, markerType);
            marker.setMarkerId(idGDMSMarkerSaved);
            marker.setMarkerType(markerType);

            // Add GDMS Marker Alias
            markerAlias.setMarkerId(idGDMSMarkerSaved);
            saveMarkerAlias(markerAlias);

            // Add Marker Details
            markerDetails.setMarkerId(idGDMSMarkerSaved);
            saveMarkerDetails(markerDetails);
            
            // Add marker user info
            markerUserInfo.setMarkerId(idGDMSMarkerSaved);
            saveMarkerUserInfo(markerUserInfo);
            
            trans.commit();
            return true;
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while saving Marker: " + e.getMessage(), e, LOG);
            return false;
        } finally {
            session.flush();
        }
        
    }
    
    @Override
    public Boolean setQTL(Dataset dataset, DatasetUsers datasetUser, List<QtlDataRow> rows) throws MiddlewareQueryException{
        Session session = requireLocalDatabaseInstance();
        Transaction trans = null;
        try {
            trans = session.beginTransaction();
            
            Integer datasetId = saveDataset(dataset, TYPE_QTL, null);
            
            saveDatasetUser(datasetId, datasetUser);
            
            // Save QTL data rows
            if (rows != null && rows.size() > 0){
                for (QtlDataRow row : rows){
                    Qtl qtl = row.getQtl();
                    QtlDetails qtlDetails = row.getQtlDetails();
    
                    Integer qtlIdSaved = saveQtl(datasetId, qtl);
                    qtlDetails.setQtlId(new QtlDetailsPK(qtlIdSaved, qtlDetails.getId().getMapId()));
                    saveQtlDetails(qtlDetails);
                }
            }

            trans.commit();
            return true;
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered with setQTL(): " + e.getMessage(), e, LOG);
            return false;
        } finally {
            session.flush();
        }
    }
    

    @Override
    public Boolean setDart(Dataset dataset, DatasetUsers datasetUser, List<DartDataRow> rows) throws MiddlewareQueryException{
        Session session = requireLocalDatabaseInstance();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();

            Integer datasetId = saveDataset(dataset, TYPE_DART, DATA_TYPE_INT);
            dataset.setDatasetId(datasetId);
            saveDatasetUser(datasetId, datasetUser);
            
            // Save data rows
            if (rows != null && rows.size() > 0){
                for (DartDataRow row : rows){
                    
                    Marker marker = row.getMarker();
                    marker.setMarkerType(TYPE_DART);
                    Integer markerId = saveMarker(marker, TYPE_DART);
                    marker.setMarkerId(markerId);
                    
                    saveAccMetadataSet(datasetId, row.getAccMetadataSet());
                    saveMarkerMetadataSet(datasetId, markerId, row.getMarkerMetadataSet());
                    saveAlleleValues(datasetId, markerId, row.getAlleleValues());
                    saveDartValues(datasetId, markerId, row.getDartValues());    
                }
            }

            trans.commit();
            return true;
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while setting DArT: setDArT(): " + e.getMessage(), e, LOG);
            return false;
        } finally {
            session.flush();
        }
    }

    @Override
    public Boolean setSSR(Dataset dataset, DatasetUsers datasetUser, List<SSRDataRow> rows) throws MiddlewareQueryException{
        Session session = requireLocalDatabaseInstance();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            
            Integer datasetId = saveDataset(dataset, TYPE_SSR, DATA_TYPE_INT);
            dataset.setDatasetId(datasetId);
            saveDatasetUser(datasetId, datasetUser);
            
            // Save data rows
            if (rows != null && rows.size() > 0){
                for (SSRDataRow row : rows){
                    Marker marker = row.getMarker();
                    marker.setMarkerType(TYPE_SSR);
                    Integer markerId = saveMarker(marker, TYPE_SSR);
                    marker.setMarkerId(markerId);
                    
                    saveAccMetadataSet(datasetId, row.getAccMetadataSet());
                    saveMarkerMetadataSet(datasetId, markerId, row.getMarkerMetadataSet());
                    saveAlleleValues(datasetId, markerId, row.getAlleleValues());
                }
            }
            
            trans.commit();
            return true;    
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while setting SSR: setSSR(): " + e.getMessage(), e, LOG);
            return false;
        } finally {
            session.flush();
        }
    }

    @Override
    public Boolean setSNP(Dataset dataset, DatasetUsers datasetUser, List<SNPDataRow> rows) throws MiddlewareQueryException{
        Session session = requireLocalDatabaseInstance();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            
            Integer datasetId = saveDataset(dataset, TYPE_SNP, DATA_TYPE_INT);
            dataset.setDatasetId(datasetId);
            saveDatasetUser(datasetId, datasetUser);
            
            // Save data rows
            if (rows != null && rows.size() > 0){
                for (SNPDataRow row : rows){
                    Marker marker = row.getMarker();
                    marker.setMarkerType(TYPE_SNP);
                    Integer markerId = saveMarker(marker, TYPE_SNP);
                    marker.setMarkerId(markerId);
                    
                    saveAccMetadataSet(datasetId, row.getAccMetadataSet());
                    saveMarkerMetadataSet(datasetId, markerId, row.getMarkerMetadataSet());
                    saveCharValues(datasetId, markerId, row.getCharValues());
                }
            }
            
            trans.commit();
            return true;
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while setting SNP: setSNP(): " + e.getMessage(), e, LOG);
            return false;
        } finally {
            session.flush();
        }
    }

    @Override
    public Boolean setMappingABH(Dataset dataset, DatasetUsers datasetUser, MappingPop mappingPop, List<MappingABHRow> rows) 
            throws MiddlewareQueryException {
        Session session = requireLocalDatabaseInstance();
        Transaction trans = null;
        
        try {
            trans = session.beginTransaction();
            Integer datasetId = saveMappingData(dataset, datasetUser, mappingPop);

            // Save data rows
            if (rows != null && rows.size() > 0){
                for (MappingABHRow row : rows){
                    Marker marker = row.getMarker();
                    marker.setMarkerType(TYPE_MAPPING);
                    Integer markerId = saveMarker(marker, TYPE_MAPPING);
                    marker.setMarkerId(markerId);
                    
                    saveAccMetadataSet(datasetId, row.getAccMetadataSet());
                    saveMarkerMetadataSet(datasetId, markerId, row.getMarkerMetadataSet());
                    saveMappingPopValues(datasetId, markerId, row.getMappingPopValues());
                }  
            }
            
            trans.commit();
            return true;
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while setting Mapping Data: setMappingABH(): " + e.getMessage(), e, LOG);
            return false;
        } finally {
            session.flush();
        }
    }
    
    @Override
    public Boolean setMappingAllelicSNP(Dataset dataset, DatasetUsers datasetUser, MappingPop mappingPop, List<MappingAllelicSNPRow> rows) 
            throws MiddlewareQueryException{
        Session session = requireLocalDatabaseInstance();
        Transaction trans = null;
        
        try {
            trans = session.beginTransaction();
            Integer datasetId = saveMappingData(dataset, datasetUser, mappingPop);
            
            // Save data rows
            if (rows != null && rows.size() > 0){
                for (MappingAllelicSNPRow row : rows){
                    Marker marker = row.getMarker();
                    marker.setMarkerType(TYPE_MAPPING);
                    Integer markerId = saveMarker(marker, TYPE_MAPPING);
                    marker.setMarkerId(markerId);
                    
                    saveAccMetadataSet(datasetId, row.getAccMetadataSet());
                    saveMarkerMetadataSet(datasetId, markerId, row.getMarkerMetadataSet());
                    saveMappingPopValues(datasetId, markerId, row.getMappingPopValues());
                    saveCharValues(datasetId, markerId, row.getCharValues());
                }           
            }
            
            trans.commit();
            return true;
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while setting Mapping Data: setMappingAllelicSNP(): " + e.getMessage(), e, LOG);
            return false;
        } finally {
            session.flush();
        }    
    }

    @Override
    public Boolean setMappingAllelicSSRDArT(Dataset dataset, DatasetUsers datasetUser,  MappingPop mappingPop, 
            List<MappingAllelicSSRDArTRow> rows) throws MiddlewareQueryException {
        Session session = requireLocalDatabaseInstance();
        Transaction trans = null;
        
        try {
            trans = session.beginTransaction();
            Integer datasetId = saveMappingData(dataset, datasetUser, mappingPop);
            
            // Save data rows
            if (rows != null && rows.size() > 0){
                for (MappingAllelicSSRDArTRow row : rows){
                    
                    Marker marker = row.getMarker();
                    marker.setMarkerType(TYPE_MAPPING);
                    Integer markerId = saveMarker(marker, TYPE_MAPPING);
                    marker.setMarkerId(markerId);

                    saveAccMetadataSet(datasetId, row.getAccMetadataSet());
                    saveMarkerMetadataSet(datasetId, markerId, row.getMarkerMetadataSet());
                    saveMappingPopValues(datasetId, markerId, row.getMappingPopValues());
                    saveAlleleValues(datasetId, markerId, row.getAlleleValues());
                    saveDartValues(datasetId, markerId, row.getDartValues());
                }    
            }
            
            trans.commit();
            return true;
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while setting Mapping Data: setMappingAllelicSSRDArT(): " + e.getMessage(), e, LOG);
            return false;
        } finally {
            session.flush();
        }   
    }
    
    // Returns the datasetId
    private Integer saveMappingData(Dataset dataset, DatasetUsers datasetUser, MappingPop mappingPop) throws Exception {

        Integer datasetId = saveDataset(dataset, TYPE_MAPPING, DATA_TYPE_MAP);
        dataset.setDatasetId(datasetId);
        saveDatasetUser(datasetId, datasetUser);
        saveMappingPop(datasetId, mappingPop);
        
        return datasetId;
    }

    @Override
    public Boolean setMaps(Marker marker, MarkerOnMap markerOnMap, Map map) throws MiddlewareQueryException {
        Session session = requireLocalDatabaseInstance();
        Transaction trans = null;
        try {
            trans = session.beginTransaction();
            Integer markerSavedId = saveMarker(marker, TYPE_UA);
            Integer mapSavedId = saveMap(map);
            saveMarkerOnMap(markerSavedId, mapSavedId, markerOnMap);
            trans.commit();
            return true;
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while setting Maps: setMaps(): " + e.getMessage(), e, LOG);
            return false;
        } finally {
            session.flush();
        }
    }
    
    private Integer getMarkerIdByMarkerName(String markerName) throws MiddlewareQueryException, MiddlewareException {
        
        setWorkingDatabase(Database.CENTRAL);
        Integer markerId = getMarkerDao().getIdByName(markerName);

        if (markerId == null) {
            setWorkingDatabase(Database.LOCAL);
            markerId = getMarkerDao().getIdByName(markerName);
        }

        return markerId;        
    }
    
    private Integer getMapIdByMapName(String mapName) throws MiddlewareQueryException {
    	Integer mapId = null;
    	setWorkingDatabase(Database.CENTRAL);
    	mapId = getMapDao().getMapIdByName(mapName);
    	if (mapId == null) {
    		setWorkingDatabase(Database.LOCAL);
    		mapId = getMapDao().getMapIdByName(mapName);
    	}
    	return mapId;
    }
    
    @Override
    public List<QtlDataElement> getQtlDataByQtlTraits(List<Integer> qtlTraitIds, int start, int numOfRows) throws MiddlewareQueryException{
        List<String> methods = Arrays.asList("countQtlDataByQtlTraits", "getQtlDataByQtlTraits");
        return (List<QtlDataElement>) super.getFromCentralAndLocalByMethod(getQtlDetailsDao(), methods, start, numOfRows, new Object[] { qtlTraitIds },
                new Class[] { List.class });
    }

    @Override
    public long countQtlDataByQtlTraits(List<Integer> qtlTraits) throws MiddlewareQueryException{
    	return super.countAllFromCentralAndLocalByMethod(getQtlDetailsDao(), "countQtlDataByQtlTraits", new Object[] { qtlTraits },
                new Class[] { List.class });    	
    }
    
    @Override
    public long countNidsFromAccMetadatasetByDatasetIds(List<Integer> datasetIds) throws MiddlewareQueryException {
    	return super.countAllFromCentralAndLocalByMethod(getAccMetadataSetDao(), "countNidsByDatasetIds",
    			new Object[] {datasetIds}, new Class[] {List.class});
    }
    
    @Override
    public long countMarkersFromMarkerMetadatasetByDatasetIds(List<Integer> datasetIds) throws MiddlewareQueryException {
        return super.countAllFromCentralAndLocalByMethod(getMarkerMetadataSetDao(), "countByDatasetIds",
                new Object[] {datasetIds}, new Class[] {List.class});
    }
    
    @Override
    public Integer getMapIdByName(String mapName) throws MiddlewareQueryException {
    	setWorkingDatabase(Database.CENTRAL);
    	Integer mapId = getMapDao().getMapIdByName(mapName);
    	if (mapId == null) {
    		setWorkingDatabase(Database.LOCAL);
    		mapId = getMapDao().getMapIdByName(mapName);
    	}
    	return mapId;
    }
    
    @Override
    public long countMappingPopValuesByGids(List<Integer> gIds) throws MiddlewareQueryException{
        return super.countAllFromCentralAndLocalByMethod(this.getMappingPopValuesDao(), "countByGids", new Object[] { gIds },
                new Class[] { List.class });
    }
    
    @Override
    public long countMappingAlleleValuesByGids(List<Integer> gIds) throws MiddlewareQueryException{
        return super.countAllFromCentralAndLocalByMethod(this.getAlleleValuesDao(), "countByGids", new Object[] { gIds },
                new Class[] { List.class });
    }
    
    @Override
    public List<MarkerMetadataSet> getAllFromMarkerMetadatasetByMarker(Integer markerId) throws MiddlewareQueryException{
        return (List<MarkerMetadataSet>) super.getAllFromCentralAndLocalByMethod(
                                                    getMarkerMetadataSetDao(), 
                                                    "getByMarkerId", new Object[] { markerId },
                                                    new Class[] { Integer.class });
    }

    @Override
    public Dataset getDatasetById(Integer datasetId) throws MiddlewareQueryException{
    	setWorkingDatabase(Database.CENTRAL);
    	Dataset dataset = getDatasetDao().getById(datasetId);
    	
    	if (dataset == null) {
    		setWorkingDatabase(Database.LOCAL);
    		dataset = getDatasetDao().getById(datasetId);
    	}
    	
    	return dataset;
    }
    
    private Dataset getDatasetByName(String datasetName) throws MiddlewareQueryException{

        setWorkingDatabase(Database.CENTRAL);
        Dataset dataset = getDatasetDao().getByName(datasetName);
        if (dataset == null){
            setWorkingDatabase(Database.LOCAL);
            dataset = getDatasetDao().getByName(datasetName);
        }
        
        return dataset;
    }
    
    @Override
    public List<Dataset> getDatasetDetailsByDatasetIds(List<Integer> datasetIds) throws MiddlewareQueryException{
        return super.getFromInstanceByIdAndMethod(getDatasetDao(), datasetIds.get(0), 
                "getDatasetsByIds", new Object[] { datasetIds }, new Class[] { List.class });
    }
    
    @Override
    public List<Integer> getQTLIdsByDatasetIds(List<Integer> datasetIds) throws MiddlewareQueryException{
        return super.getFromInstanceByIdAndMethod(getQtlDao(), datasetIds.get(0), 
                "getQTLIdsByDatasetIds", new Object[] { datasetIds }, new Class[] { List.class });
    }

    @Override
    public List<AccMetadataSetPK> getAllFromAccMetadataset(List<Integer> gIds,
            Integer datasetId, SetOperation operation) throws MiddlewareQueryException {
        return (List<AccMetadataSetPK>) super.getAllFromCentralAndLocalByMethod(
                getAccMetadataSetDao(), "getAccMetadataSetByGidsAndDatasetId", new Object[] { gIds,  datasetId, operation},
                new Class[] { List.class,  Integer.class, SetOperation.class});
    }

    @Override
    public List<MapDetailElement> getMapAndMarkerCountByMarkers(List<Integer> markerIds) throws MiddlewareQueryException {
    	return super.getAllFromCentralAndLocalByMethod(getMapDao(), "getMapAndMarkerCountByMarkers", 
    			new Object[] {markerIds}, new Class[] {List.class});
    }

    @Override
    public List<Mta> getAllMTAs() throws MiddlewareQueryException{
        return super.getAllFromCentralAndLocal(getMtaDao());
    }

    @Override
    public long countAllMTAs() throws MiddlewareQueryException{
        return super.countAllFromCentralAndLocalByMethod(getMtaDao(), "countAll", new Object[]{}, new Class[]{});
    }

    @Override
    public List<Mta> getMTAsByTrait(Integer traitId) throws MiddlewareQueryException{
        return super.getFromInstanceByIdAndMethod(getMtaDao(), traitId,
                "getMtasByTrait", new Object[] { traitId },
                new Class[] { Integer.class });
    }

    @Override
    public void deleteQTLs(List<Integer> qtlIds, Integer datasetId) throws MiddlewareQueryException {
        Session session = requireLocalDatabaseInstance();
        Transaction trans = null;
        
        try {
            trans = session.beginTransaction();
            
            //delete qtl and qtl details
            getQtlDetailsDao().deleteByQtlIds(qtlIds);
            getQtlDao().deleteByQtlIds(qtlIds);
            
            //delete dataset users and dataset
            getDatasetUsersDao().deleteByDatasetId(datasetId);
            getDatasetDao().deleteByDatasetId(datasetId);            
            
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Cannot delete QTLs and Dataset: GenotypicDataManager.deleteQTLs(qtlIds="
                    + qtlIds + " and datasetId = " + datasetId + "):  " + e.getMessage(), e);
        }
    }

	@Override
	public void deleteSSRGenotypingDatasets(Integer datasetId) throws MiddlewareQueryException {
	    Session session = requireLocalDatabaseInstance();
        Transaction trans = null;
        
        try {
            trans = session.beginTransaction();
            getAlleleValuesDao().deleteByDatasetId(datasetId);
            getDatasetUsersDao().deleteByDatasetId(datasetId);
            getAccMetadataSetDao().deleteByDatasetId(datasetId);
            getMarkerMetadataSetDao().deleteByDatasetId(datasetId);
            getDatasetDao().deleteByDatasetId(datasetId);            
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Cannot delete SSR Genotyping Datasets: " +
            		"GenotypicDataManager.deleteSSRGenotypingDatasets(datasetId = " + datasetId + "):  " 
            		+ e.getMessage(), e);
        }
	}
	
   @Override
    public void deleteSNPGenotypingDatasets(Integer datasetId) throws MiddlewareQueryException {
        Session session = requireLocalDatabaseInstance();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            getCharValuesDao().deleteByDatasetId(datasetId);
            getDatasetUsersDao().deleteByDatasetId(datasetId);
            getAccMetadataSetDao().deleteByDatasetId(datasetId);
            getMarkerMetadataSetDao().deleteByDatasetId(datasetId);
            getDatasetDao().deleteByDatasetId(datasetId);
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException(
                    "Cannot delete SNP Genotyping Datasets: "
                            + "GenotypicDataManager.deleteSNPGenotypingDatasets(datasetId = " + datasetId + "):  "
                            + e.getMessage(), e);
        }
    }
   
   @Override
   public void deleteDArTGenotypingDatasets(Integer datasetId) throws MiddlewareQueryException {
       Session session = requireLocalDatabaseInstance();
       Transaction trans = null;
       
       try {
           trans = session.beginTransaction();
           getAlleleValuesDao().deleteByDatasetId(datasetId);
           getDartValuesDao().deleteByDatasetId(datasetId);
           getDatasetUsersDao().deleteByDatasetId(datasetId);
           getAccMetadataSetDao().deleteByDatasetId(datasetId);
           getMarkerMetadataSetDao().deleteByDatasetId(datasetId);
           getDatasetDao().deleteByDatasetId(datasetId);            
           trans.commit();
       } catch (Exception e) {
           rollbackTransaction(trans);
           logAndThrowException("Cannot delete DArT Genotyping Datasets: " +
                   "GenotypicDataManager.deleteDArTGenotypingDatasets(datasetId = " + datasetId + "):  " 
                   + e.getMessage(), e);
       }
   }
   
   @Override
   public void deleteMappingPopulationDatasets(Integer datasetId) throws MiddlewareQueryException {
       Session session = requireLocalDatabaseInstance();
       Transaction trans = null;
       
       try {
           trans = session.beginTransaction();
           getMappingPopValuesDao().deleteByDatasetId(datasetId);
           getMappingPopDao().deleteByDatasetId(datasetId);
           getDatasetUsersDao().deleteByDatasetId(datasetId);
           getAccMetadataSetDao().deleteByDatasetId(datasetId);
           getMarkerMetadataSetDao().deleteByDatasetId(datasetId);
           getDatasetDao().deleteByDatasetId(datasetId);            
           trans.commit();
       } catch (Exception e) {
           rollbackTransaction(trans);
           logAndThrowException("Cannot delete Mapping Population Datasets: " +
                   "GenotypicDataManager.deleteMappingPopulationDatasets(datasetId = " + datasetId + "):  " 
                   + e.getMessage(), e);
       }
   }
   
   @Override
   public List<QtlDetails> getQtlDetailsByMapId(Integer mapId) throws MiddlewareQueryException {
           return super.getAllFromCentralAndLocalByMethod(getQtlDetailsDao(),  "getQtlDetailsByMapId"
                   , new Object[] {mapId}, new Class[] {Integer.class});
   }

   @Override
   public long countQtlDetailsByMapId(Integer mapId) throws MiddlewareQueryException {
           return countFromInstanceByIdAndMethod(getQtlDetailsDao(), mapId, "countQtlDetailsByMapId", new Object[] {mapId}, new Class[] {Integer.class});
   }
   
   @Override
   public void deleteMaps(Integer mapId) throws MiddlewareQueryException {
       Session session = requireLocalDatabaseInstance();
       Transaction trans = null;
       
       try {
           trans = session.beginTransaction();
           
           getMarkerOnMapDao().deleteByMapId(mapId);
           getMapDao().deleteByMapId(mapId);
           
           trans.commit();
       } catch (Exception e) {
           rollbackTransaction(trans);
           logAndThrowException("Cannot delete Mapping Population Datasets: " +
                   "GenotypicDataManager.deleteMappingPopulationDatasets(datasetId = " + mapId + "):  " 
                   + e.getMessage(), e);
       }
   }

   @Override
   public List<Integer> getMarkerFromCharValuesByGids(List<Integer> gIds) throws MiddlewareQueryException {
       return super.getAllFromCentralAndLocalByMethod(getCharValuesDao(), "getMarkerIdsByGids", 
               new Object[] { gIds }, new Class[] { List.class });
   }
    
   @Override
   public List<Integer> getMarkerFromAlleleValuesByGids(List<Integer> gIds) throws MiddlewareQueryException {
       return super.getAllFromCentralAndLocalByMethod(getAlleleValuesDao(), "getMarkerIdsByGids", 
               new Object[] { gIds }, new Class[] { List.class });
   }
    
   @Override
   public List<Integer> getMarkerFromMappingPopByGids(List<Integer> gIds) throws MiddlewareQueryException {
       return super.getAllFromCentralAndLocalByMethod(getMappingPopValuesDao(), "getMarkerIdsByGids", 
               new Object[] { gIds }, new Class[] { List.class });
   }

	@Override
	public long getLastId(Database instance, GdmsTable gdmsTable) throws MiddlewareQueryException {
		setWorkingDatabase(instance);
		return GenericDAO.getLastId(getActiveSession(), instance, gdmsTable.getTableName(), gdmsTable.getIdName());
	}

	@Override
	public void addMTA(Dataset dataset, Mta mta, DatasetUsers users) throws MiddlewareQueryException {
	   Session session = requireLocalDatabaseInstance();
       Transaction trans = null;
       
       try {
           trans = session.beginTransaction();
           
           dataset.setDatasetId(getDatasetDao().getNegativeId("datasetId"));
           dataset.setDatasetType(TYPE_MTA);
           dataset.setUploadTemplateDate(new Date());
           getDatasetDao().save(dataset);
           
           users.setDatasetId(dataset.getDatasetId());
           getDatasetUsersDao().save(users);
           
           mta.setMtaId(getMtaDao().getNegativeId("mtaId"));
           mta.setDatasetId(dataset.getDatasetId());
           getMtaDao().save(mta);
           
           trans.commit();
       } catch (Exception e) {
           rollbackTransaction(trans);
           logAndThrowException("Error in GenotypicDataManager.addMTA: " + e.getMessage(), e);
       }
	}
	

    // --------------------------------- COMMON SAVER METHODS ------------------------------------------//
    
	private Integer saveDataset(Dataset dataset, String datasetType, String dataType) throws Exception{
        requireLocalDatabaseInstance();

	    // If the dataset has same dataset name existing in the database (local and central) - should throw an error.
        if (getDatasetByName(dataset.getDatasetName()) != null){
            throw new MiddlewareQueryException(
                    "Dataset already exists. Please specify a new GDMS dataset record with a different name.");
        }
        

        // If the dataset is not yet existing in the database (local and central) - should create a new dataset in the local database.
        Integer datasetId = dataset.getDatasetId();
        if (datasetId == null) {
            requireLocalDatabaseInstance();
            DatasetDAO datasetDao = getDatasetDao();
            Integer datasetGeneratedId = datasetDao.getNegativeId("datasetId");
            dataset.setDatasetId(datasetGeneratedId);

            dataset.setDatasetType(datasetType);
            
            if (!datasetType.equals(TYPE_QTL)){
                dataset.setDataType(dataType);
            }

            Dataset datasetRecordSaved = datasetDao.saveOrUpdate(dataset);
            datasetId = datasetRecordSaved.getDatasetId();

        }
        
        if (datasetId == null) {
            throw new Exception(); // To immediately roll back and to avoid executing the other insert functions
        }
        
        return datasetId;

	}
	
	// If the marker is not yet in the database, add.
	private Integer saveMarker(Marker marker, String markerType) throws Exception{
        requireLocalDatabaseInstance();

        // If the marker has same marker name existing in the database (local and central) - use the existing record.
        Integer markerId = marker.getMarkerId();
        if (markerId == null){
            Integer markerIdWithName = getMarkerIdByMarkerName(marker.getMarkerName());
            if (markerIdWithName != null){
                markerId = markerIdWithName;
            }
        }

        // If the marker is not yet existing in the database (local and central) - should create a new marker in the local database.
        if (markerId == null) {
            requireLocalDatabaseInstance();
            MarkerDAO markerDao = getMarkerDao();
            Integer markerGeneratedId = markerDao.getNegativeId("markerId");
            marker.setMarkerId(markerGeneratedId);
            marker.setMarkerType(markerType);
            Marker markerRecordSaved = markerDao.saveOrUpdate(marker);
            markerId = markerRecordSaved.getMarkerId();
        }
	            
        if (markerId == null) {
            throw new Exception(); // To immediately roll back and to avoid executing the other insert functions
        }

        return markerId;
	}
	
	private Integer saveMarkerAlias(MarkerAlias markerAlias) throws Exception{
        requireLocalDatabaseInstance();
        MarkerAliasDAO markerAliasDao = getMarkerAliasDao();
        MarkerAlias markerAliasRecordSaved = markerAliasDao.save(markerAlias);
        Integer markerAliasRecordSavedMarkerId = markerAliasRecordSaved.getMarkerId();
        if (markerAliasRecordSavedMarkerId == null){
            throw new Exception();
        }
        return markerAliasRecordSavedMarkerId;
	}

	private Integer saveMarkerDetails(MarkerDetails markerDetails) throws Exception{
        requireLocalDatabaseInstance();
        MarkerDetailsDAO markerDetailsDao = getMarkerDetailsDao();

        MarkerDetails markerDetailsRecordSaved = markerDetailsDao.save(markerDetails);
        Integer markerDetailsSavedMarkerId = markerDetailsRecordSaved.getMarkerId();
        if (markerDetailsSavedMarkerId == null){
            throw new Exception();
        }
        return markerDetailsSavedMarkerId;
	}

	private Integer saveMarkerUserInfo(MarkerUserInfo markerUserInfo) throws Exception {
        requireLocalDatabaseInstance();
        MarkerUserInfoDAO dao = getMarkerUserInfoDao();

        MarkerUserInfo markerUserInfoRecordSaved = dao.save(markerUserInfo);
        Integer markerUserInfoSavedId = markerUserInfoRecordSaved.getMarkerId();
        if (markerUserInfoSavedId == null){
            throw new Exception();
        }
        return markerUserInfoSavedId;
	}
	
	private Integer saveMap(Map map) throws Exception{
        requireLocalDatabaseInstance();
	    
        Integer mapSavedId = map.getMapId() == null ? getMapIdByMapName(map.getMapName()) : map.getMapId();
        if (mapSavedId == null) {
            MapDAO mapDao = getMapDao();
        
            Integer mapGeneratedId = mapDao.getNegativeId("mapId");
            map.setMapId(mapGeneratedId);

            Map mapRecordSaved = mapDao.saveOrUpdate(map);
            mapSavedId = mapRecordSaved.getMapId();
        }
        
        if (mapSavedId == null) {
            throw new Exception(); // To immediately roll back and to avoid executing the other insert functions
        }
        return mapSavedId;

	}
	
	private Integer saveMarkerOnMap(Integer markerId, Integer mapId, MarkerOnMap markerOnMap) throws Exception{

        MarkerOnMapDAO markerOnMapDao = getMarkerOnMapDao();

        // No need to generate id, MarkerOnMap(markerId, mapId) are foreign keys
        markerOnMap.setMarkerId(markerId);
        markerOnMap.setMapId(mapId);

        if (markerOnMapDao.findByMarkerIdAndMapId(markerId, mapId) != null) {
            throw new Exception("The marker on map combination already exists (markerId=" + markerId + ", mapId=" + mapId + ")" );
        }
        MarkerOnMap markerOnMapRecordSaved = markerOnMapDao.save(markerOnMap);
        Integer markerOnMapSavedId = markerOnMapRecordSaved.getMapId();

        if (markerOnMapSavedId == null) {
            throw new Exception();
        }
        return markerOnMapSavedId;
        
	}
	
	private AccMetadataSetPK saveAccMetadataSet(Integer datasetId, AccMetadataSet accMetadataSet) throws Exception{
        AccMetadataSetDAO accMetadataSetDao = getAccMetadataSetDao();
        accMetadataSet.setDatasetId(datasetId);
        
        // No need to generate id, AccMetadataSetPK(datasetId, gId, nId) are foreign keys
        AccMetadataSet accMetadataSetRecordSaved = accMetadataSetDao.merge(accMetadataSet);
        AccMetadataSetPK accMetadatasetSavedId = accMetadataSetRecordSaved.getId();

        if (accMetadatasetSavedId == null) {
            throw new Exception(); // To immediately roll back and to avoid executing the other insert functions
        }
        return accMetadatasetSavedId;
	}
	
	private MarkerMetadataSetPK saveMarkerMetadataSet(Integer datasetId, Integer markerId, MarkerMetadataSet markerMetadataSet) throws Exception{
	    requireLocalDatabaseInstance();
	    MarkerMetadataSetDAO markerMetadataSetDao = getMarkerMetadataSetDao();
	    markerMetadataSet.setDatasetId(datasetId);
	    markerMetadataSet.setMarkerId(markerId);

        // No need to generate id, MarkerMetadataSetPK(datasetId, markerId) are foreign keys
        MarkerMetadataSet markerMetadataSetRecordSaved = markerMetadataSetDao.merge(markerMetadataSet);
        MarkerMetadataSetPK markerMetadataSetSavedId = markerMetadataSetRecordSaved.getId();

        if (markerMetadataSetSavedId == null) {
            throw new Exception(); // To immediately roll back and to avoid executing the other insert functions
        }
        return markerMetadataSetSavedId;
	}
	
	private Integer saveDatasetUser(Integer datasetId, DatasetUsers datasetUser) throws Exception {
        requireLocalDatabaseInstance();
        DatasetUsersDAO datasetUserDao = getDatasetUsersDao();
        datasetUser.setDatasetId(datasetId);

        DatasetUsers datasetUserSaved = datasetUserDao.saveOrUpdate(datasetUser);
        Integer datasetUserSavedId = datasetUserSaved.getUserId();

        if (datasetUserSavedId == null) {
            throw new Exception(); // To immediately roll back and to avoid executing the other insert functions
        }
        
        return datasetUserSavedId;
        
	}
	
	private Integer saveQtl(Integer datasetId, Qtl qtl) throws Exception {
        requireLocalDatabaseInstance();
        QtlDAO qtlDao = getQtlDao();

        Integer qtlId = qtlDao.getNegativeId("qtlId");
        qtl.setQtlId(qtlId);
        qtl.setDatasetId(datasetId);

        Qtl qtlRecordSaved = qtlDao.saveOrUpdate(qtl);
        Integer qtlIdSaved = qtlRecordSaved.getQtlId();

        if (qtlIdSaved == null){
            throw new Exception();
        }

        return qtlIdSaved;
	}
	
	private QtlDetailsPK saveQtlDetails(QtlDetails qtlDetails) throws Exception {
        requireLocalDatabaseInstance();
        
        QtlDetailsDAO qtlDetailsDao = getQtlDetailsDao();
        QtlDetails qtlDetailsRecordSaved = qtlDetailsDao.saveOrUpdate(qtlDetails);
        QtlDetailsPK qtlDetailsSavedId = qtlDetailsRecordSaved.getId();

        if (qtlDetailsSavedId == null){
            throw new Exception();
        }
        
        return qtlDetailsSavedId;

	}
	
	private Integer saveCharValues(Integer datasetId, Integer markerId, CharValues charValues) throws Exception{
	    if (charValues == null){
	        return null;
	    }
        requireLocalDatabaseInstance();
        CharValuesDAO charValuesDao = getCharValuesDao();

        Integer generatedId = charValuesDao.getNegativeId("acId");
        charValues.setAcId(generatedId);
        charValues.setDatasetId(datasetId);
        charValues.setMarkerId(markerId);

        CharValues charValuesRecordSaved = charValuesDao.saveOrUpdate(charValues);
        Integer charValuesSavedId = charValuesRecordSaved.getAcId();

        if (charValuesSavedId == null) {
            throw new Exception();
        }
        return charValuesSavedId;

	}
	
	private Integer saveMappingPop(Integer datasetId, MappingPop mappingPop) throws Exception{
        requireLocalDatabaseInstance();
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
        
        return mappingPopSavedId;
	}
	
	private Integer saveMappingPopValues(Integer datasetId, Integer markerId, MappingPopValues mappingPopValues) throws Exception {
	    if (mappingPopValues == null){
	        return null;
	    }
        requireLocalDatabaseInstance();
        MappingPopValuesDAO mappingPopValuesDao = getMappingPopValuesDao();
        mappingPopValues.setDatasetId(datasetId);
        mappingPopValues.setMarkerId(markerId);

        Integer mpId = mappingPopValuesDao.getNegativeId("mpId");
        mappingPopValues.setMpId(mpId);

        MappingPopValues mappingPopValuesRecordSaved = mappingPopValuesDao.save(mappingPopValues);
        Integer mappingPopValuesSavedId = mappingPopValuesRecordSaved.getMpId();

        if (mappingPopValuesSavedId == null) {
            throw new Exception();
        }
        return mappingPopValuesSavedId;
	}
	
    private Integer saveAlleleValues(Integer datasetId, Integer markerId, AlleleValues alleleValues) throws Exception {
        if (alleleValues == null){
            return null;
        }
        requireLocalDatabaseInstance();
        alleleValues.setDatasetId(datasetId);
        alleleValues.setMarkerId(markerId);
        return saveAlleleValues(alleleValues);
    }

    private Integer saveAlleleValues(AlleleValues alleleValues) throws Exception {
        requireLocalDatabaseInstance();
        AlleleValuesDAO alleleValuesDao = getAlleleValuesDao();

        Integer alleleValuesGeneratedId = alleleValuesDao.getNegativeId("anId");
        alleleValues.setAnId(alleleValuesGeneratedId);

        AlleleValues alleleValuesRecordSaved = alleleValuesDao.save(alleleValues);
        Integer alleleValuesSavedId = alleleValuesRecordSaved.getAnId();

        if (alleleValuesSavedId == null) {
            throw new Exception();
        }
        return alleleValuesSavedId;
    }
    
    private Integer saveDartValues(Integer datasetId, Integer markerId, DartValues dartValues) throws Exception{
        
        if (dartValues == null){
            return null;
        }
        
        requireLocalDatabaseInstance();

        DartValuesDAO dartValuesDao = getDartValuesDao();
        dartValues.setDatasetId(datasetId);
        dartValues.setMarkerId(markerId);

        Integer adId = dartValuesDao.getNegativeId("adId");
        dartValues.setAdId(adId);

        DartValues dartValuesRecordSaved = dartValuesDao.save(dartValues);
        Integer dartValuesSavedId = dartValuesRecordSaved.getAdId();

        if (dartValuesSavedId == null) {
            throw new Exception();
        }
        return dartValuesSavedId;
    }

}