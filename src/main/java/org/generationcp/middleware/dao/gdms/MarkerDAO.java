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
import org.generationcp.middleware.pojos.gdms.AlleleValues;
import org.generationcp.middleware.pojos.gdms.AllelicValueElement;
import org.generationcp.middleware.pojos.gdms.CharValues;
import org.generationcp.middleware.pojos.gdms.GermplasmMarkerElement;
import org.generationcp.middleware.pojos.gdms.MappingPopValues;
import org.generationcp.middleware.pojos.gdms.Marker;
import org.generationcp.middleware.pojos.gdms.MarkerIdMarkerNameElement;
import org.generationcp.middleware.pojos.gdms.MarkerNameElement;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;

/**
 * <b>Description</b>: DAO for Marker object.
 * 
 * <br>
 * <br>
 * 
 * <b>Authors</b>: Mark Agarrado <br>
 * <b>File Created</b>: Jul 10, 2012
 */
@SuppressWarnings("unchecked")
public class MarkerDAO extends GenericDAO<Marker, Integer>{

    /**
     * Gets the ids by names.
     *
     * @param names the names
     * @param start the start
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
            List<Integer> markerIds = query.list();
            return markerIds;
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getIdsByNames(names=" + names + ") query from Marker: " + e.getMessage(), e);
        }
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
            throw new MiddlewareQueryException("Error with getMarkerTypeByMarkerIds(markerIds=" + markerIds + ") query from Marker: " + e.getMessage(),
                    e);
        }

    }

    /**
     * Gets the marker names by gids.
     * 
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
            throw new MiddlewareQueryException("Error with getMarkerNamesByGIds(gIds=" + gIds + ") query from Marker: " + e.getMessage(), e);
        }
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
            throw new MiddlewareQueryException("Error with getGermplasmNamesByMarkerNames(markerNames=" + markerNames + ") query from Marker: "
                    + e.getMessage(), e);
        }
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
                String data = (String) result[1];
                String markerName = (String) result[2];
                AllelicValueElement allelicValueElement = new AllelicValueElement(gid, data, markerName);
                values.add(allelicValueElement);
            }
        }

        return values;
    }

    /**
     * Gets the allelic values by gids and marker names.
     *
     * @param gids the gids
     * @param markerNames the marker names
     * @return the allelic values by gids and marker names
     * @throws MiddlewareQueryException the MiddlewareQueryException
     */
    @SuppressWarnings("rawtypes")
    public List<AllelicValueElement> getAllelicValuesByGidsAndMarkerNames(List<Integer> gids, List<String> markerNames)
            throws MiddlewareQueryException {

        List<AllelicValueElement> allelicValues = new ArrayList<AllelicValueElement>();

        if (gids == null || gids.isEmpty() || markerNames == null || markerNames.isEmpty()) {
            return allelicValues;
        }

        //Get marker_ids by marker_names
        List<Integer> markerIds = getIdsByNames(markerNames, 0, Long.valueOf(countAll()).intValue());

        if (markerIds == null || markerIds.isEmpty()) {
            return allelicValues;
        }

        try {

            //retrieve allelic values from allele_values
            SQLQuery query = getSession().createSQLQuery(AlleleValues.GET_ALLELIC_VALUES_BY_GIDS_AND_MARKER_NAMES);
            query.setParameterList("gidList", gids);
            query.setParameterList("markerIdList", markerIds);
            List results = query.list();
            allelicValues.addAll(getAllelicValueElementsFromList(results));

            //retrieve allelic values from char_values
            query = getSession().createSQLQuery(CharValues.GET_ALLELIC_VALUES_BY_GIDS_AND_MARKER_NAMES);
            query.setParameterList("gidList", gids);
            query.setParameterList("markerIdList", markerIds);
            results = query.list();
            allelicValues.addAll(getAllelicValueElementsFromList(results));

            //retrieve allelic values from mapping_pop_values
            query = getSession().createSQLQuery(MappingPopValues.GET_ALLELIC_VALUES_BY_GIDS_AND_MARKER_NAMES);
            query.setParameterList("gidList", gids);
            query.setParameterList("markerIdList", markerIds);
            results = query.list();
            allelicValues.addAll(getAllelicValueElementsFromList(results));

            return allelicValues;
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getAllelicValuesByGidsAndMarkerNames(gIds=" + gids + ", markerNames="
                    + markerNames + ")  query from Marker: " + e.getMessage(), e);
        }
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
            throw new MiddlewareQueryException("Error with getNamesByIds(markerIds" + ids + ") query from Marker: "
                    + e.getMessage(), e);
        }
    }

    /**
     * Get all marker types.
     *
     * @param start the start
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
            throw new MiddlewareQueryException("Error with getAllMarkerTypes() query from Marker: " + e.getMessage(), e);
        }
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
            throw new MiddlewareQueryException("Error with countAllMarkerTypes() query from Marker: " + e.getMessage(), e);
        }
    }

    /**
     * Gets the all marker names by marker type.
     *
     * @param markerType the marker type
     * @param start the start
     * @param numOfRows the num of rows
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
            throw new MiddlewareQueryException("Error with getMarkerNamesByMarkerType(markerType=" + markerType + ") query from Marker: "
                    + e.getMessage(), e);
        }
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
            throw new MiddlewareQueryException("Error with countMarkerNamesByMarkerType(markerType=" + markerType + ") query from Marker: "
                    + e.getMessage(), e);
        }
    }

    /**
     * Gets the all db accession ids.
     *
     * @param start the start
     * @param numOfRows the num of rows
     * @return all non-empty db accession ids
     * @throws MiddlewareQueryException 
     */
    public List<String> getAllDbAccessionIds(int start, int numOfRows) throws MiddlewareQueryException {
        try {
            SQLQuery query = getSession().createSQLQuery(Marker.GET_ALL_DB_ACCESSION_IDS);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);
            List<String> dbAccessionIds = query.list();
            return dbAccessionIds;
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getAllDbAccessionIds() query from Marker: " + e.getMessage(), e);
        }
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
            throw new MiddlewareQueryException("Error with countAllDbAccessionIds() query from Marker: " + e.getMessage(), e);
        }
    }
}
