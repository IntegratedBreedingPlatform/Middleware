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
	

	public ExperimentValues transform(MeasurementRow mRow, VariableTypeList varTypeList) throws MiddlewareQueryException {
		//input: all variableTypeList(columns from the worksheet), all values for all variableTypeList (all values on the cells of row)
		
		Integer locationId = Integer.parseInt(String.valueOf(mRow.getLocationId()));
		Integer germplasmId = Integer.parseInt(String.valueOf(mRow.getStockId()));
		VariableList variableList = new VariableList() ;
		
		List<VariableType> varTypes = varTypeList.getVariableTypes();
		
		for(int i = 0, l = varTypes.size(); i < l; i++ ){
			VariableType varType = varTypes.get(i);
			String value = mRow.getDataList().get(i).getValue();
			
			Variable variable = new Variable(varType, value);
			variableList.add(variable);
		}
		
		ExperimentValues experimentValues = new ExperimentValues();
		experimentValues.setVariableList(variableList);
		experimentValues.setGermplasmId(germplasmId);
		experimentValues.setLocationId(locationId);
		
		return experimentValues;
	}
}
