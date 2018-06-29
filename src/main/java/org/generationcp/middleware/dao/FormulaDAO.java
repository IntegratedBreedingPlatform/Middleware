package org.generationcp.middleware.dao;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.derived_variables.Formula;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FormulaDAO extends GenericDAO<Formula, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(FormulaDAO.class);

	public Formula getByTargetVariableId(final Integer variableId) {

		Formula formula;

		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("targetCVTerm.cvTermId", variableId));
			criteria.setFetchMode("inputs", FetchMode.JOIN);

			formula = (Formula) criteria.uniqueResult();

		} catch (HibernateException e) {
			final String message = "Error in getByTargetVariableId(" + variableId + ")";
			FormulaDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}

		return formula;

	}

}
