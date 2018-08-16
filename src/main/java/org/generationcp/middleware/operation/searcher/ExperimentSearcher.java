/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
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
import org.generationcp.middleware.manager.DaoFactory;

public class ExperimentSearcher extends Searcher {

	private DaoFactory daoFactory;

	public ExperimentSearcher(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
		daoFactory = new DaoFactory(sessionProviderForLocal);
	}

	// TODO: Not all factors were considered in this method. to be added as needed
	public List<Integer> searchExperimentsByFactor(Integer factorId, String value) throws MiddlewareQueryException {
		Integer storedInId = this.getStoredInId(factorId);

		if (TermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId() == storedInId) {
			return this.findExperimentsByGeolocationFactorValue(factorId, value);

		} else if (TermId.TRIAL_DESIGN_INFO_STORAGE.getId() == storedInId) {
			return this.findExperimentsByExperimentFactorValue(factorId, value);

		} else if (TermId.GERMPLASM_ENTRY_STORAGE.getId() == storedInId) {
			return this.findExperimentsByStockFactorValue(factorId, value);

		} else if (TermId.ENTRY_GID_STORAGE.getId() == storedInId) {
			return this.findExperimentsByStock("dbxrefId", value);

		} else if (TermId.ENTRY_NUMBER_STORAGE.getId() == storedInId) {
			return this.findExperimentsByStock("uniqueName", value);

		} else if (TermId.ENTRY_DESIGNATION_STORAGE.getId() == storedInId) {
			return this.findExperimentsByStock("name", value);

		} else if (TermId.ENTRY_CODE_STORAGE.getId() == storedInId) {
			return this.findExperimentsByStock("value", value);
		}

		return new ArrayList<Integer>();
	}

	private Integer getStoredInId(Integer factorId) throws MiddlewareQueryException {
		List<Integer> termIds = daoFactory.getCvTermRelationshipDao().getObjectIdByTypeAndSubject(TermId.STORED_IN.getId(), factorId);
		return termIds != null && !termIds.isEmpty() ? termIds.get(0) : null;
	}

	private List<Integer> findExperimentsByGeolocationFactorValue(Integer factorId, String value) throws MiddlewareQueryException {
		Set<Integer> geolocationIds = new HashSet<Integer>();
		geolocationIds.addAll(this.getGeolocationPropertyDao().getGeolocationIdsByPropertyTypeAndValue(factorId, value));

		Set<Integer> experimentIds = new HashSet<Integer>();
		experimentIds.addAll(this.getExperimentDao().getExperimentIdsByGeolocationIds(geolocationIds));

		return new ArrayList<Integer>(experimentIds);
	}

	private List<Integer> findExperimentsByStockFactorValue(Integer factorId, String value) throws MiddlewareQueryException {
		Set<Integer> stockIds = new HashSet<Integer>();
		stockIds.addAll(this.getStockPropertyDao().getStockIdsByPropertyTypeAndValue(factorId, value));

		return this.getExperimentIdsByStockIds(stockIds);
	}

	private List<Integer> findExperimentsByExperimentFactorValue(Integer factorId, String value) throws MiddlewareQueryException {
		return this.getExperimentPropertyDao().getExperimentIdsByPropertyTypeAndValue(factorId, value);
	}

	private List<Integer> getExperimentIdsByStockIds(Collection<Integer> stockIds) throws MiddlewareQueryException {
		return this.getExperimentStockDao().getExperimentIdsByStockIds(stockIds);
	}

	private List<Integer> findExperimentsByStock(String columnName, String value) throws MiddlewareQueryException {
		Set<Integer> stockIds = new HashSet<Integer>();
		stockIds.addAll(this.getStockDao().getStockIdsByProperty(columnName, value));
		return this.getExperimentIdsByStockIds(stockIds);
	}
}
