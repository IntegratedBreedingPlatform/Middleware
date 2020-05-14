package org.generationcp.middleware.service.api;

import org.generationcp.middleware.pojos.dms.StockModel;

import java.util.List;
import java.util.Map;

public interface StockModelService {

	List<StockModel> getStocksForStudy(int studyId);

	long countStocksByStudyAndEntryTypeIds(int studyId, List<String> systemDefinedEntryTypeIds);

	StockModel getStockById(int stockId);

	Map<String, Integer> getStockMapForStudy(int datasetId);

	Map<Integer, String> getInventoryStockIdMap(List<StockModel> stockModelList);
}
