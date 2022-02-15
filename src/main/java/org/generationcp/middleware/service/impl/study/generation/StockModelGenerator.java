package org.generationcp.middleware.service.impl.study.generation;

import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.dms.StockProperty;
import org.generationcp.middleware.service.api.dataset.ObservationUnitData;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class StockModelGenerator {

	protected StockModel generate(final Map<Integer, MeasurementVariable> variablesMap, final List<ObservationUnitData> dataList) {

		final StockModel stockModel = new StockModel();
		stockModel.setIsObsolete(false);

		for (final ObservationUnitData data : dataList) {
			final Integer variableId = data.getVariableId();
			final String value = data.getValue();
			final MeasurementVariable variable = variablesMap.get(variableId);

			if (TermId.ENTRY_NO.getId() == variableId) {
				stockModel.setUniqueName(data.getValue());

			} else if (TermId.GID.getId() == variableId) {
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
				stockModel.setName(value);

			} else if (TermId.ENTRY_CODE.getId() == variableId) {
				// TODO: add entry code as property

			} else if (variable != null && VariableType.GERMPLASM_DESCRIPTOR.equals(variable.getVariableType()) && TermId.OBS_UNIT_ID.getId() != variableId) {
				this.addProperty(stockModel, new StockProperty(variableId, value));
			}
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

}
