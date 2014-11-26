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


import org.generationcp.middleware.dao.oms.CVTermRelationshipDao;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;

public class CvTermRelationshipSaver extends Saver {

	public CvTermRelationshipSaver(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	// Returns the id
	public Integer save(Integer subjectId, Integer typeId, Integer objectId)  throws MiddlewareException, MiddlewareQueryException{ 
		requireLocalDatabaseInstance();

        CVTermRelationshipDao dao = getCvTermRelationshipDao();
        Integer generatedId;
		try {
			generatedId = dao.getNegativeId("cvTermRelationshipId");
		} catch (MiddlewareQueryException e) {
			throw new MiddlewareQueryException(e.getMessage(), e);
		}
		CVTermRelationship cvTermRelationship = create(generatedId, subjectId, typeId, objectId); 
		dao.save(cvTermRelationship);
		
		return cvTermRelationship.getCvTermRelationshipId();
	}
	
	public CVTermRelationship create(Integer relationshipId, Integer subjectId, Integer typeId, Integer objectId){
		CVTermRelationship relationship = new CVTermRelationship();
		relationship.setCvTermRelationshipId(relationshipId);
		relationship.setSubjectId(subjectId);
		relationship.setTypeId(typeId);
		relationship.setObjectId(objectId);
		return relationship;
	}
	
	public CVTermRelationship saveOrUpdateRelationship(CVTermRelationship cvTermRelationship) throws MiddlewareException, MiddlewareQueryException{
        requireLocalDatabaseInstance();
        CVTermRelationshipDao dao = getCvTermRelationshipDao();
        CVTermRelationship relationship = null;
        try {
            relationship = dao.saveOrUpdateRelationship(cvTermRelationship);
        } catch (MiddlewareQueryException e) {
            throw new MiddlewareQueryException(e.getMessage(), e);
        }
        return relationship;
    }

	public void deleteRelationship(CVTermRelationship cvTermRelationship) throws MiddlewareException, MiddlewareQueryException{
	    requireLocalDatabaseInstance();
    	    CVTermRelationshipDao dao = getCvTermRelationshipDao();
    	    try {
    	        dao.makeTransient(cvTermRelationship);
    	    } catch (MiddlewareQueryException e) {
    	        throw new MiddlewareQueryException(e.getMessage(), e);
    	    }
	}
}
