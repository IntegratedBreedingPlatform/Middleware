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
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO class for {@link CVTermRelationship}.
 *
 */
public class CVTermRelationshipDao extends GenericDAO<CVTermRelationship, Integer> {
	
	private static final Logger LOG = LoggerFactory.getLogger(CVTermRelationshipDao.class);
	private static final StringType STRING = new StringType();

	@SuppressWarnings("unchecked")
	public List<Integer> getSubjectIdsByTypeAndObject(Integer typeId, Integer objectId) {
		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("typeId", typeId));
			criteria.add(Restrictions.eq("objectId", objectId));
			criteria.setProjection(Projections.property("subjectId"));

			return criteria.list();

		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error with getSubjectIdsByTypeAndObject=" + typeId + ", " + objectId
					+ ") query from CVTermRelationship: " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getObjectIdByTypeAndSubject(Integer typeId, Integer subjectId) {
		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("typeId", typeId));
			criteria.add(Restrictions.eq("subjectId", subjectId));
			criteria.setProjection(Projections.property("objectId"));

			return criteria.list();

		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error with getObjectIdByTypeAndSubject=" + typeId + ", " + subjectId
					+ ") query from CVTermRelationship: " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<CVTermRelationship> getBySubject(int subjectId) {
		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("subjectId", subjectId));

