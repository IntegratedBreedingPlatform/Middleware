package org.generationcp.middleware.v2.domain.saver;

import java.util.HashSet;

import org.generationcp.commons.util.StringUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.v2.domain.TermId;
import org.generationcp.middleware.v2.domain.Variable;
import org.generationcp.middleware.v2.pojos.Stock;
import org.generationcp.middleware.v2.pojos.StockProperty;

public class StockSaver extends Saver {

	public StockSaver(
			HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
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
