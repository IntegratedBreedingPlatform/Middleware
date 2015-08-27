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

package org.generationcp.middleware.dao.dms;

import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.oms.TermId;
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
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("typeId", typeId));
			criteria.add(Restrictions.eq("subjectProject.projectId", subjectId));

			List list = criteria.list();
			if (list != null && !list.isEmpty()) {
				return true;
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error with isSubjectTypeExisting=" + subjectId + ", " + typeId
					+ ") query from ProjectRelationship: " + e.getMessage(), e);
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	public boolean isObjectTypeExisting(Integer objectId, Integer typeId) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("typeId", typeId));
			criteria.add(Restrictions.eq("objectProject.projectId", objectId));

			List list = criteria.list();
			if (list != null && !list.isEmpty()) {
				return true;
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error with isObjectTypeExisting=" + objectId + ", " + typeId + ") query from ProjectRelationship: "
					+ e.getMessage(), e);
		}
		return false;
	}

	public void deleteByProjectId(Integer projectId) throws MiddlewareQueryException {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("delete from ProjectRelationship ");
			sb.append("where subjectProject.projectId = " + projectId.intValue());
			sb.append(" or objectProject.projectId = " + projectId.intValue());
			Session session = this.getSession();
			Query q = session.createQuery(sb.toString());

			q.executeUpdate();
		} catch (HibernateException e) {
			this.logAndThrowException("Error with deleteByProjectId=" + projectId + ") query from ProjectRelationship: " + e.getMessage(),
					e);
		}
	}

	public void deleteChildAssociation(Integer projectId) throws MiddlewareQueryException {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("delete from ProjectRelationship ");
			sb.append("where subjectProject.projectId = " + projectId.intValue());
			Session session = this.getSession();
			Query q = session.createQuery(sb.toString());

			// fix session caching end
			q.executeUpdate();

		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with deleteChildAssociation=" + projectId + ") query from ProjectRelationship: " + e.getMessage(), e);
		}
	}

	public DmsProject getObjectBySubjectIdAndTypeId(Integer subjectId, Integer typeId) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("typeId", typeId));
			criteria.add(Restrictions.eq("subjectProject.projectId", subjectId));
			criteria.setProjection(Projections.property("objectProject"));
			return (DmsProject) criteria.uniqueResult();

		} catch (HibernateException e) {
			this.logAndThrowException("Error with getObjectBySubjectIdAndTypeId=" + subjectId + ", " + typeId
					+ ") query from ProjectRelationship: " + e.getMessage(), e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<DmsProject> getSubjectsByObjectIdAndTypeId(Integer objectId, Integer typeId) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("typeId", typeId));
			criteria.add(Restrictions.eq("objectProject.projectId", objectId));
			criteria.setProjection(Projections.property("subjectProject"));
			return criteria.list();

		} catch (HibernateException e) {
			this.logAndThrowException("Error with getObjectBySubjectIdAndTypeId=" + objectId + ", " + typeId
					+ ") query from ProjectRelationship: " + e.getMessage(), e);
		}
		return null;
	}

	public ProjectRelationship getParentFolderRelationship(int studyId) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("typeId", TermId.STUDY_HAS_FOLDER.getId()));
			criteria.add(Restrictions.eq("subjectProject.projectId", studyId));
			List<ProjectRelationship> list = criteria.list();
			if (list != null && !list.isEmpty()) {
				return list.get(0);
			}

		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with getParentFolderRelationship(" + studyId + ") query from ProjectRelationship: " + e.getMessage(), e);
		}
		return null;
	}

}
