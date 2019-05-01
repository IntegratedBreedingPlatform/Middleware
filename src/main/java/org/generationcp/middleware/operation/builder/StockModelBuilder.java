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

package org.generationcp.middleware.operation.builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.StockModel;

public class StockModelBuilder {

	private DaoFactory daoFactory;
	
	public StockModelBuilder(HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	public StockModel get(int stockId) throws MiddlewareQueryException {
		StockModel stockModel = null;
		stockModel = this.daoFactory.getStockDao().getById(stockId);
		return stockModel;
	}

	public Map<Integer, StockModel> get(List<Integer> stockIds) throws MiddlewareQueryException {
		Map<Integer, StockModel> stockModels = new HashMap<Integer, StockModel>();

		if (stockIds != null && !stockIds.isEmpty()) {
			stockModels.putAll(this.daoFactory.getStockDao().getStocksByIds(stockIds));
		}

		return stockModels;
	}

	public Map<String, Integer> getStockMapForDataset(int datasetId) throws MiddlewareQueryException {
		Map<String, Integer> stockMap = new HashMap<String, Integer>();
		Set<StockModel> stocks = this.daoFactory.getStockDao().findInDataSet(datasetId);
		for (StockModel stock : stocks) {
			if (stock != null) {
				stockMap.put(stock.getUniqueName(), stock.getStockId());
			}

		}
		return stockMap;
	}
}
