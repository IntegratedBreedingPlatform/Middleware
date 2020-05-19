package org.generationcp.middleware.service.impl;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.service.api.StockModelService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockModelServiceImpl implements StockModelService {

	private final DaoFactory daoFactory;

	@Resource
	private InventoryDataManager inventoryDataManager;

	public StockModelServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<StockModel> getStocksForStudy(final int studyId) {
		return this.daoFactory.getStockDao().getStocksForStudy(studyId);
	}

	@Override
	public long countStocksForStudy(final int studyId) {
		return this.daoFactory.getStockDao().countStocksForStudy(studyId);
	}

	@Override
	public void deleteStocksForStudy(final int studyId) {
		this.daoFactory.getStockDao().deleteStocksForStudy(studyId);
	}

	@Override
	public void saveStocks(final List<StockModel> stockModelList) {
		for (final StockModel stockModel : stockModelList) {
			this.daoFactory.getStockDao().saveOrUpdate(stockModel);
		}
	}

	@Override
	public long countStocksByStudyAndEntryTypeIds(final int studyId, final List<String> systemDefinedEntryTypeIds) {
		return this.daoFactory.getStockDao().countStocksByStudyAndEntryTypeIds(studyId, systemDefinedEntryTypeIds);
	}

	@Override
	public StockModel getStockById(final int stockId) {
		return this.daoFactory.getStockDao().getById(stockId);
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

	@Override
	public Map<Integer, String> getInventoryStockIdMap(final List<StockModel> stockModelList) {

		final List<Integer> gids = new ArrayList<>();
		if (stockModelList != null && !stockModelList.isEmpty()) {
			for (final StockModel stockModel : stockModelList) {
				gids.add(stockModel.getGermplasm().getGid());
			}
		}

		return this.inventoryDataManager.retrieveStockIds(gids);

	}

}
