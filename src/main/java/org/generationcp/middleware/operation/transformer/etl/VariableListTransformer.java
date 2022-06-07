
package org.generationcp.middleware.operation.transformer.etl;

import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;

import java.util.ArrayList;
import java.util.List;

public class VariableListTransformer extends Transformer {

	public VariableListTransformer() {
	}

	public VariableList transformStock(final MeasurementRow mRow, final VariableTypeList variableTypeList,
			final List<String> trialHeaders) throws MiddlewareQueryException {
		final VariableList variableList = new VariableList();

		if (mRow == null) {
			return variableList;
		}
		final List<MeasurementData> nonTrialMD = mRow.getNonTrialDataList(trialHeaders);
		if (mRow != null && nonTrialMD != null && variableTypeList != null
				&& variableTypeList.getVariableTypes() != null) {
			final int nonTrialMDSize = nonTrialMD.size();
			final int variableTypeSize = variableTypeList.getVariableTypes().size();
			if (nonTrialMDSize == variableTypeSize) {
				for (final DMSVariableType variableType : variableTypeList.getVariableTypes()) {
					if (variableType.getStandardVariable().getPhenotypicType() == PhenotypicType.GERMPLASM) {
						String value = null;
						for (final MeasurementData data : nonTrialMD) {
							if (data.getMeasurementVariable().getTermId() == variableType.getStandardVariable()
									.getId()) {
								value = data.getValue();
							}
						}
						variableList.add(new Variable(variableType, value));
					}
				}

			} else {// else invalid data
				throw new MiddlewareQueryException("Variables did not match the Measurements Row.");
			}
		}

		return variableList;
	}

	public VariableList transformStockOptimize(final MeasurementRow mRow,
		final VariableTypeList variableTypeList, final List<String> trialHeaders) throws MiddlewareQueryException {
		final VariableList variableList = new VariableList();

		if (mRow == null) {
			return variableList;
		}

		final List<MeasurementData> nonTrialMD = mRow.getNonTrialDataList(trialHeaders);
		if (mRow != null && nonTrialMD != null && variableTypeList != null
				&& variableTypeList.getVariableTypes() != null) {
			final int nonTrialMDSize = nonTrialMD.size();
			final int variableTypeSize = variableTypeList.getVariableTypes().size();
			if (nonTrialMDSize == variableTypeSize) {
				for (final DMSVariableType variableType : variableTypeList.getVariableTypes()) {
					if (variableType.getStandardVariable().getPhenotypicType() == PhenotypicType.GERMPLASM ||
						variableType.getId() == TermId.ENTRY_NO.getId() ||
						variableType.getId() == TermId.ENTRY_TYPE.getId()) {
						String value = null;
						for (final MeasurementData data : nonTrialMD) {
							if (data.getMeasurementVariable().getTermId() == variableType.getStandardVariable()
									.getId()) {
								value = data.getcValueId() != null ? data.getcValueId() : data.getValue();
								break;
							}
						}
						variableList.add(new Variable(variableType, value));
					}
				}

			} else {// else invalid data
				throw new MiddlewareQueryException("Variables did not match the Measurements Row.");
			}
		}

		return variableList;
	}

	public List<Integer> transformStockIndexes(final MeasurementRow mRow, final VariableTypeList variableTypeList,
			final List<String> trialHeaders) throws MiddlewareQueryException {
		final List<Integer> variableIndexesList = new ArrayList<>();

		if (mRow == null) {
			return variableIndexesList;
		}
		final List<MeasurementData> nonTrialMD = mRow.getNonTrialDataList(trialHeaders);
		if (mRow != null && nonTrialMD != null && variableTypeList != null
				&& variableTypeList.getVariableTypes() != null) {
			final int nonTrialMDSize = nonTrialMD.size();
			final int variableTypeSize = variableTypeList.getVariableTypes().size();

			if (nonTrialMDSize == variableTypeSize) {
				int i = 0;
				for (final DMSVariableType variableType : variableTypeList.getVariableTypes()) {
					if (variableType.getStandardVariable().getPhenotypicType() == PhenotypicType.GERMPLASM) {
						variableIndexesList.add(i);
					}
					i++;
				}

			} else {// else invalid data
				throw new MiddlewareQueryException("Variables did not match the Measurements Row.");
			}
		}

		return variableIndexesList;
	}

