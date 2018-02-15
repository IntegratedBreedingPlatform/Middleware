
package org.generationcp.middleware.operation.transformer.etl;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;

public class VariableListTransformer extends Transformer {

	public VariableListTransformer(final HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
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

	public VariableList transformStockOptimize(final List<Integer> variableIndexesList, final MeasurementRow mRow,
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
					if (variableType.getStandardVariable().getPhenotypicType() == PhenotypicType.GERMPLASM) {
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
					final Integer phenotypeId = trialData.getPhenotypeId();

					if (trialData.getcValueId() != null) {
						value = trialData.getcValueId();
					}

					if (varType.getStandardVariable().getPhenotypicType() == PhenotypicType.TRIAL_ENVIRONMENT
							|| varType.getStandardVariable().getPhenotypicType() == PhenotypicType.VARIATE) {// include
																												// variate
						final Variable variable = new Variable(varType, value);
						variable.setPhenotypeId(phenotypeId);
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

	public VariableList transformStudyDetails(final StudyDetails studyDetails, final VariableTypeList variableTypeList)
			throws MiddlewareException {

		final VariableList variables = new VariableList();
		final String programUUID = studyDetails.getProgramUUID();

		if (studyDetails != null) {

			int rank = 1;
			rank =
					this.addVariableIfNecessary(variables, variableTypeList, TermId.STUDY_NAME, "STUDY_NAME", "Study name",
							studyDetails.getStudyName(), rank, programUUID, PhenotypicType.STUDY);
		}
		return variables.sort();
	}

	private int addVariableIfNecessary(final VariableList variables, final VariableTypeList variableTypeList,
			final TermId termId, final String localName, final String localDescription, final String value,
			final int rank, final String programUUID, final PhenotypicType role) throws MiddlewareException {

		Variable variable = null;

		boolean found = false;
		if (variableTypeList != null && variableTypeList.getVariableTypes() != null
				&& !variableTypeList.getVariableTypes().isEmpty()) {
			for (final DMSVariableType variableType : variableTypeList.getVariableTypes()) {
				if (variableType.getStandardVariable() != null) {
					final StandardVariable standardVariable = variableType.getStandardVariable();
					if (standardVariable.getId() == termId.getId()) {
						found = true;
						break;
					}
				}
			}

		}
		if (!found) {
			final StandardVariable standardVariable = this.getStandardVariableBuilder().create(termId.getId(),
					programUUID);
			standardVariable.setPhenotypicType(role);
			final DMSVariableType variableType = new DMSVariableType(localName, localDescription, standardVariable,
					rank);
			variable = new Variable(variableType, value);
			variableType.setRole(role);
			variables.add(variable);
			return rank + 1;
		}
		return rank;
	}
}
