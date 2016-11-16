/*******************************************************************************
 * 
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.manager.ontology.api;

import java.util.List;

import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermRelationship;
import org.generationcp.middleware.domain.ontology.TermRelationshipId;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;

/**
 * This is the API for retrieving ontology scale data.
 *
 *
 */
public interface TermDataManager {

	/**
	 * Returns term by id
	 */
	Term getTermById(Integer termId) throws MiddlewareException;

	/**
	 * Returns term by name and cvId
	 */
	Term getTermByNameAndCvId(String name, int cvId) throws MiddlewareException;

	/**
	 * Returns term by cvId
	 */
	List<Term> getTermByCvId(int cvId) throws MiddlewareException;

	/**
	 * Checks if term has referred to other term.
	 */
	boolean isTermReferred(int termId) throws MiddlewareException;

	/**
	 * @param objectId method, property or scale id
	 * @param relationshipId which will show type of relationship
	 * @return This will return list of relationships which have type of relationship is relationshipId and object_id is objectId
	 * @throws MiddlewareException
	 */
	List<TermRelationship> getRelationshipsWithObjectAndType(Integer objectId, TermRelationshipId relationshipId)
			throws MiddlewareException;
	
	public List<String> getCategoriesReferredInPhenotype(int scaleId) throws MiddlewareQueryException;
}
