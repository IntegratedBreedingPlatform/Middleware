package org.generationcp.middleware.dao.oms;

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

	
	private Session session;
	
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
			variable = mapResults(queryResult);
		} catch(HibernateException he) {
			throw new MiddlewareQueryException("Hibernate error in getting standard variable summary by id: " + standardVariableId, he);
		}
		
		long elapsedTime = System.nanoTime() - startTime;
		LOG.info(String.format("Loaded StandardVariableSummary for id [%d] from standard_variable_summary view in: %f ms.", standardVariableId, ((double) elapsedTime/1000000L )));
		return variable;
	}

	private StandardVariableSummary mapResults(Object[] queryResult) {
		if(queryResult != null) {
			StandardVariableSummary variable = new StandardVariableSummary((Integer)queryResult[0], (String)queryResult[1], (String) queryResult[2]);	
			variable.setProperty(createTermSummary(Integer.valueOf((String)queryResult[3]), (String)queryResult[4], (String) queryResult[5]));
			variable.setMethod(createTermSummary(Integer.valueOf((String)queryResult[6]), (String)queryResult[7], (String) queryResult[8]));
			variable.setScale(createTermSummary(Integer.valueOf((String)queryResult[9]), (String)queryResult[10], (String) queryResult[11]));
			variable.setIsA(createTermSummary(Integer.valueOf((String)queryResult[12]), (String)queryResult[13], (String) queryResult[14]));
			variable.setStoredIn(createTermSummary(Integer.valueOf((String)queryResult[15]), (String)queryResult[16], (String) queryResult[17]));
			variable.setDataType(createTermSummary(Integer.valueOf((String)queryResult[18]), (String)queryResult[19], (String) queryResult[20]));
			variable.setPhenotypicType(PhenotypicType.valueOf((String) queryResult[21]));
			return variable;
		}
		return null;
	}
	
	private TermSummary createTermSummary(Integer id, String name, String definition) {
		if(id != null) {
			return new TermSummary(id, name, definition);
		}
		return null;
	}
}
