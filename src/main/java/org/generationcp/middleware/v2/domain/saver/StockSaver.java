package org.generationcp.middleware.v2.domain.saver;

import java.util.HashSet;

import org.generationcp.commons.util.StringUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.v2.domain.TermId;
import org.generationcp.middleware.v2.domain.Variable;
import org.generationcp.middleware.v2.domain.VariableList;
import org.generationcp.middleware.v2.pojos.Stock;
import org.generationcp.middleware.v2.pojos.StockProperty;

public class StockSaver extends Saver {

	public StockSaver(
			HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public Integer saveStock(VariableList variableList) throws MiddlewareQueryException {
		setWorkingDatabase(Database.LOCAL);
		
		Stock stock = createStock(variableList);
		if (stock != null) {
			getStockDao().save(stock);
			return stock.getStockId();
		}
		
		return null;
	}
	
	private Stock createStock(VariableList variableList) throws MiddlewareQueryException {
		Stock stock = null;
		if (variableList != null && variableList.getVariables() != null && variableList.getVariables().size() > 0) {
			int propertyIndex = getStockPropertyDao().getNegativeId("stockPropId");
			for (Variable variable : variableList.getVariables()) {
				int storedInId = variable.getVariableType().getStandardVariable().getStoredIn().getId();
				String value = variable.getValue();
				
				if (TermId.ENTRY_NUMBER_STORAGE.getId() == storedInId) {
					stock = getStockObject(stock);
					stock.setUniqueName(value);
					
				} else if (TermId.ENTRY_GID_STORAGE.getId() == storedInId) {
					stock = getStockObject(stock);
					stock.setDbxrefId(StringUtil.isEmpty(value) ? null : Integer.valueOf(value));
					
				} else if (TermId.ENTRY_DESIGNATION_STORAGE.getId() == storedInId) {
					stock = getStockObject(stock);
					stock.setName(value);
					
				} else if (TermId.ENTRY_CODE_STORAGE.getId() == storedInId) {
					stock = getStockObject(stock);
					stock.setValue(value);
					
				} else if (TermId.GERMPLASM_ENTRY_STORAGE.getId() == storedInId) {
					stock = getStockObject(stock);
					addProperty(stock, createProperty(propertyIndex--, variable));
					
				} else {
					throw new MiddlewareQueryException("Non-Stock Variable was used in calling create stock: " + variable.getVariableType().getId());
				}
			}
		}
		
		return stock;
	}
	
	private Stock getStockObject(Stock stock) throws MiddlewareQueryException {
		if (stock == null) {
			stock = new Stock();
			stock.setStockId(getStockDao().getNegativeId("stockId"));
			stock.setIsObsolete(false);
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
	
	private StockProperty createProperty(int index, Variable variable) throws MiddlewareQueryException {
		StockProperty property = new StockProperty();
		
		property.setStockPropId(index);
		property.setTypeId(variable.getVariableType().getId());
		property.setValue(variable.getValue());
		property.setRank(variable.getVariableType().getRank());
		
		return property;
	}
}
