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

import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Representation;
import org.hibernate.HibernateException;
import org.hibernate.Query;

public class RepresentationDAO extends GenericDAO<Representation, Integer>{

    @SuppressWarnings("unchecked")
    public List<Representation> getRepresentationByEffectID(Integer effectId) throws MiddlewareQueryException {
        try {
            Query query = getSession().getNamedQuery(Representation.GET_REPRESENTATION_BY_EFFECT_ID);
            query.setParameter("effectId", effectId);

            return (List<Representation>) query.list();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getRepresentationByEffectID(effectId=" + effectId
                    + ") query from Representation: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Representation> getRepresentationByStudyID(Integer studyId) throws MiddlewareQueryException {
        try {
            Query query = getSession().getNamedQuery(Representation.GET_REPRESENTATION_BY_STUDY_ID);
            query.setParameter("studyId", studyId);

            return (List<Representation>) query.list();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getRepresentationByStudyID(studyId=" + studyId + ") query from Representation: "
                    + e.getMessage(), e);
        }
    }

}
