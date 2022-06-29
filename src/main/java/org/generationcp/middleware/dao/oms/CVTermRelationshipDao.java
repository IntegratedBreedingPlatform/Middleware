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

import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.collections.map.MultiKeyMap;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * DAO class for {@link CVTermRelationship}.
 */
public class CVTermRelationshipDao extends GenericDAO<CVTermRelationship, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(CVTermRelationshipDao.class);
	private static final StringType STRING = new StringType();

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

	public Set<String> getCategoriesInUse(final int scaleId) {
		try {
			final Set<String> allCategories = new HashSet<>();
			allCategories.addAll(this.getScaleCategoriesUsedInObservations(scaleId));
			allCategories.addAll(this.getScaleCategoriesUsedAsConditions(scaleId));
			allCategories.addAll(this.getScaleCategoriesUsedInStudyEntries(scaleId));
			allCategories.addAll(this.getScaleCategoriesUsedAsTrialDesignFactors(scaleId));
			allCategories.addAll(this.getScaleCategoriesUsedAsEnvironmentFactors(scaleId));
			allCategories.addAll(this.getScaleCategoriesUsedInAttributes(scaleId));
			allCategories.addAll(this.getScaleCategoriesUsedInListEntries(scaleId));
			return allCategories;
		} catch (final HibernateException e) {
			final String message = "Error in getCategoriesInUse in CVTermRelationshipDao: "
				+ e.getMessage();
			CVTermRelationshipDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);

		}
	}

	public List<String> getScaleCategoriesUsedInAttributes(final int scaleId) {
		final SQLQuery query = this.getSession().createSQLQuery(
			"SELECT DISTINCT v.name category "
				+ " FROM cvterm_relationship scale_values "
				+ " INNER JOIN cvterm v ON v.cvterm_id = scale_values.object_id and scale_values.subject_id = :scaleId and scale_values.type_id = "
				+ TermId.HAS_VALUE.getId()
				+ " INNER JOIN cvterm_relationship var ON var.object_id = scale_values.subject_id and var.type_id = "
				+ TermId.HAS_SCALE.getId()
				+ " WHERE EXISTS ( "
				+ "     SELECT 1    	 "
				+ "     FROM atributs a "
				+ "     WHERE a.cval_id = v.cvterm_id and a.atype = var.subject_id ) ");
		query.setParameter("scaleId", scaleId);
		query.addScalar("category", CVTermRelationshipDao.STRING);
		return query.list();
	}

	public List<String> getScaleCategoriesUsedInListEntries(final int scaleId) {
		final SQLQuery query = this.getSession().createSQLQuery(
			"SELECT DISTINCT v.name as category "
				+ " FROM cvterm_relationship scale_values "
				+ " INNER JOIN cvterm v ON v.cvterm_id = scale_values.object_id AND scale_values.subject_id = :scaleId AND scale_values.type_id = "
				+ TermId.HAS_VALUE.getId()
				+ " INNER JOIN cvterm_relationship var ON var.object_id = scale_values.subject_id AND var.type_id = " + TermId.HAS_SCALE
				.getId()
				+ " WHERE EXISTS ( "
				+ "     SELECT 1    	 "
				+ "     FROM list_data_details a "
				+ "     WHERE a.cvalue_id = v.cvterm_id AND a.variable_id = var.subject_id ) ");
		query.setParameter("scaleId", scaleId);
		query.addScalar("category", CVTermRelationshipDao.STRING);
		return query.list();
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
				+ "     WHERE p.cvalue_id = v.cvterm_id)"
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
				+ " INNER JOIN cvterm_relationship var ON var.object_id = scale_values.subject_id and var.type_id = " + TermId.HAS_SCALE
				.getId()
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
	protected List<String> getScaleCategoriesUsedInStudyEntries(final int scaleId) {
		final SQLQuery query = this.getSession().createSQLQuery(
			"SELECT categ.name category "
				+ " FROM cvterm_relationship scale_values "
				+ " INNER JOIN cvterm categ ON categ.cvterm_id = scale_values.object_id "
				+ " INNER JOIN cvterm_relationship var ON var.object_id = scale_values.subject_id and var.type_id = " + TermId.HAS_SCALE
				.getId()
				+ " WHERE scale_values.subject_id = :scaleId AND scale_values.type_id = " + TermId.HAS_VALUE.getId()
				+ " AND EXISTS ( "
				+ "      SELECT 1 "
				+ "      FROM stockprop sp "
				+ "      INNER JOIN stock st ON st.stock_id = sp.stock_id "
				+ "      INNER JOIN project pr ON pr.project_id = st.project_id AND pr.deleted = 0 "
				+ "      WHERE sp.type_id = var.subject_id and sp.cvalue_id = categ.cvterm_id)");
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
				+ " INNER JOIN cvterm_relationship var ON var.object_id = scale_values.subject_id and var.type_id = " + TermId.HAS_SCALE
				.getId()
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
				+ " INNER JOIN cvterm_relationship var ON var.object_id = scale_values.subject_id and var.type_id = " + TermId.HAS_SCALE
				.getId()
				+ " WHERE scale_values.subject_id = :scaleId AND scale_values.type_id = " + TermId.HAS_VALUE.getId()
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

	public Map<Integer, List<ValueReference>> getCategoriesForCategoricalVariables(final List<Integer> variableIds) {
		final Map<Integer, List<ValueReference>> map = new HashMap<>();
		if (!CollectionUtils.isEmpty(variableIds)) {
			final SQLQuery query = this.getSession().createSQLQuery(
				"SELECT var.subject_id as variableId, categ.cvterm_id as categoryId, categ.name, categ.definition "
					+ " FROM cvterm categ "
					+ " INNER JOIN cvterm_relationship scale_values ON scale_values.object_id = categ.cvterm_id AND scale_values.type_id = "
					+ TermId.HAS_VALUE.getId()
					+ " INNER JOIN cvterm_relationship var ON var.object_id = scale_values.subject_id and var.type_id = " + TermId.HAS_SCALE
					.getId()
					+ " INNER JOIN cvterm_relationship dataType on dataType.subject_id = var.object_id "
					+ "  AND dataType.type_id = " + TermId.HAS_TYPE.getId() + " AND dataType.object_id = " + DataType.CATEGORICAL_VARIABLE
					.getId()
					+ " WHERE var.subject_id IN (:variableIds) ");
			query.setParameterList("variableIds", variableIds);
			query.addScalar("variableId");
			query.addScalar("categoryId");
			query.addScalar("name");
			query.addScalar("definition");
			query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
			final List<Map<String, Object>> results = query.list();
			for (final Map<String, Object> result : results) {
				final Integer variableId = (Integer) result.get("variableId");
				map.putIfAbsent(variableId, new ArrayList<>());
				map.get(variableId).add(
					new ValueReference((Integer) result.get("categoryId"), (String) result.get("name"), (String) result.get("definition")));
			}
		}
		return map;
	}

	public MultiKeyMap retrieveAnalysisMethodsOfTraits(final List<Integer> traitVariableIds,
		final List<Integer> analysisMethodIds) {
		try {
			final String sqlQuery = "select\n"
				+ "cr.subject_id as originalVariableId,\n"
				+ "cr.object_id as analysisVariableId,\n"
				+ "mr.object_id as analysisVariableMethodId\n"
				+ "from cvterm_relationship cr\n"
				+ "INNER JOIN cvterm_relationship mr ON cr.object_id = mr.subject_id AND mr.type_id = " + TermId.HAS_METHOD.getId()
				+ " AND mr.object_id in (:analysisMethodIds)\n"
				+ "WHERE cr.type_id = " + TermId.HAS_ANALYSIS_VARIABLE.getId() + " and cr.subject_id IN (:traitVariableIds)";

			final SQLQuery query = this.getSession().createSQLQuery(sqlQuery);
			query.setParameterList("traitVariableIds", traitVariableIds);
			query.setParameterList("analysisMethodIds", analysisMethodIds);
			query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);

			final MultiKeyMap returnValue = MultiKeyMap.decorate(new LinkedMap());
			final List<Map<String, Integer>> results = query.list();
			for (final Map<String, Integer> row : results) {
				returnValue.put(row.get("originalVariableId"), row.get("analysisVariableMethodId"), row.get("analysisVariableId"));
			}
			return returnValue;

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with retrieveAnalysisMethodsOfTraits=" + traitVariableIds
				+ ", " + analysisMethodIds + " query from CVTermRelationship: " + e.getMessage(), e);
		}
	}

}
