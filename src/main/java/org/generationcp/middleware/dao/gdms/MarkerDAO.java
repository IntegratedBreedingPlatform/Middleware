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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.gdms.AlleleValues;
import org.generationcp.middleware.pojos.gdms.AllelicValueElement;
import org.generationcp.middleware.pojos.gdms.CharValues;
import org.generationcp.middleware.pojos.gdms.GermplasmMarkerElement;
import org.generationcp.middleware.pojos.gdms.MappingPopValues;
import org.generationcp.middleware.pojos.gdms.Marker;
import org.generationcp.middleware.pojos.gdms.MarkerIdMarkerNameElement;
import org.generationcp.middleware.pojos.gdms.MarkerNameElement;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link Marker}.
 * <p/>
 * <b>Authors</b>: Mark Agarrado <br>
 * <b>File Created</b>: Jul 10, 2012
 */
@SuppressWarnings("unchecked")
public class MarkerDAO extends GenericDAO<Marker, Integer> {

    /**
     * Gets the ids by names.
     *
     * @param names     the names
     * @param start     the start
     * @param numOfRows the num of rows
     * @return the ids by names
     * @throws MiddlewareQueryException the MiddlewareQueryException
     */
    public List<Integer> getIdsByNames(List<String> names, int start, int numOfRows) throws MiddlewareQueryException {

        if (names == null || names.isEmpty()) {
            return new ArrayList<Integer>();
        }

        try {
            SQLQuery query = getSession().createSQLQuery(Marker.GET_IDS_BY_NAMES);
            query.setParameterList("markerNameList", names);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);
            return (List<Integer>) query.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getIdsByNames(names=" + names + ") query from Marker: " + e.getMessage(), e);
        }
        return new ArrayList<Integer>();
    }

    public List<Marker> getByNames(List<String> names, int start, int numOfRows) throws MiddlewareQueryException {
        List<Marker> toReturn = new ArrayList<Marker>();
        if (names == null || names.isEmpty()) {
            return toReturn;
        }

        try {
            Criteria criteria = getSession().createCriteria(getPersistentClass());
            criteria.add(Restrictions.in("markerName", names));
            toReturn = criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getIdsByNames(names=" + names + ") query from Marker: " + e.getMessage(), e);
        }
        return toReturn;
    }

    public List<Marker> getByType(String markerType) throws MiddlewareQueryException {
        List<Marker> returnVal = new ArrayList<Marker>();

        try {
            Criteria criteria = getSession().createCriteria(getPersistentClass());
            criteria.add(Restrictions.eq("marker_type", markerType));
            returnVal = criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getByType(type=" + markerType + ") query from Marker: " + e.getMessage(), e);
        }

        return returnVal;
    }

    /**
     * Gets the marker_id of the first occurence of the marker_name
     *
     * @param names
     * @return
     * @throws MiddlewareQueryException
     */
    public Map<Integer, String> getFirstMarkerIdByMarkerName(List<String> names, Database instance) throws MiddlewareQueryException {
        Map<Integer, String> toReturn = new HashMap<Integer, String>();
        if (names == null || names.isEmpty()) {
            return toReturn;
        }

        try {
            SQLQuery query = getSession().createSQLQuery(Marker.GET_ID_AND_NAME_BY_NAMES);
            if (instance == Database.LOCAL) {
                query = getSession().createSQLQuery(Marker.GET_ID_AND_NAME_BY_NAMES + "DESC");
            }
            query.setParameterList("markerNameList", names);
            List<Object> results = query.list();

            for (Object o : results) {
                Object[] result = (Object[]) o;
                if (result != null) {
                    Integer id = (Integer) result[0];
                    String name = (String) result[1];

                    // Add to map if it doesn't contain the name yet. Ignore the case.
                    if (!toReturn.containsValue(name.toUpperCase())) {
                        toReturn.put(id, name.toUpperCase());
                    }
                }
            }

        } catch (HibernateException e) {
            logAndThrowException("Error with getIdAndNameByNames(names=" + names + ") query from Marker: " + e.getMessage(), e);
        }
        return toReturn;

    }

    /**
     * Gets the marker type by marker ids.
     *
     * @param markerIds the marker ids
     * @return the marker type by marker ids
     * @throws MiddlewareQueryException the MiddlewareQueryException
     */
    public List<String> getMarkerTypeByMarkerIds(List<Integer> markerIds) throws MiddlewareQueryException {

        if (markerIds == null || markerIds.isEmpty()) {
            return new ArrayList<String>();
        }

        try {

            SQLQuery query = getSession().createSQLQuery(Marker.GET_MARKER_TYPE_BY_MARKER_IDS);
            query.setParameterList("markerIdList", markerIds);
            return (List<String>) query.list();

        } catch (HibernateException e) {
            logAndThrowException("Error with getMarkerTypeByMarkerIds(markerIds=" + markerIds + ") query from Marker: " + e.getMessage(),
                    e);
        }
        return new ArrayList<String>();
    }

    /**
     * Gets the marker names by gids.
     * <p/>
     * - Searches the allele_values, char_values, mapping_pop_values tables for the existence of gids.
     * - Gets marker ids from allele_values, char_values, mapping_pop_values by gids
     * - Gets marker name from marker table by marker ids
     * - Returns marker names matching the gids
     *
     * @param gIds the g ids
     * @return the marker names by g ids
     * @throws MiddlewareQueryException the MiddlewareQueryException
     */
    @SuppressWarnings("rawtypes")
    public List<MarkerNameElement> getMarkerNamesByGIds(List<Integer> gIds) throws MiddlewareQueryException {

        // Used to store the result
        List<MarkerNameElement> dataValues = new ArrayList<MarkerNameElement>();

        if (gIds == null || gIds.isEmpty()) {
            return dataValues;
        }

        try {
            // Search the allele_values, char_values, mapping_pop_values tables for the existence of gids.
            // by getting alleleCount, charCount and mappingCount

            SQLQuery query = getSession().createSQLQuery(AlleleValues.GET_ALLELE_COUNT_BY_GID);
            query.setParameterList("gIdList", gIds);
            BigInteger alleleCount = (BigInteger) query.uniqueResult();

            query = getSession().createSQLQuery(CharValues.GET_CHAR_COUNT_BY_GID);
            query.setParameterList("gIdList", gIds);
            BigInteger charCount = (BigInteger) query.uniqueResult();

            query = getSession().createSQLQuery(MappingPopValues.GET_MAPPING_COUNT_BY_GID);
            query.setParameterList("gIdList", gIds);
            BigInteger mappingCount = (BigInteger) query.uniqueResult();

            // Retrieves markers that are being genotyped
            if (alleleCount.intValue() > 0) {
                query = getSession().createSQLQuery(Marker.GET_ALLELE_MARKER_NAMES_BY_GID);
                query.setParameterList("gIdList", gIds);
                List results = query.list();
                for (Object o : results) {
                    Object[] result = (Object[]) o;
                    if (result != null) {
                        Integer gId = (Integer) result[0];
                        String markerName = (String) result[1];
                        MarkerNameElement element = new MarkerNameElement(gId, markerName);
                        dataValues.add(element);
                    }
                }
            }

            if (charCount.intValue() > 0) {
                query = getSession().createSQLQuery(Marker.GET_CHAR_MARKER_NAMES_BY_GID);
                query.setParameterList("gIdList", gIds);
                List results = query.list();
                for (Object o : results) {
                    Object[] result = (Object[]) o;
                    if (result != null) {
                        Integer gId = (Integer) result[0];
                        String markerName = (String) result[1];
                        MarkerNameElement element = new MarkerNameElement(gId, markerName);
                        dataValues.add(element);
                    }
                }
            }

            if (mappingCount.intValue() > 0) {
                query = getSession().createSQLQuery(Marker.GET_MAPPING_MARKER_NAMES_BY_GID);
                query.setParameterList("gIdList", gIds);
                List results = query.list();
                for (Object o : results) {
                    Object[] result = (Object[]) o;
                    if (result != null) {
                        Integer gId = (Integer) result[0];
                        String markerName = (String) result[1];
                        MarkerNameElement element = new MarkerNameElement(gId, markerName);
                        dataValues.add(element);
                    }
                }
            }

            return dataValues;

        } catch (HibernateException e) {
            logAndThrowException("Error with getMarkerNamesByGIds(gIds=" + gIds + ") query from Marker: " + e.getMessage(), e);
        }
        return dataValues;
    }

    /**
     * Gets the GermplasmMarkerElement items from the given list.
     * Converts the result of SQL return values to GermplasmMarkerElement list.
     * Used by getGermplasmNamesByMarkerNames().
     *
     * @param results the results
     * @return the GermplasmMarkerElement items extracted from the list
     */
    @SuppressWarnings("rawtypes")
    private List<GermplasmMarkerElement> getGermplasmMarkerElementsFromList(List results) {
        ArrayList<GermplasmMarkerElement> dataValues = new ArrayList<GermplasmMarkerElement>();
        String prevGermplasmName = null;
        ArrayList<String> markerNameList = new ArrayList<String>();

        for (Object o : results) {
            Object[] result = (Object[]) o;
            if (result != null) {
                String germplasmName = (String) result[0];
                String markerName = (String) result[1];

                if (prevGermplasmName == null) {
                    prevGermplasmName = germplasmName;
                }

                if (germplasmName.equals(prevGermplasmName)) {
                    markerNameList.add(markerName);
                } else {
                    dataValues.add(new GermplasmMarkerElement(prevGermplasmName, markerNameList));
                    prevGermplasmName = germplasmName;
                    markerNameList = new ArrayList<String>();
                    markerNameList.add(markerName);
                }

                if (results.indexOf(result) == results.size() - 1) {
                    dataValues.add(new GermplasmMarkerElement(germplasmName, markerNameList));
                }

            }
        }

        return dataValues;
    }

    /**
     * Gets the germplasm names by marker names.
     *
     * @param markerNames the marker names
     * @return the germplasm names by marker names
     * @throws MiddlewareQueryException the MiddlewareQueryException
     */
    @SuppressWarnings("rawtypes")
    public List<GermplasmMarkerElement> getGermplasmNamesByMarkerNames(List<String> markerNames) throws MiddlewareQueryException {

        ArrayList<GermplasmMarkerElement> dataValues = new ArrayList<GermplasmMarkerElement>();

        if (markerNames == null || markerNames.isEmpty()) {
            return dataValues;
        }

        //Get marker_ids by marker_names
        List<Integer> markerIds = getIdsByNames(markerNames, 0, Long.valueOf(countAll()).intValue());

        if (markerIds.isEmpty()) {
            return dataValues;
        }

        try {
            // Search the allele_values, char_values, mapping_pop_values tables for the existence of marker_ids.
            // by getting alleleCount, charCount and mappingCount

            SQLQuery query = getSession().createSQLQuery(AlleleValues.GET_ALLELE_COUNT_BY_MARKER_ID);
            query.setParameterList("markerIdList", markerIds);
            BigInteger alleleCount = (BigInteger) query.uniqueResult();

            query = getSession().createSQLQuery(CharValues.GET_CHAR_COUNT_BY_MARKER_ID);
            query.setParameterList("markerIdList", markerIds);
            BigInteger charCount = (BigInteger) query.uniqueResult();

            query = getSession().createSQLQuery(MappingPopValues.GET_MAPPING_COUNT_BY_MARKER_ID);
            query.setParameterList("markerIdList", markerIds);
            BigInteger mappingCount = (BigInteger) query.uniqueResult();

            // Get marker name, germplasm name from allele_values given the marker names
            if (alleleCount.intValue() > 0) {
                query = getSession().createSQLQuery(AlleleValues.GET_ALLELE_GERMPLASM_NAME_AND_MARKER_NAME_BY_MARKER_NAMES);
                query.setParameterList("markerNameList", markerNames);
                List results = query.list();
                dataValues.addAll(getGermplasmMarkerElementsFromList(results));
            }

            // Get marker name, germplasm name from char_values given the marker names
            if (charCount.intValue() > 0) {
                query = getSession().createSQLQuery(CharValues.GET_CHAR_GERMPLASM_NAME_AND_MARKER_NAME_BY_MARKER_NAMES);
                query.setParameterList("markerNameList", markerNames);
                List results = query.list();
                dataValues.addAll(getGermplasmMarkerElementsFromList(results));
            }

            // Get marker name, germplasm name from mapping_pop_values given the marker names
            if (mappingCount.intValue() > 0) {
                query = getSession().createSQLQuery(MappingPopValues.GET_MAPPING_GERMPLASM_NAME_AND_MARKER_NAME_BY_MARKER_NAMES);
                query.setParameterList("markerNameList", markerNames);
                List results = query.list();
                dataValues.addAll(getGermplasmMarkerElementsFromList(results));
            }

            return dataValues;

        } catch (HibernateException e) {
            logAndThrowException("Error with getGermplasmNamesByMarkerNames(markerNames=" + markerNames + ") query from Marker: "
                    + e.getMessage(), e);
        }
        return dataValues;
    }

    /**
     * Gets the allelic value elements from list.
     *
     * @param results the results
     * @return the allelic value elements from list
     */
    @SuppressWarnings("rawtypes")
    private List<AllelicValueElement> getAllelicValueElementsFromList(List results) {
        List<AllelicValueElement> values = new ArrayList<AllelicValueElement>();

        for (Object o : results) {
            Object[] result = (Object[]) o;
            if (result != null) {
                Integer gid = (Integer) result[0];
                Integer markerId = (Integer) result[1];
                String data = (String) result[2];
                Integer peakHeight = (Integer) result[3];
                AllelicValueElement allelicValueElement = new AllelicValueElement(gid, data, markerId, null, peakHeight);
                values.add(allelicValueElement);
            }
        }

        return values;
    }

    @SuppressWarnings("rawtypes")
    private List<AllelicValueElement> getAllelicValueElementsFromListLocal(List results) {
        List<AllelicValueElement> values = new ArrayList<AllelicValueElement>();

        for (Object o : results) {
            Object[] result = (Object[]) o;
            if (result != null) {
                Integer gid = (Integer) result[0];
                Integer markerId = (Integer) result[1];
                String data = (String) result[2];
                Integer peakHeight = (Integer) result[3];
                AllelicValueElement allelicValueElement = new AllelicValueElement(gid, data, null, peakHeight);
                allelicValueElement.setMarkerId(markerId);
                values.add(allelicValueElement);
            }
        }

        return values;
    }

    /**
     * Gets the allelic values by gids and marker names.
     *
     * @param gids        the gids
     * @param markerNames the marker names
     * @return the allelic values by gids and marker names
     * @throws MiddlewareQueryException the MiddlewareQueryException
     */
    public List<AllelicValueElement> getAllelicValuesByGidsAndMarkerNames(List<Integer> gids, List<String> markerNames)
            throws MiddlewareQueryException {

        List<AllelicValueElement> allelicValues = new ArrayList<AllelicValueElement>();

        if (gids == null || gids.isEmpty() || markerNames == null || markerNames.isEmpty()) {
            return allelicValues;
        }

        //Get marker_ids by marker_names
        List<Integer> markerIds = this.getIdsByNames(markerNames, 0, Integer.MAX_VALUE);

        if (markerIds == null || markerIds.isEmpty()) {
            return allelicValues;
        }

        return getAllelicValuesByGidsAndMarkerIds(gids, markerIds);
    }


    /**
     * Gets the allelic values by gids and marker ids.
     *
     * @param gids      the gids
     * @param markerIds the marker ids
     * @return the allelic values by gids and marker ids
     * @throws MiddlewareQueryException the MiddlewareQueryException
     */
    @SuppressWarnings("rawtypes")
    public List<AllelicValueElement> getAllelicValuesByGidsAndMarkerIds(List<Integer> gids, List<Integer> markerIds)
            throws MiddlewareQueryException {

        List<AllelicValueElement> allelicValues = new ArrayList<AllelicValueElement>();

        if (gids == null || gids.isEmpty() || markerIds == null || markerIds.isEmpty()) {
            return allelicValues;
        }

        try {

            //retrieve allelic values from allele_values
            SQLQuery query = getSession().createSQLQuery(AlleleValues.GET_ALLELIC_VALUES_BY_GIDS_AND_MARKER_IDS);
            query.setParameterList("gidList", gids);
            query.setParameterList("markerIdList", markerIds);
            List results = query.list();
            allelicValues.addAll(getAllelicValueElementsFromList(results));

            //retrieve allelic values from char_values
            query = getSession().createSQLQuery(CharValues.GET_ALLELIC_VALUES_BY_GIDS_AND_MARKER_IDS);
            query.setParameterList("gidList", gids);
            query.setParameterList("markerIdList", markerIds);
            results = query.list();
            allelicValues.addAll(getAllelicValueElementsFromList(results));

            //retrieve allelic values from mapping_pop_values
            query = getSession().createSQLQuery(MappingPopValues.GET_ALLELIC_VALUES_BY_GIDS_AND_MARKER_IDS);
            query.setParameterList("gidList", gids);
            query.setParameterList("markerIdList", markerIds);
            results = query.list();
            allelicValues.addAll(getAllelicValueElementsFromList(results));

            return allelicValues;
        } catch (HibernateException e) {
            logAndThrowException("Error with getAllelicValuesByGidsAndMarkerIds(gIds=" + gids + ", markerIds="
                    + markerIds + ")  query from Marker: " + e.getMessage(), e);
        }
        return allelicValues;
    }


    @SuppressWarnings("rawtypes")
    public List<AllelicValueElement> getAllelicValuesFromLocal(List<Integer> gids) throws MiddlewareQueryException {

        List<AllelicValueElement> allelicValues = new ArrayList<AllelicValueElement>();

        if (gids == null || gids.isEmpty()) {
            return allelicValues;
        }


        try {

            //retrieve allelic values from allele_values
            SQLQuery query = getSession().createSQLQuery(AlleleValues.GET_ALLELIC_VALUES_BY_GID_LOCAL);
            query.setParameterList("gidList", gids);
            List results = query.list();
            allelicValues.addAll(getAllelicValueElementsFromListLocal(results));

            //retrieve allelic values from char_values
            query = getSession().createSQLQuery(CharValues.GET_ALLELIC_VALUES_BY_GID_LOCAL);
            query.setParameterList("gidList", gids);
            results = query.list();
            allelicValues.addAll(getAllelicValueElementsFromListLocal(results));

            //retrieve allelic values from mapping_pop_values
            query = getSession().createSQLQuery(MappingPopValues.GET_ALLELIC_VALUES_BY_GID_LOCAL);
            query.setParameterList("gidList", gids);
            results = query.list();
            allelicValues.addAll(getAllelicValueElementsFromListLocal(results));

            return allelicValues;
        } catch (HibernateException e) {
            logAndThrowException("Error with getAllelicValuesFromLocal() query from Marker: " + e.getMessage(), e);
        }
        return allelicValues;
    }


    /**
     * Get list of marker names by marker ids.
     *
     * @param ids the ids
     * @return the names by ids
     * @throws MiddlewareQueryException the MiddlewareQueryException
     */
    public List<MarkerIdMarkerNameElement> getNamesByIds(List<Integer> ids) throws MiddlewareQueryException {

        if (ids == null || ids.isEmpty()) {
            return new ArrayList<MarkerIdMarkerNameElement>();
        }

        try {
            SQLQuery query = getSession().createSQLQuery(Marker.GET_NAMES_BY_IDS);
            query.setParameterList("markerIdList", ids);

            List<MarkerIdMarkerNameElement> dataValues = new ArrayList<MarkerIdMarkerNameElement>();
            List<Object> results = query.list();

            for (Object o : results) {
                Object[] result = (Object[]) o;
                if (result != null) {
                    Integer id = (Integer) result[0];
                    String name = (String) result[1];
                    MarkerIdMarkerNameElement elem = new MarkerIdMarkerNameElement(id, name);
                    dataValues.add(elem);
                }
            }

            return dataValues;
        } catch (HibernateException e) {
            logAndThrowException("Error with getNamesByIds(markerIds" + ids + ") query from Marker: "
                    + e.getMessage(), e);
        }
        return new ArrayList<MarkerIdMarkerNameElement>();
    }

    public Map<Integer, String> getNamesByIdsMap(List<Integer> ids) throws MiddlewareQueryException {
        Map<Integer, String> dataValues = new HashMap<Integer, String>();
        if (ids == null || ids.isEmpty()) {
            return dataValues;
        }

        try {
            SQLQuery query = getSession().createSQLQuery(Marker.GET_NAMES_BY_IDS);
            query.setParameterList("markerIdList", ids);

            List<Object> results = query.list();

            for (Object o : results) {
                Object[] result = (Object[]) o;
                if (result != null) {
                    Integer id = (Integer) result[0];
                    String name = (String) result[1];
                    dataValues.put(id, name);
                }
            }

        } catch (HibernateException e) {
            logAndThrowException("Error with getNamesByIdsMap(markerIds" + ids + ") query from Marker: "
                    + e.getMessage(), e);
        }
        return dataValues;
    }

    public String getNameById(Integer markerId) throws MiddlewareQueryException {
        String name = null;

        try {
            Criteria criteria = getSession().createCriteria(getPersistentClass());
            criteria.add(Restrictions.eq("markerId", markerId));
            Marker marker = (Marker) criteria.uniqueResult();

            if (marker != null) {
                name = marker.getMarkerName();
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with getNameById query from Marker: " + e.getMessage(), e);
        }

        return name;

    }

    /**
     * Get all marker types.
     *
     * @param start     the start
     * @param numOfRows the num of rows
     * @return the all marker types
     * @throws MiddlewareQueryException the MiddlewareQueryException
     */
    public List<String> getAllMarkerTypes(int start, int numOfRows) throws MiddlewareQueryException {
        try {
            SQLQuery query = getSession().createSQLQuery(Marker.GET_ALL_MARKER_TYPES);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);

            List<String> markerTypes = query.list();

            return markerTypes;

        } catch (HibernateException e) {
            logAndThrowException("Error with getAllMarkerTypes() query from Marker: " + e.getMessage(), e);
        }
        return new ArrayList<String>();
    }

    /**
     * Count all marker types.
     *
     * @return the long
     * @throws MiddlewareQueryException the MiddlewareQueryException
     */
    public long countAllMarkerTypes() throws MiddlewareQueryException {
        try {
            SQLQuery query = getSession().createSQLQuery(Marker.COUNT_ALL_MARKER_TYPES);
            BigInteger result = (BigInteger) query.uniqueResult();
            if (result != null) {
                return result.longValue();
            } else {
                return 0L;
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with countAllMarkerTypes() query from Marker: " + e.getMessage(), e);
        }
        return 0L;
    }

    /**
     * Gets the all marker names by marker type.
     *
     * @param markerType the marker type
     * @param start      the start
     * @param numOfRows  the num of rows
     * @return the all marker names by marker type
     * @throws MiddlewareQueryException the MiddlewareQueryException
     */
    public List<String> getMarkerNamesByMarkerType(String markerType, int start, int numOfRows) throws MiddlewareQueryException {

        try {
            SQLQuery query = getSession().createSQLQuery(Marker.GET_NAMES_BY_TYPE);
            query.setParameter("markerType", markerType);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);

            List<String> markerNames = query.list();

            return markerNames;
        } catch (HibernateException e) {
            logAndThrowException("Error with getMarkerNamesByMarkerType(markerType=" + markerType + ") query from Marker: "
                    + e.getMessage(), e);
        }
        return new ArrayList<String>();
    }

    /**
     * Count marker names by marker type.
     *
     * @param markerType the marker type
     * @return the long
     * @throws MiddlewareQueryException the MiddlewareQueryException
     */
    public long countMarkerNamesByMarkerType(String markerType) throws MiddlewareQueryException {
        try {
            SQLQuery query = getSession().createSQLQuery(Marker.COUNT_MARKER_NAMES_BY_MARKER_TYPE);
            query.setParameter("markerType", markerType);
            BigInteger result = (BigInteger) query.uniqueResult();

            if (result != null) {
                return result.longValue();
            }
            return 0;
        } catch (HibernateException e) {
            logAndThrowException("Error with countMarkerNamesByMarkerType(markerType=" + markerType + ") query from Marker: "
                    + e.getMessage(), e);
        }
        return 0;
    }

    /**
     * Gets the all db accession ids.
     *
     * @param start     the start
     * @param numOfRows the num of rows
     * @return all non-empty db accession ids
     * @throws MiddlewareQueryException
     */
    public List<String> getAllDbAccessionIds(int start, int numOfRows) throws MiddlewareQueryException {
        try {
            SQLQuery query = getSession().createSQLQuery(Marker.GET_ALL_DB_ACCESSION_IDS);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);
            return (List<String>) query.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getAllDbAccessionIds() query from Marker: " + e.getMessage(), e);
        }
        return new ArrayList<String>();
    }

    /**
     * Count all db accession ids.
     *
     * @return the number of distinct db accession ids
     * @throws MiddlewareQueryException the MiddlewareQueryException
     */
    public long countAllDbAccessionIds() throws MiddlewareQueryException {
        try {
            SQLQuery query = getSession().createSQLQuery(Marker.COUNT_ALL_DB_ACCESSION_IDS);
            BigInteger result = (BigInteger) query.uniqueResult();
            if (result != null) {
                return result.longValue();
            } else {
                return 0L;
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with countAllDbAccessionIds() query from Marker: " + e.getMessage(), e);
        }
        return 0L;
    }

    @SuppressWarnings("rawtypes")
    public List<Marker> getMarkersByIds(List<Integer> markerIds, int start, int numOfRows) throws MiddlewareQueryException {
        if ((markerIds == null) || (markerIds.isEmpty())) {
            return new ArrayList<Marker>();
        }

        try {
            SQLQuery query = getSession().createSQLQuery(Marker.GET_MARKERS_BY_IDS);
            query.setParameterList("markerIdList", markerIds);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);
            List results = query.list();

            List<Marker> dataValues = new ArrayList<Marker>();
            for (Object o : results) {
                Object[] result = (Object[]) o;
                if (result != null) {
                    dataValues.add(convertToMarker(result));
                }
            }
            return dataValues;
        } catch (HibernateException e) {
            logAndThrowException("Error with getMarkersByIds() query from Marker: " + e.getMessage(), e);
        }
        return new ArrayList<Marker>();
    }

    // GCP-7874
    public List<Marker> getMarkersByHaplotype(String haplotype) throws MiddlewareQueryException {
        if (StringUtils.isEmpty(haplotype)) {
            return new ArrayList<Marker>();
        }

        try {
            SQLQuery query = getSession().createSQLQuery(Marker.GET_MARKERS_BY_IDS);
            query.setParameter("trackName", haplotype);
            List results = query.list();

            List<Marker> dataValues = new ArrayList<Marker>();
            for (Object o : results) {
                Object[] result = (Object[]) o;
                if (result != null) {
                    dataValues.add(convertToMarker(result));
                }
            }
            return dataValues;
        } catch (HibernateException e) {
            logAndThrowException("Error with getMarkersByHaplotype() query from Marker: " + e.getMessage(), e);
        }

        return new ArrayList<Marker>();
    }

    protected Marker convertToMarker(Object[] result) {

        Integer markerId = (Integer) result[0];
        String markerType = (String) result[1];
        String markerName = (String) result[2];
        String species = (String) result[3];
        String dbAccessionId = (String) result[4];
        String reference = (String) result[5];
        String genotype = (String) result[6];
        String ploidy = (String) result[7];
        String primerId = (String) result[8];
        String remarks = (String) result[9];
        String assayType = (String) result[10];
        String motif = (String) result[11];
        String forwardPrimer = (String) result[12];
        String reversePrimer = (String) result[13];
        String productSize = (String) result[14];
        Float annealingTemp = (Float) result[15];
        String amplification = (String) result[16];

        Marker element = new Marker(markerId, markerType, markerName, species, dbAccessionId, reference, genotype, ploidy,
                primerId, remarks, assayType, motif, forwardPrimer, reversePrimer, productSize, annealingTemp, amplification);

        return element;
    }

    public long countMarkersByIds(List<Integer> markerIds) throws MiddlewareQueryException {
        if ((markerIds == null) || (markerIds.isEmpty())) {
            return 0;
        }
        try {
            SQLQuery query = getSession().createSQLQuery(Marker.COUNT_MARKERS_BY_IDS);
            query.setParameterList("markerIdList", markerIds);
            BigInteger result = (BigInteger) query.uniqueResult();
            if (result != null) {
                return result.longValue();
            } else {
                return 0L;
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with countMarkersByIds() query from Marker: " + e.getMessage(), e);
        }
        return 0L;
    }

    public Set<Integer> getMarkerIDsByMapIDAndLinkageBetweenStartPosition(int mapID, String linkageGroup, double startPos, double endPos, int start, int numOfRows) throws MiddlewareQueryException {
        try {

            SQLQuery query;

            query = getSession().createSQLQuery(Marker.GET_MARKER_IDS_BY_MAP_ID_AND_LINKAGE_BETWEEN_START_POSITION);
            query.setParameter("map_id", mapID);
            query.setParameter("linkage_group", linkageGroup);
            query.setParameter("start_position", startPos);
            query.setParameter("end_position", endPos);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);
            Set<Integer> markerIDSet = new TreeSet<Integer>(query.list());

            return markerIDSet;

        } catch (HibernateException e) {
            logAndThrowException("Error with getMarkerIdsByMapIDAndLinkageBetweenStartPosition(mapID=" + mapID + ", linkageGroup=" + linkageGroup + ", start=" + start + ", numOfRows=" + numOfRows + ") query from Marker: "
                    + e.getMessage(), e);
        }
        return new TreeSet<Integer>();
    }

    public long countMarkerIDsByMapIDAndLinkageBetweenStartPosition(int mapID, String linkageGroup, double startPos, double endPos) throws MiddlewareQueryException {
        try {

            SQLQuery query;

            query = getSession().createSQLQuery(Marker.COUNT_MARKER_IDS_BY_MAP_ID_AND_LINKAGE_BETWEEN_START_POSITION);
            query.setParameter("map_id", mapID);
            query.setParameter("linkage_group", linkageGroup);
            query.setParameter("start_position", startPos);
            query.setParameter("end_position", endPos);
            BigInteger result = (BigInteger) query.uniqueResult();
            if (result != null) {
                return result.longValue();
            } else {
                return 0;
            }

        } catch (HibernateException e) {
            logAndThrowException("Error with countMarkerIdsByMapIDAndLinkageBetweenStartPosition(mapID=" + mapID + ", linkageGroup=" + linkageGroup + ") query from Marker: "
                    + e.getMessage(), e);
        }
        return 0L;
    }

    public Integer getIdByName(String name) throws MiddlewareQueryException {
        try {
            SQLQuery query = getSession().createSQLQuery(Marker.GET_ID_BY_NAME);
            query.setParameter("markerName", name);
            return (Integer) query.uniqueResult();

        } catch (HibernateException e) {
            logAndThrowException("Error with getIdByName(" + name + "): " + e.getMessage(), e);
        }

        return null;
    }
    
