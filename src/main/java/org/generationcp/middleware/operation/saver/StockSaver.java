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
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.dms.StockProperty;

import java.util.HashSet;
import java.util.Map;

public class StockSaver extends Saver {

	private DaoFactory daoFactory;

	public StockSaver(final HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	public Integer saveStock(final int studyId, final VariableList variableList, final Map<Integer, String> preferredNamesByGIDs,
		final Map<Integer, String> pedigreeByGids) {
		final StockModel stockModel = this.createStock(variableList, null);
		if (stockModel != null) {
			final Integer gid = stockModel.getGermplasm().getGid();
			stockModel.setName(preferredNamesByGIDs.get(gid));
			stockModel.setCross(pedigreeByGids.get(gid));
			stockModel.setProject(new DmsProject(studyId));
			this.daoFactory.getStockDao().save(stockModel);
			return stockModel.getStockId();
		}
		return null;
	}

	public void saveOrUpdateStock(final VariableList variableList, final int stockId) {
		final StockModel stockModel = this.daoFactory.getStockDao().getById(stockId);
		this.createStock(variableList, stockModel);
		if (stockModel != null) {
			this.daoFactory.getStockDao().merge(stockModel);
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

				} else if (TermId.ENTRY_TYPE.getId() == variableId) {
					stockModel = this.getStockObject(stockModel);
					final StockProperty stockProperty = this.getStockProperty(stockModel, variable);
					if (stockProperty == null && variable.getValue() != null && !variable.getValue().isEmpty()) {
						this.addProperty(stockModel, this.createProperty(variable));
					}
				} else if (PhenotypicType.GERMPLASM == role || PhenotypicType.ENTRY_DETAIL == role) {
					continue;

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
					final String value;
					final Integer categoricalValueId;
					if (variable.getVariableType().getStandardVariable().getDataType().getName().equals(DataType.CATEGORICAL_VARIABLE.getName())) {
						value = variable.getActualValue();
						categoricalValueId = Integer.valueOf(variable.getValue());
					} else {
						value = variable.getValue();
						categoricalValueId = null;
					}
					property.setValue(value);
					property.setCategoricalValueId(categoricalValueId);
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
		}
		return stockModel;
	}

	private void addProperty(final StockModel stockModel, final StockProperty property) {
		if (stockModel.getProperties() == null) {
			stockModel.setProperties(new HashSet<>());
		}
		property.setStock(stockModel);
		stockModel.getProperties().add(property);
	}

	private StockProperty createProperty(final Variable variable) {
		final String value;
		final Integer categoricalValueId;
		if (variable.getVariableType().getStandardVariable().getDataType().getName().equals(DataType.CATEGORICAL_VARIABLE.getName())) {
			value = variable.getActualValue();
			categoricalValueId = Integer.valueOf(variable.getValue());
		} else {
			value = variable.getValue();
			categoricalValueId = null;
		}
		return new StockProperty(null, variable.getVariableType().getId(), value, categoricalValueId);
	}
}
