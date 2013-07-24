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
 * DAO class for {@link Dataset}.
 *
 * @author Joyce Avestro
 * 
 */
@SuppressWarnings("unchecked")
public class DatasetDAO extends GenericDAO<Dataset, Integer>{

    public long countByName() throws MiddlewareQueryException {
        Query query = getSession().createSQLQuery(Dataset.COUNT_BY_NAME);
        BigInteger result = (BigInteger) query.uniqueResult();
        if (result != null) {
            return result.longValue();
        }
        return 0;
    }

    public List<String> getDatasetNames(int start, int numOfRows) throws MiddlewareQueryException {
        try {
            SQLQuery query = getSession().createSQLQuery(Dataset.GET_DATASET_NAMES_NOT_QTL);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);
            return (List<String>) query.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getDatasetNames() query from Dataset: " + e.getMessage(), e);
        }
        return new ArrayList<String>();
    }

    @SuppressWarnings("rawtypes")
    public List<DatasetElement> getDetailsByName(String name) throws MiddlewareQueryException {
        List<DatasetElement> dataValues = new ArrayList<DatasetElement>();

        try {
        	if (name != null){
				SQLQuery query = getSession().createSQLQuery(
						Dataset.GET_DETAILS_BY_NAME);
				query.setParameter("datasetName", name);
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
        	}
        } catch (HibernateException e) {
            logAndThrowException("Error with getDetailsByName(datasetName=" + name + ") query from Dataset: " + e.getMessage(), e);
        }
        return dataValues;
    }

    public List<Integer> getDatasetIdsForFingerPrinting(int start, int numOfRows) throws MiddlewareQueryException {
        try {
            SQLQuery query = getSession().createSQLQuery(Dataset.GET_DATASET_ID_NOT_MAPPING_AND_NOT_QTL);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);
            return (List<Integer>) query.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getDatasetIdsForFingerPrinting() query from Dataset: " + e.getMessage(), e);
        }
        return new ArrayList<Integer>();
    }
    
    public long countDatasetIdsForFingerPrinting() throws MiddlewareQueryException {
        Query query = getSession().createSQLQuery(Dataset.COUNT_DATASET_ID_NOT_MAPPING_AND_NOT_QTL);
        BigInteger result = (BigInteger) query.uniqueResult();
        if (result != null) {
            return result.longValue();
        }
        return 0;
    }

    public List<Integer> getDatasetIdsForMapping(int start, int numOfRows) throws MiddlewareQueryException {
        try {
            SQLQuery query = getSession().createSQLQuery(Dataset.GET_DATASET_ID_BY_MAPPING_AND_NOT_QTL);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);
            return (List<Integer>) query.list();
        } catch (HibernateException e) {
        	logAndThrowException("Error with getDatasetIdsForMapping() query from Dataset: " + e.getMessage(), e);
        }
        return new ArrayList<Integer>();
    }
    
    public long countDatasetIdsForMapping() throws MiddlewareQueryException {
        Query query = getSession().createSQLQuery(Dataset.COUNT_DATASET_ID_BY_MAPPING_AND_NOT_QTL);
        BigInteger result = (BigInteger) query.uniqueResult();
        if (result != null) {
            return result.longValue();
        }
        return 0;
    }
    
    public List<String> getDatasetNamesByQtlId(Integer qtlId, int start, int numOfRows) throws MiddlewareQueryException {
        try {
        	if (qtlId != null){
	            SQLQuery query = getSession().createSQLQuery(Dataset.GET_DATASET_NAMES_BY_QTL_ID);
	            query.setParameter("qtlId", qtlId);
	            query.setFirstResult(start);
	            query.setMaxResults(numOfRows);
	            return (List<String>) query.list();
        	}
        } catch (HibernateException e) {
            logAndThrowException("Error with getDatasetNamesByQtlId() query from Dataset: " + e.getMessage(), e);
        }
        return new ArrayList<String>();

	}

	public long countDatasetNamesByQtlId(Integer qtlId)
			throws MiddlewareQueryException {
		try {
			if (qtlId != null){
				Query query = getSession().createSQLQuery(Dataset.COUNT_DATASET_NAMES_BY_QTL_ID);
				query.setParameter("qtlId", qtlId);
				BigInteger result = (BigInteger) query.uniqueResult();
				if (result != null) {
					return result.longValue();
				}
			}
		} catch (HibernateException e) {
			logAndThrowException("Error with countDatasetNamesByQtlId() query from Dataset: " + e.getMessage(), e);
		}
		return 0;
    }
}
