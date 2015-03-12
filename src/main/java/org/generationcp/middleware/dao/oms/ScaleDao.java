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
package org.generationcp.middleware.dao.oms;

import org.generationcp.middleware.dao.OntologyBaseDAO;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Scale;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.ErrorCode;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.hibernate.HibernateException;

import java.util.*;

@SuppressWarnings("unchecked")
public class ScaleDao extends OntologyBaseDAO {

    /**
     * This will fetch Scale by scaleId*
     * @param scaleId select method by scaleId
     * @return Scale
     * @throws org.generationcp.middleware.exceptions.MiddlewareQueryException
     */
    public Scale getScaleById(int scaleId) throws MiddlewareQueryException {

        try {
            List<Scale> scales = getScales(false, new ArrayList<>(Arrays.asList(scaleId)));
            if(scales.size() == 0) return null;
            return scales.get(0);
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error at getScaleById :" + e.getMessage(), ErrorCode.DATA_PROVIDER_FAILED);
        }
    }

    /**
     * This will fetch all Properties*
     * @return List<Scale>
     * @throws org.generationcp.middleware.exceptions.MiddlewareQueryException
     */
    public List<Scale> getAllScales() throws MiddlewareQueryException {

        try {
            return getScales(true, null);
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error at getAllProperties :" + e.getMessage(), ErrorCode.DATA_PROVIDER_FAILED);
        }
    }

    //TODO: Need to bind datatype, constraints and categorical values.
    /**
     * This will fetch list of properties by passing scaleIds
     * This method is private and consumed by other methods
     * @param fetchAll will tell weather query should get all properties or not.
     * @param scaleIds will tell weather scaleIds should be pass to filter result. Combination of these two will give flexible usage.
     * @return List<Scale>
     * @throws org.generationcp.middleware.exceptions.MiddlewareQueryException
     */
    private List<Scale> getScales(Boolean fetchAll, List<Integer> scaleIds) throws MiddlewareQueryException {
        Map<Integer, Scale> map = new HashMap<>();

        if(scaleIds == null) scaleIds = new ArrayList<>();

        if(!fetchAll && scaleIds.size() == 0){
            return new ArrayList<>(map.values());
        }

        try {
            List<CVTerm> terms = fetchAll ? getCvTermDao().getAllByCvId(CvId.SCALES) : getCvTermDao().getAllByCvId(scaleIds, CvId.SCALES);
            for(CVTerm s : terms){
                map.put(s.getCvTermId(), new Scale(Term.fromCVTerm(s)));
            }
        } catch (HibernateException e) {
            logAndThrowQueryException("Error at getScales", e);
            throw new MiddlewareQueryException(" :" + e.getMessage(), ErrorCode.DATA_PROVIDER_FAILED);
        }

        return new ArrayList<>(map.values());
    }
}
