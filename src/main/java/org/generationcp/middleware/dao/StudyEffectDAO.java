package org.generationcp.middleware.dao;

import java.util.List;

import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.pojos.StudyEffect;
import org.hibernate.HibernateException;
import org.hibernate.Query;

public class StudyEffectDAO extends GenericDAO<StudyEffect, Integer> {
    @SuppressWarnings("unchecked")
    public List<StudyEffect> getByStudyID(Integer studyId)
	    throws QueryException {
	try {
	    Query query = getSession().getNamedQuery(
		    StudyEffect.GET_STUDY_EFFECTS_BY_STUDYID);
	    query.setParameter("studyId", studyId);

	    List<StudyEffect> results = query.list();
	    return results;
	} catch (HibernateException ex) {
	    throw new QueryException(
		    "Error with get StudyEffect by Study ID query: "
			    + ex.getMessage());
	}
    }

}
