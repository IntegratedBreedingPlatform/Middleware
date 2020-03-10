
package org.generationcp.middleware.operation.transformer.etl;

import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.ExperimentValues;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;

import java.util.List;
import java.util.Map;

public class ExperimentValuesTransformer extends Transformer {

	public ExperimentValuesTransformer(final HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public ExperimentValues transform(final MeasurementRow mRow, final VariableTypeList varTypeList, final List<String> trialHeaders, final Map<Integer, Integer> instanceNumberInstanceIdMap) {
		final ExperimentValues experimentValues = new ExperimentValues();
		if (mRow == null) {
			return experimentValues;
		}
		final List<MeasurementData> nonTrialMD = mRow.getNonTrialDataList(trialHeaders);
		if (nonTrialMD != null && varTypeList != null && varTypeList.getVariableTypes() != null) {
			if (nonTrialMD.size() == varTypeList.getVariableTypes().size()) {
				final Integer germplasmId = Integer.parseInt(String.valueOf(mRow.getStockId()));
				final VariableList variableList = new VariableList();

				final List<DMSVariableType> varTypes = varTypeList.getVariableTypes();

				for (int i = 0, l = varTypes.size(); i < l; i++) {
					final DMSVariableType varType = varTypes.get(i);
					String value = null;
					for (final MeasurementData data : nonTrialMD) {
						if (data.getMeasurementVariable().getTermId() == varTypes.get(i).getId()) {
							if (data.getcValueId() != null) {
								value = data.getcValueId();
							} else {
								value = data.getValue();
							}
							final Variable variable = new Variable(varType, value);
							variableList.add(variable);
							data.setVariable(variable);
						}
					}
				}
				final Integer instanceNumber = this.getTrialInstanceNumber(mRow);
				if (instanceNumberInstanceIdMap.get(instanceNumber) != null) {
					experimentValues.setLocationId(instanceNumberInstanceIdMap.get(instanceNumber));
				}
				experimentValues.setVariableList(variableList);
				experimentValues.setGermplasmId(germplasmId);
			} else {
				throw new MiddlewareQueryException("Variables did not match the Measurements Row.");
			}
		}

		return experimentValues;
	}

	private Integer getTrialInstanceNumber(final MeasurementRow row) {
		for (final MeasurementData data : row.getDataList()) {
			if (data.getMeasurementVariable().getTermId() == TermId.TRIAL_INSTANCE_FACTOR.getId()) {
				return Integer.valueOf(data.getValue());
			}
		}
		return null;
	}
}
