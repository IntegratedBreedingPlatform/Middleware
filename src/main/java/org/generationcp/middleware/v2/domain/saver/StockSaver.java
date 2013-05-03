package org.generationcp.middleware.v2.domain.saver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.generationcp.commons.util.StringUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.v2.domain.CVTermId;
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

	public List<ExperimentStock> createExperimentStocks(ExperimentModel experimentModel, VariableList factors) throws MiddlewareQueryException {
		List<ExperimentStock> experimentStocks = null;
		
		if (factors != null && factors.getVariables() != null && factors.getVariables().size() > 0) {
			for (Variable variable : factors.getVariables()) {
				
				Integer stockId = saveStock(variable);
				if (stockId != null) {
					ExperimentStock experimentStock = new ExperimentStock();
					experimentStock.setExperimentStockId(getExperimentStockDao().getNegativeId("experimentStockId"));
					experimentStock.setTypeId(variable.getVariableType().getId());
					experimentStock.setStockId(stockId);
					getExperimentStockCollection(experimentStocks).add(experimentStock);
				}
			}
		}
		
		return experimentStocks;
	}
	
	private List<ExperimentStock> getExperimentStockCollection(List<ExperimentStock> experimentStocks) throws MiddlewareQueryException {
		if (experimentStocks == null) {
			experimentStocks = new ArrayList<ExperimentStock>();
		}
		return experimentStocks;
	}
	
	private Integer saveStock(Variable variable) throws MiddlewareQueryException {
		Stock stock = createStock(variable);
		if (stock != null) {
			getStockDao().save(stock);
			return stock.getStockId(); 
		}
		return null;
	}
	
	private Stock createStock(Variable variable) throws MiddlewareQueryException {
		Stock stock = null;
		
		Integer variableId = variable.getVariableType().getId();
		String value = variable.getValue();
		
		if (CVTermId.ENTRY_NUMBER_STORAGE.getId().equals(variableId)) {
			stock = getStockObject(stock);
			stock.setUniqueName(value);
			
		} else if (CVTermId.ENTRY_GID_STORAGE.getId().equals(variableId)) {
			stock = getStockObject(stock);
			stock.setDbxrefId(StringUtil.isEmpty(value) ? null : Integer.valueOf(value));
			
		} else if (CVTermId.ENTRY_DESIGNATION_STORAGE.getId().equals(variableId)) {
			stock = getStockObject(stock);
			stock.setName(value);
			
		} else if (CVTermId.ENTRY_CODE_STORAGE.getId().equals(variableId)) {
			stock = getStockObject(stock);
			stock.setValue(value);
			
		} else if (CVTermId.GERMPLASM_ENTRY_STORAGE.getId().equals(variableId)) {
			stock = getStockObject(stock);
			addProperty(stock, createProperty(variable));
			
		}
		
		return stock;
	}
	
	private Stock getStockObject(Stock stock) throws MiddlewareQueryException {
		if (stock == null) {
			stock = new Stock();
			stock.setStockId(getStockDao().getNegativeId("stockId"));
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
