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
    public long countByName() throws MiddlewareQueryException {
        Query query = getSession().createSQLQuery(Dataset.COUNT_BY_NAME);
        BigInteger result = (BigInteger) query.uniqueResult();
        if (result != null) {
            return result.longValue();
        }
        return 0;
    }

    /**
     * Gets the dataset names.
     *
     * @param start the start
     * @param numOfRows the num of rows
     * @return the dataset names
     * @throws MiddlewareQueryException the MiddlewareQueryException
     */
    public List<String> getDatasetNames(int start, int numOfRows) throws MiddlewareQueryException {
        try {
            SQLQuery query = getSession().createSQLQuery(Dataset.GET_DATASET_NAMES_NOT_QTL);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);
            return (List<String>) query.list();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getDatasetNames() query from Dataset: " + e.getMessage(), e);
        }
    }

    /**
     * Gets the details by name.
     *
     * @param name the name
     * @return the details by name
     * @throws MiddlewareQueryException the MiddlewareQueryException
     */
    @SuppressWarnings("rawtypes")
    public List<DatasetElement> getDetailsByName(String name) throws MiddlewareQueryException {
        SQLQuery query = getSession().createSQLQuery(Dataset.GET_DETAILS_BY_NAME);
        query.setParameter("datasetName", name);
        List<DatasetElement> dataValues = new ArrayList<DatasetElement>();

        try {
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
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getDetailsByName(datasetName=" + name + ") query from Dataset: " + e.getMessage(),
                    e);
        }
    }

}
