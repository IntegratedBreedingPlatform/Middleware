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

package org.generationcp.middleware.operation.saver;

import java.util.HashSet;

import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.dms.StockProperty;

public class StockSaver extends Saver {

	public StockSaver(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public Integer saveStock(VariableList variableList) throws MiddlewareQueryException {
		StockModel stockModel = this.createStock(variableList, null);
		if (stockModel != null) {
			this.getStockDao().save(stockModel);
			return stockModel.getStockId();
		}

		return null;
	}

	public void saveOrUpdateStock(VariableList variableList, int stockId) throws MiddlewareQueryException {
		StockModel stockModel = this.getStockModelBuilder().get(stockId);
		this.createStock(variableList, stockModel);
		if (stockModel != null) {
			this.getStockDao().merge(stockModel);
		}
	}

	private StockModel createStock(VariableList variableList, StockModel stockModel) throws MiddlewareQueryException {
		if (variableList != null && variableList.getVariables() != null && !variableList.getVariables().isEmpty()) {
			int propertyIndex = this.getStockPropertyDao().getNextId("stockPropId");
			for (Variable variable : variableList.getVariables()) {
				int storedInId = variable.getVariableType().getStandardVariable().getStoredIn().getId();
				String value = variable.getValue();

				if (TermId.ENTRY_NUMBER_STORAGE.getId() == storedInId) {
					stockModel = this.getStockObject(stockModel);
					stockModel.setUniqueName(value);

				} else if (TermId.ENTRY_GID_STORAGE.getId() == storedInId) {
					stockModel = this.getStockObject(stockModel);
					Integer dbxref = null;
					if (NumberUtils.isNumber(value)) {
						if (value.indexOf(".") > -1) {
							dbxref = Integer.valueOf(value.substring(0, value.indexOf(".")));
						} else {
							dbxref = Integer.valueOf(value);
						}
					}
					stockModel.setDbxrefId(dbxref);

				} else if (TermId.ENTRY_DESIGNATION_STORAGE.getId() == storedInId) {
					stockModel = this.getStockObject(stockModel);
					stockModel.setName(value);

				} else if (TermId.ENTRY_CODE_STORAGE.getId() == storedInId) {
					stockModel = this.getStockObject(stockModel);
					stockModel.setValue(value);

				} else if (TermId.GERMPLASM_ENTRY_STORAGE.getId() == storedInId) {
					stockModel = this.getStockObject(stockModel);
					StockProperty stockProperty = this.getStockProperty(stockModel, variable);
					if (stockProperty == null && variable.getValue() != null && !variable.getValue().isEmpty()) {
						this.addProperty(stockModel, this.createProperty(propertyIndex++, variable));
					}
				} else {
					throw new MiddlewareQueryException("Non-Stock Variable was used in calling create stock: "
							+ variable.getVariableType().getId());
				}
			}
		}

		return stockModel;
	}

	private StockProperty getStockProperty(StockModel stockModel, Variable variable) {
		if (stockModel != null && stockModel.getProperties() != null && !stockModel.getProperties().isEmpty()) {
			for (StockProperty property : stockModel.getProperties()) {
				if (property.getTypeId().equals(Integer.valueOf(variable.getVariableType().getId()))) {
					property.setValue(variable.getValue());
					return property;
				}
			}
		}
		return null;
	}

	private StockModel getStockObject(StockModel stockModel) throws MiddlewareQueryException {
		if (stockModel == null) {
			stockModel = new StockModel();
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
