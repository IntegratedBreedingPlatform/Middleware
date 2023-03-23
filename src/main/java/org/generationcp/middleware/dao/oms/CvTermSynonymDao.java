/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.dao.oms;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.oms.CVTermSynonym;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link CVTermSynonym}.
 *
 */
public class CvTermSynonymDao extends GenericDAO<CVTermSynonym, Integer> {

	public CvTermSynonymDao(final Session session) {
		super(session);
	}

	@SuppressWarnings("unchecked")
	public List<CVTermSynonym> getByCvTermId(int cvTermId) throws MiddlewareQueryException {
		List<CVTermSynonym> synonyms = new ArrayList<CVTermSynonym>();
		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("cvTermId", cvTermId));
			synonyms = criteria.list();

		} catch (HibernateException e) {
			this.logAndThrowException("Error at getByCvTermId=" + cvTermId + " query on CvTermSynonymDao: " + e.getMessage(), e);
		}
		return synonyms;
	}

    public static CVTermSynonym buildCvTermSynonym(Integer cvTermId,String synonym,Integer typeId){
        CVTermSynonym cvTermSynonym = new CVTermSynonym();
        cvTermSynonym.setCvTermId(cvTermId);
        cvTermSynonym.setSynonym(synonym);
        cvTermSynonym.setTypeId(typeId);
        return cvTermSynonym;
    }
}
