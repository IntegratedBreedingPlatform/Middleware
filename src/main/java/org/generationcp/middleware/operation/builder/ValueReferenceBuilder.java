/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.operation.builder;

import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;

import java.util.*;

public class ValueReferenceBuilder extends Builder {

	public ValueReferenceBuilder(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public List<ValueReference> getDistinctStandardVariableValues(int stdVarId) throws MiddlewareQueryException {
		setWorkingDatabase(stdVarId);
		List<CVTermRelationship> relationships = getCvTermRelationshipDao().getBySubject(stdVarId);
		Integer dataType = getRelationshipValue(relationships, TermId.HAS_TYPE.getId());
		
		if (dataType != null && dataType == TermId.CATEGORICAL_VARIABLE.getId()) {
			setWorkingDatabase(stdVarId);
			if (stdVarId > 0) {
				setWorkingDatabase(Database.LOCAL);
				relationships.addAll(getCvTermRelationshipDao().getBySubject(stdVarId));				
			}
			Set<ValueReference> set = getRelationshipValues(relationships, TermId.HAS_VALUE.getId());
			for (ValueReference ref : set) {
				setWorkingDatabase(ref.getId());
				CVTerm term = getCvTermDao().getById(ref.getId());
				if (term != null) {
					ref.setKey(ref.getId().toString());
					ref.setName(term.getName());
					ref.setDescription(term.getDefinition());
				}
			}
			List<ValueReference> list = new ArrayList<ValueReference>(set);
			Collections.sort(list);
			return list;
		}
		
		return new ArrayList<ValueReference>();
	}
	
	private Integer getRelationshipValue(List<CVTermRelationship> relationships, int typeId) {
		if (relationships != null && !relationships.isEmpty()) {
			for (CVTermRelationship relationship : relationships) {
				if (relationship.getTypeId().equals(typeId)) {
					return relationship.getObjectId();
				}
			}
		}
		return null;
	}

	private Set<ValueReference> getRelationshipValues(List<CVTermRelationship> relationships, int typeId) {
		Set<ValueReference> values = new HashSet<ValueReference>();
		if (relationships != null && !relationships.isEmpty()) {
			for (CVTermRelationship relationship : relationships) {
				if (relationship.getTypeId().equals(typeId)) {
					values.add(new ValueReference(relationship.getObjectId(), null, null));
				}
			}
		}
		return values;
	}
}
