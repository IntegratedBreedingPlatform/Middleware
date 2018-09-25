/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.operation.builder;

import java.util.Set;

import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.Stock;
import org.generationcp.middleware.domain.dms.Stocks;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.dms.StockProperty;

public class StockBuilder extends Builder {

	public StockBuilder(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public Stocks getStocksInDataset(int datasetId) throws MiddlewareException {
		DataSet dataSet = this.getDataSetBuilder().build(datasetId);
		Study study = this.getStudyBuilder().createStudy(dataSet.getStudyId());

		VariableTypeList stockVariableTypes = this.getStockVariableTypes(study, dataSet);
		Set<StockModel> stockModels = this.getStockModels(datasetId);

		return this.buildStocks(stockModels, stockVariableTypes);
	}

	private VariableTypeList getStockVariableTypes(Study study, DataSet dataSet) {
		VariableTypeList stockVariableTypes = new VariableTypeList();
		stockVariableTypes.addAll(study.getVariableTypesByPhenotypicType(PhenotypicType.GERMPLASM));
		stockVariableTypes.addAll(dataSet.getFactorsByPhenotypicType(PhenotypicType.GERMPLASM));
		return stockVariableTypes;
	}

	private Set<StockModel> getStockModels(int datasetId) throws MiddlewareQueryException {
		return this.getStockDao().findInDataSet(datasetId);
	}

	private Stocks buildStocks(Set<StockModel> stockModels, VariableTypeList stockVariableTypes) {
		Stocks stocks = new Stocks();
		for (StockModel stockModel : stockModels) {
			VariableList variables = new VariableList();
			for (DMSVariableType variableType : stockVariableTypes.getVariableTypes()) {
				Variable variable = new Variable(variableType, this.getValue(stockModel, variableType));
				variables.add(variable);
			}
			stocks.add(new Stock(stockModel.getStockId(), variables));
		}
		return stocks;
	}

	String getValue(StockModel stockModel, DMSVariableType variableType) {
		String value = null;
		int id = variableType.getStandardVariable().getId();
		if (id == TermId.ENTRY_NO.getId()) {
			value = stockModel.getUniqueName();
		} else if (id == TermId.GID.getId()) {
			value = stockModel.getGermplasm() == null ? null : Integer.toString(stockModel.getGermplasm().getGid());
		} else if (id == TermId.DESIG.getId()) {
			value = stockModel.getName();
		} else if (id == TermId.ENTRY_CODE.getId()) {
			value = stockModel.getValue();
		} else {
			value = this.getPropertyValue(variableType.getId(), stockModel.getProperties());
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

	public long countStocks(int datasetId) throws MiddlewareQueryException {
		return this.getExperimentDao().countStocksByDatasetId(datasetId);
	}
}
