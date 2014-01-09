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
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ProjectRelationship;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link ProjectRelationship}.
 */
public class ProjectRelationshipDao extends GenericDAO<ProjectRelationship, Integer> {

    @SuppressWarnings("rawtypes")
    public boolean isSubjectTypeExisting(Integer subjectId, Integer typeId) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(getPersistentClass());
            criteria.add(Restrictions.eq("typeId", typeId));
            criteria.add(Restrictions.eq("subjectProject.projectId", subjectId));

            List list = criteria.list();
            if (list != null && !list.isEmpty()) {
                return true;
            }

        } catch (HibernateException e) {
            logAndThrowException("Error with isSubjectTypeExisting=" + subjectId + ", " + typeId
                    + ") query from ProjectRelationship: " + e.getMessage(), e);
        }
        return false;
    }

    @SuppressWarnings("rawtypes")
    public boolean isObjectTypeExisting(Integer objectId, Integer typeId) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(getPersistentClass());
            criteria.add(Restrictions.eq("typeId", typeId));
            criteria.add(Restrictions.eq("objectProject.projectId", objectId));

            List list = criteria.list();
            if (list != null && !list.isEmpty()) {
                return true;
            }

        } catch (HibernateException e) {
            logAndThrowException("Error with isObjectTypeExisting=" + objectId + ", " + typeId
                    + ") query from ProjectRelationship: " + e.getMessage(), e);
        }
        return false;
    }

    public void deleteByProjectId(Integer projectId) throws MiddlewareQueryException {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("delete from ProjectRelationship ");
            sb.append("where subjectProject.projectId = " + projectId.intValue());
            sb.append(" or objectProject.projectId = " + projectId.intValue());
            Session session = getSession();
            Query q = session.createQuery(sb.toString());
            //fix session caching start - http://stackoverflow.com/questions/1954208/hibernate-transaction-problem
            session.flush();
            session.clear();
            q.executeUpdate();
        } catch (HibernateException e) {
            logAndThrowException("Error with deleteByProjectId=" + projectId +
                    ") query from ProjectRelationship: " + e.getMessage(), e);
        }
    }

    public void deleteChildAssociation(Integer projectId) throws MiddlewareQueryException {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("delete from ProjectRelationship ");
            sb.append("where subjectProject.projectId = " + projectId.intValue());
            Session session = getSession();
            Query q = session.createQuery(sb.toString());
            //fix session caching start - http://stackoverflow.com/questions/1954208/hibernate-transaction-problem
            session.flush();
            session.clear();
            //fix session caching end
            q.executeUpdate();
            
        } catch (HibernateException e) {
            logAndThrowException("Error with deleteChildAssociation=" + projectId +
                    ") query from ProjectRelationship: " + e.getMessage(), e);
        }
    }

    public DmsProject getObjectBySubjectIdAndTypeId(Integer subjectId, Integer typeId) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(getPersistentClass());
            criteria.add(Restrictions.eq("typeId", typeId));
            criteria.add(Restrictions.eq("subjectProject.projectId", subjectId));
            criteria.setProjection(Projections.property("objectProject"));
            return (DmsProject) criteria.uniqueResult();

        } catch (HibernateException e) {
            logAndThrowException("Error with getObjectBySubjectIdAndTypeId=" + subjectId + ", " + typeId
                    + ") query from ProjectRelationship: " + e.getMessage(), e);
        }
        return null;
    }

}
