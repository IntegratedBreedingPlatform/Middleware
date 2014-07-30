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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.gdms.AllelicValueElement;
import org.generationcp.middleware.pojos.gdms.AllelicValueWithMarkerIdElement;
import org.generationcp.middleware.pojos.gdms.CharValues;
import org.generationcp.middleware.pojos.gdms.MarkerSampleId;
import org.generationcp.middleware.util.StringUtil;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link CharValues}.
 *
 * @author Joyce Avestro
 * 
 */
public class CharValuesDAO extends GenericDAO<CharValues, Integer>{

    // For getMarkerNamesByGIds()
    public static final String GET_CHAR_COUNT_BY_GID = 
            "SELECT COUNT(*) " +
            "FROM gdms_char_values " +
            "WHERE gid IN (:gIdList)";

    // For getGermplasmNamesByMarkerNames()
    public static final String GET_CHAR_COUNT_BY_MARKER_ID = 
            "SELECT COUNT(*) " +
            "FROM gdms_char_values " +
            "WHERE marker_id IN (:markerIdList)";
    
    // For getGermplasmNamesByMarkerNames()
    public static final String GET_CHAR_GERMPLASM_NAME_AND_MARKER_NAME_BY_MARKER_NAMES = 
            "SELECT n.nval, CONCAT(m.marker_name, '') " +  
            "FROM names n JOIN gdms_char_values c ON n.gid = c.gid " +  
            "           JOIN gdms_marker m ON c.marker_id = m.marker_id " +
            "WHERE marker_name IN (:markerNameList) AND n.nstat = 1 " +
            "ORDER BY n.nval, m.marker_name";
    
    // For getAllelicValues by gid and marker names
    public static final String GET_ALLELIC_VALUES_BY_GIDS_AND_MARKER_NAMES =
            "SELECT DISTINCT " +
                "gdms_char_values.gid, " +
                "CONCAT(gdms_char_values.char_value, ''), " +
                "CONCAT(gdms_marker.marker_name, ''), " +
                "CAST(NULL AS UNSIGNED INTEGER) " + //peak height
            "FROM gdms_char_values, " +
                "gdms_marker " +
            "WHERE gdms_char_values.marker_id = gdms_marker.marker_id " +
                "AND gdms_char_values.gid IN (:gidList) " +
                "AND gdms_char_values.marker_id IN (:markerIdList) " +
            "ORDER BY gdms_char_values.gid DESC, gdms_marker.marker_name";
    
    public static final String GET_ALLELIC_VALUES_BY_GIDS_AND_MARKER_IDS =
            "SELECT DISTINCT " +
                    "gcv.gid, " +
                    "gcv.marker_id, " +
                    "CONCAT(gcv.char_value, ''), " +
                    "CAST(NULL AS UNSIGNED INTEGER), " + //peak height
                    "gcv.marker_sample_id, " +
                    "gcv.acc_sample_id " +
            "FROM gdms_char_values gcv " +
            "WHERE gcv.gid IN (:gidList) " +
                "AND gcv.marker_id IN (:markerIdList) " +
            "ORDER BY gcv.gid DESC ";
    
    public static final String GET_ALLELIC_VALUES_BY_GID_LOCAL = 
            "SELECT DISTINCT " +
                    "gdms_char_values.gid, " +
                    "gdms_char_values.marker_id, " +
                    "CONCAT(gdms_char_values.char_value, ''), " +
                    "CAST(NULL AS UNSIGNED INTEGER) " + //peak height
            "FROM gdms_char_values " +
            "WHERE gdms_char_values.gid IN (:gidList) " +
            "ORDER BY gdms_char_values.gid ASC ";

    // For getAllelicValues by datasetId
    public static final String GET_ALLELIC_VALUES_BY_DATASET_ID = 
            "SELECT gid, marker_id,  CONCAT(char_value, '') " +
            "           , marker_sample_id, acc_sample_id " +
            "FROM gdms_char_values " +
            "WHERE dataset_id = :datasetId " +
            "ORDER BY gid ASC, marker_id ASC";

    public static final String COUNT_BY_DATASET_ID = 
            "SELECT COUNT(*) " +
            "FROM gdms_char_values " +
            "WHERE dataset_id = :datasetId";
    
    public static final String GET_GIDS_BY_MARKER_ID = 
            "SELECT DISTINCT gid " +
            "FROM gdms_char_values " +
            "WHERE marker_id = :markerId";
    
