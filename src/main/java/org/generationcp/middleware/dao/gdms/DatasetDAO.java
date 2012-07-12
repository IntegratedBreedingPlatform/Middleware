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
import org.generationcp.middleware.pojos.gdms.Dataset;
import org.generationcp.middleware.pojos.gdms.DatasetElement;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;

/**
 * The Class DatasetDAO.
 * 
 * @author Joyce Avestro
 * 
 */
@SuppressWarnings("unchecked")
public class DatasetDAO extends GenericDAO<Dataset, Integer>{
    
    /**
     * Gets the count by dataset name.
     *
     * @return the int
     */
    public int countByName(){
        Query query = getSession().createSQLQuery(Dataset.COUNT_BY_NAME);
        BigInteger count = (BigInteger) query.uniqueResult();
        return count.intValue();
    }
    
    /**
     * Gets the dataset names.
     *
     * @param start the start
     * @param numOfRows the num of rows
     * @return the dataset names
     * @throws QueryException the query exception
     */
    public List<String> getDatasetNames(Integer start, Integer numOfRows) throws QueryException{
        SQLQuery query = getSession().createSQLQuery(Dataset.GET_DATASET_NAMES_NOT_QTL); 
        query.setFirstResult(start);
        query.setMaxResults(numOfRows);
        return (List<String>) query.list();
    }
    
    /**
     * Gets the details by name.
     *
     * @param name the name
     * @return the details by name
     * @throws QueryException the query exception
     */
    @SuppressWarnings("rawtypes")
    public List<DatasetElement> getDetailsByName(String name) throws QueryException{
        SQLQuery query = getSession().createSQLQuery(Dataset.GET_DETAILS_BY_NAME);        
        query.setParameter("datasetName", name);
        List<DatasetElement> dataValues = new ArrayList<DatasetElement>();
        
        try{
            List results = query.list();
        
            for (Object o : results) {
                Object[] result = (Object[]) o;
                if (result != null) {
                    Integer datasetId = (Integer) result[0];
                    String datasetType = (String) result[1];
                    DatasetElement datasetElement = new DatasetElement(datasetId, datasetType);
                    dataValues.add(datasetElement);
                }
            }
            return dataValues;        
        } catch (HibernateException ex) {
            throw new QueryException("Error with get dataset details by dataset name: " + ex.getMessage());
        }
    }
    
    


}
