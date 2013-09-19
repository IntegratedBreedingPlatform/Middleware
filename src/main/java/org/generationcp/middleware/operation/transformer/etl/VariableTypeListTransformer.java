package org.generationcp.middleware.operation.transformer.etl;

import java.util.List;

import org.generationcp.middleware.domain.dms.FactorType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;

public class VariableTypeListTransformer extends Transformer {
	
    public VariableTypeListTransformer(HibernateSessionProvider sessionProviderForLocal,
            HibernateSessionProvider sessionProviderForCentral) {
				super(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	public VariableTypeList transform(List<MeasurementVariable> measurementVariables) 
			throws MiddlewareQueryException, MiddlewareException {
		
		return transform(measurementVariables, 1);
	}
	
	public VariableTypeList transform(List<MeasurementVariable> measurementVariables, int rank) 
			throws MiddlewareQueryException, MiddlewareException {
		
		VariableTypeList variableTypeList = new VariableTypeList();
		
		for (MeasurementVariable measurementVariable : measurementVariables) {
			
			StandardVariable standardVariable = getStandardVariableBuilder().findOrSave(
					measurementVariable.getName(), 
					measurementVariable.getDescription(), 
					measurementVariable.getProperty(),
					measurementVariable.getScale(),
					measurementVariable.getMethod(), 
					FactorType.getFactorTypeForLabel(measurementVariable.getLabel()),
					measurementVariable.getDataType());
			
			VariableType variableType = new VariableType(
						measurementVariable.getName(), 
						measurementVariable.getDescription(), 
						standardVariable, rank++);
			
			variableTypeList.add(variableType);
		}
		return variableTypeList;
	}
}
