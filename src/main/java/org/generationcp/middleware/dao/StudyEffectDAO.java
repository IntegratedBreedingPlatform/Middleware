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

import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.pojos.StudyEffect;
import org.hibernate.HibernateException;
import org.hibernate.Query;

public class StudyEffectDAO extends GenericDAO<StudyEffect, Integer>{

    @SuppressWarnings("unchecked")
    public List<StudyEffect> getByStudyID(Integer studyId) throws QueryException {
        try {
            Query query = getSession().getNamedQuery(StudyEffect.GET_STUDY_EFFECTS_BY_STUDYID);
            query.setParameter("studyId", studyId);
            return (List<StudyEffect>) query.list();
        } catch (HibernateException ex) {
            throw new QueryException("Error with get StudyEffect by Study ID query: " + ex.getMessage(), ex);
        }
    }

}
