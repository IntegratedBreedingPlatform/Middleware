package org.generationcp.middleware.dao.oms;

import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariableSummary;
import org.generationcp.middleware.domain.oms.TermSummary;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.OntologyDataManagerImpl;
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
			Object[] result = (Object[]) query.uniqueResult();
			
			if(result != null) {
				variable = new StandardVariableSummary((Integer)result[0], (String)result[1], (String) result[2]);	
				variable.setProperty(createTermSummary(Integer.valueOf((String)result[3]), (String)result[4], (String) result[5]));
				variable.setMethod(createTermSummary(Integer.valueOf((String)result[6]), (String)result[7], (String) result[8]));
				variable.setScale(createTermSummary(Integer.valueOf((String)result[9]), (String)result[10], (String) result[11]));
				variable.setIsA(createTermSummary(Integer.valueOf((String)result[12]), (String)result[13], (String) result[14]));
				variable.setStoredIn(createTermSummary(Integer.valueOf((String)result[15]), (String)result[16], (String) result[17]));
				variable.setDataType(createTermSummary(Integer.valueOf((String)result[18]), (String)result[19], (String) result[20]));
				variable.setPhenotypicType(PhenotypicType.valueOf((String) result[21]));
			}
		} catch(HibernateException he) {
			throw new MiddlewareQueryException("Hibernate error in getting standard variable summary by id: " + standardVariableId, he);
		}
		
		long elapsedTime = System.nanoTime() - startTime;
		LOG.info(String.format("Loaded StandardVariableSummary for id [%d] from standard_variable_summary view in: %f ms.", standardVariableId, ((double) elapsedTime/1000000L )));
		return variable;
	}
	
	private TermSummary createTermSummary(Integer id, String name, String definition) {
		if(id != null) {
			return new TermSummary(id, name, definition);
		}
		return null;
	}
}
