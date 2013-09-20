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
package org.generationcp.middleware.operation.builder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.StockModel;

public class StockModelBuilder extends Builder {

	public StockModelBuilder(HibernateSessionProvider sessionProviderForLocal,
			               HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public StockModel get(int stockId) throws MiddlewareQueryException {
		StockModel stockModel = null;
		if (setWorkingDatabase(stockId)) {
			stockModel = getStockDao().getById(stockId);
		}
		return stockModel;
	}
	
	public Map<String, Integer> getStockMapForDataset(int datasetId) throws MiddlewareQueryException {
		Map<String, Integer> stockMap = new HashMap<String, Integer>();
		
		if (setWorkingDatabase(datasetId)) {
			Set<StockModel> stocks = getStockDao().findInDataSet(datasetId);
			for (StockModel stock : stocks) {
				stockMap.put(stock.getUniqueName(), stock.getStockId());
			}
		}
		
		return stockMap;
	}
}
