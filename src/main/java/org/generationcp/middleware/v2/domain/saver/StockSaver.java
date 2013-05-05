package org.generationcp.middleware.v2.domain.saver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.generationcp.commons.util.StringUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.v2.domain.TermId;
import org.generationcp.middleware.v2.domain.Variable;
import org.generationcp.middleware.v2.domain.VariableList;
import org.generationcp.middleware.v2.pojos.ExperimentModel;
import org.generationcp.middleware.v2.pojos.ExperimentStock;
import org.generationcp.middleware.v2.pojos.Stock;
import org.generationcp.middleware.v2.pojos.StockProperty;

public class StockSaver extends Saver {

	public StockSaver(
			HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	//Business Rules dictates that only 1 experimentStock per Experiment row.
	public List<ExperimentStock> createExperimentStocks(ExperimentModel experimentModel, VariableList factors) throws MiddlewareQueryException {
		setWorkingDatabase(Database.LOCAL);
		
		if (factors != null && factors.getVariables() != null && factors.getVariables().size() > 0) {
			Stock stock = null;
			for (Variable variable : factors.getVariables()) {
				stock = createOrUpdateStock(stock, variable);
			}
			
			if (stock != null) {
				getStockDao().save(stock);
				createExperimentStock(experimentModel, stock.getStockId());
			}
		}
		
		return experimentModel.getExperimentStocks();
	}
	
	private void createExperimentStock(ExperimentModel experimentModel, int stockId) throws MiddlewareQueryException {
		ExperimentStock experimentStock = new ExperimentStock();
		experimentStock.setExperimentStockId(getExperimentStockDao().getNegativeId("experimentStockId"));
		experimentStock.setTypeId(TermId.IBDB_STRUCTURE.getId());
		experimentStock.setStockId(stockId);
		experimentStock.setExperimentId(experimentModel.getNdExperimentId());
		
		experimentModel.setExperimentStocks(new ArrayList<ExperimentStock>());
		experimentModel.getExperimentStocks().add(experimentStock);
	}
	
	private Stock createOrUpdateStock(Stock stock, Variable variable) 
	throws MiddlewareQueryException {
		Integer storedInId = variable.getVariableType().getStandardVariable().getStoredIn().getId();
		String value = variable.getValue();
		
		if (TermId.ENTRY_NUMBER_STORAGE.getId().equals(storedInId)) {
			stock = getStockObject(stock);
			stock.setUniqueName(value);
			
		} else if (TermId.ENTRY_GID_STORAGE.getId().equals(storedInId)) {
			stock = getStockObject(stock);
			stock.setDbxrefId(StringUtil.isEmpty(value) ? null : Integer.valueOf(value));
			
		} else if (TermId.ENTRY_DESIGNATION_STORAGE.getId().equals(storedInId)) {
			stock = getStockObject(stock);
			stock.setName(value);
			
		} else if (TermId.ENTRY_CODE_STORAGE.getId().equals(storedInId)) {
			stock = getStockObject(stock);
			stock.setValue(value);
			
		} else if (TermId.GERMPLASM_ENTRY_STORAGE.getId().equals(storedInId)) {
			stock = getStockObject(stock);
			addProperty(stock, createProperty(variable));
			
		}
		
		return stock;
	}
	
	private Stock getStockObject(Stock stock) throws MiddlewareQueryException {
		if (stock == null) {
			stock = new Stock();
			stock.setStockId(getStockDao().getNegativeId("stockId"));
			stock.setIsObsolete(false);
			stock.setUniqueName("");
			stock.setTypeId(TermId.ENTRY_CODE.getId());
		}
		return stock;
	}
	
	private void addProperty(Stock stock, StockProperty property) {
		if (stock.getProperties() == null) {
			stock.setProperties(new HashSet<StockProperty>());
		}
		property.setStock(stock);
		stock.getProperties().add(property);
	}
	
	private StockProperty createProperty(Variable variable) throws MiddlewareQueryException {
		StockProperty property = new StockProperty();
		
		property.setStockPropId(getStockPropertyDao().getNegativeId("stockPropId"));
		property.setTypeId(variable.getVariableType().getId());
		property.setValue(variable.getValue());
		property.setRank(variable.getVariableType().getRank());
		
		return property;
	}
}
