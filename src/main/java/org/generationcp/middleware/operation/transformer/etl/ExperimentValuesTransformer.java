package org.generationcp.middleware.operation.transformer.etl;

import org.generationcp.middleware.domain.dms.*;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;

import java.util.List;

public class ExperimentValuesTransformer extends Transformer {
	public ExperimentValuesTransformer(HibernateSessionProvider sessionProviderForLocal,
            HibernateSessionProvider sessionProviderForCentral) {
				super(sessionProviderForLocal, sessionProviderForCentral);
	}
	

	public ExperimentValues transform(MeasurementRow mRow, VariableTypeList varTypeList,List<String> trialHeaders) throws MiddlewareQueryException {
		ExperimentValues experimentValues = new ExperimentValues();
		if(mRow == null) {
			return experimentValues;
		}
		List<MeasurementData> nonTrialMD = mRow.getNonTrialDataList(trialHeaders);
		if (nonTrialMD != null && varTypeList != null && varTypeList.getVariableTypes() != null) {
			if (nonTrialMD.size() == varTypeList.getVariableTypes().size()) {
				Integer locationId = Integer.parseInt(String.valueOf(mRow.getLocationId()));
				Integer germplasmId = Integer.parseInt(String.valueOf(mRow.getStockId()));
				VariableList variableList = new VariableList() ;
				
				List<VariableType> varTypes = varTypeList.getVariableTypes();
				
				for(int i = 0, l = varTypes.size(); i < l; i++ ){
					VariableType varType = varTypes.get(i);
					String value = null;
					for (MeasurementData data : nonTrialMD) {
					    if (data.getMeasurementVariable().getTermId() == varTypes.get(i).getId()) {
					        if (data.getcValueId() != null) {
		                        value = data.getcValueId();
		                    }
		                    else {
		                        value = data.getValue();
		                    }
					    }
					}
					
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
