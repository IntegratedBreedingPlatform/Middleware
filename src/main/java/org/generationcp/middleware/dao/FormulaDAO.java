package org.generationcp.middleware.dao;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.derived_variables.Formula;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FormulaDAO extends GenericDAO<Formula, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(FormulaDAO.class);

	public Formula getByTargetVariableId(final Integer variableId) {

		final Formula formula;

		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("active", true));
			criteria.add(Restrictions.eq("targetCVTerm.cvTermId", variableId));
			criteria.setMaxResults(1); // Only one formula per target for now
			criteria.setFetchMode("inputs", FetchMode.SELECT);

			formula = (Formula) criteria.uniqueResult();

		} catch (final HibernateException e) {
			final String message = "Error in getByTargetVariableId(" + variableId + ")";
			FormulaDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}

		return formula;

	}

	public List<Formula> getByTargetVariableIds(final Set<Integer> variableIds) {

		List<Formula> formulas = new ArrayList<>();

		if (!variableIds.isEmpty()) {

			try {
				final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
				criteria.add(Restrictions.eq("active", true));
				criteria.add(Restrictions.in("targetCVTerm.cvTermId", variableIds));
				criteria.setFetchMode("inputs", FetchMode.JOIN);

				formulas = criteria.list();

			} catch (final HibernateException e) {
				final String message = "Error in getByTargetVariableIds(" + variableIds + ")";
				FormulaDAO.LOG.error(message, e);
				throw new MiddlewareQueryException(message, e);
			}

		}

		return formulas;

	}

	/**
	 * Get active and inactive. This is useful to identify where an input is used, and it's necessary to know it even for inactive formulas
	 */
	public List<Formula> getByInputId(final Integer inputId) {

		List<Formula> formulas = new ArrayList<>();

		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.createAlias("inputs", "input");
			criteria.add(Restrictions.eq("input.cvTermId", inputId));
			criteria.setFetchMode("inputs", FetchMode.JOIN);

			formulas = criteria.list();

		} catch (final HibernateException e) {
			final String message = "Error in getByInputId(" + inputId + ")";
			FormulaDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}

		return formulas;
	}
}
