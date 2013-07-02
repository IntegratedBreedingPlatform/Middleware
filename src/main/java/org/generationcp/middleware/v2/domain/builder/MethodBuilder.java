/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.v2.domain.builder;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.v2.domain.Term;
import org.generationcp.middleware.v2.pojos.CVTerm;

public class MethodBuilder extends Builder {

	private static final int METHOD_ID = 1020;

	public MethodBuilder(HibernateSessionProvider sessionProviderForLocal,
			             HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	public Term findMethodById(int id) throws MiddlewareQueryException {
		Term term = getTermBuilder().get(id);
		if (term != null) {
			if (term.getVocabularyId() != METHOD_ID) {
				term = null;
			}
		}
		return term;
	}
	
	public Term findMethodByName(String name) throws MiddlewareQueryException {
		Term term = null;
		if (this.setWorkingDatabase(Database.LOCAL)) {
			term = mapToTerm(this.getCvTermDao().getByNameAndCvId(name, METHOD_ID));
		}
		
		if (term == null && this.setWorkingDatabase(Database.CENTRAL)) {
			term = mapToTerm(this.getCvTermDao().getByNameAndCvId(name, METHOD_ID));
		}
		return term;
	}
	
	private Term mapToTerm(CVTerm cvTerm) {
		Term term = null;
		
		if (cvTerm != null){
			term = new Term(cvTerm.getCvTermId(), cvTerm.getName(), cvTerm.getDefinition());
			term.setObsolete(cvTerm.isObsolete());
			term.setVocabularyId(cvTerm.getCv());
		}
		return term;
	}

}
