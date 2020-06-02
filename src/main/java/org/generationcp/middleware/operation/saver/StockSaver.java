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

import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.dms.StockProperty;
import org.generationcp.middleware.service.api.study.StudyGermplasmService;
import org.generationcp.middleware.service.impl.study.StudyGermplasmServiceImpl;

import java.util.HashSet;

public class StockSaver extends Saver {

	private StudyGermplasmService studyGermplasmService;

	public StockSaver(final HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
		this.studyGermplasmService = new StudyGermplasmServiceImpl(sessionProviderForLocal);
	}

	public Integer saveStock(final int studyId, final VariableList variableList) {
		final StockModel stockModel = this.createStock(variableList, null);
		if (stockModel != null) {
			stockModel.setProjectId(studyId);
			this.getStockDao().save(stockModel);
			return stockModel.getStockId();
		}
		return null;
	}

	public void saveOrUpdateStock(final VariableList variableList, final int stockId) {
		final StockModel stockModel = this.getStockDao().getById(stockId);
		this.createStock(variableList, stockModel);
		if (stockModel != null) {
			this.getStockDao().merge(stockModel);
		}
	}

	protected StockModel createStock(final VariableList variableList, StockModel stockModel) {
		if (variableList != null && variableList.getVariables() != null && !variableList.getVariables().isEmpty()) {
			for (final Variable variable : variableList.getVariables()) {
				final int variableId = variable.getVariableType().getStandardVariable().getId();
				final String value = variable.getValue();
				final PhenotypicType role = variable.getVariableType().getRole();

				if (TermId.ENTRY_NO.getId() == variableId) {
					stockModel = this.getStockObject(stockModel);
					stockModel.setUniqueName(value);

				} else if (TermId.GID.getId() == variableId) {
					stockModel = this.getStockObject(stockModel);
					Integer dbxref = null;
					if (NumberUtils.isNumber(value)) {
						if (value.indexOf(".") > -1) {
							dbxref = Integer.valueOf(value.substring(0, value.indexOf(".")));
						} else {
							dbxref = Integer.valueOf(value);
						}
					}
					stockModel.setGermplasm(new Germplasm(dbxref));

				} else if (TermId.DESIG.getId() == variableId) {
					stockModel = this.getStockObject(stockModel);
					stockModel.setName(value);

				} else if (TermId.ENTRY_CODE.getId() == variableId) {
					stockModel = this.getStockObject(stockModel);
					stockModel.setValue(value);

				} else if (TermId.OBS_UNIT_ID.getId() == variableId) {
					continue;

				} else if (PhenotypicType.GERMPLASM == role) {
					stockModel = this.getStockObject(stockModel);
					final StockProperty stockProperty = this.getStockProperty(stockModel, variable);
					if (stockProperty == null && variable.getValue() != null && !variable.getValue().isEmpty()) {
						this.addProperty(stockModel, this.createProperty(variable));
					}
				} else {
					throw new MiddlewareQueryException("Non-Stock Variable was used in calling create stock: "
							+ variable.getVariableType().getId());
				}
			}
		}

		return stockModel;
	}

	private StockProperty getStockProperty(final StockModel stockModel, final Variable variable) {
		if (stockModel != null && stockModel.getProperties() != null && !stockModel.getProperties().isEmpty()) {
			for (final StockProperty property : stockModel.getProperties()) {
				if (property.getTypeId().equals(Integer.valueOf(variable.getVariableType().getId()))) {
					property.setValue(variable.getValue());
					return property;
				}
			}
		}
		return null;
	}

	private StockModel getStockObject(StockModel stockModel) {
		if (stockModel == null) {
			stockModel = new StockModel();
			stockModel.setIsObsolete(false);
			stockModel.setTypeId(TermId.ENTRY_CODE.getId());
		}
		return stockModel;
	}

	private void addProperty(final StockModel stockModel, final StockProperty property) {
		if (stockModel.getProperties() == null) {
			stockModel.setProperties(new HashSet<StockProperty>());
		}
		property.setStock(stockModel);
		stockModel.getProperties().add(property);
	}

	private StockProperty createProperty(final Variable variable) {
		final StockProperty property = new StockProperty();
		property.setTypeId(variable.getVariableType().getId());
		property.setValue(variable.getValue());
		property.setRank(variable.getVariableType().getRank());

		return property;
	}
}
