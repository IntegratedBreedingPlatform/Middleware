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
package org.generationcp.middleware.operation.searcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;

public class ExperimentSearcher extends Searcher {

	public ExperimentSearcher(
			HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	//TODO: Not all factors were considered in this method. to be added as needed
	public List<Integer> searchExperimentsByFactor(Integer factorId, String value) throws MiddlewareQueryException {
		Integer storedInId = getStoredInId(factorId);
		
		if (TermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId() == storedInId) {
			return findExperimentsByGeolocationFactorValue(factorId, value);
			
		} else if (TermId.TRIAL_DESIGN_INFO_STORAGE.getId() == storedInId) {
			return findExperimentsByExperimentFactorValue(factorId, value);
			
		} else if (TermId.GERMPLASM_ENTRY_STORAGE.getId() == storedInId) {
			return findExperimentsByStockFactorValue(factorId, value);
		
		} else if (TermId.ENTRY_GID_STORAGE.getId() == storedInId) {
			return findExperimentsByStock("dbxrefId", value);
		
		} else if (TermId.ENTRY_NUMBER_STORAGE.getId() == storedInId) {
			return findExperimentsByStock("uniqueName", value);
			
		} else if (TermId.ENTRY_DESIGNATION_STORAGE.getId() == storedInId) {
			return findExperimentsByStock("name", value);
			
		} else if (TermId.ENTRY_CODE_STORAGE.getId() == storedInId) {
			return findExperimentsByStock("value", value);
		}
		
		return new ArrayList<Integer>();		
	}


	private Integer getStoredInId(Integer factorId) throws MiddlewareQueryException {
		setWorkingDatabase(Database.CENTRAL);
		List<Integer> termIds = getCvTermRelationshipDao().getObjectIdByTypeAndSubject(TermId.STORED_IN.getId(), factorId);
		if (termIds == null || termIds.size() == 0) {
			setWorkingDatabase(Database.LOCAL);
			termIds = getCvTermRelationshipDao().getObjectIdByTypeAndSubject(TermId.STORED_IN.getId(), factorId);
		}

		return (termIds != null && termIds.size() > 0 ? termIds.get(0) : null);
	}

	private List<Integer> findExperimentsByGeolocationFactorValue(Integer factorId, String value) throws MiddlewareQueryException {
		Set<Integer> geolocationIds = new HashSet<Integer>();
		setWorkingDatabase(Database.CENTRAL);
		geolocationIds.addAll(getGeolocationPropertyDao().getGeolocationIdsByPropertyTypeAndValue(factorId, value));
		setWorkingDatabase(Database.LOCAL);
		geolocationIds.addAll(getGeolocationPropertyDao().getGeolocationIdsByPropertyTypeAndValue(factorId, value));
		
		Set<Integer> experimentIds = new HashSet<Integer>();
		setWorkingDatabase(Database.CENTRAL);
		experimentIds.addAll(getExperimentDao().getExperimentIdsByGeolocationIds(geolocationIds));
		setWorkingDatabase(Database.LOCAL);
		experimentIds.addAll(getExperimentDao().getExperimentIdsByGeolocationIds(geolocationIds));
		
		return new ArrayList<Integer>(experimentIds);
	}
	
	private List<Integer> findExperimentsByStockFactorValue(Integer factorId, String value) throws MiddlewareQueryException {
		Set<Integer> stockIds = new HashSet<Integer>();
		setWorkingDatabase(Database.CENTRAL);
		stockIds.addAll(getStockPropertyDao().getStockIdsByPropertyTypeAndValue(factorId, value));
		setWorkingDatabase(Database.LOCAL);
		stockIds.addAll(getStockPropertyDao().getStockIdsByPropertyTypeAndValue(factorId, value));
		
		return getExperimentIdsByStockIds(stockIds);
	}
	
	private List<Integer> findExperimentsByExperimentFactorValue(Integer factorId, String value) throws MiddlewareQueryException {
		Set<Integer> experimentIds = new HashSet<Integer>();
		setWorkingDatabase(Database.CENTRAL);
		experimentIds.addAll(getExperimentPropertyDao().getExperimentIdsByPropertyTypeAndValue(factorId, value));
		setWorkingDatabase(Database.LOCAL);
		experimentIds.addAll(getExperimentPropertyDao().getExperimentIdsByPropertyTypeAndValue(factorId, value));
		
		return new ArrayList<Integer>(experimentIds);
	}
	
	private List<Integer> getExperimentIdsByStockIds(Collection<Integer> stockIds) throws MiddlewareQueryException {
		Set<Integer> experimentIds = new HashSet<Integer>();
		setWorkingDatabase(Database.CENTRAL);
		experimentIds.addAll(getExperimentStockDao().getExperimentIdsByStockIds(stockIds));
		setWorkingDatabase(Database.LOCAL);
		experimentIds.addAll(getExperimentStockDao().getExperimentIdsByStockIds(stockIds));
		
		return new ArrayList<Integer>(experimentIds);
	}
	
	private List<Integer> findExperimentsByStock(String columnName, String value) throws MiddlewareQueryException {
		Set<Integer> stockIds = new HashSet<Integer>();
		setWorkingDatabase(Database.CENTRAL);
		stockIds.addAll(getStockDao().getStockIdsByProperty(columnName, value));
		setWorkingDatabase(Database.LOCAL);
		stockIds.addAll(getStockDao().getStockIdsByProperty(columnName, value));

		return getExperimentIdsByStockIds(stockIds);
	}
}
