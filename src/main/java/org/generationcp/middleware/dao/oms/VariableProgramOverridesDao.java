
package org.generationcp.middleware.dao.oms;

import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.oms.VariableProgramOverrides;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;

public class VariableProgramOverridesDao extends GenericDAO<VariableProgramOverrides, Integer> {

	@SuppressWarnings("unchecked")
	public List<VariableProgramOverrides> getByVariableId(Integer variableId) throws MiddlewareException {

		List properties;

		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("variableId", variableId));
			properties = criteria.list();

		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error at getByVariableId=" + variableId + " query on VariableProgramOverridesDao: "
					+ e.getMessage(), e);
		}

		return properties;
	}

	@SuppressWarnings("unchecked")
	public VariableProgramOverrides getByVariableAndProgram(Integer variableId, String programUuid) throws MiddlewareException {

		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("variableId", variableId));
			criteria.add(Restrictions.eq("programUuid", programUuid));
			return (VariableProgramOverrides) criteria.uniqueResult();

		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error at getByVariableAndProgram=" + variableId + " query on VariableProgramOverridesDao: "
					+ e.getMessage(), e);
		}
	}

	public VariableProgramOverrides save(Integer variableId, String programUuid, String alias, String minValue, String maxValue)
			throws MiddlewareException {

		VariableProgramOverrides overrides = this.getByVariableAndProgram(variableId, programUuid);
		// check for uniqueness
		if (overrides == null) {
			return this.save(new VariableProgramOverrides(this.getNextId(VariableProgramOverrides.ID_NAME), variableId, programUuid, alias,
					minValue, maxValue));
		}

		overrides.setAlias(alias);
		overrides.setMinValue(minValue);
		overrides.setMaxValue(maxValue);
		return this.merge(overrides);
	}
}
