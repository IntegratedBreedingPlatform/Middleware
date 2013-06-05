package org.generationcp.middleware.v2.manager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DataManager;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.v2.domain.StandardVariable;
import org.generationcp.middleware.v2.domain.Term;
import org.generationcp.middleware.v2.manager.api.OntologyDataManager;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class OntologyDataManagerImpl extends DataManager implements OntologyDataManager {
	
	public OntologyDataManagerImpl() { 		
	}

	public OntologyDataManagerImpl(HibernateSessionProvider sessionProviderForLocal,
			                    HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public OntologyDataManagerImpl(Session sessionForLocal, Session sessionForCentral) {
		super(sessionForLocal, sessionForCentral);
	}

	@Override
	public Term getTermById(int termId) throws MiddlewareQueryException {
		return getTermBuilder().get(termId);
	}
	
	@Override
	public StandardVariable getStandardVariable(int stdVariableId) throws MiddlewareQueryException {
		return getStandardVariableBuilder().create(stdVariableId);
	}

	@Override
	public void addStandardVariable(StandardVariable stdVariable) throws MiddlewareQueryException {
		requireLocalDatabaseInstance();
		Session session = getCurrentSessionForLocal();
        Transaction trans = null;
 
        try {
            trans = session.beginTransaction();
            getStandardVariableSaver().save(stdVariable);
			trans.commit();
			
        } catch (Exception e) {
	    	rollbackTransaction(trans);
	        throw new MiddlewareQueryException("error in addStandardVariable " + e.getMessage(), e);
	    }
	} 
	
	@Override
	public Term addMethod(String name, String definition) throws MiddlewareQueryException{
		requireLocalDatabaseInstance();
		Session session = getCurrentSessionForLocal();
        Transaction trans = null;
 
        try {
            trans = session.beginTransaction();
			Term term = getTermSaver().save(name, definition);
			trans.commit();
	        return term;
	    } catch (Exception e) {
	    	rollbackTransaction(trans);
	        throw new MiddlewareQueryException("error in addMethod " + e.getMessage(), e);
	    }

	}

	@Override
	public Set<StandardVariable> findStandardVariablesByNameOrSynonym(String nameOrSynonym) throws MiddlewareQueryException {
		Set<StandardVariable> standardVariables = new HashSet<StandardVariable>();
		if (setWorkingDatabase(Database.LOCAL)) {
			standardVariables.addAll(getStandardVariablesByNameOrSynonym(nameOrSynonym));
		}
		if (setWorkingDatabase(Database.CENTRAL)) {
			standardVariables.addAll(getStandardVariablesByNameOrSynonym(nameOrSynonym));
		}
		return standardVariables;
	}
	
	private Set<StandardVariable> getStandardVariablesByNameOrSynonym(String nameOrSynonym) throws MiddlewareQueryException {
		Set<StandardVariable> standardVariables = new HashSet<StandardVariable>();
		Set<Integer> stdVarIds = getCvTermDao().findStdVariablesByNameOrSynonym(nameOrSynonym);
		for (Integer stdVarId : stdVarIds) {
			standardVariables.add(getStandardVariable(stdVarId));
		}
		return standardVariables;
	}

	@Override
	public Term findMethodById(int id) throws MiddlewareQueryException {
		return getMethodBuilder().findMethodById(id);
	}

	@Override
	public Term findMethodByName(String name) throws MiddlewareQueryException {
		return getMethodBuilder().findMethodByName(name);
	}

	@Override
	public Integer getStandadardVariableIdByPropertyScaleMethod(
			Integer propertyId, Integer scaleId, Integer methodId)
			throws MiddlewareQueryException {
			requireLocalDatabaseInstance();
			Session session = getCurrentSessionForLocal();
		
			try {
			
			String sql = "select distinct cvr.subject_id "
					+ "from cvterm_relationship cvr " 
					+ "inner join cvterm_relationship cvrp on cvr.subject_id = cvrp.subject_id and cvrp.type_id = 1200 "
					+ "inner join cvterm_relationship cvrs on cvr.subject_id = cvrs.subject_id and cvrs.type_id = 1220 "
					+ "inner join cvterm_relationship cvrm on cvr.subject_id = cvrm.subject_id and cvrm.type_id = 1210 "
					+ "where cvrp.object_id = :propertyId and cvrs.object_id = :scaleId and cvrm.object_id = :methodId ";
			
			Query query = session.createSQLQuery(sql);
			query.setParameter("propertyId", propertyId);
			query.setParameter("scaleId", scaleId);
			query.setParameter("methodId", methodId);
								
			Integer id = (Integer) query.uniqueResult();
			
			return id;
						
		} catch(HibernateException e) {
			logAndThrowException("Error at getStandadardVariableIdByPropertyScaleMethod :" + e.getMessage(), e);
		}
		return null;
		
		
	
	}
}
