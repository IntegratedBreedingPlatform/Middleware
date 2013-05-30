package org.generationcp.middleware.v2.manager;

import java.util.HashSet;
import java.util.Set;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DataManager;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.v2.domain.StandardVariable;
import org.generationcp.middleware.v2.domain.Term;
import org.generationcp.middleware.v2.manager.api.OntologyDataManager;
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
}
