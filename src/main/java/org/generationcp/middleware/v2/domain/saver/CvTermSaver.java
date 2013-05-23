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
package org.generationcp.middleware.v2.domain.saver;


import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.v2.dao.CVTermDao;
import org.generationcp.middleware.v2.domain.CvId;
import org.generationcp.middleware.v2.domain.Term;
import org.generationcp.middleware.v2.pojos.CVTerm;

public class CvTermSaver extends Saver {

	public CvTermSaver(
			HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public Term save(String name, String definition)  throws MiddlewareException, MiddlewareQueryException{ 
		requireLocalDatabaseInstance();

		validateInputFields(name, definition);
		
        CVTermDao dao = getCvTermDao();
        Integer generatedId;
		try {
			generatedId = dao.getNegativeId("cvTermId");
		} catch (MiddlewareQueryException e) {
			e.printStackTrace();
			throw new MiddlewareQueryException(e.getMessage());
		}
		CVTerm cvTerm = create(generatedId, name, definition, CvId.METHODS.getId(), false, false); 
		dao.save(cvTerm);

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
		if (!errorMessage.toString().equals("")){
			throw new MiddlewareException(errorMessage.toString());
		} 
	}
	
	
}
