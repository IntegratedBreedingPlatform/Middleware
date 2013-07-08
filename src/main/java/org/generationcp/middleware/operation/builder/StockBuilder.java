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

import java.util.Set;

import org.generationcp.middleware.domain.DataSet;
import org.generationcp.middleware.domain.FactorType;
import org.generationcp.middleware.domain.Stock;
import org.generationcp.middleware.domain.Stocks;
import org.generationcp.middleware.domain.Study;
import org.generationcp.middleware.domain.TermId;
import org.generationcp.middleware.domain.Variable;
import org.generationcp.middleware.domain.VariableList;
import org.generationcp.middleware.domain.VariableType;
import org.generationcp.middleware.domain.VariableTypeList;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.dms.StockProperty;

public class StockBuilder extends Builder {

	public StockBuilder(
			HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public Stocks getStocksInDataset(int datasetId) throws MiddlewareQueryException {
		if (this.setWorkingDatabase(datasetId)) {
		    DataSet dataSet = getDataSetBuilder().build(datasetId);
		    Study study = getStudyBuilder().createStudy(dataSet.getStudyId());
		
		    VariableTypeList stockVariableTypes = getStockVariableTypes(study, dataSet);
		    Set<StockModel> stockModels = getStockModels(datasetId);
		
		    return buildStocks(stockModels, stockVariableTypes);
		}
		return new Stocks();
	}

	private VariableTypeList getStockVariableTypes(Study study, DataSet dataSet) {
		VariableTypeList stockVariableTypes = new VariableTypeList();
		stockVariableTypes.addAll(study.getVariableTypesByFactorType(FactorType.GERMPLASM));
		stockVariableTypes.addAll(dataSet.getFactorsByFactorType(FactorType.GERMPLASM));
		return stockVariableTypes;
	}

	private Set<StockModel> getStockModels(int datasetId) throws MiddlewareQueryException {
		return getStockDao().findInDataSet(datasetId);
	}

	private Stocks buildStocks(Set<StockModel> stockModels, VariableTypeList stockVariableTypes) {
		Stocks stocks = new Stocks();
		for (StockModel stockModel : stockModels) {
			VariableList variables = new VariableList();
			for (VariableType variableType : stockVariableTypes.getVariableTypes()) {
				Variable variable = new Variable(variableType, getValue(stockModel, variableType));
				variables.add(variable);
			}
			stocks.add(new Stock(stockModel.getStockId(), variables));
		}
		return stocks;
	}

	private String getValue(StockModel stockModel, VariableType variableType) {
		String value = null;
		int storedInId = variableType.getStandardVariable().getStoredIn().getId();
		if (storedInId == TermId.ENTRY_NUMBER_STORAGE.getId()) {
			value = stockModel.getUniqueName();
		}
		else if (storedInId == TermId.ENTRY_GID_STORAGE.getId()) {
			value = stockModel.getDbxrefId() == null ? null : Integer.toString(stockModel.getDbxrefId());
		}
	    else if (storedInId == TermId.ENTRY_DESIGNATION_STORAGE.getId()) {
	    	value = stockModel.getName();
	    }
		else if (storedInId == TermId.ENTRY_CODE_STORAGE.getId()) {
			value = stockModel.getValue();
		}
		else if (storedInId == TermId.GERMPLASM_ENTRY_STORAGE.getId()) {
			value = getPropertyValue(variableType.getId(), stockModel.getProperties());
		}
		return value;
	}

	private String getPropertyValue(int id, Set<StockProperty> properties) {
		String value = null;
		if (properties != null) {
		    for (StockProperty property : properties) {
		    	if (property.getTypeId() == id) {
		    		value = property.getValue();
		    		break;
		    	}
		    }
		}
		return value;
	}
}
