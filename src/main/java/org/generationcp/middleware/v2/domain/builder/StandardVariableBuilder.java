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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.v2.domain.Enumeration;
import org.generationcp.middleware.v2.domain.FactorType;
import org.generationcp.middleware.v2.domain.NameSynonym;
import org.generationcp.middleware.v2.domain.NameType;
import org.generationcp.middleware.v2.domain.StandardVariable;
import org.generationcp.middleware.v2.domain.Term;
import org.generationcp.middleware.v2.domain.TermId;
import org.generationcp.middleware.v2.domain.VariableConstraints;
import org.generationcp.middleware.v2.pojos.CVTerm;
import org.generationcp.middleware.v2.pojos.CVTermProperty;
import org.generationcp.middleware.v2.pojos.CVTermRelationship;
import org.generationcp.middleware.v2.pojos.CVTermSynonym;

public class StandardVariableBuilder extends Builder {


	public StandardVariableBuilder(HibernateSessionProvider sessionProviderForLocal,
			                   HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	// If the standard variable is already in the cache, return. Else, create the variable, add to cache then return
	public StandardVariable create(int standardVariableId) throws MiddlewareQueryException {

		StandardVariable standardVariable = new StandardVariable();
		standardVariable.setId(standardVariableId);
		CVTerm cvTerm = getCvTerm(standardVariableId);
		if (cvTerm != null) {
			standardVariable.setName(cvTerm.getName());
			standardVariable.setDescription(cvTerm.getDefinition());
			
			addConstraints(standardVariable, cvTerm);
			addRelatedTerms(standardVariable, cvTerm);
		}
		return standardVariable;
	}

	private void addRelatedTerms(StandardVariable standardVariable, CVTerm cvTerm) throws MiddlewareQueryException {
		if (setWorkingDatabase(standardVariable.getId())) {
			List<CVTermRelationship> cvTermRelationships  = getCvTermRelationshipDao().getBySubject(standardVariable.getId());
			standardVariable.setProperty(createTerm(cvTermRelationships, TermId.HAS_PROPERTY));	
			standardVariable.setMethod(createTerm(cvTermRelationships, TermId.HAS_METHOD));
			standardVariable.setScale(createTerm(cvTermRelationships, TermId.HAS_SCALE));
			standardVariable.setDataType(createTerm(cvTermRelationships, TermId.HAS_TYPE));
			standardVariable.setStoredIn(createTerm(cvTermRelationships, TermId.STORED_IN));
			standardVariable.setFactorType(createFactorType(standardVariable.getStoredIn().getId()));
			addEnumerations(standardVariable, cvTermRelationships);
		}
	}

	private void addEnumerations(StandardVariable standardVariable, List<CVTermRelationship> cvTermRelationships) throws MiddlewareQueryException {
		if (hasEnumerations(cvTermRelationships)) {
			List<Enumeration> enumerations = new ArrayList<Enumeration>();
			for (CVTermRelationship cvTermRelationship : cvTermRelationships) {
				if (cvTermRelationship.getTypeId().equals(TermId.HAS_VALUE.getId())) {
					Integer id = cvTermRelationship.getObjectId();
					enumerations.add(createEnumeration(getCvTerm(id)));
				}
			}
			Collections.sort(enumerations);
			standardVariable.setEnumerations(enumerations);
		}
	}

	private Enumeration createEnumeration(CVTerm cvTerm) {
		return new Enumeration(cvTerm.getCvTermId(), cvTerm.getName(), cvTerm.getDefinition(), getRank(cvTerm));
	}

	private int getRank(CVTerm cvTerm) {
		CVTermProperty property = findProperty(cvTerm.getProperties(), TermId.ORDER.getId());
		if (property != null) {
			return Integer.parseInt(property.getValue());
		}
		return 0;
	}

	private CVTermProperty findProperty(List<CVTermProperty> properties, int typeId) {
		if (properties != null) {
			for (CVTermProperty property : properties) {
				if (property.getTypeId() == typeId) {
					return property;
				}
			}
		}
		return null;
	}

	private boolean hasEnumerations(List<CVTermRelationship> cvTermRelationships) {
		return findTermId(cvTermRelationships, TermId.HAS_VALUE) != null;
	}

	private void addConstraints(StandardVariable standardVariable, CVTerm cvTerm) {
		if (cvTerm.getProperties() != null && !cvTerm.getProperties().isEmpty()) {
			Integer minValue = getPropertyValue(cvTerm.getProperties(), TermId.MIN_VALUE);
			Integer maxValue = getPropertyValue(cvTerm.getProperties(), TermId.MAX_VALUE);
			if (minValue != null || maxValue != null) {
				standardVariable.setConstraints(new VariableConstraints(minValue, maxValue));
			}
		}
	}

	private Integer getPropertyValue(List<CVTermProperty> properties, TermId termId) {
		for (CVTermProperty property : properties) {
			if (property.getTypeId().equals(termId.getId())) {
				return Integer.parseInt(property.getValue());
			}
		}
		return null;
	}
	
	private Integer findTermId(List<CVTermRelationship> cvTermRelationships, TermId relationship) {
		for (CVTermRelationship cvTermRelationship : cvTermRelationships) {
			if (cvTermRelationship.getTypeId().equals(relationship.getId())) {
				return cvTermRelationship.getObjectId();
			}
		}
		return null;
	}

	private Term createTerm(List<CVTermRelationship> cvTermRelationships, TermId relationship) throws MiddlewareQueryException {
		Integer id = findTermId(cvTermRelationships, relationship);
		return createTerm(id);
	}

	private Term createTerm(Integer id) throws MiddlewareQueryException {
		CVTerm cvTerm = getCvTerm(id);
		return cvTerm != null ? new Term(cvTerm.getCvTermId(), cvTerm.getName(), cvTerm.getDefinition(), createSynonyms(cvTerm.getSynonyms())) : null;
	}
	
	private List<NameSynonym> createSynonyms(List<CVTermSynonym> synonyms) {
		List<NameSynonym> nameSynonyms = null;
		if (synonyms != null && synonyms.size() > 0) {
			nameSynonyms = new ArrayList<NameSynonym>();
			for (CVTermSynonym synonym : synonyms) {
				nameSynonyms.add(new NameSynonym(synonym.getSynonym(), NameType.find(synonym.getTypeId())));
			}
		}
		return nameSynonyms;
	}

	private CVTerm getCvTerm(int id) throws MiddlewareQueryException {
		if (setWorkingDatabase(id)) {
		    return getCvTermDao().getById(id);
		}
		return null;
	}
	
	private FactorType createFactorType(int storedInTerm) {
		for (FactorType factorType : FactorType.values()) {
			if (factorType.getFactorStorages().contains(storedInTerm)) {
				return factorType;
			}
		}
		return null;
	}
}
