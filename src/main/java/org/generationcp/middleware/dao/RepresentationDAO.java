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

package org.generationcp.middleware.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Representation;
import org.hibernate.HibernateException;
import org.hibernate.Query;

public class RepresentationDAO extends GenericDAO<Representation, Integer>{

    @SuppressWarnings("unchecked")
    public List<Representation> getRepresentationByEffectID(Integer effectId) throws MiddlewareQueryException {
        try {
            Query query = getSession().getNamedQuery(/*Representation.GET_REPRESENTATION_BY_EFFECT_ID*/"");
            query.setParameter("effectId", effectId);

            return (List<Representation>) query.list();
        } catch (HibernateException e) {
            logAndThrowException(
                    "Error with getRepresentationByEffectID(effectId=" + effectId + ") query from Representation: " + e.getMessage(), e);
        }
        return new ArrayList<Representation>();
    }

    @SuppressWarnings("unchecked")
    public List<Representation> getRepresentationByStudyID(Integer studyId) throws MiddlewareQueryException {
        try {
            Query query = getSession().getNamedQuery(/*Representation.GET_REPRESENTATION_BY_STUDY_ID*/"");
            query.setParameter("studyId", studyId);

            return (List<Representation>) query.list();
        } catch (HibernateException e) {
            logAndThrowException(
                    "Error with getRepresentationByStudyID(studyId=" + studyId + ") query from Representation: " + e.getMessage(), e);
        }
        return new ArrayList<Representation>();
    }

    public boolean hasValuesByNumVariateAndDataset(int variateId, int datasetId) throws MiddlewareQueryException {
        try {
            Query query = getSession().createSQLQuery(/*Representation.HAS_VALUES_BY_NUM_VARIATE_ID_AND_DATASET_ID*/"");
            query.setParameter("variatid", variateId);
            query.setParameter("represno", datasetId);
            BigInteger count = (BigInteger) query.uniqueResult();

            if (count.intValue() > 0){
                return true;
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with hasValuesByNumericVariateandDataset: " + e.getMessage(), e);
        }
        return false;
    }

    public boolean hasValuesByCharVariateAndDataset(int variateId, int datasetId) throws MiddlewareQueryException {
        try {
            Query query = getSession().createSQLQuery(/*Representation.HAS_VALUES_BY_CHAR_VARIATE_ID_AND_DATASET_ID*/"");
            query.setParameter("variatid", variateId);
            query.setParameter("represno", datasetId);
            BigInteger count = (BigInteger) query.uniqueResult();

            if (count.intValue() > 0){
                return true;
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with hasValuesByCharacterVariateandDataset: " + e.getMessage(), e);
        }
        return false;
    }

    public boolean hasValuesByNumLabelAndLabelValueAndNumVariateAndDataset(int labelId, double value, int variateId, int datasetId)
            throws MiddlewareQueryException {
        try {
            Query query = getSession().createSQLQuery(
                    /*Representation.HAS_VALUES_BY_NUM_LABEL_ID_AND_LABEL_VALUE_AND_NUM_VARIATE_ID_AND_DATASET_ID*/"");
            query.setParameter("labelid", labelId);
            query.setParameter("value", value);
            query.setParameter("variatid", variateId);
            query.setParameter("represno", datasetId);
            BigInteger count = (BigInteger) query.uniqueResult();

            if (count.intValue() > 0){
                return true;
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with hasValuesByNumLabelAndLabelValueAndNumVariateAndDataset: " + e.getMessage(), e);
        }
        return false;
    }

    public boolean hasValuesByCharLabelAndLabelValueAndNumVariateAndDataset(int labelId, String value, int variateId, int datasetId)
            throws MiddlewareQueryException {
        try {
            Query query = getSession().createSQLQuery(
                    /*Representation.HAS_VALUES_BY_CHAR_LABEL_ID_AND_LABEL_VALUE_AND_NUM_VARIATE_ID_AND_DATASET_ID*/"");
            query.setParameter("labelid", labelId);
            query.setParameter("value", value);
            query.setParameter("variatid", variateId);
            query.setParameter("represno", datasetId);
            BigInteger count = (BigInteger) query.uniqueResult();

            if (count.intValue() > 0){
                return true;
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with hasValuesByCharLabelAndLabelValueAndNumVariateAndDataset: " + e.getMessage(), e);
        }
        return false;
    }

    public boolean hasValuesByNumLabelAndLabelValueAndCharVariateAndDataset(int labelId, double value, int variateId, int datasetId)
            throws MiddlewareQueryException {
        try {
            Query query = getSession().createSQLQuery(
                    /*Representation.HAS_VALUES_BY_NUM_LABEL_ID_AND_LABEL_VALUE_AND_CHAR_VARIATE_ID_AND_DATASET_ID*/"");
            query.setParameter("labelid", labelId);
            query.setParameter("value", value);
            query.setParameter("variatid", variateId);
            query.setParameter("represno", datasetId);
            BigInteger count = (BigInteger) query.uniqueResult();

            if (count.intValue() > 0) {
                return true;
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with hasValuesByNumLabelAndLabelValueAndCharVariateAndDataset: " + e.getMessage(), e);
        }
        return false;
    }

    public boolean hasValuesByCharLabelAndLabelValueAndCharVariateAndDataset(int labelId, String value, int variateId, int datasetId)
            throws MiddlewareQueryException {
        try {
            Query query = getSession().createSQLQuery(
                    /*Representation.HAS_VALUES_BY_CHAR_LABEL_ID_AND_LABEL_VALUE_AND_CHAR_VARIATE_ID_AND_DATASET_ID*/"");
            query.setParameter("labelid", labelId);
            query.setParameter("value", value);
            query.setParameter("variatid", variateId);
            query.setParameter("represno", datasetId);
            BigInteger count = (BigInteger) query.uniqueResult();

            if (count.intValue() > 0) {
                return true;
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with hasValuesByCharLabelAndLabelValueAndCharVariateAndDataset: " + e.getMessage(), e);
        }
        return false;
    }
}
