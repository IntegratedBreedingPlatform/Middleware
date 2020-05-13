package org.generationcp.middleware.service.impl;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.service.api.StockModelService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockModelServiceImpl implements StockModelService {

	private final DaoFactory daoFactory;

	public StockModelServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<StockModel> getStocksForStudy(final int studyId) {
		return this.daoFactory.getStockDao().getStocksForStudy(studyId);
	}

	@Override
	public long countStocksByStudyAndEntryTypeIds(final int studyId, final List<Integer> systemDefinedEntryTypeIds) {
		return this.daoFactory.getStockDao().countStocksByStudyAndEntryTypeIds(studyId, systemDefinedEntryTypeIds);
	}

	@Override
	public StockModel getStockById(final int stockId) {
		return this.daoFactory.getStockDao().getById(stockId);
	}

	@Override
	public Map<Integer, StockModel> getStockyId(final List<Integer> stockIds) {
		final Map<Integer, StockModel> stockModels = new HashMap<>();

		if (stockIds != null && !stockIds.isEmpty()) {
			stockModels.putAll(this.daoFactory.getStockDao().getStocksByIds(stockIds));
		}

		return stockModels;
	}

	@Override
	public Map<String, Integer> getStockMapForStudy(final int datasetId) {
		final Map<String, Integer> stockMap = new HashMap<>();
		final List<StockModel> stocks = this.daoFactory.getStockDao().getStocksForStudy(datasetId);
		for (final StockModel stock : stocks) {
			if (stock != null) {
				stockMap.put(stock.getUniqueName(), stock.getStockId());
			}

		}
		return stockMap;
	}

}
