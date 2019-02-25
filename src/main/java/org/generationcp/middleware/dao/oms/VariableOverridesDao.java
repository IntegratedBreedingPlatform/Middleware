
package org.generationcp.middleware.dao.oms;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.oms.VariableOverrides;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.domain.oms.TermId;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Order;

public class VariableOverridesDao extends GenericDAO<VariableOverrides, Integer> {

	@SuppressWarnings("unchecked")
	public List<VariableOverrides> getByVariableId(Integer variableId) throws MiddlewareException {

		List properties;

		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("variableId", variableId));
			properties = criteria.list();

		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error at getByVariableId=" + variableId + " query on VariableOverridesDao: "
					+ e.getMessage(), e);
		}

		return properties;
	}

	@SuppressWarnings("unchecked")
	public VariableOverrides getByVariableAndProgram(Integer variableId, String programUuid) throws MiddlewareException {

		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("variableId", variableId));
			criteria.add(Restrictions.eq("programUuid", programUuid));
			return (VariableOverrides) criteria.uniqueResult();

		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error at getByVariableAndProgram=" + variableId + " query on VariableOverridesDao: "
					+ e.getMessage(), e);
		}
	}

	public VariableOverrides save(Integer variableId, String programUuid, String alias, String minValue, String maxValue)
			throws MiddlewareException {

		VariableOverrides overrides = this.getByVariableAndProgram(variableId, programUuid);
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
	public List<VariableOverrides> getVariableOverridesByVariableIds(final List<Integer> variableIds) throws MiddlewareException {

		List<VariableOverrides> properties;

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

	public Map<String, Map<Integer, VariableType>> getVariableOverridesByVariableIdsAndProgram(final List<String> variableNames, final String programUuid) throws MiddlewareException {

		final Map<String, Map<Integer, VariableType>> stdVarMap = new HashMap<String, Map<Integer, VariableType>>();

		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass()).
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
					stdVarIdsWithType = new HashMap<Integer, VariableType>();
					stdVarMap.put(alias, stdVarIdsWithType);
				}
				stdVarIdsWithType.put(variableId, this.getDefaultVariableType(variableId));
		}

		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error at getVariableOverridesByVariableIdsAndProgram=" + programUuid + " query on VariableOverridesDao: "
					+ e.getMessage(), e);
		}
		return stdVarMap;

	}

	public  VariableType getDefaultVariableType(final Integer cvTermId) {
		final Criteria criteria = this.getSession().createCriteria(CVTermProperty.class);
		criteria.add(Restrictions.eq("cvTermId", cvTermId));
		criteria.add(Restrictions.eq("typeId", TermId.VARIABLE_TYPE.getId()));
		criteria.addOrder(Order.asc("cvTermPropertyId"));
		final List<CVTermProperty> variableTypes = criteria.list();
		if (variableTypes != null) {
			for (final CVTermProperty cvTermProperty : variableTypes) {
				return VariableType.getByName(cvTermProperty.getValue());
			}
		}
		return null;
	}
}
