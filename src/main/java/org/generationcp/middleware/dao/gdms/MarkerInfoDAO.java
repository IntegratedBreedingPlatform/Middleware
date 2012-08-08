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
import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.pojos.gdms.MarkerInfo;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;

/**
 * The Class MarkerInfoDAO.
 * 
 * @author Joyce Avestro
 * 
 */
@SuppressWarnings("unchecked")
public class MarkerInfoDAO extends GenericDAO<MarkerInfo, Integer>{
    
    
    /**
     * Gets the list of marker info objects corresponding to the given marker name.
     *
     * @param markerName the marker name
     * @param start the start row
     * @param numOfRows the number of rows to retrieve
     * @return the list of MarkerInfo objects by marker name
     * @throws QueryException
     */
    public List<MarkerInfo> getByMarkerName(String markerName, int start, int numOfRows) throws QueryException{
        
        if (markerName == null){
            return new ArrayList<MarkerInfo>();
        }
        
        try{
            SQLQuery query = getSession().createSQLQuery(MarkerInfo.GET_BY_MARKER_NAME);
            query.setParameter("markerName", markerName);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);
            List<MarkerInfo> results = query.list();
            
            ArrayList<MarkerInfo> toReturn = new ArrayList<MarkerInfo>();
            for (Object o : results) {
                Object[] result = (Object[]) o;
                if (result != null) {
                    Integer markerId = (Integer) result[0];
                    String markerType = (String) result[1];
                    String markerName2 = (String) result[2];
                    String species = (String) result[3];
                    String accessionId = (String) result[4];
                    String reference = (String) result[5];
                    String genotype = (String) result[6];
                    String ploidy = (String) result[7];
                    String principalInvestigator = (String) result[8];
                    String contact = (String) result[9]; 
                    String institute = (String) result[10];
                    BigInteger genotypesCount = (BigInteger) result[11];
                    MarkerInfo markerInfo = new MarkerInfo(markerId, markerType, markerName2, species, accessionId, reference,
                            genotype, ploidy, principalInvestigator, contact, institute, genotypesCount);
                    toReturn.add(markerInfo);
                }
            }
            return toReturn;
            
        } catch(HibernateException e){
            throw new QueryException("Error with get marker info by marker name query: " + e.getMessage(), e);
        }
    }

    /**
     * Count the number of entries in marker info corresponding to the given marker name.
     *
     * @param markerName the marker name
     * @return the count
     * @throws QueryException
     */
    public int countByMarkerName(String markerName) throws QueryException{
        
        if (markerName == null) {
            return 0;
        }
        
        try{
            SQLQuery query = getSession().createSQLQuery(MarkerInfo.COUNT_BY_MARKER_NAME);
            query.setParameter("markerName", markerName);
            BigInteger count = (BigInteger) query.uniqueResult();
            return count.intValue();
        } catch(HibernateException e){
            throw new QueryException("Error with count marker info by marker name query: " + e.getMessage(), e);
        }
    }


    /**
     * Gets the list of marker info objects corresponding to the given genotype.
     *
     * @param genotype the genotype
     * @param start the start row
     * @param numOfRows the number of rows to retrieve
     * @return the list of MarkerInfo objects by genotype
     * @throws QueryException
     */
    @SuppressWarnings("rawtypes")
    public List<MarkerInfo> getByGenotype(String genotype, int start, int numOfRows) throws QueryException{
        
        if (genotype == null){
            return new ArrayList<MarkerInfo>();
        }
        
        try{
            SQLQuery query = getSession().createSQLQuery(MarkerInfo.GET_BY_GENOTYPE);
            query.setParameter("genotype", genotype);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);
            List results = query.list();

            ArrayList<MarkerInfo> toReturn = new ArrayList<MarkerInfo>();
            for (Object o : results) {
                Object[] result = (Object[]) o;
                if (result != null) {
                    Integer markerId = (Integer) result[0];
                    String markerType = (String) result[1];
                    String markerName = (String) result[2];
                    String species = (String) result[3];
                    String accessionId = (String) result[4];
                    String reference = (String) result[5];
                    String genotype2 = (String) result[6];
                    String ploidy = (String) result[7];
                    String principalInvestigator = (String) result[8];
                    String contact = (String) result[9]; 
                    String institute = (String) result[10];
                    BigInteger genotypesCount = (BigInteger) result[11];
                    MarkerInfo markerInfo = new MarkerInfo(markerId, markerType, markerName, species, accessionId, reference,
                            genotype2, ploidy, principalInvestigator, contact, institute, genotypesCount);
                    toReturn.add(markerInfo);
                }
            }
            return toReturn;
            } catch(HibernateException e){
            throw new QueryException("Error with get marker info by genotype query: " + e.getMessage(), e);
        }
    }

    /**
     * Count the number of entries in marker info corresponding to the given genotype.
     *
     * @param genotype the genotype
     * @return the count
     * @throws QueryException
     */
    public int countByGenotype(String genotype) throws QueryException{
        
        if (genotype == null) {
            return 0;
        }
        
        try{
            SQLQuery query = getSession().createSQLQuery(MarkerInfo.COUNT_BY_GENOTYPE);
            query.setParameter("genotype", genotype);
            BigInteger count = (BigInteger) query.uniqueResult();
            return count.intValue();
        } catch(HibernateException e){
            throw new QueryException("Error with count marker info by genotype query: " + e.getMessage(), e);
        }
    }

    /**
     * Gets the list of marker info objects corresponding to the given db accession id.
     *
     * @param dbAccessionId the db accession id
     * @param start the start row
     * @param numOfRows the number of rows to retrieve
     * @return the list of MarkerInfo objects by db accession id
     * @throws QueryException
     */
    @SuppressWarnings("rawtypes")
    public List<MarkerInfo> getByDbAccessionId(String dbAccessionId, int start, int numOfRows) throws QueryException{
        
        if (dbAccessionId == null){
            return new ArrayList<MarkerInfo>();
        }
        
        try{
            SQLQuery query = getSession().createSQLQuery(MarkerInfo.GET_BY_DB_ACCESSION_ID);
            query.setParameter("dbAccessionId", dbAccessionId);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);
            List results = query.list();

            ArrayList<MarkerInfo> toReturn = new ArrayList<MarkerInfo>();
            for (Object o : results) {
                Object[] result = (Object[]) o;
                if (result != null) {
                    Integer markerId = (Integer) result[0];
                    String markerType = (String) result[1];
                    String markerName = (String) result[2];
                    String species = (String) result[3];
                    String accessionId = (String) result[4];
                    String reference = (String) result[5];
                    String genotype = (String) result[6];
                    String ploidy = (String) result[7];
                    String principalInvestigator = (String) result[8];
                    String contact = (String) result[9]; 
                    String institute = (String) result[10];
                    BigInteger genotypesCount = (BigInteger) result[11];
                    MarkerInfo markerInfo = new MarkerInfo(markerId, markerType, markerName, species, accessionId, reference,
                            genotype, ploidy, principalInvestigator, contact, institute, genotypesCount);
                    toReturn.add(markerInfo);
                }
            }
            return toReturn;
            } catch(HibernateException e){
            throw new QueryException("Error with get marker info by db accession id query: " + e.getMessage(), e);
        }
    }

    /**
     * Count the number of entries in marker info corresponding to the given db accession id.
     *
     * @param dbAccessionId the db accession id
     * @return the count
     * @throws QueryException
     */
    public int countByDbAccessionId(String dbAccessionId) throws QueryException{
        
        if (dbAccessionId == null) {
            return 0;
        }
        
        try{
            SQLQuery query = getSession().createSQLQuery(MarkerInfo.COUNT_BY_DB_ACCESSION_ID);
            query.setParameter("dbAccessionId", dbAccessionId);
            BigInteger count = (BigInteger) query.uniqueResult();
            return count.intValue();
        } catch(HibernateException e){
            throw new QueryException("Error with count marker info by db accession id query: " + e.getMessage(), e);
        }
    }
}
