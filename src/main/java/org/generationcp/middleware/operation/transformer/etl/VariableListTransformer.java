
package org.generationcp.middleware.operation.transformer.etl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

	public VariableListTransformer(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public VariableList transformStock(MeasurementRow mRow, VariableTypeList variableTypeList, List<String> trialHeaders)
			throws MiddlewareQueryException {
		VariableList variableList = new VariableList();

		if (mRow == null) {
			return variableList;
		}
		List<MeasurementData> nonTrialMD = mRow.getNonTrialDataList(trialHeaders);
		if (mRow != null && nonTrialMD != null && variableTypeList != null && variableTypeList.getVariableTypes() != null) {
			int nonTrialMDSize = nonTrialMD.size();
			int variableTypeSize = variableTypeList.getVariableTypes().size();
			if (nonTrialMDSize == variableTypeSize) {
				for (DMSVariableType variableType : variableTypeList.getVariableTypes()) {
					if (variableType.getStandardVariable().getPhenotypicType() == PhenotypicType.GERMPLASM) {
						String value = null;
						for (MeasurementData data : nonTrialMD) {
							if (data.getMeasurementVariable().getTermId() == variableType.getStandardVariable().getId()) {
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

	public VariableList transformStockOptimize(List<Integer> variableIndexesList, MeasurementRow mRow, VariableTypeList variableTypeList,
			List<String> trialHeaders) throws MiddlewareQueryException {
		VariableList variableList = new VariableList();

		if (mRow == null) {
			return variableList;
		}

		List<MeasurementData> nonTrialMD = mRow.getNonTrialDataList(trialHeaders);
		if (mRow != null && nonTrialMD != null && variableTypeList != null && variableTypeList.getVariableTypes() != null) {
			int nonTrialMDSize = nonTrialMD.size();
			int variableTypeSize = variableTypeList.getVariableTypes().size();
			if (nonTrialMDSize == variableTypeSize) {
				for (DMSVariableType variableType : variableTypeList.getVariableTypes()) {
					if (variableType.getStandardVariable().getPhenotypicType() == PhenotypicType.GERMPLASM) {
						String value = null;
						for (MeasurementData data : nonTrialMD) {
							if (data.getMeasurementVariable().getTermId() == variableType.getStandardVariable().getId()) {
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

	public List<Integer> transformStockIndexes(MeasurementRow mRow, VariableTypeList variableTypeList, List<String> trialHeaders)
			throws MiddlewareQueryException {
		List<Integer> variableIndexesList = new ArrayList<Integer>();

		if (mRow == null) {
			return variableIndexesList;
		}
		List<MeasurementData> nonTrialMD = mRow.getNonTrialDataList(trialHeaders);
		if (mRow != null && nonTrialMD != null && variableTypeList != null && variableTypeList.getVariableTypes() != null) {
			int nonTrialMDSize = nonTrialMD.size();
			int variableTypeSize = variableTypeList.getVariableTypes().size();

			if (nonTrialMDSize == variableTypeSize) {
				int i = 0;
				for (DMSVariableType variableType : variableTypeList.getVariableTypes()) {
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

	public VariableList transformTrialEnvironment(MeasurementRow mRow, VariableTypeList variableTypeList, List<String> trialHeaders)
			throws MiddlewareQueryException {
		VariableList variableList = new VariableList();

		if (mRow == null) {
			return variableList;
		}
		List<MeasurementData> trialMD = mRow.getTrialDataList(trialHeaders);
		if (trialMD != null && variableTypeList != null && variableTypeList.getVariableTypes() != null) {
			List<DMSVariableType> varTypes = variableTypeList.getVariableTypes();
			int varTypeSize = varTypes.size();
			for (int i = 0, l = varTypeSize; i < l; i++) {
				DMSVariableType varType = varTypes.get(i);

				if (varType.getStandardVariable().getPhenotypicType() == PhenotypicType.TRIAL_ENVIRONMENT
						|| varType.getStandardVariable().getPhenotypicType() == PhenotypicType.VARIATE) {// include variate
					String value = null;
					for (MeasurementData data : trialMD) {
						if (data.getMeasurementVariable().getTermId() == varTypes.get(i).getId()) {
							value = data.getValue();
						}
					}
					Variable variable = new Variable(varType, value);
					variableList.add(variable);
				}
			}
		}

		return variableList;
	}

	public VariableList transformTrialEnvironment(MeasurementRow mRow, VariableTypeList variableTypeList) throws MiddlewareQueryException {
		VariableList variableList = new VariableList();

		List<MeasurementData> trialMD = mRow.getDataList();
		if (trialMD != null && variableTypeList != null && variableTypeList.getVariableTypes() != null) {
			List<DMSVariableType> varTypes = variableTypeList.getVariableTypes();
			int varTypeSize = varTypes.size();
			for (int i = 0, l = varTypeSize; i < l; i++) {
				DMSVariableType varType = varTypes.get(i);
				MeasurementData trialData = null;
				for (MeasurementData aData : trialMD) {
					if (aData.getMeasurementVariable().getTermId() == varType.getId()) {
						trialData = aData;
						break;
					}
				}

				if (trialData != null) {
					String value = trialData.getValue();
					Integer phenotypeId = trialData.getPhenotypeId();

					if (trialData.getcValueId() != null) {
						value = trialData.getcValueId();
					}

					if (varType.getStandardVariable().getPhenotypicType() == PhenotypicType.TRIAL_ENVIRONMENT
							|| varType.getStandardVariable().getPhenotypicType() == PhenotypicType.VARIATE) {// include variate
						Variable variable = new Variable(varType, value);
						variable.setPhenotypeId(phenotypeId);
						variableList.add(variable);
					}
				}
			}
		}

		return variableList;
	}

	public VariableList transformTrialEnvironment(List<MeasurementVariable> measurementVariableList, VariableTypeList variableTypeList)
			throws MiddlewareQueryException {
		VariableList variableList = new VariableList();

		if(measurementVariableList == null && variableTypeList == null){
			return variableList;
		}

		if(measurementVariableList == null || variableTypeList == null || variableTypeList.getVariableTypes() == null) {
			throw new MiddlewareQueryException("Variables did not match the Measurement Variable List.");
		}

		List<DMSVariableType> varTypes = variableTypeList.getVariableTypes();

		if (!Objects.equals(measurementVariableList.size(), varTypes.size())) {
			throw new MiddlewareQueryException("Variables did not match the Measurement Variable List.");
		}

		for (MeasurementVariable measurementVariable : measurementVariableList) {
			String value = measurementVariable.getValue();
			for (DMSVariableType varType : varTypes) {
				if (measurementVariable.getTermId() == varType.getId()) {
					variableList.add(new Variable(varType, value));
				}
			}
		}

		return variableList;
	}

	public VariableList transformStudyDetails(StudyDetails studyDetails, 
			VariableTypeList variableTypeList) throws MiddlewareException {

		VariableList variables = new VariableList();
		String programUUID = studyDetails.getProgramUUID();

		if (studyDetails != null) {

			int rank = 1;
			rank =
					this.addVariableIfNecessary(variables, variableTypeList, TermId.STUDY_NAME, "STUDY_NAME", "Study name",
							studyDetails.getStudyName(), rank, programUUID, PhenotypicType.STUDY);
			rank =
					this.addVariableIfNecessary(variables, variableTypeList, TermId.STUDY_OBJECTIVE, "STUDY_OBJECTIVE", "Study objective",
							studyDetails.getObjective(), rank, programUUID, PhenotypicType.STUDY);
			rank =
					this.addVariableIfNecessary(variables, variableTypeList, TermId.START_DATE, "START_DATE", "Start date",
							studyDetails.getStartDate(), rank, programUUID, PhenotypicType.STUDY);
			rank =
					this.addVariableIfNecessary(variables, variableTypeList, TermId.END_DATE, "END_DATE", "End date",
							studyDetails.getEndDate(), rank, programUUID, PhenotypicType.STUDY);
		}
		return variables.sort();
	}

	private int addVariableIfNecessary(VariableList variables, VariableTypeList variableTypeList, TermId termId, String localName,
			String localDescription, String value, int rank, String programUUID, PhenotypicType role) throws MiddlewareException {

		Variable variable = null;

		boolean found = false;
		if (variableTypeList != null && variableTypeList.getVariableTypes() != null && !variableTypeList.getVariableTypes().isEmpty()) {
			for (DMSVariableType variableType : variableTypeList.getVariableTypes()) {
				if (variableType.getStandardVariable() != null) {
					StandardVariable standardVariable = variableType.getStandardVariable();
					if (standardVariable.getId() == termId.getId()) {
						found = true;
						break;
					}
				}
			}

		}
		if (!found) {
			StandardVariable standardVariable = this.getStandardVariableBuilder().create(
					termId.getId(),programUUID);
			standardVariable.setPhenotypicType(role);
			DMSVariableType variableType = new DMSVariableType(localName, localDescription, standardVariable, rank);
			variable = new Variable(variableType, value);
			variableType.setRole(role);
			variables.add(variable);
			return rank + 1;
		}
		return rank;
	}
}
