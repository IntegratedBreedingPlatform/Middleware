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
package org.generationcp.middleware.operation.saver;

import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.oms.CVTerm;

import java.util.List;

public class CvTermSaver extends Saver {

	public CvTermSaver(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public Term save(String name, String definition, CvId cvId)  throws MiddlewareException, MiddlewareQueryException{ 
		validateInputFields(name, definition);
        CVTermDao dao = getCvTermDao();

        Integer generatedId;
		try {
			generatedId = dao.getNextId("cvTermId");
		} catch (MiddlewareQueryException e) {
			throw new MiddlewareQueryException(e.getMessage(), e);
		}
		CVTerm cvTerm = create(generatedId, name, definition, cvId.getId(), false, false); 
		dao.save(cvTerm);

		return new Term(cvTerm.getCvTermId(), cvTerm.getName(), cvTerm.getDefinition());
	}
	

    public Term saveOrUpdate(String name, String definition, CvId cvId) throws MiddlewareException, MiddlewareQueryException{
        validateInputFields(name, definition);
        CVTermDao dao = getCvTermDao();
        
        List<Integer> termIds = dao.getTermsByNameOrSynonym(name, cvId.getId());

        CVTerm cvTerm = null; 
        if (termIds == null || termIds.isEmpty()){ // add
            Integer generatedId = dao.getNextId("cvTermId");
            cvTerm = create(generatedId, name, definition, cvId.getId(), false, false); 
        } else if (termIds.size() == 1){ // update
            cvTerm = create(termIds.get(0), name, definition, cvId.getId(), false, false); 
        } else {
            throw new MiddlewareException("Term with non-unique name (" + name +") retrieved for cv_id = " + cvId.getId());
        }
        dao.saveOrUpdate(cvTerm);
        return new Term(cvTerm.getCvTermId(), cvTerm.getName(), cvTerm.getDefinition());
    }
    
    public Term update(Term term) throws MiddlewareException, MiddlewareQueryException{
        validateInputFields(term.getName(), term.getDefinition());
        CVTermDao dao = getCvTermDao();

        CVTerm cvTerm = dao.getById(term.getId());
        
        if (cvTerm != null) { //update
             cvTerm.setName(term.getName());
             cvTerm.setDefinition(term.getDefinition());
             dao.update(cvTerm);
        } else {
            throw new MiddlewareException("Error: Term not found in the local database. ");
        }
        return new Term(cvTerm.getCvTermId(), cvTerm.getName(), cvTerm.getDefinition());
    }
	
	public CVTerm create(int id, String name, String definition, int cvId,  boolean isObsolete, boolean isRelationshipType){
		CVTerm cvTerm = new CVTerm();
		cvTerm.setCvTermId(id);
		cvTerm.setName(name);
		cvTerm.setDefinition(definition);
		cvTerm.setCv(cvId);
		cvTerm.setIsObsolete(isObsolete);
		cvTerm.setIsRelationshipType(isRelationshipType);
		return cvTerm;
	}

	private void validateInputFields(String name, String definition) throws MiddlewareException{
		StringBuffer errorMessage = new StringBuffer("");
		if (name == null || name.equals("")) {
			errorMessage.append("\nname is null or empty");
		}
		if (definition == null || definition.equals("")) {
			errorMessage.append("\ndefinition is null or empty");
		}
		if (errorMessage.length() > 0){
			throw new MiddlewareException(errorMessage.toString());
		} 
	}
	
	public void delete(CVTerm cvTerm, CvId cvId) throws MiddlewareQueryException {
        CVTermDao dao = getCvTermDao();
        try {
            dao.makeTransient(cvTerm);
        } catch (MiddlewareQueryException e) {
            throw new MiddlewareQueryException(e.getMessage(), e);
        }
	} 
}
