package org.generationcp.middleware.manager;

import org.generationcp.middleware.Work;
import org.generationcp.middleware.dao.ProgramDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.ProgramDataManager;
import org.generationcp.middleware.pojos.Program;


public class ProgramDataManagerImpl extends DataManager implements ProgramDataManager {
	
	public ProgramDataManagerImpl(HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}
	
	@Override
	public Program addProgram(final Program program) throws MiddlewareQueryException {
		
		doInTransaction(new Work() {			
			@Override
			public String getName() {
				return "Add Program";
			}
			
			@Override
			public void doWork() throws Exception {
				ProgramDAO programDAO = new ProgramDAO();
				programDAO.setSession(getActiveSession());
				programDAO.save(program);							
			}
		});
		return program;
	}
	 
}
