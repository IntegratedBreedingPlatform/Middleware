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

import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermRelationship;
import org.generationcp.middleware.domain.ontology.TermRelationshipId;
import org.generationcp.middleware.exceptions.MiddlewareException;

import java.util.List;
import java.util.Set;

/**
 * This is the API for retrieving ontology scale data.
 *
 *
 */
public interface TermDataManager {

	/**
	 * Returns term by id
	 */
	Term getTermById(Integer termId);

	List<Term> getTermByIds(List<Integer> termIds) throws MiddlewareException;

	/**
	 * Returns term by name and cvId
	 */
	Term getTermByNameAndCvId(String name, int cvId);

	/**
	 * Returns term by name
	 */
	Term getTermByName(String name);

	/**
	 * Returns term by cvId
	 */
	List<Term> getTermByCvId(int cvId);

	/**
	 * Checks if term is referred by another term.
	 */
	boolean isTermReferred(int termId);

	/**
	 * @param objectId method, property or scale id
	 * @param relationshipId which will show type of relationship
	 * @return This will return list of relationships which have type of relationship is relationshipId and object_id is objectId
	 */
	List<TermRelationship> getRelationshipsWithObjectAndType(Integer objectId, TermRelationshipId relationshipId);

	/**
	 * Return list of scale's values that are being used in non-deleted studies
	 *
	 * @param scaleId
	 * @return
	 */
	public Set<String> getCategoriesInUse(int scaleId);

}
