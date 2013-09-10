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
package org.generationcp.middleware.operation.builder;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermProperty;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;

public class TermBuilder extends Builder {

	public TermBuilder(HibernateSessionProvider sessionProviderForLocal,
			               HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public Term get(int termId) throws MiddlewareQueryException {
		Term term = null;
		if (setWorkingDatabase(termId)) {
			term = mapCVTermToTerm(getCvTermDao().getById(termId));
		}
		return term;
	}
	
	private Term mapCVTermToTerm(CVTerm cVTerm){
		Term term = null;
		
		if (cVTerm != null){
			term = new Term(cVTerm.getCvTermId(), cVTerm.getName(), cVTerm.getDefinition());
			term.setObsolete(cVTerm.isObsolete());
			term.setVocabularyId(cVTerm.getCv());
			
			List<TermProperty> properties = new ArrayList<TermProperty>();
			for (CVTermProperty property: cVTerm.getProperties()){
			    properties.add(getTermPropertyBuilder().create(property));
			}
			term.setProperties(properties);
		}
		return term;
	}
}
