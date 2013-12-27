package org.generationcp.middleware.operation.transformer.etl;

import java.util.List;

import org.generationcp.middleware.domain.dms.ExperimentValues;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;

public class ExperimentValuesTransformer extends Transformer {
	public ExperimentValuesTransformer(HibernateSessionProvider sessionProviderForLocal,
            HibernateSessionProvider sessionProviderForCentral) {
				super(sessionProviderForLocal, sessionProviderForCentral);
	}
	

	public ExperimentValues transform(MeasurementRow mRow, VariableTypeList varTypeList,List<String> trialHeaders) throws MiddlewareQueryException {
		ExperimentValues experimentValues = new ExperimentValues();
		
		if (mRow != null && mRow.getNonTrialDataList(trialHeaders) != null && varTypeList != null && varTypeList.getVariableTypes() != null) {
			if (mRow.getNonTrialDataList(trialHeaders).size() == varTypeList.getVariableTypes().size()) {
				Integer locationId = Integer.parseInt(String.valueOf(mRow.getLocationId()));
				Integer germplasmId = Integer.parseInt(String.valueOf(mRow.getStockId()));
				VariableList variableList = new VariableList() ;
				
				List<VariableType> varTypes = varTypeList.getVariableTypes();
				
				for(int i = 0, l = varTypes.size(); i < l; i++ ){
					VariableType varType = varTypes.get(i);
					String value = mRow.getNonTrialDataList(trialHeaders).get(i).getValue();
					
					Variable variable = new Variable(varType, value);
					variableList.add(variable);
				}
					
				experimentValues.setVariableList(variableList);
				experimentValues.setGermplasmId(germplasmId);
				experimentValues.setLocationId(locationId);
			}
			else{
				throw new MiddlewareQueryException("Variables did not match the Measurements Row.");
			}
		}
		
		return experimentValues;
	}
}
