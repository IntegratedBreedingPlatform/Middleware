
package org.generationcp.middleware.dao.oms;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.pojos.oms.VariableOverrides;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VariableOverridesDao extends GenericDAO<VariableOverrides, Integer> {

	@SuppressWarnings("unchecked")
	public List<VariableOverrides> getByVariableId(final Integer variableId) {

		final List properties;

		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("variableId", variableId));
			properties = criteria.list();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error at getByVariableId=" + variableId + " query on VariableOverridesDao: "
				+ e.getMessage(), e);
		}

		return properties;
	}

	@SuppressWarnings("unchecked")
	public VariableOverrides getByVariableAndProgram(final Integer variableId, final String programUuid) {

		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("variableId", variableId));
			criteria.add(Restrictions.eq("programUuid", programUuid));
			return (VariableOverrides) criteria.uniqueResult();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error at getByVariableAndProgram=" + variableId + " query on VariableOverridesDao: "
				+ e.getMessage(), e);
		}
	}

	public VariableOverrides save(
		final Integer variableId, final String programUuid, final String alias, final String minValue, final String maxValue) {

		final VariableOverrides overrides = this.getByVariableAndProgram(variableId, programUuid);
		// check for uniqueness
		if (overrides == null) {
			return this.save(new VariableOverrides(null, variableId, programUuid, alias, minValue, maxValue));
		}

		overrides.setAlias(alias);
		overrides.setExpectedMin(minValue);
		overrides.setExpectedMax(maxValue);
		return this.merge(overrides);
	}

	@SuppressWarnings("unchecked")
	public List<VariableOverrides> getVariableOverridesByVariableIds(final List<Integer> variableIds) {

		final List<VariableOverrides> properties;

		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.in("variableId", variableIds));
			properties = criteria.list();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				"Error at getVariableOverridesByVariableIds IN " + variableIds + " query on VariableOverridesDao: " + e.getMessage(), e);
		}

		return properties;
	}

	public Map<String, Map<Integer, VariableType>> getVariableOverridesByVariableIdsAndProgram(
		final List<String> variableNames, final String programUuid) {

		final Map<String, Map<Integer, VariableType>> stdVarMap = new HashMap<>();

		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass()).
				setProjection(Projections.distinct(Projections.projectionList()
					.add(Projections.property("alias"))
					.add(Projections.property("variableId"))));

			criteria.add(Restrictions.in("alias", variableNames));
			criteria.add(Restrictions.eq("programUuid", programUuid));
			final List<Object[]> results = criteria.list();

			for (final Object[] row : results) {

				final String alias = ((String) row[0]).trim().toUpperCase();
				final Integer variableId = (Integer) row[1];

				Map<Integer, VariableType> stdVarIdsWithType = null;
				if (stdVarMap.containsKey(alias)) {
					stdVarIdsWithType = stdVarMap.get(alias);
				} else {
					stdVarIdsWithType = new HashMap<>();
					stdVarMap.put(alias, stdVarIdsWithType);
				}
				stdVarIdsWithType.put(variableId, this.getDefaultVariableType(variableId));
			}

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				"Error at getVariableOverridesByVariableIdsAndProgram=" + programUuid + " query on VariableOverridesDao: "
					+ e.getMessage(), e);
		}
		return stdVarMap;

	}

	private VariableType getDefaultVariableType(final Integer cvTermId) {
		final Criteria criteria = this.getSession().createCriteria(CVTermProperty.class);
		criteria.add(Restrictions.eq("cvTermId", cvTermId));
		criteria.add(Restrictions.eq("typeId", TermId.VARIABLE_TYPE.getId()));
		criteria.addOrder(Order.asc("cvTermPropertyId"));
		final List<CVTermProperty> variableTypes = criteria.list();
		if (variableTypes != null) {
			final List<Term> allVariableTypes = this.getTermByCvId(CvId.VARIABLE_TYPE.getId());
			final Map<String, Integer> allVariableTypesByName = new HashMap<>();
			for (final Term term: allVariableTypes) {
				allVariableTypesByName.put(term.getName(), term.getId());
			}
			for (final CVTermProperty cvTermProperty : variableTypes) {
				return VariableType.getById(allVariableTypesByName.get(cvTermProperty.getValue()));
			}
		}
		return null;
	}

	private List<Term> getTermByCvId(final int cvId) {

		final List<Term> terms = new ArrayList<>();

		try {

			final StringBuilder sqlString = new StringBuilder()
				.append("SELECT DISTINCT cvt.cvterm_id, cvt.cv_id, cvt.name, cvt.definition ")
				.append("FROM cvterm cvt ").append("WHERE cvt.cv_id = :cvId");

			final SQLQuery query = this.getSession().createSQLQuery(sqlString.toString());
			query.setParameter("cvId", cvId);

			final List<Object[]> results = query.list();

			if (!results.isEmpty()) {

				for (final Object[] row : results) {

					final Integer cvtermId = (Integer) row[0];
					final Integer cvtermCvId = (Integer) row[1];
					final String cvtermName = (String) row[2];
					final String cvtermDefinition = (String) row[3];

					final Term term = new Term();
					term.setId(cvtermId);
					term.setName(cvtermName);
					term.setDefinition(cvtermDefinition);
					term.setVocabularyId(cvtermCvId);
					terms.add(term);
				}
			}

		} catch (final HibernateException e) {
			this.logAndThrowException("Error at getTermByCvId=" + cvId + " query on CVTermDao: " + e.getMessage(), e);
		}

		return terms;
	}
}