	public VariableList transformTrialEnvironment(final MeasurementRow mRow, final VariableTypeList variableTypeList,
			final List<String> trialHeaders) throws MiddlewareQueryException {
		final VariableList variableList = new VariableList();

		if (mRow == null) {
			return variableList;
		}
		final List<MeasurementData> trialMD = mRow.getTrialDataList(trialHeaders);
		if (trialMD != null && variableTypeList != null && variableTypeList.getVariableTypes() != null) {
			final List<DMSVariableType> varTypes = variableTypeList.getVariableTypes();
			final int varTypeSize = varTypes.size();
			for (int i = 0, l = varTypeSize; i < l; i++) {
				final DMSVariableType varType = varTypes.get(i);

				if (varType.getStandardVariable().getPhenotypicType() == PhenotypicType.TRIAL_ENVIRONMENT
						|| varType.getStandardVariable().getPhenotypicType() == PhenotypicType.VARIATE) {// include
																											// variate
					String value = null;
					for (final MeasurementData data : trialMD) {
						if (data.getMeasurementVariable().getTermId() == varTypes.get(i).getId()) {
							value = data.getValue();
						}
					}
					final Variable variable = new Variable(varType, value);
					variableList.add(variable);
				}
			}
		}

		return variableList;
	}

	public VariableList transformTrialEnvironment(final MeasurementRow mRow, final VariableTypeList variableTypeList)
			throws MiddlewareQueryException {
		final VariableList variableList = new VariableList();

		final List<MeasurementData> trialMD = mRow.getDataList();
		if (trialMD != null && variableTypeList != null && variableTypeList.getVariableTypes() != null) {
			final List<DMSVariableType> varTypes = variableTypeList.getVariableTypes();
			final int varTypeSize = varTypes.size();
			for (int i = 0, l = varTypeSize; i < l; i++) {
				final DMSVariableType varType = varTypes.get(i);
				MeasurementData trialData = null;
				for (final MeasurementData aData : trialMD) {
					if (aData.getMeasurementVariable().getTermId() == varType.getId()) {
						trialData = aData;
						break;
					}
				}

				if (trialData != null) {
					String value = trialData.getValue();
					final Integer phenotypeId = trialData.getMeasurementDataId();

					if (trialData.getcValueId() != null) {
						value = trialData.getcValueId();
					}

					if (varType.getStandardVariable().getPhenotypicType() == PhenotypicType.TRIAL_ENVIRONMENT
							|| varType.getStandardVariable().getPhenotypicType() == PhenotypicType.VARIATE) {// include
																												// variate
						final Variable variable = new Variable(varType, value);
						variable.setVariableDataId(phenotypeId);
						variableList.add(variable);
					}
				}
			}
		}

		return variableList;
	}

	public VariableList transformTrialEnvironment(final List<MeasurementVariable> measurementVariableList,
			final VariableTypeList variableTypeList) throws MiddlewareQueryException {
		final VariableList variableList = new VariableList();

		if (measurementVariableList == null && variableTypeList == null) {
			return variableList;
		}

		if (measurementVariableList == null || variableTypeList == null
				|| variableTypeList.getVariableTypes() == null) {
			throw new MiddlewareQueryException("Variables did not match the Measurement Variable List.");
		}

		final List<DMSVariableType> varTypes = variableTypeList.getVariableTypes();

		for (final MeasurementVariable measurementVariable : measurementVariableList) {
			final String value = measurementVariable.getValue();
			for (final DMSVariableType varType : varTypes) {
				if (measurementVariable.getTermId() == varType.getId()) {
					variableList.add(new Variable(varType, value));
				}
			}
		}

		return variableList;
	}

}
