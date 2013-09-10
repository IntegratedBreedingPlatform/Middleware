package org.generationcp.middleware.operation.transformer.etl;

import java.util.List;

import org.generationcp.middleware.domain.dms.ExperimentValues;
import org.generationcp.middleware.domain.dms.FactorType;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;

public class VariableListTransformer {
	
	public VariableList transformStock(MeasurementRow mRow, VariableTypeList variableTypeList) throws MiddlewareQueryException {
		VariableList variableList = new VariableList();

		if (mRow != null && mRow.getDataList() != null && variableTypeList != null && variableTypeList.getVariableTypes() != null) {
			if (mRow.getDataList().size() == variableTypeList.getVariableTypes().size()) {
				int i = 0;
				for (VariableType variableType : variableTypeList.getVariableTypes()) {
					if (variableType.getStandardVariable().getFactorType() == FactorType.GERMPLASM) {
						variableList.add(new Variable(variableType, mRow.getDataList().get(i).getValue()));
					}
					i++;
				}
				
			} else {//else invalid data
				throw new MiddlewareQueryException("Variables did not match the Measurements Row.");
			}
		}
		
		return variableList;
	}
	
	public VariableList transformTrialEnvironment(MeasurementRow mRow, VariableTypeList variableTypeList) throws MiddlewareQueryException {
		VariableList variableList = new VariableList() ;
		
		if (mRow != null && mRow.getDataList() != null && variableTypeList != null && variableTypeList.getVariableTypes() != null) {
			if (mRow.getDataList().size() == variableTypeList.getVariableTypes().size()) {
				int i = 0;
				for (VariableType variableType : variableTypeList.getVariableTypes()) {
					if (variableType.getStandardVariable().getFactorType() == FactorType.TRIAL_ENVIRONMENT) {
						variableList.add(new Variable(variableType, mRow.getDataList().get(i).getValue()));
					}
					i++;
				}
				
			} else {//else invalid data
				throw new MiddlewareQueryException("Variables did not match the Measurements Row.");
			}
		}
		
		return variableList;
	}
	
}
