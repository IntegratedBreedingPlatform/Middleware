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
package org.generationcp.middleware.dao.dms;

import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.ProjectRelationship;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link ProjectRelationship}.
 * 
 */
public class ProjectRelationshipDao extends GenericDAO<ProjectRelationship, Integer> {
	
	@SuppressWarnings("rawtypes")
	public boolean isSubjectTypeExisting(Integer subjectId, Integer typeId) throws MiddlewareQueryException {
		try {
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.add(Restrictions.eq("typeId", typeId));
			criteria.add(Restrictions.eq("subjectProject.projectId", subjectId));
			
			List list = criteria.list();
			if(list!=null && !list.isEmpty()) {
				return true;
			}
			
		} catch (HibernateException e) {
			logAndThrowException("Error with isSubjectTypeExisting=" + subjectId + ", " + typeId 
					+ ") query from ProjectRelationship: " + e.getMessage(), e);
		}
		return false;
	}
	
}
