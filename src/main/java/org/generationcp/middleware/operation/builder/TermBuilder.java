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

import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermProperty;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
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
	
	public Term mapCVTermToTerm(CVTerm cVTerm){
		Term term = null;
		
		if (cVTerm != null){
			term = new Term(cVTerm.getCvTermId(), cVTerm.getName(), cVTerm.getDefinition());
			term.setObsolete(cVTerm.isObsolete());
			term.setVocabularyId(cVTerm.getCv());
			
			List<TermProperty> properties = new ArrayList<TermProperty>();
			if (cVTerm.getProperties() != null){
				for (CVTermProperty property: cVTerm.getProperties()){
				    properties.add(getTermPropertyBuilder().create(property));
				}
			}
			term.setProperties(properties);
		}
		return term;
	}

	public List<Term> getTermsByCvId(CvId cvId) throws MiddlewareQueryException {
		List<Term> terms = new ArrayList<Term>();		
		if (setWorkingDatabase(Database.CENTRAL)) {
			List<CVTerm> cvTerms = getCvTermDao().getTermsByCvId(cvId,0,0);
			for (CVTerm cvTerm : cvTerms){
				terms.add(mapCVTermToTerm(cvTerm));
			}
		}
		if (setWorkingDatabase(Database.LOCAL)) {
			List<CVTerm> cvTerms = getCvTermDao().getTermsByCvId(cvId,0,0);
			for (CVTerm cvTerm : cvTerms){
				terms.add(mapCVTermToTerm(cvTerm));
			}
		}
		
		return terms;
	}
	
	public List<Term> getTermsByCvId(CvId cvId,int start, int numOfRows) throws MiddlewareQueryException {
		List<Term> terms = new ArrayList<Term>();		
		if (setWorkingDatabase(cvId.getId())) {
			List<CVTerm> cvTerms = getCvTermDao().getTermsByCvId(cvId,start,numOfRows);
			for (CVTerm cvTerm : cvTerms){
				terms.add(mapCVTermToTerm(cvTerm));
			}
		}
		
		return terms;
	}
	
	public Term findTermByName(String name, CvId cvId) throws MiddlewareQueryException {
		Term term = null;
		
		if (setWorkingDatabase(Database.CENTRAL)) {
			term = mapToTerm(getCvTermDao().getByNameAndCvId(name, cvId.getId()));
			if (term == null) {
				if (setWorkingDatabase(Database.LOCAL)) {
					term =  mapToTerm(getCvTermDao().getByNameAndCvId(name, cvId.getId()));
				}
			}
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
	
	public List<Term> getTermsByIds(List<Integer> ids) throws MiddlewareQueryException {
		List<Term> terms = null;
		if (setWorkingDatabase(Database.CENTRAL)) {
			List<CVTerm> cvTerms = getCvTermDao().getByIds(ids);
			if(cvTerms!=null) {
				terms = new ArrayList<Term>();
				for (CVTerm cvTerm : cvTerms) {
					Term term = mapCVTermToTerm(cvTerm);
					terms.add(term);
				}
			}
		}
		return terms;
	}

	public Term findOrSaveTermByName(String name, CvId cv) throws MiddlewareQueryException, MiddlewareException {
		Term term = findTermByName(name, cv);
        if (term == null) {
        	term = getTermSaver().save(name, name, cv);
        }
        return term;
	}
	
	public Term getTermOfClass(int termId) throws MiddlewareQueryException {
		Term term = null;
		if (setWorkingDatabase(termId)) {
			term = mapCVTermToTerm(getCvTermDao().getTermOfClass(termId));
		}
		return term;
	}
}
