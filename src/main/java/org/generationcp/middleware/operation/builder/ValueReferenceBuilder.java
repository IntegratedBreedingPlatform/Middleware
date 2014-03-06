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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;

public class ValueReferenceBuilder extends Builder {

	public ValueReferenceBuilder(
			HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public List<ValueReference> getDistinctStandardVariableValues(int stdVarId) throws MiddlewareQueryException {
		Set<ValueReference> set = new HashSet<ValueReference>();
		
		setWorkingDatabase(stdVarId);
		List<CVTermRelationship> relationships = getCvTermRelationshipDao().getBySubject(stdVarId);
		Integer dataType = getRelationshipValue(relationships, TermId.HAS_TYPE.getId());
		
		if (dataType != null && dataType == TermId.CATEGORICAL_VARIABLE.getId()) {
			for (ValueReference ref : set) {
				if (ref.getKey() != null && NumberUtils.isNumber(ref.getKey())) {
					setWorkingDatabase(Integer.valueOf(ref.getKey()));
					CVTerm term = getCvTermDao().getById(Integer.valueOf(ref.getKey()));
					ref.setName(term.getName());
					ref.setDescription(term.getDefinition());
				}
			}
		}
		
		List<ValueReference> list = new ArrayList<ValueReference>(set);
		
		Collections.sort(list);
		assignIds(list);
		
		return list;
	}
	
	private void assignIds(List<ValueReference> list) {
		if (list != null && !list.isEmpty()) {
			int index = 1;
			for (ValueReference ref : list) {
				ref.setId(index++);
			}
		}
	}
	
	private Set<ValueReference> getStudyInformation(int stdVarId) throws MiddlewareQueryException {
		Set<ValueReference> list = new HashSet<ValueReference>();

		setWorkingDatabase(Database.LOCAL);
		list.addAll(getProjectPropertyDao().getDistinctStandardVariableValues(stdVarId));
		
		setWorkingDatabase(Database.CENTRAL);
		list.addAll(getProjectPropertyDao().getDistinctStandardVariableValues(stdVarId));
		
		return list;
	}
	
	private Set<ValueReference> getStudyNames() throws MiddlewareQueryException {
		Set<ValueReference> list = new HashSet<ValueReference>();

		setWorkingDatabase(Database.LOCAL);
		list.addAll(getDmsProjectDao().getDistinctProjectNames());
		
		setWorkingDatabase(Database.CENTRAL);
		list.addAll(getDmsProjectDao().getDistinctProjectNames());
		
		return list;
	}

	private Set<ValueReference> getStudyDescriptions() throws MiddlewareQueryException {
		Set<ValueReference> list = new HashSet<ValueReference>();

		setWorkingDatabase(Database.LOCAL);
		list.addAll(getDmsProjectDao().getDistinctProjectDescriptions());
		
		setWorkingDatabase(Database.CENTRAL);
		list.addAll(getDmsProjectDao().getDistinctProjectDescriptions());
		
		return list;
	}
	
	private Set<ValueReference> getTrialEnvironmentValues(int stdVarId) throws MiddlewareQueryException {
		Set<ValueReference> list = new HashSet<ValueReference>();

		setWorkingDatabase(Database.LOCAL);
		list.addAll(getGeolocationPropertyDao().getDistinctPropertyValues(stdVarId));
		
		setWorkingDatabase(Database.CENTRAL);
		list.addAll(getGeolocationPropertyDao().getDistinctPropertyValues(stdVarId));
		
		return list;
	}

	private Set<ValueReference> getTrialInstances() throws MiddlewareQueryException {
		Set<ValueReference> list = new HashSet<ValueReference>();

		setWorkingDatabase(Database.LOCAL);
		list.addAll(getGeolocationDao().getDistinctTrialInstances());
		
		setWorkingDatabase(Database.CENTRAL);
		list.addAll(getGeolocationDao().getDistinctTrialInstances());
		
		return list;
	}
	
	private Set<ValueReference> getLatitudes() throws MiddlewareQueryException {
		Set<ValueReference> list = new HashSet<ValueReference>();

		setWorkingDatabase(Database.LOCAL);
		list.addAll(getGeolocationDao().getDistinctLatitudes());
		
		setWorkingDatabase(Database.CENTRAL);
		list.addAll(getGeolocationDao().getDistinctLatitudes());
		
		return list;
	}
	
	private Set<ValueReference> getLongitudes() throws MiddlewareQueryException {
		Set<ValueReference> list = new HashSet<ValueReference>();

		setWorkingDatabase(Database.LOCAL);
		list.addAll(getGeolocationDao().getDistinctLongitudes());
		
		setWorkingDatabase(Database.CENTRAL);
		list.addAll(getGeolocationDao().getDistinctLongitudes());
		
		return list;
	}
	
	private Set<ValueReference> getAltitudes() throws MiddlewareQueryException {
		Set<ValueReference> list = new HashSet<ValueReference>();

		setWorkingDatabase(Database.LOCAL);
		list.addAll(getGeolocationDao().getDistinctAltitudes());
		
		setWorkingDatabase(Database.CENTRAL);
		list.addAll(getGeolocationDao().getDistinctAltitudes());
		
		return list;
	}

	private Set<ValueReference> getDatumStorages() throws MiddlewareQueryException {
		Set<ValueReference> list = new HashSet<ValueReference>();

		setWorkingDatabase(Database.LOCAL);
		list.addAll(getGeolocationDao().getDistinctDatums());
		
		setWorkingDatabase(Database.CENTRAL);
		list.addAll(getGeolocationDao().getDistinctDatums());
		
		return list;
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
}
