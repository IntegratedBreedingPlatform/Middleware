package org.generationcp.middleware.v2.domain.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.v2.domain.CVTermId;
import org.generationcp.middleware.v2.domain.CvTerm;
import org.generationcp.middleware.v2.domain.Enumeration;
import org.generationcp.middleware.v2.domain.StandardVariable;
import org.generationcp.middleware.v2.domain.VariableConstraints;
import org.generationcp.middleware.v2.pojos.CVTerm;
import org.generationcp.middleware.v2.pojos.CVTermProperty;
import org.generationcp.middleware.v2.pojos.CVTermRelationship;

public class StandardVariableBuilder extends Builder {

	public StandardVariableBuilder(HibernateSessionProvider sessionProviderForLocal,
			                   HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}
	
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
			standardVariable.setProperty(createTerm(cvTermRelationships, CVTermId.HAS_PROPERTY));
			standardVariable.setMethod(createTerm(cvTermRelationships, CVTermId.HAS_METHOD));
			standardVariable.setScale(createTerm(cvTermRelationships, CVTermId.HAS_SCALE));
			standardVariable.setDataType(createTerm(cvTermRelationships, CVTermId.HAS_TYPE));
			standardVariable.setStoredIn(createTerm(cvTermRelationships, CVTermId.STORED_IN));
			addEnumerations(standardVariable, cvTermRelationships);
		}
	}

	private void addEnumerations(StandardVariable standardVariable, List<CVTermRelationship> cvTermRelationships) throws MiddlewareQueryException {
		if (hasEnumerations(cvTermRelationships)) {
			List<Enumeration> enumerations = new ArrayList<Enumeration>();
			for (CVTermRelationship cvTermRelationship : cvTermRelationships) {
				if (cvTermRelationship.getTypeId().equals(CVTermId.HAS_VALUE.getId())) {
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
		CVTermProperty property = findProperty(cvTerm.getProperties(), CVTermId.ORDER);
		if (property != null) {
			return Integer.parseInt(property.getValue());
		}
		return 0;
	}

	private CVTermProperty findProperty(List<CVTermProperty> properties, CVTermId cvTermId) {
		if (properties != null) {
			for (CVTermProperty property : properties) {
				if (property.getTypeId() == cvTermId.getId()) {
					return property;
				}
			}
		}
		return null;
	}

	private boolean hasEnumerations(List<CVTermRelationship> cvTermRelationships) {
		return findTermId(cvTermRelationships, CVTermId.HAS_VALUE) != null;
	}

	private void addConstraints(StandardVariable standardVariable, CVTerm cvTerm) {
		if (cvTerm.getProperties() != null && !cvTerm.getProperties().isEmpty()) {
			Integer minValue = getPropertyValue(cvTerm.getProperties(), CVTermId.MIN_VALUE);
			Integer maxValue = getPropertyValue(cvTerm.getProperties(), CVTermId.MAX_VALUE);
			if (minValue != null || maxValue != null) {
				standardVariable.setConstraints(new VariableConstraints(minValue, maxValue));
			}
		}
	}

	private Integer getPropertyValue(List<CVTermProperty> properties, CVTermId termId) {
		for (CVTermProperty property : properties) {
			if (property.getTypeId().equals(termId.getId())) {
				return Integer.parseInt(property.getValue());
			}
		}
		return null;
	}
	
	private Integer findTermId(List<CVTermRelationship> cvTermRelationships, CVTermId relationship) {
		for (CVTermRelationship cvTermRelationship : cvTermRelationships) {
			if (cvTermRelationship.getTypeId().equals(relationship.getId())) {
				return cvTermRelationship.getObjectId();
			}
		}
		return null;
	}

	private CvTerm createTerm(List<CVTermRelationship> cvTermRelationships, CVTermId relationship) throws MiddlewareQueryException {
		Integer id = findTermId(cvTermRelationships, relationship);
		return createTerm(id);
	}

	private CvTerm createTerm(Integer id) throws MiddlewareQueryException {
		CVTerm cvTerm = getCvTerm(id);
		return cvTerm != null ? new CvTerm(cvTerm.getCvTermId(), cvTerm.getName(), cvTerm.getDefinition()) : null;
	}
	
	private CVTerm getCvTerm(int id) throws MiddlewareQueryException {
		if (setWorkingDatabase(id)) {
		    return getCvTermDao().getById(id);
		}
		return null;
	}
}
