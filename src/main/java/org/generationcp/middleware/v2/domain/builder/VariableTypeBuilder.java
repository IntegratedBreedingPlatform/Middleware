package org.generationcp.middleware.v2.domain.builder;

import java.util.List;
import java.util.Set;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.v2.domain.CVTermId;
import org.generationcp.middleware.v2.domain.CvTerm;
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
		
		if (setWorkingDatabase(variableInfo.getStdVariableId())) {
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
			variableType.setProperty(getObject(cvTermRelationships, CVTermId.HAS_PROPERTY));
			variableType.setMethod(getObject(cvTermRelationships, CVTermId.HAS_METHOD));
			variableType.setScale(getObject(cvTermRelationships, CVTermId.HAS_SCALE));
			variableType.setDataType(getObject(cvTermRelationships, CVTermId.HAS_TYPE));
			variableType.setStoredIn(getObject(cvTermRelationships, CVTermId.STORED_IN));
			
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

	private CvTerm getObject(List<CVTermRelationship> cvTermRelationships, CVTermId relationship) throws MiddlewareQueryException {
		for (CVTermRelationship cvTermRelationship : cvTermRelationships) {
			if (cvTermRelationship.getTypeId().equals(relationship.getId())) {
				return getCvTerm(cvTermRelationship.getObjectId());
			}
		}
		return null;
	}

	private CvTerm getCvTerm(Integer id) throws MiddlewareQueryException {
		setWorkingDatabase(Database.CENTRAL);
		CVTerm term = getCvTermDao().getById(id);
		if (term != null) {
			setWorkingDatabase(Database.LOCAL);
			term = getCvTermDao().getById(id);
		}
		return term != null ? new CvTerm(term.getCvTermId(), term.getName(), term.getDefinition()) : null;
	}
}
