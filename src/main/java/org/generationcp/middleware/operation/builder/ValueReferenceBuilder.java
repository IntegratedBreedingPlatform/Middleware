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
import java.util.List;

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
		List<ValueReference> list = new ArrayList<ValueReference>();
		
		setWorkingDatabase(stdVarId);
		List<CVTermRelationship> relationships = getCvTermRelationshipDao().getBySubject(stdVarId);
		Integer storedIn = getRelationshipValue(relationships, TermId.STORED_IN.getId());
		Integer dataType = getRelationshipValue(relationships, TermId.HAS_TYPE.getId());
		
		//Currently only applicable to Study and Trial Environment variables
		if (storedIn != null) {
			if (TermId.STUDY_INFO_STORAGE.getId() == storedIn || TermId.DATASET_INFO_STORAGE.getId() == storedIn) {
				list = getStudyInformation(stdVarId);
			}
			else if (TermId.STUDY_NAME_STORAGE.getId() == storedIn || TermId.DATASET_NAME_STORAGE.getId() == storedIn) {
				list = getStudyNames();
			}
			else if (TermId.STUDY_TITLE_STORAGE.getId() == storedIn || TermId.DATASET_TITLE_STORAGE.getId() == storedIn) {
				list = getStudyDescriptions();
			}
			else if (TermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId() == storedIn) {
				list = getTrialEnvironmentValues(stdVarId);
			}
			else if (TermId.TRIAL_INSTANCE_STORAGE.getId() == storedIn) {
				list = getTrialInstances();
			}
			else if (TermId.LATITUDE_STORAGE.getId() == storedIn) {
				list = getLatitudes();
			}
			else if (TermId.LONGITUDE_STORAGE.getId() == storedIn) {
				list = getLongitudes();
			}
			else if (TermId.DATUM_STORAGE.getId() == storedIn) {
				list = getDatumStorages();
			}
			else if (TermId.ALTITUDE_STORAGE.getId() == storedIn) {
				list = getAltitudes();
			}
		}
		
		if (dataType != null && dataType == TermId.CATEGORICAL_VARIABLE.getId()) {
			for (ValueReference ref : list) {
				if (ref.getKey() != null && NumberUtils.isNumber(ref.getKey())) {
					setWorkingDatabase(Integer.valueOf(ref.getKey()));
					CVTerm term = getCvTermDao().getById(Integer.valueOf(ref.getKey()));
					ref.setName(term.getName());
					ref.setDescription(term.getDefinition());
				}
			}
		}
		
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
	
	private List<ValueReference> getStudyInformation(int stdVarId) throws MiddlewareQueryException {
		List<ValueReference> list;

		setWorkingDatabase(Database.LOCAL);
		list = getProjectPropertyDao().getDistinctStandardVariableValues(stdVarId);
		
		setWorkingDatabase(Database.CENTRAL);
		list.addAll(getProjectPropertyDao().getDistinctStandardVariableValues(stdVarId));
		
		return list;
	}
	
	private List<ValueReference> getStudyNames() throws MiddlewareQueryException {
		List<ValueReference> list;

		setWorkingDatabase(Database.LOCAL);
		list = getDmsProjectDao().getDistinctProjectNames();
		
		setWorkingDatabase(Database.CENTRAL);
		list.addAll(getDmsProjectDao().getDistinctProjectNames());
		
		return list;
	}

	private List<ValueReference> getStudyDescriptions() throws MiddlewareQueryException {
		List<ValueReference> list;

		setWorkingDatabase(Database.LOCAL);
		list = getDmsProjectDao().getDistinctProjectDescriptions();
		
		setWorkingDatabase(Database.CENTRAL);
		list.addAll(getDmsProjectDao().getDistinctProjectDescriptions());
		
		return list;
	}
	
	private List<ValueReference> getTrialEnvironmentValues(int stdVarId) throws MiddlewareQueryException {
		List<ValueReference> list;

		setWorkingDatabase(Database.LOCAL);
		list = getGeolocationPropertyDao().getDistinctPropertyValues(stdVarId);
		
		setWorkingDatabase(Database.CENTRAL);
		list.addAll(getGeolocationPropertyDao().getDistinctPropertyValues(stdVarId));
		
		return list;
	}

	private List<ValueReference> getTrialInstances() throws MiddlewareQueryException {
		List<ValueReference> list;

		setWorkingDatabase(Database.LOCAL);
		list = getGeolocationDao().getDistinctTrialInstances();
		
		setWorkingDatabase(Database.CENTRAL);
		list.addAll(getGeolocationDao().getDistinctTrialInstances());
		
		return list;
	}
	
	private List<ValueReference> getLatitudes() throws MiddlewareQueryException {
		List<ValueReference> list;

		setWorkingDatabase(Database.LOCAL);
		list = getGeolocationDao().getDistinctLatitudes();
		
		setWorkingDatabase(Database.CENTRAL);
		list.addAll(getGeolocationDao().getDistinctLatitudes());
		
		return list;
	}
	
	private List<ValueReference> getLongitudes() throws MiddlewareQueryException {
		List<ValueReference> list;

		setWorkingDatabase(Database.LOCAL);
		list = getGeolocationDao().getDistinctLongitudes();
		
		setWorkingDatabase(Database.CENTRAL);
		list.addAll(getGeolocationDao().getDistinctLongitudes());
		
		return list;
	}
	
	private List<ValueReference> getAltitudes() throws MiddlewareQueryException {
		List<ValueReference> list;

		setWorkingDatabase(Database.LOCAL);
		list = getGeolocationDao().getDistinctAltitudes();
		
		setWorkingDatabase(Database.CENTRAL);
		list.addAll(getGeolocationDao().getDistinctAltitudes());
		
		return list;
	}

	private List<ValueReference> getDatumStorages() throws MiddlewareQueryException {
		List<ValueReference> list;

		setWorkingDatabase(Database.LOCAL);
		list = getGeolocationDao().getDistinctDatums();
		
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