			return criteria.list();

		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error with getBySubject=" + subjectId + " query from CVTermRelationship: " + e.getMessage(),
					e);
		}
	}

	@SuppressWarnings("unchecked")
	public CVTermRelationship getRelationshipSubjectIdObjectIdByTypeId(int subjectId, int objectId, int typeId) {
		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("typeId", typeId));
			criteria.add(Restrictions.eq("subjectId", subjectId));
			criteria.add(Restrictions.eq("objectId", objectId));

			List<CVTermRelationship> cvList = criteria.list();
			if (cvList == null || cvList.isEmpty()) {
				return null;
			} else {
				return cvList.get(0);
			}

		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error with getRelationshipSubjectIdObjectIdByTypeId=" + subjectId + ", " + objectId + ", "
					+ typeId + " query from CVTermRelationship: " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public CVTermRelationship getRelationshipBySubjectIdAndTypeId(int subjectId, int typeId) {
		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("typeId", typeId));
			criteria.add(Restrictions.eq("subjectId", subjectId));

			List<CVTermRelationship> cvList = criteria.list();
			if (cvList == null || cvList.isEmpty()) {
				return null;
			} else {
				return cvList.get(0);
			}

		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error with getRelationshipBySubjectIdAndTypeId=" + subjectId + ", " + typeId
					+ " query from CVTermRelationship: " + e.getMessage(), e);
		}
	}

	public List<CVTermRelationship> getBySubjectIdAndTypeId(int subjectId, int typeId) {

		List<CVTermRelationship> relationships = new ArrayList<>();

		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("typeId", typeId));
			criteria.add(Restrictions.eq("subjectId", subjectId));

			List cvList = criteria.list();
			for (Object r : cvList) {
				relationships.add((CVTermRelationship) r);
			}

		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error with getBySubjectIdAndTypeId=" + subjectId + ", " + typeId, e);

		}
		return relationships;
	}

	public List<CVTermRelationship> getBySubjectIdsAndTypeId(List<Integer> subjectIds, int typeId) {

		List<CVTermRelationship> relationships = new ArrayList<>();

		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("typeId", typeId));
			criteria.add(Restrictions.in("subjectId", subjectIds));

			relationships = criteria.list();
			if (relationships == null || relationships.isEmpty()) {
				return null;
			}

		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error with getBySubjectIdsAndTypeId=" + subjectIds + ", " + typeId, e);

		}
		return relationships;
	}


	public List<CVTermRelationship> getByObjectId(int objectId) {

		List<CVTermRelationship> relationships = new ArrayList<>();

		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("objectId", objectId));

			List cvList = criteria.list();
			for (Object r : cvList) {
				relationships.add((CVTermRelationship) r);
			}

		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error with getByObjectId=" + objectId, e);

		}
		return relationships;
	}

	@SuppressWarnings("unchecked")
	public CVTermRelationship getRelationshipByObjectId(int objectId) {
		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("objectId", objectId));

			List<CVTermRelationship> cvList = criteria.list();
			if (cvList == null || cvList.isEmpty()) {
				return null;
			} else {
				return cvList.get(0);
			}

		} catch (HibernateException e) {
			throw new MiddlewareQueryException(
					"Error with getRelationshipByObjectId=" + objectId + " query from CVTermRelationship: " + e.getMessage(), e);
		}
	}

	public CVTermRelationship saveOrUpdateRelationship(CVTermRelationship cvTermRelationship) {
		try {
			this.saveOrUpdate(cvTermRelationship);

		} catch (HibernateException e) {
			throw new MiddlewareQueryException(
					"Error with saveOrUpdateRelationship=" + cvTermRelationship + " query from CVTermRelationship: " + e.getMessage(), e);
		}
		return cvTermRelationship;
	}

	public boolean isTermReferred(int termId) {
		try {

			SQLQuery query =
					this.getSession().createSQLQuery("SELECT subject_id FROM cvterm_relationship where object_id = :objectId limit 1;");
			query.setParameter("objectId", termId);
			List list = query.list();
			return !list.isEmpty();
		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error with isTermReferred=" + termId + " query from CVTermRelationship: " + e.getMessage(),
					e);
		}
	}

	public CVTermRelationship save(Integer subjectId, Integer typeId, Integer objectId) {
		CVTermRelationship relationship = this.getRelationshipSubjectIdObjectIdByTypeId(subjectId, objectId, typeId);
		if (relationship != null) {
			return relationship;
		}
		CVTermRelationship cvTermRelationship = new CVTermRelationship(null, typeId, subjectId, objectId);
		return this.save(cvTermRelationship);
	}

    public Integer retrieveAnalysisDerivedVariableID(Integer originalVariableID, Integer analysisMethodTermID) {
        try {
            String sqlQuery =
                    "select cr.object_id from cvterm_relationship cr WHERE cr.type_id = " + TermId.HAS_ANALYSIS_VARIABLE.getId()
                            + " and cr.subject_id = :variableID AND EXISTS "
                            + "(SELECT 1 FROM cvterm_relationship mr WHERE cr.object_id = mr.subject_id AND mr.type_id = "
                            + TermId.HAS_METHOD.getId() + " AND mr.object_id = :methodID)";
            SQLQuery query = this.getSession().createSQLQuery(sqlQuery);
            query.setParameter("variableID", originalVariableID);
            query.setParameter("methodID", analysisMethodTermID);

            Object result = query.uniqueResult();
            if (result == null) {
                return null;
            } else {
                return (Integer) result;
            }
        } catch (HibernateException e) {
			throw new MiddlewareQueryException("Error with retrieveAnalysisDerivedVariableID=" + originalVariableID + ", "
					+ analysisMethodTermID + " query from CVTermRelationship: " + e.getMessage(), e);
        }
    }
    
	@SuppressWarnings({"unchecked"})
	public List<String> getCategoriesReferredInPhenotype(int scaleId) {
		try {

			SQLQuery query = this.getSession()
					.createSQLQuery("SELECT v.name category " +
			                " FROM cvterm_relationship scale_values "
			                + "INNER JOIN cvterm v ON v.cvterm_id = scale_values.object_id "
			                + "WHERE scale_values.subject_id = :scaleId AND scale_values.type_id = 1190 "
			                + "AND EXISTS ( SELECT 1 "
			                + " 	 FROM phenotype p "
			                + " 	INNER JOIN nd_experiment_phenotype eph on eph.phenotype_id = p.phenotype_id "
			                + "     INNER JOIN nd_experiment_project ep on ep.nd_experiment_id = eph.nd_experiment_id "
			                + "     WHERE cvalue_id = v.cvterm_id "
			                + "       AND ep.project_id not in (SELECT stat.project_id FROM projectprop stat WHERE stat.project_id = ep.project_id "
			                + "       AND stat.type_id = :studyStatusId AND value = :deletedStudy )"
			                + "   )");
			query.setParameter("scaleId", scaleId);
			query.setParameter("studyStatusId", TermId.STUDY_STATUS.getId());
			query.setParameter("deletedStudy", TermId.DELETED_STUDY.getId());
			query.addScalar("category", STRING);
			return query.list();
		} catch (HibernateException e) {
			final String message = "Error in getCategoriesReferredInPhenotype in CVTermRelationshipDao: " + e.getMessage();
			CVTermRelationshipDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);

		}
	}
    
}