/*    @SuppressWarnings("rawtypes")
    public Set<Integer> getMarkersByMarkerIDs(List<Integer> markerIDs, int start, int numOfRows) throws MiddlewareQueryException{
        try {
            
            SQLQuery query;

            query = getSession().createSQLQuery(Marker.GET_MARKERS_BY_MARKER_IDS);
            query.setParameterList("map_id", markerIDs);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);
            Set<Integer> markerIDSet = new TreeSet<Integer>(query.list());

            return markerIDSet;
            
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getMarkersByMarkerIDs(mapID=" + markerIDs + ", start=" + start + ", numOfRows=" + numOfRows + ") query from Marker: "
                    + e.getMessage(), e);
        }
    }
    
    @SuppressWarnings("rawtypes")
    public long countMarkersByMarkerIDs(List<Integer> markerIDs) throws MiddlewareQueryException{
        try {
            
            SQLQuery query;

            query = getSession().createSQLQuery(Marker.COUNT_MARKERS_BY_MARKER_IDS);
            query.setParameterList("map_id", markerIDs);
            BigInteger result = (BigInteger) query.uniqueResult();
            if (result != null) {
                return result.longValue();
            } else {
                return 0;
            }
            
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with countMarkersByMarkerIDs(mapID=" + markerIDs + ") query from Marker: "
                    + e.getMessage(), e);
        }
    }*/

}
