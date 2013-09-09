package org.generationcp.middleware.operation.transformer.etl;

import java.util.List;

import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;

public class VariableTypeListTransformer extends Transformer {
	
    public VariableTypeListTransformer(HibernateSessionProvider sessionProviderForLocal,
            HibernateSessionProvider sessionProviderForCentral) {
				super(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	public VariableTypeList transform(List<MeasurementVariable> measurementVariables) throws MiddlewareQueryException {
		VariableTypeList variableTypeList = new VariableTypeList();
		int rank = 1;
		for (MeasurementVariable measurementVariable : measurementVariables) {
			StandardVariable standardVariable = getOntologyDataManager().findStandardVariableByTraitScaleMethodNames(
						measurementVariable.getProperty(), 
						measurementVariable.getScale(), 
						measurementVariable.getMethod());
			
			VariableType variableType = new VariableType(
						measurementVariable.getName(), 
						measurementVariable.getDescription(), 
						standardVariable, rank++);
			
			variableTypeList.add(variableType);
		}
		return variableTypeList;
	}
}
