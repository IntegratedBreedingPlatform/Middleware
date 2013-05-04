package org.generationcp.middleware.v2.domain.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.v2.domain.CVTermId;
import org.generationcp.middleware.v2.domain.CvTerm;
import org.generationcp.middleware.v2.domain.Enumeration;
import org.generationcp.middleware.v2.domain.VariableConstraints;
import org.generationcp.middleware.v2.domain.VariableInfo;
import org.generationcp.middleware.v2.domain.VariableType;
import org.generationcp.middleware.v2.domain.VariableTypeList;
import org.generationcp.middleware.v2.pojos.CVTerm;
import org.generationcp.middleware.v2.pojos.CVTermProperty;
import org.generationcp.middleware.v2.pojos.CVTermRelationship;
import org.generationcp.middleware.v2.pojos.ProjectProperty;

public class VariableTypeBuilder extends Builder {

	public VariableTypeBuilder(HibernateSessionProvider sessionProviderForLocal,
			                   HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	public VariableTypeList create(List<ProjectProperty> properties) throws MiddlewareQueryException {
		Set<VariableInfo> variableInfoList = getVariableInfoBuilder().create(properties);
		
		VariableTypeList variableTypes = new VariableTypeList();
		for (VariableInfo variableInfo : variableInfoList) {
			variableTypes.add(create(variableInfo));
		}
		return variableTypes;
	}

	public VariableType create(VariableInfo variableInfo) throws MiddlewareQueryException {
		VariableType variableType = new VariableType();
		variableType.setId(variableInfo.getStdVariableId());
		variableType.setLocalName(variableInfo.getLocalName());
		variableType.setLocalDescription(variableInfo.getLocalDescription());
		variableType.setRank(variableInfo.getRank());
		
		CVTerm cvTerm = getCvTerm(variableInfo.getStdVariableId());
		if (cvTerm != null) {
			variableType.setName(cvTerm.getName());
			variableType.setDescription(cvTerm.getDefinition());
			
			addConstraints(variableType, cvTerm);
			addRelatedTerms(variableType, cvTerm);
		}
		return variableType;
	}

	private void addRelatedTerms(VariableType variableType, CVTerm cvTerm) throws MiddlewareQueryException {
		if (setWorkingDatabase(variableType.getId())) {
			List<CVTermRelationship> cvTermRelationships  = getCvTermRelationshipDao().getBySubject(variableType.getId());
			variableType.setProperty(createTerm(cvTermRelationships, CVTermId.HAS_PROPERTY));
			variableType.setMethod(createTerm(cvTermRelationships, CVTermId.HAS_METHOD));
			variableType.setScale(createTerm(cvTermRelationships, CVTermId.HAS_SCALE));
			variableType.setDataType(createTerm(cvTermRelationships, CVTermId.HAS_TYPE));
			variableType.setStoredIn(createTerm(cvTermRelationships, CVTermId.STORED_IN));
			addEnumerations(variableType, cvTermRelationships);
		}
	}

	private void addEnumerations(VariableType variableType, List<CVTermRelationship> cvTermRelationships) throws MiddlewareQueryException {
		if (hasEnumerations(cvTermRelationships)) {
			List<Enumeration> enumerations = new ArrayList<Enumeration>();
			for (CVTermRelationship cvTermRelationship : cvTermRelationships) {
				if (cvTermRelationship.getTypeId().equals(CVTermId.HAS_VALUE.getId())) {
					Integer id = cvTermRelationship.getObjectId();
					enumerations.add(createEnumeration(getCvTerm(id)));
				}
			}
			variableType.setEnumerations(enumerations);
		}
	}

	private Enumeration createEnumeration(CVTerm cvTerm) {
		return new Enumeration(cvTerm.getCvTermId(), cvTerm.getName(), cvTerm.getDefinition());
	}

	private boolean hasEnumerations(List<CVTermRelationship> cvTermRelationships) {
		return findTermId(cvTermRelationships, CVTermId.HAS_VALUE) != null;
	}

	private void addConstraints(VariableType variableType, CVTerm cvTerm) {
		if (cvTerm.getProperties() != null && !cvTerm.getProperties().isEmpty()) {
			Integer minValue = getPropertyValue(cvTerm.getProperties(), CVTermId.MIN_VALUE);
			Integer maxValue = getPropertyValue(cvTerm.getProperties(), CVTermId.MAX_VALUE);
			if (minValue != null || maxValue != null) {
				variableType.setConstraints(new VariableConstraints(minValue, maxValue));
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
