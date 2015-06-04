
package org.generationcp.middleware.dao.oms;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariableSummary;
import org.generationcp.middleware.domain.oms.TermSummary;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StandardVariableDao {

	private static final Logger LOG = LoggerFactory.getLogger(StandardVariableDao.class);

	private final Session session;

	public StandardVariableDao(Session session) {
		this.session = session;
	}

	/**
	 * Fetches the most commonly used summary information about a standrad variable form standard_variable_summary database view.
	 *
	 * @param standardVariableId
	 * @return {@link StandardVariableSummary}
	 * @throws MiddlewareQueryException
	 */
	public StandardVariableSummary getStandardVariableSummary(Integer standardVariableId) throws MiddlewareQueryException {
		long startTime = System.nanoTime();
		assert this.session != null : "Hibernate session is required.";

		StandardVariableSummary variable = null;
		try {
			SQLQuery query = this.session.createSQLQuery("SELECT * FROM standard_variable_summary where id = :id");
			query.setParameter("id", standardVariableId);
			Object[] queryResult = (Object[]) query.uniqueResult();
			variable = this.mapResults(queryResult);
		} catch (HibernateException he) {
			throw new MiddlewareQueryException(String.format(
					"Hibernate error in getting standard variable summary from standard_variable_summary view by id: %s. Cause: %s",
					standardVariableId, he.getCause().getMessage()));
		}

		long elapsedTime = System.nanoTime() - startTime;
		StandardVariableDao.LOG.debug(String.format("Time taken: %f ms.", (double) elapsedTime / 1000000L));
		return variable;
	}

	/**
	 * Loads a list of {@link StandardVariableSummary}'s for the given set of standard variable ids from standard_variable_summary database
	 * view.
	 *
	 * @param standardVariableIds
	 * @return
	 * @throws MiddlewareQueryException
	 */
	public List<StandardVariableSummary> getStarndardVariableSummaries(List<Integer> standardVariableIds) throws MiddlewareQueryException {
		long startTime = System.nanoTime();
		assert this.session != null : "Hibernate session is required.";

		List<StandardVariableSummary> variableSummaries = new ArrayList<StandardVariableSummary>();
		if (standardVariableIds != null && !standardVariableIds.isEmpty()) {
			try {
				SQLQuery query = this.session.createSQLQuery("SELECT * FROM standard_variable_summary where id in (:ids)");
				query.setParameterList("ids", standardVariableIds);
				@SuppressWarnings("rawtypes")
				List queryResults = query.list();
				for (Object result : queryResults) {
					StandardVariableSummary summary = this.mapResults((Object[]) result);
					if (summary != null) {
						variableSummaries.add(summary);
					}
				}
			} catch (HibernateException he) {
				throw new MiddlewareQueryException(String.format(
						"Hibernate error in getting standard variable summaries from standard_variable_summary view. Cause: %s", he
								.getCause().getMessage()));
			}
		}

		long elapsedTime = System.nanoTime() - startTime;
		StandardVariableDao.LOG.debug(String.format("Time taken: %f ms.", (double) elapsedTime / 1000000L));
		StandardVariableDao.LOG.debug(String.format("Number of IDs supplied: %d. Number of standard variable summaries found: %d.",
				standardVariableIds.size(), variableSummaries.size()));
		return variableSummaries;
	}

	/**
	 * Fetches the most commonly used summary information about a standrad variable form standard_variable_summary database view.
	 *
	 * @param standardVariableId
	 * @return {@link StandardVariableSummary}
	 * @throws MiddlewareQueryException
	 */
	public List<StandardVariableSummary> getStandardVariableSummaryWithIsAId(List<Integer> isAIds) throws MiddlewareQueryException {
		long startTime = System.nanoTime();
		assert this.session != null : "Hibernate session is required.";

		List<StandardVariableSummary> variableSummaries = new ArrayList<StandardVariableSummary>();
		if (isAIds != null && !isAIds.isEmpty()) {
			try {
				SQLQuery query = this.session.createSQLQuery("SELECT * FROM standard_variable_summary where is_a_id in (:ids)");
				query.setParameterList("ids", isAIds);
				@SuppressWarnings("rawtypes")
				List queryResults = query.list();
				for (Object result : queryResults) {
					StandardVariableSummary summary = this.mapResults((Object[]) result);
					if (summary != null) {
						variableSummaries.add(summary);
					}
				}
			} catch (HibernateException he) {
				throw new MiddlewareQueryException(String.format(
						"Hibernate error in getting standard variable summaries from standard_variable_summary view. Cause: %s", he
								.getCause().getMessage()));
			}
		}

		long elapsedTime = System.nanoTime() - startTime;
		StandardVariableDao.LOG.debug(String.format("Time taken: %f ms.", (double) elapsedTime / 1000000L));
		return variableSummaries;
	}

	private StandardVariableSummary mapResults(Object[] queryResult) {
		if (queryResult != null) {
			StandardVariableSummary variable =
					new StandardVariableSummary((Integer) queryResult[0], (String) queryResult[1], (String) queryResult[2]);

			variable.setProperty(this.createTermSummary(this.convertToId(queryResult[3]), (String) queryResult[4], (String) queryResult[5]));
			variable.setMethod(this.createTermSummary(this.convertToId(queryResult[6]), (String) queryResult[7], (String) queryResult[8]));
			variable.setScale(this.createTermSummary(this.convertToId(queryResult[9]), (String) queryResult[10], (String) queryResult[11]));
			variable.setIsA(this.createTermSummary(this.convertToId(queryResult[12]), (String) queryResult[13], (String) queryResult[14]));
			variable.setStoredIn(this.createTermSummary(this.convertToId(queryResult[15]), (String) queryResult[16],
					(String) queryResult[17]));
			variable.setDataType(this.createTermSummary(this.convertToId(queryResult[18]), (String) queryResult[19],
					(String) queryResult[20]));

			variable.setPhenotypicType(queryResult[21] != null ? PhenotypicType.valueOf((String) queryResult[21]) : null);
			return variable;
		}
		return null;
	}

	private Integer convertToId(Object rawValue) {
		if (rawValue != null) {
			try {
				return Integer.valueOf((String) rawValue);
			} catch (NumberFormatException nfe) {
				StandardVariableDao.LOG.debug("Failed to convert column's raw value to an Integer Id. Raw value was: " + rawValue);
				return null;
			}
		}
		return null;
	}

	private TermSummary createTermSummary(Integer id, String name, String definition) {
		if (id == null && StringUtils.isBlank(name) && StringUtils.isBlank(definition)) {
			// Avoid creating an empty TermSummary
			return null;
		}
		return new TermSummary(id, name, definition);
	}
}
