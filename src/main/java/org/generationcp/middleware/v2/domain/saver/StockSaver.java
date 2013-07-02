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
package org.generationcp.middleware.v2.domain.saver;

import java.util.HashSet;

import org.generationcp.commons.util.StringUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.v2.domain.TermId;
import org.generationcp.middleware.v2.domain.Variable;
import org.generationcp.middleware.v2.domain.VariableList;
import org.generationcp.middleware.v2.pojos.StockModel;
import org.generationcp.middleware.v2.pojos.StockProperty;

public class StockSaver extends Saver {

	public StockSaver(
			HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public Integer saveStock(VariableList variableList) throws MiddlewareQueryException {
		setWorkingDatabase(Database.LOCAL);
		
		StockModel stockModel = createStock(variableList);
		if (stockModel != null) {
			getStockDao().save(stockModel);
			return stockModel.getStockId();
		}
		
		return null;
	}
	
	private StockModel createStock(VariableList variableList) throws MiddlewareQueryException {
		StockModel stockModel = null;
		if (variableList != null && variableList.getVariables() != null && variableList.getVariables().size() > 0) {
			int propertyIndex = getStockPropertyDao().getNegativeId("stockPropId");
			for (Variable variable : variableList.getVariables()) {
				int storedInId = variable.getVariableType().getStandardVariable().getStoredIn().getId();
				String value = variable.getValue();
				
				if (TermId.ENTRY_NUMBER_STORAGE.getId() == storedInId) {
					stockModel = getStockObject(stockModel);
					stockModel.setUniqueName(value);
					
				} else if (TermId.ENTRY_GID_STORAGE.getId() == storedInId) {
					stockModel = getStockObject(stockModel);
					stockModel.setDbxrefId(StringUtil.isEmpty(value) ? null : Integer.valueOf(value));
					
				} else if (TermId.ENTRY_DESIGNATION_STORAGE.getId() == storedInId) {
					stockModel = getStockObject(stockModel);
					stockModel.setName(value);
					
				} else if (TermId.ENTRY_CODE_STORAGE.getId() == storedInId) {
					stockModel = getStockObject(stockModel);
					stockModel.setValue(value);
					
				} else if (TermId.GERMPLASM_ENTRY_STORAGE.getId() == storedInId) {
					stockModel = getStockObject(stockModel);
					addProperty(stockModel, createProperty(propertyIndex--, variable));
					
				} else {
					throw new MiddlewareQueryException("Non-Stock Variable was used in calling create stock: " + variable.getVariableType().getId());
				}
			}
		}
		
		return stockModel;
	}
	
	private StockModel getStockObject(StockModel stockModel) throws MiddlewareQueryException {
		if (stockModel == null) {
			stockModel = new StockModel();
			stockModel.setStockId(getStockDao().getNegativeId("stockId"));
			stockModel.setIsObsolete(false);
			stockModel.setTypeId(TermId.ENTRY_CODE.getId());
		}
		return stockModel;
	}
	
	private void addProperty(StockModel stockModel, StockProperty property) {
		if (stockModel.getProperties() == null) {
			stockModel.setProperties(new HashSet<StockProperty>());
		}
		property.setStock(stockModel);
		stockModel.getProperties().add(property);
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
