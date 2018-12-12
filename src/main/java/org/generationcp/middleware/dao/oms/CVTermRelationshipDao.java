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
	public List<Integer> getSubjectIdsByTypeAndObject(final Integer typeId, final Integer objectId) {
		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("typeId", typeId));
			criteria.add(Restrictions.eq("objectId", objectId));
			criteria.setProjection(Projections.property("subjectId"));

			return criteria.list();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with getSubjectIdsByTypeAndObject=" + typeId + ", " + objectId
					+ ") query from CVTermRelationship: " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getObjectIdByTypeAndSubject(final Integer typeId, final Integer subjectId) {
		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("typeId", typeId));
			criteria.add(Restrictions.eq("subjectId", subjectId));
			criteria.setProjection(Projections.property("objectId"));

			return criteria.list();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with getObjectIdByTypeAndSubject=" + typeId + ", " + subjectId
					+ ") query from CVTermRelationship: " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<CVTermRelationship> getBySubject(final int subjectId) {
		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("subjectId", subjectId));

			return criteria.list();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
					"Error with getBySubject=" + subjectId + " query from CVTermRelationship: " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public CVTermRelationship getRelationshipSubjectIdObjectIdByTypeId(final int subjectId, final int objectId,
			final int typeId) {
		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("typeId", typeId));
			criteria.add(Restrictions.eq("subjectId", subjectId));
			criteria.add(Restrictions.eq("objectId", objectId));

			final List<CVTermRelationship> cvList = criteria.list();
			if (cvList == null || cvList.isEmpty()) {
				return null;
			} else {
				return cvList.get(0);
			}

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with getRelationshipSubjectIdObjectIdByTypeId=" + subjectId + ", "
					+ objectId + ", " + typeId + " query from CVTermRelationship: " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public CVTermRelationship getRelationshipBySubjectIdAndTypeId(final int subjectId, final int typeId) {
		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("typeId", typeId));
			criteria.add(Restrictions.eq("subjectId", subjectId));

			final List<CVTermRelationship> cvList = criteria.list();
			if (cvList == null || cvList.isEmpty()) {
				return null;
			} else {
				return cvList.get(0);
			}

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with getRelationshipBySubjectIdAndTypeId=" + subjectId + ", "
					+ typeId + " query from CVTermRelationship: " + e.getMessage(), e);
		}
	}

	public List<CVTermRelationship> getBySubjectIdAndTypeId(final int subjectId, final int typeId) {

		final List<CVTermRelationship> relationships = new ArrayList<>();

		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("typeId", typeId));
			criteria.add(Restrictions.eq("subjectId", subjectId));

			final List cvList = criteria.list();
			for (final Object r : cvList) {
				relationships.add((CVTermRelationship) r);
			}

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with getBySubjectIdAndTypeId=" + subjectId + ", " + typeId, e);

		}
		return relationships;
	}

	public List<CVTermRelationship> getBySubjectIdsAndTypeId(final List<Integer> subjectIds, final int typeId) {

		final List<CVTermRelationship> relationships;

		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("typeId", typeId));
			criteria.add(Restrictions.in("subjectId", subjectIds));

			relationships = criteria.list();
			if (relationships == null || relationships.isEmpty()) {
				return null;
			}

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with getBySubjectIdsAndTypeId=" + subjectIds + ", " + typeId, e);

		}
		return relationships;
	}

	public List<CVTermRelationship> getByObjectId(final int objectId) {

		final List<CVTermRelationship> relationships = new ArrayList<>();

		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("objectId", objectId));

			final List cvList = criteria.list();
			for (final Object r : cvList) {
				relationships.add((CVTermRelationship) r);
			}

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with getByObjectId=" + objectId, e);

		}
		return relationships;
	}

	@SuppressWarnings("unchecked")
	public CVTermRelationship getRelationshipByObjectId(final int objectId) {
		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("objectId", objectId));

			final List<CVTermRelationship> cvList = criteria.list();
			if (cvList == null || cvList.isEmpty()) {
				return null;
			} else {
				return cvList.get(0);
			}

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with getRelationshipByObjectId=" + objectId
					+ " query from CVTermRelationship: " + e.getMessage(), e);
		}
	}

	public CVTermRelationship saveOrUpdateRelationship(final CVTermRelationship cvTermRelationship) {
		try {
			this.saveOrUpdate(cvTermRelationship);

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with saveOrUpdateRelationship=" + cvTermRelationship
					+ " query from CVTermRelationship: " + e.getMessage(), e);
		}
		return cvTermRelationship;
	}

	public boolean isTermReferred(final int termId) {
		try {

			final SQLQuery query = this.getSession()
					.createSQLQuery("SELECT subject_id FROM cvterm_relationship where object_id = :objectId limit 1;");
			query.setParameter("objectId", termId);
			final List list = query.list();
			return !list.isEmpty();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
					"Error with isTermReferred=" + termId + " query from CVTermRelationship: " + e.getMessage(), e);
		}
	}

	public CVTermRelationship save(final Integer subjectId, final Integer typeId, final Integer objectId) {
		final CVTermRelationship relationship = this.getRelationshipSubjectIdObjectIdByTypeId(subjectId, objectId,
				typeId);
		if (relationship != null) {
			return relationship;
		}
		final CVTermRelationship cvTermRelationship = new CVTermRelationship(null, typeId, subjectId, objectId);
		return this.save(cvTermRelationship);
	}

	public Integer retrieveAnalysisDerivedVariableID(final Integer originalVariableID,
			final Integer analysisMethodTermID) {
		try {
			final String sqlQuery = "select cr.object_id from cvterm_relationship cr WHERE cr.type_id = "
					+ TermId.HAS_ANALYSIS_VARIABLE.getId() + " and cr.subject_id = :variableID AND EXISTS "
					+ "(SELECT 1 FROM cvterm_relationship mr WHERE cr.object_id = mr.subject_id AND mr.type_id = "
					+ TermId.HAS_METHOD.getId() + " AND mr.object_id = :methodID)";
			final SQLQuery query = this.getSession().createSQLQuery(sqlQuery);
			query.setParameter("variableID", originalVariableID);
			query.setParameter("methodID", analysisMethodTermID);

			final Object result = query.uniqueResult();
			if (result == null) {
				return null;
			} else {
				return (Integer) result;
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with retrieveAnalysisDerivedVariableID=" + originalVariableID
					+ ", " + analysisMethodTermID + " query from CVTermRelationship: " + e.getMessage(), e);
		}
	}

	public List<String> getCategoriesUsedInStudies(final int scaleId) {
		try {
			final List<String> allCategories = new ArrayList<>();
			allCategories.addAll(getScaleCategoriesUsedInObservations(scaleId));
			allCategories.addAll(getScaleCategoriesUsedAsConditions(scaleId));
			allCategories.addAll(getScaleCategoriesUsedAsGermplasmDescriptors(scaleId));
			allCategories.addAll(getScaleCategoriesUsedAsTrialDesignFactors(scaleId));
			allCategories.addAll(getScaleCategoriesUsedAsEnvironmentFactors(scaleId));
			return allCategories;
		} catch (final HibernateException e) {
			final String message = "Error in getCategoriesUsedInStudies in CVTermRelationshipDao: "
					+ e.getMessage();
			CVTermRelationshipDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);

		}
	}

	@SuppressWarnings("unchecked")
	protected List<String> getScaleCategoriesUsedInObservations(final int scaleId) {
		final SQLQuery query = this.getSession().createSQLQuery(
				"SELECT v.name category "
				+ " FROM cvterm_relationship scale_values "
				+ " INNER JOIN cvterm v ON v.cvterm_id = scale_values.object_id "
				+ " WHERE scale_values.subject_id = :scaleId AND scale_values.type_id = " + TermId.HAS_VALUE.getId() 
				+ " AND EXISTS ( "
				+ "     SELECT 1    	 "
				+ "     FROM phenotype p "
				+ "     INNER JOIN nd_experiment ep on ep.nd_experiment_id = p.nd_experiment_id "
				+ "     INNER JOIN project pr ON pr.project_id = ep.project_id and pr.deleted = 0 "
				+ "     WHERE cvalue_id = v.cvterm_id)"
				+ "");
		query.setParameter("scaleId", scaleId);
		query.addScalar("category", CVTermRelationshipDao.STRING);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	protected List<String> getScaleCategoriesUsedAsConditions(final int scaleId) {
		final SQLQuery query = this.getSession().createSQLQuery(
				"SELECT categ.name category "
				+ " FROM cvterm_relationship scale_values "
				+ " INNER JOIN cvterm categ ON categ.cvterm_id = scale_values.object_id "
				+ " INNER JOIN cvterm_relationship var ON var.object_id = scale_values.subject_id and var.type_id = " + TermId.HAS_SCALE.getId() 
				+ " WHERE scale_values.subject_id = :scaleId AND scale_values.type_id = " + TermId.HAS_VALUE.getId() 
				+ " AND EXISTS ( "
				+ "     SELECT 1    	 "
				+ "     FROM projectprop pp "
				+ "     INNER JOIN project pr ON pr.project_id =pp.project_id and pr.deleted = 0 "
				+ "		WHERE pp.variable_id = var.subject_id and pp.value = categ.cvterm_id)");
		query.setParameter("scaleId", scaleId);
		query.addScalar("category", CVTermRelationshipDao.STRING);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	protected List<String> getScaleCategoriesUsedAsGermplasmDescriptors(final int scaleId) {
		final SQLQuery query = this.getSession().createSQLQuery(
				"SELECT categ.name category "
				+ " FROM cvterm_relationship scale_values "
				+ " INNER JOIN cvterm categ ON categ.cvterm_id = scale_values.object_id "
				+ " INNER JOIN cvterm_relationship var ON var.object_id = scale_values.subject_id and var.type_id = " + TermId.HAS_SCALE.getId() 
				+ " WHERE scale_values.subject_id = :scaleId AND scale_values.type_id = " + TermId.HAS_VALUE.getId()
				+ " AND EXISTS ( "
				+ "      SELECT 1 "
				+ "      FROM stockprop sp "
				+ "      INNER JOIN nd_experiment ep on ep.stock_id = sp.stock_id "
				+ "      INNER JOIN project pr ON pr.project_id =ep.project_id and pr.deleted = 0 "
				+ "      WHERE sp.type_id = var.subject_id and sp.value = categ.cvterm_id)");
		query.setParameter("scaleId", scaleId);
		query.addScalar("category", CVTermRelationshipDao.STRING);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	protected List<String> getScaleCategoriesUsedAsEnvironmentFactors(final int scaleId) {
		final SQLQuery query = this.getSession().createSQLQuery(
				"SELECT categ.name category "
				+ " FROM cvterm_relationship scale_values "
				+ " INNER JOIN cvterm categ ON categ.cvterm_id = scale_values.object_id "
				+ " INNER JOIN cvterm_relationship var ON var.object_id = scale_values.subject_id and var.type_id = " + TermId.HAS_SCALE.getId() 
				+ " WHERE scale_values.subject_id = :scaleId AND scale_values.type_id = " + TermId.HAS_VALUE.getId() 
				+ " AND EXISTS ( "
				+ "      SELECT 1 "
				+ "      FROM nd_geolocationprop gp "
				+ "      INNER JOIN nd_experiment ep on ep.nd_geolocation_id = gp.nd_geolocation_id "
				+ "      INNER JOIN project pr ON pr.project_id =ep.project_id and pr.deleted = 0 "
				+ "      WHERE gp.type_id = var.subject_id and gp.value = categ.cvterm_id)");
		query.setParameter("scaleId", scaleId);
		query.addScalar("category", CVTermRelationshipDao.STRING);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	protected List<String> getScaleCategoriesUsedAsTrialDesignFactors(final int scaleId) {
		final SQLQuery query = this.getSession().createSQLQuery(
				"SELECT categ.name category "
				+ " FROM cvterm_relationship scale_values "
				+ " INNER JOIN cvterm categ ON categ.cvterm_id = scale_values.object_id "
				+ " INNER JOIN cvterm_relationship var ON var.object_id = scale_values.subject_id and var.type_id = " + TermId.HAS_SCALE .getId()
				+ " WHERE scale_values.subject_id = :scaleId AND scale_values.type_id = " + TermId.HAS_VALUE .getId()
				+ " AND EXISTS ( "
				+ "      SELECT 1 "
				+ "      FROM nd_experimentprop e "
				+ "      INNER JOIN nd_experiment ep on e.nd_experiment_id = ep.nd_experiment_id "
				+ "      INNER JOIN project pr ON pr.project_id =ep.project_id and pr.deleted = 0 "
				+ "      WHERE e.type_id = var.subject_id and e.value = categ.cvterm_id)");
		query.setParameter("scaleId", scaleId);
		query.addScalar("category", CVTermRelationshipDao.STRING);
		return query.list();
	}
}
