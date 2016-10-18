
package org.generationcp.middleware.dao.oms;

import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.oms.VariableOverrides;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;

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
					"Error at getByVariableIds IN " + variableIds + " query on VariableOverridesDao: " + e.getMessage(), e);
		}

		return properties;
	}
}
