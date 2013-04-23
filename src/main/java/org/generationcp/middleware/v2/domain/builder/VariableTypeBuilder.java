package org.generationcp.middleware.v2.domain.builder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.v2.domain.CVTermId;
import org.generationcp.middleware.v2.domain.VariableConstraints;
import org.generationcp.middleware.v2.domain.VariableInfo;
import org.generationcp.middleware.v2.domain.VariableType;
import org.generationcp.middleware.v2.pojos.CVTerm;
import org.generationcp.middleware.v2.pojos.CVTermProperty;
import org.generationcp.middleware.v2.pojos.CVTermRelationship;
import org.generationcp.middleware.v2.pojos.ProjectProperty;

public class VariableTypeBuilder extends Builder {

	public VariableTypeBuilder(HibernateSessionProvider sessionProviderForLocal,
			                   HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	public Set<VariableType> create(List<ProjectProperty> properties) throws MiddlewareQueryException {
		Set<VariableInfo> variableInfoList = getVariableInfoBuilder().create(properties);
		
		Set<VariableType> variableTypes = new HashSet<VariableType>();
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
		
		if (setWorkingDatabase(variableInfo.getStdVariableId())) {
			variableInfo.print(0);
			CVTerm cvTerm = getCvTermDao().getById(variableInfo.getStdVariableId());
			variableType.setName(cvTerm.getName());
			variableType.setDescription(cvTerm.getDefinition());
			
			if (cvTerm.getProperties() != null && !cvTerm.getProperties().isEmpty()) {
				Integer minValue = getPropertyValue(cvTerm.getProperties(), CVTermId.MIN_VALUE);
				Integer maxValue = getPropertyValue(cvTerm.getProperties(), CVTermId.MAX_VALUE);
				if (minValue != null || maxValue != null) {
					variableType.setConstraints(new VariableConstraints(minValue, maxValue));
				}
			}
			
			List<CVTermRelationship> cvTermRelationships  = getCvTermRelationshipDao().getBySubject(variableType.getId());
			variableType.setPropertyId(getObjectId(cvTermRelationships, CVTermId.HAS_PROPERTY));
			variableType.setMethodId(getObjectId(cvTermRelationships, CVTermId.HAS_METHOD));
			variableType.setScaleId(getObjectId(cvTermRelationships, CVTermId.HAS_SCALE));
			variableType.setDataTypeId(getObjectId(cvTermRelationships, CVTermId.HAS_TYPE));
			variableType.setStoredInId(getObjectId(cvTermRelationships, CVTermId.STORED_IN));
			
		}
		
		return variableType;
	}

	private Integer getPropertyValue(List<CVTermProperty> properties, CVTermId termId) {
		for (CVTermProperty property : properties) {
			if (property.getTypeId().equals(termId.getId())) {
				return Integer.parseInt(property.getValue());
			}
		}
		return null;
	}

	private Integer getObjectId(List<CVTermRelationship> cvTermRelationships, CVTermId relationship) {
		for (CVTermRelationship cvTermRelationship : cvTermRelationships) {
			if (cvTermRelationship.getTypeId().equals(relationship.getId())) {
				return cvTermRelationship.getObjectId();
			}
		}
		return null;
	}

}
