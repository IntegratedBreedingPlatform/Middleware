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

import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Scale;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.oms.CVTerm;

import java.util.ArrayList;
import java.util.List;

public class TermBuilder extends Builder {

	public TermBuilder(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public Term get(int termId) throws MiddlewareQueryException {
		Term term = null;
		if (setWorkingDatabase(termId)) {
			term = mapCVTermToTerm(getCvTermDao().getById(termId));
		}
		return term;
	}
	
	public static Term mapCVTermToTerm(CVTerm cVTerm) throws MiddlewareQueryException {
		Term term = null;
		
		if (cVTerm != null){
			term = new Term(cVTerm.getCvTermId(), cVTerm.getName(), cVTerm.getDefinition());
			term.setObsolete(cVTerm.isObsolete());
			term.setVocabularyId(cVTerm.getCv());
			//No longer populate properties !! This is a major change. However, no caller was using properties anyway!
		}
		return term;
	}

	public List<Term> getTermsByCvId(CvId cvId) throws MiddlewareQueryException {
		List<Term> terms = new ArrayList<Term>();
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
		if (setWorkingDatabase(Database.LOCAL)) {
			term = mapToTerm(getCvTermDao().getByNameAndCvId(name, cvId.getId()));
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
		if (setWorkingDatabase(Database.LOCAL)) {
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
        	//assign unclassified trait class
        	setWorkingDatabase(Database.LOCAL);
        	CVTerm cvTerm = getCvTermDao().getById(TermId.GENERAL_TRAIT_CLASS.getId());
        	Integer typeClass = null;
        	if(cvTerm != null) {
                typeClass = TermId.GENERAL_TRAIT_CLASS.getId();
            } else {
                typeClass = TermId.ONTOLOGY_TRAIT_CLASS.getId();
            }
        	getCvTermRelationshipSaver().save(term.getId(), TermId.IS_A.getId(), typeClass);
        }
        return term;
	}
	
	public Term getTermOfProperty(int termId, int cvId) throws MiddlewareQueryException {
		Term term = null;
		if (setWorkingDatabase(termId)) {
			term = mapCVTermToTerm(getCvTermDao().getTermOfProperty(termId, cvId));
		}
		return term;
	}
	
	public Term getTermOfClassOfProperty(int termId, int cvId, int isATermId) throws MiddlewareQueryException {
		Term term = null;
		if (setWorkingDatabase(termId)) {
			List<Integer> list = getCvTermRelationshipDao().getObjectIdByTypeAndSubject(isATermId,termId);
			//since we're getting the isA relationship, we're only expecting only one object (only hasValue has many result)
			if(list!=null && !list.isEmpty()) {
				Integer objectId = list.get(0);
				if (setWorkingDatabase(objectId)) {
					term = mapCVTermToTerm(getCvTermDao().getById(objectId));
				}
			}
			
		}
		return term;
	}
	
	public List<Scale> getAllInventoryScales() throws MiddlewareQueryException {
		List<Scale> list = new ArrayList<Scale>();
		setWorkingDatabase(Database.LOCAL);
		list.addAll(getCvTermDao().getAllInventoryScales());
		return list;
	}
}
