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

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.dms.StockModel;

import java.util.*;

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

	public Map<Integer, StockModel> get(List<Integer> stockIds) throws MiddlewareQueryException {
		 Map<Integer, StockModel> stockModels = new HashMap<Integer, StockModel>();
		 
		 List<Integer> positiveIds = new ArrayList<Integer>();
		 List<Integer> negativeIds = new ArrayList<Integer>();
		 
		 for (Integer stockId : stockIds){
			 if (stockId >= 0) {
				 positiveIds.add(stockId);
			 } else {
				 negativeIds.add(stockId);
			 }
		 }
		 
		 if (!positiveIds.isEmpty()){
			 setWorkingDatabase(Database.CENTRAL);
			 stockModels.putAll(getStockDao().getStocksByIds(positiveIds));
		 }
		 
		 if (!negativeIds.isEmpty()){
			 setWorkingDatabase(-1);
			 stockModels.putAll(getStockDao().getStocksByIds(negativeIds));
		 }

		return stockModels;
	}
	
	public Map<String, Integer> getStockMapForDataset(int datasetId) throws MiddlewareQueryException {
		Map<String, Integer> stockMap = new HashMap<String, Integer>();
		
		if (setWorkingDatabase(datasetId)) {
			Set<StockModel> stocks = getStockDao().findInDataSet(datasetId);
			for (StockModel stock : stocks) {
                if (stock != null) {
                    stockMap.put(stock.getUniqueName(), stock.getStockId());
                }

			}
		}
		
		return stockMap;
	}
}