    public static final String COUNT_GIDS_BY_MARKER_ID = 
            "SELECT COUNT(distinct gid) " +
            "FROM gdms_char_values " +
            "WHERE marker_id = :markerId";

    public static final String COUNT_CHAR_VALUES_BY_GIDS = 
            "SELECT COUNT(*) " +
            "FROM gdms_char_values " +
            "WHERE gid in (:gids)";
    
    public static final String GET_MARKER_SAMPLE_IDS_BY_GIDS = 
        "SELECT DISTINCT marker_id, marker_sample_id " +
        "FROM gdms_char_values " +
        "WHERE gid IN (:gids)";
	
    public static final String GET_ALLELIC_VALUES_BY_MARKER_IDS =
    		"SELECT ac_id, dataset_id, marker_id, gid, CONCAT(char_value, ''), marker_sample_id, acc_sample_id "
    		+ "FROM gdms_char_values cv " 
    		+ "WHERE  cv.marker_id IN (:markerIdList) " 
    		+ "ORDER BY cv.gid DESC ";

    /**
     * Gets the allelic values based on the given dataset id. The result is limited by the start and numOfRows parameters.
     * 
     * @param datasetId the dataset id
     * @param start the start of the rows to retrieve
     * @param numOfRows the number of rows to retrieve
     * @return the Allelic Values (germplasm id, data value, and marker id) for the given dataset id
     * @throws MiddlewareQueryException the MiddlewareQueryException
     */
    @SuppressWarnings("rawtypes")
    public List<AllelicValueWithMarkerIdElement> getAllelicValuesByDatasetId(Integer datasetId, int start, int numOfRows)
            throws MiddlewareQueryException {
        List<AllelicValueWithMarkerIdElement> toReturn = new ArrayList<AllelicValueWithMarkerIdElement>();

        try {
            if (datasetId != null) {
	            SQLQuery query = getSession().createSQLQuery(GET_ALLELIC_VALUES_BY_DATASET_ID);
	            query.setParameter("datasetId", datasetId);
	            query.setFirstResult(start);
	            query.setMaxResults(numOfRows);
	            List results = query.list();
	
	            for (Object o : results) {
	                Object[] result = (Object[]) o;
	                if (result != null) {
	                    Integer gid = (Integer) result[0];
	                    Integer markerId = (Integer) result[1];
	                    String data = (String) result[2];
                        Integer markerSampleId = (Integer) result[3];
                        Integer accSampleId = (Integer) result[4];
	                            
	                    AllelicValueWithMarkerIdElement allelicValueElement = new AllelicValueWithMarkerIdElement(
	                            gid, data, markerId, markerSampleId, accSampleId);
	                    toReturn.add(allelicValueElement);
	                }
	            }
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with getAllelicValuesByDatasetId(datasetId=" + datasetId
                    + ") queryfrom char_values : " + e.getMessage(), e);
        }
        return toReturn;
    }

    /**
     * Count by dataset id.
     *
     * @param datasetId the dataset id
     * @return the number of entries in char_values table corresponding to the given datasetId
     * @throws MiddlewareQueryException the MiddlewareQueryException
     */
    public long countByDatasetId(Integer datasetId) throws MiddlewareQueryException {
        try {
            if (datasetId != null) {
	            Query query = getSession().createSQLQuery(COUNT_BY_DATASET_ID);
	            query.setParameter("datasetId", datasetId);
	            BigInteger result = (BigInteger) query.uniqueResult();
	            if (result != null) {
	                return result.longValue();
	            }
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with countByDatasetId(datasetId=" + datasetId + ") query from char_values: "
                    + e.getMessage(), e);
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    public List<Integer> getGIDsByMarkerId(Integer markerId, int start, int numOfRows) throws MiddlewareQueryException {

        try {
            if (markerId != null) {
	            SQLQuery query = getSession().createSQLQuery(GET_GIDS_BY_MARKER_ID);
	            query.setParameter("markerId", markerId);
	            query.setFirstResult(start);
	            query.setMaxResults(numOfRows);
	            return query.list();
	        }
        } catch (HibernateException e) {
            logAndThrowException("Error with getGIDsByMarkerId(markerId=" + markerId + ") query from CharValues: " + e.getMessage(), e);
        }
        return new ArrayList<Integer>();
    }

    public long countGIDsByMarkerId(Integer markerId) throws MiddlewareQueryException {
        try {
        	if (markerId != null){
	            SQLQuery query = getSession().createSQLQuery(COUNT_GIDS_BY_MARKER_ID);
	            query.setParameter("markerId", markerId);
	            BigInteger result = (BigInteger) query.uniqueResult();
	            if (result != null) {
	                return result.longValue();
	            }
        	}
        } catch (HibernateException e) {
            logAndThrowException("Error with countGIDsByMarkerId(markerId=" + markerId + ") query from CharValues: " + e.getMessage(), e);
        }
        return 0;
    }

    public long countCharValuesByGids(List<Integer> gids) throws MiddlewareQueryException{
        try {
        	if (gids != null && !gids.isEmpty()){
	            SQLQuery query = getSession().createSQLQuery(COUNT_CHAR_VALUES_BY_GIDS);
	            query.setParameterList("gids", gids);
	            BigInteger result = (BigInteger) query.uniqueResult();
	            if (result != null) {
	                return result.longValue();
	            }
        	}
        } catch (HibernateException e) {
            logAndThrowException("Error with countCharValuesByGids(gids=" + gids + ") query from CharValues: " + e.getMessage(), e);
        }
        return 0;
    }
    
    public void deleteByDatasetId(int datasetId) throws MiddlewareQueryException {
        try {
            this.flush();
            
            SQLQuery statement = getSession().createSQLQuery("DELETE FROM gdms_char_values WHERE dataset_id = " + datasetId);
            statement.executeUpdate();

            this.flush();
            this.clear();

        } catch(HibernateException e) {
            logAndThrowException("Error in deleteByDatasetId=" + datasetId + " in CharValuesDAO: " + e.getMessage(), e);
        }
    }
    
    @SuppressWarnings("rawtypes")
    public List<MarkerSampleId> getMarkerSampleIdsByGids(List<Integer> gIds) throws MiddlewareQueryException {
    	List<MarkerSampleId> toReturn = new ArrayList<MarkerSampleId>();

        try {
            if (gIds != null && gIds.size() > 0) {
                SQLQuery query = getSession().createSQLQuery(GET_MARKER_SAMPLE_IDS_BY_GIDS);
                query.setParameterList("gids", gIds);
                
				List results = query.list();
                for (Object o : results) {
                    Object[] result = (Object[]) o;
                    if (result != null) {
                    	Integer markerId = (Integer) result[0];
                    	Integer markerSampleId = (Integer) result[1];
                        MarkerSampleId dataElement = new MarkerSampleId(markerId, markerSampleId);
                        toReturn.add(dataElement);
                    }
                }
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with getMarkerIdsByGids(gIds=" + gIds + ") query from CharValues: " + e.getMessage(), e);
        }
        return toReturn;
    }


	@SuppressWarnings("rawtypes")
	public List<CharValues> getCharValuesByDatasetId(Integer datasetId) throws MiddlewareQueryException{
		
	    List<CharValues> toReturn = new ArrayList<CharValues>();
        try {
            if (datasetId != null){
                SQLQuery query = getSession().createSQLQuery(
                		"SELECT ac_id, dataset_id, marker_id, gid, CONCAT(char_value, ''), marker_sample_id, acc_sample_id " +
                		" FROM gdms_char_values where dataset_id = :datasetId "); 
                query.setParameter("datasetId", datasetId);

                List results = query.list();
                for (Object o : results) {
                    Object[] result = (Object[]) o;
                    if (result != null) {
                    	Integer acId = (Integer) result[0];
                        Integer datasetId2 =  (Integer) result[1];
                        Integer markerId = (Integer) result[2];
                        Integer gId = (Integer) result[3];
                        String charValue  = (String) result[4];
                        Integer markerSampleId = (Integer) result[5];
                        Integer accSampleId = (Integer) result[6];
                        
                        CharValues dataElement = new CharValues(acId, datasetId2, markerId, gId, charValue, markerSampleId, accSampleId);
                        toReturn.add(dataElement);
                    }
                }
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with getCharValuesByDatasetId(datasetId=" + datasetId + ") query from CharValues " + e.getMessage(), e);
        }
        return toReturn;
	}

	@SuppressWarnings("rawtypes")
	public List<AllelicValueElement> getAlleleValuesByMarkerId(List<Integer> markerIdList)  throws MiddlewareQueryException {
	        List<AllelicValueElement> returnVal = new ArrayList<AllelicValueElement>();

	        if (markerIdList == null || markerIdList.size() == 0) {
	            return returnVal;
	        }

	        try {
	            SQLQuery query = getSession().createSQLQuery(GET_ALLELIC_VALUES_BY_MARKER_IDS);
	            query.setParameterList("markerIdList", markerIdList);

	            List results = query.list();

	            for (Object o : results) {
	                Object[] result = (Object[]) o;
	                if (result != null) {
	                    Integer acId = (Integer) result[0];
	                    Integer datasetId = (Integer) result[1];
	                    Integer markerId = (Integer) result[2];
	                    Integer gId = (Integer) result[3];
	                    String data = (String) result[4];
	                    Integer markerSampleId = (Integer) result[5];
	                    Integer accSampleId = (Integer) result[6];
	                    AllelicValueElement value = new AllelicValueElement(acId, datasetId, gId, markerId
	                    		, data, markerSampleId, accSampleId);
	                    returnVal.add(value);
	                }
	            }
	        } catch (HibernateException e) {
	            logAndThrowException("Error with getAlleleValuesByMarkerId() query from AlleleValues: " + e.getMessage(), e);
	        }

	        return returnVal;
	    }

    @SuppressWarnings("rawtypes")
    public List<AllelicValueElement> getByMarkersAndAlleleValues(
            List<Integer> markerIdList, List<String> alleleValueList) throws MiddlewareQueryException {
        List<AllelicValueElement> values = new ArrayList<AllelicValueElement>();
        
        if (markerIdList.size() == 0 || alleleValueList.size() == 0) {
            throw new MiddlewareQueryException("markerIdList and alleleValueList must not be empty");
        }
        if (markerIdList.size() != alleleValueList.size()) {
            throw new MiddlewareQueryException("markerIdList and alleleValueList must have the same size");
        }
        
        List<String> placeholderList = new ArrayList<String>();
        for (int i=0; i < markerIdList.size(); i++) {
            placeholderList.add("(?,?)");
        }
        String placeholders = StringUtil.joinIgnoreNull(",", placeholderList);
        
        String sql = new StringBuffer()
                .append("SELECT dataset_id, gid, marker_id, CONCAT(char_value,''), marker_sample_id, acc_sample_id ")
                .append("FROM gdms_char_values ")
                .append("   WHERE (marker_id, char_value) IN (" + placeholders + ") ")
                .toString();

        try {
            SQLQuery query = getSession().createSQLQuery(sql);
            for (int i=0; i < markerIdList.size(); i++) {
                int baseIndex = i * 2;

                query.setInteger(baseIndex, markerIdList.get(i));
                query.setString(baseIndex + 1, alleleValueList.get(i));
            }
            
            List results = query.list();
            
            for (Object o : results) {
                Object[] result = (Object[]) o;
                if (result != null) {
                    Integer datasetId = (Integer) result[0];
                    Integer gid = (Integer) result[1];
                    Integer markerId = (Integer) result[2];
                    String charValue = (String) result[3];
                    Integer markerSampleId = (Integer) result[4];
                    Integer accSampleId = (Integer) result[5];
                    AllelicValueElement allelicValueElement =
                            new AllelicValueElement(null, datasetId, gid, markerId, charValue, markerSampleId, accSampleId);
                    values.add(allelicValueElement);
                }
            }
            
        } catch (HibernateException e) {
            logAndThrowException("Error with getByMarkersAndAlleleValues(markerIdList=" + markerIdList 
                    + ", alleleValueList=" + alleleValueList + "): " + e.getMessage(), e);
        }
        
        return values;
	}
    
    
	@SuppressWarnings("unchecked")
	public List<CharValues> getCharValuesByMarkerIds(List<Integer> markerIds) throws MiddlewareQueryException {
		List<CharValues> toReturn = new ArrayList<CharValues>();
		try {
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.add(Restrictions.in("markerId", markerIds));
			toReturn = criteria.list();
			
		} catch (HibernateException e) {
			logAndThrowException("Error in getCharValuesByMarkerIds=" + markerIds.toString() + " query on CharValuesDAO: " + e.getMessage(), e);
		}
		
		return toReturn;
	}

    
    
    
}
